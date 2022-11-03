package com.example.eximporter.importer.workflow.writer.product;

import com.example.eximporter.importer.model.api.Product;
import com.example.eximporter.importer.service.http.ProductApiService;
import com.example.eximporter.importer.service.http.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.buildSimpleAttributeValues;
import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.getAttributeValue;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.*;

/**
 * Base batch operations with write product to REST api
 */
public class BaseProductWriter {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProductApiService apiService;
    private SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
    private static final String MSG_WRONG_TYPE = "Wrong type of Product: ";

    /**
     * Process  and Update existing product
     *
     * @param existingProductId
     * @param newProduct
     * @return id
     * @throws RestException
     */
    Long processProductForUpdate(Long existingProductId, Product newProduct) throws
            RestException {

        newProduct.getAttributes().put(getBooleanAttributeForUpdate(newProduct.getType()),
                buildSimpleAttributeValues(Boolean.TRUE.toString()));
        newProduct.getAttributes().put(getDateAttributeForUpdate(newProduct.getType()),
                buildSimpleAttributeValues(formatter.format(new Date())));
        updateProduct(newProduct, existingProductId);
        return existingProductId;

    }

    /**
     * Process and create new product
     *
     * @param newProduct
     * @param parentId
     * @return
     * @throws RestException
     */
    Long processProductForCreate(Product newProduct, Long parentId) throws RestException {

        newProduct.setParentId(parentId);
        newProduct.getAttributes().put(getBooleanAttributeForCreate(newProduct.getType()),
                buildSimpleAttributeValues(Boolean.TRUE.toString()));
        newProduct.getAttributes().put(getDateAttributeForCreate(newProduct.getType()),
                buildSimpleAttributeValues(formatter.format(new Date())));
        return createProduct(newProduct);
    }


    /**
     * Get attribute to insert in product while updating
     *
     * @param type of product
     * @return attribute identifier
     */
    private String getBooleanAttributeForUpdate(String type) throws RestException {
        switch (type) {
            case ATTR_AGS_PRODUCT_DEFAULT:
                return ATTR_AGS_PRD_DEF_SYNCHRONIZED_BY_FACHPORTAL;
            case ATTR_AGS_ARTICLE_DEFAULT:
                return ATTR_AGS_ART_DEF_SYNCHRONIZED_BY_FACHPORTAL;
            case ATTR_AGS_VARIANT_DEFAULT:
                return ATTR_AGS_VAR_DEF_SYNCHRONIZED_BY_FACHPORTAL;
            default:
                throw new RestException(MSG_WRONG_TYPE + type);
        }
    }

    /**
     * Get attribute to insert in product while creating
     *
     * @param type of product
     * @return attribute identifier
     */
    private String getBooleanAttributeForCreate(String type) throws RestException {
        switch (type) {
            case ATTR_AGS_PRODUCT_DEFAULT:
                return ATTR_AGS_PRD_DEF_IMPORTED_BY_FACHPORTAL;
            case ATTR_AGS_ARTICLE_DEFAULT:
                return ATTR_AGS_ART_DEF_IMPORTED_BY_FACHPORTAL;
            case ATTR_AGS_VARIANT_DEFAULT:
                return ATTR_AGS_VAR_DEF_IMPORTED_BY_FACHPORTAL;
            default:
                throw new RestException(MSG_WRONG_TYPE + type);
        }
    }

    /**
     * Get attribute to insert in product while updating
     *
     * @param type of product
     * @return attribute identifier
     */
    private String getDateAttributeForUpdate(String type) {
        switch (type) {
            case ATTR_AGS_PRODUCT_DEFAULT:
                return ATTR_AGS_PRD_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME;
            case ATTR_AGS_ARTICLE_DEFAULT:
                return ATTR_AGS_ART_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME;
            case ATTR_AGS_VARIANT_DEFAULT:
                return ATTR_AGS_VAR_DEF_SYNCHRONIZED_BY_FACHPORTAL_DATETIME;
            default:
                return null;
        }
    }

    /**
     * Get attribute to insert in product while create
     *
     * @param type of product
     * @return attribute identifier
     */
    private String getDateAttributeForCreate(String type) {
        switch (type) {
            case ATTR_AGS_PRODUCT_DEFAULT:
                return ATTR_AGS_PRD_DEF_IMPORTED_BY_FACHPORTAL_DATETIME;
            case ATTR_AGS_ARTICLE_DEFAULT:
                return ATTR_AGS_ART_DEF_IMPORTED_BY_FACHPORTAL_DATETIME;
            case ATTR_AGS_VARIANT_DEFAULT:
                return ATTR_AGS_VAR_DEF_IMPORTED_BY_FACHPORTAL_DATETIME;
            default:
                return null;
        }
    }

    /**
     * Update product with id of existing product
     *
     * @param newProduct      new product
     * @param existingProductId existing id of product in base
     * @throws RestException
     */
    private void updateProduct(Product newProduct, Long existingProductId) throws RestException {
        newProduct.setId(existingProductId);
        try {
            apiService.updateProduct(newProduct);
            logger.info("Product updated. Id: {}", newProduct.getId());
        } catch (RestException e) {
            logger.error(ERROR_API_MSG, e.getMessage(), e.getHttpStatus());
            throw e;
        }
    }

    /**
     * Create new product
     *
     * @param newProduct new product for create
     * @return id of created product
     * @throws RestException
     */
    private Long createProduct(Product newProduct) throws RestException {
        Long newProductID;
        try {
            newProductID = apiService.createProduct(newProduct);
            if (newProductID != null) {
                logger.info("Product created. Id: {}", newProductID);
            }
        } catch (RestException e) {
            logger.error(ERROR_API_MSG, e.getMessage(), e.getHttpStatus());
            throw e;
        }
        return newProductID;
    }

    /**
     * Find product
     *
     * @param newProduct    product to get attribute value
     * @param attributeName name of attribute to search
     * @return found product
     * @throws RestException
     */
    Long searchProduct(Product newProduct, String attributeName) throws RestException {
        String fpIdValue = getAttributeValue(newProduct.getAttributes(), attributeName);
        Assert.notNull(fpIdValue, "FPID must not be null");
        Assert.isTrue(!fpIdValue.isEmpty(), "FPID must not be empty");
        try {
            Long[] products = apiService.searchProductsByFpID(newProduct.getType(), attributeName, fpIdValue);
            if (products.length > 1) {
                throw new RestException("More than one product was found. Duplicate product: "+fpIdValue);
            } else if (products.length > 0) {
                return products[0];
            } else
                return null;
        } catch (RestException e) {
            logger.debug("Error when search Product. Type: {},  Attribute: {}, Value: {} ", newProduct.getType(), attributeName,
                    fpIdValue);
            throw e;
        }
    }
}
