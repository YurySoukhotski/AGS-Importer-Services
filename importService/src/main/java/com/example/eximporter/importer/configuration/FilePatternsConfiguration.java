package com.example.eximporter.importer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilePatternsConfiguration
{
	@Value("${default.project.file.pattern}")
	private String projectFilePattern;
	@Value("${default.product.file.pattern}")
	private String productFilePattern;
	@Value("${default.peo.file.pattern}")
	private String peoFilePattern;
	@Value("${default.page.file.pattern}")
	private String pageFilePattern;

	/**
	 * Gets project file pattern
	 * @return project file pattern
	 */
	public String getProjectFilePattern()
	{
		return projectFilePattern;
	}

	/**
	 * Sets project file pattern
	 * @param projectFilePattern
	 *            project file pattern
	 */
	public void setProjectFilePattern(String projectFilePattern)
	{
		this.projectFilePattern = projectFilePattern;
	}

	/**
	 * Gets project file pattern
	 * @return project file pattern
	 */
	public String getProductFilePattern()
	{
		return productFilePattern;
	}

	/**
	 * Sets project file pattern
	 * @param productFilePattern
	 *            project file pattern
	 */
	public void setProductFilePattern(String productFilePattern)
	{
		this.productFilePattern = productFilePattern;
	}

	/**
	 * Gets peo file pattern
	 * @return peo file pattern
	 */
	public String getPeoFilePattern()
	{
		return peoFilePattern;
	}

	/**
	 * Sets peo file pattern
	 * @param peoFilePattern
	 *            peo file pattern
	 */
	public void setPeoFilePattern(String peoFilePattern)
	{
		this.peoFilePattern = peoFilePattern;
	}

	/**
	 * Gets page file pattern
	 * @return page file pattern
	 */
	public String getPageFilePattern()
	{
		return pageFilePattern;
	}

	/**
	 * Sets page file pattern
	 * @param pageFilePattern
	 *            page file pattern
	 */
	public void setPageFilePattern(String pageFilePattern)
	{
		this.pageFilePattern = pageFilePattern;
	}
}
