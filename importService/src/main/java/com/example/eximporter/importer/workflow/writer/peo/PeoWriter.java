package com.example.eximporter.importer.workflow.writer.peo;

import com.example.eximporter.importer.model.api.AttributeValues;
import com.example.eximporter.importer.model.api.Box;
import com.example.eximporter.importer.model.api.Peo;
import com.example.eximporter.importer.model.api.Product;
import com.example.eximporter.importer.model.api.Project;
import com.example.eximporter.importer.model.extended.ExtendedPeo;
import com.example.eximporter.importer.model.extended.ParkedPeo;
import com.example.eximporter.importer.service.http.PeoApiService;
import com.example.eximporter.importer.service.http.ProductApiService;
import com.example.eximporter.importer.service.http.ProjectApiService;
import com.example.eximporter.importer.service.http.RestException;
import com.example.eximporter.importer.service.parking.ParkedPeoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.eximporter.importer.controller.StepListener.FILE_PATH_JOB_PARAMETER;
import static com.example.eximporter.importer.controller.StepListener.PARTIALLY_PROCESSED_MESSAGE;
import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.buildSimpleAttributeValues;
import static com.example.eximporter.importer.helper.JsonModelBuilderHelper.getAttributeValue;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.*;
import static com.example.eximporter.importer.service.parking.ParkedPeoProcessor.*;

/**
 * Interacts with omn to save peos during import
 */
@Component("peoWriter")
public class PeoWriter implements ItemWriter<ExtendedPeo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeoWriter.class);
    private static final String TRUE_VALUE = "true";
    @Autowired
    private ProductApiService productApiService;
    @Autowired
    private PeoApiService peoApiService;
    @Autowired
    private ProjectApiService projectApiService;
    @Autowired
    private ParkedPeoService parkedPeoService;
    private Map<String, List<Long>> pageFpIdsMap = new HashMap<>();
    private Boolean isSendParkedMessage = false;
    private Boolean isSendBriefMessage = false;
    private Boolean isSendDuplicated = false;
    private StringBuilder parkedPeos = new StringBuilder();
    private StringBuilder briefingPeo = new StringBuilder();
    private StringBuilder duplicatePeo = new StringBuilder();
    private String fileName;
    private Integer countDuplicates = 0;
    private Integer countBrief = 0;

    @BeforeStep
    public void initMessage(StepExecution stepExecution) {
        duplicatePeo = new StringBuilder("PEO for following articles can not be created for this catalog, because they are already existing")
                .append(MSG_NEXTLINE).append("\tFound Articles").append(MSG_NEXTLINE);
        briefingPeo = new StringBuilder("Peo cannot be moved, because PEO is in use in Patch+Brief").append(MSG_NEXTLINE);
        parkedPeos = new StringBuilder("Peo can not be linked to a pages:" + MSG_NEXTLINE);
        String filePath = stepExecution.getJobParameters().getString(FILE_PATH_JOB_PARAMETER);
        fileName = filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER) + 1);
        parkedPeos.append("File name:").append(fileName).append(MSG_NEXTLINE);
        isSendParkedMessage = false;
        isSendBriefMessage = false;
        isSendDuplicated = false;
        countBrief = 0;
        countDuplicates = 0;
    }

    @AfterStep
    public void sendMessage(StepExecution stepExecution) {
        if (isSendBriefMessage || isSendDuplicated) {
            stepExecution.getJobExecution().getExecutionContext().put(PARTIALLY_PROCESSED_MESSAGE,
                    new JobParameter(buildFooterAndBodyMessage(isSendDuplicated, isSendBriefMessage)));
        }
        if (isSendParkedMessage) {
            stepExecution.getJobExecution().getExecutionContext().put(PARKED_PEO, new JobParameter(parkedPeos.toString()));
        }
    }

    /**
     * Saves peo on omn if it's allowed.
     *
     * @param peos processed peos
     * @throws Exception errors during saving
     */
    @Override
    public void write(List<? extends ExtendedPeo> peos) throws Exception {
        for (ExtendedPeo extendedPeo : peos) {
            Peo[] foundPeos = extendedSearchPeo(extendedPeo);
            if (foundPeos.length > 1) {
                throw new RestException("More than one peo was found");
            } else if (foundPeos.length > 0) {
                Peo foundPeo = foundPeos[0];
                Peo newPeo = extendedPeo.getPeo();
                Box[] briefingLinks = peoApiService.getBriefingLinks(foundPeo.getId());
                if (!getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID).equalsIgnoreCase(getAttributeValue(foundPeo.getAttributes(), FP_PAGE_ID)) && briefingLinks.length > 0) {
                    LOGGER.error("Peo can't be updated and moved to another page. Peo with id={} is placed on page briefing. PageKey  Old:{}  New: {}", foundPeo.getId(),
                            getAttributeValue(foundPeo.getAttributes(), FP_PAGE_ID), getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID));
                    isSendBriefMessage = true;
                    countBrief++;
                    briefingPeo.append(buildBriefLinkedPageInfo(foundPeo, extendedPeo, briefingLinks));
                } else {
                    updatePeoInfo(newPeo, briefingLinks, foundPeo, extendedPeo);
                }
            } else {
                createPeo(extendedPeo);
            }
        }
    }

    /**
     * Update peo info
     * @param newPeo  -received peo
     * @param briefingLinks  briefing box
     * @param foundPeo target peo
     * @param extendedPeo received extended peo
     * @throws RestException
     */
    private void updatePeoInfo(Peo newPeo, Box[] briefingLinks, Peo foundPeo, ExtendedPeo extendedPeo) throws RestException {

        addSynchronizationInfo(newPeo);
        if (briefingLinks.length > 0) {
            LOGGER.info("Peo with id={} is placed on page briefing with pageId: {}. Partial update SKU", foundPeo.getId(), briefingLinks[0].getPageId());
            AttributeValues tempPageKey = newPeo.getAttributes().get(FP_PAGE_ID);
            newPeo.getAttributes().remove(FP_PAGE_ID);
            newPeo.getAttributes().remove(ATTR_AGS_PEO_DEF_FP_ARTICLE_TECH_ID);
            newPeo.getAttributes().remove(PLACEMENT_ID_IDENTIFIER);
            peoApiService.update(foundPeo.getId(), newPeo);
            newPeo.getAttributes().put(FP_PAGE_ID, tempPageKey);

        } else {
            peoApiService.update(foundPeo.getId(), newPeo);
        }
        updatePeoLinks(foundPeo, extendedPeo);
    }


    /**
     * Search peo by peoFpId, when not found - search by productId and pageId for de language
     * @param extendedPeo
     * @return
     * @throws RestException
     */
    private Peo[]  extendedSearchPeo(ExtendedPeo extendedPeo) throws RestException {
        Peo[] foundPeos = peoApiService.searchPeoByFpId(extendedPeo.getPeoFpId());
        if (foundPeos.length==0){
            String pageIdDe= getAttributeValue(extendedPeo.getPeo().getAttributes(), FP_PAGE_ID)+"_D";
            LOGGER.info("Peo is not founded by peoFpId : {}. Try to find by productId:{} and pageId for De:{}", extendedPeo.getPeoFpId(), extendedPeo.getArticleFpId(), pageIdDe);
            foundPeos = peoApiService.searchByArticleFpIdAndPageFpId(extendedPeo.getArticleFpId(), pageIdDe);
        }
        return  foundPeos;
    }

    /**
     * Update PEO links on pages
     *
     * @param foundPeo    founded peo
     * @param extendedPeo peo data with linking info
     * @throws RestException errors during saving peo
     */
    private void updatePeoLinks(Peo foundPeo, ExtendedPeo extendedPeo) throws RestException {
        Peo newPeo = extendedPeo.getPeo();
        List<Long> linkedOldPages = peoApiService.getPeoProjectLinks(foundPeo.getId());
        List<Long> receivedNewPages = findPageByFpId(getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID));
        LOGGER.info("Result compare key. Old:{}  New: {}", getAttributeValue(foundPeo.getAttributes(), FP_PAGE_ID), getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID));
        if (getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID).equals(getAttributeValue(foundPeo.getAttributes(), FP_PAGE_ID))) {
            for (Long pageForLinkId : getChangesList(linkedOldPages, receivedNewPages)) {
                LOGGER.info("Add link for page:{} to peo:{}", pageForLinkId, foundPeo.getId());
                peoApiService.createPageLink(foundPeo.getId(), pageForLinkId);
            }
        } else {
            movePeoToAnotherPages(foundPeo.getId(), getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID));
        }
    }

    /**
     * Compare to list and return list ids for add new links
     *
     * @param linkedOldPages
     * @param receivedNewPages
     * @return
     * @throws RestException
     */
    private List<Long> getChangesList(List<Long> linkedOldPages, List<Long> receivedNewPages) throws RestException {
        if (linkedOldPages != null && receivedNewPages != null) {
            if (linkedOldPages.size() == receivedNewPages.size() && linkedOldPages.containsAll(receivedNewPages)) {
                LOGGER.info("Linked pages list and new received pages are identical. Skip re-linking");
                return Collections.emptyList();
            }
            if (linkedOldPages.size() <= receivedNewPages.size()) {
                List<Long> list = new ArrayList<>();
                Set<Long> ad = new HashSet<>(linkedOldPages);
                Set<Long> bd = new HashSet<>(receivedNewPages);
                bd.removeAll(ad);
                list.addAll(bd);
                return list;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Moves peo to another pages.
     * Deletes existing pages' links
     *
     * @param peoId   peo identifier to move
     * @param pageKey ids of pages for moving
     * @throws RestException errors during peo moving
     */
    private void movePeoToAnotherPages(Long peoId, String pageKey) throws RestException {
        List<Long> linkedProjectIds = peoApiService.getPeoProjectLinks(peoId);
        for (Long projectId : linkedProjectIds) {
            peoApiService.deletePageLink(peoId, projectId);
        }
        LOGGER.info("link to new peoid:{}, key:{}", peoId, pageKey);
        linkPeoToPage(pageKey, peoId);

    }

    /**
     * Links peo to another page.
     * Save parkedPeo when page is not found
     *
     * @param pageKey    page key to move or link
     * @param foundPeoId peo id for moving
     * @throws RestException errors during peo linking
     */
    public void linkPeoToPage(String pageKey, Long foundPeoId) throws RestException {
        List<Long> pageIds = findPageByFpId(pageKey);
        if (pageIds != null) {
            for (Long pageId : pageIds) {
                peoApiService.createPageLink(foundPeoId, pageId);
            }
        } else {
            LOGGER.error("Page project with fp id={} hasn't been found. Save it on parked peo storage", pageKey);
            ParkedPeo parkedPeo = new ParkedPeo();
            parkedPeo.setAttempt(1L);
            parkedPeo.setIdPeo(foundPeoId);
            parkedPeo.setPageFpId(pageKey);
            parkedPeo.setFileName(fileName);
            parkedPeoService.saveParkedPeo(parkedPeo);
            isSendParkedMessage = true;
            parkedPeos.append(PEO).append(MSG_PREF).append(parkedPeo.getIdPeo().toString()).append(MSG_PREFLONG);
            parkedPeos.append(PAGE).append(MSG_PREF).append(parkedPeo.getPageFpId()).append(MSG_PREFLONG);
            parkedPeos.append("Count attempts").append(MSG_PREF).append(parkedPeo.getAttempt()).append(MSG_NEXTLINE);

        }
    }

    public boolean linkParkedPeoToPage(ParkedPeo parkedPeo) throws RestException {
        List<Long> pageIds = findPageByFpId(parkedPeo.getPageFpId());
        if (pageIds != null) {
            for (Long pageId : pageIds) {
                peoApiService.createPageLink(parkedPeo.getIdPeo(), pageId);
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Find page id from projects cache or by api service.
     *
     * @param pageKey page FP identity to find
     * @return found page identity
     * @throws RestException error during searching page by api
     */
    public List<Long> findPageByFpId(String pageKey) throws RestException {
        List<Long> pageIds = pageFpIdsMap.get(pageKey);
        if (pageIds == null) {
            Long[] projectIds = projectApiService.searchProjectsByFpID(ATTR_AGS_6_SEITE, ATTR_SEITE_FP_PAGEKEY, pageKey);
            if (projectIds.length > 0) {
                pageIds = Arrays.asList(projectIds);
                pageFpIdsMap.put(pageKey, pageIds);
            } else {
                LOGGER.error("Page project with pageKey={} hasn't been found.", pageKey);
            }
        }
        return pageIds;
    }

    /**
     * Creates new peo and links to page.
     *
     * @param extendedPeo peo attributes and linking data
     * @throws RestException errors during creating peo
     */
    private void createPeo(ExtendedPeo extendedPeo) throws RestException {
        Peo newPeo = extendedPeo.getPeo();
        String articleFpId = extendedPeo.getArticleFpId();
        Long[] products = productApiService.searchProductsByFpID(ATTR_AGS_ARTICLE_DEFAULT, ATTR_AGS_ART_DEF_FP_ID, articleFpId);
        if (products.length > 0) {
            Long productId = products[0];
            Product product = productApiService.getProductById(productId);
            newPeo.setProductId(productId);
            newPeo.setName(buildPeoName(product, extendedPeo.getPeoFpId().split("~")[1]));
            addImportInfo(newPeo);
            List<Long> duplicatedPeoIds = new ArrayList<>();
            Long newPeoId = peoApiService.create(newPeo, duplicatedPeoIds);
            if (newPeoId != null) {
                List<Long> pageIds = findPageByFpId(getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID));
                relinkPeo(pageIds, newPeoId, newPeo);
            } else {
                LOGGER.error("Duplicated Peo. PeoFpId:{}, Peo name name:{} , article Fpid:{} ", getAttributeValue(newPeo.getAttributes(), PLACEMENT_ID_IDENTIFIER), newPeo.getName(), articleFpId);
                isSendDuplicated = true;
                countDuplicates++;
                duplicatePeo.append(buildDuplicateMessage(duplicatedPeoIds.get(0), newPeo));
            }

        } else {
            LOGGER.error("Product with fpId= {} hasn't been found. Skip this ");
        }
    }

    /**
     * Relink peo and page
     *
     * @param pageIds  list pages
     * @param newPeoId id of created peo
     * @param newPeo   new peo
     */
    private void relinkPeo(List<Long> pageIds, Long newPeoId, Peo newPeo) throws RestException {
        if (pageIds != null) {
            for (Long pageId : pageIds) {
                peoApiService.createPageLink(newPeoId, pageId);
            }
        } else {
            LOGGER.error("Page project for PageKey={} hasn't been found. Save it on parked peo storage", getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID));
            ParkedPeo parkedPeo = new ParkedPeo();
            parkedPeo.setAttempt(1L);
            parkedPeo.setIdPeo(newPeoId);
            parkedPeo.setPageFpId(getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID));
            parkedPeo.setFileName(fileName);
            parkedPeoService.saveParkedPeo(parkedPeo);
            isSendParkedMessage = true;
            parkedPeos.append(PEO).append(MSG_PREF).append(parkedPeo.getIdPeo().toString()).append(MSG_PREFLONG);
            parkedPeos.append(PAGE).append(MSG_PREF).append(parkedPeo.getPageFpId()).append(MSG_PREFLONG);
            parkedPeos.append("Count attempts").append(MSG_PREF).append(parkedPeo.getAttempt()).append(MSG_NEXTLINE);
        }

    }


    private String buildBriefLinkedPageInfo(Peo foundPeo, ExtendedPeo extendedPeo, Box[] briefingLinks) throws RestException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Article: ").append(extendedPeo.getArticleFpId()).append(MSG_NEXTLINE)
                .append("Peo ID: ").append(foundPeo.getId()).append(MSG_NEXTLINE)
                .append("PlacementTechId: ").append(getAttributeValue(extendedPeo.getPeo().getAttributes(), PLACEMENT_ID_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("Order number: ").append(getAttributeValue(extendedPeo.getPeo().getAttributes(), ORDER_NUMBER_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("PageKey: ").append(getAttributeValue(extendedPeo.getPeo().getAttributes(), FP_PAGE_ID)).append(MSG_NEXTLINE)
                .append("Linked catalog pages").append(MSG_NEXTLINE)
                .append(buildLinkedPageInfo(foundPeo.getId())).append(MSG_NEXTLINE)
                .append("Used in patch+Brief").append(MSG_NEXTLINE)
                .append(buildLinkedBriefMessage(briefingLinks)).append(MSG_NEXTLINE);
        return stringBuilder.toString();
    }


    private String buildDuplicateMessage(Long duplicatePeoId, Peo newPeo) throws RestException {
        StringBuilder stringBuilder = new StringBuilder();
        Peo oldPeo = peoApiService.findById(duplicatePeoId);
        Box[] oldBriefingLinks = peoApiService.getBriefingLinks(duplicatePeoId);
        stringBuilder.append("Article number: ").append(getAttributeValue(newPeo.getAttributes(), ATTR_AGS_PEO_DEF_FP_ARTICLE_TECH_ID)).append(MSG_NEXTLINE)
                .append("  Old Info").append(MSG_NEXTLINE)
                .append("\tPeo Id: ").append(duplicatePeoId).append(MSG_NEXTLINE)
                .append("\tPlacementTechId: ").append(getAttributeValue(oldPeo.getAttributes(), PLACEMENT_ID_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("\tOrder number: ").append(getAttributeValue(oldPeo.getAttributes(), ORDER_NUMBER_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("\tPageKey: ").append(getAttributeValue(oldPeo.getAttributes(), FP_PAGE_ID)).append(" position: ").append(getAttributeValue(oldPeo.getAttributes(), POSITION_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("\tLinked catalog pages").append(MSG_NEXTLINE)
                .append(buildLinkedPageInfo(duplicatePeoId)).append(MSG_NEXTLINE);
        if (oldBriefingLinks.length > 0) {
            stringBuilder.append("\tUsed in patch+Brief").append(MSG_NEXTLINE)
                    .append(buildLinkedBriefMessage(oldBriefingLinks)).append(MSG_NEXTLINE);
        }
        stringBuilder.append("  New Info").append(MSG_NEXTLINE)
                .append("\tPlacementTechId: ").append(getAttributeValue(newPeo.getAttributes(), PLACEMENT_ID_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("\tOrder number: ").append(getAttributeValue(newPeo.getAttributes(), ORDER_NUMBER_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("\tPageKey: ").append(getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID)).append(" position: ").append(getAttributeValue(newPeo.getAttributes(), POSITION_IDENTIFIER)).append(MSG_NEXTLINE)
                .append("\tPages to link").append(MSG_NEXTLINE)
                .append(buildPageToLinkInfo(getAttributeValue(newPeo.getAttributes(), FP_PAGE_ID))).append(MSG_NEXTLINE);
        return stringBuilder.toString();
    }

    /**
     * Build message about linked peo to page
     *
     * @param foundPeoId
     * @return
     * @throws RestException
     */
    private String buildLinkedPageInfo(Long foundPeoId) throws RestException {
        List<Long> linkedProjectIds = peoApiService.getPeoProjectLinks(foundPeoId);
        return buildLinkedPageInfoByIds(linkedProjectIds);
    }


    private String buildPageToLinkInfo(String pageKey) throws RestException {
        List<Long> pageIds = findPageByFpId(pageKey);
        return buildLinkedPageInfoByIds(pageIds);
    }

    private String buildLinkedPageInfoByIds(List<Long> pageIds) throws RestException {
        if (pageIds != null && !pageIds.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            Set<String> linkedPages = new HashSet<>();
            for (Long projectId : pageIds) {
                Project pageProject = projectApiService.getProjectById(projectId);
                Project languageProject = projectApiService.getProjectById(pageProject.getParentId());
                Project catalogProject = projectApiService.getProjectById(languageProject.getParentId());
                String pagetoLink = "\t   [" + getAttributeValue(catalogProject.getAttributes(), ATTR_WERBEMITTEL_FP_ID) + " " + getAttributeValue(catalogProject.getAttributes(), "WERBEMITTELBEZEICHNUNG") + "][" +
                        getAttributeValue(languageProject.getAttributes(), ATTR_COUNTRY_VALUE) + "][" + getAttributeValue(pageProject.getAttributes(), ATTR_ARBEITSSEITE) + "]";
                linkedPages.add(pagetoLink);
            }
            for (String link : linkedPages) {
                stringBuilder.append(link).append(MSG_NEXTLINE);
            }
            return stringBuilder.toString();
        } else return "\t   PEO is not linked to any page \n";
    }

    /**
     * Build info about brief links
     *
     * @param briefingLinks
     * @return
     */
    private String buildLinkedBriefMessage(Box[] briefingLinks) throws RestException {
        List<Long> pageIds = new ArrayList<>();
        for (Box box : briefingLinks) {
            pageIds.add(box.getPageId());
        }
        return buildLinkedPageInfoByIds(pageIds);
    }


    private String buildFooterAndBodyMessage(Boolean isSendDuplicated, Boolean isSendBriefMessage) {
        StringBuilder msgBuilderFooter = new StringBuilder();
        StringBuilder msgBuilderBody = new StringBuilder();
        if (isSendDuplicated) {
            msgBuilderFooter.append("PEO duplicates: ").append(countDuplicates).append(MSG_NEXTLINE);
            msgBuilderBody.append(duplicatePeo).append(MSG_NEXTLINE);
        }
        if (isSendBriefMessage) {
            msgBuilderFooter.append("PEO movement not possible because PEO in use on Patch+Brief: ").append(countBrief).append(MSG_NEXTLINE);
            msgBuilderBody.append(briefingPeo).append(MSG_NEXTLINE);
        }
        return msgBuilderFooter.toString() + MSG_NEXTLINE + msgBuilderBody.toString();
    }


    /**
     * Build name of peo
     *
     * @param product
     * @param projectKey
     * @return
     */
    private String buildPeoName(Product product, String projectKey) {
        return getAttributeValue(product.getAttributes(), ATTR_PIM_OBJECT_NAME) + (projectKey != null ? "_" + projectKey : "");
    }


    /**
     * Adds synchronization info attributes to peo.
     *
     * @param newPeo to add attributes
     */
    private void addSynchronizationInfo(Peo newPeo) {
        newPeo.getAttributes().put(PEO_SYNCHRONIZED_DATETIME, buildSimpleAttributeValues(new Date().toString()));
        newPeo.getAttributes().put(PEO_SYNCHRONIZED, buildSimpleAttributeValues(TRUE_VALUE));
        newPeo.getAttributes().put(ATTR_AGS_PEO_DEF_PLACED_FP, buildSimpleAttributeValues(TRUE_VALUE));
    }

    /**
     * Adds import info attributes to peo.
     *
     * @param newPeo to add attributes
     */
    private void addImportInfo(Peo newPeo) {
        newPeo.getAttributes().put(PEO_IMPORTED_DATETIME, buildSimpleAttributeValues(new Date().toString()));
        newPeo.getAttributes().put(PEO_IMPORTED, buildSimpleAttributeValues(TRUE_VALUE));
        newPeo.getAttributes().put(ATTR_AGS_PEO_DEF_PLACED_FP, buildSimpleAttributeValues(TRUE_VALUE));
    }

}
