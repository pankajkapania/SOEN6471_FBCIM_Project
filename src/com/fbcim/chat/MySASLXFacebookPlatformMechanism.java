package com.fbcim.chat;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
/*  Facebook Chat Instant Messenger
    Copyright (C) 2012 Ori Rejwan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
public class MySASLXFacebookPlatformMechanism extends SASLMechanism {
    private static final String NAME              = "X-FACEBOOK-PLATFORM";

    private String              apiKey            = "";
    private String              accessToken        = "";

    /**
     * Constructor.
     */
    public MySASLXFacebookPlatformMechanism(SASLAuthentication saslAuthentication) {
        super(saslAuthentication);
    }

    @Override
    protected void authenticate() throws IOException, XMPPException {
        getSASLAuthentication().send(new SASLMechanism.AuthMechanism(NAME, ""));
    }

    @Override
    public void authenticate(String apiKey, String host, String accessToken) throws IOException, XMPPException {
        if (apiKey == null || accessToken == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        this.apiKey = apiKey;
        this.accessToken = accessToken;
        this.hostname = host;

        String[] mechanisms = { "DIGEST-MD5" };
        Map<String, String> props = new HashMap<String, String>();
        this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, this);
        authenticate();
    }

    @Override
    public void authenticate(String username, String host, CallbackHandler cbh) throws IOException, XMPPException {
        String[] mechanisms = { "DIGEST-MD5" };
        Map<String, String> props = new HashMap<String, String>();
        this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, cbh);
        authenticate();
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    public void challengeReceived(String challenge) throws IOException {
        byte[] response = null;

        if (challenge != null) {
            String decodedChallenge = new String(Base64.decode(challenge));
            Map<String, String> parameters = getQueryMap(decodedChallenge);

            String version = "1.0";
            String nonce = parameters.get("nonce");
            String method = parameters.get("method");

            String composedResponse =
                "method=" + URLEncoder.encode(method, "utf-8") +
                        "&nonce=" + URLEncoder.encode(nonce, "utf-8") +
                        "&access_token=" + URLEncoder.encode(accessToken, "utf-8") +
                        "&api_key=" + URLEncoder.encode(apiKey, "utf-8") +
                        "&call_id=0" +
                        "&v=" + URLEncoder.encode(version, "utf-8");
            response = composedResponse.getBytes();
        }

        String authenticationText = "";

        if (response != null) {
            authenticationText = Base64.encodeBytes(response);
        }

        // Send the authentication to the server
        getSASLAuthentication().send(new Response(authenticationText));
    }

    private Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<String, String>();
        String[] params = query.split("\\&");

        for (String param : params) {
            String[] fields = param.split("=", 2);
            map.put(fields[0], (fields.length > 1 ? fields[1] : null));
        }

        return map;
    }
}