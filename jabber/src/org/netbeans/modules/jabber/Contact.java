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

package org.netbeans.modules.jabber;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import org.jivesoftware.smack.PacketListener;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;

/**
 * Models a single contact in an user's roster.
 * Notifies attached Observers about changes in its properties.
 * The instances are managed by the associated Contact.List.
 *
 * @author  nenik
 */
public class Contact {
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    private Manager manager;
    
    private String jid;
    private String nick;
    
    /* 0 - none - don't see each other
     * 1 - from - he sees me
     * 2 - to - I see him
     * 3 - both - both see each other
     */
    private int subscription;
    
    /* 0 - unknown
     * 1 - offline
     * 2 - xa
     * 3 - away
     * 4 - dnd
     * 5 - online
     * 6 - chatty
     */
    private int state;
    
    // like "gone fishing"
    private String statusMessage;
    
    /** Creates a new instance of Contact */
    Contact(Manager man, String jid, String nick, RosterPacket.ItemType sub) {
        manager = man;
        this.jid = jid;
        this.nick = nick;
        this.statusMessage = "";
        
        updateSubscription(sub);
        
        this.state = (subscription < 2) ? 0 : 1;
    }
    
    public String getJid() {
        return jid;
    }
    
    public String getName() {
        return nick;
    }
    
    public int getSubscription() {
        return subscription;
    }
    
    public int getStatus() {
        return state;
    }
    
    public String getStatusMessage() {
        return statusMessage;
    }
    
    public void sendMessage(Message message) {
        message.setTo(jid);
        manager.getConnection().sendPacket(message);
    }
    
    public Manager getManager() {
        return manager;
    }
    
    public boolean isPendingMessage() {
        return manager.getMessageQueue().getMessageCountFrom(jid) > 0;
    }
    
    public Message nextMessage() {
        return manager.getMessageQueue().nextMessageFrom(jid);
    }
    
    public void requestPresence() {
        sendPacket(new Presence(Presence.Type.SUBSCRIBE));
    }
    
    public void grantPresence() {
        sendPacket(new Presence(Presence.Type.SUBSCRIBED));
    }
    
    public void revokePresence() {
        sendPacket(new Presence(Presence.Type.UNSUBSCRIBED));
    }

    private void sendPacket(Packet p) {
        p.setTo(jid);
        manager.getConnection().sendPacket(p);
    }
    
    private String getCurrentResource() {
        Presence p = manager.getContactList().roster.getPresence(jid);
        return p == null ? null : p.getFrom();
    }
    
    /*XXX*/ public void sendPacketToResource(Packet packet) {
        String res = getCurrentResource();
        if (res != null) {
            packet.setTo(res);
            manager.getConnection().sendPacket(packet);
        }
    }
    
    void setStatus(int newStatus) {
        if (newStatus == 1 && subscription < 2) newStatus = 0;
        state = newStatus;
        firePropertyChange("status"); // XXX filter
    }
    
    void setStatusMessage(String message) {
        if (message == null) message = "";
        
        if (message.equals(statusMessage)) return;
        
        statusMessage = message;
        firePropertyChange("statusMessage");
    }
    
    private static java.util.List subs = Arrays.asList(new String[] {
            "none", "to", "from", "both"});

    private static java.util.List subscriptions = Arrays.asList(
        new RosterPacket.ItemType[]{
            RosterPacket.ItemType.NONE,
            RosterPacket.ItemType.FROM,
            RosterPacket.ItemType.TO,
            RosterPacket.ItemType.BOTH
    });

    void updateSubscription(RosterPacket.ItemType newSubscription) {
        int intNew = subscriptions.indexOf(newSubscription);
        if (intNew >= 0 && intNew != subscription) {
            subscription = intNew;
            firePropertyChange("subscription");
            
            if (subscription < 2 && state == 1) {
                state = 0;
                firePropertyChange("status");
            }
        }
    }
    
    void updateName(String newName) {
        if (newName != nick && (nick == null || !nick.equals(newName))) {
            nick = newName;
            firePropertyChange("name");
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    }
    
    private void firePropertyChange(String propName) {
        support.firePropertyChange(propName, null, null);
    }
    
    
    
    public static class List {
        Manager manager;
        Roster roster;
        private Map/*<String,Contact>*/ users = new HashMap();
        
        private Vector listeners = new Vector();
        private static Listener[] EMPTY = new Listener[0];
        
        List(Manager man) {
            manager = man;
            updateRoster(); // probably won't do anything, roster is still null
        }

        void updateRoster() {
            synchronized (users) {
                roster = manager.getConnection().getRoster();
                if (roster == null) { // we're offline, change presences
                    for (Iterator it = users.values().iterator(); it.hasNext();) {
                        Contact act = (Contact)it.next();
                        act.setStatus(1); // don't know when we're offline
                        act.setStatusMessage("");
                    }
                } else {
                    roster.addRosterListener(new RL(this));
                    rosterModified();
               }
            }
        }
        
        public Collection getContacts() {
            return new ArrayList(users.values());
        }
        
        public Contact getContact(String jid) {
            return (Contact)users.get(StringUtils.parseBareAddress(jid));
        }

        // format: "Nick" | "address@server"
        public String getShortDisplayName(String jid) {
            jid = StringUtils.parseBareAddress(jid);
            Contact con = getContact(jid);
            String name = con == null ? null : con.getName();
            
            return name == null ? jid : name;
        }

        // format: "Nick (address@server)" | "address@server"
        public String getLongDisplayName(String jid) {
            jid = StringUtils.parseBareAddress(jid);
            Contact con = getContact(jid);
            String name = con == null ? null : con.getName();
            
            return (name == null || name.equals(jid)) ? jid :
                name + " (" + jid + ")"; 
        }
            
        public Result removeContacts(Contact[] contacts, Result.Callback callback) {
            RosterPacket packet = new RosterPacket();
            packet.setType(org.jivesoftware.smack.packet.IQ.Type.SET);
            for (int i = 0; i<contacts.length; i++) {
                RosterPacket.Item item = new RosterPacket.Item(contacts[i].getJid(), null);
                item.setItemType(RosterPacket.ItemType.REMOVE);
                packet.addRosterItem(item);
            }
            
            return manager.sendPacketWithResult(packet, callback);
        }
        
        public Result addContact(String jid, String name, Result.Callback callback) {
            RosterPacket packet = new RosterPacket();
            packet.setType(org.jivesoftware.smack.packet.IQ.Type.SET);
            packet.addRosterItem(new RosterPacket.Item(jid, name));
            
            return manager.sendPacketWithResult(packet, callback);
        }
        
        public void askForPresence(String jid) {
            Presence pres = new Presence(Presence.Type.SUBSCRIBE);
            pres.setTo(jid);
            manager.getConnection().sendPacket(pres);
        }

        public void addListener(Listener l) {
            listeners.add(l);
        }
        
        public void removeListener(Listener l) {
            listeners.remove(l);
        }
        
        // ineffective but OK for few items
        private static java.util.List modes = Arrays.asList(new Presence.Mode[] {
            Presence.Mode.INVISIBLE,
            null, 
            Presence.Mode.EXTENDED_AWAY,
            Presence.Mode.AWAY,
            Presence.Mode.DO_NOT_DISTURB,
            Presence.Mode.AVAILABLE,
            Presence.Mode.CHAT
        });
        
        // watching methods
        void presenceChanged(String jid) {
            synchronized (users) {
                        Contact c = (Contact)users.get(jid);
                        if (c == null) return; // strange:debug

                        Presence p = roster.getPresence(jid);
                        if (p == null) {
                            c.setStatus(1); // unavailable?
                            c.setStatusMessage("");
                        } else {
                            Presence.Mode pm = p.getMode(); // XXX - possible NPE
                            int state = modes.indexOf(pm);
                            c.setStatus(state);
                            c.setStatusMessage(p.getStatus());
                        }
            }
        }
        
        private void addEntryFromRoster(RosterEntry re) {
            String jid = re.getUser();
            Contact c = new Contact(manager, jid, re.getName(), re.getType());
            users.put(jid, c);
            
            presenceChanged(jid);

            Listener[] lst = (Listener[]) listeners.toArray(EMPTY);
            for (int i=0; i<lst.length; i++) lst[i].contactAdded(jid);
        }
        
        private void removeEntry(String jid) {
            users.remove(jid);
            Listener[] lst = (Listener[]) listeners.toArray(EMPTY);
            for (int i=0; i<lst.length; i++) lst[i].contactRemoved(jid);
        }

        void rosterModified() {
            synchronized (users) {
                Set removed = new HashSet(users.keySet());

                // iterate all current entries, adding new and updating known
                for (Iterator it = roster.getEntries(); it.hasNext(); ) {
                    RosterEntry re = (RosterEntry)it.next();
                    String jid = re.getUser();
                    removed.remove(jid);

                    Contact c = (Contact)users.get(jid);
                    if (c == null) {
                        addEntryFromRoster(re);
                    } else {
                        c.updateName(re.getName());
                        c.updateSubscription(re.getType());
                        presenceChanged(re.getUser());
                    }
                }

                // remove all lost entries
                for (Iterator it = removed.iterator(); it.hasNext();) {
                    removeEntry((String)it.next());
                }
            }
        }
        
        
        public interface Listener {
            public void contactAdded(String jid);
            public void contactRemoved(String jid);
        }
        
        
        
    }

    /*
     * A RosterListener that just forwards events to the Contact.List
     * implementation.
     */
    private static class RL implements RosterListener {
        Contact.List cList;
        RL (Contact.List cList) {
            this.cList = cList;
        }
        
        public void presenceChanged(String addr) {
            cList.presenceChanged(addr);
        }
        
        public void rosterModified() {
            cList.rosterModified();
        }
        
    }


    static String[] parseJid(String jid) throws IllegalArgumentException {
        int atOff = jid.indexOf('@');
        int slashOff = jid.indexOf('/');

        if (atOff < 1) throw new IllegalArgumentException(jid);
        // if (slashOff != -1 && at)
            
        String user = jid.substring(0, atOff);
        String server;
        String res = null;
        
        if (slashOff != -1) {
            if (slashOff < atOff + 2 ) throw new IllegalArgumentException(jid);
            
            server = jid.substring(atOff+1, slashOff);
            res = jid.substring(slashOff+1, jid.length());
        } else {
            server = jid.substring(atOff+1, jid.length());
        }
        if (server.length() < 1) throw new IllegalArgumentException(jid);
        
        // if (res == null) res = "";
        return new String[] { user, server, res };
    }

}
