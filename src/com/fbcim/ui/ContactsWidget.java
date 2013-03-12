package com.fbcim.ui;

import com.fbcim.FBChatContext;
import com.fbcim.chat.FBChat;
import com.fbcim.chat.FBContact;
import com.fbcim.chat.FBContactList;
import com.fbcim.chat.FBContactListListener;
import com.fbcim.util.FileUtil;
import com.fbcim.util.ImageUtil;
import org.jivesoftware.smack.packet.Presence;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
public class ContactsWidget extends JPanel {
    /** The app. context to work with. */
    private FBChatContext context;

    /** The actual UI component responsible for contacts list rendering. */
    private JTable widgetRenderer;

    /** The data model that contains list of  */
    private ContactsWidgetModel dataModel;

    private int chatOpenActionCount = 0;

    /**
     * Constructs contacts list widget instance.
     *
     * @param context
     *          the app. context to work with.
     */
    public ContactsWidget(final FBChatContext context) {
        this.context = context;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        setBackground(FBChatColors.CONTACTS_LIST_BG);
        this.dataModel = new ContactsWidgetModel(context.getContactList());
        this.widgetRenderer = new JTable(dataModel);
        this.widgetRenderer.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.widgetRenderer.setTableHeader(null);
        this.widgetRenderer.setShowGrid(false);
        this.widgetRenderer.setRowHeight(36);
        this.widgetRenderer.setIntercellSpacing(new Dimension(0, 0));
        this.widgetRenderer.getColumnModel().getColumn(0).setCellRenderer(new ContactsWidgetRenderer());
        this.widgetRenderer.setBackground(FBChatColors.CONTACTS_LIST_BG);

        this.widgetRenderer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 1) {
                    FBChat chat = ContactsWidget.this.context.chat((FBContact)dataModel.getValueAt(widgetRenderer.getSelectedRow(), 0));
                    chat.getChatFrame().setVisible(true);
                    chatOpenActionCount++;
                    if ((chatOpenActionCount == 5) && !context.getSettings().getRecommendDialogShown()) {
                        context.getSettings().recommendDialogShown();
                        int exitCode = new MessageDialog(null, "", Msg.getString(FBChatStrings.RECOMMEND_TEXT), "Yes", "No").showMessageDialog();
                        if  (exitCode == 0) {
                            FileUtil.launchUrl("http://www.facebook.com/sharer/sharer.php?u=http%253A%252F%252Fwww.fbcim.com&t=Facebook+Chat+Instant+Messenger");
                        }
                    }
                }
            }

            public void mouseExited(MouseEvent e) {
                widgetRenderer.clearSelection();
            }
        });

        this.widgetRenderer.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = widgetRenderer.rowAtPoint(e.getPoint());
                if (row > -1) {
                    widgetRenderer.setRowSelectionInterval(row, row);
                }
            }
        });


        JScrollPane sp = new JScrollPane(this.widgetRenderer);
        FBScrollBarUI.decorateScrollBar(
                sp.getVerticalScrollBar(),
                true, true,
                FBChatColors.SCROLLBAR_BORDER_LEFT, new Dimension(21,150));
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.getViewport().setBackground(FBChatColors.CONTACTS_LIST_BG);
        add(sp, BorderLayout.CENTER);
    }


    /**
     * Renders a single contact in a list.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class ContactsWidgetRenderer extends JPanel
            implements TableCellRenderer {
        /** Contact is offline. */
        private Icon offlineIcon;

        /** Contact is online.  */
        private Icon availableIcon;

        /** Contact is online.  */
        private Icon awayIcon;

        /** Used to display user avatar. */
        private JLabel avatarLabel;

        /** Displays contact name. */
        private JLabel contactName;

        /** Displays contact status text */
        private JLabel contactStatusText;

        /** Displays contact status icon. */
        private JLabel contactStatusLabel;

        /**
         * Constructs renderer instance.
         */
        private ContactsWidgetRenderer() {
            super(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            setBackground(FBChatColors.CONTACTS_LIST_BG);

            offlineIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_OFFLINE);
            availableIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_AVAILABLE);
            awayIcon = ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_AWAY);

            avatarLabel = new JLabel(ImageUtil.getImageIcon(FBChatImages.CONTACT_AVATAR_MALE));

            contactName = new JLabel();
            contactName.setForeground(FBChatColors.CONTACTS_NAME);
            contactName.setFont(new Font("Lucinda Grande", Font.BOLD, 11));

            contactStatusText = new JLabel("at the movies");
            contactStatusText.setForeground(FBChatColors.CONTACTS_STATUS);
            contactStatusText.setFont(new Font("Lucinda Grande", Font.PLAIN, 11));

            contactStatusLabel = new JLabel(ImageUtil.getImageIcon(FBChatImages.CONTACT_STATUS_AVAILABLE));
            contactStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.weightx = 0;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 2;
            add(avatarLabel, gbc);

            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.insets.left = 10;
            add(contactName, gbc);

            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.insets.left = 10;
            gbc.insets.top = 5;
            add(contactStatusLabel, gbc);

            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.insets.left = 10;
            gbc.insets.top = 0;
            add(contactStatusText, gbc);
        }

        /**
         * Sets contact avatar.
         *
         * @param newVal
         *          the avatar to display.
         */
        private void setContactAvatar(Icon newVal) {
            avatarLabel.setIcon(newVal);
        }

        /**
         * Sets contact name.
         *
         * @param newVal
         *          the contact name to display.
         */
        private void setContactName(String newVal) {
            contactName.setText(newVal);
        }

        /**
         * Sets contact status text.
         *
         * @param newVal
         *          the contact status text to set.
         */
        private void setContactStatusText(String newVal) {
            contactStatusText.setText(newVal);
        }

        /**
         * Ses custom status icon.
         */
        private void setContactPresence(Presence presence) {
            Icon statusIcon = offlineIcon;
            if (presence != null) {
                if (presence.isAvailable()) {
                    statusIcon = availableIcon;
                } else if (presence.isAway()) {
                    statusIcon = awayIcon;
                }
                String status = presence.getStatus();
                if (status != null) {
                    contactStatusText.setText(status);
                } else {
                    contactStatusText.setText("");
                }
            }

            contactStatusLabel.setIcon(statusIcon);

        }

        /**
         * Prepares renderer component to display given contact entry.
         *
         * @return
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof FBContact) {
                FBContact contact = (FBContact)value;
                setContactName(contact.getName());
                setContactStatusText(contact.getStatusText());
                setContactPresence(contact.getPresence());
                setContactAvatar(contact.getAvatarSmall());
            }
            if (isSelected) {
                setBackground(FBChatColors.CONTACTS_LIST_SEL_BG);
            } else {
                setBackground(FBChatColors.CONTACTS_LIST_BG);
            }
            return this;
        }
    }


    /**
     * Custom data model for contacts list widget.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class ContactsWidgetModel extends DefaultTableModel
        implements FBContactListListener {
        /** Keeps actual contacts. */
        private FBContactList list;

        /** The filtered and sorted data to display. */
        private List<FBContact> data;

        /** Used to filter visibility of contacts. */
        private ContactsFilter filter;

        /** Used to sort contacts in the list. */
        private ContactsComparator comparator;

        /**
         * Constructs data model instance.
         *
         * @param list
         *          the actual list of contacts.
         */
        ContactsWidgetModel(FBContactList list) {
            this.data = Collections.synchronizedList(new ArrayList<FBContact>());
            this.filter = new ContactsFilter();
            this.comparator = new ContactsComparator();

            list.addContactListListener(this);
        }

        /**
         * Sorts list of contacts according current preferences.
         */
        private void sortContacts() {
            synchronized (data) {
                Collections.sort(data, comparator);
            }
            fireTableRowsUpdated(0, data.size() - 1);
        }

        /**
         * Updates list of contacts according current sorting/display rules.
         */
        private void updateContacts() {

        }

        /**
         * Invoked when given contact is added to the list.
         */
        public void contactAdded(FBContact contact) {
            int rowIndex = -1;
            synchronized (data) {
                if  (!data.contains(contact) && filter.isContactVisible(contact)) {
                    rowIndex = data.size();
                    data.add(contact);
                }
            }

            // sort contacts.

            if (rowIndex > -1) {
                fireTableRowsInserted(rowIndex, rowIndex );
                sortContacts();
            }
        }

        /**
         * Invoked when avatar for a given contact is updated.
         */
        public void contactUpdated(FBContact contact) {
            int rowIndex = -1;
            synchronized (data) {
                for (int i = 0; i < data.size(); i++) {
                    FBContact aContact = data.get(i);
                    if (contact.equals(aContact)) {
                        rowIndex = i;
                        break;
                    }
                }
            }
            if (rowIndex > -1) {
                fireTableRowsUpdated(rowIndex, rowIndex);
                sortContacts();
            }
        }

        public int getColumnCount() {
            return 1;
        }

        /**
         * Returns rows count for this table.
         *
         * @return  the number of rows in the table.
         */
        public int getRowCount() {
            if (data == null) {
                return 0;
            }
            return data.size();
        }

        public Object getValueAt(int row, int column) {
            if (getRowCount() == 0) {
                return null;
            }
            return data.get(row);
        }

         /**
         * Tests if given cell is editable.
         *
         * @param row
         *          the cell's row.
         * @param column
         *          the cell's column.
         *
         * @return  <code>true<code> if cell is editable,
         *          <code>false</code> otherwise.
         */
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void setValueAt(Object aValue, int row, int column) {
            // we do nothing here :)
        }
    }

    /**
     * Used to filter visible records in the contacts list.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class ContactsFilter {

        /** <code>true</code> if offline contacts should be set visible. */
        private boolean offlineContactsVisible = true;

        /** The text fragment used to filter contacts by name. */
        private String mask = "";

        /**
         * Sets offline contacts display mode.
         *
         * @param newVal
         *          <code>true</code>
         */
        public void setOfflineContactsVisible(boolean newVal) {
            if (offlineContactsVisible != newVal) {
                offlineContactsVisible = newVal;
            }
        }

        /**
         * Sets filter mask.
         *
         * @param newVal
         *          the filter mask to set.
         */
        public void setFilterMask(String newVal) {
            this.mask = newVal == null ? "" : newVal.toLowerCase();
        }

        /**
         * Tests if given contact is visible.
         *
         * @param contact
         *          the contact to check visibility for.
         *
         * @return  <code>true</code> if contact is visible in the list,
         *          <code>false</code> otherwise.
         */
        public boolean isContactVisible(FBContact contact) {
            boolean isVisible = true;

            // First filter by name.
            if ((mask != null) && (mask.length() > 0)) {
                isVisible = contact.getName().toLowerCase().indexOf(mask) > -1;
            }

            // Then filter by visibility status.
            if (!offlineContactsVisible && isVisible) {
                isVisible = !contact.isOffline();
            }
            return isVisible;
        }
    }

    /**
     * Used to sort visible contacts in the list.
     *
     * @author Aleksey Prochukhan
     * @version 1.0
     */
    private class ContactsComparator implements Comparator<FBContact> {

        /** <code>true</code> if online contacts should be displayed fist.  */
        private boolean onlineContactsFirst = true;


        /**
         * Sets current sorting mode.
         *
         * @param newVal
         *          <code>true</code> if online contacts should be displayed before offline contacts,
         *          <code>false</code> if all contacts should be sorted alphabetically
         */
        public void setOnlineContactsFirst(boolean newVal) {
            this.onlineContactsFirst = newVal;
        }

        /**
         * Compares two contacts.
         *
         * @param o1
         *          the first contact to compare.
         * @param o2
         *          the second contact to compare.
         *
         * @return  a negative integer, zero, or a positive integer as the
         * 	       first argument is less than, equal to, or greater than the
         *	       second.
         */
        public int compare(FBContact o1, FBContact o2) {
            if (onlineContactsFirst) {
                if (o1.isOffline() == o2.isOffline()) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                } else if (o1.isOffline()) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        }
    }
}