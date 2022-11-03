package com.example.eximporter.importer.service.converter.project;

import com.example.eximporter.importer.helper.JsonModelBuilderHelper;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import org.springframework.stereotype.Service;
import com.example.eximporter.importer.model.extended.ExtendedPage;
import com.example.eximporter.importer.model.xml.project.AdmediumPage;

/**
 * Convert {@link ExtendedPage} to {@link AdmediumPage}
 */
@Service
public class ParkedPageConverter
{
	/**
	 * Convert {@link ExtendedPage} to {@link AdmediumPage}
	 * @param extendedPage page for parking
	 * @return AdmediumPage with changed attempt value
	 */
	public AdmediumPage convertPage(ExtendedPage extendedPage)
	{
		AdmediumPage adMediumPage = new AdmediumPage();
		adMediumPage.setAdmediumVersionId(extendedPage.getLanguageProjectKey());
		adMediumPage.setPageKey(JsonModelBuilderHelper.getAttributeValue(extendedPage.getPage().getAttributes(), MappingAttributeHelper.ATTR_SEITE_FP_PAGEKEY));
		adMediumPage.setPageTechId(JsonModelBuilderHelper.getAttributeValue(extendedPage.getPage().getAttributes(), MappingAttributeHelper.ATTR_SEITE_FP_ID));
		adMediumPage.setWorkPageLabel(JsonModelBuilderHelper.getAttributeValue(extendedPage.getPage().getAttributes(), MappingAttributeHelper.ATTR_ARBEITSSEITE));
		adMediumPage.setWorkPageNum(JsonModelBuilderHelper.getAttributeValue(extendedPage.getPage().getAttributes(), MappingAttributeHelper.ATTR_ARBEITSSEITE));
		adMediumPage.setMaster(Boolean.valueOf(JsonModelBuilderHelper.getAttributeValue(extendedPage.getPage().getAttributes(), MappingAttributeHelper.ATTR_SEITE_FP_ISMASTERPAGE)));
		adMediumPage.setAttempt(1+extendedPage.getAttempt());
		adMediumPage.setId(extendedPage.getId());
		adMediumPage.setFileName(extendedPage.getFileName());
		return adMediumPage;
	}
}
