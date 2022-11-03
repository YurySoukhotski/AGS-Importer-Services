package com.example.eximporter.importer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * API configuration
 */
@Configuration
public class ApiConfiguration
{
	@Value("${default.api.url}")
	private String apiUrl;
	@Value("${default.api.basic.auth}")
	private String apiBasicAuth;
	@Value("${default.api.product.path}")
	private String apiProductPath;
	@Value("${default.api.project.path}")
	private String apiProjectPath;
	@Value("${default.api.peo.path}")
	private String apiPeoPath;
	@Value("${default.auth.path}")
	private String authPath;

	/**
	 * Sets API url
	 * @param apiUrl
	 *            API URL
	 */
	public void setApiUrl(String apiUrl)
	{
		this.apiUrl = apiUrl;
	}

	/**
	 * Gets basic access authentication
	 * @return basic access authentication
	 */
	public String getApiBasicAuth()
	{
		return apiBasicAuth;
	}

	/**
	 * Sets basic authentication string
	 * @param apiBasicAuth
	 *            basic authentication string
	 */
	public void setApiBasicAuth(String apiBasicAuth)
	{
		this.apiBasicAuth = apiBasicAuth;
	}

	/**
	 * Sets product API path
	 * @param apiProductPath
	 *            product API path
	 */
	public void setApiProductPath(String apiProductPath)
	{
		this.apiProductPath = apiProductPath;
	}

	/**
	 * Sets project API path
	 * @param apiProjectPath
	 *            project API path
	 */
	public void setApiProjectPath(String apiProjectPath)
	{
		this.apiProjectPath = apiProjectPath;
	}

	/**
	 * Sets peo API path
	 * @param apiPeoPath
	 *            peo API path
	 */
	public void setApiPeoPath(String apiPeoPath)
	{
		this.apiPeoPath = apiPeoPath;
	}

	/**
	 * Gets URL for product API
	 * @return URL for product API
	 */
	public String getProductApiUrl()
	{
		return new StringBuilder().append(apiUrl).append(apiProductPath).toString();
	}

	/**
	 * Gets URL for project API
	 * @return URL for project API
	 */
	public String getProjectApiUrl()
	{
		return new StringBuilder().append(apiUrl).append(apiProjectPath).toString();
	}

	/**
	 * Gets URL for PEO API
	 * @return URL for PEO API
	 */
	public String getPeoApiUrl()
	{
		return new StringBuilder().append(apiUrl).append(apiPeoPath).toString();
	}

	/**
	 * Gets URL for Auth API
	 * @return URL for Auth API
	 */
    public String getAuthPath() {
        return new StringBuilder().append(apiUrl).append(authPath).toString();
    }
}
