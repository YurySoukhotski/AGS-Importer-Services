package com.example.eximporter.importer.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class UnzipHelper
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UnzipHelper.class);

	public void unzipFile(Path zipFile, Path outputDir) throws IOException
	{
		Assert.notNull(zipFile, "Zip file must not be null");
		Assert.notNull(outputDir, "Output folder must not be null");
		LOGGER.info("Unzip file {} to {}", zipFile, outputDir);
		try (FileInputStream in = new FileInputStream(zipFile.toFile()))
		{
			unzipStream(in, outputDir.toFile());
		}
		catch (IOException e)
		{
			throw new IOException("Error occur while unzipping " + zipFile, e);
		}
	}

	private void unzipStream(InputStream stream, File outputDir) throws IOException
	{
		ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(stream));
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		if (zipEntry == null)
		{
			throw new IOException("Invalid Zip File Format");
		}
		do
		{
			File outFile = new File(outputDir, zipEntry.getName());
			if (zipEntry.isDirectory())
			{
				Files.createDirectories(outFile.toPath());
			}
			else
			{
				if (outFile.exists())
				{
					Files.delete(outFile.toPath());
				}
				else
				{
					Files.createDirectories(outFile.getParentFile().toPath());
				}
				unzip(zipInputStream, outFile);
				if (!outFile.setLastModified(zipEntry.getTime()))
				{
					LOGGER.debug("Modification date isn't set to {}", outFile);
				}
			}
			zipInputStream.closeEntry();
		}
		while (( zipEntry = zipInputStream.getNextEntry() ) != null);
		zipInputStream.close();
	}

	private void unzip(ZipInputStream zipInputStream, File outFile) throws IOException
	{
		try
		{
			try (FileOutputStream fileOutputStream = new FileOutputStream(outFile))
			{
				IOUtils.copy(zipInputStream, fileOutputStream);
			}
		}
		catch (FileNotFoundException e)
		{
			throw new IOException("File not found: " + outFile, e);
		}
	}
}
