package com.fbcim;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import com.fbcim.util.FileUtil;
import com.fbcim.util.OSUtils;
import com.fbcim.util.WinRegistry;

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
public class FBCIMSettings {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBCIMSettings.class.getName());

    public static final String HELP_URL = "http://www.fbcim.com/help/";

    /** Facebook graph api OAUTH token. */
    private static final String OAUTH_TOKEN = "OAUTH_TOKEN";

    /** Main frame X coordinate. */
    private static final String X = "X";

    /** Main frame Y coordinate. */
    private static final String Y = "Y";

    /** Main frame width. */
    private static final String W = "W";

    /** Main frame height. */
    private static final String H = "H";

    /** The unix timestamp keeping last successful update check. */
    private static final String T = "T";

    private static final String AUTORUN = "autorun";
    private static final String NOTIFICATIONS = "notifications";
    private static final String TIMESTAMP = "timestamp";
    private static final String HISTORY = "history";
    private static final String INCOMING_SOUND = "incomingsound";
    private static final String OUTGOING_SOUND = "outgoingsound";
    private static final String NOTIFICATION_SOUND = "notificationsound";
    private static final String USE_PROXY = "useproxy";
    private static final String PROXY_HOST = "proxyhost";
    private static final String PROXY_PORT = "proxyport";
    private static final String GAMES_BAR = "gamesbar";
    private static final String SHOW_RECOMMEND_DIALOG = "showrecommend";

    private static final String REG_AUTORUN_PATH = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String REG_AUTORUN_KEY = "Fbcim";

    public static final String SILENT_STATUP = "/silent";

    /** Keeps actual settings. */
    private Preferences settings;

    public FBCIMSettings() {
        settings = Preferences.userRoot().node(this.getClass().getName());

        // Add registry key to launch app. on startup.
        setAutorun(getAutorun());
        setUseProxy(getUseProxy());
    }

    public String getOAuthToken() {
        return settings.get(OAUTH_TOKEN, "");
    }

    public void setOAuthToken(String newVal) {
        settings.put(OAUTH_TOKEN, newVal);
    }

    public void setFrameSize(Dimension d) {
        settings.putInt(W, d.width);
        settings.putInt(H, d.height);
        save();
    }

    public Dimension getFrameSize() {
        int w = settings.getInt(W, 250);
        int h = settings.getInt(H, 750);

        return new Dimension(w, h);
    }

    public Point getFrameLocation() {
        int x = settings.getInt(X, -1);
        int y = settings.getInt(Y, -1);

        if ((x == -1) || (y == -1)) {
            return null;
        } else {
            return new Point(x, y);
        }
    }

    public void setFrameLocation(Point p) {
        settings.putInt(X, p.x);
        settings.putInt(Y, p.y);
        save();
    }

    public long getLastUpdateTime() {
        return settings.getLong(T, 0);
    }

    public void setLastUpdateTimestamp(long t) {
        settings.putLong(T, t);
        save();
    }

    public boolean getAutorun() {
        return settings.getBoolean(AUTORUN, true);
    }

    public void setAutorun(boolean newVal) {
        settings.putBoolean(AUTORUN, newVal);
        if (!OSUtils.isWin()) {
            LOG.log(Level.WARNING, "Trying to run app on OS other than Windows");
            return;
        }
        try {
            if (newVal) {
                File launcherFile = FileUtil.getLauncherFile();
                if (launcherFile.exists()) {
                    // Run FBCIM on Windows startup automatically.
                    WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, REG_AUTORUN_PATH);
                    WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, REG_AUTORUN_PATH, REG_AUTORUN_KEY,
                        "\"" + launcherFile.getAbsolutePath() + "\" " + SILENT_STATUP);
                }
            } else {
                // Do not run FBCIM on Windows startup.
                String run = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, REG_AUTORUN_PATH, REG_AUTORUN_KEY);
                if (run != null) {
                    WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, REG_AUTORUN_PATH, REG_AUTORUN_KEY);
                }
            }
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to modify windows registry", t);
        }
    }

    public boolean getShowNotifications() {
        return settings.getBoolean(NOTIFICATIONS, true);
    }

    public void setShowNotifications(boolean newVal) {
        settings.putBoolean(NOTIFICATIONS, newVal);
    }

    public boolean getShowTimestamp() {
        return settings.getBoolean(TIMESTAMP, true);
    }

    public void setShowTimestamp(boolean newVal) {
        settings.putBoolean(TIMESTAMP, newVal);
    }

    public boolean getSaveHistory() {
        return settings.getBoolean(HISTORY, true);
    }

    public void setSaveHistory(boolean newVal) {
        settings.putBoolean(HISTORY, newVal);
    }

    public boolean getIncomingSound() {
        return settings.getBoolean(INCOMING_SOUND, true);
    }

    public void setIncomingSound(boolean newVal) {
        settings.putBoolean(INCOMING_SOUND, newVal);
    }

    public boolean getOutgoingSound() {
        return settings.getBoolean(OUTGOING_SOUND, false);
    }

    public void setOutgoingSound(boolean newVal) {
        settings.putBoolean(OUTGOING_SOUND, newVal);
    }

    public boolean getNotificationSound() {
        return settings.getBoolean(NOTIFICATION_SOUND, true);
    }

    public void setNotificationSound(boolean newVal) {
        settings.putBoolean(NOTIFICATION_SOUND, newVal);
    }

    public boolean getUseProxy() {
        return settings.getBoolean(USE_PROXY, false);
    }

    /**
     * This method should be called after setting proxy host and port!!!
     */
    public void setUseProxy(boolean newVal) {
        settings.putBoolean(USE_PROXY, newVal);
        if (newVal) {
            System.setProperty("http.proxyHost", getProxyHost());
            System.setProperty("http.proxyPort", getProxyPort());
            System.setProperty("https.proxyHost", getProxyHost());
            System.setProperty("https.proxyPort", getProxyPort());
        } else {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
            System.setProperty("https.proxyHost", "");
            System.setProperty("https.proxyPort", "");
        }
        save();
    }

    public String getProxyHost() {
        return settings.get(PROXY_HOST, "");
    }

    public void setProxyHost(String newVal) {
        settings.put(PROXY_HOST, newVal);
    }

    public String getProxyPort() {
        return settings.get(PROXY_PORT, "");
    }

    public void setProxyPort(String newVal) {
        settings.put(PROXY_PORT, newVal);
    }

    public boolean getDisplayGamesBar() {
        return settings.getBoolean(GAMES_BAR, true);
    }

    public void setDisplayGamesBar(boolean newVal) {
        settings.putBoolean(GAMES_BAR, newVal);
    }

    public boolean getRecommendDialogShown() {
        return settings.getBoolean(SHOW_RECOMMEND_DIALOG, false);
    }

    public void recommendDialogShown() {
        settings.putBoolean(SHOW_RECOMMEND_DIALOG, true);
        save();
    }


    public void save() {
        try {
            settings.flush();
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to save application settings!");
        }
    }
}
