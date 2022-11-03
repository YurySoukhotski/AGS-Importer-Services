package com.example.eximporter.importer.configuration.folder;

import com.example.eximporter.importer.file.ImportType;

public interface FolderConfiguration
{
	/**
	 * Gets input folder
	 * @return input folder
	 */
	String getInputFolder();

	/**
	 * Sets input folder
	 * @param value
	 *            input folder
	 */
	void setInputFolder(String value);

	/**
	 * Gets success folder
	 * @return success folder
	 */
	String getSuccessFolder();

	/**
	 * Sets success folder
	 * @param value
	 *            success folder
	 */
	void setSuccessFolder(String value);

	/**
	 * Gets error folder
	 * @return error folder
	 */
	String getErrorFolder();

	/**
	 * Sets error folder
	 * @param value
	 *            error folder
	 */
	void setErrorFolder(String value);

	/**
	 * Gets archive folder
	 * @return archive folder
	 */
	String getArchiveFolder();

	/**
	 * Sets archive folder
	 * @param value
	 *            archive folder
	 */
	void setArchiveFolder(String value);

	/**
	 * Gets temp folder
	 * @return temp folder
	 */
	String getTempFolder();

	/**
	 * Sets temp folder
	 * @param value
	 *            temp folder
	 */
	void setTempFolder(String value);

	/**
	 * Checks whether the configuration matches the type.
	 * @param importType
	 *            file type
	 * @return <code>true</code> if configuration is matched, otherwise <code>false</code>
	 */
	boolean isSupported(ImportType importType);
}
