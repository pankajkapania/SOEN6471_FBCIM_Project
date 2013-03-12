package com.fbcim.util;

import java.util.Locale;

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
public class OSUtils {
    private static boolean isWin = false;
    private static boolean isMacOSX = false;
    private static boolean isLinux = false;

    /**
     * Sets the operating system variables.
     */
    public static void setOperatingSystems() {
        String os = System.getProperty("os.name").toLowerCase(Locale.US);
        String version = System.getProperty("os.version").toLowerCase(Locale.US);

        if (os.startsWith("windows")) {
            isWin = true;
        } else if(os.indexOf("linux") != -1) {
    	    isLinux = true;
        } else if(os.startsWith("mac os")) {
    		if(os.endsWith("x")) {
    			isMacOSX = true;
    		}
    	}
    }

    /**
     * Returns whether or not the OS is Mac OS X.
     *
     * @return <tt>true</tt> if the application is running on Mac OS X,
     *         <tt>false</tt> otherwise
     */
    public static boolean isMacOSX() {
    	return isMacOSX;
    }

    /**
     * Returns whether or not the OS is Linux.
     *
     * @return <tt>true</tt> if the application is running on Linux,
     *         <tt>false</tt> otherwise
     */
    public static boolean isLinux() {
    	return isLinux;
    }

    public static boolean isWin() {
        return isWin;
    }
}
