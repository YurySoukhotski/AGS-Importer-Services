package com.example.eximporter.importer.service.converter.product;

import com.example.eximporter.importer.helper.JsonModelBuilderHelper;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.model.api.AttributeValues;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.OrderedAttributeValue;
import com.example.eximporter.importer.model.api.Product;
import com.example.eximporter.importer.model.api.TableAttributeValue;
import com.example.eximporter.importer.model.api.TableAttributeValueInner;
import com.example.eximporter.importer.model.api.TableAttributesValues;
import com.example.eximporter.importer.model.extended.ExtendedArticle;
import com.example.eximporter.importer.model.extended.ExtendedProduct;
import com.example.eximporter.importer.model.xml.product.GenreAttribute;
import com.example.eximporter.importer.model.xml.product.Style;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert {@link Style} to {@link ExtendedProduct}
 */
@Service
public class ProductConverter
{
	@Autowired
	private ArticleConverter articleConverter;

	/**
	 * Convert {@link Style} to {@link ExtendedProduct}
	 * @param style
	 *            xml model of product with all articles and variants
	 * @return Extended Product with linked article products
	 */
	public ExtendedProduct convertStyleToExtendedProduct(Style style)
	{
		Product product = new Product();
		AttributesValues attributesValues = new AttributesValues();
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_FP_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(style.getStyleTechId()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_ARTICLE_FORM_TYPE, JsonModelBuilderHelper.buildSimpleAttributeValues(style.getFormType()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_OMN_PRODUCT_KEY, JsonModelBuilderHelper.buildSimpleAttributeValues(style.getStyleKey()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_PRODUCT_MAIN_GROUP, JsonModelBuilderHelper.buildSimpleAttributeValues(style.getWogName()));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_PRODUCT_SUB_GROUP, JsonModelBuilderHelper.buildSimpleAttributeValues(style.getWugName()));
		TableAttributesValues tableAttributesValues = new TableAttributesValues();
		TableAttributeValue tableAttributeValue = new TableAttributeValue();
		for (int i = 0; i < style.getGenreAttribute().size(); i++)
		{
			buildGenreAttributes(style.getGenreAttribute().get(i), tableAttributeValue, i);
		}
		tableAttributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE, tableAttributeValue);
		List<ExtendedArticle> extendedArticles = new ArrayList<>();
		style.getArticle().forEach(article -> extendedArticles.add(articleConverter.convertArticleToExtendedArticle(article)));
		product.setAttributes(attributesValues);
		product.setTableAttributes(tableAttributesValues);
		product.getAttributes().put(MappingAttributeHelper.ATTR_PIM_OBJECT_NAME, extractPimObjectAttribute(extendedArticles));
		return new ExtendedProduct(product, extendedArticles);
	}

	/**
	 * Extract pim_obj attribute from any of language project
	 * @param extendedArticles
	 *            received articles
	 * @return attribute pim_obj
	 */
	private AttributeValues extractPimObjectAttribute(List<ExtendedArticle> extendedArticles)
	{
		for (ExtendedArticle extendedArticle : extendedArticles)
		{
				for (OrderedAttributeValue orderedAttributeValue : extendedArticle.getArticle().getAttributes()
					.get(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_NAME))
				{
				if (orderedAttributeValue.getLang().equals(MappingAttributeHelper.D))
					{
						return JsonModelBuilderHelper.buildSimpleAttributeValues(orderedAttributeValue.getValue());
					}
				}
			}
		return null;
	}

	/**
	 * Create Genre Table attributes
	 * @param genreAttribute
	 *            attributes to process
	 * @param tableAttributeValue
	 *            storage for all filled rows
	 * @param i
	 *            current row
	 */
	private void buildGenreAttributes(GenreAttribute genreAttribute, TableAttributeValue tableAttributeValue, int i)
	{
		AttributesValues attributesValues = new AttributesValues();
		AttributeValues attributeGenreID = new AttributeValues();
		AttributeValues attributeGenreLabel = new AttributeValues();
		AttributeValues attributeGenreValue = new AttributeValues();
		TableAttributeValueInner tableAttributeValueInner = new TableAttributeValueInner();
		tableAttributeValueInner.setRownum((long) i);
		attributeGenreID.add(JsonModelBuilderHelper.buildOrderedAttributeValue(genreAttribute.getGenreAttrId(), null));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE_ID, attributeGenreID);
		attributeGenreLabel.add(JsonModelBuilderHelper.buildOrderedAttributeValue(genreAttribute.getGenreAttrName(), null));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE_LABEL, attributeGenreLabel);
		attributeGenreValue.add(JsonModelBuilderHelper.buildOrderedAttributeValue(genreAttribute.getValue(), null));
		attributesValues.put(MappingAttributeHelper.ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE_VALUE, attributeGenreValue);
		tableAttributeValueInner.setCells(attributesValues);
		tableAttributeValue.add(tableAttributeValueInner);
	}
}
