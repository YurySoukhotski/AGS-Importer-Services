package com.example.eximporter.importer.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of FTP
 */
@Configuration
public class FtpConfiguration
{
	@Value("${default.ftp.host}")
	private String host;
	@Value("${default.ftp.port}")
	private String port;
	@Value("${default.ftp.user.name}")
	private String userName;
	@Value("${default.ftp.user.password}")
	private String userPassword;
	@Value("${default.ftp.project.source.folder}")
	private String projectSourceFolder;
	@Value("${default.ftp.page.source.folder}")
	private String pageSourceFolder;
	@Value("${default.ftp.product.source.folder}")
	private String productSourceFolder;
	@Value("${default.ftp.peo.source.folder}")
	private String peoSourceFolder;
	@Value("${default.ftp.error.folder}")
	private String errorFolder;

	/**
	 * Gets set of source folders on FTP
	 * @return set of source folders
	 */
	public Set<String> getSourceFolders()
	{
		return new HashSet<>(Arrays.asList(projectSourceFolder, pageSourceFolder, productSourceFolder, peoSourceFolder));
	}

	/**
	 * Gets FTP host
	 * @return FTP host
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Sets FTP host
	 * @param host
	 *            FTP host
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * Gets FTP port
	 * @return FTP port
	 */
	public String getPort()
	{
		return port;
	}

	/**
	 * Sets FTP port
	 * @param port
	 *            FTP port
	 */
	public void setPort(String port)
	{
		this.port = port;
	}

	/**
	 * Gets user name
	 * @return user name
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets user name
	 * @param userName
	 *            user name
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets user password
	 * @return user password
	 */
	public String getUserPassword()
	{
		return userPassword;
	}

	/**
	 * Sets user password
	 * @param userPassword
	 *            user password
	 */
	public void setUserPassword(String userPassword)
	{
		this.userPassword = userPassword;
	}

	/**
	 * Gets project source folder
	 * @return project source folder
	 */
	public String getProjectSourceFolder()
	{
		return projectSourceFolder;
	}

	/**
	 * Sets project source folder
	 * @param projectSourceFolder
	 *            project source folder
	 */
	public void setProjectSourceFolder(String projectSourceFolder)
	{
		this.projectSourceFolder = projectSourceFolder;
	}

	/**
	 * Gets page source folder
	 * @return page source folder
	 */
	public String getPageSourceFolder()
	{
		return pageSourceFolder;
	}

	/**
	 * Sets page source folder
	 * @param pageSourceFolder
	 *            page source folder
	 */
	public void setPageSourceFolder(String pageSourceFolder)
	{
		this.pageSourceFolder = pageSourceFolder;
	}

	/**
	 * Gets product source folder
	 * @return product source folder
	 */
	public String getProductSourceFolder()
	{
		return productSourceFolder;
	}

	/**
	 * Sets product source folder
	 * @param productSourceFolder
	 *            product source folder
	 */
	public void setProductSourceFolder(String productSourceFolder)
	{
		this.productSourceFolder = productSourceFolder;
	}

	/**
	 * Gets PEO source folder
	 * @return PEO source folder
	 */
	public String getPeoSourceFolder()
	{
		return peoSourceFolder;
	}

	/**
	 * Sets PEO source folder
	 * @param peoSourceFolder
	 *            PEO source folder
	 */
	public void setPeoSourceFolder(String peoSourceFolder)
	{
		this.peoSourceFolder = peoSourceFolder;
	}

	/**
	 * Gets error folder
	 * @return error folder
	 */
	public String getErrorFolder()
	{
		return errorFolder;
	}

	/**
	 * Sets error folder
	 * @param errorFolder
	 *            error folder
	 */
	public void setErrorFolder(String errorFolder)
	{
		this.errorFolder = errorFolder;
	}
}
