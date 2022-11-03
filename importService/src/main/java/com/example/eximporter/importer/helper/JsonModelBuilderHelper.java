package com.example.eximporter.importer.helper;

import org.springframework.util.CollectionUtils;

import com.example.eximporter.importer.model.api.AttributeValues;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.OrderedAttributeValue;

/**
 * Help to create or get attribute from JSON model api
 */
public class JsonModelBuilderHelper
{
	private JsonModelBuilderHelper()
	{
		throw new IllegalStateException("Utility class");
	}

	public static String getAttributeValue(AttributesValues attributesMap, String attributeName)
	{
		if (CollectionUtils.isEmpty(attributesMap) || CollectionUtils.isEmpty(attributesMap.get(attributeName)))
		{
			return null;
		}
		return attributesMap.get(attributeName).get(0).getValue();
	}

	public static AttributeValues buildSimpleAttributeValues(String value)
	{
		AttributeValues attributeValues = new AttributeValues();
		attributeValues.add(buildOrderedAttributeValue(value, null));
		return attributeValues;
	}

	public static OrderedAttributeValue buildOrderedAttributeValue(String value, String language)
	{
		OrderedAttributeValue orderedAttributeValue = new OrderedAttributeValue();
		orderedAttributeValue.setValue(value);
		orderedAttributeValue.setLang(language);
		return orderedAttributeValue;
	}
}
