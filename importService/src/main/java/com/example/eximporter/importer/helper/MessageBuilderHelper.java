package com.example.eximporter.importer.helper;

import com.example.eximporter.importer.controller.StepListener;
import com.example.eximporter.importer.service.http.RestException;
import org.springframework.batch.core.StepExecution;

import javax.xml.bind.UnmarshalException;
import java.util.HashMap;
import java.util.Map;

import static com.example.eximporter.importer.helper.MappingAttributeHelper.PARKED_PAGE;
import static com.example.eximporter.importer.helper.MappingAttributeHelper.PARKED_PEO;
import static com.example.eximporter.importer.service.parking.ParkedPeoProcessor.MSG_NEXTLINE;

/**
 * Build message for notification
 */
public class MessageBuilderHelper {
    public static final String COUNTER_STYLES = "COUNTER_STYLES";
    public static final String COUNTER_ARTICLES = "COUNTER_ARTICLES";
    public static final String COUNTER_VARIANTS = "COUNTER_VARIANTS";
    public static final String COUNTER_CATALOG = "COUNTER_CATALOG";
    public static final String COUNTER_LANGUAGES = "COUNTER_LANGUAGES";
    private static final String MSG_PREF = "*****************************";
    private static final String MSG_ERROR_JAXB = "UnmarshalException. Please check your file";
    private static final String MSG_ERROR_REST = "Rest API Error. Please contact with your administrator";
    private static final String MSG_ERROR_DUPL = "More than one product was found";
    private static final String MSG_ERROR_UNC = "Unknown error. Please contact with your administrator";

    private MessageBuilderHelper() {
    }

    /**
     * Create message
     *
     * @param messages
     * @return
     */
    public static String buildMessage(String... messages) {
        StringBuilder messageBuilder = new StringBuilder();
        for (String message : messages) {
            messageBuilder.append(message);
        }
        return messageBuilder.toString();
    }

    /**
     * Get information about step execution
     *
     * @param stepExecution
     * @return message
     */
    public static String getMessageInfo(StepExecution stepExecution) {
        String productStatistic = "";
        if (stepExecution.getJobExecution().getExecutionContext().get(COUNTER_STYLES) != null && stepExecution.getJobExecution().getExecutionContext().get(COUNTER_ARTICLES) != null && stepExecution.getJobExecution().getExecutionContext().get(COUNTER_VARIANTS) != null) {
            productStatistic = buildMessage(MSG_PREF, MSG_NEXTLINE, "Product statistics", MSG_NEXTLINE, "Count processed styles: ",
                    String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(COUNTER_STYLES)), MSG_NEXTLINE, "Count processed articles: ",
                    String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(COUNTER_ARTICLES)), MSG_NEXTLINE, "Count processed variants: ",
                    String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(COUNTER_VARIANTS)));
        }
        String catalogStatistic = "";
        if (stepExecution.getJobExecution().getExecutionContext().get(COUNTER_CATALOG) != null && stepExecution.getJobExecution().getExecutionContext().get(COUNTER_LANGUAGES) != null) {
            catalogStatistic = buildMessage(MSG_PREF, MSG_NEXTLINE, "Catalog statistics", MSG_NEXTLINE, "Count processed catalog: ", String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(COUNTER_CATALOG)), MSG_NEXTLINE,
                    "Count processed languages: ", String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(COUNTER_LANGUAGES)));
        }

        return buildMessage("Job name: ", stepExecution.getJobExecution().getJobParameters().getString(StepListener.FILE_PATH_JOB_PARAMETER), MSG_NEXTLINE,
                "Read count: ", String.valueOf(stepExecution.getReadCount()), MSG_NEXTLINE, "Read skip count: ",
                String.valueOf(stepExecution.getReadSkipCount()), MSG_NEXTLINE, "Process skip count: ",
                String.valueOf(stepExecution.getProcessSkipCount()), MSG_NEXTLINE, "Write count: ",
                String.valueOf(stepExecution.getWriteCount()), MSG_NEXTLINE, "Write skip count: ",
                String.valueOf(stepExecution.getWriteSkipCount()), MSG_NEXTLINE, "Rollback count: ",
                String.valueOf(stepExecution.getRollbackCount()), MSG_NEXTLINE, "Commit count: ",
                String.valueOf(stepExecution.getCommitCount()), MSG_NEXTLINE, "Exit status:", getExitStatusMessage(stepExecution), MSG_NEXTLINE, productStatistic, catalogStatistic);
    }

    public static Map<String, String> getMessageForParkedService(StepExecution stepExecution) {
        Map<String, String> map = new HashMap<>();

        if (stepExecution.getJobExecution().getExecutionContext().get(PARKED_PEO) != null) {
            map.put(PARKED_PEO, String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(PARKED_PEO)));
        }

        if (stepExecution.getJobExecution().getExecutionContext().get(PARKED_PAGE) != null) {
            map.put(PARKED_PAGE, String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(PARKED_PAGE)));
        }
        return map;
    }

    private static String getExitStatusMessage(StepExecution stepExecution) {
        String msg = stepExecution.getExitStatus().getExitCode();
        if (!stepExecution.getFailureExceptions().isEmpty()) {
                if (stepExecution.getFailureExceptions().get(0) instanceof UnmarshalException) {
                    return MSG_ERROR_JAXB;
                }
                if (stepExecution.getFailureExceptions().get(0) instanceof RestException && stepExecution.getFailureExceptions().get(0).getMessage().contains(MSG_ERROR_DUPL)) {
                    return stepExecution.getFailureExceptions().get(0).getMessage();
                }
                if (stepExecution.getFailureExceptions().get(0) instanceof RestException) {
                    return MSG_ERROR_REST + "\n" + (stepExecution.getFailureExceptions().get(0).getMessage().length() > 300 ? stepExecution.getFailureExceptions().get(0).getMessage().substring(0, 300) : stepExecution.getFailureExceptions().get(0).getMessage());
                } else {
                    return MSG_ERROR_UNC + "\n" + (stepExecution.getFailureExceptions().get(0).getMessage().length() > 300 ? stepExecution.getFailureExceptions().get(0).getMessage().substring(0, 300) : stepExecution.getFailureExceptions().get(0).getMessage());
                }

            }
        return msg;
    }
}


