module wisp.logger {
    exports wisp.logger;

    requires wisp.api;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires slf4j.api;
    requires typesafe.config;

    provides wisp.logger.Slf4jLoggerFactory with wisp.logger.Log4j2Slf4jLoggerFactory;
}