/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
