package com.fbcim.chat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

import com.fbcim.ui.FBChatImages;
import com.fbcim.ui.ImageBackgroundPainter;
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
public class CustomButton extends JButton {

    private ImageBackgroundPainter painter;
    private ImageBackgroundPainter pressedPainter;

    /**
     * Constructs custom button instance.
     *
     * @param painter
     *          paints this button.
     * @param pressedPainter
     *          paints pressed button.
     */
    public CustomButton(ImageBackgroundPainter painter, ImageBackgroundPainter pressedPainter) {
        this.painter = painter;
        this.pressedPainter = pressedPainter;
        this.setOpaque(false);
        this.setPressedIcon(new ImageIcon());
    }

    /**
     * Creates button with given title, action listener and default renderers.
     *
     * @param title
     *          the button title.
     * @param actionListener
     *          the button action listener.
     *
     * @return  the constructed button instance.
     */
    public static CustomButton getInstance(String title, ActionListener actionListener) {
        ImageBackgroundPainter painter = new ImageBackgroundPainter(
                ImageUtil.getImageIcon(FBChatImages.BUTTON_LEFT).getImage(),
                ImageUtil.getImageIcon(FBChatImages.BUTTON_MID).getImage(),
                ImageUtil.getImageIcon(FBChatImages.BUTTON_RIGHT).getImage()
        );
        ImageBackgroundPainter pressedPainter = new ImageBackgroundPainter(
                ImageUtil.getImageIcon(FBChatImages.BUTTON_LEFT_PRESSED).getImage(),
                ImageUtil.getImageIcon(FBChatImages.BUTTON_MID_PRESSED).getImage(),
                ImageUtil.getImageIcon(FBChatImages.BUTTON_RIGHT_PRESSED).getImage()
        );

        CustomButton btn = new CustomButton(painter, pressedPainter);
        btn.setUI(new BasicButtonUI());
        btn.setBorderPainted(false);
//        btn.setBorder(null);
        btn.setText(title);
        btn.setForeground(Color.WHITE);
        if (actionListener != null) {
            btn.addActionListener(actionListener);
        }
        return btn;
    }

    public void paintComponent(Graphics g) {
        if (model.isPressed()) {
            pressedPainter.paint((Graphics2D)g, this, getWidth(), getHeight());
        } else {
            painter.paint((Graphics2D)g, this, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }
}
