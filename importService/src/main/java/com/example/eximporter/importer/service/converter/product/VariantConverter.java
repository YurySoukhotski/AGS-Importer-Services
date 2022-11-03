package com.example.eximporter.importer.service.converter.product;

import com.example.eximporter.importer.helper.JsonModelBuilderHelper;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import org.springframework.stereotype.Service;
import com.example.eximporter.importer.model.api.AttributeValues;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.OrderedAttributeValue;
import com.example.eximporter.importer.model.api.Product;
import com.example.eximporter.importer.model.xml.product.Sku;
import com.example.eximporter.importer.model.xml.product.SkuTranslation;

/**
 * Convert {@link Sku} to {@link Product} variant
 */
@Service
public class VariantConverter
{
	/**
	 * Convert from {@link Sku} to {@link Product}
	 * @param sku
	 *            received Sku model of variant
	 * @return variant product
	 */
	Product convertSkuToVariant(Sku sku)
	{
		Product variant = new Product();
		AttributesValues attributesValues = new AttributesValues();
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_FP_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(sku.getSkuTechId()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(sku.getSkuTechId()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_ERP_SIZE_KEY, JsonModelBuilderHelper.buildSimpleAttributeValues(sku.getErpSizeKey()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_SKU_SEQUENCE_NUMBER, JsonModelBuilderHelper.buildSimpleAttributeValues(sku.getSkuSequenceNum()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_RECOMMENDED_PRICE, JsonModelBuilderHelper.buildSimpleAttributeValues(sku.getRecommendedRetailPrice()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_SIZE_TITLE, JsonModelBuilderHelper.buildSimpleAttributeValues(sku.getSizeTitle()));
		AttributeValues attributeSizeValues = new AttributeValues();
		AttributeValues attributeSizeCategoryValues = new AttributeValues();
		AttributeValues attributeSkuMeasureValues = new AttributeValues();
		sku.getSkuTranslation().forEach(
			skuTranslation -> buildTranslationAttributes(skuTranslation, attributeSizeValues, attributeSizeCategoryValues, attributeSkuMeasureValues));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_SIZE, attributeSizeValues);
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_SIZE_CATEGORY, attributeSizeCategoryValues);
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_VAR_DEF_MEASURES, attributeSkuMeasureValues);
		attributesValues.put(MappingAttributeHelper.ATTR_PIM_OBJECT_NAME, JsonModelBuilderHelper.buildSimpleAttributeValues(buildPimObjAttribute(attributesValues)));
		variant.setAttributes(attributesValues);
		return variant;
	}

	/**
	 * Build attribute value for pim_obj attribute
	 * @param attributesValues
	 *            attributes for search values
	 * @return value
	 */
	private String buildPimObjAttribute(AttributesValues attributesValues)
	{
			for (OrderedAttributeValue orderedAttributeValue : attributesValues.get(MappingAttributeHelper.ATTR_AGS_VAR_DEF_SIZE))
			{
			if (orderedAttributeValue.getLang().equals(MappingAttributeHelper.D))
				{
					return orderedAttributeValue.getValue();
				}
			}
		return null;
	}

	/**
	 * Create attributes with value and language
	 * @param skuTranslation
	 *            content translations
	 * @param attributeSizeValues
	 *            language dependent attribute
	 * @param attributeSizeCategoryValues
	 * @param attributeSkuMeasureValues
	 */
	private void buildTranslationAttributes(SkuTranslation skuTranslation, AttributeValues attributeSizeValues,
											AttributeValues attributeSizeCategoryValues, AttributeValues attributeSkuMeasureValues)
	{
		String language = MappingAttributeHelper.getLanguageMapping().get(skuTranslation.getPrintLanguageId());
		attributeSizeValues.add(JsonModelBuilderHelper.buildOrderedAttributeValue(skuTranslation.getSizeTranslation(), language));
		attributeSizeCategoryValues.add(JsonModelBuilderHelper.buildOrderedAttributeValue(skuTranslation.getSizeCategoryTranslation(), language));
		attributeSkuMeasureValues.add(JsonModelBuilderHelper.buildOrderedAttributeValue(skuTranslation.getQsLength(), language));
	}
}
