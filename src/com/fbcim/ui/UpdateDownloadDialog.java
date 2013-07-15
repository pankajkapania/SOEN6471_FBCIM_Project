package com.fbcim.ui;

import com.fbcim.update.UpdateDownloadListener;
import com.fbcim.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.math.RoundingMode;
import java.text.NumberFormat;
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
public class UpdateDownloadDialog extends CustomDialog
    implements UpdateDownloadListener {

    private static final int WIDTH = 450;
    private static final int HEIGHT = 130;

    /** The content pane container. */
    private JPanel contentPane;

    /** Displays update download progress in the realtime. */
    private JProgressBar updateProgress;

    /** Diplays information about current download status. */
    private JLabel updateStats;

    /** Used to format megabytes. */
    private NumberFormat FORMAT;

    /**
     * Constructs dialog instance
     *
     * @param owner
     *          the owner frame.
     */
    public UpdateDownloadDialog(Frame owner) {
        super(owner, true);
        setSize(WIDTH, HEIGHT);
        setTitle(Msg.getString(FBChatStrings.UPDATE_IN_PROGRESS));

        titleBar.setMinimizeBtnVisible(false);
        titleBar.setCloseBtnVisible(false);
        titleBar.setFont(new Font("Lucinda Grande", Font.PLAIN, 13));

        FORMAT = NumberFormat.getNumberInstance();
        FORMAT.setMaximumFractionDigits(1);
        FORMAT.setMinimumFractionDigits(0);
        FORMAT.setGroupingUsed(true);
        FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    @Override
    public JComponent createContentPane() {
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(FBChatColors.DIALOG_BG);
        contentPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 1, 1, FBChatColors.MAIN_FRAME_BORDER),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12))
        );

        final Font font = new Font("Lucinda Grande", Font.PLAIN, 13);

        JLabel topLabel = new JLabel(Msg.getString(FBChatStrings.WAIT_UPDATE_PROGRESS));
        topLabel.setForeground(FBChatColors.DIALOG_TEXT);
        topLabel.setFont(font);
        topLabel.setOpaque(false);
        topLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        updateProgress = new ProgressBar(0, 100);

        updateStats = new JLabel(Msg.getString(FBChatStrings.CONNECTING_TO_SERVER));
        updateStats.setForeground(FBChatColors.DIALOG_TEXT);
        updateStats.setFont(font);
        updateStats.setOpaque(false);
        updateStats.setBorder(BorderFactory.createEmptyBorder(12,0,8,0));

        contentPane.add(topLabel, BorderLayout.NORTH);
        contentPane.add(updateProgress, BorderLayout.CENTER);
        contentPane.add(updateStats, BorderLayout.SOUTH);

        return contentPane;
    }

    protected Border createContainerBorder() {
        return BorderFactory.createEmptyBorder();
    }

    protected Border createTitleBarBorder() {
        return BorderFactory.createMatteBorder(1, 1, 0, 1, FBChatColors.FRONT_PAGE_BORDER);
    }

    /**
     * Returns custom titlebar icon.
     *
     * @return  the custom titlebar icon.
     */
    public Icon getTitlebarIcon() {
        return ImageUtil.getImageIcon(FBChatImages.CHAT_ICON);
    }

    public void closeButtonPressed() {
        // ignore
    }

    public void minimizeButtonPressed() {
        // ignore
    }

    /**
     * Invoked when download starts.
     */
    public void downloadStarted() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    downloadStarted();
                }
            });
            return;
        }

        setLocationRelativeTo(getOwner());
        setVisible(true);
        updateProgress.setValue(0);
        updateStats.setText(Msg.getString(FBChatStrings.REMOTE_SERVER_CONNECTION_PROGRESS));
        setTitle(Msg.getString(FBChatStrings.UPDATE_IN_PROGRESS));
    }

    /**
     * Invoked when download progress changes.
     *
     * @param downloaded
     *          the number of downloaded bytes.
     * @param total
     *          the total size of data.
     * @param bytesPerSecond
     *          the current download speed (bytes per second).
     */
    public void downloadProgress(final long downloaded, final long total, final long bytesPerSecond, final long secondsLeft) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    downloadProgress(downloaded, total, bytesPerSecond, secondsLeft);
                }
            });
            return;
        }

        // Calculate download progress.
        int progress =  (int)(downloaded / (total / 100));
        updateProgress.setValue(progress);
        setTitle(progress + "% - " + Msg.getString(FBChatStrings.UPDATE_IN_PROGRESS));
        updateStats.setText(String.format(
                Msg.getString(FBChatStrings.UPDATE_STATS_FORMAT),
                formatRemainingTime(secondsLeft),
                formatMB(downloaded),
                formatMB(total),
                formatKBps(bytesPerSecond)
        ));
    }

    /**
     * Formats download time left to the human-friendly format.
     *
     * @param timeLeft
     *          how many seconds left till download completion.
     *
     * @return  download time left in the human-friendly format.
     */
    private String formatRemainingTime(long timeLeft) {
        String timeStr = "";
        if (timeLeft > 3600) {
            int hours = (int)timeLeft/3600;
            timeStr += hours + " " + Msg.getString(FBChatStrings.HOURS);
            int minutes = (int)((timeLeft - (hours * 3600)) / 60);
            timeStr += ", " + minutes + " " + Msg.getString(FBChatStrings.MINUTES);
        } else if (timeLeft > 60) {
            int minutes = (int)(timeLeft / 60);
            timeStr += minutes + " " + Msg.getString(FBChatStrings.MINUTES);
            int seconds = (int)(timeLeft - minutes * 60);
            timeStr += ", " + seconds + " " + Msg.getString(FBChatStrings.SECONDS);
        } else {
            timeStr += timeLeft + " " + Msg.getString(FBChatStrings.SECONDS);
        }

        // Returns formatted time.
        return timeStr;
    }

    /**
     * Formats data size to human-friendly form.
     *
     * @param bytes
     *          the value to format.
     *
     * @return  the formatted value (in MB).
     */
    private String formatMB(long bytes) {
        return FORMAT.format((double)bytes / (double)(1024 * 1024));
    }

    /**
     * Formats given value (bytes per second) to kilobytes per second.
     *
     * @param bytesPerSecond
     *          the speed value to conver (in bytes per second).
     *
     * @return  the converted speed in KBps.
     */
    private String formatKBps(long bytesPerSecond) {
        return FORMAT.format(bytesPerSecond / 1024);
    }

    /**
     * Invoked when download completes.
     *
     * @param f
     *          the downloaded installer file.
     */
    public void downloadCompleted(File f) {
        setVisible(false);
        dispose();
    }

    /**
     * Invoked when download fails.
     *
     * @param t
     *          the optional exception object.
     *
     */
    public void downloadFailed(Throwable t) {
        setVisible(false);
        dispose();
    }
}
