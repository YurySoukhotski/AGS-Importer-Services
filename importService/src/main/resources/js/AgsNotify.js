var WFL_JAVASCRIPT_NAME = "MailSendTest";
var LOGGER_NAME = mandatorName + ".Workflows." + WFL_JAVASCRIPT_NAME;
var LOGGER_PREFIX = WFL_JAVASCRIPT_NAME + ": ";
var LOGGER_FILE = "/usr/local/omn/logs/" + mandatorName + "/workflows/" + WFL_JAVASCRIPT_NAME + ".log";

// Logger configuration
var Log4j = Packages.org.apache.log4j;
var loggerExists = Log4j.LogManager.exists(LOGGER_NAME);
var logger = Log4j.Logger.getLogger(LOGGER_NAME);
if (!loggerExists) {
    logger.setLevel(Log4j.Level.toLevel(Log4j.Level.INFO_INT));
    logger.setAdditivity(false); // otherwise also written to tomcat.log
    var rollingFileAppender = new Log4j.RollingFileAppender();
    rollingFileAppender.setFile(LOGGER_FILE);
    rollingFileAppender.setAppend(true);
    rollingFileAppender.setBufferedIO(false);
    rollingFileAppender.setBufferSize(4096);
    rollingFileAppender.setMaxBackupIndex(4);
    // Default max size is 10MB...
    var layout = new Log4j.PatternLayout("%d [%t] %-5p %C.%M() %c{1} - %m%n");
    rollingFileAppender.setLayout(layout);
    rollingFileAppender.activateOptions();
    logger.removeAllAppenders();
    logger.addAppender(rollingFileAppender);
}

var cpFacade = entryFacade.lookupBean("ClientProjectFacade");
var basicHelper = entryFacade.lookupBean("basicJavaScriptHelper");
var configurationHelper = basicHelper.getConfigurationHelper();
var messageHelper = basicHelper.getMessagingHelper();


try {
    logger.info(LOGGER_PREFIX + "####### Starting #######");
    main();
}
catch (e) {
    result = "ERROR";
    var javaException = e.javaException;
    if (javaException) {
        logger.error(LOGGER_PREFIX + "Java exception caught", javaException);
    }
    else {
        var rhinoException = e.rhinoException;
        if (rhinoException) {
            logger.error(LOGGER_PREFIX + "Rhino exception caught" + rhinoException.getMessage());
        }
        else {
            logger.error(LOGGER_PREFIX + "Unknown exception caught" + e.toString());
        }
    }
}
finally {
    logger.info(LOGGER_PREFIX + "####### End #######");
}

function main() {

    var attrService = getExecutionParameter("msgTemplate", "string");
    if (attrService) {
        logger.info(" ********** Start template ******:" + attrService);
        sendMessage("01-Fachportal-SST", attrService);
    } else {
        logger.error(" ********** Wrong template ******:" + attrService);
    }
}

function getExecutionParameter(identifier, asType) {
    if (parameters.containsKey(identifier) && parameters.get(identifier) != null) {
        var value = parameters.get(identifier);

        logger.info("Attribute with identifier: " + identifier + " with value: " + value);
        if ("long".equalsIgnoreCase(asType))
            return java.lang.Long(value);
        else if ("string".equalsIgnoreCase(asType))
            return java.lang.String(value);
        else
            return value;
    }
    return null;
}

function sendMessage(msgNavIdentifier, msgIdentifier) {

    var roleList = new java.util.ArrayList();
    var templateFilePath = "[filePath]";
    var templateMessage = "[message]";
    var templateCatalogs="[catalogs]";
    var valueFilePath = getExecutionParameter("filePath", "string");
    var valueMessage = getExecutionParameter("message", "string");
    var valueSubject = getExecutionParameter("msgTemplate", "string");
    var catalogs = getExecutionParameter("catalogs", "string");
    var valueFileName = getExecutionParameter("fileName", "string");
    var valueMap = getMsgValues(msgNavIdentifier, msgIdentifier);

    var recipients = valueMap.get("recipient").replace(", ", ",").replace(" , ", ",").replace(" ,", ",");
    recipients = recipients.split(",");
    for (var j = 0; j < recipients.length; j++) {
        roleList.add(recipients[j]);
    }
    var roles = "";
    for (var i = 0; i < roleList.size(); i++) {
        if (i == 0) {
            roles += roleList.get(i);
        } else {
            roles += ", " + roleList.get(i);
        }
    }

    var messageSeverity = valueMap.get("severity");
    logger.error("messageSeverity: " + messageSeverity);
    if (messageSeverity == "[Error]") {
        messageSeverity = "Error";
    } else if (messageSeverity == "[Warn]") {
        messageSeverity = "Warn";
    }
    else {
        messageSeverity = "Info";
    }
    if (valueFileName) {
        valueSubject = valueSubject + " [" + valueFileName + "]";
    }


    var messageBodyInformation = valueMap.get("text");

    if (valueFilePath) {
        messageBodyInformation = messageBodyInformation.replace(templateFilePath, valueFilePath);
    } else {
        messageBodyInformation = messageBodyInformation.replace(templateFilePath, " ");
    }


    if (catalogs) {
        messageBodyInformation = messageBodyInformation.replace(templateCatalogs, catalogs);
    } else {
        messageBodyInformation = messageBodyInformation.replace(templateCatalogs, " ");
    }

    if (valueMessage) {
        messageBodyInformation = messageBodyInformation.replace(templateMessage, valueMessage);

    } else {
        messageBodyInformation = messageBodyInformation.replace(templateMessage, " ");
    }


    var messageBodyGreeting = configurationHelper.getConfigurationSetting("SENDMAIL_MESSAGE_OPENING");
    var messageBodyEnding = configurationHelper.getConfigurationSetting("SENDMAIL_MESSAGE_CLOSING");
    var messageBody = messageBodyGreeting + "\n\r" + messageBodyInformation + "\n\r" + messageBodyEnding;

    var messageType = "TEXT";
    var messageSenderPlugin = valueMap.get("senderplugin")
    var messageSenderUser = "mm-admin";

    var messageSenderApp = valueMap.get("senderprogramm");

    var filePath = getExecutionParameter("filePath", "string");

    var tempMap = new java.util.LinkedHashMap();
    tempMap.put("Rollen", roles);

    messageHelper.sendOmnMessage(valueSubject, messageBody, messageType, messageSeverity, mandatorId, roleList, null, messageSenderApp, messageSenderPlugin, null, tempMap);
//       cpFacade.sendMessage( valueSubject , messageBody , messageType , messageSeverity, mandatorId,null , "ags_import" , messageSenderApp, messageSenderPlugin, null, tempMap);
}

function getMsgValues(msgNavIdentifier, msgIdentifier) {
    var confId = cpFacade.findProjectByIdentifier("Configuration").getIdentity();
    var parentPrj = cpFacade.getChildren(confId);
    var parentPrjId;
    var messageChildren;
    var msgSeverity;
    var msgSubject;
    var msgText;
    var msgRecipient;
    var msgSenderprogramm;
    var msgSenderplugin;

    for (var i = 0; i < parentPrj.size(); i++) {
        if (parentPrj.get(i).getDisplayName() == msgNavIdentifier) {
            parentPrjId = parentPrj.get(i).getIdentity();
            messageChildren = cpFacade.getChildren(parentPrjId);
        }
    }

    if (messageChildren != null) {
        for (var j = 0; j < messageChildren.size(); j++) {
            var identifier = cpFacade.getMetaData(messageChildren.get(j).getIdentity(), "MSG_IDENTIFIER").getvString();
            logger.error("identifier: " + identifier + " id: " + messageChildren.get(j).getIdentity());
            if (identifier == msgIdentifier) {
                msgSeverity = cpFacade.getMetaData(messageChildren.get(j).getIdentity(), "MSG_PRIORITY").getvEnum();
                msgSubject = cpFacade.getMetaData(messageChildren.get(j).getIdentity(), "MSG_SUBJECT").getvString();
                msgText = cpFacade.getMetaData(messageChildren.get(j).getIdentity(), "MSG_TEXT").getvString();
                msgRecipient = cpFacade.getMetaData(messageChildren.get(j).getIdentity(), "MSG_RECIPIENT").getvString();
                msgSenderprogramm = cpFacade.getMetaData(messageChildren.get(j).getIdentity(), "MSG_SENDERPROGRAM").getvString();
                msgSenderplugin = cpFacade.getMetaData(messageChildren.get(j).getIdentity(), "MSG_SENDERPLUGIN").getvString();
            }
        }
    }
    var valueMap = new java.util.HashMap();
    valueMap.put("severity", msgSeverity);
    valueMap.put("subject", msgSubject);
    valueMap.put("text", msgText);
    valueMap.put("recipient", msgRecipient);
    valueMap.put("senderprogramm", msgSenderprogramm);
    valueMap.put("senderplugin", msgSenderplugin);

    return valueMap;
}
