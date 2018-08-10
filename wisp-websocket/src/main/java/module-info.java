module wisp.websocket {
    exports wisp.websocket.api;

    requires jdk.incubator.httpclient;
    requires wisp.api;
    requires wisp.logger;
    requires slf4j.api;
    requires typesafe.config;

    provides wisp.api.ServiceModule with wisp.websocket.WebsocketServer;
    uses wisp.websocket.api.WebSocketService;
}