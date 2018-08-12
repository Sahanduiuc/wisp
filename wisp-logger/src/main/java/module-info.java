module wisp.logger {
    exports wisp.logger.api;

    requires wisp.api;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires slf4j.api;

    provides wisp.api.ServiceModule with wisp.logger.Log4j2LoggerFactory;
}