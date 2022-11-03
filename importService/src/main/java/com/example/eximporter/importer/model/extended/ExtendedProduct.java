package com.example.eximporter.importer.model.extended;

import com.example.eximporter.importer.model.api.Product;

import java.util.List;

/**
 * Class contains product and linked articles
 */
public class ExtendedProduct
{
	private Product product;
	private List<ExtendedArticle> extendedArticles;

	public ExtendedProduct(Product product, List<ExtendedArticle> extendedArticles)
	{
		this.product = product;
		this.extendedArticles = extendedArticles;
	}

	public Product getProduct()
	{
		return product;
	}

	public List<ExtendedArticle> getExtendedArticles()
	{
		return extendedArticles;
	}
}
