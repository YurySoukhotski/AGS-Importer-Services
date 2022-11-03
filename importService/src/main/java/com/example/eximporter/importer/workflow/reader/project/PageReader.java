package com.example.eximporter.importer.workflow.reader.project;

import com.example.eximporter.importer.model.xml.project.AdmediumPage;
import com.example.eximporter.importer.model.xml.project.AdmediumPageExport;
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
 * Batch reader from xml to {@link AdmediumPage}
 */
@Component("pageReader")
@JobScope
public class PageReader implements ItemReader<AdmediumPage>, ItemStream {
    private static final int START_INDEX = 0;
    private List<AdmediumPage> pages;
    private int currentIndex = START_INDEX;
    private static final Logger LOGGER = LoggerFactory.getLogger(PageReader.class);
    private static final String CURRENT_INDEX = "pages.current.index";
    private String fileName;

    /**
     * Read from XML file and convert to {@link AdmediumPage}
     *
     * @param fileName name of file to read
     */
    public PageReader(@Value("#{jobParameters['filePath']}") String fileName) {
        this.fileName = fileName;
    }

    @Override
    public AdmediumPage read() throws Exception {
        if (pages == null) {
            LOGGER.info("Read file : {}", fileName);
            File inputFile = new File(fileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(AdmediumPageExport.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            AdmediumPageExport pagesRoot = (AdmediumPageExport) jaxbUnmarshaller.unmarshal(inputFile);
            this.pages = pagesRoot.getAdmediumPage();
        }
        if (pages != null && currentIndex < pages.size()) {
            return pages.get(currentIndex++);
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
