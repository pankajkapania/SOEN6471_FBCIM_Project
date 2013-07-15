package com.fbcim.chat;

import com.fbcim.FBChatContext;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

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
public class FBChatPacketListener implements PacketListener {
    /** The app. context to work with. */
    private FBChatContext context;

    /**
     * Constructs incoming packet listener instance.
     *
     * @param context
     *          the app. context to work with.
     */
    public FBChatPacketListener(FBChatContext context) {
        this.context = context;
    }

    /**
     * Processes given packet.
     *
     * @param packet
     *          the packet to process.
     */
    public void processPacket(Packet packet) {
        //Contact who sends the packet
        FBContact contact = context.getFBContact(packet.getFrom());

        if (contact != null) {
            // Managing a message.
            Message msg = (Message) packet;

            // Forward received message to the chat.
            FBChat chat = context.chat(contact);
            if (chat != null) {
                chat.messageReceived(msg);
            }
        }
    }
}
