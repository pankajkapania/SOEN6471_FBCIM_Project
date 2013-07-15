package com.fbcim.chat;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fbcim.FBChatContext;
import com.fbcim.ui.FBChatFrame;
import com.fbcim.ui.FBChatStrings;
import com.fbcim.ui.Msg;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

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
public class FBChat {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBChat.class.getName());

    /** The app. context to work with. */
    private FBChatContext context;

    /** The contact you chat with. */
    private FBContact contact;

    /** The chat frame associated with this chat. */
    private FBChatFrame chatFrame;

    /** The actual chat proxy. */
    private Chat chat;

    /** The listener stub. */
    private FBChatMessageListener listener;

    /** <code>true</code> if user haven't sent outgoing messages to this chat yet. */
    private boolean isFirstOutgoingMessage = true;

    /**
     * Constructs new chat object.
     *
     * @param context
     *          the app. context to work with.
     * @param contact
     *          the contact you are going to chat with.
     */
    public FBChat(FBChatContext context, FBContact contact) {
        this.context = context;
        this.contact = contact;
        this.chatFrame = new FBChatFrame(context, contact, this);
        this.chatFrame.setSize(FBChatFrame.DEFAULT_CHAT_FRAME_WIDTH, FBChatFrame.DEFAULT_CHAT_FRAME_HEIGHT);
        this.chatFrame.setLocationByPlatform(true);

        // Create chat proxy object.
        chat = context.getXmppConnection().getChatManager().createChat(
                contact.getId(), new FBChatMessageListener()
        );
    }

    public void updateContactPresence() {
        this.chatFrame.updateContactPresence();
    }

    /**
     * Returns contact associated with this chat.
     *
     * @return  the contact associated with this chat.
     */
    public FBContact getContact() {
        return contact;
    }

    /**
     * Returns chat window associated with this chat.
     *
     * @return  the chat window associated with this chat.
     */
    public FBChatFrame getChatFrame() {
        return chatFrame;
    }

    /**
     * Sets message history for this chat.
     *
     * @param history
     *          the message history to set.
     */
    public void setHistory(List<FBChatMessage> history) {
        chatFrame.setHistory(history);
    }

    public List<FBChatMessage> getHistory() {
        return chatFrame.getHistory();
    }

    /**
     * Sends text message to the chat.
     *
     * @param msg
     *          the message to send.
     */
    public void sendText(String msg) {
        try {
            if (isFirstOutgoingMessage) {
                msg = String.format(Msg.getString(FBChatStrings.ADVERT_TEXT), context.getFirstName()) + msg;
                isFirstOutgoingMessage = false;
            }
            chat.sendMessage(msg);
        } catch (XMPPException ex) {
            LOG.log(Level.SEVERE, "Failed to send text message!", ex);
        }
    }

    /**
     * Notifies other party that you are typing a message.
     */
    public void keyTyped() {
        try {
            chat.sendMessage(new Message(null));
        } catch (XMPPException ex) {
            LOG.log(Level.SEVERE, "Failed to notify other party that user is typing now!", ex);
        }
    }

    /**
     * Adds incoming message.
     *
     * @param msg
     *          the message to append.
     */
    public void messageReceived(Message msg) {
        chatFrame.messageReceived(msg);
    }

    /**
     * Listens to incoming chat messages.
     */
    private class FBChatMessageListener implements MessageListener {
        public void processMessage(Chat chat, Message message) {
//            messageReceived(message);
        }
    }

    /**
     * Closes the chat and releases all allocated resources.
     */
    public void close() {
        if (chat != null) {
            if  (listener != null) {
                chat.removeMessageListener(listener);
            }
            chat = null;
        }

        if (chatFrame != null) {
            chatFrame.setVisible(false);
            chatFrame.dispose();
            chatFrame = null;
        }
        context = null;
        contact = null;
        listener = null;
    }
}
