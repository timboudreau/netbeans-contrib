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

import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.jabber.Contact;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;

/**
 * A base for all actions operating over Contact or a set of contacts.
 * Specific actions must implement performAction method. They can also override
 * isEnabled method, which defaults to true.
 *
 * @author  nenik
 */
public abstract class ContactAction extends AbstractAction implements ContextAwareAction {
    
    /** Creates a new instance of ContactAction */
    public ContactAction(String name) {
        super(name);
    }
    
    public final void actionPerformed(java.awt.event.ActionEvent e) {
        assert false : "Should never be called";
    }    
    
    public final Action createContextAwareInstance(Lookup context) {
        return new Delegate(this, context);
    }
    
    
    protected abstract void performAction(Contact[] contacts);
    
    protected boolean isEnabled(Contact[] contacts) {
        return true;
    }
    
    private static class Delegate extends AbstractAction {
        private static Contact[] EMPTY = new Contact[0];
        
        ContactAction del;
        Lookup context;
        
        Delegate(ContactAction del, Lookup context ) {
            this.del = del;
            this.context = context;
            setEnabled(del.isEnabled(getContacts(context)));
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            del.performAction(getContacts(context));
        }
        
        public Object getValue(String key) {
            return del.getValue(key);
        }
        
        private Contact[] getContacts(Lookup ctx) {
            Collection col = ctx.lookup(new Lookup.Template(Contact.class)).allInstances();
            Contact[] contacts = (Contact[])col.toArray(EMPTY);
            return contacts;
        }
    }
    
}
