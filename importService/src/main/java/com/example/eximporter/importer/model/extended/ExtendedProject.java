package com.example.eximporter.importer.model.extended;

import com.example.eximporter.importer.model.api.Project;

import java.util.List;

/**
 * Class contains catalog project and information about season project and language projects
 */
public class ExtendedProject
{
	private String brand;
	private String season;
	private String expenses;
	private Project project;
	private List<Project> languageProjects;

	public ExtendedProject(String brand, String season, String expenses, Project project, List<Project> languageProjects)
	{
		this.brand = brand;
		this.season = season;
		this.expenses = expenses;
		this.project = project;
		this.languageProjects = languageProjects;
	}

	public String getBrand()
	{
		return brand;
	}

	public String getSeason()
	{
		return season;
	}

	public String getExpenses()
	{
		return expenses;
	}

	public Project getProject()
	{
		return project;
	}

	public List<Project> getLanguageProjects()
	{
		return languageProjects;
	}
}
