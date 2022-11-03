var WFL_JAVASCRIPT_NAME = "TestCheckin";
var LOGGER_NAME =  mandatorName + ".Workflows." + WFL_JAVASCRIPT_NAME;
var LOGGER_PREFIX = WFL_JAVASCRIPT_NAME + ": ";
var LOGGER_FILE = "/usr/local/omn/logs/" + mandatorName + "/workflows/" + WFL_JAVASCRIPT_NAME + ".log";

// Logger configuration
var Log4j = Packages.org.apache.log4j;
var loggerExists = Log4j.LogManager.exists(LOGGER_NAME);
var logger = Log4j.Logger.getLogger(LOGGER_NAME);
if (!loggerExists)
{
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


var jsFacade = entryFacade.lookupBean("ISYObjectJSFacade");
var isyObjectFacade = entryFacade.lookupBean("ISYObjectFacade");

try
{
    logger.info(LOGGER_PREFIX + "####### Starting #######");
    main();
}
catch (e)
{
    result = "ERROR";
    var javaException = e.javaException;
    if (javaException)
    {
        logger.error(LOGGER_PREFIX + "Java exception caught", javaException);
    }
    else
    {
        var rhinoException = e.rhinoException;
        if (rhinoException)
        {
            logger.error(LOGGER_PREFIX + "Rhino exception caught" + rhinoException.getMessage());
        }
        else
        {
            logger.error(LOGGER_PREFIX + "Unknown exception caught" + e.toString());
        }
    }
}
finally
{
    logger.info(LOGGER_PREFIX + "####### End #######");
}

function main()
{
    var filePath = getExecutionParameter("filePath", "string");
    if (filePath){
        logger.info("Run checkin file:"+filePath);
        getSynchronizer().checkIn(filePath);
    }

    var fileName = getExecutionParameter("fileName", "string");
    if (fileName) {
        logger.info("Run un-checkin file:"+fileName);
        var folderprefix = fileName.split('ags_web_prod/daten');
        var jsFacade = entryFacade.lookupBean("ISYObjectJSFacade");
        logger.info("Processed file to delete " + folderprefix[1]);
        var entity = jsFacade.getISYObject("ags_web_prod", folderprefix[1])
        if (entity){
            logger.info("Try to delete file: "+ fileName);
            isyObjectFacade.deleteISYObject(entity);
        }
        else {
            logger.error("Delete failed file: "+ fileName);
        }


        logger.info("Deleted");
    }



}

function getExecutionParameter( identifier , asType )
{
    if ( parameters.containsKey( identifier ) && parameters.get( identifier ) != null )
    {
        var value = parameters.get( identifier );

        logger.info( "Attribute with identifier: " + identifier + " with value: " + value );
        if ( "long".equalsIgnoreCase( asType ) )
            return java.lang.Long( value );
        else if ( "string".equalsIgnoreCase( asType ) )
            return java.lang.String( value );
        else
            return value;
    }
    return null;
}

function getSynchronizer()
{
    return com.example.isy.sync.service.SynchronizerManagerFactory.getInstance().getSynchronizer();
}
