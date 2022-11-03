package com.example.eximporter.importer.service.http;

import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.example.eximporter.importer.model.api.Project;

/**
 * Service to work with Project REST api
 */
@Service
public class ProjectApiService extends BaseApiService
{
	private static final Logger log = LogManager.getLogger(ProjectApiService.class);
	private static final String ERROR_MSG = "Server error";
	/**
	 * Create new Project and return id
	 * @param newProject
	 *            project for create
	 * @return id of created project
	 * @throws RestException
	 */
	public Long createProject(Project newProject) throws RestException
	{
		ResponseEntity responseEntity;
		Long newProjectId;
		try
		{
			responseEntity = doPost(Project.class, getEndPointURL(), newProject);
			String[] splitURI = responseEntity.getHeaders().getLocation().toString().split(getEndPointURL(DELIMITER));
			if (splitURI.length == 2)
			{
				newProjectId = Long.valueOf(splitURI[1]);
			}
			else
			{
				log.debug("Sent URL: " + Arrays.toString(splitURI));
				throw new RestException("ID of created object is wrong");
			}
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_MSG, e);
		}
		return newProjectId;
	}

	/**
	 * Update some project
	 * @param newProject
	 *            project for update
	 * @throws RestException
	 */
	public void updateProject(Project newProject) throws RestException
	{
		try
		{
			doPatch(Project.class, getEndPointURL(DELIMITER, newProject.getId().toString()), newProject);
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_MSG, e);
		}
	}

	/**
	 * Delete project by id
	 * @param id
	 *            of the project to be removed
	 * @throws RestException
	 */
	public void deleteProject(Long id) throws RestException
	{
		try
		{
			doDelete(Project.class, getEndPointURL(DELIMITER, id.toString()));
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_MSG, e);
		}
	}

	/**
	 * Find project by id
	 * @param id
	 *            of project
	 * @return searched project
	 * @throws RestException
	 */
	public Project getProjectById(Long id) throws RestException
	{
		try
		{
			ResponseEntity responseEntity = doGet(Project.class, getEndPointURL(DELIMITER, id.toString()));
			return (Project) responseEntity.getBody();
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_MSG, e);
		}
	}

	/**
	 * Search any {@link Project} by project type and FPID attribute with it value
	 * @param projectType
	 *            configured type name oа project
	 * @param fpIDAttribute
	 *            name of FPID attribute
	 * @param fpIDValue
	 *            value of FPID attribute
	 * @return found projects
	 * @throws RestException
	 */
	public Long[] searchProjectsByFpID(String projectType, String fpIDAttribute, String fpIDValue) throws RestException
	{
		String searchURL = getEndPointURL(QUERY_TEMPLATE, projectType, AND, fpIDAttribute, EQ, fpIDValue);
		try
		{
			ResponseEntity responseEntity = doSearch(searchURL, Long[].class);
			return (Long[]) responseEntity.getBody();
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_MSG, e);
		}
	}

	@Override
	protected String getUrl()
	{
		return apiConfiguration.getProjectApiUrl();
	}
}
