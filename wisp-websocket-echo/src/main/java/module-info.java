module wisp.websocket.echo {
    requires java.net.http;
    requires wisp.websocket;

    provides wisp.websocket.api.WebSocketService with wisp.websocket.echo.EchoWebSocketService;
}