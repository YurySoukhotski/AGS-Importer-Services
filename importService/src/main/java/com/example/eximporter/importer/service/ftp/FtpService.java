package com.example.eximporter.importer.service.ftp;

import com.example.eximporter.importer.configuration.FtpConfiguration;
import com.example.eximporter.importer.file.FolderType;
import com.example.eximporter.importer.file.ImportType;
import com.example.eximporter.importer.file.manager.FileManager;
import com.example.eximporter.importer.file.object.FTPFileInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for retrieving files from FTP
 */
@Service
public class FtpService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpService.class);
	private static final String FTP_FILE_SEPARATOR = "/";
	@Autowired
	private FtpConfiguration ftpConfiguration;
	@Autowired
	private FileManager fileManager;

	/**
	 * Connect to FTP and copy all files to local temp folders
	 */
	public void copyFilesFromFtp()
	{
		FTPClient ftpClient = new FTPClient();
		ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
		ftpClient.enterLocalPassiveMode();
		try
		{
			ftpClient.connect(ftpConfiguration.getHost());
			ftpClient.login(ftpConfiguration.getUserName(), ftpConfiguration.getUserPassword());
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// collect files from configured ftp folders. The source folder can be one and contain all files
			List<FTPFileInfo> ftpFileInfos = ftpConfiguration.getSourceFolders().stream().filter(Objects::nonNull)
				.map(folder -> getFiles(folder, ftpClient)).flatMap(List::stream).collect(Collectors.toList());
			copyFiles(ftpFileInfos, ftpClient);
		}
		catch (IOException e)
		{
			LOGGER.error("Files haven't found", e);
		}
		finally
		{
			try
			{
				if (ftpClient.isConnected())
				{
					ftpClient.logout();
					ftpClient.disconnect();
				}
			}
			catch (IOException e)
			{
				LOGGER.error("Files haven't found", e);
			}
		}
	}

	/**
	 * Gets list of ftp files from folder
	 * @param folderPath
	 *            folder to scan
	 * @param ftpClient
	 *            ftp client
	 * @return list of {@link FTPFileInfo} objects
	 */
	private List<FTPFileInfo> getFiles(String folderPath, FTPClient ftpClient)
	{
		Assert.notNull(folderPath, "Folder path must not be null");
		try
		{
			FTPFile[] files = ftpClient.listFiles(folderPath);
			return fileManager.convert(folderPath, Arrays.asList(files));
		}
		catch (IOException e)
		{
			LOGGER.error("Can't read list of files from {} ", folderPath, e);
		}
		return Collections.emptyList();
	}

	/**
	 * Copies files from FTP to temp folders
	 * @param ftpFileInfos
	 *            list of {@link FTPFileInfo} objects
	 * @param ftpClient
	 *            ftp client
	 * @throws IOException
	 *             if an I/O error occurs or the parent directory does not exist
	 */
	private void copyFiles(List<FTPFileInfo> ftpFileInfos, FTPClient ftpClient)
	{
		if (CollectionUtils.isEmpty(ftpFileInfos))
			return;
		ftpFileInfos.stream().forEach(file -> copyFile(file, ftpClient));
	}

	/**
	 * Copies ftp file to local temp folder
	 * @param ftpFileInfo
	 *            {@link FTPFileInfo} object
	 * @param ftpClient
	 *            ftp client
	 * @throws IOException
	 */
	private void copyFile(FTPFileInfo ftpFileInfo, FTPClient ftpClient)
	{
		String ftpFileName = ftpFileInfo.getFtpFile().getName();
		String ftpFilePath = ftpFileInfo.getFtpFolder() + FTP_FILE_SEPARATOR + ftpFileName;
		if (!ftpFileInfo.getFtpFile().isFile())
		{
			LOGGER.debug("FTP-file {} is skipped. Isn't a regular file", ftpFilePath);
			return;
		}
		if (ImportType.UNDEFINED.equals(ftpFileInfo.getImportType()))
		{
			LOGGER.info("Name of file {} doesn't match any of the configured file patterns", ftpFilePath);
			moveFileToError(ftpClient, ftpFileInfo);
			return;
		}
		LOGGER.info("Copy file : {}", ftpFileName);
		Path tempFolder = fileManager.getFolder(ftpFileInfo.getImportType(), FolderType.TEMP);
		Path localFile = tempFolder.resolve(ftpFileName);
		reloadFileFromFtp(localFile, ftpFilePath, ftpFileInfo);

	}

	/**
	 * Create new FTPClient for every file and download it
	 * @param localFile
	 * @param ftpFilePath
	 * @param ftpFileInfo
	 */
	private void reloadFileFromFtp(Path localFile, String ftpFilePath, FTPFileInfo ftpFileInfo){
		FTPClient ftpClientForCopy = new FTPClient();
		ftpClientForCopy.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
		ftpClientForCopy.enterLocalPassiveMode();
		try {
			ftpClientForCopy.connect(ftpConfiguration.getHost());
			ftpClientForCopy.login(ftpConfiguration.getUserName(), ftpConfiguration.getUserPassword());
			ftpClientForCopy.setFileType(FTP.BINARY_FILE_TYPE);
			createLocalFile(ftpClientForCopy, localFile, ftpFilePath, ftpFileInfo);
		}
		catch (Exception e)
		{
			LOGGER.error("Error while copy", e);
		}
		finally
		{
			try
			{
				if (ftpClientForCopy.isConnected())
				{
					ftpClientForCopy.logout();
					ftpClientForCopy.disconnect();
				}
			}
			catch (IOException e)
			{
				LOGGER.error("Error while copy", e);
			}
		}
	}

	/**
	 * Get FTPClient and copy file into local storage
	 * @param ftpClientForCopy
	 * @param localFile
	 * @param ftpFilePath
	 * @param ftpFileInfo
	 */
	private void createLocalFile(FTPClient ftpClientForCopy, Path localFile, String ftpFilePath, FTPFileInfo ftpFileInfo){
		try (FileOutputStream fileOutputStream = new FileOutputStream(localFile.toFile());
			 OutputStream outputStream = new BufferedOutputStream(fileOutputStream)) {
			if (ftpClientForCopy.retrieveFile(ftpFilePath, outputStream)) {
				LOGGER.info("File {} was successfully downloaded", ftpFilePath);
				deleteFile(ftpFileInfo, ftpClientForCopy);
			}
		} catch (IOException e) {
			LOGGER.error("File isn't copied", e);
		}
	}
	/**
	 * Moves file to error folder
	 * @param ftpClient
	 *            ftp client
	 * @param ftpFileInfo
	 *            {@link FTPFileInfo} object
	 * @throws IOException
	 */
	private void moveFileToError(FTPClient ftpClient, FTPFileInfo ftpFileInfo)
	{
		try
		{
			if (!ftpClient.changeWorkingDirectory(ftpConfiguration.getErrorFolder()))
			{
				ftpClient.makeDirectory(ftpConfiguration.getErrorFolder());
			}
			String ftpFileName = ftpFileInfo.getFtpFile().getName();
			String ftpFilePath = ftpFileInfo.getFtpFolder() + FTP_FILE_SEPARATOR + ftpFileName;
			String ftpErrorFilePath = ftpConfiguration.getErrorFolder() + FTP_FILE_SEPARATOR + ftpFileName;
			LOGGER.info("Move file {} to error folder", ftpFilePath);
			ftpClient.rename(ftpFilePath, ftpErrorFilePath);
		}
		catch (IOException e)
		{
			LOGGER.error("Can't move file to e", e);
		}
	}

	/**
	 * Deletes file from ftp
	 * @param ftpFileInfo
	 *            {@link FTPFileInfo} object
	 * @param ftpClient
	 *            ftp client
	 * @throws IOException
	 *             If an I/O error occurs while either sending a command to the server or receiving a reply from the server.
	 */
	private void deleteFile(FTPFileInfo ftpFileInfo, FTPClient ftpClient)
	{
		String fileName = ftpFileInfo.getFtpFile().getName();
		LOGGER.info("Delete file {} from {} folder on FTP", fileName, ftpFileInfo.getFtpFolder());
		try
		{
			ftpClient.changeWorkingDirectory(ftpFileInfo.getFtpFolder());
			boolean isDeleted = ftpClient.deleteFile(fileName);
			LOGGER.info(isDeleted ? "File {} was successfully deleted" : "File {} was not deleted", fileName);
		}
		catch (IOException e)
		{
			LOGGER.error("The file {} from {} folder can't be deleted", fileName, ftpFileInfo.getFtpFolder(), e);
		}
	}
}
