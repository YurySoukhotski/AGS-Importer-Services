package com.example.eximporter.importer.workflow.reader.product;

import com.example.eximporter.importer.model.xml.product.OmnArticleExport;
import com.example.eximporter.importer.model.xml.product.Style;
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
 * Batch reader from xml to Style
 */
@Component("productReader")
@JobScope
public class ProductReader implements ItemReader<Style>, ItemStream
{
	private static final int START_INDEX = 0;
	private List<Style> styles;
	private int currentIndex = START_INDEX;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductReader.class);
	private static final String CURRENT_INDEX = "product.current.index";
	private String fileName;

	/**
	 * Read from XML file and convert to {@link Style}
	 * @param fileName
	 *            name of file to read
	 */
	public ProductReader(@Value("#{jobParameters['filePath']}") String fileName)
	{
		this.fileName=fileName;

	}

	@Override
	public Style read() throws Exception
	{
		if (styles==null){
			LOGGER.info("Read file : {}", fileName);
			File inputFile = new File(fileName);
			JAXBContext jaxbContext = JAXBContext.newInstance(OmnArticleExport.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			OmnArticleExport omnArticleExport = (OmnArticleExport) jaxbUnmarshaller.unmarshal(inputFile);
			this.styles = omnArticleExport.getStyle();
		}
		if (styles!=null && currentIndex < styles.size()) {
				return styles.get(currentIndex++);
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
