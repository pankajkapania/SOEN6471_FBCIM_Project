package com.fbcim.ui;

import com.fbcim.FBChatContext;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
public class StatusUpdateBox extends JTextField {
    /** The promt text to display. */
    private String promptText;

    /** The app. context to work with. */
    private FBChatContext context;

    /**
     * Constructs status update box object instance.
     *
     * @param context
     *          the app. context to work with.
     * @param promptText
     *          the promt text to display.
     */
    public StatusUpdateBox(FBChatContext context, String promptText) {
        this.context = context;
        this.promptText = promptText;
        setForeground(FBChatColors.STATUS_UPDATE_TEXT);
        setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
        setPreferredSize(new Dimension(160,26));
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    final String newStatus = getText().trim();
                    new Thread() {
                        public void run() {
                            StatusUpdateBox.this.context.updateXMPPStatus(newStatus);
                            StatusUpdateBox.this.context.updateFBStatus(newStatus);
                        }
                    }.start();
                    setText("");
                }
            }
        });
    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!hasFocus() && getText().isEmpty() && promptText != null) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setPaint(getForeground());
            g2.setFont(getFont());

            int dot  = getCaret().getDot();
            Rectangle r = null;

            // Find the caret position
            try {
                r = modelToView(dot);
            } catch (BadLocationException e) {
                // Carat location could not be found
                //  therefore do not attempt to print
                //  the prompt text since
                //  it will not match properly with
                //  the text position
                return;
            }

            int x = r.x;
            int y = r.y + r.height - 3;
            g2.drawString(promptText, x, y);
        }
    }
}
