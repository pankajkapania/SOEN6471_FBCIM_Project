package com.fbcim;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.fbcim.ads.AdsGateway;
import com.fbcim.chat.FBContactList;
import com.fbcim.ui.AvatarCache;
import com.fbcim.ui.FBChatMainFrame;
import com.fbcim.ui.FBIcons;
import com.fbcim.ui.MessageDialog;
import com.fbcim.ui.TrayNotifier;
import com.fbcim.util.FileUtil;

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

public class FBChatContextLoader {
	
	private FBChatContext context;
	
	public FBChatContextLoader(FBChatContext context,TrayNotifier trayNotifier, FBIcons fbIcons, FBCIMSettings settings){
		this.context = context;
		intializeFBChatContext(trayNotifier, fbIcons, settings);
	}
	
	private void intializeFBChatContext(TrayNotifier trayNotifier, FBIcons fbIcons, FBCIMSettings settings){
		context.listeners = Collections.synchronizedSet(new HashSet<FBChatContextListener>());
		context.trayNotifier = trayNotifier;
		context.fbIcons = fbIcons;
		context.settings = settings;
		context.trayNotifier.setContext(context);
		context.contactList = new FBContactList(context);
		context.serviceTimer = new Timer("ServiceTimer",true);
		context.avatarCache = new AvatarCache(FileUtil.getAvatarCacheDir());
		context.adsGateway = new AdsGateway();

		context.fbChatMainFrame = new FBChatMainFrame(context);
		context.fbLoginManager = new FBLoginManager(context, new FBLoginManagerListener() {
			public void loginSuccessful(boolean showMainFrame) {
				openContactList(showMainFrame);
				loadContactList();
			}

			public void loginCanceled() {
				new MessageDialog(null, "Error", "Login to Facebook chat canceled!", "OK").showMessageDialog();

//	            JOptionPane.showMessageDialog(null, "Login to Facebook chat canceled!");
				context.signOut(true);
			}

			public void loginFailed(Throwable... t) {
				new MessageDialog(null, "Error", "Login to Facebook chat failed!", "OK").showMessageDialog();
//	            JOptionPane.showMessageDialog(null, "Login to Facebook chat failed!");
				context.signOut(true);
			}
		});


		if (trayNotifier != null) {
			trayNotifier.setPresence(null);
		}
	}
	
	/**
	 * Opens window that displays list of contacts.
	 */
    private void openContactList(boolean showMainFrame) {
    	context.fbChatMainFrame.setSize(context.settings.getFrameSize());

        // Get saved frame location and size.
        Point p = context.settings.getFrameLocation();

        // Get current screen size.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Position main frame on the screen.
        if ((p == null) ||
            (p.x < 0) || (p.y < 0) ||
            (p.x > screenSize.width) || (p.y > screenSize.height))  {
        	context.fbChatMainFrame.setLocationRelativeTo(null);
        } else {
        	context.fbChatMainFrame.setLocation(p.x, p.y);
        }
        if (showMainFrame) {
        	context.showMainFrame();
        }
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
        	context.getContactList().loadContactList();
        	context.serviceTimer.schedule(new UpdateFBStatusTask(), context.FB_STATUS_UPDATE_INITIAL_DELAY, context.FB_STATUS_UPDATE_INTERVAL);
        	context.serviceTimer.schedule(new SaveChatHistoryTask(), context.CHAT_HISTORY_SAVE_INTERVAL, context.CHAT_HISTORY_SAVE_INTERVAL);
        	context.serviceTimer.schedule(new UpdateAdsTask(), context.UPDATE_ADS_INTERVAL, context.UPDATE_ADS_INTERVAL);
        }
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
            context.contactList.updateContactsFBStatus(skipOffline);
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
            context.chatManager.saveChatHistory();
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
            context.adsGateway.updateAds();
        }
    }
} 
