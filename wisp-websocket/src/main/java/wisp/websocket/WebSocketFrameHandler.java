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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import wisp.websocket.api.WebSocketService;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * Handler which dispatches incoming WebSocketFrames to handler code.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final WebSocketService wss;

    WebSocketFrameHandler(WebSocketService wss) {
        this.wss = wss;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // ping and pong frames already handled
        if (frame instanceof TextWebSocketFrame) {
            wss.onText(new WebSocketImpl(ctx), ((TextWebSocketFrame) frame).text(), true);
        } else if (frame instanceof BinaryWebSocketFrame) {
            wss.onBinary(new WebSocketImpl(ctx), frame.content().nioBuffer(), true);
        } else if (frame instanceof CloseWebSocketFrame) {
            wss.onClose(new WebSocketImpl(ctx), ((CloseWebSocketFrame) frame).statusCode(),
                    ((CloseWebSocketFrame) frame).reasonText());
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    private class WebSocketImpl implements WebSocket {
        private final ChannelHandlerContext ctx;

        WebSocketImpl(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public CompletableFuture<WebSocket> sendText(CharSequence message, boolean isLast) {
            return writeFrameLater(new TextWebSocketFrame(message.toString()));
        }

        @Override
        public CompletableFuture<WebSocket> sendBinary(ByteBuffer message, boolean isLast) {
            return writeFrameLater(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(message)));
        }

        @Override
        public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
            return writeFrameLater(new PingWebSocketFrame(Unpooled.wrappedBuffer(message)));
        }

        @Override
        public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
            return writeFrameLater(new PongWebSocketFrame(Unpooled.wrappedBuffer(message)));
        }

        @Override
        public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
            return writeFrameLater(new CloseWebSocketFrame(statusCode, reason));
        }

        @Override
        public void request(long n) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSubprotocol() {
            return null;
        }

        @Override
        public boolean isOutputClosed() {
            return false;
        }

        @Override
        public boolean isInputClosed() {
            return false;
        }

        @Override
        public void abort() {
            throw new UnsupportedOperationException();
        }

        private CompletableFuture<WebSocket> writeFrameLater(WebSocketFrame frame) {
            var thisFuture = new CompletableFuture<WebSocket>();
            ctx.channel().writeAndFlush(frame).addListener(future -> thisFuture.complete(WebSocketImpl.this));
            return thisFuture;
        }
    }
}