package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.ads.Ad;
import com.fbcim.ads.AdsGatewayListener;
import com.fbcim.util.FileUtil;
import com.fbcim.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class AdBar extends JPanel
    implements AdsGatewayListener {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(AdBar.class.getName());

    /** Space between ads. */
    private static final int PADDING = 5;

    /** How much to shift images during a single animation step. */
    private static final int SLIDE_STEP = 80;

    /** Predefined ad height. */
    private static final int AD_HEIGHT = 45;

    /** The list of ads currently displayed in this bar. */
    private List<Ad> ads;

    /** Animation timer. */
    private Timer animationTimer;

    /** Offset of the first image in the list. */
    private int xOffset = 0;

    /** The number of scrolled ads in the current animation cycle. */
    private int scrolledAdsCount = 0;

    /** Number of ads to scroll in current animation cycle.  */
    private int ads2Scroll;

    /** <code>true</code> if ads should be scrolled left */
    private boolean isScrollLeft;

    /** Last time ads were scrolled. */
    private long lastScrollTimestamp;

    /** Used to scroll ads automatically. */
    private Timer autoscrollTimer;

    /**
     * Constructs advertisement bar.
     */
    public AdBar(FBChatContext context) {
        super(new BorderLayout());
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(11, 11, 0, 0),
                        BorderFactory.createMatteBorder(0, PADDING, 0, PADDING, FBChatColors.AD_BAR_BG)
                )
        );
        setBackground(FBChatColors.CHAT_FRAME_BG);
        add(new AdContainer());

        final Dimension adSize = new Dimension(200, 66);
        setMinimumSize(adSize);
        setPreferredSize(adSize);

        // Get initial list of ads.
        ads = new ArrayList<Ad>();
        ads.addAll(
                context.getAdsGateway().getAds()
        );

        // Listen for ad updates.
        context.getAdsGateway().addListener(this);

        // Setup animation timer.
        animationTimer = new Timer(50, new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                repaint();
            }
        });

        autoscrollTimer = new Timer(5 * 1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - lastScrollTimestamp > 15 * 1000) {
                    scrollAds(true);
                }
            }
        });
        autoscrollTimer.start();
        lastScrollTimestamp = System.currentTimeMillis();
    }

    public void stop() {
        autoscrollTimer.stop();
        animationTimer.stop();
    }

    /**
     * Initializes ads scrolling to the specified direction.
     *
     * @param left
     *          <code>true</code> if ads should be scrolled left,
     *          <code>false</code> otherwise.
     */
    private void scrollAds(boolean left) {
        lastScrollTimestamp = System.currentTimeMillis();
        if (!isVisible()) {
            return;
        }
        if (!animationTimer.isRunning()) {
            ads2Scroll = getVisibleAdsCount();
            scrolledAdsCount = 0;
            xOffset = 0;
            animationTimer.start();
        }
        isScrollLeft = left;
    }

    /**
     * Returns number of the currently visible ads.
     *
     * @return  the number of the currently visible ads.
     */
    private synchronized int getVisibleAdsCount() {
        int x = -xOffset;
        int adsCount = 0;
        for (Ad ad : ads) {
            x += ad.img.getIconWidth() + PADDING;
            adsCount++;
            if (x > getWidth()) {
                break;
            }
        }
        return adsCount;
    }

    /**
     * Invoked when the list of ads is updated.
     */
    public synchronized void adsLoaded(List<Ad> newAds) {
        ads.clear();
        ads.addAll(newAds);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }

    /**
     * Displays ads.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class AdContainer extends JPanel {

        /** Used to scroll ads to the left. */
        private JButton scrollLeftBtn;

        /** Used to scroll ads to the right. */
        private JButton scrollRightBtn;

        /** Keeps information about current placement of ads. */
        private Map<Rectangle, Ad> adsMap;

        /** The current mouse point. */
        private Point mousePoint;

        private JToolTip tooltip;

        /**
         * Constructs ad container instance.
         */
        AdContainer() {
            super(new GridBagLayout());

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            adsMap = Collections.synchronizedMap(new HashMap<Rectangle, Ad>());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Ad ad = getAdAtPoint(e.getPoint());
                    if (ad != null) {
                        FileUtil.launchUrl(ad.getAdUrl());
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePoint = e.getPoint();
                    Ad ad = getAdAtPoint(mousePoint);
                    if (ad == null) {
                        setCursor(Cursor.getDefaultCursor());
                        setToolTipText(null);
                    } else {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        setToolTipText(ad.Text);
                    }
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    mousePoint = null;
                    repaint();
                }
            });

            scrollLeftBtn = new JButton();
            scrollLeftBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    scrollAds(true);
                }
            });
            scrollLeftBtn.setUI(new BasicButtonUI());
            scrollLeftBtn.setBorderPainted(false);
            scrollLeftBtn.setOpaque(false);
            scrollLeftBtn.setBorder(BorderFactory.createEmptyBorder(0, PADDING, 0, 0));
            scrollLeftBtn.setRolloverEnabled(true);
            scrollLeftBtn.setIcon(ImageUtil.getImageIcon(FBChatImages.AD_LEFT_BTN));
            scrollLeftBtn.setRolloverIcon(ImageUtil.getImageIcon(FBChatImages.AD_LEFT_BTN_ROLLOVER));

            scrollRightBtn = new JButton();
            scrollRightBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    scrollAds(false);
                }
            });
            scrollRightBtn.setUI(new BasicButtonUI());
            scrollRightBtn.setBorderPainted(false);
            scrollRightBtn.setOpaque(false);
            scrollRightBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, PADDING));
            scrollRightBtn.setRolloverEnabled(true);
            scrollRightBtn.setIcon(ImageUtil.getImageIcon(FBChatImages.AD_RIGHT_BTN));
            scrollRightBtn.setRolloverIcon(ImageUtil.getImageIcon(FBChatImages.AD_RIGHT_BTN_ROLLOVER));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            add(scrollLeftBtn, gbc);

            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridx = 1;
            add(Box.createHorizontalGlue(), gbc);

            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.gridx = 2;
            add(scrollRightBtn, gbc);

            tooltip = new JToolTip();
            tooltip.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        }

        /**
         * Returns ad displayed at given point.
         *
         * @param p
         *          the point to check.
         *
         * @return  ad at given point (<code>null</code> if nothing was found).
         */
        private Ad getAdAtPoint(Point p) {
            synchronized (adsMap) {
                for (Rectangle rect : adsMap.keySet()) {
                    if (rect.contains(p)) {
                        return adsMap.get(rect);
                    }
                }
            }
            return null;
        }

        public JToolTip createToolTip() {
            JToolTip tooltip = super.createToolTip();
            tooltip.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
            return tooltip;
        }

        public Point getToolTipLocation(MouseEvent event) {

            synchronized (adsMap) {
                for (Rectangle rect : adsMap.keySet()) {
                    if (rect.contains(event.getPoint())) {
                        Ad ad = adsMap.get(rect);
                        tooltip.setToolTipText(ad.Text);
                        int tooltipWidth = (getFontMetrics(tooltip.getFont()).stringWidth(ad.Text) + 20);
                        int tooltipX = rect.x + (rect.width - tooltipWidth) / 2;
                        if (tooltipX >= getWidth()) {
                            tooltipX = rect.x;
                        }
                        return new Point(tooltipX, 40);
                    }
                }
            }
            return super.getToolTipLocation(event);
        }

        /**
         * Responsible for component painting/animation.
         *
         * @param g
         *          the graphics context to work with.
         */
        public synchronized void paintComponent(Graphics g) {
            // Paint background.
            g.setColor(FBChatColors.AD_BAR_BG);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Calculate animation offset.
            int x = 0;
            if (animationTimer.isRunning()) {
                if (isScrollLeft) {
                    xOffset += SLIDE_STEP;
                    x = - xOffset;

                    if (x + ads.get(0).img.getIconWidth() + PADDING < 0) {
                        xOffset = 0;
                        x = 0;
                         ads.add(
                            ads.remove(0)
                         );
                        scrolledAdsCount++;
                    }
                } else {
                    if (xOffset > 0) {
                        ads.add(0,
                                ads.remove(ads.size() - 1)
                        );
                        xOffset = - (ads.get(0).img.getIconWidth() + PADDING);
                        scrolledAdsCount++;
                    } else {
                        xOffset+= SLIDE_STEP/2;
                    }
                    x = xOffset;
                }
            }
            int y = PADDING;

            // Display ads.
            synchronized (adsMap) {
                adsMap.clear();
                while (true) {
                    for (Ad ad : ads) {
                        Rectangle adRect = new Rectangle(x, y, ad.img.getIconWidth(), AD_HEIGHT);

                        Image adImg = ad.img.getImage();
                        if ((mousePoint != null) && adRect.contains(mousePoint) && (ad.imgRollover != null)) {
                            adImg = ad.imgRollover.getImage();
                        }

                        g.drawImage(adImg, x, y, adImg.getWidth(null), AD_HEIGHT, null);
                        adsMap.put(adRect, ad);
                        x += ad.img.getIconWidth() + PADDING;
                        if (x > getWidth()) {
                            break;
                        }
                    }
                    if (x > getWidth()) {
                        break;
                    }
                }
            }

            if (scrolledAdsCount >= ads2Scroll) {
                animationTimer.stop();
                if (ads.get(0).img.getIconWidth() + xOffset <= 0) {
                    ads.add(
                            ads.remove(0)
                    );
                }
                scrolledAdsCount = 0;
                xOffset = 0;
            }
        }
    }
}