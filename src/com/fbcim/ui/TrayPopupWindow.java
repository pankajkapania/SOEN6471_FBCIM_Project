package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.chat.FBContact;
import com.fbcim.util.ImageUtil;
import com.fbcim.util.WindowTransparencyUtils;
import org.jivesoftware.smack.packet.Presence;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class TrayPopupWindow extends JWindow {
    /** The contact that raised this event. */
    private FBContact contact;

    /** The app. context to work with. */
    private FBChatContext context;

    /** Popup window background. */
    private Image bg;

    /** The window content pane. */
    private JPanel contentPane;

    /** Displays contact avatar. */
    private JLabel contactAvatar;

    /** Displays contact name. */
    private JLabel contactName;

    /** Displays contact online status. */
    private JLabel contactStatus;

    /**
     * Constructs tray icon window.
     *
     * @param contact
     *          the contact that raised this event.
     * @param context
     *          the app. context to work with.
     */
    public TrayPopupWindow(FBContact contact, FBChatContext context) {
        this.contact = contact;
        this.context = context;
        bg = ImageUtil.getImageIcon(FBChatImages.TRAY_POPUP_BG).getImage();
        setAlwaysOnTop(true);
        setSize(bg.getWidth(null), bg.getHeight(null));
        setPreferredSize(getSize());
        contentPane = new ContentPane(contact);
        GridBagConstraints gbc = new GridBagConstraints();

        contactAvatar = new JLabel(contact.getAvatarTray());
        contactAvatar.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));

        contentPane.add(contactAvatar,  BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.left = 10;
        rightPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = GridBagConstraints.REMAINDER;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.left = 118;
        gbc.insets.right = 6;
        rightPanel.add(createCloseButton(), gbc);

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.left = 10;
        gbc.insets.top = 0;
        rightPanel.add(contactName = createContactNameLabel(contact), gbc);

        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.left = 10;
        gbc.insets.top = 5;
        rightPanel.add(contactStatus = createOnlineStatusLabel(contact), gbc);

        contentPane.add(rightPanel, BorderLayout.CENTER);

        getContentPane().add(contentPane);
        WindowTransparencyUtils.setWindowOpaque(this, false);
    }

    /**
     * Closes this popup window.
     */
    public void closeWindow() {
        setVisible(false);
        dispose();
    }

    private JRadioButton createCloseButton() {
        final JRadioButton closeButton = new JRadioButton(ImageUtil.getImageIcon(FBChatImages.POPUP_CLOSE));
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });
        closeButton.setRolloverEnabled(true);
        closeButton.setRolloverIcon(ImageUtil.getImageIcon(FBChatImages.CLOSE_BTN_ROLLOVER));
        closeButton.setPressedIcon(ImageUtil.getImageIcon(FBChatImages.CLOSE_BTN_PRESSED));
        closeButton.setOpaque(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return closeButton;
    }

    private JLabel createContactNameLabel(FBContact contact) {
        JLabel nameLabel = new JLabel(contact.getName());
        nameLabel.setForeground(FBChatColors.POPUP_CONTACT_NAME);
        nameLabel.setFont(new Font("Lucinda Grande", Font.BOLD, 11));
        return nameLabel;
    }

    private JLabel createOnlineStatusLabel(FBContact contact) {

        Presence presence = contact.getPresence();
        Icon presenceIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_OFFLINE);
        String presenceText = Msg.getString(FBChatStrings.POPUP_STATUS_OFFLINE);
        if (presence.isAway()) {
            presenceText = Msg.getString(FBChatStrings.POPUP_STATUS_AWAY);
            presenceIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_AWAY);
        } else if (presence.isAvailable()) {
            presenceText = Msg.getString(FBChatStrings.POPUP_STATUS_AVAILABLE);
            presenceIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_AVAILABLE);
        }

        JLabel onlineStatusLabel = new JLabel(presenceText);
        onlineStatusLabel.setIcon(presenceIcon);

        onlineStatusLabel.setForeground(FBChatColors.POPUP_TEXT);
        onlineStatusLabel.setFont(new Font("Lucinda Grande", Font.PLAIN, 11));
        return onlineStatusLabel;
    }

    /**
     * Moves the window from its current location to the new one.
     */
    public synchronized void moveTo(Point newLocation) {
        setLocation(newLocation);
    }

    private class ContentPane extends JPanel {
        public ContentPane(final FBContact contact) {
            super(new BorderLayout());
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            closeWindow();
                            context.chat(contact).getChatFrame().setVisible(true);
                        }
                    });
                }
            });
        }

        public void paintComponent(Graphics g) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
            super.paintComponents(g);
        }
    }
}
