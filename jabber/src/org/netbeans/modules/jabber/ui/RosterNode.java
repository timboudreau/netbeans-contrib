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

import org.netbeans.modules.jabber.Contact;
import org.netbeans.modules.jabber.Manager;
import org.netbeans.modules.jabber.MessageQueue;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  nenik 
 */
public class RosterNode extends AbstractNode {
    
    /** Creates a new instance of RosterNode */
    public RosterNode(Manager man) {
        super(new RosterChildren(man));
        setName("roster");
        setDisplayName("Contact List");
    }
    

    private static class RosterChildren extends Children.Keys 
                implements MessageQueue.Listener, Contact.List.Listener {
        private Contact.List clist;
        private Manager manager;
        
        RosterChildren(Manager man) {
            this.manager = man;
            this.clist = man.getContactList();
            clist.addListener(this);
            man.getMessageQueue().addListener(this);
        }
        
        protected Node[] createNodes(Object key) {
            return new Node[] { new ContactNode((Contact)key, manager) };
        }
        
        protected void addNotify() {
            updateKeys();
        }
        
        public void messageReceived(String fromJID) {
            ContactNode target = (ContactNode)findChild(fromJID);
            if (target != null) target.updateMessage();
        }
        
        public void messageRemoved(String fromJID) {
            ContactNode target = (ContactNode)findChild(fromJID);
            if (target != null) target.updateMessage();
        }
        
        public void contactAdded(String jid) {
            updateKeys();
        }
        
        public void contactRemoved(String jid) {
            updateKeys();
        }
        
        private void updateKeys() {
            setKeys(clist.getContacts());            
        }
        
    }
    
}
