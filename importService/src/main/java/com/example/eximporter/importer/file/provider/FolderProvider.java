package com.example.eximporter.importer.file.provider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.example.eximporter.importer.configuration.folder.FolderConfiguration;
import com.example.eximporter.importer.file.ImportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.eximporter.importer.file.FolderType;

@Component
public class FolderProvider
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FolderProvider.class);
	@Autowired
	private List<FolderConfiguration> configurations;
	
	public List<Path> getFoldersByType(FolderType folderType)
	{
		return configurations.stream()
			.map(configuration -> getFolder(folderType, configuration))
			.collect(Collectors.toList());
	}

	public Path getFolder(ImportType importType, FolderType folderType)
	{
		FolderConfiguration configuration = getConfiguration(importType);
		if (configuration == null)
		{
			LOGGER.info("Configuration of folders is missing for type {}", importType);
			return null;
		}
		return getFolder(folderType, configuration);
	}

	private Path getFolder(FolderType folderType, FolderConfiguration configuration)
	{
		switch (folderType)
		{
			case INPUT:
				return Paths.get(configuration.getInputFolder());
			case SUCCESS:
				return Paths.get(configuration.getSuccessFolder());
			case ERROR:
				return Paths.get(configuration.getErrorFolder());
			case ARCHIVE:
				return Paths.get(configuration.getArchiveFolder());
			case TEMP:
				return Paths.get(configuration.getTempFolder());
			default:
				return null;
		}
	}

	private FolderConfiguration getConfiguration(ImportType importType)
	{
		return configurations.stream()
			.filter(configuration -> configuration.isSupported(importType))
			.findFirst()
			.orElse(null);
	}
}
