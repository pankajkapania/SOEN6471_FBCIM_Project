package com.fbcim.update;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fbcim.FBCIMSettings;
import com.fbcim.FBCIMApp;
import com.fbcim.ui.AvatarCache;
import com.fbcim.ui.FBChatStrings;
import com.fbcim.ui.FBLoginFrame;
import com.fbcim.ui.MessageDialog;
import com.fbcim.ui.Msg;
import com.fbcim.ui.UpdateDownloadDialog;
import com.fbcim.util.FileUtil;

import javax.security.auth.login.FailedLoginException;
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
public class UpdateManager
    implements UpdateDownloadListener {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(UpdateManager.class.getName());

    /** How long to wait between update checks. */
    private static final long UPDATE_INTERVAL = 7 * 24 * 60 * 60 * 1000;

    /** The update service url. */
    private static final String SERVICE_URL = "http://update.fbcim.com/";

    /** The app. settings */
    private FBCIMSettings settings;

    /** The app. login frame (parent for all popup dialogs displayed by update manager). */
    private FBLoginFrame loginFrame;

    /** The currently active update download. */
    private UpdateDownloader updateDownload = null;

    /**
     * Constructs update manager instance.
     *
     * @param settings
     *          the app. settings.
     */
    public UpdateManager(FBCIMSettings settings, FBLoginFrame loginFrame) {
        this.settings = settings;
        this.loginFrame = loginFrame;

        // Delete all incomplete updates.
        File updateDownloadDir = FileUtil.getUpdateDownloadDir();
        if (updateDownloadDir.exists()) {
            File[] oldFiles = updateDownloadDir.listFiles();
            for (File incompleteUpdate : oldFiles) {
                LOG.info("Deleting incomplete update: " + incompleteUpdate);
                incompleteUpdate.delete();
            }
        }
    }

    /**
     * Checks if update is available.
     *
     * @return  <code>true</code> if user decided to update now,
     *          <code>false</code> otherwise.
     */
    public boolean checkUpdate() {
        // Retrieve update information from server.
        UpdateInfo updateInfo = queueUpdateService();

        // Notify user about available update.
        if (updateInfo != null) {
            return notifyUserAboutUpdate(updateInfo);
        } else {
            return false;
        }

        /////////////////////////////
        // DEBUG
        /////////////////////////////
//        UpdateInfo updateInfo = new UpdateInfo(1, "http://www.google.com/");
//        notifyUserAboutUpdate(updateInfo);
//        updateInfo = new UpdateInfo(2, "http://www.mail.ru/");
//        notifyUserAboutUpdate(updateInfo);
//        updateInfo = new UpdateInfo(3, "http://www.fbcim.com/");
//        notifyUserAboutUpdate(updateInfo);

//        updateInfo = new UpdateInfo(11, "http://www.rarlab.com/rar/wrar410.exe");
//
//        updateInfo = new UpdateInfo(12, "http://www.mail.ru/");
//        return notifyUserAboutUpdate(updateInfo);
//        updateInfo = new UpdateInfo(13, "http://www.fbcim.com/");
//
//        notifyUserAboutUpdate(updateInfo);
    }

    /**
     * Retrieves information about available update from remote update service.
     *
     * @return  information about available update.
     */
    private UpdateInfo queueUpdateService() {
        // First, check if enough time has passed since last update check.
        if (settings.getLastUpdateTime() > System.currentTimeMillis() - UPDATE_INTERVAL) {
            return null;
        }

        try {
            // Construct data
            StringBuffer data = new StringBuffer();
            data.append(URLEncoder.encode("t", "UTF-8"));
            data.append("=");
            data.append(URLEncoder.encode(String.valueOf(settings.getLastUpdateTime() / 1000), "UTF-8"));
            data.append("&");
            data.append(URLEncoder.encode("v", "UTF-8"));
            data.append("=");
            data.append(URLEncoder.encode(FBCIMApp.VERSION, "UTF-8"));

            // Send data
            URL url = new URL(SERVICE_URL);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data.toString());
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer(30);
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            LOG.info("Update Service Response: " + response.toString());
            wr.close();
            rd.close();

            // Analyse response.
            StringTokenizer updateInfo = new StringTokenizer(response.toString(),"&", false);
            int a = -1;
            String u = null;
            while (updateInfo.hasMoreTokens()) {
                String var = updateInfo.nextToken();
                if (var.startsWith("a=")) {
                    a = Integer.valueOf(var.substring(2));
                } else if (var.startsWith("u=")) {
                    u = var.substring(2);
                }
            }

            // Return extracted update information.
            return new UpdateInfo(a, u);

        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to retrieve update information from Update Service!", t);
            return null;
        }
    }

    /**
     * Notifies user about available update and starts update download if necessary.
     *
     * @param updateInfo
     *          information about available update.
     *
     * @return  <code>true</code> if user decided to update now,
     *          <code>false</code> otherwise.
     */
    private boolean notifyUserAboutUpdate(UpdateInfo updateInfo) {
        switch (updateInfo.a) {
            case 0: // no update available.
                updateTimestamp();
                return false;
            case 1:
                return optionalUpdate(updateInfo.u, true);
            case 2:
                return importantUpdate(updateInfo.u, true);
            case 3:
                return criticalUpdate(updateInfo.u, true);
            case 11:
                return optionalUpdate(updateInfo.u, false);
            case 12:
                return importantUpdate(updateInfo.u, false);
            case 13:
                return criticalUpdate(updateInfo.u, false);
        }

        return false;
    }


    /**
     * Optional update is available (the user can choose to update or not) and regardless
     * of the user action (weather he chooses to update or not to update) we update
     * the unix time stamp item (so the update dialog will not appear for at least another 7 days).
     *
     * @param url
     *          the update resource url.
     * @param website
     *          <code>true</code> if update resource is a website,
     *          <code>false</code> if url points to executable binary file
     *
     * @return  <code>true</code> if user decided to update now,
     *          <code>false</code> otherwise.
     */
    private boolean optionalUpdate(String url, boolean website) {
        updateTimestamp();
        MessageDialog msg
                = new MessageDialog(loginFrame, Msg.getString(FBChatStrings.UPDATE_AVAILABLE_TITLE), Msg.getString(FBChatStrings.UPDATE_AVAILABLE_MSG),
                                    Msg.getString(FBChatStrings.YES), Msg.getString(FBChatStrings.LATER));
        final int selectedOption = msg.showMessageDialog();
        if (selectedOption == 0) {
            // update now.
            update(url, website);
            return true;
        } else {
            // update later.
            return false;
        }
    }

    /**
     * @param url
     *          the update resource url.
     * @param website
     *          <code>true</code> if update resource is a website,
     *          <code>false</code> if url points to executable binary file
     *
     * @return  <code>true</code> if user decided to update now,
     *          <code>false</code> otherwise.
     */
    private boolean importantUpdate(String url, boolean website) {
        MessageDialog msg
                = new MessageDialog(loginFrame, Msg.getString(FBChatStrings.CRITICAL_UPDATE_AVAILABLE_TITLE), Msg.getString(FBChatStrings.IMPORTANT_UPDATE_AVAILABLE_MSG),
                                    Msg.getString(FBChatStrings.YES), Msg.getString(FBChatStrings.LATER));
        final int selectedOption = msg.showMessageDialog();
        if (selectedOption == 0) {
            // update now.
            update(url, website);
            return true;
        } else {
            // update later.
            return false;
        }
    }

    /**
     * @param url
     *          the update resource url.
     * @param website
     *          <code>true</code> if update resource is a website,
     *          <code>false</code> if url points to executable binary file
     *
     * @return  <code>true</code>
     */
    private boolean criticalUpdate(String url, boolean website) {
        MessageDialog msg
                = new MessageDialog(loginFrame, Msg.getString(FBChatStrings.CRITICAL_UPDATE_AVAILABLE_TITLE), Msg.getString(FBChatStrings.CRITICAL_UPDATE_AVAILABLE_MSG),
                                    Msg.getString(FBChatStrings.OK));
        final int selectedOption = msg.showMessageDialog();
        if (selectedOption == 0) {
            // update now.
            update(url, website);
        } else {
            // exit.
            exit();
        }
        return true;
    }

    /**
     * Update timestamp with the current time.
     */
    private void updateTimestamp() {
        settings.setLastUpdateTimestamp(System.currentTimeMillis());
    }

    /**
     * Initializes update using given parameters.
     *
     * @param url
     *          the update resource url.
     * @param website
     *          <code>true</code> if update resource is a website,
     *          <code>false</code> if url points to executable binary file
     */
    private void update(String url, boolean website) {
        if (website) {
            FileUtil.launchUrl(url);
            exit();
        } else {
            downloadUpdate(url);
        }
    }

     /**
     * Downloads file from given url and launches it.
     *
     * @param url
     *          the target url to download update from.
     */
    private void downloadUpdate(String url) {
        // Cancel previous update download.
        if (updateDownload != null) {
            updateDownload.cancelDownload();
            updateDownload = null;
        }

        // Construct file path to store downloaded update at.
        File updateFile = new File(FileUtil.getUpdateDownloadDir(), String.valueOf(System.currentTimeMillis() + ".exe"));

        // Construct update downloader.
        updateDownload = new UpdateDownloader(url, updateFile);

        // Construct update progress dialog.
        UpdateDownloadDialog updateProgressDialog = new UpdateDownloadDialog(loginFrame);

        // Attach listeners.
        updateDownload.addListener(updateProgressDialog);
        updateDownload.addListener(this);

        // Start download process.
        updateDownload.start();
    }

    /**
     * Quit the app.
     */
    private void exit() {
        // @todo    it is better to use standard method for exiting the app.
        System.exit(0);
    }

    public void downloadStarted() {}

    public void downloadProgress(long downloaded, long total, long bytesPerSecond, long secondsLeft) {}

    public void downloadFailed(Throwable t) {}

    /**
     * Invoked when update download is completed.
     *
     * This method launches downloaded file and exists the app.
     *
     * @param f
     *          the downloaded update file.
     */
    public void downloadCompleted(File f) {
        // Launch downloaded file and exit app.
        try {
            Desktop.getDesktop().open(f);
        } catch (Throwable t) {
            try {
                Runtime.getRuntime().exec(f.getAbsolutePath());
            } catch (Throwable t2) {
                LOG.log(Level.SEVERE, "Failed to launch downloaded update " + f, t2);
            }
        }

        // Exit app.
        exit();
    }

    /**
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class UpdateInfo {
        /** Update action code. */
        final int a;

        /** Update resource url. */
        final String u;

        /**
         * Constructs update info object instance.
         *
         * @param a
         *          the update action code.
         * @param u
         *          the update resource url.
         */
        UpdateInfo(int a, String u) {
            this.a = a;
            this.u = u;
        }
    }
}