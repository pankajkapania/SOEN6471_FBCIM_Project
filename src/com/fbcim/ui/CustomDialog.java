package com.fbcim.ui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.fbcim.FBChatContext;
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
public abstract class CustomDialog  extends JDialog
    implements ITitleBarEventListener {

    /** The title bar component. */
    protected CustomTitleBar titleBar;

    /** The actual container component. */
    protected JPanel container;

    /** The app. context to work with. */
    protected FBChatContext context;


    public CustomDialog(Frame owner, boolean modal) {
        super(owner, modal);
        setUndecorated(true);
        setIconImage(
                ImageUtil.getImageIcon(FBChatImages.TRAY_ICON).getImage()
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

        super.setTitle(Msg.getString(FBChatStrings.APP_TITLE));
    }

    public CustomDialog() {
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
     * Constructs main content pane.
     *
     * @return  the main content pane object.
     */
    public abstract JComponent createContentPane();

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

    protected Border createContainerBorder() {
        return BorderFactory.createLineBorder(FBChatColors.FRONT_PAGE_BORDER);
    }

    protected Border createTitleBarBorder() {
        return BorderFactory.createEmptyBorder();
    }

}
