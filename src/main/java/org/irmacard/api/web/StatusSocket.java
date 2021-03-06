/*
 * VerificationResource.java
 *
 * Copyright (c) 2015, Wouter Lueks, Radboud University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the IRMA project nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.irmacard.api.web;

import org.irmacard.api.web.sessions.IrmaSession;
import org.irmacard.api.web.sessions.Sessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/api/v2/status/{sessionToken}")
public class StatusSocket {
    private static Logger logger = LoggerFactory.getLogger(StatusSocket.class);

    private Session session;
    private RemoteEndpoint.Async remote;

    @OnClose
    public void onWebSocketClose(CloseReason closeReason) {
        this.session = null;
        this.remote = null;
        logger.info("WebSocket Close: " + closeReason.getCloseCode() + " "
                + closeReason.getReasonPhrase());
    }

    @OnOpen
    public void onWebSocketOpen(Session session,
            @PathParam("sessionToken") String sessionToken) {
        this.session = session;
        this.remote = this.session.getAsyncRemote();

        // Prevent websockets from being closed prematurely.
        session.setMaxIdleTimeout(0);

        logger.info("WebSocket Connect: " + session);

        // Store websocket connection in the corresponding session
        IrmaSession irmaSession = Sessions.findAnySession(sessionToken);
        if (irmaSession == null) {
            // TODO: Add some error handling here
            logger.error("Strange: session not yet setup");
        } else {
            irmaSession.setStatusSocket(this);
        }
    }

    /**
     * Messages sent by the client are not supported
     *
     * @param message
     *            The client-message
     * @param sessionToken
     *            The url-supplied sessionToken
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sessionToken") String sessionToken) {
        logger.info("Received message from client: " + message);

        if (this.session != null && this.session.isOpen() && this.remote != null) {
            this.remote.sendText("NOT SUPPORTED");
        }
    }

    /**
     * Informs the client-website that a token has connected to the verification
     * server.
     */
    public void sendConnected() {
        sendUpdate("CONNECTED");
    }

    /**
     * Inform the client-website that the token has completed the verification.
     */
    public void sendDone() {
        sendUpdate("DONE");
    }

    /**
     * Inform the client website that the token rejected the verification
     */
    public void sendCancelled() {
        sendUpdate("CANCELLED");
    }

    public void sendTimeout() {
        sendUpdate("TIMEOUT");
    }

    /**
     * Returns whether the status socket is still connected.
     *
     * @return if the status socket is open
     */
    public boolean isSocketConnected() {
        return remote != null;
    }

    /**
     * Closes the statusSocket
     */
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) { /* ignore, connection is gone anyway */ }
        }

    }

    private void sendUpdate(String msg) {
        if (remote != null) {
            remote.sendText(msg);
        } else {
            logger.warn("StatusSocket.sendUpdate() called but no websocket registered");
        }
    }
}