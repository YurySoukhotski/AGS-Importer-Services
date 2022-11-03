package com.example.eximporter.importer.service.http;

import com.example.eximporter.importer.configuration.CommonConfiguration;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.model.api.Box;
import com.example.eximporter.importer.model.api.Peo;
import com.example.eximporter.importer.model.api.ProjectLink;
import com.example.eximporter.importer.model.api.ProjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Interacts with api to perform operations with peo.
 */
@Service
public class PeoApiService extends BaseApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeoApiService.class);
    private static final String PRODUCT_QUERY_PREFIX = "Product.";
    private static final String PROJECT_QUERY_PREFIX = "Project.";
    private static final String SEARCH_PEO_BY_ID_QUERY = "?query=Type=%s AND %s=%s";
    private static final String SEARCH_PEO_QUERY = SEARCH_PEO_BY_ID_QUERY + " AND %s=%s";
    private static final String CREATE_PROJECT_LINK_PATH = "/%s/projectlink";
    private static final String PEO_UPDATE_PATH = "/%s";
    private static final String DELETE_PROJECT_LINK_PATH = CREATE_PROJECT_LINK_PATH + "/%s";
    private static final String GET_PROJECT_LINKS_PATH = "/%s/projectlinks";
    private static final String GET_BRIEFING_LINKS_PATH = "/%s/briefinglinks";
    private static final char PATH_SPLITTER = '/';

    @Autowired
    private CommonConfiguration commonConfiguration;

    /**
     * Creates peo by sending product export option with attributes that will be created.
     *
     * @param peo that will be created
     */
    public Long create(Peo peo, List<Long> duplicatedPeoIds) throws RestException {
        LOGGER.info("Try to create peo with name={}", peo.getName());
        try {
            ResponseEntity response = doPost(Peo.class, getEndPointURL(), peo);
            Long createdPeoId = extractCreatedPeoId(response);
            LOGGER.info("Peo has been created with id={}", createdPeoId);
            return createdPeoId;
        } catch (HttpClientErrorException e) {
            HttpStatus httpStatus = e.getStatusCode();
            if (httpStatus != null && httpStatus.value() == 409) {
                String path = e.getResponseHeaders().getLocation().getPath();
                String stringId = path.substring(path.lastIndexOf(PATH_SPLITTER) + 1);
                if (!stringId.isEmpty()) {
                    duplicatedPeoIds.add(Long.valueOf(stringId));
                }
                LOGGER.error("HTTP CODE:{} . Duplicate PEO name: {} Duplicate peo:{}", httpStatus.value(), peo.getName(), stringId);
                return null;
            }
            LOGGER.error("Can't create peo with name={}", peo.getName(), e);
            throw new RestException("Api returns error during creating peo", e);
        } catch (RestClientException e) {
            LOGGER.error("Can't create peo with name={}", peo.getName(), e);
            throw new RestException("Api returns error during creating peo", e);
        }
    }

    /**
     * Updates peo by sending product export option with attributes that will be updated.
     *
     * @param foundPeoId existing peo id
     * @param newPeo     peo data to update
     * @throws RestException api returns error
     */
    public void update(Long foundPeoId, Peo newPeo) throws RestException {
        LOGGER.info("Try to update peo with id={}", foundPeoId);
        try {
            String updatePath = String.format(PEO_UPDATE_PATH, foundPeoId);
            doPatch(Peo.class, getEndPointURL(updatePath), newPeo);
            LOGGER.info("Peo with id={} has been updated", foundPeoId);
        } catch (RestClientException e) {
            LOGGER.error("Can't update peo with id={}", foundPeoId, e);
            throw new RestException("Api returns error during updating peo with id = " + foundPeoId, e);
        }
    }

    /**
     * Gets peo by id
     *
     * @param peoId existing peo id
     * @throws RestException api returns error
     */
    public Peo findById(Long peoId) throws RestException {
        LOGGER.info("Try to find peo with id={}", peoId);
        try {
            String findPath = String.format(PEO_UPDATE_PATH, peoId);
            ResponseEntity<Peo> response = doGet(Peo.class, getEndPointURL(findPath));
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.error("Can't find peo with id={}", peoId, e);
            throw new RestException("Api returns error during finding peo with id = " + peoId, e);
        }
    }

    /**
     * Search peo by FPId attribute and linked page.
     *
     * @param peoFpId  peo FPId attribute value
     * @param pageFpId page fpId attribute value
     * @return found peos
     * @throws RestException api returns error
     */
    public Peo[] searchPeoByPageIdAndFpId(String peoFpId, String pageFpId) throws RestException {
        String query = String
                .format(SEARCH_PEO_QUERY, commonConfiguration.getPeoTemplateName(), MappingAttributeHelper.PLACEMENT_ID_IDENTIFIER, peoFpId, PROJECT_QUERY_PREFIX + MappingAttributeHelper.ATTR_SEITE_FP_ID,
                        pageFpId);
        return searchPeo(query);
    }

    /**
     * Search peo by FPid.
     *
     * @param peoFpId peo FPId attribute value
     * @return found peos
     * @throws RestException api returns error
     */
    public Peo[] searchPeoByFpId(String peoFpId) throws RestException {
        String query = String.format(SEARCH_PEO_BY_ID_QUERY, commonConfiguration.getPeoTemplateName(), MappingAttributeHelper.PLACEMENT_ID_IDENTIFIER, peoFpId);
        return searchPeo(query);
    }

    /**
     *
     * @param deletedAttr
     * @param value
     * @return
     * @throws RestException
     */
    public Peo[] searchPeoByAttributeAndValue(String deletedAttr, String value) throws RestException {
        String query = String.format(SEARCH_PEO_BY_ID_QUERY, commonConfiguration.getPeoTemplateName(), deletedAttr, value);
        return searchPeo(query);
    }
    /**
     * Search peo by article FPId and page FPId attributes.
     *
     * @param articleFpId article fpId attribute value
     * @param pageFpId    page fpId attribute value
     * @return found peos
     * @throws RestException api returns error
     */
    public Peo[] searchByArticleFpIdAndPageFpId(String articleFpId, String pageFpId) throws RestException {
        String query = String.format(SEARCH_PEO_QUERY, commonConfiguration.getPeoTemplateName(), PRODUCT_QUERY_PREFIX + MappingAttributeHelper.ATTR_AGS_ART_DEF_FP_ID, articleFpId,
                PROJECT_QUERY_PREFIX + MappingAttributeHelper.ATTR_SEITE_FP_ID, pageFpId);
        return searchPeo(query);
    }

    /**
     * Gets briefing links of peo.
     *
     * @param peoId peo identifier for finding
     * @return identifiers of linked boxes
     * @throws RestException api returns error
     */
    public Box[] getBriefingLinks(Long peoId) throws RestException {
        try {
            String briefingLinksPath = String.format(GET_BRIEFING_LINKS_PATH, peoId);
            ResponseEntity<Box[]> response = doGet(Box[].class, getEndPointURL(briefingLinksPath));
            return response.getBody();
        } catch (RestClientException e) {
            throw new RestException("Rest api has returned error", e);
        }
    }

    /**
     * Delete peo's page link.
     *
     * @param peoId  peo identifier
     * @param pageId deleting page identifier
     * @throws RestException api returns error
     */
    public void deletePageLink(Long peoId, Long pageId) throws RestException {
        LOGGER.info("Try to delete peo's(id={}) page(id={}) link.", peoId, pageId);
        try {
            String deletePageLinkPath = String.format(DELETE_PROJECT_LINK_PATH, peoId, pageId);
            doDelete(Long.class, getEndPointURL(deletePageLinkPath));
            LOGGER.info("Project(id={}) link of peo(id={}) has been deleted", peoId, pageId);
        } catch (RestClientException e) {
            LOGGER.error("Can't delete project(id={}) link of peo(id={})", pageId, peoId, e);
            throw new RestException("Rest api has returned error during deleting page link", e);
        }
    }

    /**
     * Creates peo's page link.
     *
     * @param peoId  eo identifier
     * @param pageId linked page identifier
     * @throws RestException api returns error
     */
    public void createPageLink(Long peoId, Long pageId) throws RestException {
        LOGGER.info("Try to create page(id={}) link to peo(id={}).", pageId, peoId);
        try {
            String deletePageLinkPath = String.format(CREATE_PROJECT_LINK_PATH, peoId);
            ProjectRequest projectRequest = new ProjectRequest().projectId(pageId);
            doPost(Long.class, getEndPointURL(deletePageLinkPath), projectRequest);
            LOGGER.info("Project(id={}) link of peo(id={}) has been created", peoId, pageId);
        } catch (RestClientException e) {
            LOGGER.error("Can't create project(id={pageId}) link for peo(id={peoId})", pageId, peoId, e);
            throw new RestException("Rest api has returned error during creating page link", e);
        }
    }

    /**
     * Gets peo's page link.
     *
     * @param peoId peo identifier
     * @return list of page identifiers
     * @throws RestException api returns error
     */
    public List<Long> getPeoProjectLinks(Long peoId) throws RestException {
        LOGGER.info("Try to get page links of peo(id={}).", peoId);
        try {
            String getPageLinkPath = String.format(GET_PROJECT_LINKS_PATH, peoId);
            ResponseEntity<ProjectLink[]> response = doGet(ProjectLink[].class, getEndPointURL(getPageLinkPath));
            ProjectLink[] links = response.getBody();
            LOGGER.info("{} project links of peo(id={}) have been found", links.length, peoId);
            return Arrays.stream(links).map(ProjectLink::getProjectId).collect(Collectors.toList());
        } catch (RestClientException e) {
            LOGGER.error("Can't get project links for peo(id={peoId})", peoId, e);
            throw new RestException("Rest api has returned error during getting page link", e);
        }
    }

    /**
     * Search peo by query.
     *
     * @param query query to search
     * @return found peos
     * @throws RestException api returns error
     */
    private Peo[] searchPeo(String query) throws RestException {
        LOGGER.info("Searching peo with parameters {}", query);
        try {
            ResponseEntity<Peo[]> response = doGet(Peo[].class, getEndPointURL(query));
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.error("Search peo has been failed", e);
            throw new RestException("Rest api has returned error", e);
        }
    }

    /**
     * Extracts peo identity from response header
     *
     * @param response response entity
     * @return peo identifier
     */
    private Long extractCreatedPeoId(ResponseEntity response) {
        String path = response.getHeaders().getLocation().getPath();
        return Long.valueOf(path.substring(path.lastIndexOf(PATH_SPLITTER) + 1));
    }

    @Override
    protected String getUrl() {
        return apiConfiguration.getPeoApiUrl();
    }
}
