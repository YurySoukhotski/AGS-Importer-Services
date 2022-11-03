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
import com.example.eximporter.importer.model.xml.product.Article;
import com.example.eximporter.importer.model.xml.product.ArticleFeature;
import com.example.eximporter.importer.model.xml.product.ArticleTranslation;
import com.example.eximporter.importer.model.xml.product.MaterialComposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert article from xml model to article {@link Product}
 */
@Service
public class ArticleConverter {
    @Autowired
    private VariantConverter variantConverter;
    private static final String ATTR_DELIMITER = " - ";
    private static final String ATTR_DELIMITER_START = " (";
    private static final String ATTR_DELIMITER_END = ")";

    /**
     * Get XML Article and Convert to Article Product
     *
     * @param receivedArticle xml model of article
     * @return article product with linked variants
     */
    ExtendedArticle convertArticleToExtendedArticle(Article receivedArticle) {
        Product article = new Product();
        AttributesValues attributesValues = new AttributesValues();
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_FP_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getArticleTechId()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getArticleTechId()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_KEY, JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getArticleKey()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_TRADEMARKS,
                JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getTrademarks()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_TRADEMARK_CARE_DESCR, JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getTrademarkCareTagDescription()));

        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_EKMODUL,
                JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getModuleDescription()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_COLOR_GROUP,
                JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getColorGroupDescription()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_COLOR_RANGE,
                JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getColorRangeDescription()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_PURCHASE_TEAM_NAME,
                JsonModelBuilderHelper.buildSimpleAttributeValues(receivedArticle.getPurchaseTeamName()));
        AttributeValues attributeMaterialComposition = new AttributeValues();
        receivedArticle.getMaterialComposition()
                .forEach(materialComposition -> buildMaterialAttributes(materialComposition, attributeMaterialComposition));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_MATERIAL, attributeMaterialComposition);
        AttributeValues attributeWashComment = new AttributeValues();
        AttributeValues attributeColor = new AttributeValues();
        AttributeValues attributeArticleName = new AttributeValues();
        AttributeValues attributeArticleForm = new AttributeValues();
        receivedArticle.getArticleTranslation().forEach(articleTranslation -> buildArticleTranslations(articleTranslation,
                attributeWashComment, attributeColor, attributeArticleName, attributeArticleForm));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_WASHCOMMENT, attributeWashComment);
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_COLOR, attributeColor);
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_NAME, attributeArticleName);
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_FORM, attributeArticleForm);
        List<Product> variants = new ArrayList<>();
        receivedArticle.getSku().forEach(sku -> variants.add(variantConverter.convertSkuToVariant(sku)));
        article.setAttributes(attributesValues);
        article.getAttributes().put(MappingAttributeHelper.ATTR_PIM_OBJECT_NAME, JsonModelBuilderHelper.buildSimpleAttributeValues(buildPimObjAttribute(attributesValues)));

        if (receivedArticle.getArticleFeature() != null) {
            TableAttributesValues tableAttributesValues = new TableAttributesValues();
            TableAttributeValue tableAttributeValue = new TableAttributeValue();
            for (int i = 0; i < receivedArticle.getArticleFeature().size(); i++) {
                buildArticleFeatureAttributes(receivedArticle.getArticleFeature().get(i), tableAttributeValue, i);
            }
            tableAttributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_FEATURE, tableAttributeValue);
            article.setTableAttributes(tableAttributesValues);
        }


        return new ExtendedArticle(article, variants);
    }

    /**
     * Build table article feature attributes
     *
     * @param articleFeature
     * @return
     */
    private void buildArticleFeatureAttributes(ArticleFeature articleFeature, TableAttributeValue tableAttributeValue, int i) {
        AttributesValues attributesValues = new AttributesValues();

        AttributeValues attributeFeatureId = new AttributeValues();
        AttributeValues attributeFeatureType = new AttributeValues();
        AttributeValues attributeFeatureValue = new AttributeValues();

        TableAttributeValueInner tableAttributeValueInner = new TableAttributeValueInner();
        tableAttributeValueInner.setRownum((long) i);

        attributeFeatureId.add(JsonModelBuilderHelper.buildOrderedAttributeValue(articleFeature.getArticleFeatureId(), null));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_FEATURE_ID, attributeFeatureId);

        attributeFeatureType.add(JsonModelBuilderHelper.buildOrderedAttributeValue(articleFeature.getArticleFeatureType(), null));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_FEATURE_TYPE, attributeFeatureType);

        attributeFeatureValue.add(JsonModelBuilderHelper.buildOrderedAttributeValue(articleFeature.getValue(), null));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_FEATURE_VALUE, attributeFeatureValue);

        tableAttributeValueInner.setCells(attributesValues);
        tableAttributeValue.add(tableAttributeValueInner);
    }

    /**
     * Build attribute value for pim_obj attribute
     *
     * @param attributesValues attributes for search values
     * @return value
     */
    private String buildPimObjAttribute(AttributesValues attributesValues) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JsonModelBuilderHelper.getAttributeValue(attributesValues, MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_KEY)).append(ATTR_DELIMITER);
        for (OrderedAttributeValue orderedAttributeValue : attributesValues.get(MappingAttributeHelper.ATTR_AGS_ART_DEF_ARTICLE_NAME)) {
            if (orderedAttributeValue.getLang().equals(MappingAttributeHelper.D)) {
                stringBuilder.append(orderedAttributeValue.getValue());
            }
        }
        stringBuilder.append(ATTR_DELIMITER_START);
        for (OrderedAttributeValue orderedAttributeValue : attributesValues.get(MappingAttributeHelper.ATTR_AGS_ART_DEF_COLOR)) {
            if (orderedAttributeValue.getLang().equals(MappingAttributeHelper.D)) {
                stringBuilder.append(orderedAttributeValue.getValue());
            }
        }
        stringBuilder.append(ATTR_DELIMITER_END);
        return stringBuilder.toString();
    }

    /**
     * Fill translated attributes with value and language
     *
     * @param articleTranslation   contains values for all attributes for one language
     * @param attributeWashComment language dependency attribute
     * @param attributeColor       language dependency attribute
     * @param attributeArticleName language dependency attribute
     * @param attributeArticleForm language dependency attribute
     */
    private void buildArticleTranslations(ArticleTranslation articleTranslation, AttributeValues attributeWashComment,
                                          AttributeValues attributeColor, AttributeValues attributeArticleName, AttributeValues attributeArticleForm) {
        String language = MappingAttributeHelper.getLanguageMapping().get(articleTranslation.getPrintLanguageId());
        attributeWashComment.add(JsonModelBuilderHelper.buildOrderedAttributeValue(articleTranslation.getCare(), language));
        attributeColor.add(JsonModelBuilderHelper.buildOrderedAttributeValue(articleTranslation.getColorCombination(), language));
        attributeArticleName.add(JsonModelBuilderHelper.buildOrderedAttributeValue(articleTranslation.getName(), language));
        attributeArticleForm.add(JsonModelBuilderHelper.buildOrderedAttributeValue(articleTranslation.getArticleForm(), language));
    }

    /**
     * Build translated attribute with value and language
     *
     * @param materialComposition          contains value for attribute material composition for separated language
     * @param attributeMaterialComposition
     */
    private void buildMaterialAttributes(MaterialComposition materialComposition, AttributeValues attributeMaterialComposition) {
        String language = MappingAttributeHelper.getLanguageMapping().get(materialComposition.getPrintLanguageId());
        attributeMaterialComposition.add(JsonModelBuilderHelper.buildOrderedAttributeValue(materialComposition.getValue(), language));
    }
}
