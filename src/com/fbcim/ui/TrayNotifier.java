package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.FBChatContextListener;
import com.fbcim.chat.FBContact;
import com.fbcim.util.ImageUtil;
import com.fbcim.util.Sound;

import org.jivesoftware.smack.packet.Presence;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class TrayNotifier {
    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(TrayNotifier.class.getName());

    /** Provides access to the system tray. */
    private final SystemTray tray;

    /** The tray icon object. */
    private final TrayIcon trayIcon;

    /** The list of visible popup windows. */
    private List<TrayPopupWindow> popupWindows;

    /** The app. context to work with. */
    private FBChatContext context;

    /** Played when new popup window opens. */
    private Sound popupSound;

    /**
     * Constructs tray notifier.
     *
     * @throws Exception
     *          in case if an error happened.
     */
    public TrayNotifier()
        throws Exception {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayIcon = buildTrayIcon();
            tray.add(trayIcon);
            popupSound = new Sound(FBChatSounds.TRAY_POPUP);
        } else {
            String errorMsg = "System Tray not supported on this platform!";
            LOG.severe(errorMsg);
            throw new Exception(errorMsg);
        }

        this.popupWindows = Collections.synchronizedList(new ArrayList<TrayPopupWindow>());
    }

    /**
     * Sets the app. context to work with.
     *
     * @param newVal
     *          the app. context to work with.
     */
    public void setContext(FBChatContext newVal) {
        this.context = newVal;
        this.context.addListener((TrayPopupMenu) trayIcon.getPopupMenu());
    }

    public synchronized void createWindow(FBContact contact) {
        if (!context.getSettings().getShowNotifications()) {
            return;
        }
        final TrayPopupWindow popupWindow = new TrayPopupWindow(contact, context);
        new Thread("TrayPopupWindowCloser") {
            public void run() {
                try {
                    Thread.sleep(3500);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to sleep before closing tray popup!", t);
                }
                popupWindow.closeWindow();
            }
        }.start();
        popupWindow.setLocation(getNewWindowLocation(popupWindow, -1 * popupWindow.getPreferredSize().height));
        popupWindows.add(popupWindow);
        popupWindow.setVisible(true);

        if (context.getSettings().getNotificationSound()) {
            popupSound.play();
        }
        bumpWindows();
    }

    private synchronized void bumpWindows() {
        int totalWindowHeight = 0;// will be relative to the system tray on windows.
        for (int i = 0; i < popupWindows.size(); i++) {
            TrayPopupWindow popupWindow = popupWindows.get(popupWindows.size() - (i + 1));
            Point newLocation = getNewWindowLocation(popupWindow, totalWindowHeight);
            popupWindow.moveTo(newLocation);
            Dimension preferredSize = popupWindow.getPreferredSize();
            totalWindowHeight += preferredSize.height + 5;//keep a space between windows
        }
    }

    public Point getNewWindowLocation(TrayPopupWindow popupWindow, int startHeight) {
        final Rectangle screenBounds;
        final Insets screenInsets;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsConfiguration gc = getGraphicsConfiguration(new Point(screenSize.width - 1,
                screenSize.height - 1));

        if (gc != null) {
            screenInsets = toolkit.getScreenInsets(gc);
            screenBounds = gc.getBounds();
        } else {
            screenInsets = new Insets(0, 0, 0, 0);
            screenBounds = new Rectangle(toolkit.getScreenSize());
        }

        final int screenWidth = screenBounds.width
                - Math.abs(screenInsets.right);
        final int screenHeight = screenBounds.height
                - Math.abs(screenInsets.bottom);

        Dimension preferredSize = popupWindow.getPreferredSize();
        Point newLocation = new Point(screenWidth - preferredSize.width - 5, screenHeight
                - preferredSize.height - startHeight);

        return newLocation;
    }

    private GraphicsConfiguration getGraphicsConfiguration(Point location) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        for (GraphicsDevice screenDevice : screenDevices) {
            if (screenDevice.getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                GraphicsConfiguration gc = screenDevice.getDefaultConfiguration();
                if (gc.getBounds().contains(location)) {
                    return gc;
                }
            }
        }
        return null;
    }

    /**
     * Constructs tray icon.
     *
     * @return  the tray icon to use.
     */
    private TrayIcon buildTrayIcon() {
        Icon icon = ImageUtil.getImageIcon(FBChatImages.TRAY_ICON);
        TrayIcon trayIcon = new TrayIcon(((ImageIcon) icon).getImage(), Msg.getString(FBChatStrings.APP_TITLE), null);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    context.showMainFrame();
                }
            }
        });

        TrayPopupMenu popupMenu = new TrayPopupMenu();
        trayIcon.setPopupMenu(popupMenu);

        return trayIcon;
    }

    public void setPresence(Presence.Mode presence) {
        String statusString = Msg.getString(FBChatStrings.STATUS_OFFLINE);
        if (presence != null) {
            switch (presence) {
                case available:
                    statusString = Msg.getString(FBChatStrings.STATUS_AVAILABLE);
                    break;
                case away:
                    statusString = Msg.getString(FBChatStrings.STATUS_AWAY);
                    break;
            }
        }

        trayIcon.setToolTip(Msg.getString(FBChatStrings.APP_TITLE) + " (" + statusString + ")");
    }

    /**
     * Removes tray icon from the system tray.
     */
    public void hideTrayIcon() {
        if (tray != null) {
            tray.remove(trayIcon);
        }
    }


    /**
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class TrayPopupMenu extends PopupMenu
        implements FBChatContextListener {

        private Menu statusMenu;
        private CheckboxMenuItem available;
        private CheckboxMenuItem away;
        private CheckboxMenuItem offline;

        private MenuItem openChat;

        /**
         * Constructs popup menu.
         */
        public TrayPopupMenu() {
            available = new CheckboxMenuItem(Msg.getString(FBChatStrings.STATUS_AVAILABLE));
            available.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    context.updateXMPPPresence(Presence.Mode.available);
                }
            });

            away = new CheckboxMenuItem(Msg.getString(FBChatStrings.STATUS_AWAY));
            away.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    context.updateXMPPPresence(Presence.Mode.away);
                }
            });

            offline = new CheckboxMenuItem(Msg.getString(FBChatStrings.LOGOUT));
            offline.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    context.signOut(true);
                }
            });

            statusMenu = new Menu(Msg.getString(FBChatStrings.CHANGE_ONLINE_STATUS));
            statusMenu.add(available);
            statusMenu.add(away);
            statusMenu.addSeparator();
            statusMenu.add(offline);
            statusMenu.setEnabled(false);

            openChat = new MenuItem(Msg.getString(FBChatStrings.OPEN_CHAT));
            openChat.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    context.showMainFrame();
                }
            });

            openChat.setEnabled(false);
            MenuItem quit = new MenuItem(Msg.getString(FBChatStrings.QUIT));
            quit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    context.exitFBChat();
                }
            });

            MenuItem settings = new MenuItem(Msg.getString(FBChatStrings.OPTIONS));
            settings.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    context.showSettingsDialog();
                }
            });

            add(statusMenu);
            add(openChat);
            addSeparator();
            add(settings);
            addSeparator();
            add(quit);
        }

        public void userAvatarLoaded() {
            statusMenu.setEnabled(true);
            openChat.setEnabled(true);
            available.setState(true);
        }

        public void userSignedOut() {
            statusMenu.setEnabled(false);
            openChat.setEnabled(false);
//            context.removeListener(this);
        }

        public void presenceUpdated(Presence presence) {
            statusMenu.setEnabled(true);
            openChat.setEnabled(true);
            if (presence.isAway()) {
                available.setState(false);
                away.setState(true);
                offline.setState(false);
            } else if (presence.isAvailable()) {
                available.setState(true);
                away.setState(false);
                offline.setState(false);
            } else {
                statusMenu.setEnabled(false);
                openChat.setEnabled(false);
                available.setState(false);
                away.setState(false);
                offline.setState(true);
            }
        }
    }
}
