package com.example.eximporter.importer.model.extended;

import com.example.eximporter.importer.model.api.Project;

/**
 * Class contains Page project and information about linking
 */
public class ExtendedPage
{
	private Long id;
	private Project page;
	private String languageProjectKey;
	private Long attempt;
	private String fileName;

	public ExtendedPage(Project page, String languageProjectKey, Long attempt, Long id, String fileName)
	{
		this.page = page;
		this.languageProjectKey = languageProjectKey;
		this.attempt = attempt;
		this.id = id;
		this.fileName=fileName;
	}

	public Project getPage()
	{
		return page;
	}

	public String getLanguageProjectKey()
	{
		return languageProjectKey;
	}

	public Long getAttempt()
	{
		return attempt;
	}

	public Long getId()
	{
		return id;
	}

	public void setAttempt(Long attempt)
	{
		this.attempt = attempt;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
}
