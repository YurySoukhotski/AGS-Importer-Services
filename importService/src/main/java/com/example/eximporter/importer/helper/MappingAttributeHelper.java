package com.example.eximporter.importer.helper;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MappingAttributeHelper {
    // product general attributes
    public static final String ATTR_AGS_PRD_DEF_FP_ID = "AGS_PRD_DEF_FP_ID";
    public static final String ATTR_AGS_ART_DEF_FP_ID = "AGS_ART_DEF_FP_ID";
    public static final String ATTR_AGS_VAR_DEF_FP_ID = "AGS_VAR_DEF_FP_ID";
    public static final String ATTR_AGS_VAR_DEF_ID = "AGS_VAR_DEF_ID";
    public static final String ATTR_AGS_ART_DEF_ID = "AGS_ART_DEF_ID";
    public static final String ATTR_AGS_PRODUCT_DEFAULT = "AGS_PRODUCT_DEFAULT";
	public static final String ATTR_AGS_ARTICLE_DEFAULT = "AGS_ARTICLE_DEFAULT";
	public static final String ATTR_AGS_VARIANT_DEFAULT = "AGS_VARIANT_DEFAULT";
    public static final String ATTR_PIM_OBJECT_NAME = "PIM_OBJECT_NAME";
    public static final String IMPORTED_BY_FACHPORTAL = "IMPORTED_BY_FACHPORTAL";
    public static final String D = "D";
    public static final String FINSV = "FINSV";
    // product attributes mapping
    public static final String ATTR_AGS_PRD_DEF_ARTICLE_FORM_TYPE = "AGS_PRD_DEF_ARTICLE_FORM_TYPE";
    public static final String ATTR_AGS_PRD_DEF_OMN_PRODUCT_KEY = "AGS_PRD_DEF_OMN_PRODUCT_KEY";
    public static final String ATTR_AGS_PRD_DEF_PRODUCT_MAIN_GROUP = "AGS_PRD_DEF_PRODUCT_MAIN_GROUP";
    public static final String ATTR_AGS_PRD_DEF_PRODUCT_SUB_GROUP = "AGS_PRD_DEF_PRODUCT_SUB_GROUP";
    public static final String ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE = "AGS_PRD_DEF_GENRE_ATTRIBUTE";
    public static final String ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE_ID = "AGS_PRD_DEF_GENRE_ATTRIBUTE_ID";
    public static final String ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE_LABEL = "AGS_PRD_DEF_GENRE_ATTRIBUTE_LABEL";
    public static final String ATTR_AGS_PRD_DEF_GENRE_ATTRIBUTE_VALUE = "AGS_PRD_DEF_GENRE_ATTRIBUTE_VALUE";
    // article attributes mapping
    public static final String ATTR_AGS_ART_DEF_ARTICLE_KEY = "AGS_ART_DEF_ARTICLE_KEY";
    public static final String ATTR_AGS_ART_DEF_ARTICLE_FEATURE = "AGS_ART_DEF_ARTICLE_FEATURE";
    public static final String ATTR_AGS_ART_DEF_ARTICLE_FEATURE_ID = "AGS_ART_DEF_ARTICLE_FEATURE_ID";
    public static final String ATTR_AGS_ART_DEF_ARTICLE_FEATURE_TYPE = "AGS_ART_DEF_ARTICLE_FEATURE_TYPE";
    public static final String ATTR_AGS_ART_DEF_ARTICLE_FEATURE_VALUE = "AGS_ART_DEF_ARTICLE_FEATURE_VALUE";

    public static final String ATTR_AGS_ART_TRADEMARK_CARE_DESCR="AGS_ART_DEF_TRADEMARKCAREDESCRIPTION";
    public static final String ATTR_AGS_ART_DEF_TRADEMARKS="AGS_ART_DEF_TRADEMARKS";

    public static final String ATTR_AGS_ART_DEF_COLOR_GROUP = "AGS_ART_DEF_COLOR_GROUP";
    public static final String ATTR_AGS_ART_DEF_COLOR_RANGE = "AGS_ART_DEF_COLOR_RANGE";
    public static final String ATTR_AGS_ART_DEF_PURCHASE_TEAM_NAME = "AGS_ART_DEF_PURCHASE_TEAM_NAME";
    public static final String ATTR_AGS_ART_DEF_MATERIAL = "AGS_ART_DEF_MATERIAL";
    public static final String ATTR_AGS_ART_DEF_WASHCOMMENT = "AGS_ART_DEF_WASHCOMMENT";
    public static final String ATTR_AGS_ART_DEF_COLOR = "AGS_ART_DEF_COLOR";
    public static final String ATTR_AGS_ART_DEF_ARTICLE_NAME = "AGS_ART_DEF_ARTICLE_NAME";
    public static final String ATTR_AGS_ART_DEF_ARTICLE_FORM = "AGS_ART_DEF_ARTICLE_FORM";
    public static final String ATTR_AGS_ART_DEF_EKMODUL="AGS_ART_DEF_EKMODUL";

    // variant attributes mapping
    public static final String ATTR_AGS_VAR_DEF_ERP_SIZE_KEY = "AGS_VAR_DEF_ERP_SIZE_KEY";
    public static final String ATTR_AGS_VAR_DEF_SKU_SEQUENCE_NUMBER = "AGS_VAR_DEF_SKU_SEQUENCE_NUMBER";
    public static final String ATTR_AGS_VAR_DEF_MEASURES = "AGS_VAR_DEF_MEASURES";
    public static final String ATTR_AGS_VAR_DEF_SIZE = "AGS_VAR_DEF_SIZE";
    public static final String ATTR_AGS_VAR_DEF_SIZE_CATEGORY = "AGS_VAR_DEF_SIZE_CATEGORY";
    public static final String ATTR_AGS_VAR_DEF_RECOMMENDED_PRICE = "AGS_VAR_DEF_RECOMMENDED_PRICE";
    public static final String ATTR_AGS_VAR_DEF_SIZE_TITLE = "AGS_VAR_DEF_SIZE_TITLE";

    // product service attributes
    public static final String ATTR_AGS_PRD_DEF_IMPORTED_BY_FACHPORTAL_DATETIME = "AGS_PRD_DEF_IMPORTED_BY_FACHPORTAL_DATETIME";
    public static final String ATTR_AGS_PRD_DEF_IMPORTED_BY_FACHPORTAL = "AGS_PRD_DEF_IMPORTED_BY_FACHPORTAL";
    public static final String ATTR_AGS_PRD_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME =
            "AGS_PRD_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME";
    public static final String ATTR_AGS_PRD_DEF_SYNCHRONIZED_BY_FACHPORTAL = "AGS_PRD_DEF_SYNCHRONIZED_BY_FACHPORTAL";
    public static final String ATTR_AGS_ART_DEF_IMPORTED_BY_FACHPORTAL_DATETIME = "AGS_ART_DEF_IMPORTED_BY_FACHPORTAL_DATETIME";
    public static final String ATTR_AGS_ART_DEF_IMPORTED_BY_FACHPORTAL = "AGS_ART_DEF_IMPORTED_BY_FACHPORTAL";
    public static final String ATTR_AGS_ART_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME =
            "AGS_ART_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME";
    public static final String ATTR_AGS_ART_DEF_SYNCHRONIZED_BY_FACHPORTAL = "AGS_ART_DEF_SYNCHRONIZED_BY_FACHPORTAL";
    public static final String ATTR_AGS_VAR_DEF_IMPORTED_BY_FACHPORTAL_DATETIME = "AGS_VAR_DEF_IMPORTED_BY_FACHPORTAL_DATETIME";
    public static final String ATTR_AGS_VAR_DEF_IMPORTED_BY_FACHPORTAL = "AGS_VAR_DEF_IMPORTED_BY_FACHPORTAL";
    public static final String ATTR_AGS_VAR_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME =
            "AGS_VAR_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME";
    public static final String ATTR_AGS_VAR_DEF_SYNCHRONIZED_BY_FACHPORTAL = "AGS_VAR_DEF_SYNCHRONIZED_BY_FACHPORTAL";
    public static final String PRD_IDENTIFIER = "PRD_ID_";
    public static final String ART_IDENTIFIER = "_ART_IT_";
    public static final String VAR_IDENTIFIER = "_V_ID_";
    // project general attributes
    public static final String ATTR_SAISON_FP_ID = "SAISON_FP_ID";
    public static final String ATTR_WERBEMITTEL_FP_ID = "WERBEMITTEL_FP_ID";
    public static final String ATTR_LANGUAGE_FP_ID = "LANGUAGE_FP_ID";
    public static final String ATTR_SEITE_FP_ID = "SEITE_FP_ID";
    public static final String ATTR_AGS_3_SAISON = "AGS_3_SAISON";
    public static final String ATTR_AGS_4_WERBEMITTELVARIANTE = "AGS_4_WERBEMITTELVARIANTE";
    public static final String ATTR_AGS_5_SPRACHVERSION = "AGS_5_SPRACHVERSION";
    public static final String ATTR_AGS_6_SEITE = "AGS_6_SEITE";
    // project attribute prefix
    public static final String ATTR_PREF_SPRACHVERSION = "SPRACHVERSION_";
    public static final String ATTR_PREF_LAGO_KEY = "_LAGO_KEY";
    public static final String IS_VALUE = "1";
    // project attributes mapping
    public static final String ATTR_SPRACHVERSION = "SPRACHVERSION";
    public static final String ATTR_COUNTRY_VALUE = "COUNTRY_VALUE";
    public static final String ATTR_COUNTRY_IMPORT_VALUE = "COUNTRY_IMPORT_VALUE";
    public static final String ATTR_SAISON = "SAISON";
    public static final String SAISON_IMPORTED_BY_FACHPORTAL_DATE = "SAISON_IMPORTED_BY_FACHPORTAL_DATE";
    public static final String SAISON_IMPORTED_BY_FACHPORTAL = "SAISON_IMPORTED_BY_FACHPORTAL";
    public static final String SAISON_SYNCHRONIZED_BY_FACHPORTAL_DATE = "SAISON_SYNCHRONIZED_BY_FACHPORTAL_DATE";
    public static final String SAISON_SYNCHRONIZED_BY_FACHPORTAL = "SAISON_SYNCHRONIZED_BY_FACHPORTAL";
    public static final String WERBEMITTEL_IMPORTED_BY_FACHPORTAL = "WERBEMITTEL_IMPORTED_BY_FACHPORTAL";
    public static final String WERBEMITTEL_SYNCHRONIZED_BY_FACHPORTAL = "WERBEMITTEL_SYNCHRONIZED_BY_FACHPORTAL";
    public static final String WERBEMITTEL_IMPORTED_BY_FACHPORTAL_DATE = "WERBEMITTEL_IMPORTED_BY_FACHPORTAL_DATE";
    public static final String WERBEMITTEL_SYNCHRONIZED_BY_FACHPORTAL_DATE = "WERBEMITTEL_SYNCHRONIZED_BY_FACHPORTAL_DATE";
    public static final String ATTR_WERBEMITTELSCHLUESSEL = "WERBEMITTELSCHLUESSEL";
    public static final String LANGUAGE_IMPORTED_BY_FACHPORTAL_DATE = "LANGUAGE_IMPORTED_BY_FACHPORTAL_DATE";
    public static final String LANGUAGE_IMPORTED_BY_FACHPORTAL = "LANGUAGE_IMPORTED_BY_FACHPORTAL";
    public static final String LANGUAGE_SYNCHRONIZED_BY_FACHPORTAL_DATE = "LANGUAGE_SYNCHRONIZED_BY_FACHPORTAL_DATE";
    public static final String LANGUAGE_SYNCHRONIZED_BY_FACHPORTAL = "LANGUAGE_SYNCHRONIZED_BY_FACHPORTAL";
    public static final String ATTR_ARBEITSSEITE = "ARBEITSSEITE";
    public static final String ATTR_SEITE_FP_PAGEKEY = "SEITE_FP_PAGEKEY";
    public static final String ATTR_SEITE_FP_ISMASTERPAGE = "SEITE_FP_ISMASTERPAGE";
    public static final String SEITE_IMPORTED_BY_FACHPORTAL_DATE = "SEITE_IMPORTED_BY_FACHPORTAL_DATE";
    public static final String SEITE_IMPORTED_BY_FACHPORTAL = "SEITE_IMPORTED_BY_FACHPORTAL";
    public static final String SEITE_SYNCHRONIZED_BY_FACHPORTAL_DATE = "SEITE_SYNCHRONIZED_BY_FACHPORTAL_DATE";
    public static final String SEITE_SYNCHRONIZED_BY_FACHPORTAL = "SEITE_SYNCHRONIZED_BY_FACHPORTAL";

    //peo service attributes
    public static final String PLACEMENT_ID_IDENTIFIER = "AGS_PEO_DEF_FP_PLACEMENT_ID";
    public static final String ORDER_NUMBER_IDENTIFIER = "AGS_ART_DEF_ARTICLE_PLACEMENT_ORDER_NUMBER";
    public static final String POSITION_IDENTIFIER = "AGS_PEO_DEF_FP_POSITION";
    public static final String PAGE_SLICE_IDENTIFIER = "AGS_PEO_DEF_FP_PAGESLICE";
    public static final String FP_PAGE_ID = "AGS_PEO_DEF_FP_PAGEID";
    public static final String FABRIC_SAMPLE_INDICATOR_IDENTIFIER = "AGS_ART_DEF_HAS_FABRIC_SAMPLE_INDICATOR";
    public static final String SKU_ID_IDENTIFIER = "AGS_PEO_DEF_SKU_ID";
    public static final String COUNTRY_IDENTIFIER = "AGS_PEO_DEF_COUNTRY";
    public static final String PRICE_IDENTIFIER = "AGS_PEO_DEF_ADV_PRICE";
    public static final String ATTR_AGS_PEO_DEF_ADV_CURRENCY = "AGS_PEO_DEF_ADV_CURRENCY";
    public static final String MARKDOWN_PRICE_IDENTIFIER = "AGS_PEO_DEF_MARKDOWN_PRICE";
    public static final String PRICE_REDUCTION_ROUNDED_DOWN_IDENTIFIER = "AGS_PEO_DEF_MAX_PRICE_REDUCTION_ROUNDED_DOWN";
    public static final String PRICE_REDUCTION_5_PERC_ROUNDED_IDENTIFIER = "AGS_PEO_DEF_MAX_PRICE_REDUCTION5_PERC_ROUNDED";
    public static final String PRICE_DELETED_FLAG_IDENTIFIER = "AGS_PEO_DEF_PF_ADVERTICED_PRICE_DELETED_FLAG";
    public static final String AGS_PEO_DEF_TABLE_PLACEMENT_SKU = "AGS_PEO_DEF_TABLE_PLACEMENT_SKU";
    public static final String PEO_IMPORTED_DATETIME = "AGS_PEO_DEF_IMPORTED_BY_FACHPORTAL_DATETIME";
    public static final String PEO_IMPORTED = "AGS_PEO_DEF_IMPORTED_BY_FACHPORTAL";
    public static final String PEO_SYNCHRONIZED_DATETIME = "AGS_PEO_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME";
    public static final String PEO_SYNCHRONIZED = "AGS_PEO_DEF_SYNCHRONIZED_BY_FACHPORTAL";
    public static final String ATTR_AGS_PEO_DEF_FP_ARTICLE_TECH_ID ="AGS_PEO_DEF_FP_ARTICLE_TECH_ID";
    public static final String ATTR_AGS_PEO_DEF_PLACED_FP="AGS_PEO_DEF_PLACED_FP";

    //notification
    public static final String ARTICLE_IMPORT_ARRIVED="Article import arrived";
    public static final String CATALOG_IMPORT_ARRIVED="Catalog import arrived";
    public static final String PAGE_IMPORT_ARRIVED="Page import arrived";
    public static final String PEO_IMPORT_ARRIVED="Placement import arrived";

    public static final String ERROR_EXEC_ARTICLE="Error during execution of Article import";
    public static final String ERROR_EXEC_CATALOG="Error during execution of Catalog import";
    public static final String ERROR_EXEC_PAGE="Error during execution of Page import";
    public static final String ERROR_EXEC_PEO="Error during execution of Placement import";

    public static final String PROBLEM_WITH_ARTICLE="Problem with input data in Article import";
    public static final String PROBLEM_WITH_CATALOG="Problem with input data in Catalog import";
    public static final String PROBLEM_WITH_PAGE="Problem with input data in Page import";
    public static final String PROBLEM_WITH_PEO="Problem with input data in Placement import";

    // for parked case
    public static final String WARN_ARTICLE="Warning during execution of Article import";
    public static final String WARN_CATALOG="Warning during execution of Catalog import";
    public static final String WARN_PAGE="Warning during execution of Page import";
    public static final String WARN_PEO="Warning during execution of Placement import";

    public static final String PARKED_PAGE="Parked Page report";
    public static final String PARKED_PEO="Parked Peo report";
    public static final String PEO="Peo";
    public static final String PAGE="Page";

    public static final String FINISH_IMPORT_ARTICLE="Article import is finished";
    public static final String FINISH_IMPORT_CATALOG="Catalog import is finished";
    public static final String FINISH_IMPORT_PAGE="Page import is finished";
    public static final String FINISH_IMPORT_PEO="Placement import is finished";
    public static final String JOB_ALREADY_COMPLETED="Import file with the given name was already processed";
    public static final String CLEANING_SERVICE="Cleaning service report";
    public static final String WRONG_SEQ_NUMBER="Wrong sequence number";

    private static final Map<String, String> attributesMapping = new HashMap<>();
    private static final Map<String, String> languageDescriptions = new HashMap<>();
    private static final Map<String, String> shortCountryValues = new HashMap<>();
    private static final Map<String, String> shortLanguageValues = new HashMap<>();
    private static final Map<String, String> languageMapping = new HashMap<>();
    private static final Map<String, String> prefixCountryValues=new HashMap<>();

    static {
        attributesMapping.put("marke", "VERTRIEBSMARKE");
        attributesMapping.put("Saison", ATTR_SAISON);
        attributesMapping.put("EINSATZ_CHF", "EINSATZ_CHF");
        attributesMapping.put("EINSATZ_SWE", "EINSATZ_SWE");
        attributesMapping.put("EINSATZ_A", "EINSATZ_AT");
        attributesMapping.put("EINSATZ_BNL", "EINSATZ_BNL");
        attributesMapping.put("EINSATZ_BF", "EINSATZ_BF");
        attributesMapping.put("EINSATZ_D", "EINSATZ_DE");
        attributesMapping.put("EINSATZ_F", "EINSATZ_FR");
        attributesMapping.put("EINSATZ_FIN", "EINSATZ_FIN");
        attributesMapping.put("EINSATZ_NOR", "EINSATZ_NOR");
        attributesMapping.put("EINSATZ_FIS", "EINSATZ_FIS");
        attributesMapping.put("EINSATZ_GB", "EINSATZ_GB");
        attributesMapping.put("EINSATZ_CHD", "EINSATZ_CHD");
        attributesMapping.put("EINSATZ_NL", "EINSATZ_NL");
        attributesMapping.put("SPRACHVERSION_CHF", "SPRACHVERSION_CHF");
        attributesMapping.put("SPRACHVERSION_SWE", "SPRACHVERSION_SWE");
        attributesMapping.put("SPRACHVERSION_A", "SPRACHVERSION_AT");
        attributesMapping.put("SPRACHVERSION_BNL", "SPRACHVERSION_BNL");
        attributesMapping.put("SPRACHVERSION_BF", "SPRACHVERSION_BF");
        attributesMapping.put("SPRACHVERSION_D", "SPRACHVERSION_DE");
        attributesMapping.put("SPRACHVERSION_F", "SPRACHVERSION_FR");
        attributesMapping.put("SPRACHVERSION_FIN", "SPRACHVERSION_FIN");
        attributesMapping.put("SPRACHVERSION_NOR", "SPRACHVERSION_NOR");
        attributesMapping.put("SPRACHVERSION_FIS", "SPRACHVERSION_FIS");
        attributesMapping.put("SPRACHVERSION_GB", "SPRACHVERSION_GB");
        attributesMapping.put("SPRACHVERSION_CHD", "SPRACHVERSION_CHD");
        attributesMapping.put("SPRACHVERSION_NL", "SPRACHVERSION_NL");
        attributesMapping.put("WERBEMITTELART", "WERBEMITTELART");
        attributesMapping.put("WERBEMITTELBEZEICHNUNG", "WERBEMITTELBEZEICHNUNG");
        attributesMapping.put("PROJEKTVERANTWORTLICH", "PROJEKTVERANTWORTLICH");
        attributesMapping.put("VARIANTENSCHLUESSEL", ATTR_WERBEMITTEL_FP_ID);
        attributesMapping.put("WERBEMITTELKATEGORIE", "WERBEMITTELKATEGORIE");
        attributesMapping.put("SEITEN", "SEITEN");
        attributesMapping.put("IS_MASTER","WERBEMITTEL_FP_ISMASTERPROJECT");
        attributesMapping.put("STOFFMUSTER", "STOFFMUSTER");
        languageDescriptions.put("AT", "A - Österreich (deutsch)");
        languageDescriptions.put("BF", "BF - Belgien (französisch)");
        languageDescriptions.put("BNL", "BNL - Belgien (niederländisch)");
        languageDescriptions.put("CHD", "CHD - Schweiz (deutsch)");
        languageDescriptions.put("CHF", "CHF - Schweiz (französisch)");
        languageDescriptions.put("DE", "D - Deutschland (deutsch)");
        languageDescriptions.put("FR", "F - Frankreich (französisch)");
        languageDescriptions.put("FIN", "FIN - Finnland (finnisch)");
        languageDescriptions.put("NOR", "NOR - Norwegen (norwegisch)");
        languageDescriptions.put("NL", "NL - Niederlande (niederländisch)");
        languageDescriptions.put("SWE", "SWE - Schweden (schwedisch)");

        shortCountryValues.put("AT", "A");
        shortCountryValues.put("BF", "BF");
        shortCountryValues.put("BNL", "BNL");
        shortCountryValues.put("CHD", "CHD");
        shortCountryValues.put("CHF", "CHF");
        shortCountryValues.put("DE", D);
        shortCountryValues.put("FR", "F");
        shortCountryValues.put("FIS", FINSV);
        shortCountryValues.put("FIN", "FIN");
        shortCountryValues.put("GB", "EN");
        shortCountryValues.put("NOR", "NOR");
        shortCountryValues.put("NL", "NL");
        shortCountryValues.put("SWE", "SWE");

        prefixCountryValues.put("A", "AT");
        prefixCountryValues.put("BF", "BF");
        prefixCountryValues.put("BNL", "BNL");
        prefixCountryValues.put("CHD", "CHD");
        prefixCountryValues.put("CHF", "CHF");
        prefixCountryValues.put("D", "DE");
        prefixCountryValues.put("F", "FR");
        prefixCountryValues.put(FINSV, "FIS");
        prefixCountryValues.put("FIN", "FIN");
        prefixCountryValues.put("EN", "EN");
        prefixCountryValues.put("NOR", "NOR");
        prefixCountryValues.put("NL", "NL");
        prefixCountryValues.put("SWE", "SWE");

        shortLanguageValues.put("AT", "A");
        shortLanguageValues.put("BF", "B");
        shortLanguageValues.put("BNL", "B");
        shortLanguageValues.put("CHD", "CH");
        shortLanguageValues.put("CHF", "CH");
        shortLanguageValues.put("DE", D);
        shortLanguageValues.put("FR", "F");
        shortLanguageValues.put("FIS", "FI");
        shortLanguageValues.put("FIN", "FI");
        shortLanguageValues.put("GB", "GB");
        shortLanguageValues.put("NOR", "N");
        shortLanguageValues.put("NL", "NL");
        shortLanguageValues.put("SWE", "S");

        languageMapping.put(D, D);
        languageMapping.put("A", "A");
        languageMapping.put("CH", "CHD");
        languageMapping.put("CH-F", "CHF");
        languageMapping.put("B-F", "BF");
        languageMapping.put("FI-S", FINSV);
        languageMapping.put("N", "NOR");
        languageMapping.put("NL", "NL");
        languageMapping.put("F", "F");
        languageMapping.put("B", "BNL");
        languageMapping.put("FI", "FIN");
        languageMapping.put("S", "SWE");
    }

    public static final String ERROR_API_MSG = "Operation failed with message: {} and HttpCode : - {}";

    private MappingAttributeHelper() {
    }

    public static Map<String, String> getAttributesMapping() {
        return attributesMapping;
    }

    public static Map<String, String> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public static Map<String, String> getShortCountryValues() {
        return shortCountryValues;
    }

    public static Map<String, String> getShortLanguageValues() {
        return shortLanguageValues;
    }

    public static Map<String, String> getLanguageMapping() {
        return languageMapping;
    }

    public static Map<String, String> getPrefixCountryValues() {
        return prefixCountryValues;
    }
}
