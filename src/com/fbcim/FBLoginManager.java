package com.fbcim;

import com.fbcim.chat.FBConnectionConfiguration;
import com.fbcim.ui.FBLoginFrame;
import com.fbcim.ui.FBLoginFrameListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

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
public class FBLoginManager
    implements FBLoginFrameListener {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBLoginManager.class.getName());


    enum FBLoginState {
        LOGIN_QUEUED,
        FB_LOGIN_STARTED,
        FB_LOGIN_SUCCESSFUL,
        CHAT_LOGIN_STARTED,
        CHAT_LOGIN_SUCCESSFUL,
        LOGIN_FAILED,
        LOGIN_CANCELED,
    }

    /** Target host. */
    public static final String CHAT_SERVER_HOST = "chat.facebook.com";

    /** Target port. */
    public static final int CHAT_SERVER_PORT = 5222;

    /** Service name. */
    public static final String SERVICE_NAME = "chat.facebook.com";

    public static final String API_KEY = "307694312620146";


    /** The app. context to work with. */
    private FBChatContext context;

    /** Login manager listener. */
    private FBLoginManagerListener listener;

    /** Keeps current login state. */
    private FBLoginState state;

    /** Actual login frame with built in browser. */
    private FBLoginFrame fbLoginFrame;

    /** <code>true</code> if silent login should be performed. */
    private boolean silentLogin = false;

    /**
     * Constructs login manager and attaches given listener to it.
     *
     * @param context
     *          the app. context to work with.
     * @param l
     *          the login manager listener to attach.
     */
    public FBLoginManager(FBChatContext context, FBLoginManagerListener l) {
        // Save properties.
        this.context = context;
        this.listener = l;

        // Construct facebook login frame.
        this.fbLoginFrame = new FBLoginFrame(context, this);

        // Set default login state.
        setState(FBLoginState.LOGIN_QUEUED);
    }

    public FBLoginFrame getLoginFrame() {
        return fbLoginFrame;
    }

    public void setSilentLogin(boolean newVal) {
        this.silentLogin = newVal;
    }

    /**
     * Initializes facebook login process.
     */
    public void login() {
        if (isLoginInPogress()) {
            return;
        }
        String oAuthToken = context.getSettings().getOAuthToken();
        if (silentLogin && (oAuthToken != null) && !oAuthToken.equals("")) {
            setState(FBLoginState.CHAT_LOGIN_STARTED);
        } else {
            setState(FBLoginState.FB_LOGIN_STARTED);
        }
    }

    /**
     * Tests if login to facebook is in progress.
     *
     * @return  <code>true</code> if login is in progress,
     *          <code>false</code> otherwise.
     */
    private boolean isLoginInPogress() {
        switch (state) {
            case FB_LOGIN_STARTED:
            case FB_LOGIN_SUCCESSFUL:
            case CHAT_LOGIN_STARTED:
            case CHAT_LOGIN_SUCCESSFUL:
                return true;

            default:
                return false;
        }
    }

    /**
     * Initialize login to facebook in order to get oauth token.
     */
    private void doFBLogin() {
        fbLoginFrame.setVisible(true);
        fbLoginFrame.toFront();
    }

    /**
     * Initialize login to facebook chat.
     */
    private void doFBChatLogin() {
        ConnectionConfiguration config = new ConnectionConfiguration("chat.facebook.com", 5222);
        config.setSASLAuthenticationEnabled(true);

        SmackConfiguration.setPacketReplyTimeout(15000);

        XMPPConnection connection = new XMPPConnection(new FBConnectionConfiguration(CHAT_SERVER_HOST, CHAT_SERVER_PORT, SERVICE_NAME));
        try {
            connection.connect();
            connection.login(API_KEY, context.getSettings().getOAuthToken(), "Application");
            context.setXmppConnection(connection);
            context.updateXMPPStatus(null);
            setState(FBLoginState.CHAT_LOGIN_SUCCESSFUL);
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to login to facebook XMPP chat!", t);
            setState(FBLoginManager.FBLoginState.LOGIN_FAILED);
        }
    }

    /**
     * Resets oauth token.
     */
    private void failLogin() {
        context.getSettings().setOAuthToken("");
        listener.loginFailed();
    }

    /**
     * Failed to login to facebook and receive oauth token.
     *
     * @param t
     *          the optional exception object.
     */
    public void fbLoginError(Throwable t) {
        setState(FBLoginState.LOGIN_FAILED);
    }

    /**
     * Invoked if user canceled login to facebook.
     */
    public void fbLoginCanceled() {
        setState(FBLoginState.LOGIN_CANCELED);
    }

    /**
     * OAuth access token received.
     *
     * @param accessToken
     *          the received access token.
     */
    public void fbAccessTokenReceived(String accessToken) {
        context.getSettings().setOAuthToken(accessToken);
        setState(FBLoginState.FB_LOGIN_SUCCESSFUL);
    }

    /**
     * Sets current facebook login state.
     *
     * @param newVal
     *          the facebook login state to set.
     */
    private synchronized void setState(FBLoginState newVal) {
        // Make sure we really change the state.
        if ((state != null) && state.equals(newVal)) {
            return;
        }

        this.state = newVal;
        switch (state) {
            case LOGIN_QUEUED:
                break;

            case FB_LOGIN_STARTED:
                doFBLogin();
                break;

            case FB_LOGIN_SUCCESSFUL:
            case CHAT_LOGIN_STARTED:
                doFBChatLogin();
                break;

            case CHAT_LOGIN_SUCCESSFUL:
                listener.loginSuccessful(!silentLogin);
                setSilentLogin(false);
                break;

            case LOGIN_FAILED:
                setSilentLogin(false);
                failLogin();
                break;

            case LOGIN_CANCELED:
                setSilentLogin(false);
                listener.loginCanceled();
                break;
        }
    }
}
