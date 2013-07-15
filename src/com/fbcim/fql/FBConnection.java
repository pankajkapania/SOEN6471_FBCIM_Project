package com.fbcim.fql;

import com.fbcim.chat.FBContactList;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;

import java.util.List;
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
public class FBConnection {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBConnection.class.getName());

    /** Facebook client instance to work with. */
    private final FacebookClient facebookClient;

    /**
     * Constructs facebook search object instance.
     *
     * @param accessToken
     *          the facebook access token to be used.
     */
    public FBConnection(String accessToken) {
        facebookClient = new DefaultFacebookClient(accessToken);
    }


    public void updateFBStatus(String status) {
        try {
            facebookClient.publish("/feed", String.class, Parameter.with("message", status));
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to post a message to the user's facebook wall!", t);
        }
    }

    public void disconnect() {
        // do nothing here.
    }

    /**
     * Retrieves current facebook wall status of user with given id.
     *
     * @param userId
     *          the ID of the user to get facebook wall status for.
     *
     * @return  the user status string.
     */
    public String getUserStatus(String userId) {
        try {
            // Extract actual facebook uid.
            userId = userId.substring(1, userId.indexOf("@"));
            Object obj = facebookClient.executeQuery("SELECT message FROM status WHERE uid  = " + userId + " ORDER BY time DESC LIMIT 1",
                    JsonObject.class,  Parameter.with("limit", 1));
            if (obj instanceof List) {
                List list = ((List)obj);
                if (list.size() > 0) {
                    return ((JsonObject)list.get(0)).getString("message");
                }
            }
            return "";
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to retrieve last wall message for contact " + userId, t);
            return "";
        }
    }
}
