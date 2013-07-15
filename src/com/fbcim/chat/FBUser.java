package com.fbcim.chat;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;

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
public class FBUser {
    /** The actual roster entry. */
    private Roster roster;

    /** The current presence status. */
    private Presence presence;

    /**
     * Constructs new facebook chat contact.
     *
     * @param roster
     *          the actual user entry.
     * @param initialPresence
     *          the initial presence status of this user.
     */
    public FBUser(Roster roster, Presence initialPresence) {
        this.roster = roster;
        this.presence = initialPresence;
    }
}
