import wisp.boot.HoconConfigurationFactory;

module wisp.boot {
   requires wisp.api;

   requires com.google.common;
   requires jsr305;
   requires typesafe.config;

   uses wisp.api.ConfigurationFactory;
   uses wisp.api.ServiceModule;

   provides wisp.api.ConfigurationFactory with HoconConfigurationFactory;
}