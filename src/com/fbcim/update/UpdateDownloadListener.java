package com.fbcim.update;

import java.io.File;

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
public interface UpdateDownloadListener {
    /**
     * Invoked when download starts.
     */
    public void downloadStarted();

    /**
     * Invoked when download progress changes.
     *
     * @param downloaded
     *          the number of downloaded bytes.
     * @param total
     *          the total size of data.
     */
    public void downloadProgress(long downloaded, long total, long bytesPerSecond, long secondsLeft);

    /**
     * Invoked when download completes.
     *
     * @param f
     *          the downloaded installer file.
     */
    public void downloadCompleted(File f);

    /**
     * Invoked when download fails for some reason.
     *
     * @param t
     *          the optional exception object that caused this download to fail.
     */
    public void downloadFailed(Throwable t);
}