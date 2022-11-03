package com.example.eximporter.importer.service.converter.project;

import com.example.eximporter.importer.model.xml.project.Project;
import com.example.eximporter.importer.model.api.AttributeValues;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.OrderedAttributeValue;
import com.example.eximporter.importer.model.xml.project.MetaDataAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.buildSimpleAttributeValues;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.*;


/**
 * Convert project from XML model to Project
 */
@Service
public class ProjectConverter
{
	private static final Logger logger = LoggerFactory.getLogger(ProjectConverter.class);
	private static final String ERROR_MAPPED_VALUE = "Value : {} not mapped in map ";

	/**
	 * Create project with attributes
	 * @param projectType
	 *            received project from xml
	 * @return Project with attributes
	 */
	public com.example.eximporter.importer.model.api.Project convertXmlToProject(Project projectType)
	{
		com.example.eximporter.importer.model.api.Project project = new com.example.eximporter.importer.model.api.Project();
		AttributesValues attributesValues = new AttributesValues();
		projectType.getMetaDataAttribute().forEach(metaDataAttribute -> fillAttributeValues(metaDataAttribute, attributesValues));
		project.setAttributes(attributesValues);
		project.setType(projectType.getProjectType());
		return project;
	}

	/**
	 * Find language projects by attributes and create list of language Project
	 * @param project
	 *            project to find language projects
	 * @return language projects
	 */
	public List<com.example.eximporter.importer.model.api.Project> extractLanguageProjects(com.example.eximporter.importer.model.api.Project project)
	{
		List<com.example.eximporter.importer.model.api.Project> languageProjects = new ArrayList<>();
		for (String attributeIdentifier : project.getAttributes().keySet())
		{
			if (attributeIdentifier.startsWith(ATTR_PREF_SPRACHVERSION) && attributeIdentifier.contains(ATTR_PREF_LAGO_KEY))
			{
				com.example.eximporter.importer.model.api.Project languageProject = new com.example.eximporter.importer.model.api.Project();
				String keyLanguage = attributeIdentifier.replace(ATTR_PREF_SPRACHVERSION, "");
				keyLanguage = keyLanguage.replace(ATTR_PREF_LAGO_KEY, "");
				AttributesValues attributesValues= new AttributesValues();
				attributesValues.put(ATTR_COUNTRY_VALUE, buildSimpleAttributeValues(getShortCountryValues().get(keyLanguage)));
				String valueEnum = getLanguageDescriptions().get(keyLanguage);
				if (valueEnum != null)
				{
					attributesValues.put(ATTR_SPRACHVERSION, buildSimpleAttributeValues(valueEnum));
				}
				attributesValues.put(ATTR_COUNTRY_IMPORT_VALUE, buildSimpleAttributeValues(getShortLanguageValues().get(keyLanguage)));
				languageProject.setAttributes(attributesValues);
				languageProjects.add(languageProject);
			}
		}
		return languageProjects;
	}


	/**
	 * Fill attributes by type
	 * @param metaDataAttribute
	 *            attribute to process
	 * @param attributesValues
	 *            created attributes
	 */
	private void fillAttributeValues(MetaDataAttributeType metaDataAttribute, AttributesValues attributesValues)
	{
		if (isConditionStringValueWithKey(metaDataAttribute))
		{
			createStringAttributeValue(metaDataAttribute, attributesValues);
		}
		if (isConditionStringValue(metaDataAttribute))
		{
			createSimpleStringAttributeValue(metaDataAttribute, attributesValues);
		}
		if (isConditionEnumerationValue(metaDataAttribute))
		{
			createEnumerationAttributeValue(metaDataAttribute, attributesValues);
		}
		if (isConditionBooleanValue(metaDataAttribute))
		{
			createBooleanAttributeValue(metaDataAttribute, attributesValues);
		}
		if (isSimpleConditionBooleanValue(metaDataAttribute))
		{
			createBooleanAttributeValue(metaDataAttribute, attributesValues);
		}
	}

	private boolean isConditionStringValueWithKey(MetaDataAttributeType metaDataAttribute)
	{
		return metaDataAttribute.getStringValue() != null && metaDataAttribute.getBooleanValue() != null;
	}

	private boolean isConditionBooleanValue(MetaDataAttributeType metaDataAttribute)
	{
		return metaDataAttribute.getBooleanValue() != null && metaDataAttribute.getStringValue() == null
			&& metaDataAttribute.getEnumerationValue() == null;
	}

	private boolean isSimpleConditionBooleanValue(MetaDataAttributeType metaDataAttribute)
	{
		return metaDataAttribute.getBooleanValue() != null && metaDataAttribute.getStringValue() != null
			|| metaDataAttribute.getBooleanValue() != null && metaDataAttribute.getEnumerationValue() != null;
	}

	private boolean isConditionEnumerationValue(MetaDataAttributeType metaDataAttribute)
	{
		return metaDataAttribute.getEnumerationValue() != null;
	}

	private boolean isConditionStringValue(MetaDataAttributeType metaDataAttribute)
	{
		return metaDataAttribute.getStringValue() != null && metaDataAttribute.getBooleanValue() == null;
	}

	private void createStringAttributeValue(MetaDataAttributeType metaDataAttribute, AttributesValues attributesValues)
	{
		OrderedAttributeValue orderedAttributeValue = new OrderedAttributeValue();
		AttributeValues values = new AttributeValues();
		String mappedValue = getAttributesMapping().get(metaDataAttribute.getIdentifier());
		if (mappedValue != null)
		{
			String identifierKey = mappedValue + ATTR_PREF_LAGO_KEY;
			orderedAttributeValue.setValue(metaDataAttribute.getStringValue());
			values.add(orderedAttributeValue);
			attributesValues.put(identifierKey, values);
		}
		else
		{
			logger.debug(ERROR_MAPPED_VALUE, metaDataAttribute.getIdentifier());
		}
	}

	private void createSimpleStringAttributeValue(MetaDataAttributeType metaDataAttribute, AttributesValues attributesValues)
	{
		OrderedAttributeValue orderedAttributeValue = new OrderedAttributeValue();
		AttributeValues values = new AttributeValues();
		String key = "";
		String mappedValue = getAttributesMapping().get(metaDataAttribute.getIdentifier());
		if (mappedValue != null)
		{
			String identifierKey = mappedValue + key;
			orderedAttributeValue.setValue(metaDataAttribute.getStringValue());
			values.add(orderedAttributeValue);
			attributesValues.put(identifierKey, values);
		}
		else
		{
			logger.debug(ERROR_MAPPED_VALUE, metaDataAttribute.getIdentifier());
		}
	}

	private void createEnumerationAttributeValue(MetaDataAttributeType metaDataAttribute, AttributesValues attributesValues)
	{
		OrderedAttributeValue orderedAttributeValue = new OrderedAttributeValue();
		AttributeValues values = new AttributeValues();
		String key = "";
		String mappedValue = getAttributesMapping().get(metaDataAttribute.getIdentifier());
		if (mappedValue != null)
		{
			String identifierKey = mappedValue + key;
			orderedAttributeValue.setValue(metaDataAttribute.getEnumerationValue());
			values.add(orderedAttributeValue);
			attributesValues.put(identifierKey, values);
		}
		else
		{
			logger.debug(ERROR_MAPPED_VALUE, metaDataAttribute.getIdentifier());
		}
	}

	private void createBooleanAttributeValue(MetaDataAttributeType metaDataAttribute, AttributesValues attributesValues)
	{
		OrderedAttributeValue orderedAttributeValue = new OrderedAttributeValue();
		AttributeValues values = new AttributeValues();
		String key = "";
		String mappedValue = getAttributesMapping().get(metaDataAttribute.getIdentifier());
		if (mappedValue != null)
		{
			String identifierKey = mappedValue + key;
			orderedAttributeValue.setValue(Boolean.toString(metaDataAttribute.getBooleanValue().equals(IS_VALUE)));
			values.add(orderedAttributeValue);
			attributesValues.put(identifierKey, values);
		}
		else
		{
			logger.debug(ERROR_MAPPED_VALUE, metaDataAttribute.getIdentifier());
		}
	}
}
