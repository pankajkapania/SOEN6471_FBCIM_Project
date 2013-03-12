package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
public class FBChatMainFrame extends CustomFrame {

    private UserInfoWidget userPanel;

    /** Displays list of contacts. */
    private ContactsWidget contacts;

    /** The content pane container. */
    private JPanel contentPane;

    /**
     * Constructs frame object instance.
     *
     * @param context
     *          the app. context to work with.
     */
    public FBChatMainFrame(FBChatContext context) {
        super(context);
        this.context = context;
    }

    @Override
    public JComponent createContentPane() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(userPanel = new UserInfoWidget(context), BorderLayout.NORTH);
        contentPane.add(contacts = new ContactsWidget(context), BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, FBChatColors.MAIN_FRAME_BORDER));
        return contentPane;
    }

    /**
     * Create custom title bar.
     *
     * @return  the custom title bar component.
     */
    protected CustomTitleBar createTitleBar() {
        return new CustomTitleBar(getTitlebarIcon(), this) {
            protected List<JButton> createCustomButtons() {
                List<JButton> customButtons =  new ArrayList<JButton>();


                JButton helpButton = new JButton();
                helpButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        context.showHelp();
                    }
                });
                helpButton.setOpaque(false);
                helpButton.setUI(new BasicButtonUI());
                helpButton.setBorderPainted(false);
                helpButton.setBorder(null);
                helpButton.setRolloverEnabled(true);
                helpButton.setIcon(ImageUtil.getImageIcon(FBChatImages.HELP_BTN));
                helpButton.setPressedIcon(ImageUtil.getImageIcon(FBChatImages.HELP_BTN_ROLLOVER));
                helpButton.setRolloverIcon(ImageUtil.getImageIcon(FBChatImages.HELP_BTN_ROLLOVER));
                helpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                helpButton.setToolTipText(Msg.getString(FBChatStrings.HELP_BTN_TOOLTIP));
                customButtons.add(helpButton);


                JButton settingsButton = new JButton();
                settingsButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        context.showSettingsDialog();
                    }
                });
                settingsButton.setOpaque(false);
                settingsButton.setUI(new BasicButtonUI());
                settingsButton.setBorderPainted(false);
                settingsButton.setBorder(null);
                settingsButton.setRolloverEnabled(true);
                settingsButton.setIcon(ImageUtil.getImageIcon(FBChatImages.CONFIG_BTN));
                settingsButton.setPressedIcon(ImageUtil.getImageIcon(FBChatImages.CONFIG_BTN_PRESSED));
                settingsButton.setRolloverIcon(ImageUtil.getImageIcon(FBChatImages.CONFIG_BTN_ROLLOVER));
                settingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                settingsButton.setToolTipText(Msg.getString(FBChatStrings.CONFIG_BTN_TOOLTIP));

                customButtons.add(settingsButton);
                return customButtons;
            }
        };
    }

    public void closeButtonPressed() {
        this.setState(Frame.ICONIFIED);
        setVisible(false);
    }

    public void minimizeButtonPressed() {
        this.setState(Frame.ICONIFIED);
    }

    protected Border createContainerBorder() {
        return BorderFactory.createEmptyBorder();
    }

    protected Border createTitleBarBorder() {
        return BorderFactory.createMatteBorder(1,1,0,1,FBChatColors.FRONT_PAGE_BORDER);
    }

    public void setVisible(boolean newVal) {
        if (!newVal && isVisible()) {
            // Save window placement on the screen.
            context.getSettings().setFrameSize(getSize());
            context.getSettings().setFrameLocation(getLocationOnScreen());
        }

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

}