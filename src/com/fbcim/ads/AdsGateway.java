package com.fbcim.ads;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
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
public class AdsGateway {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(AdsGateway.class.getName());

    /** URL to retrieve the list of ads from.  */
    public static final String ADS_URL = "";

    /** Used for the search instance synchronization. */
    private final Gson gson;

    /** Up-to-date list of ads. */
    private List<Ad> ads;

    /** The list of assigned listeners. */
    private final Set<AdsGatewayListener> listeners;

    /** Retrieves ads from remote server. */
    private AdsDownloader downloader;

    /** Used to cache ad images. */
    private Map<String, ImageIcon> adCache;

    /**
     * Constructs ads gateway object instance.
     */
    public AdsGateway() {
        gson = new Gson();
        ads = new ArrayList<Ad>();
        listeners = new HashSet<AdsGatewayListener>();
        adCache = new HashMap<String, ImageIcon>();
        updateAds();
    }

    /**
     * Initializes ads update.
     */
    public synchronized void updateAds() {
        if (downloader != null) {
            return;
        }
        downloader = new AdsDownloader();
        downloader.start();
    }

    /**
     * Registers new listener.
     *
     * @param l
     *          the listener to register.
     */
    public void addListener(AdsGatewayListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /**
     * Unregisters given listener.
     *
     * @param l
     *          the listener to unregister.
     */
    public void removeListener(AdsGatewayListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    /**
     * Returns up-to-date list of ads.
     *
     * @return  the up-to-date list of ads.
     */
    public List<Ad> getAds() {
        synchronized(this) {
            return ads;
        }
    }

    /**
     * Sets up-to-date list of ads.
     */
    private void setAds(Ad[] newAds) {
        synchronized (this) {
            ads.clear();
            for (Ad ad : newAds) {
                if (ad.img != null) {
                    ads.add(ad);
                }
            }
        }

        fireAdsLoaded();

        // Cleanup downloader reference.
        downloader = null;
    }

    /**
     * Notifies listeners that list of ads has been updated.
     */
    private void fireAdsLoaded() {
        synchronized (listeners) {
            for(AdsGatewayListener l : listeners) {
                try {
                    l.adsLoaded(ads);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener: " + l, t);
                }
            }
        }
    }

    /**
     * Retrieves external IP address.
     *
     * @return  the external IP address.
     */
    private String getExternalIP() {
        BufferedReader in = null;
        String externalIP = null;
        try {
            URL whatismyip = new URL("http://automation.whatismyip.com/n09230945.asp");
            in = new BufferedReader(new InputStreamReader(
            whatismyip.openStream()));
            externalIP = in.readLine().trim();
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "First attempt to detect external IP address failed!");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable t) {
                    in = null;
                }
            }
        }

        if (externalIP == null) {
            try {
                URL whatismyip = new URL("http://jsonip.appspot.com/");
                in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
                String externalIpJson = in.readLine().trim();
                IPAddress addr = gson.fromJson(externalIpJson, IPAddress.class);
                externalIP = addr.ip;
            } catch (Throwable t) {
                LOG.log(Level.SEVERE, "Second attempt to detect external IP address failed!");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Throwable t) {
                        in = null;
                    }
                }
            }
        }

        if (externalIP == null) {
            try {
                InetAddress thisIp = InetAddress.getLocalHost();
                externalIP = thisIp.getHostAddress();
            } catch (Throwable t2) {
                LOG.log(Level.SEVERE, "Third attempt to detect external IP address failed!");
                externalIP = "127.0.0.1";
            }
        }

        return externalIP;
    }

    /**
     * Retrieves JSon format file sharing data.
     *
     * @param url
     *          the target url to send request to.
     *
     * @return the server response (in JSon format).
     */
    private String getAdsFromServer(String url) {
        URL targetUrl;
        HttpURLConnection connection = null;
        InputStream is = null;
        String externalIp = getExternalIP();

        try {
            targetUrl = new URL(String.format(url, externalIp));

            // Init connection.
            connection = (HttpURLConnection)targetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Get Response
            is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                if (response.length() > 0) {
                    response.append('\r');
                }
                response.append(line);
            }
            rd.close();
            return response.toString().trim();

        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Could not load list of ads!", t);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Could not close connection to remote server!", t);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * Responsible for downloading fresh ads from web-server.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class AdsDownloader extends Thread {
        /**
         * Downloads list of ads from server.
         */
        public void run() {
            // Get list of ads from server.
            String data = getAdsFromServer(ADS_URL);
            AdContainer newAds = gson.fromJson(data, AdContainer.class);

            // Load ads images.
            for (Ad ad : newAds.data) {
                if (ad.sizeType == 0) {
                    continue;
                }
                try {
                    ImageIcon adImg = adCache.get(ad.src);
                    if (adImg == null) {
                        adImg = new ImageIcon(new URL(ad.src));
                        adCache.put(ad.src, adImg);
                    }
                    ad.img = adImg;

                    ImageIcon adHoverImg = adCache.get(ad.srcHover);
                    if (adHoverImg == null) {
                        adHoverImg = new ImageIcon(new URL(ad.srcHover));
                        adCache.put(ad.srcHover, adHoverImg);
                    }
                    ad.imgRollover = adHoverImg;

                } catch (MalformedURLException e) {
                    LOG.log(Level.SEVERE, "Failed to load image from url: " + ad.src, e);
                }
            }

            // Notify listeners that list of ads has been updated.
            setAds(newAds.data);
        }
    }
}
