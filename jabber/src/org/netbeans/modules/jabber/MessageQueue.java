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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

/**
 * MessageQueue is a concentrator of incomming messages.
 * The messages can be picked up in the order they came of they can be
 * selected on the per-contact basis.
 *
 * This class is multithreaded, expected usage is filling from daemon
 * communication thread and queries/spilling from UI thread.
 *
 * @author  nenik
 * XXX - firing threading 
 */
public final class MessageQueue {
    
    private Object lock = new Object();

    private Map/*<String,List>*/ messages = new HashMap();
    private List/*<message>*/ order = new LinkedList();

    private Vector listeners = new Vector();
    private static Listener[] EMPTY = new Listener[0];

    public int getMessageCount() {
        synchronized (lock) {
            return order.size();
        }
    }
    
    public Message nextMessage() {
        Message m;
        
        synchronized (lock) {
            if (order.size() == 0) return null;
            m = (Message)order.remove(0);
            String jid = StringUtils.parseBareAddress(m.getFrom());
            List priv = (List)messages.get(jid);
            
            // there have to be a mapping for given JID
            assert priv != null;
            
            // there have to be a message from JID
            assert priv.size() > 0;
            
            Message m2 = (Message)priv.remove(0);
            
            // it have to be the oldest message from JID
            assert m == m2;
        }

        fireChange(m.getFrom(), false);
        return m;
    }
    
    
    public int getMessageCountFrom(String jid) {
        synchronized (lock) {
            List priv = (List)messages.get(jid);
            return priv == null ? 0 : priv.size();
        }
    }
    
    public Message nextMessageFrom(String jid) {
        Message m;
        synchronized (lock) {
            List priv = (List)messages.get(jid);

            if (priv == null || priv.size() == 0) return null;
            
            m = (Message)priv.remove(0);
            
            // TODO: theoretic performance problem
            boolean contained = order.remove(m);

            // it have to be in the global list
            assert contained;
        }
        
        fireChange(m.getFrom(), false);
        
        return m;
    }
    
    void put(Message m) {
        String jid = StringUtils.parseBareAddress(m.getFrom());
        synchronized (lock) {
            List priv = (List)messages.get(jid);
            if (priv == null) messages.put(jid, priv = new LinkedList());
            priv.add(m);
            order.add(m);
        }
        
        fireChange(jid, true);
    }
        
    public static interface Listener {
        public void messageReceived(String fromJID);
        public void messageRemoved(String fromJID);
    }
    
    
    public void addListener(Listener l) {
        listeners.add(l);
    }
    
    public void removeListener(Listener l) {
        listeners.remove(l);
    }
    
    private void fireChange(String jid, boolean added) {
        Listener[] all = (Listener[]) listeners.toArray(EMPTY);
        jid = StringUtils.parseBareAddress(jid);
        for (int i=0; i<all.length; i++) {
            if (added) {
                all[i].messageReceived(jid);
            } else {
                all[i].messageRemoved(jid);
            }
        }
    }
    
}
