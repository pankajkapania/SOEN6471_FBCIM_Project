package com.fbcim.chat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
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
public class FBChatHistoryArchive {
    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBChatHistoryArchive.class.getName());

    /** Max allowed history size. */
    private static final int MAX_HISTORY_SIZE = 5000;

    /** The directory to save archives at. */
    private final File archiveDir;

    /** The xstream engine to use. */
    private final XStream xstream;

    /**
     * Constructs chat history archive manager.
     *
     * @param archiveDir
     *          the directory where chat history archives should be saved.
     */
    public FBChatHistoryArchive(File archiveDir) {
        this.archiveDir = archiveDir;
        xstream = new XStream();
        xstream.processAnnotations(ChatHistoryMeta.class);
        xstream.processAnnotations(FBChatMessage.class);
    }

    /**
     * Returns chat history for the given contact.
     *
     * @param contact
     *          the contact to return chat history for.
     *
     * @return  the chat history associated with given contact.
     */
    @SuppressWarnings("true")
    public List<FBChatMessage> getHistory(FBContact contact) {
        try {
            File f = getFile(contact);
            if (f.exists()) {
                return ((ChatHistoryMeta)xstream.fromXML(f)).messages;
            } else {
                return null;
            }
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to load chat history for " + contact, t);
            return null;
        }
    }

    /**
     * Saves given chat history.
     *
     * @param contact
     *          the contact given chat history is associated with.
     * @param history
     *          the chat history to save.
     *
     * @return  the chat history associated with given contact.
     */
    public void saveHistory(FBContact contact, List<FBChatMessage> history) {
        OutputStream out = null;
        try {
            // Truncate history if needed.
            if (history.size() > MAX_HISTORY_SIZE) {
                history = history.subList(history.size() - MAX_HISTORY_SIZE, history.size());
            }
            File f = getFile(contact);
            f.getParentFile().mkdirs();
            out = new BufferedOutputStream(new FileOutputStream(f));
            xstream.toXML(
                    new ChatHistoryMeta(System.currentTimeMillis(), history),
                    out
            );
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to save chat history for " + contact, t);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (Throwable t2) {
                    LOG.log(Level.SEVERE, "Failed to release output stream after saving chat history for " + contact, t2);
                }
            }
        }
    }

    /**
     * Returns archive file associated with given contact.
     *
     * @param contact
     *          the contact to return archive file for.
     *
     * @return  the archive file associated with given contact.
     */
    private File getFile(FBContact contact) {
        return new File(archiveDir, contact.getId());
    }

    @XStreamAlias("history")
    private static class ChatHistoryMeta {
        @XStreamAlias("timestamp")
        @XStreamAsAttribute
        public long timestamp;

        @XStreamAlias("messages")
        @XStreamImplicit
        public List<FBChatMessage> messages;

        public ChatHistoryMeta(long timestamp, List<FBChatMessage> messages) {
             this.timestamp = timestamp;
            this.messages = messages;
        }
    }
}