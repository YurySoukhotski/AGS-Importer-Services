package com.example.eximporter.importer.file.object;

import com.example.eximporter.importer.file.ImportType;
import org.apache.commons.net.ftp.FTPFile;

public class FTPFileInfo
{
	private String ftpFolder;
	private FTPFile ftpFile;
	private ImportType importType;

	public FTPFileInfo(String ftpFolder, FTPFile ftpFile, ImportType importType)
	{
		this.ftpFolder = ftpFolder;
		this.ftpFile = ftpFile;
		this.importType = importType;
	}

	public String getFtpFolder()
	{
		return ftpFolder;
	}

	public FTPFile getFtpFile()
	{
		return ftpFile;
	}

	public ImportType getImportType()
	{
		return importType;
	}
}
