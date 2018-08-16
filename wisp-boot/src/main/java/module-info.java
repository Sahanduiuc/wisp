import wisp.boot.HoconConfigurationFactory;
import wisp.boot.PropertiesConfigurationFactory;

module wisp.boot {
   requires wisp.api;

   requires args4j;
   requires cloudwall.graph;
   requires com.google.common;
   requires jsr305;
   requires typesafe.config;

   opens wisp.boot to args4j;

   uses wisp.api.ConfigurationFactory;
   uses wisp.api.LogInitializer;
   uses wisp.api.ServiceModule;

   provides wisp.api.ConfigurationFactory
           with HoconConfigurationFactory, PropertiesConfigurationFactory;
}