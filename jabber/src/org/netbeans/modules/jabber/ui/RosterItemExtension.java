/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Jabber module.
 * The Initial Developer of the Original Code is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.RemoteRosterEntry;
import org.jivesoftware.smackx.packet.RosterExchange;
import org.netbeans.modules.jabber.Contact;
import org.netbeans.modules.jabber.Manager;
import org.openide.awt.HtmlBrowser;

/**
 *
 * Provides a displayer for extensions in the format:
 * <pre>
 *   &lt;x xmlns="jabber:x:roster">
 *     &lt;item jid="nenik@jabber.sh.cvut.cz" name="Nenik"/>
 *   &lt;/x>
 * </pre>
 *
 * Also covers messages from unknown contacts
 *
 * @author  nenik
 */
public final class RosterItemExtension implements ExtensionDisplayer {
    
    /** Creates a new instance of UrlExtension */
    public RosterItemExtension() {
    }
    
    public String extensionNamespace() {
        // need to process messages manually to cover message from unknown jid
        return null; 
    }
    
    public boolean accept(Manager man, Message msg) {
        String from = msg.getFrom();
        // sender unknown, but not the server itself
        if (StringUtils.parseName(from).length() > 0 && man.getContactList().getContact(from) == null) return true;
        
        if (msg.getExtension("x", "jabber:x:roster") != null) return true;
        return false;
    }

    public Object preferredPosition() {
        return java.awt.BorderLayout.EAST;
    }
    
    public JComponent createPanel(Manager man, Message msg) {
        return new Display(man, msg);
    }
    
    
    private class Display extends JPanel implements ActionListener {
        private Manager man;
        private Message msg;
        private JList contacts;
        
        
        public Display(Manager man, Message msg) {
            this.man = man;
            this.msg = msg;
            
            Vector items = new Vector();
            
            PacketExtension ext = msg.getExtension("x", "jabber:x:roster");
            if (ext instanceof RosterExchange) {
                RosterExchange def = (RosterExchange)ext;
                for (Iterator it = def.getRosterEntries(); it.hasNext(); ) {
                    RemoteRosterEntry re = (RemoteRosterEntry)it.next();
                    items.add(new Item(re.getUser(), re.getName()));
                }
            }

            if (man.getContactList().getContact(msg.getFrom()) == null) {
                items.add(new Item(msg.getFrom(), ""));
            }

            if (items.size() > 0) { // fill in the UI
                setBorder(new CompoundBorder(
                    new EmptyBorder(0, 12, 0, 0),
                    new TitledBorder(new BevelBorder(BevelBorder.LOWERED), "New contacts:")
                ));
                setLayout(new BorderLayout());

                contacts = new JList(items);                
                contacts.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                contacts.setSelectionInterval(0, items.size()-1);
                add(contacts, BorderLayout.CENTER);
                
                JButton button = new JButton("Add contacts");
                button.addActionListener(this);
                add(button, BorderLayout.SOUTH);
            }
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            Object[] toAdd = contacts.getSelectedValues();
            Contact.List cList = man.getContactList();
            RosterPacket packet = new RosterPacket();
            packet.setType(org.jivesoftware.smack.packet.IQ.Type.SET);

            StringBuffer listBuffer = new StringBuffer("Adding ");
            for (int i = 0; i<toAdd.length; i++ ) {
                Item act = (Item)toAdd[i];
                String jid = StringUtils.parseBareAddress(act.jid);
                String name = act.name != null && act.name.length() > 0 ?
                    act.name : StringUtils.parseName(act.jid);
                packet.addRosterItem(new RosterPacket.Item(jid, name));

                listBuffer.append(name).append(" (").append(jid).append(")");
                if (i < toAdd.length - 1) listBuffer.append(", ");
            }
            
            WaitForResultUI.performQuery(man, packet, "Adding ...", listBuffer.toString());
        }
        
        
    }
    
    private static class Item {
        String jid;
        String name;

	Item(String jid, String name) {
            this.jid = jid;
            this.name = name;
        }
        
        public String toString() {
            return name != null && name.length() > 0 ?
                name + " (" + jid + ")" : jid;
        }
    }
    
}
