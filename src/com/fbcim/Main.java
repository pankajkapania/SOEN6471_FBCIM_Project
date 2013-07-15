package com.fbcim;

import java.io.File;
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
public class Main {

    /** The logger instance to use. */
    private static final Logger LOG;

    static {
        // Set properties file.
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // Init logger.
        LOG = Logger.getLogger(Main.class.getName());
    }

    /**
     * Application entry point.
     *
     * @param args
     *          the list of command-line arguments.
     */
    public static void main(String[] args) {
        LOG.fine("Starting app....");

        // Set unhandled exception handler.
        Thread.currentThread().setUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        LOG.log(Level.SEVERE, "Unexpected error occurred in thread: " + t.getName(), e);
                    }
                }
        );

        // Display startup info.
        LOG.log(Level.FINE, "java.library.path: " + System.getProperty("java.library.path"));
        LOG.log(Level.FINE, "jna.library.path: " + System.getProperty("jna.library.path"));
        LOG.log(Level.FINE, "current directory: " + new File("").getAbsolutePath());

        // Start the app.
        try {
            new FBCIMApp().start(args);
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to start the app!",t);
        }
    }
}
