package com.example.eximporter.importer.workflow.writer.project;

import com.example.eximporter.importer.configuration.JobCronConfiguration;
import com.example.eximporter.importer.model.api.Project;
import com.example.eximporter.importer.model.extended.ExtendedPage;
import com.example.eximporter.importer.service.http.ProjectApiService;
import com.example.eximporter.importer.service.http.RestException;
import com.example.eximporter.importer.service.js.JSParameter;
import com.example.eximporter.importer.service.js.notification.NotificationService;
import com.example.eximporter.importer.service.parking.ParkedPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.*;

import static com.example.eximporter.importer.controller.StepListener.FILE_PATH_JOB_PARAMETER;
import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.buildSimpleAttributeValues;
import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.getAttributeValue;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.*;
import static com.example.eximporter.importer.service.parking.ParkedPeoProcessor.*;

/**
 * Batch write operations with {@link ExtendedPage}
 */
@Configuration
public class PageWriter extends BaseProjectWriter implements ItemWriter<ExtendedPage> {
    private Logger logger = LoggerFactory.getLogger(PageWriter.class);
    private Map<String, Long> languageProjectsStorage = new HashMap<>();
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ParkedPageService parkedPageService;
    @Autowired
    private JobCronConfiguration jobCronConfiguration;
    private Boolean isSendMessage;
    private StringBuilder pagesInfo=new StringBuilder();
    private String fileName;

    @BeforeStep
    public void initMessage(StepExecution stepExecution) {
        isSendMessage=false;
        pagesInfo = new StringBuilder("Language project not found for pages: Skip this page" + MSG_NEXTLINE);
        String filePath = stepExecution.getJobParameters().getString(FILE_PATH_JOB_PARAMETER);
        this.fileName=filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER)+1);
        pagesInfo.append("File name:").append(fileName).append(MSG_NEXTLINE);
    }

    @AfterStep
    public void sendMessage() {
        if (isSendMessage) {
            Map<JSParameter, String> parameters = new EnumMap<>(JSParameter.class);
            parameters.put(JSParameter.MSG_TEMPLATE, WARN_PAGE);
            parameters.put(JSParameter.MESSAGE, pagesInfo.toString());
            parameters.put(JSParameter.FILE_NAME, fileName);
            notificationService.call(parameters);
        }
    }

    /**
     * Batch write operation with {@link ExtendedPage}
     *
     * @param extendedPages received {@link ExtendedPage} projects
     * @throws Exception when process
     */

    @Override
    public void write(List<? extends ExtendedPage> extendedPages) throws RestException {
        for (ExtendedPage extendedPage : extendedPages) {
            if (extendedPage.getLanguageProjectKey() != null) {
                logger.info("Processing Page with Language  key: {} ", extendedPage.getLanguageProjectKey());
                Project newPageProject = extendedPage.getPage();
                newPageProject.setType(ATTR_AGS_6_SEITE);
                processProject(newPageProject, extendedPage, null);
            } else {
                throw new RestException("Empty language key in Page. Break import. Page Id:"
                        + getAttributeValue(extendedPage.getPage().getAttributes(), ATTR_SEITE_FP_ID));
            }
        }
    }

    /**
     * Create or update Page Project
     *
     * @param newPageProject page project
     * @param extendedPage   Extended Page project
     * @throws RestException when process
     */
    public void processProject(Project newPageProject, ExtendedPage extendedPage, StringBuilder msg) throws RestException {
        String languageFpId = extendedPage.getLanguageProjectKey();
        Long existingPageProjectId = searchProject(newPageProject, ATTR_AGS_6_SEITE, ATTR_SEITE_FP_ID, null);
        if (existingPageProjectId != null) {
            newPageProject.getAttributes().put(SEITE_SYNCHRONIZED_BY_FACHPORTAL,
                    buildSimpleAttributeValues(Boolean.TRUE.toString()));
            newPageProject.getAttributes().put(SEITE_SYNCHRONIZED_BY_FACHPORTAL_DATE,
                    buildSimpleAttributeValues(formatter.format(new Date())));
            updateProject(newPageProject, existingPageProjectId);
            logger.info("Updated existing Page Project with id: {}", existingPageProjectId);
            if (extendedPage.getAttempt() > 0) {
                extendedPage.setAttempt(jobCronConfiguration.getMaxCountAttempts() + 1);
                parkedPageService.saveExtendedPage(extendedPage);
            }
        } else
        {
            Long existingLanguageProjectId = languageProjectsStorage.get(languageFpId);
            if (existingLanguageProjectId == null)
            {
                existingLanguageProjectId = searchProject(null, ATTR_AGS_5_SPRACHVERSION, ATTR_LANGUAGE_FP_ID, languageFpId);
            }
            if (existingLanguageProjectId != null)
            {
                Project existingLanguageProject = getProjectById(existingLanguageProjectId);
                languageProjectsStorage.put(languageFpId, existingLanguageProjectId);
                String countryValue = getAttributeValue(existingLanguageProject.getAttributes(), ATTR_COUNTRY_VALUE);
                StringBuilder identifierBuilder = new StringBuilder();
                identifierBuilder.append(existingLanguageProject.getPath()).append(PREF).append(existingLanguageProject.getParentId()).append(PREF).append(countryValue).append(PREF)
                    .append(existingLanguageProject.getId()).append(PREF)
                    .append(getAttributeValue(newPageProject.getAttributes(), ATTR_SEITE_FP_ID));
                newPageProject.setDisplayName(identifierBuilder.toString());
                newPageProject.setParentId(existingLanguageProject.getId());
                newPageProject.getAttributes().put(SEITE_IMPORTED_BY_FACHPORTAL, buildSimpleAttributeValues(Boolean.TRUE.toString()));
                newPageProject.getAttributes().put(SEITE_IMPORTED_BY_FACHPORTAL_DATE, buildSimpleAttributeValues(formatter.format(new Date())));
                Long newProjectId = createProject(newPageProject);
                logger.info("Create new Page Project with id: {} with name: {} ", newProjectId, newPageProject.getDisplayName());
                if (extendedPage.getAttempt() > 0)
                {
                    extendedPage.setAttempt(jobCronConfiguration.getMaxCountAttempts() + 1);
                    parkedPageService.saveExtendedPage(extendedPage);
                    logger.info("Parked Page processed: Marked for delete");
                }
            }
            else
            {
                logger.error("Language project not found for value: {}. Page project id: {}. Skip this page", languageFpId,
                    getAttributeValue(newPageProject.getAttributes(), ATTR_SEITE_FP_ID));
                isSendMessage = true;
                if (extendedPage.getFileName()==null){
                    extendedPage.setFileName(fileName);
                }
                buildInfoMessage(newPageProject,extendedPage, languageFpId,msg);
                parkedPageService.saveExtendedPage(extendedPage);
            }
        }
    }

    private void buildInfoMessage(Project newPageProject, ExtendedPage extendedPage, String languageFpId, StringBuilder message){
        if (message!=null){
            message.append("File name").append(MSG_PREF).append(extendedPage.getFileName()).append(MSG_NEXTLINE);
            message.append(PAGE).append(MSG_PREF).append(getAttributeValue(newPageProject.getAttributes(), ATTR_SEITE_FP_ID)).append(MSG_PREFLONG);
            message.append("Language").append(MSG_PREF).append(languageFpId).append(MSG_PREFLONG);
            message.append("Count attempts").append(MSG_PREF).append(Long.valueOf(extendedPage.getAttempt()+1)).append(MSG_NEXTLINE);
        }
        else {
            pagesInfo.append(PAGE).append(MSG_PREF)
                .append(getAttributeValue(newPageProject.getAttributes(), ATTR_SEITE_FP_ID)).append(MSG_NEXTLINE);
            pagesInfo.append("Language").append(MSG_PREF).append(languageFpId).append(MSG_PREFLONG);
            pagesInfo.append("Count attempts").append(MSG_PREF).append(Long.valueOf(extendedPage.getAttempt()+1)).append(MSG_NEXTLINE);
        }

    }
}
