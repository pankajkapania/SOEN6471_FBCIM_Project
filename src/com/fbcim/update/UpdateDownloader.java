package com.fbcim.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
public class UpdateDownloader extends Thread {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(UpdateDownloader.class.getName());

    /** The target url to download data from. */
    private String urlStr;

    /** The local file. */
    private File file;

    /** The total size of the file to download. */
    private volatile long total = -1;

    /** The total size of the downloaded data. */
    private volatile long downloaded = 0;

    /** The current download speed. */
    private volatile long bytesPerSecond = -1;

    /** How long to wait before download completion. */
    private volatile long secondsRemaining = -1;

    /** Timestamp of the first read operation from remote connection. */
    private volatile long downloadStarted;


    /** List of registered listeners. */
    private Set<UpdateDownloadListener> listeners;

    /** <code>true</code> if this download has been canceled. */
    private volatile boolean canceled = false;

    /**
     * Constructs update downloader instance.
     *
     * @param url
     *          the target url to download a file from.
     */
    UpdateDownloader(String url, File f) {
        this.urlStr = url;
        this.file = f;
        this.listeners = Collections.synchronizedSet(new HashSet<UpdateDownloadListener>());
    }

    /**
     * Registers given listener.
     *
     * @param l
     *          the listener to register.
     */
    public void addListener(UpdateDownloadListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /**
     * Notifies registered listeners that download has been started.
     */
    public void fireDownloadStarted() {
        synchronized (listeners) {
            for (UpdateDownloadListener l : listeners) {
                try {
                    l.downloadStarted();
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener that download has been started!", t);
                }
            }
        }
    }

    /**
     * Notifies registered listeners that download has been started.
     */
    public void fireDownloadProgress() {
        if (canceled) {
            return;
        }
        synchronized (listeners) {
            for (UpdateDownloadListener l : listeners) {
                try {
                    l.downloadProgress(downloaded, total, bytesPerSecond, secondsRemaining);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener about download progress!", t);
                }
            }
        }
    }

    /**
     * Notifies registered listeners that download has been completed successfully.
     */
    public void fireDownloadCompleted() {
        if (canceled) {
            return;
        }
        synchronized (listeners) {
            for (UpdateDownloadListener l : listeners) {
                try {
                    l.downloadCompleted(file);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener about completion!", t);
                }
            }
        }
    }

    /**
     * Notifies registered listeners that download has been failed.
     *
     * @param error
     *          the optional exception object that caused this download to fail.
     */
    public void fireDownloadFailed(Throwable error) {
        if (canceled) {
            return;
        }
        synchronized (listeners) {
            for (UpdateDownloadListener l : listeners) {
                try {
                    l.downloadFailed(error);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener about download error", t);
                }
            }
        }
    }

    /**
     * Cancels current download.
     */
    public void cancelDownload() {
        this.canceled = true;
    }

    /**
     * The actual download process goes here.
     */
    public void run() {

        InputStream reader = null;
        FileOutputStream writer = null;
        boolean downloadCompleted = false;
        try {
            // Notify registered listeners that download has been started.
            fireDownloadStarted();

            // Open connection to remote file.
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();


            // Get file size.
            total = Long.parseLong(connection.getHeaderField("Content-Length"));
            reader = connection.getInputStream();
            if (total <= 0) {
                fireDownloadFailed(new Exception("Unknown file size!"));
                return;
            }

            // Open local file for writing.
            if (file.exists()) {
                // We need to write to the empty file.
                file.delete();
            }
            file.getParentFile().mkdirs();
            writer = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;

            // Read content from remote connection and write it down to file.
            long lastNotification = System.currentTimeMillis();
            long lastDownloaded = 0;
            downloadStarted = lastNotification;
            while ((bytesRead = reader.read(buffer)) > 0) {
                if (canceled) {
                    return;
                }
                writer.write(buffer, 0, bytesRead);
                downloaded += bytesRead;

                long now = System.currentTimeMillis();
                long delta = now - lastNotification;
                if (delta > 1000) {
                    // calculate current speed and remaining time.
                    long deltaDownloaded = downloaded - lastDownloaded;

//                    speed for the last second.
//                    bytesPerSecond = (long)(deltaDownloaded / (delta / 1000.0));

                    // Average speed.
                    bytesPerSecond = (long)(downloaded / ((now - downloadStarted) / 1000.0));
                    secondsRemaining = (total - downloaded) / bytesPerSecond;

                    // Save new values.
                    lastNotification = now;
                    lastDownloaded = downloaded;

                    // Notify listeners about update progress.
                    fireDownloadProgress();
                }
            }

            downloadCompleted = true;
        } catch (Throwable t) {
            // Notify registered listeners that download has been failed for some reason.
            fireDownloadFailed(t);
        } finally {
            // Close opened resources.
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                    writer = null;
                }
            } catch (Throwable t) {
                LOG.log(Level.SEVERE, "Failed to close writer object!", t);
            }
            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
            } catch (Throwable t) {
                LOG.log(Level.SEVERE, "Failed to close reader object!", t);
            }
        }

        // Notify registered listeners that download has been completed successfully.
        if (downloadCompleted) {
            fireDownloadCompleted();
        }
    }
}
