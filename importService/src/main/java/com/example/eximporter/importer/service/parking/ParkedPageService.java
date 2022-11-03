package com.example.eximporter.importer.service.parking;

import com.example.eximporter.importer.dao.ParkedPageRepository;
import com.example.eximporter.importer.model.xml.project.AdmediumPage;
import com.example.eximporter.importer.service.converter.project.ParkedPageConverter;
import com.example.eximporter.importer.model.extended.ExtendedPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to work with Parked Page
 */
@Service
public class ParkedPageService
{
	@Autowired
	private ParkedPageRepository parkedPageRepository;
	@Autowired
	private ParkedPageConverter parkedPageConverter;
	@Autowired
	private ParkedPageProcessor parkedPageProcessor;
	private Logger logger = LoggerFactory.getLogger(ParkedPageProcessor.class);

	/**
	 * Save or Update parked page
	 * @param extendedPage
	 *            page for converting
	 */
	public void saveExtendedPage(ExtendedPage extendedPage)
	{
		parkedPageRepository.save(parkedPageConverter.convertPage(extendedPage));
	}

	/**
	 * Check and Process all parked pages
	 */
	public void checkParkedPages(StepExecution stepExecution)
	{
		List<AdmediumPage> parkedPages = (List<AdmediumPage>) getPages();
		if (!parkedPages.isEmpty())
		{
			logger.info("Parked pages count: {}", parkedPages.size());
			parkedPageProcessor.processParkedPages(parkedPages, stepExecution);
		}
		else
		{
			logger.info("Parked pages not found ");
		}
	}

	/**
	 * Delete parked page
	 * @param admediumPage
	 */
	void deletePage(AdmediumPage admediumPage)
	{
		parkedPageRepository.delete(admediumPage);
	}

	/**
	 * Return all parked pages
	 * @return
	 */
	Iterable<AdmediumPage> getPages()
	{
		return parkedPageRepository.findAll();
	}
}
