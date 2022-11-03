package com.example.eximporter.importer.service.converter.project;

import com.example.eximporter.importer.helper.JsonModelBuilderHelper;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import org.springframework.stereotype.Service;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.Project;
import com.example.eximporter.importer.model.extended.ExtendedPage;
import com.example.eximporter.importer.model.xml.project.AdmediumPage;

/**
 * Convert {@link AdmediumPage} to {@link ExtendedPage} Project
 */
@Service
public class PageConverter
{
	/**
	 * Create from {@link AdmediumPage} new project and return {@link ExtendedPage}
	 * @param admediumPage
	 * @return
	 */
	public ExtendedPage convert(AdmediumPage admediumPage)
	{
		Project page = new Project();
		AttributesValues attributesValues = new AttributesValues();
		attributesValues.put(MappingAttributeHelper.ATTR_SEITE_FP_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(admediumPage.getPageTechId()));
		attributesValues.put(MappingAttributeHelper.ATTR_ARBEITSSEITE, JsonModelBuilderHelper.buildSimpleAttributeValues(admediumPage.getWorkPageLabel()));
		attributesValues.put(MappingAttributeHelper.ATTR_SEITE_FP_PAGEKEY, JsonModelBuilderHelper.buildSimpleAttributeValues(admediumPage.getPageKey()));
		attributesValues.put(MappingAttributeHelper.ATTR_SEITE_FP_ISMASTERPAGE, JsonModelBuilderHelper.buildSimpleAttributeValues(admediumPage.getMaster().toString()));
		page.setAttributes(attributesValues);
		return new ExtendedPage(page, admediumPage.getAdmediumVersionId(),
			admediumPage.getAttempt() != null ? admediumPage.getAttempt() : 0, admediumPage.getId(), admediumPage.getFileName());
	}
}
