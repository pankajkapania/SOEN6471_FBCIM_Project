package com.fbcim.ui;

import com.fbcim.util.ImageUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.metal.MetalScrollBarUI;

import java.awt.Color;
import java.awt.Dimension;
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
public class FBScrollBarUI extends MetalScrollBarUI {

    private boolean drawTopBottomBorder;
    private boolean drawExternalBorder;
    private Color internalBorderColor;

    public static void decorateScrollBar(JScrollBar scrollBar,
                                         boolean drawTopBottomBorder, boolean drawExternalBorder,
                                         Color internalBorderColor, Dimension scrollBarSize) {
        scrollBar.setUI(
                new FBScrollBarUI(drawTopBottomBorder, drawExternalBorder, internalBorderColor)
        );

        scrollBar.setPreferredSize(scrollBarSize);
        scrollBar.setSize(scrollBarSize);
        scrollBar.setMinimumSize(scrollBarSize);
        scrollBar.setOpaque(true);
        scrollBar.setBackground(Color.white);
    }

    public FBScrollBarUI(boolean drawTopBottomBorder, boolean drawExternalBorder, Color color) {
        this.drawTopBottomBorder = drawTopBottomBorder;
        this.drawExternalBorder = drawExternalBorder;
        this.internalBorderColor = color;
    }

    protected JButton createDecreaseButton(int orientation)  {
        JButton btn = new JButton() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(internalBorderColor);
                g.drawLine(0,0,0,getHeight());
                g.setColor(FBChatColors.SCROLLBAR_BORDER_TOP_BOTTOM_RIGHT);
                if (drawTopBottomBorder) {
                    g.drawLine(0,0,getWidth(),0);
                }
                if (drawExternalBorder) {
                    g.drawLine(getWidth() - 1, 0 , getWidth() - 1,getHeight());
                }
            }
        };
        btn.setIcon(ImageUtil.getImageIcon(FBChatImages.SCROLLBAR_DECREASE));
        btn.setBorder(null);
        btn.setBorderPainted(false);
        return btn;
    }

    protected JButton createIncreaseButton(int orientation)  {
        JButton btn = new JButton() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(internalBorderColor);
                g.drawLine(0,0,0,getHeight());
                g.setColor(FBChatColors.SCROLLBAR_BORDER_TOP_BOTTOM_RIGHT);
                if (drawTopBottomBorder) {
                    g.drawLine(0,getHeight() - 1 ,getWidth(),getHeight() - 1);
                }
                if (drawExternalBorder) {
                    g.drawLine(getWidth() - 1, 0 , getWidth() - 1,getHeight());
                }
            }
        };
        btn.setIcon(ImageUtil.getImageIcon(FBChatImages.SCROLLBAR_INCREASE));
        btn.setBorder(null);
        btn.setBorderPainted(false);
        return btn;
    }

    protected void paintTrack( Graphics g, JComponent c, Rectangle trackBounds) {
        g.translate( trackBounds.x, trackBounds.y );

        g.setColor(FBChatColors.SCROLLBAR_BACKGROUND);
        g.fillRect(0, 0, trackBounds.width, trackBounds.height);

        g.setColor(internalBorderColor);
        g.drawLine(0,0, 0, trackBounds.height);
        g.setColor(FBChatColors.SCROLLBAR_BORDER_TOP_BOTTOM_RIGHT);
        if (drawExternalBorder) {
            g.drawLine(trackBounds.width - 1, 0, trackBounds.width - 1, trackBounds.height);
        }

        g.translate( -trackBounds.x, -trackBounds.y );
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        g.translate( thumbBounds.x, thumbBounds.y );

        g.setColor(FBChatColors.SCROLLBAR_BACKGROUND);
        g.fillRect(0, 0, thumbBounds.width, thumbBounds.height - 1);

        g.setColor(internalBorderColor);
        g.drawLine(0,0, 0, thumbBounds.height - 1);

        if (drawExternalBorder) {
            g.setColor(FBChatColors.SCROLLBAR_BORDER_TOP_BOTTOM_RIGHT);
            g.drawLine(thumbBounds.width - 1, 0, thumbBounds.width - 1, thumbBounds.height -1 );
        }

        g.setColor(FBChatColors.SCROLLBAR_THUMB_BORDER);
        g.drawRect(2, 0, thumbBounds.width - 5, thumbBounds.height - 1);

        g.setColor(FBChatColors.SCROLLBAR_THUMB_BG);
        g.fillRect(4, 2, thumbBounds.width - 8, thumbBounds.height - 4);

        g.translate( -thumbBounds.x, -thumbBounds.y );
    }
}
