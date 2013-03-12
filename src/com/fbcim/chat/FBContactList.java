package com.fbcim.chat;

import com.fbcim.FBChatContext;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class FBContactList {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBContactList.class.getName());

    /** The app. context to work with. */
    private FBChatContext context;

    /** The currently logged in user info. */
    private VCard vCard;

    /** Keeps actual contacts (contact_id -> contact). */
    private Map<String, FBContact> contacts;

    /** The list of registered listeners. */
    private Set<FBContactListListener> listeners;

    private boolean signOut = false;

    /**
     * Constructs contact list object instance.
     *
     * @param context
     *          the app. context to work with.
     */
    public FBContactList(FBChatContext context) {
        this.context = context;
        this.contacts = Collections.synchronizedMap(new HashMap<String,FBContact>());
        this.listeners = Collections.synchronizedSet(new HashSet<FBContactListListener>());
    }

    /**
     * Loads list of contacts from server.
     */
    public void loadContactList() {
        Roster roster = context.getRoster();

        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            Presence presence = roster.getPresence(entry.getUser());
            if ((presence.getStatus() == null) || presence.getStatus().equals("")) {
                if (signOut) {
                    break;
                }
            }
            FBContact contact = new FBContact(entry, presence);
            if (signOut) {
                break;
            }
            contact.loadAvatar(context.getXmppConnection(), context.getAvatarCache());
            contacts.put(contact.getId(), contact);
            fireContactAdded(contact);
        }


    }

    /**
     * Updates status of all online contacts in the list.
     */
    public void updateContactsFBStatus(boolean skipOffline) {
        for (FBContact contact : contacts.values()) {
            if (skipOffline && contact.isOffline()) {
                continue;
            }
            String status = context.getFBConnection().getUserStatus(contact.getId());
            String oldStatus = contact.getPresence().getStatus();
            if (!status.equals(oldStatus)) {
                contact.getPresence().setStatus(status);
                fireContactUpdated(contact);
            }
        }
    }

    /**
     * Notifies registered listeners that given contact was added to the list.
     *
     * @param contact
     *          the contact added to the list.
     */
    private void fireContactAdded(FBContact contact) {
        synchronized (listeners) {
            for (FBContactListListener l : listeners) {
                try {
                    l.contactAdded(contact);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener about added contact!", t);
                }
            }
        }
    }

    /**
     * Notifies registered listeners that given contact was added to the list.
     *
     * @param contact
     *          the contact added to the list.
     */
    private void fireContactUpdated(FBContact contact) {
        synchronized (listeners) {
            for (FBContactListListener l : listeners) {
                try {
                    l.contactUpdated(contact);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener about contact update!", t);
                }
            }
        }
    }

    public void setPresence(Presence presence) {
        FBContact contact = contacts.get(presence.getFrom());
        if (contact != null) {
            String statusStr = presence.getStatus();
            if ((statusStr == null) || statusStr.equals("")) {
                // Update status text from facebook wall.
                presence.setStatus(context.getFBConnection().getUserStatus(contact.getId()));
            }
            contact.setPresence(presence);
            context.showTrayPopup(contact);
            fireContactUpdated(contact);
        }
    }

    /**
     * Returns number of contacts in the list.
     *
     * @return  the number of contacts in the list.
     */
    public int getSize() {
        return contacts.size();
    }

    /**
     * Returns list of contacts.
     *
     * @return  the list of contacts.
     */
    public List<FBContact> getContacts() {
        return Collections.unmodifiableList(new ArrayList<FBContact>(contacts.values()));
    }


    /**
     * Returns contact with given id
     *
     * @param contactId
     *          the contact id.
     *
     * @return  the contact with given id (or <code>null</code> if nothing was found).
     */
    public FBContact getFBContact(String contactId) {
        return contacts.get(contactId);
    }


    /**
     * Registers a listener.
     *
     * @param l
     *          the listener that receives all notification events
     */
    public void addContactListListener(FBContactListListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public void clear() {
        signOut = true;
        vCard = null;
        if  (contacts != null) {
            contacts.clear();
//            contacts = null;
        }
        if (listeners != null) {
            listeners.clear();
//            listeners = null;
        }
    }
}
