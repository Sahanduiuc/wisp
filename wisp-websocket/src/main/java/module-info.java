module wisp.websocket {
    exports wisp.websocket.api;

    requires jdk.incubator.httpclient;
    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.codec.http;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.resolver;
    requires io.netty.transport;
    requires slf4j.api;
    requires wisp.api;
    requires wisp.logger;

    provides wisp.api.ServiceModule with wisp.websocket.WebsocketServer;
    uses wisp.websocket.api.WebSocketService;
}