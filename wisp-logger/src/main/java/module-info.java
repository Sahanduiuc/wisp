module wisp.logger {
    requires wisp.api;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires slf4j.api;

    provides wisp.api.LogInitializer with wisp.logger.Log4J2Initializer;
}