package com.fbcim.ui;

import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JComponent;

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
public class ImageBackgroundPainter {
    /** Image painted on the left side of the component. */
    private final Image leftImg;

    /** Image painted in the central part of the component (can be stretched horizontally). */
    private Image centerImg;

    /** Image painted on the right side of the component. */
    private final Image rightImg;

    /** The left image width. */
    private final int leftImgWidth;

    /** The right image width. */
    private final int rightImgWidth;

    /**
     * Constructs painter component instance.
     *
     * @param left
     *          the left image (optional).
     * @param center
     *          the center stretchable image (optional).
     * @param right
     *          the right image (optional).
     */
    public ImageBackgroundPainter(Image left, Image center, Image right) {
        this.leftImg = left;
        this.centerImg = center;
        this.rightImg = right;

        this.leftImgWidth = left != null ? left.getWidth(null) : 0;
        this.rightImgWidth = right != null ? right.getWidth(null) : 0;
    }

    /**
     * Performs the paint job.
     *
     * @param graphics2D
     *          the graphics context.
     * @param jComponent
     *          the component to paint background for.
     */
    public void paint(Graphics2D graphics2D, JComponent jComponent, int cw, int ch) {
        int width = jComponent.getWidth();
        int height = jComponent.getHeight();
        if (width <= 0) {
            width = cw;
        }
        if (height <= 0) {
            height = ch;
        }
        int centerImgWidth = 0;

        if (leftImg != null) {
            graphics2D.drawImage(leftImg, 0, 0, leftImgWidth, height, null);
        }
        if (centerImg != null) {
            centerImgWidth = width - leftImgWidth - rightImgWidth;
            graphics2D.drawImage(centerImg, leftImgWidth, 0, centerImgWidth, height, null);
        }
        if (rightImg != null) {
            graphics2D.drawImage(rightImg, leftImgWidth + centerImgWidth, 0, rightImgWidth, height, null);
        }
    }

    /**
     * Returns width of the left image.
     *
     * @return  the width of the left image.
     */
    public int getLeftImgWidth() {
        return leftImgWidth;
    }

    /**
     * Returns width of the right image.
     *
     * @return  the width of the right image.
     */
    public int getRightImgWidth() {
        return rightImgWidth;
    }

    protected void setCenterImage(Image newVal) {
        this.centerImg = newVal;
    }
}
