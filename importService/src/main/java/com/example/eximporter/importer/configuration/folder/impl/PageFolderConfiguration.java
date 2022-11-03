package com.example.eximporter.importer.configuration.folder.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.example.eximporter.importer.configuration.folder.FolderConfiguration;
import com.example.eximporter.importer.file.ImportType;

@Configuration
public class PageFolderConfiguration implements FolderConfiguration
{
	@Value("${default.page.input.folder}")
	private String inputFolder;
	@Value("${default.page.success.folder}")
	private String successFolder;
	@Value("${default.page.error.folder}")
	private String errorFolder;
	@Value("${default.page.archive.folder}")
	private String archiveFolder;
	@Value("${default.page.temp.folder}")
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
		return ImportType.PAGE.equals(importType);
	}
}
