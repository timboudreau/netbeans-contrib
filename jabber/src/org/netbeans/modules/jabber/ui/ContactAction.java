/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
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
