/*
 * mxpsd - Matrix Push Server Daemon
 * Copyright (C) 2019 Kamax Sarl
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.mxpsd;

import io.kamax.matrix.json.GsonUtil;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpPushServer {

    private PushServer ps;
    private Undertow http;

    private class NotifyHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange exchange) throws IOException {
            exchange.startBlocking();
            if (exchange.isInIoThread()) {
                exchange.dispatch(this);
            } else {
                handleRequestBlocking(exchange);
            }
        }

        private void handleRequestBlocking(HttpServerExchange exchange) throws IOException {
            String body = IOUtils.toString(exchange.getInputStream(), StandardCharsets.UTF_8);
            ps.notify(GsonUtil.parseObj(body));
        }

    }

    public HttpPushServer() {
        ps = new PushServer();

        HttpHandler handler = Handlers.routing().post("/_matrix/push/v1/notify", new NotifyHandler());
        http = Undertow.builder().addHttpListener(8558, "0.0.0.0").setHandler(handler).build();
    }

    public void run() {
        http.start();
    }

}
