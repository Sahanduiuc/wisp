module wisp.boot {
   requires wisp.api;

   requires com.google.common;
   requires jsr305;
   requires typesafe.config;
   
   uses wisp.api.ServiceModule;
}