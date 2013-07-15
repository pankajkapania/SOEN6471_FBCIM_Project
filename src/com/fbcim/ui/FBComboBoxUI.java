package com.fbcim.ui;

import com.fbcim.util.ImageUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

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
public class FBComboBoxUI extends BasicComboBoxUI {
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        listBox.setBackground(FBChatColors.COMBOBOX_BG);
        listBox.setSelectionBackground(FBChatColors.COMBOBOX_BG);
        listBox.setRequestFocusEnabled(false);
        listBox.setFont(new Font("Lucinda Grande", Font.PLAIN, 11));
    }


    protected JButton createArrowButton() {
        arrowButton = new JButton() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        arrowButton.setIcon(ImageUtil.getImageIcon(FBChatImages.BUTTON_ARROW));
        arrowButton.setBorder(null);
        arrowButton.setBorderPainted(false);
        arrowButton.setFocusPainted(false);
        arrowButton.setBackground(FBChatColors.COMBOBOX_BG);
        arrowButton.setRequestFocusEnabled(false);
        arrowButton.setRolloverEnabled(false);
        return arrowButton;
    }

    /**
     * Paints the background of the currently selected item.
     */
    public void paintCurrentValueBackground(Graphics g,Rectangle bounds,boolean hasFocus) {
        Color t = g.getColor();
        g.setColor(FBChatColors.COMBOBOX_BG);
        g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
        g.setColor(t);
    }
}
