package com.fbcim.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.fbcim.FBCIMSettings;
import com.fbcim.chat.CustomButton;
import com.fbcim.util.ImageUtil;

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
public class FBCIMOptionsDialog extends CustomDialog {

    private static Dimension BUTTON_SIZE = new Dimension(76, 26);

    /** The app. settings. */
    private FBCIMSettings settings;

    /** The content pane container. */
    private JPanel contentPane;

    private JCheckBox loadOnStartup;
    private JCheckBox displayNotification;
    private JCheckBox showTimestamp;
    private JCheckBox saveHistory;
    private JCheckBox displayGamesBar;

    private JCheckBox incomingMsgSound;
    private JCheckBox outgoingMsgSound;
    private JCheckBox notificationSound;

    private JCheckBox useProxy;
    private JTextField proxyHost;
    private JTextField proxyPort;

    /**
     * Constructs options dialog instance.
     */
    public FBCIMOptionsDialog(Frame owner, FBCIMSettings settings) {
        super(owner, true);
        this.settings = settings;

        setTitle(Msg.getString(FBChatStrings.OPTIONS_TITLE));

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        titleBar.setMinimizeBtnVisible(false);
        pack();
    }

    @Override
    public JComponent createContentPane() {
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(FBChatColors.DIALOG_BG);
        contentPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 1, 1, FBChatColors.MAIN_FRAME_BORDER),
                        BorderFactory.createEmptyBorder(0, 0, 0,0))
        );

        final Font textFont = new Font("Lucinda Grande", Font.PLAIN, 13);
        final Font borderFont = new Font("Lucinda Grande", Font.BOLD, 13);

        loadOnStartup = createCheckbox(FBChatStrings.OPTIONS_LOAD_ON_STARTUP, textFont);
        displayNotification = createCheckbox(FBChatStrings.OPTIONS_TRAY_ALERTS, textFont);
        showTimestamp = createCheckbox(FBChatStrings.OPTIONS_DISPLAY_TIME_IN_CHAT_HISTORY, textFont);
        saveHistory = createCheckbox(FBChatStrings.OPTIONS_KEEP_MESSAGE_HISTORY, textFont);
        displayGamesBar = createCheckbox(FBChatStrings.OPTIONS_DISPLAY_GAMES_BAR, textFont);

        incomingMsgSound = createCheckbox(FBChatStrings.OPTIONS_SOUND_FOR_INCOMING_MSG, textFont);
        outgoingMsgSound = createCheckbox(FBChatStrings.OPTIONS_SOUND_FOR_SENT_MSG, textFont);
        notificationSound = createCheckbox(FBChatStrings.OPTIONS_SOUND_FOR_NOTIFICATIONS, textFont);

        useProxy = createCheckbox(FBChatStrings.OPTIONS_USE_PROXY, textFont);
        proxyHost = new JTextField();
        proxyPort = new JTextField();

        JLabel proxyHostLabel = new JLabel(Msg.getString(FBChatStrings.OPTIONS_PROXY_HOST));
        proxyHostLabel.setForeground(FBChatColors.DIALOG_TEXT);
        proxyHostLabel.setFont(textFont);

        JLabel proxyPortLabel = new JLabel(Msg.getString(FBChatStrings.OPTIONS_PROXY_PORT));
        proxyPortLabel.setForeground(FBChatColors.DIALOG_TEXT);
        proxyPortLabel.setFont(textFont);

        JPanel proxyPanel = new JPanel(new GridBagLayout());
        proxyPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.right = 6;
        gbc.insets.left = 22;
        proxyPanel.add(proxyHostLabel, gbc);

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.insets.right = 16;
        gbc.insets.left = 0;
        gbc.ipadx = 165;
        proxyPanel.add(proxyHost, gbc);

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
        gbc.insets.right = 6;
        gbc.ipadx = 0;
        proxyPanel.add(proxyPortLabel, gbc);

        gbc.weightx = 1;
        gbc.gridx = 3;
        gbc.insets.right = 0;
        gbc.ipadx = 40;
        proxyPanel.add(proxyPort, gbc);



        JPanel generalOptions = new JPanel(new GridLayout(5, 1, 8, 0));
        generalOptions.setOpaque(false);
        generalOptions.setBackground(FBChatColors.DIALOG_BG);
        generalOptions.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(16,10,12,14),
                        BorderFactory.createTitledBorder(
                                BorderFactory.createEmptyBorder(),
                                Msg.getString(FBChatStrings.OPTIONS_GENERAL),
                                TitledBorder.LEFT, TitledBorder.TOP, borderFont,
                                FBChatColors.DIALOG_TEXT)
                        )
        );

        generalOptions.add(loadOnStartup);
        generalOptions.add(displayNotification);
        generalOptions.add(displayGamesBar);
        generalOptions.add(showTimestamp);
        generalOptions.add(saveHistory);


        JPanel soundOptions = new JPanel(new GridLayout(3, 1, 8, 0));
        soundOptions.setOpaque(false);
        soundOptions.setBackground(FBChatColors.DIALOG_BG);
        soundOptions.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(8,10,16,14),
                        BorderFactory.createTitledBorder(
                                BorderFactory.createEmptyBorder(),
                                Msg.getString(FBChatStrings.OPTIONS_SOUND),
                                TitledBorder.LEFT, TitledBorder.TOP, borderFont,
                                FBChatColors.DIALOG_TEXT)
                        )
        );
        soundOptions.add(incomingMsgSound);
        soundOptions.add(outgoingMsgSound);
        soundOptions.add(notificationSound);


        JPanel connectionOptions = new JPanel(new GridLayout(2, 1, 8, 0));
        connectionOptions.setOpaque(false);
        connectionOptions.setBackground(FBChatColors.DIALOG_BG);
        connectionOptions.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(8,10,16,14),
                        BorderFactory.createTitledBorder(
                                BorderFactory.createEmptyBorder(),
                                Msg.getString(FBChatStrings.OPTIONS_CONNECTION),
                                TitledBorder.LEFT, TitledBorder.TOP, borderFont,
                                FBChatColors.DIALOG_TEXT)
                        )
        );
        connectionOptions.add(useProxy);
        connectionOptions.add(proxyPanel);


        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.add(generalOptions);
        optionsPanel.add(soundOptions);
        optionsPanel.add(connectionOptions);

//        contentPane.add(generalOptions, BorderLayout.NORTH);
//        contentPane.add(soundOptions, BorderLayout.CENTER);
        contentPane.add(optionsPanel, BorderLayout.CENTER);
        contentPane.add(new ButtonsPane(), BorderLayout.SOUTH);
        return contentPane;
    }

    private JCheckBox createCheckbox(String textKey, Font font) {
        JCheckBox cb = new JCheckBox(Msg.getString(textKey));
        cb.setOpaque(false);
        cb.setFont(font);
        cb.setForeground(FBChatColors.DIALOG_TEXT);
        return cb;
    }

    public void closeButtonPressed() {
        closeDialog();
    }

    public void minimizeButtonPressed() {
        // ignore it.
    }

    protected Border createContainerBorder() {
        return BorderFactory.createEmptyBorder();
    }

    protected Border createTitleBarBorder() {
        return BorderFactory.createMatteBorder(1, 1, 0, 1, FBChatColors.FRONT_PAGE_BORDER);
    }

    /**
     * Returns custom titlebar icon.
     *
     * @return  the custom titlebar icon.
     */
    public Icon getTitlebarIcon() {
        return ImageUtil.getImageIcon(FBChatImages.CHAT_ICON);
    }

    /**
     * Closes this dialog.
     */
    private void closeDialog() {
        setVisible(false);
        dispose();
    }

    /**
     * Saves changes to the app. settings.
     */
    private void saveSettings() {
        settings.setAutorun(loadOnStartup.isSelected());
        settings.setShowNotifications(displayNotification.isSelected());
        settings.setDisplayGamesBar(displayGamesBar.isSelected());
        settings.setShowTimestamp(showTimestamp.isSelected());
        settings.setSaveHistory(saveHistory.isSelected());

        settings.setIncomingSound(incomingMsgSound.isSelected());
        settings.setOutgoingSound(outgoingMsgSound.isSelected());
        settings.setNotificationSound(notificationSound.isSelected());

        settings.setUseProxy(useProxy.isSelected());
        settings.setProxyHost(proxyHost.getText().trim());
        settings.setProxyPort(proxyPort.getText().trim());
    }

    public void setVisible(boolean newVal) {
        if (newVal) {
            loadOnStartup.setSelected(settings.getAutorun());
            displayNotification.setSelected(settings.getShowNotifications());
            displayGamesBar.setSelected(settings.getDisplayGamesBar());
            showTimestamp.setSelected(settings.getShowTimestamp());
            saveHistory.setSelected(settings.getSaveHistory());

            incomingMsgSound.setSelected(settings.getIncomingSound());
            outgoingMsgSound.setSelected(settings.getOutgoingSound());
            notificationSound.setSelected(settings.getNotificationSound());

            // do not change this order!!!
            proxyHost.setText(settings.getProxyHost());
            proxyPort.setText(settings.getProxyPort());
            useProxy.setSelected(settings.getUseProxy());
        }
        super.setVisible(newVal);
    }

    /**
     * Contains control buttons.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class ButtonsPane extends JPanel {
        /**
         * Constructs buttons pane object.
         */
        ButtonsPane() {
            super(new GridBagLayout());
            setBackground(FBChatColors.DIALOG_BUTTONS_BG);
            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 1, 0, 0, FBChatColors.DIALOG_BG),
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createMatteBorder(1, 0, 0, 0, FBChatColors.DIALOG_BUTTONS_TOP_BORDER),
                                    BorderFactory.createEmptyBorder(6, 0, 6, 0)
                            )
                    )
            );


            int gridx = 0;

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = gridx;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            add(Box.createHorizontalGlue(), gbc);

            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets.right = 10;

            gbc.gridx = 1;

            Font buttonFont = new Font("Lucinda Grande", Font.PLAIN, 13);

            CustomButton saveBtn = CustomButton.getInstance(Msg.getString(FBChatStrings.SAVE), new SaveOptionsAction());
            saveBtn.setSize(BUTTON_SIZE);
            saveBtn.setPreferredSize(BUTTON_SIZE);
            saveBtn.setFont(buttonFont);
            add(saveBtn, gbc);

            gbc.gridx = 2;
            CustomButton cancelBtn = CustomButton.getInstance(Msg.getString(FBChatStrings.CANCEL), new CancelAction());
            cancelBtn.setSize(BUTTON_SIZE);
            cancelBtn.setPreferredSize(BUTTON_SIZE);
            cancelBtn.setFont(buttonFont);
            add(cancelBtn, gbc);
        }
    }

    /**
     * Invoked when user saves app. settings.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class SaveOptionsAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            saveSettings();
            closeDialog();
        }
    }

    /**
     * Just closes app. settings dialog.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class CancelAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            closeDialog();
        }
    }
}
