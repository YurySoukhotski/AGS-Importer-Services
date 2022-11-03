package com.example.eximporter.importer.service.parking;

import com.example.eximporter.importer.configuration.JobCronConfiguration;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.model.api.Project;
import com.example.eximporter.importer.model.xml.project.AdmediumPage;
import com.example.eximporter.importer.service.converter.project.PageConverter;
import com.example.eximporter.importer.service.http.RestException;
import com.example.eximporter.importer.workflow.writer.project.PageWriter;
import com.example.eximporter.importer.model.extended.ExtendedPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Process parked pages
 */
@Service
public class ParkedPageProcessor {
	@Autowired
	private ParkedPageService parkedPageService;
	@Autowired
	private PageConverter pageConverter;
	@Autowired
	private PageWriter pageWriter;
	@Autowired
	private JobCronConfiguration jobCronConfiguration;
	private Logger logger = LoggerFactory.getLogger(ParkedPageProcessor.class);

	/**
	 * Get pages and process it
     *
     * @param adMediumPages parked pages from db
	 */
    void processParkedPages(List<AdmediumPage> adMediumPages, StepExecution stepExecution)
	{
		Boolean isSendDeleted=false;
		Boolean isSendProblem=false;
		StringBuilder problemInfo = new StringBuilder("Language project not found for pages:" + ParkedPeoProcessor.MSG_NEXTLINE);
		StringBuilder deletedInfo = new StringBuilder("Remove parked page after max count attempts:" + ParkedPeoProcessor.MSG_NEXTLINE);
		List<ExtendedPage> extendedPages = new ArrayList<>();
		for (AdmediumPage adMediumPage : adMediumPages)
		{
			if (adMediumPage.getAttempt() <= jobCronConfiguration.getMaxCountAttempts())
			{
				extendedPages.add(pageConverter.convert(adMediumPage));
			}
			else
			{
				logger.info("Remove parked page: {} with language: {} ", adMediumPage.getPageTechId(),
					adMediumPage.getAdmediumVersionId());
				parkedPageService.deletePage(adMediumPage);
				isSendDeleted = true;
				deletedInfo.append("File").append(ParkedPeoProcessor.MSG_PREF).append(adMediumPage.getFileName()).append(ParkedPeoProcessor.MSG_NEXTLINE);
				deletedInfo.append(MappingAttributeHelper.PAGE).append(ParkedPeoProcessor.MSG_PREF).append(adMediumPage.getPageTechId()).append(ParkedPeoProcessor.MSG_PREFLONG);
				deletedInfo.append("Language:").append(ParkedPeoProcessor.MSG_PREF).append(adMediumPage.getPageTechId()).append(ParkedPeoProcessor.MSG_PREFLONG);
				deletedInfo.append("Count attempts").append(ParkedPeoProcessor.MSG_PREF).append(adMediumPage.getAttempt()).append(ParkedPeoProcessor.MSG_NEXTLINE);
			}
		}
		try
		{
			for (ExtendedPage extendedPage : extendedPages)
			{
				if (extendedPage.getLanguageProjectKey() != null)
				{
					logger.info("Parked Page. Processing Page with Language  key: {} ", extendedPage.getLanguageProjectKey());
					Project newPageProject = extendedPage.getPage();
					newPageProject.setType(MappingAttributeHelper.ATTR_AGS_6_SEITE);
					pageWriter.processProject(newPageProject, extendedPage, problemInfo);
				}
			}
		}
		catch (RestException e)
		{
			logger.error("Error while processing parked pages", e);
		}
		String msg = "";
		if (isSendDeleted)
		{
			msg = msg + deletedInfo.toString();
		}
		if (!problemInfo.toString().equalsIgnoreCase("Language project not found for pages:" + ParkedPeoProcessor.MSG_NEXTLINE)){
			msg=msg+problemInfo.toString();
			isSendProblem=true;
		}
		if (isSendDeleted || isSendProblem){
		stepExecution.getJobExecution().getExecutionContext().put(MappingAttributeHelper.PARKED_PAGE, new JobParameter(msg));
	}}

}
