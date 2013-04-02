package com.fbcim;

import com.fbcim.ui.FBChatColors;
import com.fbcim.ui.FBIcons;
import com.fbcim.ui.TrayNotifier;
import com.fbcim.util.OSUtils;
import org.jivesoftware.smack.packet.Presence;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import java.awt.Font;
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
public class FBCIMApp
    implements FBChatContextListener {
    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(FBCIMApp.class.getName());

    /** Keeps the current app. version. */
    public static final String VERSION = "1.0";

    /** Keeps all registered smiley icons. */
    private FBIcons fbIcons;

    /** Provides access to the system tray. */
    private TrayNotifier trayNotifier;

    /** The app. settings object. */
    private FBCIMSettings settings;

    /** The application context to work with.  */
    private FBChatContext context;

    /** The application context to work with.  */
    FBChatContextLoader ctxIntializer;

    /**
     * Constructs application instance.
     */
    public FBCIMApp() {
        // Detect current OS.
        OSUtils.setOperatingSystems();

        // Load smileys.
        fbIcons = new FBIcons();

        // Load settings.
        settings = new FBCIMSettings();

        // Install tray icon.
        try {
            trayNotifier = new TrayNotifier();
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to install system tray icon!",t);
        }

        // Init app. context.
        initContext();

        // Install some UI properties.
        UIManager.put("SplitPane.shadow", FBChatColors.CHAT_FRAME_BG);
        UIManager.put("SplitPane.highlight", FBChatColors.CHAT_FRAME_BG);
        UIManager.put("SplitPane.darkShadow", FBChatColors.CHAT_FRAME_BG);
        UIManager.put("SplitPaneDivider.draggingColor", FBChatColors.CHAT_FRAME_BG);


        UIManager.put("ToolTip.font", new FontUIResource("Lucinda Grande", Font.BOLD, 11));
        UIManager.put("ToolTip.background", new ColorUIResource(FBChatColors.TOOLTIP_BG));
        UIManager.put("ToolTip.foreground", new ColorUIResource(FBChatColors.TOOLTIP_FG));

//   ToolTip.border	BorderUIResource
    }

    public void initContext() {
        
    	this.context = new FBChatContext(trayNotifier, fbIcons, settings);
        ctxIntializer =  new FBChatContextLoader(context, trayNotifier, fbIcons, settings);
        this.context.addListener(this);
    }

    /**
     * Starts the facebook chat application.
     */
    public void start(String[] args) {
        boolean silentLogin = false;
        if ((args != null) && (args.length > 0)) {
            final String silentStartup = FBCIMSettings.SILENT_STATUP.trim();
            for (String arg : args) {
                if (arg.toLowerCase().equals(silentStartup)) {
                    silentLogin = true;
                    break;
                }
            }
        }
        ctxIntializer.login(silentLogin);
    }

    /**
     * Invoked when user signed out of facebook chat.
     */
    public void userSignedOut() {
        initContext();
        start(null);
    }

    public void presenceUpdated(Presence presence) {}

    public void userAvatarLoaded() {}
}