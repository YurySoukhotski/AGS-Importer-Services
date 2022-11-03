package com.example.eximporter.importer.model.extended;

import com.example.eximporter.importer.model.api.Product;

import java.util.List;

/**
 * Class contains article Product and linked variants
 */
public class ExtendedArticle
{
	private Product article;
	private List<Product> variants;

	public ExtendedArticle(Product article, List<Product> variants)
	{
		this.article = article;
		this.variants = variants;
	}

	public Product getArticle()
	{
		return article;
	}


	public List<Product> getVariants()
	{
		return variants;
	}


}
