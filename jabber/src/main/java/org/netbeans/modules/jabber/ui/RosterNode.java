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
