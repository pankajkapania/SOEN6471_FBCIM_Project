package com.fbcim.chat;

import com.fbcim.ui.AvatarCache;
import com.fbcim.ui.FBChatImages;
import com.fbcim.util.ImageUtil;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.packet.VCard;

import javax.swing.ImageIcon;

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
public class FBContact {
    /** Keeps the actual entry reference. */
    private RosterEntry entry;

    /** Keeps entry presence status. */
    private Presence presence;

    /** Contact virtual card. */
    private VCard vCard;

    /** Contact avatar (small). */
    private ImageIcon avatarSmall;

    /** Contact avatar (big). */
    private ImageIcon avatarBig;

    /** Contact avatar displayed in the system tray popup window. */
    private ImageIcon avatarTray;

    /**
     * Constructs new facebook chat contact.
     *
     * @param entry
     *          the actual user entry.
     * @param initialPresence
     *          the initial presence status of this user.
     */
    public FBContact(RosterEntry entry, Presence initialPresence) {
        this.vCard = new VCard();
        this.entry = entry;
        this.presence = initialPresence;
    }

    /**
     * Loads avatar for a given contact.
     *
     * @param connection
     *          the connection to use.
     * @param cache
     *          the avatar cache.
     */
    public void loadAvatar(Connection connection, AvatarCache cache) {
        ImageIcon rawAvatar = null;
        try {
            // First, try to load avatar from cache.
            rawAvatar = cache.getAvatarFromFile(this);

            if (rawAvatar == null) {
                byte[] avatarBytes = loadAvatarBytes(connection);
                if ((avatarBytes == null) || (avatarBytes.length == 0)) {
                    // Try loading avatar once again.
                    avatarBytes = loadAvatarBytes(connection);
                }
                if ((avatarBytes != null) && (avatarBytes.length > 0)) {
                    rawAvatar = new ImageIcon(avatarBytes);

                    // Put loaded avatar to cache.
                    cache.saveAvatar2File(rawAvatar, this);
                }
            }

            if (rawAvatar != null) {
                this.avatarSmall = ImageUtil.scaleAvatarSmall(rawAvatar);
                this.avatarBig = ImageUtil.scaleAvatarMid(rawAvatar);
                this.avatarTray = ImageUtil.scaleAvatarTray(rawAvatar);
            } else {
                // Init avatars with default values.
                this.avatarSmall = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_SMALL);
                this.avatarBig = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_MEDIUM);
                this.avatarTray = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_TRAY);
            }

        } catch(XMPPException e) {
            e.printStackTrace();

            // Init avatars with default values.
            this.avatarSmall = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_SMALL);
            this.avatarBig = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_MEDIUM);
            this.avatarTray = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_TRAY);
        }
    }

    private byte[] loadAvatarBytes(Connection connection)
        throws XMPPException{
        vCard.load(connection, entry.getUser());
        return vCard.getAvatar();
    }

    /**
     * Returns contact name.
     *
     * @return  the contact name.
     */
    public String getName() {
        return entry.getName();
    }

    public String getFirstName() {
        String fullName = getName();
        int i = fullName.indexOf(" ");
        if (i > 0) {
            fullName = fullName.substring(0, i);
        }
        return fullName;
    }

    /**
     * Returns contact user id (username).
     *
     * @return  the contact id (username).
     */
    public String getId() {
        return entry.getUser();
    }

    /**
     * Returns current presence status of the contact.
     *
     * @return  the contact presence status.
     */
    public Presence getPresence() {
        return presence;
    }

    public ImageIcon getAvatarSmall() {
        return avatarSmall;
    }

    public ImageIcon getAvatarBig() {
        return avatarBig;
    }

    public ImageIcon getAvatarTray() {
        return avatarTray;
    }

    /**
     * Returns current status text.
     *
     * @return  the current status text.
     */
    public String getStatusText() {
        RosterPacket.ItemStatus status = entry.getStatus();
        if (status == null) {
            return "";
        } else {
            return status.toString();
        }
    }

    /**
     * Sets current presence status of the contact.
     *
     * @param newVal
     *          the new status to set.
     */
    public void setPresence(Presence newVal) {
        this.presence = newVal;
    }

    /**
     * Tests if given contact is currently offline.
     *
     * @return  <code>true</code> if this contact is currently offline,
     *          <code>false</code> otherwise.
     */
    public boolean isOffline() {
        return (presence == null) || (!presence.isAway() && !presence.isAvailable());
    }

    /**
     * Tests if two contacts are equal.
     *
     * @param o
     *          the object to compare with this one.
     *
     * @return  <code>true</code> if two objects are equal,
     *          <code>false</code> otherwise.
     */
    public boolean equals(Object o) {
        return ((o instanceof FBContact) && ((FBContact)o).getId().equals(getId()));
    }

    public String toString() {
        try {
            return getName() + "(" + getId() + ")";
        } catch (Throwable t) {
            return null;
        }
    }
}