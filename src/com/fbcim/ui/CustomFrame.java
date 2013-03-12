package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
public abstract class CustomFrame extends JFrame
    implements ITitleBarEventListener {

    private int MIN_FRAME_WIDTH = 230;

    private int MIN_FRAME_HEIGHT = 290;

    /** The title bar component. */
    protected CustomTitleBar titleBar;

    /** The actual container component. */
    protected JPanel container;

    /** The app. context to work with. */
    protected FBChatContext context;

    /**
     * Constructs frame object.
     */
    public CustomFrame(FBChatContext context) {
        this.context = context;
        setUndecorated(true);
        setIconImage(
                ImageUtil.getImageIcon(FBChatImages.FRAME_ICON).getImage()
        );


        this.container = new JPanel(new BorderLayout());
        titleBar = createTitleBar();
        JComponent titleBarContainer = new JPanel(new BorderLayout());
        titleBarContainer.add(titleBar, BorderLayout.CENTER);
        titleBarContainer.setBorder(createTitleBarBorder());
        container.add(
                titleBarContainer,
                BorderLayout.NORTH
        );
        container.add(
                createContentPane(),
                BorderLayout.CENTER
        );

        container.setBorder(createContainerBorder());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(
                this.container,
                BorderLayout.CENTER
        );

        // Make title bar draggable.
        ComponentMover cm = new ComponentMover(this, titleBar);

        CustomFrameResizer resizer = new CustomFrameResizer();
        addMouseListener(resizer);
        addMouseMotionListener(resizer);

        super.setTitle(Msg.getString(FBChatStrings.APP_TITLE));
    }

    protected Border createContainerBorder() {
        return BorderFactory.createLineBorder(FBChatColors.FRONT_PAGE_BORDER);
    }

    protected Border createTitleBarBorder() {
        return BorderFactory.createEmptyBorder();
    }

    /**
     * Create custom title bar.
     *
     * @return  the custom title bar component.
     */
    protected CustomTitleBar createTitleBar() {
        return new CustomTitleBar(getTitlebarIcon(), this);
    }

    /**
     * Returns custom titlebar icon.
     *
     * @return  the custom titlebar icon.
     */
    public Icon getTitlebarIcon() {
        return ImageUtil.getImageIcon(FBChatImages.HEADER_LOGO);
    }

    public void setTitle(String newVal) {
        titleBar.setTitle(newVal);
        super.setTitle(newVal);
    }

    /**
     * Constructs main content pane.
     *
     * @return  the main content pane object.
     */
    public abstract JComponent createContentPane();

    /**
     * Used to resize frame.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    public class CustomFrameResizer extends MouseAdapter {
        private int x1;
        private int x2;
        private int y1;
        private int y2;

        private int positionx;
        private int positiony;

        public void mouseExited(MouseEvent evt) {
            setCursor(Cursor.getDefaultCursor());
        }

        public void mouseMoved(MouseEvent evt) {
            Point p = evt.getPoint();
            if ((getWidth() - p.x < 10) && (getHeight() - p.y < 10)) {
                setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            } else if (getWidth() - p.x < 10) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else if (getHeight() - p.y < 10) {
                setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }

        public void mousePressed(MouseEvent evt) {
            onPress(evt);
        }

        public void mouseDragged(MouseEvent evt) {
            resizeWindowWidth(evt);
            resizeWindowHeight(evt);
        }

        public void resizeWindowWidth(java.awt.event.MouseEvent evt) {

            this.positionx = evt.getXOnScreen();

            if(this.positionx > this.x1) {
                this.x2 = this.positionx - this.x1;
                setSize(getSize().width + this.x2, getSize().height);
            } else if(this.positionx < this.x1) {
                int newWidth = getSize().width - (this.x1- this.positionx);
                if (newWidth>= MIN_FRAME_WIDTH) {
                    this.x2 =  this.x1- this.positionx;
                    setSize(getSize().width - this.x2, getSize().height);
                }
            }

            this.x1 = this.positionx;
        }


        public void resizeWindowHeight(java.awt.event.MouseEvent evt) {

            this.positiony = evt.getYOnScreen();

            if (this.positiony > this.y1){
                this.y2 = this.positiony - this.y1;
                setSize(getSize().width, getSize().height + this.y2);
            } else if(this.positiony < this.y1){

                int newHeight = getSize().height - (this.y1- this.positiony);
                if (newHeight >= MIN_FRAME_HEIGHT) {
                    this.y2 =  this.y1 - this.positiony;
                    setSize(getSize().width, getSize().height - this.y2);
                }
            }

            this.y1 = this.positiony;
        }

        public void onPress(java.awt.event.MouseEvent evt){
            this.x1 = evt.getXOnScreen();
            this.y1 = evt.getYOnScreen();
        }
    }
}