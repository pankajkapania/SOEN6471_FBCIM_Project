package com.fbcim.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import com.fbcim.util.ImageUtil;

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
public class FBIcons {
    /** The list of icons. */
    private final List<FBSmiley> icons;

    /** The list of icons. */
    private final String[] iconCodes = {
            ":)", ":(", ":p", ":D", ":o", ";)", "8)", "8|", ">:(", ":/", ":'(",
            "3:)", "O:)", ":*", "<3", "^_^", "-_-", "o.O", ">:O", ":v", ":3", "(y)"
    };

    /** Mapping between icons and their text codes. */
    private final Map<String, Icon> iconsMap;

    /**
     * Constructs icons map.
     */
    public FBIcons() {
        // Load smiley icons.
        this.icons = new ArrayList<FBSmiley>();
        for (int i = 0; i < 22; i++) {
            icons.add(
                    new FBSmiley(
                            ImageUtil.getSmileyIcon(i),
                            iconCodes[i]
                            )
            );
        }

        // Setup smiley dictionary.
        this.iconsMap = Collections.synchronizedMap(new HashMap<String, Icon>());
        this.iconsMap.put(":)", icons.get(0).getIcon());
        this.iconsMap.put(":-)", icons.get(0).getIcon());
        this.iconsMap.put("=)", icons.get(0).getIcon());
        this.iconsMap.put(":(", icons.get(1).getIcon());
        this.iconsMap.put(":-(", icons.get(1).getIcon());
        this.iconsMap.put("=(", icons.get(1).getIcon());
        this.iconsMap.put(":p", icons.get(2).getIcon());
        this.iconsMap.put(":P", icons.get(2).getIcon());
        this.iconsMap.put(":D", icons.get(3).getIcon());
        this.iconsMap.put(":o", icons.get(4).getIcon());
        this.iconsMap.put(":O", icons.get(4).getIcon());
        this.iconsMap.put(":0", icons.get(4).getIcon());
        this.iconsMap.put(":-o", icons.get(4).getIcon());
        this.iconsMap.put(":-O", icons.get(4).getIcon());
        this.iconsMap.put(":-0", icons.get(4).getIcon());
        this.iconsMap.put(";)", icons.get(5).getIcon());
        this.iconsMap.put(";-)", icons.get(5).getIcon());
        this.iconsMap.put("8)", icons.get(6).getIcon());
        this.iconsMap.put("8-)", icons.get(6).getIcon());
        this.iconsMap.put("8|", icons.get(7).getIcon());
        this.iconsMap.put("8-|", icons.get(7).getIcon());
        this.iconsMap.put(">:(", icons.get(8).getIcon());
        this.iconsMap.put(">:-(", icons.get(8).getIcon());
        this.iconsMap.put(":/", icons.get(9).getIcon());
        this.iconsMap.put("=/", icons.get(9).getIcon());
        this.iconsMap.put(":-/", icons.get(9).getIcon());
        this.iconsMap.put(":'(", icons.get(10).getIcon());
        this.iconsMap.put("3:)", icons.get(11).getIcon());
        this.iconsMap.put("]:)", icons.get(11).getIcon());
        this.iconsMap.put("O:)", icons.get(12).getIcon());
        this.iconsMap.put("O:-)", icons.get(12).getIcon());
        this.iconsMap.put("O=)", icons.get(12).getIcon());
        this.iconsMap.put(":*", icons.get(13).getIcon());
        this.iconsMap.put(":-*", icons.get(13).getIcon());
        this.iconsMap.put("=*", icons.get(13).getIcon());
        this.iconsMap.put("<3", icons.get(14).getIcon());
        this.iconsMap.put("^_^", icons.get(15).getIcon());
        this.iconsMap.put("-_-", icons.get(16).getIcon());
        this.iconsMap.put("o.O", icons.get(17).getIcon());
        this.iconsMap.put(">:O", icons.get(18).getIcon());
        this.iconsMap.put(":v", icons.get(19).getIcon());
        this.iconsMap.put(":3", icons.get(20).getIcon());
        this.iconsMap.put(":-3", icons.get(20).getIcon());
        this.iconsMap.put("=3", icons.get(20).getIcon());
        this.iconsMap.put("(y)", icons.get(21).getIcon());
        this.iconsMap.put("(Y)", ImageUtil.getSmileyIcon(21));
    }

    /**
     * Returns currently used smiley icons and associated text codes.
     *
     * @return  the list of currently supported smileys.
     */
    public List<FBSmiley> getIcons() {
        return icons;
    }

    /**
     * Returns icon mapped to a given text code.
     *
     * @param code
     *          the text code to return icon for.
     *
     * @return  the icon mapped to a given text code.
     */
    public Icon getIcon(String code) {
        return iconsMap.get(code);
    }

    /**
     * Presents a smiley used in FB chat.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    public static class FBSmiley {
        /** The smiley icon. */
        private Icon icon;

        /** The smiley text code. */
        private String text;

        /**
         * Constructs smiley object.
         *
         * @param icon
         *          the smiley icon.
         * @param text
         *          the smiley text code.
         */
        public FBSmiley(Icon icon, String text) {
            this.icon = icon;
            this.text = text;
        }

        /**
         * Returns the smiley icon.
         *
         * @return  the smiley icon.
         */
        public Icon getIcon() {
            return icon;
        }

        /**
         * Returns the smiley text code.
         *
         * @return  the smiley text code.
         */
        public String getText() {
            return text;
        }

        /**
         * Returns string presentation of this object.
         *
         * @return  the string presentation of this object.
         */
        public String toString() {
            return getText();
        }
    }
}
