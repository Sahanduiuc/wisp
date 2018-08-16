module wisp.websocket.echo {
    requires jdk.incubator.httpclient;
    requires wisp.websocket;

    provides wisp.websocket.api.WebSocketService with wisp.websocket.echo.EchoWebSocketService;
}