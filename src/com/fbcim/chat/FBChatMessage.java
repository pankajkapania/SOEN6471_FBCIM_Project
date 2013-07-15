package com.fbcim.chat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
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
@XStreamAlias("message")
public class FBChatMessage {
    /** Sender id. */
    @XStreamAlias("from")
    @XStreamAsAttribute
    private String fromId;

    /** Receiver id */
    @XStreamAlias("to")
    @XStreamAsAttribute
    private String toId;

    /** Message raw text. */
    private String text;

    /** Message timestamp. */
    @XStreamAlias("timestamp")
    @XStreamAsAttribute
    private long timestamp;

    /**
     * Constrcuts a chat message.
     *
     * @param fromId
     *          the sender id.
     * @param toId
     *          the receiver id.
     * @param text
     *          the message raw text.
     * @param timestamp
     *          the message send timestamp.
     */
    public FBChatMessage(String fromId, String toId, String text, long timestamp) {
        this.fromId = fromId;
        this.toId = toId;
        this.text = text.trim();
        this.timestamp = timestamp;
    }

    /**
     * Returns sender id.
     *
     * @return  the sender id.
     */
    public String getFromId() {
        return fromId;
    }

    /**
     * Returns receiver id.
     *
     * @return  the receiver id.
     */
    public String getToId() {
        return toId;
    }

    /**
     * Returns raw message text.
     *
     * @return  the raw message text.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns message timestamp.
     *
     * @return  the message timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }
}