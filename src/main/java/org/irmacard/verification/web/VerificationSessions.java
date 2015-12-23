/*
 * VerificationSessions.java
 *
 * Copyright (c) 2015, Sietse Ringers, Radboud University
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

package org.irmacard.verification.web;

import java.util.HashMap;
import java.util.Map.Entry;

public class VerificationSessions {
    private static VerificationSessions vs = null;

    public static VerificationSessions getInstance() {
        if (vs == null) {
            vs = new VerificationSessions();
        }
        return vs;
    }

    private HashMap<String, VerificationSession> sessions;

    public VerificationSessions() {
        sessions = new HashMap<String, VerificationSession>();
    }

    public void addSession(VerificationSession session) {
        sessions.put(session.getSessionToken(), session);
    }

    public VerificationSession getSession(String sessionToken) {
        return sessions.get(sessionToken);
    }

    public void remove(VerificationSession session) {
        sessions.remove(session.getSessionToken());
    }

    public void print() {
        System.out.println("Active verification sessions:");
        for (Entry<String, VerificationSession> pairs : sessions.entrySet()) {
            String key = pairs.getKey();
            VerificationSession session = pairs.getValue();

            System.out.println(key + ": " + session);
        }
    }
}
