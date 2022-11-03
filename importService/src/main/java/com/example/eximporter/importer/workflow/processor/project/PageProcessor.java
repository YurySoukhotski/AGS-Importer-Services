package com.example.eximporter.importer.workflow.processor.project;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import com.example.eximporter.importer.model.extended.ExtendedPage;
import com.example.eximporter.importer.model.xml.project.AdmediumPage;
import com.example.eximporter.importer.service.converter.project.PageConverter;

/**
 * Process {@link AdmediumPage} to {@link ExtendedPage}
 */
@Configuration
public class PageProcessor implements ItemProcessor<AdmediumPage, ExtendedPage>
{
	@Autowired
	private PageConverter pageConverter;

	/**
	 * Process {@link AdmediumPage} to {@link ExtendedPage}
	 * @param page received xml model of Page
	 * @return ExtendedPage
	 * @throws Exception
	 */
	@Override
	public ExtendedPage process(AdmediumPage page) throws Exception
	{
		return pageConverter.convert(page);
	}
}
