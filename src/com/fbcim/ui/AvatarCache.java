package com.fbcim.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.fbcim.FBChatContext;
import com.fbcim.chat.FBContact;

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
public class AvatarCache extends Thread {
    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(AvatarCache.class.getName());

    /** Format of the images in the file cache. */
    private static String CACHE_IMAGE_FORMAT = "JPG";

    /** File path to keep cached avatars in. */
    private File cacheDir;

    /**
     * Constructs avatar loader object instance.
     *
     * @param cacheDir
     *          the directory that keeps cached avatars.
     */
    public AvatarCache(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    /**
     * Loads avatar for a given facebook contact from file.
     *
     * @param contact
     *          the contact to get avatar for.
     *
     * @return  the avatar for a given contact (or <code>null</code> if avatar was not found).
     */
    public ImageIcon getAvatarFromFile(FBContact contact) {
        File f = getTargetFile(contact);
        if (f.exists()) {

            // Make sure file is less than 1 week old.
            if (System.currentTimeMillis() - f.lastModified() > 1000 * 60 * 60 * 24 * 7) {
                f.delete();
                return null;
            }

            // Load image from file.
            try {
                BufferedImage bi = ImageIO.read(f);
                return new ImageIcon(bi);
            } catch (Throwable t) {
                LOG.log(Level.SEVERE, "Failed to load cached avatar from file!", t);
            }
        }

        // Avatar file not found.
        return null;
    }

    /**
     * Saves given thumbnail to file cache.
     *
     * @param avatar
     *          the avatar to be put to file cache.
     * @param contact
     *          the facebook contact this avatar belongs to.
     */
    public void saveAvatar2File(Icon avatar, FBContact contact) {
        if ((avatar.getIconWidth() < 1) || (avatar.getIconHeight() < 1)) {
            return;
        }
        File f = getTargetFile(contact);
        BufferedImage bi = new BufferedImage(avatar.getIconWidth(), avatar.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        avatar.paintIcon(null, bi.getGraphics(), 0, 0);
        try {
            ImageIO.write(bi, CACHE_IMAGE_FORMAT, f);
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to put avatar to file cache.", t);
        }
    }

    /**
     * Retrieves target file for a given facebook contact.
     *
     * @param contact
     *          the facebook contact to get avatar file for.
     *
     * @return  the facebook contact avatar file.
     */
    private File getTargetFile(FBContact contact) {
        cacheDir.mkdirs();
        return new File(cacheDir, contact.getId());
    }
}
