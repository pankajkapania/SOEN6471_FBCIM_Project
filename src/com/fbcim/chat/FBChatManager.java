package com.fbcim.chat;

import com.fbcim.FBChatContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
public class FBChatManager
    implements FBContactListListener {
    /** Keeps contact -> chat mapping. */
    private final Map<FBContact, FBChat> chatMap;

    /** The app. context to work with. */
    private FBChatContext context;

    /** Chat history archive manager. */
    private FBChatHistoryArchive archive;

    /**
     * Constructs chat manager instance.
     *
     * @param context
     *          the app. context to work with.
     */
    public FBChatManager(FBChatContext context) {
        this.context = context;
        this.archive = new FBChatHistoryArchive(context.getArchiveDir());
        this.chatMap = Collections.synchronizedMap(new HashMap<FBContact, FBChat>());
    }

    /**
     * Returns chat associated with given contact.
     *
     * @param contact
     *          the contact to return chat for.
     *
     * @return  the chat associated with given contact.
     */
    public FBChat getChat(FBContact contact) {
        FBChat chat;
        synchronized (chatMap) {
            chat = chatMap.get(contact);
            if (chat == null) {
                chat = new FBChat(context, contact);
                if (context.getSettings().getSaveHistory()) {
                    chat.setHistory(archive.getHistory(contact));
                }
                chatMap.put(contact,  chat);
            }
        }
        return chat;
    }

    /**
     * Saves chat history to archive.
     */
    public void saveChatHistory() {
        if (!context.getSettings().getSaveHistory()) {
            return;
        }
        synchronized (chatMap) {
            for (FBChat chat : chatMap.values()) {
                     archive.saveHistory(chat.getContact(), chat.getHistory());
            }
        }
    }

    /**
     * Removes all frames.
     */
    public void clear() {
        saveChatHistory();

        synchronized (chatMap) {
            for (FBChat chat : chatMap.values()) {
                chat.close();
            }
            chatMap.clear();
        }
        context = null;
    }

    public void contactAdded(FBContact contact) {}

    public void contactUpdated(FBContact contact) {
        FBChat chat = chatMap.get(contact);
        if (chat != null) {
            chat.updateContactPresence();
        }
    }

    public void updateGamesBarVisibility() {
        boolean gamesBarVisible = context.getSettings().getDisplayGamesBar();
        synchronized (chatMap) {
            for (FBChat chat : chatMap.values()) {
                chat.getChatFrame().setGamesBarVisible(gamesBarVisible);
            }
        }
    }
}