package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.FBChatContextListener;
import com.fbcim.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class UserInfoWidget extends JPanel
    implements FBChatContextListener {

    /** Available use statuses. */
    enum UserStatus {
        AVAILABLE,
        AWAY,
        OFFLINE,
    }

    /** Display user avatar. */
    private JLabel avatarLabel;

    /** Displays username. */
    private JLabel usernameLabel;

    /** Displays current user status mode. */
    private JComboBox status;

    /** The current app. context to work with. */
    private FBChatContext context;

    /** Used to update user status on facebook. */
    private JTextField statusUpdateBox;

    public UserInfoWidget(FBChatContext context) {
        super(new GridBagLayout());
        this.context = context;
        this.context.addListener(this);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        setBackground(FBChatColors.CONTACTS_LIST_BG);

        avatarLabel = new JLabel();
        usernameLabel = new JLabel();
        usernameLabel.setForeground(FBChatColors.CONTACTS_NAME);
        usernameLabel.setFont(new Font("Lucinda Grande", Font.PLAIN, 13));
        usernameLabel.setForeground(FBChatColors.USER_NAME);

        status = new JComboBox();
        status.setFont(new Font("Lucinda Grande", Font.PLAIN, 11));
        status.setBorder(BorderFactory.createLineBorder(FBChatColors.COMBOBOX_BORDER));
        status.setUI(new FBComboBoxUI());
        status.addItem(UserStatus.AVAILABLE);
        status.addItem(UserStatus.AWAY);
        status.addItem(UserStatus.OFFLINE);
        status.setRenderer(new UserStatusRenderer());
        status.setBackground(FBChatColors.COMBOBOX_BG);
        status.setPreferredSize(new Dimension(100,20));
        status.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UserStatus newStatus = (UserStatus)status.getSelectedItem();
                switch (newStatus) {
                    case AVAILABLE:
                        UserInfoWidget.this.context.updateXMPPPresence(Presence.Mode.available);
                        break;
                    case AWAY:
                        UserInfoWidget.this.context.updateXMPPPresence(Presence.Mode.away);
                        break;
                    case OFFLINE:
                        UserInfoWidget.this.context.signOut(true);
                        break;
                }
            }
        });


        JPanel statusUpdatePanel = new JPanel(new BorderLayout());
        statusUpdatePanel.setBorder(BorderFactory.createLineBorder(FBChatColors.STATUS_UPDATE_BORDER));
        statusUpdateBox = new StatusUpdateBox(
                context,
                Msg.getString(FBChatStrings.STATUS_UPDATE_PROMPT)
        );

        statusUpdatePanel.add(statusUpdateBox, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        add(avatarLabel, gbc);

        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.left = 10;
        add(usernameLabel, gbc);

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.left = 10;
        gbc.insets.top = 5;
        gbc.ipady = 2;
        add(status, gbc);

        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.insets.left = 10;
        gbc.insets.top = 0;
        gbc.ipady = 0;
        add(Box.createHorizontalGlue(), gbc);

        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.insets.left = 0;
        gbc.insets.top = 11;
        add(statusUpdatePanel, gbc);

    }

    public void userAvatarLoaded() {
        avatarLabel.setIcon(context.getAvatarMid());
        usernameLabel.setText(context.getUserName());
    }

    public void userSignedOut() {}

    public void presenceUpdated(Presence presence) {
        if (presence.isAway()) {
            status.setSelectedItem(UserStatus.AWAY);
        } else if (presence.isAvailable()) {
            status.setSelectedItem(UserStatus.AVAILABLE);
        } else {
            status.setSelectedItem(UserStatus.OFFLINE);
        }
    }


    /**
     * Used to render user status drop-down list.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class UserStatusRenderer extends JLabel
            implements ListCellRenderer {

        private Icon availableIcon;
        private Icon awayIcon;
        private Icon offlineIcon;

        public UserStatusRenderer() {
            setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

            setFont(new Font("Lucinda Grande", Font.PLAIN, 13));
            setForeground(FBChatColors.USER_STATUS);
            setOpaque(true);

            availableIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_AVAILABLE);
            awayIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_AWAY);
            offlineIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_OFFLINE);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof UserStatus) {
                UserStatus status = (UserStatus)value;
                switch (status) {
                    case AVAILABLE:
                        setIcon(availableIcon);
                        setText(Msg.getString(FBChatStrings.STATUS_AVAILABLE));
                        break;
                    case AWAY:
                        setIcon(awayIcon);
                        setText(Msg.getString(FBChatStrings.STATUS_AWAY));
                        break;
                    case OFFLINE:
                        setIcon(offlineIcon);
                        setText(Msg.getString(FBChatStrings.LOGOUT));
                        break;
                }
                if (!isSelected) {
                    setBackground(FBChatColors.STATUS_LIST_BG);
                } else {
                    setBackground(FBChatColors.STATUS_LIST_SEL_BG);
                }
            }
            return this;
        }
    }

}
