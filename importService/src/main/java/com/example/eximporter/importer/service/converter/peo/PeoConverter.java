package com.example.eximporter.importer.service.converter.peo;

import com.example.eximporter.importer.configuration.CommonConfiguration;
import com.example.eximporter.importer.helper.JsonModelBuilderHelper;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.Peo;
import com.example.eximporter.importer.model.api.TableAttributeValue;
import com.example.eximporter.importer.model.api.TableAttributeValueInner;
import com.example.eximporter.importer.model.api.TableAttributesValues;
import com.example.eximporter.importer.model.extended.ExtendedPeo;
import com.example.eximporter.importer.model.xml.peo.Placement;
import com.example.eximporter.importer.model.xml.peo.PlacementSku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Converts Placement to Peo
 */
@Service
public class PeoConverter {

    @Autowired
    private CommonConfiguration commonConfiguration;

    /**
     * Converts placement from "xml" object to "json" object.
     *
     * @param placement income peo
     * @return {@link Peo}
     */
    public ExtendedPeo convert(Placement placement) {
        Peo peo = new Peo();
        AttributesValues attributesValues = buildSimpleAttributesValues(placement);
        TableAttributesValues tableAttributesValues = buildTableAttributesValues(placement.getPlacementSku());
        peo.setTemplateId(commonConfiguration.getPeoTemplateId());
        peo.setAttributes(attributesValues);
        peo.setTableAttributes(tableAttributesValues);
        return new ExtendedPeo(peo, placement.getPlacementTechId(), placement.getArticleTechId());
    }

    /**
     * Builds simple attributes.
     *
     * @param placement income placement to fill peo attributes
     * @return {@link AttributesValues}
     */
    private AttributesValues buildSimpleAttributesValues(Placement placement) {
        AttributesValues attributesValues = new AttributesValues();
        attributesValues.put(MappingAttributeHelper.PLACEMENT_ID_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placement.getPlacementTechId()));
        attributesValues.put(MappingAttributeHelper.FP_PAGE_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(placement.getPageKey()));
        attributesValues.put(MappingAttributeHelper.ORDER_NUMBER_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placement.getOrderNumber()));
        attributesValues.put(MappingAttributeHelper.POSITION_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placement.getPosition()));
        attributesValues.put(MappingAttributeHelper.PAGE_SLICE_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placement.getPageSlice()));
        attributesValues.put(MappingAttributeHelper.FABRIC_SAMPLE_INDICATOR_IDENTIFIER,
                JsonModelBuilderHelper.buildSimpleAttributeValues(placement.getHasFabricSampleIndicator()));
        attributesValues.put(MappingAttributeHelper.ATTR_AGS_PEO_DEF_FP_ARTICLE_TECH_ID, JsonModelBuilderHelper.buildSimpleAttributeValues(placement.getArticleTechId()));
        return attributesValues;
    }

    /**
     * Builds table attributes.
     *
     * @param placementSkuList list of {@link PlacementSku} with data to fill table attribute
     * @return {@link TableAttributesValues}
     */
    private TableAttributesValues buildTableAttributesValues(List<PlacementSku> placementSkuList) {
        TableAttributesValues tableAttributesValues = new TableAttributesValues();
        TableAttributeValue tableAttributeValue = new TableAttributeValue();
        long rowNumber = 0;
        for (PlacementSku placementSku : placementSkuList) {
            TableAttributeValueInner tableAttributeRow = new TableAttributeValueInner();
            AttributesValues attributesValues = new AttributesValues();
            attributesValues.put(MappingAttributeHelper.SKU_ID_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getSkuTechId()));
            attributesValues.put(MappingAttributeHelper.COUNTRY_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getCountryIsoCode()));
            attributesValues.put(MappingAttributeHelper.PRICE_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getAdvertisedPrice()));
            attributesValues.put(MappingAttributeHelper.MARKDOWN_PRICE_IDENTIFIER, JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getMarkDownPrice()));

            attributesValues.put(MappingAttributeHelper.ATTR_AGS_PEO_DEF_ADV_CURRENCY, JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getCurrency()));

            attributesValues.put(MappingAttributeHelper.PRICE_REDUCTION_ROUNDED_DOWN_IDENTIFIER,
                    JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getPriceReductionRoundedDown()));
            attributesValues.put(MappingAttributeHelper.PRICE_REDUCTION_5_PERC_ROUNDED_IDENTIFIER,
                    JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getPriceReduction5PercRounded()));
            attributesValues.put(MappingAttributeHelper.PRICE_DELETED_FLAG_IDENTIFIER,
                    JsonModelBuilderHelper.buildSimpleAttributeValues(placementSku.getAdvertisedPriceDeletedIndicator()));

            tableAttributeRow.setRownum(rowNumber++);
            tableAttributeRow.setCells(attributesValues);
            tableAttributeValue.add(tableAttributeRow);
        }
        tableAttributesValues.put(MappingAttributeHelper.AGS_PEO_DEF_TABLE_PLACEMENT_SKU, tableAttributeValue);
        return tableAttributesValues;
    }
}
