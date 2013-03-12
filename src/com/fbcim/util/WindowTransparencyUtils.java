package com.fbcim.util;

import javax.swing.JWindow;
import java.awt.Window;
import java.lang.reflect.Method;
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
public class WindowTransparencyUtils {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(WindowTransparencyUtils.class.getName());

    /**
     * Sets the alpha transparency for a component. com.sun.awt.AWTUtilities
     * exists for java 1.6 update 10. Calling it for reflection to support older
     * 1.6 clients which it will not exist. Mac does not yet have a 1.6 update
     * 10 client yet, but they support a client property Window.alpha to get the
     * same effect.
     */
    @SuppressWarnings("unchecked")
    public static void setAlpha(Window window, float alpha) {
        if (alpha >= .99f) {
            alpha = .99f;// prevents flash when component switches from opaque
            // to transparent
        }

        try {
            // windows, linux, solaris java 1.6 update 10
            Class awtutil = Class.forName("com.sun.awt.AWTUtilities");
            Method setWindowOpaque = awtutil.getMethod("setWindowOpacity", Window.class,
                    float.class);
            setWindowOpaque.invoke(null, window, alpha);
        } catch (Exception ex) {
            if (OSUtils.isMacOSX() && (window instanceof JWindow)) {
                ((JWindow)window).getRootPane().putClientProperty("Window.alpha", new Float(alpha));// mac
            }
        }
    }

    /**
     * Controls opacity of the target window.
     *
     * @param window
     *          the window to adjust opacity for.
     * @param newVal
     *          <code>true</code> if window should be set opaque,
     *          <code>false</code> otherwise.
     */
    public static void setWindowOpaque(Window window, boolean newVal) {
        try {
            // windows, linux, solaris java 1.6 update 10
            Class awtutil = Class.forName("com.sun.awt.AWTUtilities");
            Method setWindowOpaque = awtutil.getMethod("setWindowOpaque", Window.class,
                    boolean.class);
            setWindowOpaque.invoke(null, window, newVal);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Failed to set window opacity!", ex);
        }
    }

    /**
     * Tests if window transparency can be adjusted.
     *
     * @return  <code>true</code> if window transparency is adjustable,
     *          <code>false</code> otherwise.
     */
    public static boolean isWindowTransparencySupported() {
        if (OSUtils.isLinux()) {
            // Transparency does not work on Linux :(
            return false;
        }

        if (OSUtils.isMacOSX()) {
            // @todo    we need to find a way to remove window border
            // that is displayed around transparent window.
            return false;
        }

        try {
            return (Class.forName("com.sun.awt.AWTUtilities") != null);
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to load com.sun.awt.AWTUtilities. Window transparency is not supported!", t);
            return false;
        }
    }
}
