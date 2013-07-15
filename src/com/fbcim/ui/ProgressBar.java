package com.fbcim.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JProgressBar;

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
public class ProgressBar extends JProgressBar {

    private Image bgLeft;
    private Image bgCenter;
    private Image bgRight;

    private Image fgLeft;
    private Image fgCenter;
    private Image fgRight;

    /**
     * Constructs progress bar.
     *
     * @param min
     *          the min. value.
     * @param max
     *          the max. value.
     */
    public ProgressBar(int min, int max) {
        super(min, max);
        bgLeft = ImageUtil.getImageIcon(FBChatImages.PROGRESS_BG_LEFT).getImage();
        bgRight = ImageUtil.getImageIcon(FBChatImages.PROGRESS_BG_RIGHT).getImage();
        bgCenter = ImageUtil.getImageIcon(FBChatImages.PROGRESS_BG_CENTER).getImage();
        fgLeft = ImageUtil.getImageIcon(FBChatImages.PROGRESS_FG_LEFT).getImage();
        fgRight = ImageUtil.getImageIcon(FBChatImages.PROGRESS_FG_RIGHT).getImage();
        fgCenter = ImageUtil.getImageIcon(FBChatImages.PROGRESS_FG_CENTER).getImage();

        setPreferredSize(new Dimension(165, 12));
    }


    protected int getProgressWidth() {
        return (int) (getWidth() * getPercentComplete());
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        int width = getWidth();
        int height = getHeight();

        int leftImgWidth = bgLeft.getWidth(null);
        int rightImgWidth = bgRight.getWidth(null);

        // paint background content.
        g2.drawImage(bgCenter, leftImgWidth, 0, width - (leftImgWidth + rightImgWidth), height, null);

        // paint left border.
        g2.drawImage(bgLeft, 0, 0, leftImgWidth, height, null);

        // paint right border.
        g2.drawImage(bgRight, width - rightImgWidth, 0, rightImgWidth, height, null);

        int progress = getProgressWidth();

        if (progress > 0) {
            // paint left border.
            g2.drawImage(fgLeft, 0, 0, leftImgWidth, height, null);

            if (progress > leftImgWidth) {
                g2.drawImage(fgCenter, leftImgWidth, 0, progress - leftImgWidth, height, null);

                if (progress > width - leftImgWidth) {
                    g2.drawImage(fgRight, width - rightImgWidth, 0, rightImgWidth, height, null);
                }
            }
        }


    }

}
