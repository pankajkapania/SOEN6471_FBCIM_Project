package com.fbcim.util;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class FileUtil {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FileUtil.class.getName());

    public static File getLauncherFile() {
        String launcherFilePath = System.getProperty("launcherFile");
        if ((launcherFilePath == null) || (launcherFilePath.equals(""))) {
            launcherFilePath = new File(new File("").getAbsolutePath(), "fbcim.exe").getAbsolutePath();
        }
        return new File(launcherFilePath);
    }

    /**
     * Returns the user home directory.
     */
    public static File getUserHomeDir() {
        return new File(System.getProperty("user.home"));
    }

    /**
     * Returns app. settings directory.
     */
    public static File getAppSettingsDir() {
        return new File(getUserHomeDir(), "fbcim");
    }

    /**
     * Returns avatar cache dir.
     *
     * @return  the avatar cache dir.
     */
    public static File getAvatarCacheDir() {
        return new File(getAppSettingsDir(), "cache");
    }

    /**
     * Returns directory where app. keeps downloaded updates.
     *
     * @return  the directory to store downloaded updates in.
     */
    public static File getUpdateDownloadDir() {
        return new File(getAppSettingsDir(), "update");
    }

    /**
     * Returns chat history archive dir.
     *
     * @return  the chat history archive dir.
     */
    public static File getArchiveDir() {
        return new File(getAppSettingsDir(), "archive");
    }

    public static void launchUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to launch url in external browser: " + url, t);
        }
    }
}
