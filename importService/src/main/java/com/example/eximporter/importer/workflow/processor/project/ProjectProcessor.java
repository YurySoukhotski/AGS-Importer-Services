package com.example.eximporter.importer.workflow.processor.project;

import com.example.eximporter.importer.model.xml.project.Project;
import com.example.eximporter.importer.service.converter.project.ProjectConverter;
import com.example.eximporter.importer.model.extended.ExtendedProject;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Process from Xml project type to {@link ExtendedProject}
 */
@Configuration
public class ProjectProcessor
	implements ItemProcessor<Project, ExtendedProject>
{
	@Autowired
	private ProjectConverter projectConverter;

	/**
	 * Process Xml project to extended Project type
	 * @param projectType
	 *            received xml model of project
	 * @return Project with all information about season and languages
	 * @throws Exception
	 */
	@Override
	public ExtendedProject process(Project projectType) throws Exception
	{
		String type = projectType.getProjectType();
		com.example.eximporter.importer.model.api.Project project = projectConverter.convertXmlToProject(projectType);
		project.setType(type);
		List<com.example.eximporter.importer.model.api.Project> languageProjects = projectConverter.extractLanguageProjects(project);
		return new ExtendedProject(projectType.getMarke(), projectType.getSaison(), projectType.getExpenses(), project,
			languageProjects);
	}
}
