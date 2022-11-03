package com.example.eximporter.importer.workflow.writer.product;

import com.example.eximporter.importer.model.api.Product;
import com.example.eximporter.importer.model.extended.ExtendedArticle;
import com.example.eximporter.importer.model.extended.ExtendedProduct;
import com.example.eximporter.importer.service.http.RestException;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.getAttributeValue;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.*;
import static com.example.eximporter.importer.helper.MessageBuilderHelper.*;
import static com.example.eximporter.importer.service.http.BaseApiService.DELIMITER;

/**
 * Service for batch write {@link ExtendedProduct}
 */
@Component("productWriter")
@StepScope
public class ProductWriter extends BaseProductWriter implements ItemWriter<ExtendedProduct>
{
	private static final String KEYWORD_DELIMITER = "->";
	private Long styleCounter = 0L;
	private Long articleCounter = 0L;
	private Long variantCounter = 0L;

	@BeforeStep
	private void resetStatistic()
	{
		this.styleCounter = 0L;
		this.articleCounter = 0L;
		this.variantCounter = 0L;
	}

	@AfterStep
	public void saveStatistic(StepExecution stepExecution)
	{
		stepExecution.getJobExecution().getExecutionContext().put(COUNTER_STYLES, new JobParameter(styleCounter));
		stepExecution.getJobExecution().getExecutionContext().put(COUNTER_ARTICLES, new JobParameter(articleCounter));
		stepExecution.getJobExecution().getExecutionContext().put(COUNTER_VARIANTS, new JobParameter(variantCounter));
	}

	/**
	 * Batch write operation with {@link ExtendedProduct}
	 * @param extendedProducts
	 *            received products for write
	 * @throws RestException
	 */
	@Override
	public void write(List<? extends ExtendedProduct> extendedProducts) throws RestException
	{
		for (ExtendedProduct extendedProduct : extendedProducts)
		{
			Product newProduct = extendedProduct.getProduct();
			String productFpIDValue = getAttributeValue(newProduct.getAttributes(), ATTR_AGS_PRD_DEF_FP_ID);
			StringBuilder identifierBuilder = new StringBuilder();
			identifierBuilder.append(PRD_IDENTIFIER).append(productFpIDValue);
			String keywordPrefix;
			newProduct.setType(ATTR_AGS_PRODUCT_DEFAULT);
			newProduct.setName(identifierBuilder.toString());
			newProduct.setPath(DELIMITER + identifierBuilder.toString());
			keywordPrefix = getAttributeValue(newProduct.getAttributes(), ATTR_AGS_PRD_DEF_PRODUCT_MAIN_GROUP) + KEYWORD_DELIMITER
				+ getAttributeValue(newProduct.getAttributes(), ATTR_AGS_PRD_DEF_PRODUCT_SUB_GROUP);
			newProduct.setKeywordPath("Produkte" + KEYWORD_DELIMITER + keywordPrefix);
			logger.info("Processing Product with FPID value: {}", productFpIDValue);
			Long existingProductId = searchProduct(newProduct, ATTR_AGS_PRD_DEF_FP_ID);
			Long newProductId;
			Boolean isNewProduct = false;
			if (existingProductId != null)
			{
				newProductId = processProductForUpdate(existingProductId, newProduct);
			}
			else
			{
				newProductId = processProductForCreate(newProduct, null);
				isNewProduct = true;
			}
			for (ExtendedArticle extendedArticle : extendedProduct.getExtendedArticles())
			{
				processExtendedArticle(extendedArticle, newProductId, identifierBuilder.toString(), keywordPrefix, isNewProduct);
			}
			styleCounter++;
		}
	}

	/**
	 * Create or update {@link ExtendedArticle}
	 * @param extendedArticle
	 *            article with variants
	 * @param parentProductId
	 *            id of parent product to linking
	 * @param productIdentifierPrefix
	 *            string prefix to create identifier of article
	 * @param keywordPrefix
	 *            prefix to link product
	 * @param isNewProduct
	 *            if received new product and must create new article and variant
	 */
	private void processExtendedArticle(ExtendedArticle extendedArticle, Long parentProductId, String productIdentifierPrefix,
		String keywordPrefix, Boolean isNewProduct) throws RestException
	{
		Product newArticle = extendedArticle.getArticle();
		String articleFpIDValue = getAttributeValue(newArticle.getAttributes(), ATTR_AGS_ART_DEF_FP_ID);
		StringBuilder identifierBuilder = new StringBuilder();
		identifierBuilder.append(productIdentifierPrefix).append(ART_IDENTIFIER).append(articleFpIDValue);
		newArticle.setType(ATTR_AGS_ARTICLE_DEFAULT);
		newArticle.setName(identifierBuilder.toString());
		newArticle.setKeywordPath("Artikel" + KEYWORD_DELIMITER + keywordPrefix);
		newArticle.setPath(DELIMITER + identifierBuilder.toString());
		logger.info("Processing Article with FPID: {}", articleFpIDValue);
		Long newArticleId;
		Boolean isNewArticle = false;
		if (isNewProduct)
		{
			newArticleId = processProductForCreate(newArticle, parentProductId);
			isNewArticle = true;
		}
		else
		{
			Long existingArticleId = searchProduct(newArticle, ATTR_AGS_ART_DEF_FP_ID);
			if (existingArticleId == null)
			{
				newArticleId = processProductForCreate(newArticle, parentProductId);
				isNewArticle = true;
			}
			else
			{
				newArticleId = processProductForUpdate(existingArticleId, newArticle);
			}
		}
		if (extendedArticle.getVariants() != null)
		{
			for (Product variant : extendedArticle.getVariants())
			{
				processVariant(variant, newArticleId, identifierBuilder.toString(), isNewArticle);
			}
		}
		articleCounter++;
	}

	/**
	 * Create or update variant
	 * @param variant
	 *            variant for create or update
	 * @param parentArticleId
	 *            id article to linking variant
	 * @param articleIdentifierPrefix
	 *            string prefix to create identifier of variant
	 * @param isNewArticle
	 * @throws RestException
	 */
	private void processVariant(Product variant, Long parentArticleId, String articleIdentifierPrefix, Boolean isNewArticle)
		throws RestException
	{
		String variantFpIDValue = getAttributeValue(variant.getAttributes(), ATTR_AGS_VAR_DEF_FP_ID);
		StringBuilder identifierBuilder = new StringBuilder();
		identifierBuilder.append(articleIdentifierPrefix).append(VAR_IDENTIFIER).append(variantFpIDValue);
		variant.setType(ATTR_AGS_VARIANT_DEFAULT);
		variant.setName(identifierBuilder.toString());
		variant.setPath(DELIMITER + identifierBuilder.toString());
		logger.info("Processing Variant with FPID: {}", variantFpIDValue);
		if (isNewArticle)
		{
			processProductForCreate(variant, parentArticleId);
		}
		else
		{
			Long existingVariantId = searchProduct(variant, ATTR_AGS_VAR_DEF_FP_ID);
			if (existingVariantId == null)
			{
				processProductForCreate(variant, parentArticleId);
			}
			else
			{
				processProductForUpdate(existingVariantId, variant);
			}
		}
		variantCounter++;
	}
}
