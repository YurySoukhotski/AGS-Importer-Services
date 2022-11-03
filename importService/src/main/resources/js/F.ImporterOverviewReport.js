/**
 * Create report from importe overview
 *
 * @author Yury Soukhotski <yury.soukhotski@intexsoft.by>
 * @lastUpdate 2018-04-20
 * @version 1.0
 */

var startTime = new java.util.Date( );

result = "error";

var LOGGER_NAME = "overviewreport";
var LOGGER_PREFIX = LOGGER_NAME + ": ";
var LOGGER_FILE = "/usr/local/omn/logs/" + LOGGER_NAME + ".log";

/*
 * Logger Konfiguration
 */
var Log4j = Packages.org.apache.log4j;
var loggerExists = Log4j.LogManager.exists( LOGGER_NAME );
var logger = Log4j.Logger.getLogger( LOGGER_NAME );

if ( !loggerExists )
{

    logger.setLevel( Log4j.Level.toLevel( Log4j.Level.INFO_INT ) );
    logger.setAdditivity( false ); // else it's also written to tomcat.log

    var rollingFileAppender = new Log4j.RollingFileAppender( );
    rollingFileAppender.setFile( LOGGER_FILE );
    rollingFileAppender.setAppend( true );
    rollingFileAppender.setBufferedIO( false );
    rollingFileAppender.setBufferSize( 4096 );
    rollingFileAppender.setMaxBackupIndex( 4 );

    // Default max size is 10MB...

    var layout = new Log4j.PatternLayout( "%d [%t] %-5p %C.%M() %c{1} - %m%n" );
    rollingFileAppender.setLayout( layout );

    rollingFileAppender.activateOptions( );

    logger.removeAllAppenders( );
    logger.addAppender( rollingFileAppender );
}
//----------------------------- FACADES -----------------------------
var cpFacade = entryFacade.lookupBean( "ClientProjectFacade" );
var wflFacade = entryFacade.lookupBean( "ClientWorkflowRuntimeFacade" );

var basicHelper = entryFacade.lookupBean( "basicJavaScriptHelper" );
var isyObjectFacade = entryFacade.lookupBean("ISYObjectFacade");


/////////////////////////// --- MAIN --- ////////////////////////////
try
{
    result = main( );
}
catch ( e )
{
    if ( basicHelper != null )
    {
        var exceptionResult = basicHelper.handleException( e );
        if ( exceptionResult != null )
        {
            logger.error( LOGGER_PREFIX + exceptionResult.toString( ) , exceptionResult.getException( ) );
            // Setting Error reason
        }
        else
        {
            logger.error( "Could not handle the exception! Please inform the supplier of BasicJavaScriptHelper to fix this!" );
        }
    }
    else
    {
        logger.error( "BasicHelper not ready!" , e );
    }
}
finally
{
    logger.info( LOGGER_PREFIX + "result=[" + result + "]" );
    logger.warn( LOGGER_PREFIX + "####### End #######" );
}
function main()

{
    var client;
    var method;
    try
    {
        client = new org.apache.commons.httpclient.HttpClient();
        method = new org.apache.commons.httpclient.methods.GetMethod("http://localhost:8080/getstatus");
        client.executeMethod(method);
    }
    catch ( e )
    {
        logger.error(e);
    }
    var text = method.getResponseBodyAsString();
    var HTML_START  ="<html><body>";
    var HTML_END = "</body></html>";
    return HTML_START + text+ HTML_END;
}

