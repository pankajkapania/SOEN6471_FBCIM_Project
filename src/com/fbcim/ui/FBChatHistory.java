package com.fbcim.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalSeparatorUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.fbcim.FBChatContext;
import com.fbcim.chat.FBChatMessage;
import com.fbcim.util.Sound;

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
public class FBChatHistory extends JTextPane {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBChatHistory.class.getName());

    private String STYLE_CONTACT_NAME = "contactName";
    private String STYLE_YOUR_NAME = "yourName";
    private String STYLE_CONTACT_TEXT = "contactText";
    private String STYLE_YOUR_TEXT = "yourText";
    private String STYLE_SEPARATOR = "separator";
    private String STYLE_TIMESTAMP = "timestamp";
    private String STYLE_ARCHIVE_DATE = "archiveDate";

    private static final int FONT_SIZE = 12;
    private static final int TIMESTAMP_FONT_SIZE = 9;
    private static final int ARCHIVE_DATE_FONT_SIZE = 9;
    private static final int LINE_SPACING = 1;

    /** The list of messages in a chat. */
    private List<FBChatMessage> messages;

    /** <code>true</code> if we should display time in the message history. */
    private boolean displayTime = false;

    /** <code>true</code> only if the contact's first name should be displayed. */
    private boolean firstNameOnly = true;

    /** Your facebook chat id. */
    private String yourId;

    /** Your name. */
    private String yourName;

    /** The facebook id of the contact you chat with. */
    private String contactId;

    /** The name of the contact you chat with. */
    private String contactName;

    /** The app. context to work with. */
    private FBChatContext context;

    /** The actual document that contains chat history. */
    private StyledDocument document;

    /** The ID of the person who sent the last message in this chat. */
    private String lastSenderId = null;

    /** Used to format message timestamp. */
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("h:mm a");

    /** Used to format archive date for the current year. */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd");

    /** Used to format archive date for the previous years. */
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    /** Keeps the list of existing archive entries. */
    private final Set<String> archiveEntries;


    /**
     * Constructs new chat history object.
     *
     * @param context
     *          the app. context to work with.
     */
    public FBChatHistory(FBChatContext context) {
        this.context = context;
        this.yourId = context.getUserId();
        this.messages = Collections.synchronizedList(new ArrayList<FBChatMessage>());
        this.archiveEntries = Collections.synchronizedSet(new HashSet<String>());

//        this.yourName = context.getUserName();
        this.yourName = Msg.getString(FBChatStrings.YOU);

        setEditable(false);
        setFont(new Font("SansSerif", Font.PLAIN, 11));
        setMargin(new Insets(10, 10, 10, 10));

        setDocument(document = new DefaultStyledDocument());
        addStyles();
    }

    /**
     * Set id of the contact your are chatting with.
     *
     * @param contactId
     *          the contact id.
     */
    public void setContactId(String contactId) {
        this.contactId = contactId;
        this.contactName = context.getFBContact(contactId).getName().trim();
        if (firstNameOnly) {
            int i = contactName.indexOf(" ");
            if (i > 0) {
                contactName = contactName.substring(0, i);
            }
        }
    }

    /**
     * Install text styles.
     */
    private void addStyles() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style yourName = document.addStyle(STYLE_YOUR_NAME, def);
        StyleConstants.setFontFamily(yourName, "Lucinda Grande");
        StyleConstants.setFontSize(yourName, FONT_SIZE);
        StyleConstants.setBold(yourName, true);
        StyleConstants.setForeground(yourName, FBChatColors.CHAT_YOUR_NAME);
        StyleConstants.setAlignment(yourName , StyleConstants.ALIGN_LEFT);

        Style contactName = document.addStyle(STYLE_CONTACT_NAME, def);
        StyleConstants.setFontFamily(contactName, "Lucinda Grande");
        StyleConstants.setFontSize(contactName, FONT_SIZE);
        StyleConstants.setBold(contactName, true);
        StyleConstants.setForeground(contactName, FBChatColors.CHAT_CONTACT_NAME);
        StyleConstants.setAlignment(contactName , StyleConstants.ALIGN_LEFT);

        Style yourText = document.addStyle(STYLE_YOUR_TEXT, def);
        StyleConstants.setFontFamily(yourText, "Lucinda Grande");
        StyleConstants.setFontSize(yourText, FONT_SIZE);
        StyleConstants.setForeground(yourText, FBChatColors.CHAT_YOUR_TEXT);
        StyleConstants.setAlignment(yourText , StyleConstants.ALIGN_LEFT);

        Style contactText = document.addStyle(STYLE_CONTACT_TEXT, def);
        StyleConstants.setFontFamily(contactText, "Lucinda Grande");
        StyleConstants.setFontSize(contactText, FONT_SIZE);
        StyleConstants.setForeground(contactText, FBChatColors.CHAT_CONTACT_TEXT);
        StyleConstants.setAlignment(contactText, StyleConstants.ALIGN_LEFT);

        Style separator = document.addStyle(STYLE_SEPARATOR, def);
        StyleConstants.setFontFamily(separator, "Lucinda Grande");
        StyleConstants.setFontSize(separator, FONT_SIZE);
        StyleConstants.setForeground(separator, FBChatColors.CHAT_CONTACT_TEXT);
        StyleConstants.setAlignment(separator , StyleConstants.ALIGN_LEFT);

        Style timestamp = document.addStyle(STYLE_TIMESTAMP, def);
        StyleConstants.setFontFamily(timestamp, "Lucinda Grande");
        StyleConstants.setFontSize(timestamp, TIMESTAMP_FONT_SIZE);
        StyleConstants.setForeground(timestamp, FBChatColors.CHAT_MESSAGE_TIMESTAMP);
        StyleConstants.setAlignment(timestamp , StyleConstants.ALIGN_RIGHT);

        Style archiveDate = document.addStyle(STYLE_ARCHIVE_DATE, def);
        StyleConstants.setFontFamily(archiveDate, "Lucinda Grande");
        StyleConstants.setFontSize(archiveDate, ARCHIVE_DATE_FONT_SIZE);
        StyleConstants.setForeground(archiveDate, FBChatColors.CHAT_ARCHIVE_DATE);
        StyleConstants.setAlignment(archiveDate , StyleConstants.ALIGN_LEFT);
    }

    /**
     * Returns messages from this chat history.
     *
     * @return  the messages from this chat history.
     */
    public List<FBChatMessage> getMessages() {
        return messages;
    }

    /**
     * Adds message to the chat history.
     *
     * @param msg
     *          the message to add.
     */
    public synchronized void addMessage(final FBChatMessage msg) {

        final boolean isFirstMessage = messages.size() == 0;
        final boolean isSameSender = (lastSenderId != null) && lastSenderId.equalsIgnoreCase(msg.getFromId());

        messages.add(msg);

        lastSenderId = msg.getFromId();

        final boolean isYourMessage = msg.getFromId().equals(yourId);
        final Date timestamp = new Date(msg.getTimestamp());
        final Date now = new Date();
        final String time = formatTimestamp(timestamp);
        final String date = formatDate(timestamp);
        final String year = formatYear(timestamp);
        final String currentYear = formatYear(now);
        final String currentDate = formatDate(now);
        final Style nameStyle = isYourMessage ?  document.getStyle(STYLE_YOUR_NAME) : document.getStyle(STYLE_CONTACT_NAME);
        final Style textStyle = isYourMessage ?  document.getStyle(STYLE_YOUR_TEXT) : document.getStyle(STYLE_CONTACT_TEXT);
        final String senderName = isYourMessage ? yourName : contactName;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    if (!isSameSender) {
                        // Put a separator line between messages.
                        if (!isFirstMessage) {
                            String separator;
                            if (context.getSettings().getShowTimestamp()) {
                                separator = "\r\n";
                            } else {
                                separator = "\r\n\r\n";
                            }
                            document.insertString(document.getLength(), separator, document.getStyle(STYLE_SEPARATOR));
                        }

                        // Insert archive timestamp.
                        String archiveTimestamp;
                        if (currentYear.equals(year)) {
                            if (currentDate.equals(date)) {
                                archiveTimestamp = Msg.getString(FBChatStrings.TODAY);
                            } else {
                                archiveTimestamp = date;
                            }
                        } else {
                            archiveTimestamp = date + ", " +year;
                        }

                        String archiveTimestampPrefix = isFirstMessage ? "" : "\r\n";

                        if (archiveEntries.add(archiveTimestamp)) {
                            setParagraphAttributes(document.getStyle(STYLE_ARCHIVE_DATE), true);
                            document.insertString(document.getLength(), archiveTimestampPrefix + archiveTimestamp,  document.getStyle(STYLE_ARCHIVE_DATE));
                            insertHorizontalLine();
                        }

                        // Add message timestamp.
                        if (context.getSettings().getShowTimestamp()) {
                            setParagraphAttributes(document.getStyle(STYLE_TIMESTAMP), true);
                            document.insertString(document.getLength(), time + "\r\n", document.getStyle(STYLE_TIMESTAMP));
                        }

                        // Add sender name.
                        setParagraphAttributes(nameStyle, true);
                        document.insertString(
                                document.getLength(),
                                senderName + " : ",
                                nameStyle
                        );
                    } else {
                        // Just move to the new line.
                        document.insertString(document.getLength(), "\r\n", document.getStyle(STYLE_SEPARATOR));
                    }

                    // Add message text.
                    final StringTokenizer tokenizer = new StringTokenizer(msg.getText(), " \n \t", true);
                    while (tokenizer.hasMoreTokens()) {
                        String textFound = tokenizer.nextToken();
                        if (!insertImage(textFound)) {
                            insertText(textFound, textStyle);
                        }
                    }

                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to insert message to the chat history!", t);
                }
            }
        });
    }

    private String formatTimestamp(Date timestamp) {
        return timestampFormat.format(timestamp);
    }

    private String formatDate(Date timestamp) {
        return dateFormat.format(timestamp);
    }

    private String formatYear(Date timestamp) {
        return yearFormat.format(timestamp);
    }

    /**
     * Inserts smiley with given code to the end of chat history.
     *
     * @param imageCode
     *          the smiley text code.
     *
     * @return  <code>true</code> if image was successfully inserted,
     *          <code>false</code> otherwise.
     */
    private boolean insertImage(String imageCode)
        throws BadLocationException {
        FBIcons fbIcons = context.getFBIcons();
        Icon icon = fbIcons.getIcon(imageCode);
        if (icon != null) {
            // The image must first be wrapped in a style
            Style style = document.addStyle(imageCode, null);
            StyleConstants.setIcon(style, icon);
            document.insertString(document.getLength(), imageCode, style);
            document.removeStyle(imageCode);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Inserts given text to the end of the chat history.
     */
    private void insertText(String text, Style textStyle)
        throws BadLocationException {
        setParagraphAttributes(textStyle, true);
        document.insertString(document.getLength(), text, textStyle);
    }


    /**
     * Inserts horizontal line.
     */
    public void insertHorizontalLine() {
        try {
//            insertText("\r\n", document.getStyle(STYLE_SEPARATOR));
            insertComponent(new Separator());
            insertText("\r\n", document.getStyle(STYLE_SEPARATOR));
        } catch (BadLocationException e) {
            LOG.log(Level.SEVERE, "Failed to insert horizontal separator to the chat history!", e);
        }
    }

    /**
     * Horizontal separator in the chat history.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class Separator extends JSeparator {
        Separator() {
            setUI(new SeparatorUI());
            setForeground(FBChatColors.CHAT_HORIZONTAL_SEPARATOR);
            setBackground(Color.WHITE);
        }
    }

    /**
     * Custom separator UI.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private static class SeparatorUI extends MetalSeparatorUI {

        private static final int LEFT_BORDER = 3;
        private static final Dimension PREF_SIZE = new Dimension(0, 5);

        public void paint(Graphics g, JComponent c) {
            Dimension s = c.getSize();

            int x = LEFT_BORDER > s.width ? s.width : LEFT_BORDER;
            g.setColor(c.getForeground() );
            g.drawLine(x, 0, s.width, 0 );
            g.setColor( c.getBackground() );
            g.drawLine(x, 1, s.width, 1 );
        }

        public Dimension getPreferredSize(JComponent c) {
            return PREF_SIZE;
        }
    }
}