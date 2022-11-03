package com.example.eximporter.importer.configuration.folder.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.example.eximporter.importer.configuration.folder.FolderConfiguration;
import com.example.eximporter.importer.file.ImportType;

@Configuration
public class ProductFolderConfiguration implements FolderConfiguration
{
	@Value("${default.product.input.folder}")
	private String inputFolder;
	@Value("${default.product.success.folder}")
	private String successFolder;
	@Value("${default.product.error.folder}")
	private String errorFolder;
	@Value("${default.product.archive.folder}")
	private String archiveFolder;
	@Value("${default.product.temp.folder}")
	private String tempFolder;

	@Override
	public String getInputFolder()
	{
		return inputFolder;
	}

	@Override
	public void setInputFolder(String inputFolder)
	{
		this.inputFolder = inputFolder;
	}

	@Override
	public String getSuccessFolder()
	{
		return successFolder;
	}

	@Override
	public void setSuccessFolder(String successFolder)
	{
		this.successFolder = successFolder;
	}

	@Override
	public String getErrorFolder()
	{
		return errorFolder;
	}

	@Override
	public void setErrorFolder(String errorFolder)
	{
		this.errorFolder = errorFolder;
	}

	@Override
	public String getArchiveFolder()
	{
		return archiveFolder;
	}

	@Override
	public void setArchiveFolder(String archiveFolder)
	{
		this.archiveFolder = archiveFolder;
	}

	@Override
	public String getTempFolder()
	{
		return tempFolder;
	}

	@Override
	public void setTempFolder(String tempFolder)
	{
		this.tempFolder = tempFolder;
	}

	@Override
	public boolean isSupported(ImportType importType)
	{
		return ImportType.PRODUCT.equals(importType);
	}
}
