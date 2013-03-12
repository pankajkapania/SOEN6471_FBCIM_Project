package com.fbcim.util;

import com.fbcim.ui.FBChatImages;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
public class ImageUtil {

    private static int SMALL_AVATAR_WIDTH = 28;
    private static int SMALL_AVATAR_HEIGHT = 28;

    private static int MID_AVATAR_WIDTH = 50;
    private static int MID_AVATAR_HEIGHT = 50;

    private static int TRAY_AVATAR_WIDTH = 50;
    private static int TRAY_AVATAR_HEIGHT = 50;

    private static int BIG_AVATAR_WIDTH = 90;
    private static int BIG_AVATAR_HEIGHT = 90;

    private static ImageIcon AVATAR_OVERLAY_SMALL;
    private static ImageIcon AVATAR_OVERLAY_MID;
    private static ImageIcon AVATAR_OVERLAY_BIG;
    private static ImageIcon AVATAR_OVERLAY_TRAY;

    private static final Map<String, ImageIcon> CACHE = Collections.synchronizedMap(new HashMap<String, ImageIcon>());

    /**
     * Loads icon with given name and puts it to the image cache.
     *
     * @param iconName
     *          the name of the icon to load.
     *
     * @return  the loaded icon object.
     */
    public static ImageIcon getImageIcon(String iconName) {
        String imagePath = "/com/fbcim/" + iconName;
        ImageIcon img = CACHE.get(imagePath);
        if (img == null) {
            img = new ImageIcon(ImageUtil.class.getResource(imagePath));
            CACHE.put(imagePath, img);
        }
        return img;
    }

    /**
     * Clears image cache.
     */
    public void clearCache() {
        CACHE.clear();
    }

    public static ImageIcon getSmileyIcon(int index) {
        StringBuffer fileName = new StringBuffer("emoticons/smile_");

        // Switch from 0-based indexes to 1-based.
        index++;

        String.valueOf(index);
        if ((index > 0) && (index < 10)) {
            fileName.append("0");
        }
        fileName.append(index);
        fileName.append(".png");
        return getImageIcon(fileName.toString());
    }

    /**
     * Returns instance of the small avatar overlay.
     *
     * @return  the instance of the small avatar overlay.
     */
    private static synchronized ImageIcon getAvatarOverlaySmall() {
        if (AVATAR_OVERLAY_SMALL == null) {
            AVATAR_OVERLAY_SMALL = getImageIcon(FBChatImages.CONTACT_AVATAR_OVERLAY_SMALL);
        }
        return AVATAR_OVERLAY_SMALL;
    }

    private static synchronized ImageIcon getAvatarOverlayMid() {
        if (AVATAR_OVERLAY_MID == null) {
            AVATAR_OVERLAY_MID = getImageIcon(FBChatImages.CONTACT_AVATAR_OVERLAY_MID);
        }
        return AVATAR_OVERLAY_MID;
    }

    private static synchronized ImageIcon getAvatarOverlayTray() {
        if (AVATAR_OVERLAY_TRAY == null) {
            AVATAR_OVERLAY_TRAY = getImageIcon(FBChatImages.CONTACT_AVATAR_OVERLAY_TRAY);
        }
        return AVATAR_OVERLAY_TRAY;
    }

    /**
     * Returns instance of the small avatar overlay.
     *
     * @return  the instance of the small avatar overlay.
     */
    private static synchronized ImageIcon getAvatarOverlayBig() {
        if (AVATAR_OVERLAY_BIG == null) {
            AVATAR_OVERLAY_BIG = getImageIcon(FBChatImages.CONTACT_AVATAR_OVERLAY_BIG);
        }
        return AVATAR_OVERLAY_BIG;
    }

    public static ImageIcon scaleAvatarSmall(ImageIcon img) {
        ImageIcon scaledAvatar = scale(img, SMALL_AVATAR_WIDTH, SMALL_AVATAR_HEIGHT);
        ImageIcon avatarOverlay = getAvatarOverlaySmall();
        BufferedImage avatarSmall = new BufferedImage(avatarOverlay.getIconWidth(), avatarOverlay.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = avatarSmall.createGraphics();
        g.drawImage(avatarOverlay.getImage(), 0, 0, null);
        g.drawImage(
                scaledAvatar.getImage(),
                (avatarOverlay.getIconWidth() - scaledAvatar.getIconWidth()) / 2,
                (avatarOverlay.getIconHeight() - scaledAvatar.getIconHeight()) / 2,
                null
        );

        return new ImageIcon(avatarSmall);
    }

    public static ImageIcon scaleAvatarMid(ImageIcon img) {
        ImageIcon scaledAvatar = scale(img, MID_AVATAR_WIDTH, MID_AVATAR_HEIGHT);
        ImageIcon avatarOverlay = getAvatarOverlayMid();
        BufferedImage avatarBig = new BufferedImage(avatarOverlay.getIconWidth(), avatarOverlay.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = avatarBig.createGraphics();

        g.drawImage(
                scaledAvatar.getImage(),
                (avatarOverlay.getIconWidth() - scaledAvatar.getIconWidth()) / 2,
                (avatarOverlay.getIconHeight() - scaledAvatar.getIconHeight()) / 2,
                null
        );
        g.drawImage(avatarOverlay.getImage(), 0, 0, null);
        return new ImageIcon(avatarBig);
    }


    public static ImageIcon scaleAvatarTray(ImageIcon img) {
        ImageIcon scaledAvatar = scale(img, TRAY_AVATAR_WIDTH, TRAY_AVATAR_HEIGHT);
        ImageIcon avatarOverlay = getAvatarOverlayTray();
        BufferedImage avatarBig = new BufferedImage(avatarOverlay.getIconWidth(), avatarOverlay.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = avatarBig.createGraphics();

        g.drawImage(
                scaledAvatar.getImage(),
                (avatarOverlay.getIconWidth() - scaledAvatar.getIconWidth()) / 2,
                (avatarOverlay.getIconHeight() - scaledAvatar.getIconHeight()) / 2,
                null
        );
        g.drawImage(avatarOverlay.getImage(), 0, 0, null);
        return new ImageIcon(avatarBig);
    }

    public static ImageIcon scaleAvatarBig(ImageIcon img) {
        ImageIcon scaledAvatar = scale(img, BIG_AVATAR_WIDTH, BIG_AVATAR_HEIGHT);
        ImageIcon avatarOverlay = getAvatarOverlayBig();
        BufferedImage avatarBig = new BufferedImage(avatarOverlay.getIconWidth(), avatarOverlay.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = avatarBig.createGraphics();

        g.drawImage(
                scaledAvatar.getImage(),
                (avatarOverlay.getIconWidth() - scaledAvatar.getIconWidth()) / 2,
                (avatarOverlay.getIconHeight() - scaledAvatar.getIconHeight()) / 2,
                null
        );
        g.drawImage(avatarOverlay.getImage(), 0, 0, null);
        return new ImageIcon(avatarBig);
    }


    /**
     * Scales given image to specified width and height.
     *
     * @return  the downscaled thumbnail.
     */
    public static ImageIcon scale(ImageIcon img, int width, int height) {
        double scale = (double)width / (double)img.getIconWidth();
        if (scale != 1) {
            BufferedImage source = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            img.paintIcon(null, source.getGraphics(), 0, 0);
            BufferedImage dest = new BufferedImage(width, (int)((double)img.getIconHeight() * scale), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = dest.createGraphics();
            g.drawImage(source.getScaledInstance(dest.getWidth(), dest.getHeight(),  Image.SCALE_SMOOTH), 0, 0, null);

            return new ImageIcon(dest);
        } else {
            // Return unchanged image.
            return img;
        }
    }
}
