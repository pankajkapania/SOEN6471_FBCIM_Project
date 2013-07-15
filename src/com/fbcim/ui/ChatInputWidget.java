package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.chat.CustomButton;
import com.fbcim.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
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
public class ChatInputWidget extends JPanel
    implements KeyListener {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(ChatInputWidget.class.getName());

    /** How long to display "is typing..." notification. */
    private static final int IS_TYPING_DELAY = 4 * 1000;

    /** Used to enter text message. */
    private JTextPane input;

    /** Displays selection of the most popular smileys. */
    private JButton smileyButton;

    /** Used to send entered message. */
    private JButton sendBtn;

    /** Keeps timestamp of the last user typing. */
    private long lastTypedTimestamp;

    /** Displays when contact is typing a message. */
    private JLabel isTypingLabel;

    /** Used to hide "is typing" timer. */
    private Timer hideIsTypingTimer;

    /** Styled document that supports smileys. */
    private StyledDocument document;

    /** Keeps all registered listeners. */
    private final Set<ChatInputWidgetListener> listeners;

    public ChatInputWidget(final FBChatContext context) {
        super(new GridBagLayout());
        this.input = new JTextPane();
        this.input.addKeyListener(this);
        this.input.setMargin(new Insets(6, 6, 10, 10));
        this.input.setDocument(document = new DefaultStyledDocument());
        setMinimumSize(new Dimension(150,112));

        this.listeners = Collections.synchronizedSet(new HashSet<ChatInputWidgetListener>());

        JScrollPane sp = new JScrollPane(input, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createEmptyBorder());
        Dimension scrollBarSize = new Dimension(31,150);
        FBScrollBarUI.decorateScrollBar(sp.getVerticalScrollBar(), false, false,
                FBChatColors.CHAT_INPUT_BORDER, scrollBarSize);

        sp.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        setBackground(FBChatColors.CHAT_FRAME_BOTTOM_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 8, 6, 8));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(FBChatColors.CHAT_FRAME_BG);
        inputPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FBChatColors.CHAT_INPUT_BORDER),
                        BorderFactory.createEmptyBorder(0, 4, 0, 0)
                )
        );
        inputPanel.add(sp, BorderLayout.CENTER);

        JPanel inputIconPanel = new JPanel(new BorderLayout());

        inputIconPanel.setBackground(FBChatColors.CHAT_FRAME_BG);
        inputIconPanel.add(new JLabel(ImageUtil.getImageIcon(FBChatImages.CHAT_INPUT)), BorderLayout.NORTH);
        inputIconPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 6));
        inputPanel.add(inputIconPanel, BorderLayout.WEST);


        smileyButton = new JButton(ImageUtil.getImageIcon(FBChatImages.SMILEY_BUTTON));
        smileyButton.setUI(new BasicButtonUI());
        smileyButton.setBorderPainted(false);
        smileyButton.setBorder(null);
        smileyButton.setRolloverEnabled(true);

        smileyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popup = new JPopupMenu() {
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };

                popup.insert(new SmileyList(context.getFBIcons(), popup), 0);
                popup.setOpaque(false);
                popup.setBorderPainted(false);
                popup.setFocusable(true);
                popup.addPopupMenuListener(new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        smileyButton.setIcon(ImageUtil.getImageIcon(FBChatImages.SMILEY_BUTTON_PRESSED));
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        smileyButton.setIcon(ImageUtil.getImageIcon(FBChatImages.SMILEY_BUTTON));
                    }

                    public void popupMenuCanceled(PopupMenuEvent e) {}
                });
                popup.show(smileyButton, -1, -64);
            }
        });

        isTypingLabel = new JLabel(ImageUtil.getImageIcon(FBChatImages.CONTACT_TYPING));
        isTypingLabel.setForeground(FBChatColors.CONTACT_IS_TYPING);
        isTypingLabel.setFont(new Font("Lucinda Grande", Font.PLAIN, 11));
        isTypingLabel.setVisible(false);

        hideIsTypingTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lastTypedTimestamp < System.currentTimeMillis() - 4000) {
                    setIsTypingVisible(false);
                }
            }
        });
        hideIsTypingTimer.start();

        this.sendBtn = CustomButton.getInstance(Msg.getString(FBChatStrings.SEND), null);
        this.sendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.insets.bottom = 6;
        add(inputPanel, gbc);

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.bottom = 0;
        gbc.insets.top = 4;
        gbc.insets.left = 0;
        gbc.insets.right = 8;
        add(smileyButton, gbc);

        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.bottom = 0;
        gbc.insets.left = 0;
        gbc.insets.top = 4;
        add(isTypingLabel, gbc);

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets.top = 0;
        gbc.insets.right = 0;
        add(sendBtn, gbc);
    }

    /**
     * Registers new listener.
     *
     * @param l
     *          the listener to register.
     */
    public void addListener(ChatInputWidgetListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /**
     * Notifies all registered listeners that user is now typing.
     */
    private void fireKeyTyped() {
        synchronized (listeners) {
            for (ChatInputWidgetListener l : listeners) {
                try {
                    l.keyTyped();
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener about typed key!", t);
                }
            }
        }
    }

    private void fireSendMessage(String msg) {
        synchronized (listeners) {
            for (ChatInputWidgetListener l : listeners) {
                try {
                    l.sendMessage(msg);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to notify listener about sent message!", t);
                }
            }
        }
    }

    /**
     * Sets status of the contact typing.
     *
     * @param newVal
     *          <code>true</code> if contact is currently typing a message,
     *          <code>false</code> otherwise.
     */
    public void setIsTypingVisible(boolean newVal) {
        lastTypedTimestamp = System.currentTimeMillis();
        isTypingLabel.setVisible(newVal);
        validate();
    }

    /**
     * Set "XXX is typing" text.
     *
     * @param text
     *          the text to display while contact is typing a message.
     */
    public void setIsTypingText(String text) {
        isTypingLabel.setText(text);
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            if (e.isControlDown()) {
                try {
                    input.getDocument().insertString(input.getDocument().getLength(), "\n", null);
                } catch (Throwable t) {
                    LOG.log(Level.SEVERE, "Failed to move cursor to the new line!", t);
                }
            } else {
                // Send text message.
                sendMessage();
            }
        } else {
            // Notify other party that we are typing now.
            fireKeyTyped();
        }
    }

    private void sendMessage() {
        String msg = input.getText();
        input.setText("");
        fireSendMessage(msg);
        input.requestFocus();
    }

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    public void insertSmiley(FBIcons.FBSmiley smiley) {
        Style style = document.addStyle(smiley.getText(), null);
        StyleConstants.setIcon(style, smiley.getIcon());
        try {
            document.insertString(document.getLength(), " ", null);
            document.insertString(document.getLength(), smiley.getText(), style);
            document.insertString(document.getLength(), " ", null);
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Failed to insert smiley!", t);
        }
        document.removeStyle(smiley.getText());
    }

    /**
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class SmileyList extends JPanel {
        /** Popup window background. */
        private Image bg;

        public SmileyList(FBIcons icons, final JPopupMenu popup) {
            super(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(5, 7, 10, 0));
            setBackground(Color.red);
            setOpaque(false);
            bg = ImageUtil.getImageIcon(FBChatImages.SMILEY_POPUP_BG).getImage();
            setSize(bg.getWidth(null), bg.getHeight(null));
            setPreferredSize(getSize());
            Vector<FBIcons.FBSmiley> smileys = new Vector<FBIcons.FBSmiley>(icons.getIcons());

            final JList l = new JList(smileys);
            l.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            l.setVisibleRowCount(2);
            l.setBorder(BorderFactory.createEmptyBorder());
            l.setOpaque(false);
            l.setBackground(new Color(0xeb, 0xee, 0xf5));
            l.setCellRenderer(new SmileyListRenderer());
            l.addMouseMotionListener(new MouseAdapter() {
                public void mouseMoved(MouseEvent me) {
                    Point p = new Point(me.getX(),me.getY());
                    int index = l.locationToIndex(p);
                    if (index == l.getModel().getSize() -1) {
                        if (me.getX() < l.getWidth() - 20) {
                            index = -1;
                        }
                    }
                    l.setSelectedIndex(
                            index
                    );
                }
            });

            l.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    FBIcons.FBSmiley selectedSmiley = (FBIcons.FBSmiley) l.getSelectedValue();
                    insertSmiley(selectedSmiley);
                    popup.setVisible(false);
                }

                public void mouseExited(MouseEvent e) {
                    l.clearSelection();
                }
            });

            add(l);
        }

        public void paintComponent(Graphics g) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
            super.paintComponents(g2);
            g2.dispose();
        }


        private class SmileyListRenderer extends DefaultListCellRenderer {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected,boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof FBIcons.FBSmiley) {
                    FBIcons.FBSmiley smiley = (FBIcons.FBSmiley)value;
                    setIcon(smiley.getIcon());
                    setText("");
                }
                setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                return this;
            }
        }
    }
}
