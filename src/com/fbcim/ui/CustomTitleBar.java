package com.fbcim.ui;

import com.fbcim.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
public class CustomTitleBar extends JPanel {

    private JLabel titleLabel;
    private JButton closeBtn;
    private JButton minimizeBtn;

    /**
     * Constructs custom title bar object.
     */
    public CustomTitleBar(Icon icon, final ITitleBarEventListener l) {
        setBorder(BorderFactory.createEmptyBorder(4,8,5,6));
        setBackground(FBChatColors.FRONT_PAGE_TITLE_BG);
        setLayout(new BorderLayout());
        add(titleLabel = new JLabel(icon), BorderLayout.WEST);
        titleLabel.setForeground(FBChatColors.FRAME_TITLE);

        closeBtn = new JButton();
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (l != null) {
                    l.closeButtonPressed();
                }
            }
        });
        closeBtn.setOpaque(false);
        closeBtn.setUI(new BasicButtonUI());
        closeBtn.setBorderPainted(false);
        closeBtn.setBorder(null);
        closeBtn.setRolloverEnabled(true);
        closeBtn.setIcon(ImageUtil.getImageIcon(FBChatImages.CLOSE_BTN));
        closeBtn.setPressedIcon(ImageUtil.getImageIcon(FBChatImages.CLOSE_BTN_PRESSED));
        closeBtn.setRolloverIcon(ImageUtil.getImageIcon(FBChatImages.CLOSE_BTN_ROLLOVER));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setToolTipText(Msg.getString(FBChatStrings.CLOSE_BTN_TOOLTIP));

        minimizeBtn = new JButton();
        minimizeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (l != null) {
                    l.minimizeButtonPressed();
                }
            }
        });
        minimizeBtn.setOpaque(false);
        minimizeBtn.setUI(new BasicButtonUI());
        minimizeBtn.setBorderPainted(false);
        minimizeBtn.setBorder(null);
        minimizeBtn.setRolloverEnabled(true);
        minimizeBtn.setIcon(ImageUtil.getImageIcon(FBChatImages.MINIMIZE_BTN));
        minimizeBtn.setPressedIcon(ImageUtil.getImageIcon(FBChatImages.MINIMIZE_BTN_PRESSED));
        minimizeBtn.setRolloverIcon(ImageUtil.getImageIcon(FBChatImages.MINIMIZE_BTN_ROLLOVER));
        minimizeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        minimizeBtn.setToolTipText(Msg.getString(FBChatStrings.MINIMIZE_BTN_TOOLTIP));
/*
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBorder(BorderFactory.createEmptyBorder(3, 4, 0, 4));
        btnPanel.setOpaque(false);

        btnPanel.add(minimizeBtn, BorderLayout.WEST);
        btnPanel.add(Box.createHorizontalStrut(8));
        btnPanel.add(closeBtn,  BorderLayout.EAST);
*/
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setBorder(BorderFactory.createEmptyBorder(3, 4, 0, 4));
        btnPanel.setOpaque(false);

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
        btnPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets.right = 8;
        for (JButton customButton : createCustomButtons()) {
            gbc.gridx = ++gridx;
            btnPanel.add(customButton, gbc);
        }

        gbc.gridx = ++gridx;
        btnPanel.add(minimizeBtn, gbc);

        gbc.gridx = ++gridx;
        gbc.insets.right = 0;
        btnPanel.add(closeBtn, gbc);

        add(btnPanel, BorderLayout.EAST);
    }

    /**
     * Create list of custom buttons placed before standard "minimize" and "close" buttons.
     *
     * @return  the list of custom buttons placed before standard "minimize" and "close" buttons.
     */
    protected List<JButton> createCustomButtons() {
        return new ArrayList<JButton>();
    }

    public void setFont(Font font) {
        if (titleLabel != null) {
            titleLabel.setFont(font);
        }
    }

    /**
     * Sets custom title text.
     *
     * @param newVal
     *          the title text to set.
     */
    public void setTitle(String newVal) {
        titleLabel.setText(newVal);
    }

    /**
     * Adjusts visibility of the 'Close' button.
     *
     * @param newVal
     *          <code>true</code> if 'Close' button should be set visible,
     *          <code>false</code> otherwise.
     */
    public void setCloseBtnVisible(boolean newVal) {
        closeBtn.setVisible(newVal);
    }

    /**
     * Adjusts visibility of the 'Minimize' button.
     *
     * @param newVal
     *          <code>true</code> if 'Minimize' button should be set visible,
     *          <code>false</code> otherwise.
     */
    public void setMinimizeBtnVisible(boolean newVal) {
        minimizeBtn.setVisible(newVal);
    }
}
