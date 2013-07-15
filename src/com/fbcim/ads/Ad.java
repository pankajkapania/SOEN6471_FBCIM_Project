package com.fbcim.ads;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

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
public class Ad {

    /** The ad tooltip text. */
    public String Text;

    /** ignored! */
    public String srcHover;

    /** The ad image source. */
    public String src;

    /** The ad size code. */
    public int sizeType;

    /** The target ad url. Invoked when user clicks on the ad. */
    public String adUrl;

    /** The loaded ad image. */
    public ImageIcon img;

    /** The loaded ad mouse-over image. */
    public ImageIcon imgRollover;

    /**
     * Default constructor.
     */
    public Ad() {}

    /**
     *
     */
    public Ad(ImageIcon i) {
        this.img = i;
    }

    public String getAdUrl() {
        try {
            return new URL(adUrl).toExternalForm();
        } catch (Throwable t) {
            return adUrl;
        }
    }
}
