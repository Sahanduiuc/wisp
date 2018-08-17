/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wisp.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import wisp.websocket.api.WebSocketService;

import java.util.Map;

/**
 * Helper to set up Netty channel pipeline for HTTP, HTTPS and Websockets.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final Map<String, WebSocketService> servicePaths;

    WebSocketServerInitializer(SslContext sslCtx, Map<String, WebSocketService> servicePaths) {
        this.sslCtx = sslCtx;
        this.servicePaths = servicePaths;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());

        for (var path : servicePaths.keySet()) {
            var wss = servicePaths.get(path);
            pipeline.addLast(new WebSocketServerProtocolHandler(path, null, true));
            pipeline.addLast(new WebSocketFrameHandler(wss));
        }
    }
}
