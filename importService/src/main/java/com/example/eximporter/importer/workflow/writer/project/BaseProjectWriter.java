package com.example.eximporter.importer.workflow.writer.project;

import com.example.eximporter.importer.model.api.Project;
import com.example.eximporter.importer.service.http.ProjectApiService;
import com.example.eximporter.importer.service.http.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.getAttributeValue;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.ERROR_API_MSG;

/**
 * Base batch write operations with Project
 */
public class BaseProjectWriter {
    private Logger logger = LoggerFactory.getLogger(BaseProjectWriter.class);
    static final String PREF = "_";
    @Autowired
    protected ProjectApiService apiService;
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

    /**
     * Find project
     *
     * @param newProject    project to get attribute value
     * @param attributeName name of attribute to search
     * @param type          of received project
     * @return found project
     * @throws RestException
     */
    Long searchProject(Project newProject, String type, String attributeName, String attributeValue)
            throws RestException {

        String fpIDValue = attributeValue;
        if (newProject != null) {
            fpIDValue = getAttributeValue(newProject.getAttributes(), attributeName);
        }
        try {
            if (fpIDValue != null) {
                Long[] projects = apiService.searchProjectsByFpID(type, attributeName, fpIDValue);
                if (projects.length > 1) {
                    throw new RestException("More than one project was found");
                } else if (projects.length > 0) {
                    return projects[0];
                } else
                    return null;
            } else
                throw new RestException("FPID value is null, break operation");
        } catch (RestException e) {
            logger.debug("Error when search Project. Type: {},  Attribute: {}, Value: {} ", type, attributeName, fpIDValue);
            throw e;
        }
    }

    /**
     * Create new Project
     *
     * @param newProject new project to create
     * @return id of created project
     * @throws RestException
     */
    Long createProject(Project newProject) throws RestException {
        Long newProjectID;
        try {
            newProjectID = apiService.createProject(newProject);
            if (newProjectID != null) {
                logger.info("Project created. Id: {}", newProjectID);
            }
        } catch (RestException e) {
            logger.error(ERROR_API_MSG, e.getMessage(), e.getHttpStatus());
            throw e;
        }
        return newProjectID;
    }

    /**
     * Update operation with project
     *
     * @param newProject new project
     * @param existingProjectId existing project id
     * @throws RestException
     */
    void updateProject(Project newProject, Long existingProjectId) throws RestException {
        newProject.setId(existingProjectId);
        try {
            apiService.updateProject(newProject);
        } catch (RestException e) {
            logger.error(ERROR_API_MSG, e.getMessage(), e.getHttpStatus());
            throw e;
        }
    }

    /**
     *
     * @param id
     * @return
     * @throws RestException
     */
    Project getProjectById(Long id) throws RestException {
        return apiService.getProjectById(id);
    }


}
