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

package wisp.websocket.echo;

import jdk.incubator.http.WebSocket;
import wisp.websocket.api.WebSocketService;

import java.util.Locale;
import java.util.concurrent.CompletionStage;

/**
 * Simple websocket service that echoes back its input.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class EchoWebSocketService implements WebSocketService {
    @Override
    public String getPath() {
        return "/echo";
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence message, WebSocket.MessagePart part) {
        webSocket.sendText(message.toString().toUpperCase(Locale.US), true);
        return null;
    }
}
