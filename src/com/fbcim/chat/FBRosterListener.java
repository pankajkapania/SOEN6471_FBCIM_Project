package com.fbcim.chat;

import java.util.Collection;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import com.fbcim.FBChatContext;

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
public class FBRosterListener implements RosterListener {

    /** The app. context to work with. */
    private FBChatContext context;

    /**
     * Constructs roster listener object instance.
     *
     * @param context
     *          the app. context to work with.
     */
    public FBRosterListener(FBChatContext context) {
        this.context = context;
    }

    /**
     * Invoked when presence status of a contact in the contact list changes.
     *
     * @param presence
     *          the presence change event.
     */
    public void presenceChanged(Presence presence) {
        context.getContactList().setPresence(presence);
    }

    public void entriesAdded(Collection<String> strings) {}

    public void entriesUpdated(Collection<String> strings) {}

    public void entriesDeleted(Collection<String> strings) {}
}
