package com.example.eximporter.importer.service.parking;

import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.workflow.writer.peo.PeoWriter;
import com.example.eximporter.importer.configuration.JobCronConfiguration;
import com.example.eximporter.importer.model.extended.ParkedPeo;
import com.example.eximporter.importer.service.http.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Process parked peos
 */
@Service
public class ParkedPeoProcessor {
    @Autowired
    private ParkedPeoService parkedPeoService;
    @Autowired
    private PeoWriter peoWriter;
    @Autowired
    private JobCronConfiguration jobCronConfiguration;
    private Logger logger = LoggerFactory.getLogger(ParkedPeoProcessor.class);
    public static final String MSG_PREF = ":";
    public static final String MSG_PREFLONG = " | ";
    public static final String MSG_NEXTLINE = "\n";
    Boolean isSendProcessed;

    /**
     * Get parked peo and process it
     *
     * @param parkedPeos parked peos from db
     */
    void processParkedPeos(List<ParkedPeo> parkedPeos, StepExecution stepExecution) {
        Boolean isSendDeleted = false;
        isSendProcessed = false;
        StringBuilder deletedPeos = new StringBuilder("Remove parked peo after max count attempts." + MSG_NEXTLINE);
        StringBuilder problemPeos = new StringBuilder("\n Peo can not be linked to a pages:" + MSG_NEXTLINE);
        for (ParkedPeo parkedPeo : parkedPeos) {
            if (parkedPeo.getAttempt() <= jobCronConfiguration.getMaxCountAttempts()) {
                try {
                    processParkedPeo(parkedPeo, problemPeos);
                } catch (RestException e) {
                    logger.error("RestApi problem when linking Peo: {} to a page: {}", parkedPeo.getIdPeo(),
                            parkedPeo.getPageFpId(), e);
                }
            } else {
                logger.info("Remove parked peo: {} with page fpId: {} ", parkedPeo.getIdPeo(), parkedPeo.getPageFpId());
                isSendDeleted = true;
                problemPeos.append("File name").append(MSG_PREF).append(parkedPeo.getFileName()).append(MSG_NEXTLINE);
                deletedPeos.append(MappingAttributeHelper.PEO).append(MSG_PREF).append(parkedPeo.getIdPeo().toString())
                        .append(MSG_PREFLONG);
                deletedPeos.append(MappingAttributeHelper.PAGE).append(MSG_PREF).append(parkedPeo.getPageFpId()).append(MSG_NEXTLINE);
                parkedPeoService.deleteParkedPeo(parkedPeo);
            }
        }

        String msg = "";
        if (isSendDeleted) {
            msg = msg + deletedPeos.toString();
        }
        if (isSendProcessed) {
            msg = msg + problemPeos.toString();
        }
        if (isSendDeleted || isSendProcessed){
        stepExecution.getJobExecution().getExecutionContext().put(MappingAttributeHelper.PARKED_PEO, new JobParameter(msg));
        }
    }

    /**
     * Process parked peo
     *
     * @param parkedPeo   Parked Peo
     * @param problemPeos
     */
    private void processParkedPeo(ParkedPeo parkedPeo, StringBuilder problemPeos) throws RestException {
        if (peoWriter.linkParkedPeoToPage(parkedPeo)) {
            parkedPeo.setAttempt(jobCronConfiguration.getMaxCountAttempts() + 1);
            parkedPeoService.saveParkedPeo(parkedPeo);
            logger.info("Peo:{} linked to a page: {}. Remove from parked peo", parkedPeo.getIdPeo(), parkedPeo.getPageFpId());
        } else {

            parkedPeo.setAttempt(parkedPeo.getAttempt() + 1);
            parkedPeoService.saveParkedPeo(parkedPeo);
            logger.info("Parked peo is not linked to page. Update count attempts: {}",parkedPeo.getAttempt());
            isSendProcessed = true;
            problemPeos.append("File name").append(MSG_PREF).append(parkedPeo.getFileName())
                    .append(MSG_NEXTLINE);
            problemPeos.append(MappingAttributeHelper.PEO).append(MSG_PREF).append(parkedPeo.getIdPeo().toString())
                    .append(MSG_PREFLONG);
            problemPeos.append(MappingAttributeHelper.PAGE).append(MSG_PREF).append(parkedPeo.getPageFpId()).append(MSG_PREFLONG);
            problemPeos.append("Count attempts").append(MSG_PREF).append(parkedPeo.getAttempt()).append(MSG_NEXTLINE);
        }
    }
}
