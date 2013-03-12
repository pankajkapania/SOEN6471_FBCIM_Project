package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.chat.FBChat;
import com.fbcim.chat.FBChatMessage;
import com.fbcim.chat.FBContact;
import com.fbcim.util.ImageUtil;
import com.fbcim.util.Sound;

import org.jivesoftware.smack.packet.Message;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

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
public class FBChatFrame extends CustomFrame
    implements ChatInputWidgetListener {

    public static int DEFAULT_CHAT_FRAME_WIDTH = 474;
    public static int DEFAULT_CHAT_FRAME_HEIGHT = 390;

    /** The content pane container. */
    private JPanel contentPane;

    /** Used to split top and bottom parts of the chat frame. */
    private JSplitPane splitPane;

    /** The contact associated with this chat. */
    private FBContact contact;

    /** Displays chat history. */
    private FBChatHistory chatHistory;

    /** Allows to type and send messages. */
    private ChatInputWidget chatInput;

    /** The actual chat proxy. */
    private FBChat chat;

    /** */
    private TopPanel topPanel;

    /** */
    private BottomPanel bottomPanel;

    private String advText;

    /** Plays when a message from other contact is received. */
    private Sound incomingMsgSound;

    /** Plays when a message to other contact is sent. */
    private Sound outgoingMsgSound;

    /** The advertisement bar. */
    private AdBar adBar;

    /**
     * Constructs frame object.
     */
    public FBChatFrame(FBChatContext context, FBContact contact, FBChat chat) {
        super(context);
        this.contact = contact;
        this.chat = chat;
        this.chatHistory.setContactId(contact.getId());
        this.chatInput.addListener(this);
        this.chatInput.setIsTypingText(contact.getFirstName() + " " + Msg.getString(FBChatStrings.IS_TYPING));
        setTitle(contact.getName());
        topPanel.setAvatar(contact.getAvatarBig());
        topPanel.setOfflineContact(contact);
        bottomPanel.setAvatar(context.getAvatarBig());
        this.incomingMsgSound = new Sound(FBChatSounds.INCOMING_MSG);
        this.outgoingMsgSound = new Sound(FBChatSounds.SENT_MSG);

        advText = String.format(Msg.getString(FBChatStrings.ADVERT_TEXT), contact.getFirstName());

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            // close the frame when the user presses escape
            public void actionPerformed(ActionEvent e) {
                closeButtonPressed();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        setGamesBarVisible(context.getSettings().getDisplayGamesBar());
    }

    public void setVisible(boolean newVal) {
        // Update offline notification.
        topPanel.setOfflineNotificationVisible(contact.isOffline());

        super.setVisible(newVal);

        // Make sure we bring window to front.
        if (newVal) {
            setAlwaysOnTop(true);
            setExtendedState(JFrame.ICONIFIED);
            toFront();
            setExtendedState(JFrame.NORMAL);
            setAlwaysOnTop(false);
        }
    }

    public void setGamesBarVisible(boolean newVal) {
        adBar.setVisible(newVal);
        validate();
        repaint();
    }

    @Override
    public JComponent createContentPane() {

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(FBChatColors.CHAT_FRAME_BG);
        contentPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 1, 1, FBChatColors.MAIN_FRAME_BORDER),
                        BorderFactory.createEmptyBorder(0, 0, 12, 11))
        );

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                topPanel = new TopPanel(),
                bottomPanel = new BottomPanel()
        );
        splitPane.setDividerSize(11);
        splitPane.setBackground(FBChatColors.CHAT_FRAME_BG);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setResizeWeight(1);
        splitPane.setUI(new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void setBorder(Border b) {
                    }
                };
            }
        });
        splitPane.setContinuousLayout(true);


        contentPane.add(adBar = new AdBar(context), BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);

        return contentPane;
    }

    /**
     * Returns custom titlebar icon.
     *
     * @return  the custom titlebar icon.
     */
    public Icon getTitlebarIcon() {
        return ImageUtil.getImageIcon(FBChatImages.CHAT_ICON);
    }

    public void closeButtonPressed() {
        setVisible(false);
    }

    public void minimizeButtonPressed() {
        this.setState(Frame.ICONIFIED);
    }

    protected Border createContainerBorder() {
        return BorderFactory.createEmptyBorder();
    }

    protected Border createTitleBarBorder() {
        return BorderFactory.createMatteBorder(1, 1, 0, 1, FBChatColors.FRONT_PAGE_BORDER);
    }

    /**
     * Sets message history for this chat.
     *
     * @param history
     *          the message history to set.
     */
    public void setHistory(List<FBChatMessage> history) {
        if ((history != null) && (history.size() > 0)) {
            for (FBChatMessage msg : history) {
                chatHistory.addMessage(msg);
            }
        }
    }

    public List<FBChatMessage> getHistory() {
        return chatHistory.getMessages();
    }

    /**
     * Adds incoming message.
     *
     * @param msg
     *          the message to append.
     */
    public void messageReceived(Message msg) {
        String msgBody = msg.getBody();
        if (msgBody == null) {
            chatInput.setIsTypingVisible(true);
        } else {
            if (!isVisible()) {
                setVisible(true);
            }
            chatInput.setIsTypingVisible(false);

            // Trim special characters in the end of the message.
            if (msgBody.endsWith("��")) {
                msgBody = msgBody.substring(0, msgBody.length() - 2);
            }

            // Trim advertisement in the beginning of the message.
            if (msgBody.startsWith(advText)) {
                msgBody = msgBody.substring(advText.length());
            }

            // Display message in the chat.
            chatHistory.addMessage(new FBChatMessage(msg.getFrom(), msg.getTo(), msgBody, System.currentTimeMillis()));

            if (context.getSettings().getIncomingSound()) {
                incomingMsgSound.play();
            }
        }
    }

    /**
     * Notifies other party that user is now typing.
     */
    public void keyTyped() {
        chat.keyTyped();
    }

    public void sendMessage(String msg) {
        chatHistory.addMessage(
                new FBChatMessage(context.getUserId(), contact.getId(), msg, System.currentTimeMillis())
        );
        chat.sendText(msg);
        if (context.getSettings().getOutgoingSound()) {
            outgoingMsgSound.play();
        }
    }

    public void updateContactPresence() {
        topPanel.setOfflineNotificationVisible(contact.isOffline());
    }

    /**
     * Contains target contact avatar and chat history.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class TopPanel extends AbstractPanel {
        /** Displays contact avatar. */
        private JLabel contactAvatar;

        /** Displays offline contact notification. */
        private OfflineNotificationPanel offlineNotification;

        private OfflineNotificationPanel offlineNotificationCorner;

        private JScrollPane sp;

        /**
         * Constructs top frame components.
         */
        TopPanel() {
            super();
            setBackground(FBChatColors.CHAT_FRAME_BG);
        }

        Border createBorder() {
            return BorderFactory.createEmptyBorder(11, 11, 0, 0);
        }

        @Override
        JPanel createContentPane() {
            chatHistory = new FBChatHistory(context);
            offlineNotification = new OfflineNotificationPanel(false);
            offlineNotificationCorner = new OfflineNotificationPanel(true);

            sp = new JScrollPane(chatHistory);
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            sp.setBorder(BorderFactory.createMatteBorder(1,1,1,0, FBChatColors.SCROLLBAR_BORDER_TOP_BOTTOM_RIGHT));
            FBScrollBarUI.decorateScrollBar(sp.getVerticalScrollBar(), false, true, FBChatColors.SCROLLBAR_BORDER_TOP_BOTTOM_RIGHT, new Dimension (21, 150));
            JPanel p =  new JPanel(new BorderLayout());
            p.setBackground(FBChatColors.CHAT_FRAME_BG);
            p.add(sp, BorderLayout.CENTER);
            return p;
        }

        void setOfflineContact(FBContact contact) {
            offlineNotification.setOfflineContact(contact);
        }

        void setOfflineNotificationVisible(boolean newVal) {
            if (newVal) {
                sp.setColumnHeaderView(offlineNotification);
                sp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, offlineNotificationCorner);
            } else {
                sp.setColumnHeaderView(null);
                sp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, null);
            }
            sp.validate();
            sp.repaint();
        }
    }

    /**
     * Displays notification when sending messages to offline contact.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class OfflineNotificationPanel extends JPanel {
        /** Displays actual message. */
        private JLabel messageLabel;

        /**
         * Constructs offline notification panel instance.
         */
        OfflineNotificationPanel(boolean rightBorder) {
            setBackground(FBChatColors.OFFLINE_NOTIFICATION_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, rightBorder ? 1 : 0, FBChatColors.SCROLLBAR_BORDER_TOP_BOTTOM_RIGHT),
                    BorderFactory.createEmptyBorder(0, 4, 0, 0)
            ));
            this.messageLabel = new JLabel();
            this.messageLabel.setForeground(FBChatColors.OFFLINE_NOTIFICATION);
            this.messageLabel.setOpaque(false);
            this.messageLabel.setFont(new Font("Lucinda Grande", Font.PLAIN, 11));
            add(messageLabel, BorderLayout.WEST);
            add(Box.createHorizontalStrut(1280), BorderLayout.CENTER);
        }

        /**
         * Sets contact offline message.
         *
         * @param contact
         *          the contact to display offline notification for.
         */
        public void setOfflineContact(FBContact contact) {
            messageLabel.setText(String.format(
                    Msg.getString(FBChatStrings.OFFLINE_NOTIFICATION),
                    contact.getFirstName()
            ));
        }
    }

    /**
     * Contains logged in user avatar and chat input controls.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class BottomPanel extends AbstractPanel {
        /**
         * Constructs top frame components.
         */
        BottomPanel() {
            super();
            setBackground(FBChatColors.CHAT_FRAME_BG);
        }

        Border createBorder() {
            return BorderFactory.createEmptyBorder(0, 11, 0, 0);
        }

        @Override
        JPanel createContentPane() {
            chatInput = new ChatInputWidget(context);
            return chatInput;
        }
    }


    private abstract class AbstractPanel extends JPanel {
        /** Displays contact avatar. */
        private JLabel contactAvatar;

        /**
         * Contructs top frame components.
         */
        AbstractPanel() {
            super(new GridBagLayout());
            setBorder(createBorder());
            setBackground(FBChatColors.CHAT_FRAME_BG);
            contactAvatar = new JLabel();
            contactAvatar.setMinimumSize(new Dimension(50, 50));
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            add(contactAvatar, gbc);

            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 2;
            gbc.insets.left = 12;
            add(createContentPane(), gbc);
        }

        void setAvatar(Icon newVal) {
            contactAvatar.setIcon(newVal);
            validate();
            invalidate();
            repaint();
        }

        abstract Border createBorder();

        abstract JPanel createContentPane();
    }

    public void dispose() {
        if (adBar != null) {
            adBar.stop();
        }
        super.dispose();
    }
}
