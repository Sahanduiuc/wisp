module wisp {
    exports wisp.api;

    requires jdk.incubator.httpclient;
    requires typesafe.config;

    uses wisp.api.ServiceModule;
    uses wisp.api.WebSocketService;
}