package com.example.eximporter.importer.workflow.writer.project;

import com.example.eximporter.importer.configuration.CommonConfiguration;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.Project;
import com.example.eximporter.importer.model.extended.ExtendedProject;
import com.example.eximporter.importer.service.http.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.buildSimpleAttributeValues;
import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.getAttributeValue;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.*;
import static com.example.eximporter.importer.helper.MessageBuilderHelper.COUNTER_CATALOG;
import static com.example.eximporter.importer.helper.MessageBuilderHelper.COUNTER_LANGUAGES;

/**
 * Batch operation to write Season, Catalog, Language projects
 */
@Configuration
public class ProjectWriter extends BaseProjectWriter implements ItemWriter<ExtendedProject> {
    private Logger logger = LoggerFactory.getLogger(ProjectWriter.class);
    private Map<String, Long> seasonsStorage = new HashMap<>();
    private Map<String, Long> catalogsStorage = new HashMap<>();
    @Autowired
    private CommonConfiguration commonConfiguration;
    private Long catalogCounter = 0L;
    private Long languageCounter = 0L;

    @BeforeStep
    private void resetStatistic(){
        this.catalogCounter=0L;
        this.languageCounter=0L;
    }

    @AfterStep
    public void saveStatistic(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put(COUNTER_CATALOG, new JobParameter(catalogCounter));
        stepExecution.getJobExecution().getExecutionContext().put(COUNTER_LANGUAGES, new JobParameter(languageCounter));
    }

    /**
     * Batch Write {@link ExtendedProject}
     *
     * @param extendedProjects converted projects
     * @throws RestException
     */
    @Override
    public void write(List<? extends ExtendedProject> extendedProjects) throws RestException {
        for (ExtendedProject extendedProject : extendedProjects) {
            logger.info("Processing Project with Brand {}  : Season {}", extendedProject.getBrand(), extendedProject.getSeason());
            processExtendedProject(extendedProject);
        }
    }

    /**
     * Create or update {@link ExtendedProject}
     *
     * @param extendedProject project with season , catalog, language projects
     * @throws RestException
     */
    private void processExtendedProject(ExtendedProject extendedProject) throws RestException {
        Long existingSeasonId = getSeasonByIdentifier(extendedProject.getSeason());
        Project newCatalog = extendedProject.getProject();
        String catalogFpIdValue = getAttributeValue(newCatalog.getAttributes(), ATTR_WERBEMITTEL_FP_ID);
        Long existingCatalogId = catalogsStorage.get(catalogFpIdValue);
        if (existingCatalogId == null) {
            existingCatalogId = searchProject(newCatalog, ATTR_AGS_4_WERBEMITTELVARIANTE, ATTR_WERBEMITTEL_FP_ID, null);
        }
        StringBuilder identifierBuilder;
        if (existingCatalogId != null) {
            newCatalog.getAttributes().put(WERBEMITTEL_SYNCHRONIZED_BY_FACHPORTAL_DATE,
                    buildSimpleAttributeValues(formatter.format(new Date())));
            newCatalog.getAttributes().put(WERBEMITTEL_SYNCHRONIZED_BY_FACHPORTAL,
                    buildSimpleAttributeValues(Boolean.TRUE.toString()));
            newCatalog.getAttributes().put(ATTR_WERBEMITTELSCHLUESSEL, buildSimpleAttributeValues(getAttributeValue(newCatalog.getAttributes(), ATTR_WERBEMITTEL_FP_ID)));
            updateProject(newCatalog, existingCatalogId);
            logger.info("Catalog updated. Identifier: {}", newCatalog.getId());
            catalogsStorage.put(catalogFpIdValue, existingCatalogId);
            identifierBuilder = new StringBuilder();
            identifierBuilder.append(newCatalog.getType()).append(PREF).append(existingSeasonId).append(PREF)
                    .append(getAttributeValue(newCatalog.getAttributes(), ATTR_WERBEMITTEL_FP_ID));
            identifierBuilder.append(PREF).append(existingCatalogId).append(PREF);
            processLanguageProjects(existingCatalogId, newCatalog, extendedProject.getLanguageProjects(),
                    identifierBuilder.toString(), false);
        } else {
            identifierBuilder = new StringBuilder();
            identifierBuilder.append(newCatalog.getType()).append(PREF).append(existingSeasonId).append(PREF)
                    .append(getAttributeValue(newCatalog.getAttributes(), ATTR_WERBEMITTEL_FP_ID));
            newCatalog.setParentId(existingSeasonId);
            newCatalog.setDisplayName(identifierBuilder.toString());
            newCatalog.getAttributes().put(WERBEMITTEL_IMPORTED_BY_FACHPORTAL_DATE,
                    buildSimpleAttributeValues(formatter.format(new Date())));
            newCatalog.getAttributes().put(WERBEMITTEL_IMPORTED_BY_FACHPORTAL,
                    buildSimpleAttributeValues(Boolean.TRUE.toString()));
            newCatalog.getAttributes().put(ATTR_WERBEMITTELSCHLUESSEL, buildSimpleAttributeValues(getAttributeValue(newCatalog.getAttributes(), ATTR_WERBEMITTEL_FP_ID)));
            logger.info("Create Catalog with identifier: {}", newCatalog.getDisplayName());
            existingCatalogId = createProject(newCatalog);
            catalogsStorage.put(catalogFpIdValue, existingCatalogId);
            identifierBuilder.append(PREF).append(existingCatalogId).append(PREF);
            processLanguageProjects(existingCatalogId, newCatalog, extendedProject.getLanguageProjects(),
                    identifierBuilder.toString(), true);
        }
        catalogCounter++;
    }

    /**
     * Search existing season or create new
     *
     * @param season to search or create
     * @return id of season
     * @throws RestException
     */
    private Long getSeasonByIdentifier(String season) throws RestException {
        Long existingSeasonId = seasonsStorage.get(season);
        if (existingSeasonId == null) {
            existingSeasonId = searchProject(null, ATTR_AGS_3_SAISON, ATTR_SAISON_FP_ID, season);
            if (existingSeasonId == null) {
                Long productionId = commonConfiguration.getProductionId();
                Project existingSeason = buildSeasonProject(season, productionId == 0 ? null : productionId);
                existingSeasonId = createProject(existingSeason);
                seasonsStorage.put(season, existingSeasonId);
                logger.info("New Season id: {}", existingSeasonId);
            } else {
                Project existingSeason = getProjectById(existingSeasonId);
                existingSeason.getAttributes().put(SAISON_SYNCHRONIZED_BY_FACHPORTAL_DATE,
                        buildSimpleAttributeValues(formatter.format(new Date())));
                existingSeason.getAttributes().put(SAISON_SYNCHRONIZED_BY_FACHPORTAL,
                        buildSimpleAttributeValues(Boolean.TRUE.toString()));
                existingSeason.getAttributes().put(ATTR_SAISON, buildSimpleAttributeValues(season));
                updateProject(existingSeason, existingSeasonId);
                logger.info("Season updated id: {}", existingSeasonId);
                seasonsStorage.put(season, existingSeasonId);
            }
        }
        return existingSeasonId;
    }

    /**
     * Create project of type Season
     *
     * @param valueOfFPid FPID value for season
     * @param parentId    parent id for linking season project
     * @return season project
     */
    private Project buildSeasonProject(String valueOfFPid, Long parentId) {
        StringBuilder identifierBuilder = new StringBuilder();
        Project newProject = new Project();
        newProject.setType(ATTR_AGS_3_SAISON);
        newProject.setParentId(parentId);
        identifierBuilder.append(ATTR_AGS_3_SAISON).append(PREF).append(parentId).append(PREF).append(valueOfFPid);
        newProject.setDisplayName(identifierBuilder.toString());
        AttributesValues attributesValues = new AttributesValues();
        attributesValues.put(ATTR_SAISON_FP_ID, buildSimpleAttributeValues(valueOfFPid));
        attributesValues.put(ATTR_SAISON, buildSimpleAttributeValues(valueOfFPid));
        attributesValues.put(SAISON_IMPORTED_BY_FACHPORTAL, buildSimpleAttributeValues(Boolean.TRUE.toString()));
        attributesValues.put(IMPORTED_BY_FACHPORTAL, buildSimpleAttributeValues(Boolean.TRUE.toString()));
        attributesValues.put(SAISON_IMPORTED_BY_FACHPORTAL_DATE, buildSimpleAttributeValues(formatter.format(new Date())));
        newProject.setAttributes(attributesValues);
        return newProject;
    }

    /**
     * Create or update language projects
     *
     * @param parentId                  parent id to linking language projects
     * @param projectCatalog            parent catalog project
     * @param languageProjects          language projects to create or update
     * @param projectLanguageIdentifier identifier to create identifier of new language projects
     * @param isNewCatalog              is created languages from new Catalog
     * @throws RestException
     */
    private void processLanguageProjects(Long parentId, Project projectCatalog, List<Project> languageProjects,
                                         String projectLanguageIdentifier, Boolean isNewCatalog) throws RestException {
        for (Project newLanguageProject : languageProjects) {
            String languagePrefix = getAttributeValue(newLanguageProject.getAttributes(), ATTR_COUNTRY_VALUE);

            languagePrefix= MappingAttributeHelper.getPrefixCountryValues().get(languagePrefix);

            newLanguageProject.getAttributes().put(ATTR_LANGUAGE_FP_ID,
                    buildSimpleAttributeValues(getAttributeValue(projectCatalog.getAttributes(),
                            ATTR_PREF_SPRACHVERSION + languagePrefix + ATTR_PREF_LAGO_KEY)));
            if (!isNewCatalog) {
                Long existingLanguageProjectId = searchProject(newLanguageProject, ATTR_AGS_5_SPRACHVERSION, ATTR_LANGUAGE_FP_ID, null);
                if (existingLanguageProjectId != null) {
                    newLanguageProject.getAttributes().put(LANGUAGE_SYNCHRONIZED_BY_FACHPORTAL,
                            buildSimpleAttributeValues(Boolean.TRUE.toString()));
                    newLanguageProject.getAttributes().put(LANGUAGE_SYNCHRONIZED_BY_FACHPORTAL_DATE,
                            buildSimpleAttributeValues(formatter.format(new Date())));
                    updateProject(newLanguageProject, existingLanguageProjectId);
                    logger.info("Update language projects for language {}, project id {}", languagePrefix,
                            existingLanguageProjectId);
                } else {
                    createNewLanguageProject(newLanguageProject, projectLanguageIdentifier, languagePrefix, parentId);
                }
            } else {
                createNewLanguageProject(newLanguageProject, projectLanguageIdentifier, languagePrefix, parentId);
            }
            languageCounter++;
        }
    }

    /**
     * Create new language Project
     * @param newLanguageProject new language project
     * @param projectLanguageIdentifier language identifier
     * @param languagePrefix iso language prefix
     * @param parentId id parent catalog
     * @throws RestException
     */
    private void createNewLanguageProject(Project newLanguageProject, String projectLanguageIdentifier, String languagePrefix, Long parentId) throws RestException {
        newLanguageProject.setType(ATTR_AGS_5_SPRACHVERSION);
        newLanguageProject.setDisplayName(projectLanguageIdentifier + languagePrefix);
        newLanguageProject.setParentId(parentId);
        newLanguageProject.getAttributes().put(LANGUAGE_IMPORTED_BY_FACHPORTAL,
                buildSimpleAttributeValues(Boolean.TRUE.toString()));
        newLanguageProject.getAttributes().put(LANGUAGE_IMPORTED_BY_FACHPORTAL_DATE,
                buildSimpleAttributeValues(formatter.format(new Date())));
        logger.info("Create language projects with identifier :{}", newLanguageProject.getDisplayName());
        createProject(newLanguageProject);
    }
}
