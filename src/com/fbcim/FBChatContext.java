package com.fbcim;

import com.fbcim.ads.AdsGateway;
import com.fbcim.chat.FBChat;
import com.fbcim.chat.FBChatManager;
import com.fbcim.chat.FBChatPacketListener;
import com.fbcim.chat.FBContact;
import com.fbcim.chat.FBContactList;
import com.fbcim.chat.FBRosterListener;
import com.fbcim.fql.FBConnection;
import com.fbcim.ui.AvatarCache;
import com.fbcim.ui.FBCIMOptionsDialog;
import com.fbcim.ui.FBChatImages;
import com.fbcim.ui.FBChatMainFrame;
import com.fbcim.ui.FBIcons;
import com.fbcim.ui.MessageDialog;
import com.fbcim.ui.TrayNotifier;
import com.fbcim.util.FileUtil;
import com.fbcim.util.ImageUtil;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.VCard;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
public class FBChatContext {

    /** Get logger instance to work with. */
	protected static Logger LOG = Logger.getLogger(FBChatContext.class.getCanonicalName());

    /**  */
	protected static final int CHAT_HISTORY_SAVE_INTERVAL = 3 * 60 * 1000;

    /**  */
	protected static final int FB_STATUS_UPDATE_INTERVAL = 15 * 60 * 1000;

	protected static final int FB_STATUS_UPDATE_INITIAL_DELAY = 1 * 1000;

    /** How often to request new list of ads from server. */
	protected static final int UPDATE_ADS_INTERVAL = 15 * 60 * 1000;

    /** Used to login to facebook and retrieve oauth token. */
	protected FBLoginManager fbLoginManager;

    /** The chat app main frame. */
	protected FBChatMainFrame fbChatMainFrame;

    /** The app. settings object. */
	protected FBCIMSettings settings;

    /** Connection to facebook graph api. */
	protected FBConnection fbConnection;

    /** The actual connection to facebook chat. */
	protected XMPPConnection xmppConnection;

    /** The currently logged in user. */
	protected Roster roster;

    /** The currently logged in user data. */
	protected VCard vCard;

    /** The currently logged in user avatar (mid-size). */
	protected ImageIcon avatarMid;

    /** The currently logged in user avatar (big). */
	protected ImageIcon avatarBig;

    /** The current user presence status. */
	protected Presence presence;

    /** The current contact list. */
	protected FBContactList contactList;

    /** Keeps all chats initialized during current session. */
	protected FBChatManager chatManager;

    /** The list of registered listeners. */
	protected Set<FBChatContextListener> listeners;

    /** The currently active filter used to filter incoming packets. */
	protected PacketFilter filter;

    /** The currently logged in user id.  */
	protected String userId;

    /** Incoming packets listener. */
	protected FBChatPacketListener listener;

    /** Responsible for contacts FB status update. */
	protected Timer serviceTimer;

    /** Provides access to the system tray. */
	protected TrayNotifier trayNotifier;

    /** Keeps smiley icons. */
	protected FBIcons fbIcons;

    /** The avatar cache. */
	protected AvatarCache avatarCache;

    /** Gateway to ads server. */
	protected AdsGateway adsGateway;

    /**
     * Constructs chat app. context object.
     *
     * @param trayNotifier
     *          tray notifier object.
     */
    public FBChatContext(TrayNotifier trayNotifier, FBIcons fbIcons, FBCIMSettings settings) {
        this.listeners = Collections.synchronizedSet(new HashSet<FBChatContextListener>());
        this.trayNotifier = trayNotifier;
        this.fbIcons = fbIcons;
        this.settings = settings;
        this.trayNotifier.setContext(this);
        this.contactList = new FBContactList(this);
        this.serviceTimer = new Timer("ServiceTimer",true);
        this.avatarCache = new AvatarCache(FileUtil.getAvatarCacheDir());
        this.adsGateway = new AdsGateway();

        this.fbChatMainFrame = new FBChatMainFrame(this);
        this.fbLoginManager = new FBLoginManager(this, new FBLoginManagerListener() {
            public void loginSuccessful(boolean showMainFrame) {
                openContactList(showMainFrame);
                loadContactList();
            }

            public void loginCanceled() {
                new MessageDialog(null, "Error", "Login to Facebook chat canceled!", "OK").showMessageDialog();

//                JOptionPane.showMessageDialog(null, "Login to Facebook chat canceled!");
                signOut(true);
            }

            public void loginFailed(Throwable... t) {
                new MessageDialog(null, "Error", "Login to Facebook chat failed!", "OK").showMessageDialog();
//                JOptionPane.showMessageDialog(null, "Login to Facebook chat failed!");
                signOut(true);
            }
        });


        if (trayNotifier != null) {
            trayNotifier.setPresence(null);
        }
    }

    public AdsGateway getAdsGateway() {
        return adsGateway;
    }

    public void login(boolean silentLogin) {
        fbLoginManager.setSilentLogin(silentLogin);
        fbLoginManager.login();
    }

    /**
     * Opens window that displays list of contacts.
     */
    public void openContactList(boolean showMainFrame) {
        fbChatMainFrame.setSize(settings.getFrameSize());

        // Get saved frame location and size.
        Point p = settings.getFrameLocation();

        // Get current screen size.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Position main frame on the screen.
        if ((p == null) ||
            (p.x < 0) || (p.y < 0) ||
            (p.x > screenSize.width) || (p.y > screenSize.height))  {
            fbChatMainFrame.setLocationRelativeTo(null);
        } else {
            fbChatMainFrame.setLocation(p.x, p.y);
        }
        if (showMainFrame) {
            showMainFrame();
        }
    }

    public void showMainFrame() {
        if (isLoggedIn()) {
            fbChatMainFrame.setVisible(true);
        }
    }

    public void showHelp() {
        FileUtil.launchUrl(FBCIMSettings.HELP_URL);
     }

    public void showSettingsDialog() {
        FBCIMOptionsDialog optionsDialog = new FBCIMOptionsDialog(fbChatMainFrame, getSettings());
        if (isLoggedIn()) {
            optionsDialog.setLocationRelativeTo(fbChatMainFrame);
        } else {
            optionsDialog.setLocationRelativeTo(fbLoginManager.getLoginFrame());
        }
        optionsDialog.setVisible(true);

        // Make sure we update UI according to the latest settings.
        chatManager.updateGamesBarVisibility();
    }

    private void loadContactList() {
        if (SwingUtilities.isEventDispatchThread()) {
            new Thread() {
                public void run() {
                    loadContactList();
                }
            }.start();
            return;
        } else {
            getContactList().loadContactList();
            this.serviceTimer.schedule(new UpdateFBStatusTask(), FB_STATUS_UPDATE_INITIAL_DELAY, FB_STATUS_UPDATE_INTERVAL);
            this.serviceTimer.schedule(new SaveChatHistoryTask(), CHAT_HISTORY_SAVE_INTERVAL, CHAT_HISTORY_SAVE_INTERVAL);
            this.serviceTimer.schedule(new UpdateAdsTask(), UPDATE_ADS_INTERVAL, UPDATE_ADS_INTERVAL);
        }
    }

    /**
     * Registers given context listener.
     *
     * @param l
     *          the given context listener.
     */
    public void addListener(FBChatContextListener l) {
        synchronized (listeners) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }
    }

    public void removeListener(FBChatContextListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private void fireUserAvatarLoaded() {
        synchronized (listeners) {
            for (FBChatContextListener l : listeners) {
                try {
                    l.userAvatarLoaded();
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener that user avatar was loaded!", t);
                }
            }
        }
    }

    private void fireUserSignedOut() {
        synchronized (listeners) {
            for (FBChatContextListener l : listeners) {
                try {
                    l.userSignedOut();
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener that user signed out from chat!", t);
                }
            }
        }
    }

    private void firePresenceUpdated(Presence presence) {
        synchronized (listeners) {
            for (FBChatContextListener l : listeners) {
                try {
                    l.presenceUpdated(presence);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener that user presence has been updated!", t);
                }
            }
        }
    }

    /**
     * Terminates facebook chat and exists application.
     */
    public void exitFBChat() {
        signOut(false);
        if (trayNotifier != null) {
            trayNotifier.hideTrayIcon();
        }
        System.exit(0);
    }

    /**
     * Returns the contact list object instance.
     *
     * @return  the contact list object instance.
     */
    public FBContactList getContactList() {
        return contactList;
    }

    /**
     * Returns settings object instance.
     *
     * @return  the settings object instance.
     */
    public FBCIMSettings getSettings() {
        return settings;
    }


    private void openFBConnection() {
        fbConnection = new FBConnection(getSettings().getOAuthToken());
    }

    public synchronized FBConnection getFBConnection() {
        if (fbConnection == null) {
            openFBConnection();
        }

        return fbConnection;
    }

    /**
     * Sets facebook chat connection.
     *
     * @param newVal
     *          the connection to save reference to.
     */
    public void setXmppConnection(XMPPConnection newVal)
        throws Throwable {
        this.xmppConnection = newVal;
        this.roster = xmppConnection.getRoster();
        this.vCard = new VCard();

        String tmpUserId = xmppConnection.getUser();
        int  i = tmpUserId.indexOf("/");
        if (i > 0) {
            userId = tmpUserId.substring(0, i);
        } else {
            userId = tmpUserId;
        }

        this.chatManager = new FBChatManager(this);
        contactList.addContactListListener(this.chatManager);

        String user = "";
        vCard.load(xmppConnection, user);

        byte[] avatarBytes = vCard.getAvatar();
        if ((avatarBytes == null) || (avatarBytes.length == 0)) {
            // Try again.
            avatarBytes = vCard.getAvatar();
        }

        if ((avatarBytes != null) && (avatarBytes.length > 0)) {
            this.avatarMid = ImageUtil.scaleAvatarMid(new ImageIcon(avatarBytes));
            this.avatarBig = ImageUtil.scaleAvatarBig(new ImageIcon(avatarBytes));
        }

        if (avatarMid == null) {
            this.avatarMid = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_MEDIUM);
            this.avatarBig = ImageUtil.getImageIcon(FBChatImages.UNKNOWN_AVATAR_MEDIUM);
        }

        startPacketListening();
        fireUserAvatarLoaded();

        if (trayNotifier != null) {
            trayNotifier.setPresence(Presence.Mode.available);
        }
    }

    /**
     * Returns archive directory used by the current app. instance.
     *
     * @return  the archive directory to use.
     */
    public File getArchiveDir() {
        File archiveDir = new File(FileUtil.getArchiveDir(), userId);
        archiveDir.mkdirs();
        return archiveDir;
    }


     /**
     * Listen for incoming packets and roster
     */

    public void startPacketListening(){
        try{
            filter = new PacketTypeFilter(Message.class);
            PacketCollector collector = xmppConnection.createPacketCollector(filter);
            listener = new FBChatPacketListener(this);
            xmppConnection.addPacketListener(listener, filter);
            roster.addRosterListener(new FBRosterListener(this));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public FBChatPacketListener getPacketListener() {
        return listener;
    }

    public boolean isLoggedIn() {
        return xmppConnection != null;
    }


    /**
     * Returns current XMPP connection.
     *
     * @return  the current XMPP connection.
     */
    public XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    /**
     * Returns the currently logged in user.
     *
     * @return  the currently logged in user.
     */
    public Roster getRoster() {
        return roster;
    }

    /**
     * Returns mid-size avatar of the currently logged in user.
     *
     * @return  the mid-size avatar of the currently logged in user.
     */
    public ImageIcon getAvatarMid() {
        return avatarMid;
    }

    /**
     * Returns big avatar of the currently logged in user.
     *
     * @return  the big avatar of the currently logged in user.
     */
    public ImageIcon getAvatarBig() {
        // @todo    we need to access big avatar too.
        return getAvatarMid();
    }

    public String getUserId() {
        return userId;
    }

    /**
     * Returns currently logged in user name.
     *
     * @return  the currently logged in user name.
     */
    public String getUserName() {
        if (vCard != null) {
            return vCard.getField("FN");
        }
        return null;
    }

    public String getFirstName() {
        String fullName = getUserName();
        int i = fullName.indexOf(" ");
        if (i > 0) {
            fullName = fullName.substring(0, i);
        }
        return fullName;
    }

    /**
     * Update the presence to the server
     *
     * @param statusText
     *          the status text to set.
     */
    public void updateXMPPStatus(String statusText) {
        try {
            if (presence == null) {
                presence = new Presence(Presence.Type.available);
            }
            if (statusText != null) {
                presence.setStatus(statusText);
            }

            xmppConnection.sendPacket(this.presence);
        }
        catch(Exception e){
            LOG.warning("Cannot update presence " + e.toString());
        }
    }

    public void updateXMPPPresence(Presence.Mode newVal) {
        if (presence == null) {
            updateXMPPStatus(null);
        }
        presence.setMode(newVal);
        xmppConnection.sendPacket(this.presence);

        if (trayNotifier != null) {
            trayNotifier.setPresence(newVal);
        }

        firePresenceUpdated(this.presence);
    }

    public void showTrayPopup(FBContact contact) {
        if (trayNotifier != null) {
            trayNotifier.createWindow(contact);
        }
    }

    /**
     * Post new status message on facebook wall.
     *
     * @param statusText
     *          the status text to post.
     */
    public void updateFBStatus(String statusText) {
        getFBConnection().updateFBStatus(statusText);
    }

    /**
     * Sings out the currently logged in user.
     */
    public void signOut(boolean notify) {
        if (fbChatMainFrame != null) {
            fbChatMainFrame.setVisible(false);
            fbChatMainFrame.dispose();
            fbChatMainFrame = null;
        }


        if ((xmppConnection != null) && (xmppConnection.isConnected())) {
            xmppConnection.disconnect(new Presence(Presence.Type.unavailable));
            xmppConnection = null;
        }

        if (trayNotifier != null) {
            trayNotifier.setPresence(null);
        }

        if (fbConnection != null) {
            fbConnection.disconnect();
            fbConnection = null;
        }

        if (serviceTimer != null) {
            serviceTimer.cancel();
            serviceTimer = null;
        }

        roster = null;
        vCard = null;
        avatarMid = null;
        avatarBig = null;
        presence = null;

        if (contactList != null) {
            contactList.clear();
            contactList = null;
        }

        if (chatManager != null) {
            chatManager.clear();
            chatManager = null;
        }

        filter = null;
        userId = null;
        listener = null;

        if (notify) {
            fireUserSignedOut();
        }
        synchronized (listeners) {
            listeners.clear();
        }
//
//        if (listeners != null) {
//            synchronized (listeners) {
//                listeners.clear();
//            }
//            listeners = null;
//        }
    }

    /**
     * Starts/resumes chat with given contact.
     *
     * @param targetContact
     *          the contact to chat with.
     *
     * @return  the chat object associated with given contact.
     */
    public FBChat chat(FBContact targetContact) {
        FBChat chat = chatManager.getChat(targetContact);
        return chat;
    }


    /**
     * Returns contact with given id
     *
     * @param contactId
     *          the contact id.
     *
     * @return  the contact with given id (or <code>null</code> if nothing was found).
     */
    public FBContact getFBContact(String contactId) {
        return contactList.getFBContact(contactId);
    }

    public FBChatManager getChatManager() {
        return chatManager;
    }

    /**
     * Returns registered smiley icons.
     *
     * @return  the list of registered smiley icons.
     */
    public FBIcons getFBIcons() {
        return fbIcons;
    }

    public AvatarCache getAvatarCache() {
        return avatarCache;
    }

    public void testFileTransfer(FBContact targetContact) {

        FileTransferManager ftm = new FileTransferManager(xmppConnection);
        OutgoingFileTransfer t = ftm.createOutgoingFileTransfer(targetContact.getId());
        try {
            t.sendFile("c:\\canada-flag.jpg", 6673, "Test File");
            while (!t.isDone()) {
                try {
                    Thread.sleep(1000);
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        System.out.println(targetContact);
    }



    /**
     * Updates fb status of all online items.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class UpdateFBStatusTask extends TimerTask {
        /** <code>true</code> if we should not update offline contacts. */
        private boolean skipOffline = false;

        @Override
        public void run() {
            contactList.updateContactsFBStatus(skipOffline);
            if (!skipOffline) {
                skipOffline = true;
            }
        }
    }

    /**
     * Updates fb status of all online items.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class SaveChatHistoryTask extends TimerTask {
        /** <code>true</code> if we should not update offline contacts. */
        private boolean skipOffline = false;

        @Override
        public void run() {
            chatManager.saveChatHistory();
        }
    }

    /**
     * Retrieves new ads from server.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class UpdateAdsTask extends TimerTask {
        @Override
        public void run() {
            adsGateway.updateAds();
        }
    }
}