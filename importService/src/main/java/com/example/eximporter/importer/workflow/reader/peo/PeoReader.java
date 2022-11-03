package com.example.eximporter.importer.workflow.reader.peo;

import com.example.eximporter.importer.model.xml.peo.OmnPlacementExport;
import com.example.eximporter.importer.model.xml.peo.Placement;
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
 * Represents part of batch job to reading placements data
 */
@Component("peoReader")
@JobScope
public class PeoReader implements ItemReader<Placement>, ItemStream
{
    private static final int START_INDEX = 0;
    private List<Placement> placements;
    private int currentIndex = START_INDEX;
    private static final Logger LOGGER = LoggerFactory.getLogger(PeoReader.class);
    private static final String CURRENT_INDEX = "peos.current.index";
    private String fileName;

    /**
     * Constructor.
     * Reads xml file with placements data and converts to objects
     *
     * @param fileName
     */
    public PeoReader(@Value("#{jobParameters['filePath']}") String fileName)
    {
        this.fileName=fileName;

    }

    @Override
    public Placement read() throws Exception
    {
        if (placements==null){
            LOGGER.info("Read file : {}", fileName);
            File inputFile = new File(fileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(OmnPlacementExport.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            OmnPlacementExport projectRoot = (OmnPlacementExport) jaxbUnmarshaller.unmarshal(inputFile);
            this.placements = projectRoot.getPlacement();
        }
        if (placements!=null && currentIndex < placements.size()) {
                return placements.get(currentIndex++);
            }
        return null;
    }

    @Override
    public void open(ExecutionContext executionContext)
    {
        if (executionContext.containsKey(CURRENT_INDEX))
        {
            currentIndex = (int) executionContext.getLong(CURRENT_INDEX);
        }
        else
        {
            currentIndex = START_INDEX;
        }
    }

    @Override
    public void update(ExecutionContext executionContext)
    {
        executionContext.putLong(CURRENT_INDEX, currentIndex);
    }

    @Override
    public void close()
    {
        // not need specific logic
    }
}
