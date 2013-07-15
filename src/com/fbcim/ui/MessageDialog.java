package com.fbcim.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

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
public class MessageDialog extends CustomDialog {

    private static Dimension BUTTON_SIZE = new Dimension(64, 26);

    /** The content pane container. */
    private JPanel contentPane;

    /** Displays text message. */
    private MessagePane message;

    /** Displays buttons in the bottom right corner of the dialog. */
    private ButtonsPane buttons;

    /** Keeps exit code. */
    private int exitCode = -1;

    /**
     * Constructs frame object.
     */
    public MessageDialog(Frame owner, String title, String text, String... options) {
        super(owner, true);
        setTitle(title);
        message.setMessage(text);
        buttons.setOptions(options);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        titleBar.setMinimizeBtnVisible(false);
        titleBar.setCloseBtnVisible(false);
        titleBar.setFont(new Font("Lucinda Grande", Font.PLAIN, 13));
        pack();
    }

    @Override
    public JComponent createContentPane() {
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(FBChatColors.DIALOG_BG);
        contentPane.setBorder(
           BorderFactory.createCompoundBorder(
                   BorderFactory.createMatteBorder(0, 1, 1, 1, FBChatColors.MAIN_FRAME_BORDER),
                   BorderFactory.createEmptyBorder(0, 0, 0, 0))
        );

        contentPane.add(message = new MessagePane(), BorderLayout.CENTER);
        contentPane.add(buttons = new ButtonsPane(), BorderLayout.SOUTH);

        return contentPane;
    }

    /**
     * Use this method to show a modal message dialog and get exit code.
     *
     * @return  the user selected option number (0-based).
     */
    public int showMessageDialog() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return exitCode;
    }

    public void closeButtonPressed() {
        // ignore.
    }

    public void minimizeButtonPressed() {
        // ignore
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
     * Closes message dialog and set given exit code.
     *
     * @param exitCode
     *          the exit code to set.
     */
    private void closeMessageDialog(int exitCode) {
        this.exitCode = exitCode;
        setVisible(false);
        dispose();
    }

    /**
     * Contains text message displayed to user.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class MessagePane extends JPanel {
        /** Displays message text. */
        private JLabel msgLabel;

        /**
         * Constructs message pane object.
         */
        MessagePane() {
            super(new BorderLayout());
            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 1, 0, 0, FBChatColors.DIALOG_BG),
                            BorderFactory.createEmptyBorder(12, 8, 12, 8)
                    )
            );
            setBackground(FBChatColors.DIALOG_BG);
            add(msgLabel = new JLabel(), BorderLayout.CENTER);
            msgLabel.setForeground(FBChatColors.DIALOG_TEXT);
            msgLabel.setFont(new Font("Lucinda Grande", Font.PLAIN, 13));
        }

        /**
         * Sets message text to be displayed in this dialog.
         *
         * @param newVal
         *          the message text to display.
         */
        public void setMessage(String newVal) {
            msgLabel.setText(newVal);
            validate();
            repaint();
        }
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
        }

        /**
         * Adds buttons to the message dialog.
         *
         * @param options
         *          the list of options to display.
         */
        public void setOptions(String[] options) {
            GridBagConstraints gbc = new GridBagConstraints();

            int gridx = 0;

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

            for (String option : options) {
                gbc.gridx = ++gridx;
                CustomButton btn = CustomButton.getInstance(option, new CloseMessageDialogAction(gridx - 1));
                btn.setSize(BUTTON_SIZE);
                btn.setPreferredSize(BUTTON_SIZE);
                btn.setFont(new Font("Lucinda Grande", Font.PLAIN, 13));
                add(btn, gbc);
            }
        }
    }

    /**
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class CloseMessageDialogAction implements ActionListener {
        /** The exit code associated with this action. */
        private int exitCode;

        /**
         * Conctructs action object.
         *
         * @param exitCode
         *          the exit code to assign to this action.
         */
        public CloseMessageDialogAction(int exitCode) {
            this.exitCode = exitCode;
        }

        /**
         * Invoked when action occurs.
         *
         * @param e
         *          the action event object.
         */
        public void actionPerformed(ActionEvent e) {
            closeMessageDialog(exitCode);
        }
    }
}