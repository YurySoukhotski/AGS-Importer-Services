package com.example.eximporter.importer.workflow.reader.project;

import com.example.eximporter.importer.model.xml.project.Project;
import com.example.eximporter.importer.model.xml.project.Projects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

/**
 * Batch reader from xml to Project
 */
@Component("projectReader")
@JobScope
public class ProjectReader implements ItemReader<Project>, ItemStream {
    private static final int START_INDEX = 0;
    private List<Project> projects;
    private int currentIndex = START_INDEX;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectReader.class);
    private static final String CURRENT_INDEX = "projects.current.index";
    private String fileName;

    /**
     * Read from XML file and convert to {@link Project}
     *
     * @param fileName name of file to read
     */
    public ProjectReader(@Value("#{jobParameters['filePath']}") String fileName) {
        this.fileName = fileName;

    }

    @Override
    public Project read() throws Exception {
        if (projects == null) {
            LOGGER.info("Read file : {}", fileName);
            File inputFile = new File(fileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(Projects.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Projects projectRoot = (Projects) jaxbUnmarshaller.unmarshal(inputFile);
            this.projects = projectRoot.getProject();
        }
        if (projects!=null && currentIndex < projects.size()) {
                return projects.get(currentIndex++);
            }

        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (executionContext.containsKey(CURRENT_INDEX)) {
            currentIndex = (int) executionContext.getLong(CURRENT_INDEX);
        } else {
            currentIndex = START_INDEX;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        executionContext.putLong(CURRENT_INDEX, currentIndex);
    }

    @Override
    public void close() {
        // not need specific logic
    }
}
