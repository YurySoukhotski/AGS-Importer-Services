package com.example.eximporter.importer.file.manager;

import com.example.eximporter.importer.file.ImportType;
import com.example.eximporter.importer.helper.ImportPropertyHelper;
import com.example.eximporter.importer.helper.UnzipHelper;
import com.example.eximporter.importer.service.js.JSParameter;
import com.example.eximporter.importer.service.js.checkin.CheckinService;
import com.example.eximporter.importer.configuration.FilePatternsConfiguration;
import com.example.eximporter.importer.file.FolderType;
import com.example.eximporter.importer.file.object.FTPFileInfo;
import com.example.eximporter.importer.file.object.FileInfo;
import com.example.eximporter.importer.file.provider.FolderProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportPropertyHelper.class);
	@Autowired
	private FilePatternsConfiguration filePatternsConfiguration;
	@Autowired
	private FolderProvider folderProvider;
	@Autowired
	private UnzipHelper unzipHelper;
	@Autowired
	private CheckinService checkinService;

	/**
	 * Converts list of {@link FTPFile} objects to list of {@link FTPFileInfo} objects
	 * @param ftpFolder
	 *            ftp folder
	 * @param ftpFiles
	 *            ftp file
	 * @return {@link FTPFileInfo} object
	 */
	public List<FTPFileInfo> convert(String ftpFolder, List<FTPFile> ftpFiles)
	{
		if (CollectionUtils.isEmpty(ftpFiles))
			return Collections.emptyList();
		return ftpFiles.stream().map(ftpFile -> convert(ftpFolder, ftpFile)).collect(Collectors.toList());
	}

	/**
	 * Converts {@link FTPFile} to {@link FTPFileInfo}
	 * @param ftpFolder
	 *            ftp folder
	 * @param ftpFile
	 *            ftp file
	 * @return {@link FTPFileInfo} object
	 */
	public FTPFileInfo convert(String ftpFolder, FTPFile ftpFile)
	{
		ImportType importType = getImportType(ftpFile.getName());
		return new FTPFileInfo(ftpFolder, ftpFile, importType);
	}

	/**
	 * Converts {@link Path} to {@link FileInfo}
	 * @param path
	 *            {@link Path} object
	 * @return {@link FileInfo} object
	 */
	private FileInfo convert(Path path)
	{
		ImportType importType = getImportType(path.toString());
		return new FileInfo(path, importType);
	}

	/**
	 * Checks the existence and returns {@link Path} object for folder by type of import and type of folder. If the folder does
	 * not exist, creates it.
	 * @param importType
	 *            import type
	 * @param folderType
	 *            folder type
	 * @return {@link Path} object for folder
	 */
	public Path getFolder(ImportType importType, FolderType folderType)
	{
		Path folder = folderProvider.getFolder(importType, folderType);
		Assert.notNull(folder, "Can't determine folder");
		return checkAndCreate(folder);
	}

	private Path checkAndCreate(Path path)
	{
		if (path.toFile().exists())
			return path;
		LOGGER.info("Local directory {} doesn't exist", path);
		try
		{
			Files.createDirectories(path);
		}
		catch (IOException e)
		{
			LOGGER.error("Can't create {} folder", path, e);
		}
		LOGGER.info("Local directory {} created", path);
		return path;
	}

	public void unzipFiles()
	{
		getFiles(FolderType.TEMP).stream().forEach(zipFile -> {
			try
			{
				unzipHelper.unzipFile(zipFile.getPath(), getFolder(zipFile.getImportType(), FolderType.INPUT));
				Files.delete(zipFile.getPath());
			}
			catch (IOException e)
			{
				LOGGER.error("Can't unzip file {}", zipFile.getPath(), e);
			}
		});
	}

	public Map<ImportType, List<FileInfo>> getFilesMap(FolderType folderType)
	{
		List<FileInfo> files = getFiles(folderType);
		return files.stream().collect(Collectors.groupingBy(FileInfo::getImportType));
	}

	/**
	 * Gets files from folders by folder type
	 * @param folderType
	 *            type of folder
	 * @return list of files
	 */
	public List<FileInfo> getFiles(FolderType folderType)
	{
		List<Path> folders = folderProvider.getFoldersByType(folderType);
		return folders.stream()
				.map(this::checkAndCreate)
				.map(this::getFiles)
				.flatMap(List::stream)
				.sorted((e1, e2) -> e1.getOrder().compareTo(e2.getOrder()))
				.collect(Collectors.toList());
	}
	/**
	 * Gets files from folder
	 * @param folder
	 *            {@link Path} object for folder
	 * @return list of files
	 */
	private List<FileInfo> getFiles(Path folder)
	{
		try (Stream<Path> files = Files.list(folder))
		{
			return files.filter(Files::isRegularFile).map(this::convert).collect(Collectors.toList());
		}
		catch (IOException e)
		{
			LOGGER.error("Problem when trying to get a list of files from income directory", e);
		}
		return Collections.emptyList();
	}

	/**
	 * Moves file to folder by type
	 * @param file
	 *            source file path
	 * @param folderType
	 *            folder type
	 */
	public void moveFile(String file, FolderType folderType, boolean isCheckin)
	{
		ImportType importType = getImportType(file);
		Path destinationFolder = getFolder(importType, folderType);
		Path sourceFile = Paths.get(file);
		try
		{
			Path destinationPath = destinationFolder.resolve(sourceFile.getFileName());
			Files.move(sourceFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			if (isCheckin)
			{
				Map<JSParameter, String> parameters = new EnumMap<>(JSParameter.class);
				parameters.put(JSParameter.FILE_PATH, destinationPath.toString());
				checkinService.call(parameters);
			}
			LOGGER.info("File {} is moved successfully to {} ", sourceFile, destinationFolder);
		}
		catch (IOException e)
		{
			LOGGER.error("File {} isn't moved to {} ", sourceFile, destinationFolder, e);
		}
	}

	/**
	 * Return {@link ImportType} by file name or file path
	 * @param value
	 *            file name or file path
	 * @return type of file
	 */
	private ImportType getImportType(String value)
	{
		Assert.notNull(value, "File name must not be null");
		if (value.matches(filePatternsConfiguration.getProductFilePattern()))
			return ImportType.PRODUCT;
		if (value.matches(filePatternsConfiguration.getProjectFilePattern()))
			return ImportType.PROJECT;
		if (value.matches(filePatternsConfiguration.getPeoFilePattern()))
			return ImportType.PEO;
		if (value.matches(filePatternsConfiguration.getPageFilePattern()))
			return ImportType.PAGE;
		return ImportType.UNDEFINED;
	}
}
