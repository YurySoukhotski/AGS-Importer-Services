package com.example.eximporter.importer.workflow.processor.product;

import com.example.eximporter.importer.model.extended.ExtendedProduct;
import com.example.eximporter.importer.model.xml.product.Style;
import com.example.eximporter.importer.service.converter.product.ProductConverter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Process {@link Style} to {@link ExtendedProduct}
 */
@Configuration
public class ProductProcessor implements ItemProcessor<Style, ExtendedProduct>
{
	@Autowired
	private ProductConverter productConverter;

	/**
	 * Process {@link Style} to {@link ExtendedProduct}
	 * @param style received style from xml
	 * @return ExtendedProduct with all articles and variants
	 * @throws Exception
	 */
	@Override
	public ExtendedProduct process(Style style) throws Exception
	{
		return productConverter.convertStyleToExtendedProduct(style);
	}
}
