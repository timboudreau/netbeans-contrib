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

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.jabber.Contact;
import org.netbeans.modules.jabber.Manager;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  nenik
 */
public final class ContactNode extends AbstractNode implements PropertyChangeListener {
    
    private static Image[] icons = new Image[] {
        getImage("unknown"),
        getImage("offline"),
        getImage("xa"),
        getImage("away"),
        getImage("dnd"),
        getImage("online"),
        getImage("chat")
    };

    private static Image messageIcon = getImage("message");
    
    static Image getImage(String base) {
        return Utilities.loadImage("org/netbeans/modules/jabber/resources/" + base + ".png");
    }
    
    private Contact model;
    private Manager manager;
    private boolean message;
    
    
    /** Creates a new instance of ContactNode */
    public ContactNode(Contact c, Manager man) {
        super(Children.LEAF, Lookups.singleton(c));
        model = c;
        manager = man;
        model.addPropertyChangeListener(this); // XXX - maybe weak or deregister
        updateProps();
        updateMessage();
    }
    
    
    
    public Action[] getActions(boolean ctx) {
        List lst = new ArrayList(11);
        
        if (message) lst.add(Actions.getReadMessageAction());
        lst.add(Actions.getSendMessageAction());
        lst.add(Actions.getRemoveContactAction());
        lst.add(null);

        /* 0 - none - don't see each other
         * 1 - from - he sees me
         * 2 - to - I see him
         * 3 - both - both see each other
         */

        int sub = model.getSubscription();
        if (sub < 2) lst.add(Actions.getPresenceRequestAction());
        lst.add(((sub & 1) == 0) ? Actions.getPresenceGrantAction() : Actions.getPresenceRevokeAction());
        lst.add(null);
        
        lst.add(Actions.getRemoteShowAction());
        lst.add(Actions.getRemoteHelpAction());
        
        lst.add(PropertiesAction.get(PropertiesAction.class));
        
        return (Action[])lst.toArray(new Action[lst.size()]);
    }
    
    public Action getPreferredAction() {
        return message ? Actions.getReadMessageAction() : Actions.getSendMessageAction();
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        // XXX - real redistribution
        fireIconChange();
        updateProps();
    }    
    
    private static String[] labels = new String[] {
        "Unknown", "Offline", "Extended Away", "Away", "Do not disturb", "Online", "Chatty"
    };
    
    private void updateProps() {
        setName(model.getJid());
        setDisplayName(model.getName());
        String statusMessage = model.getStatusMessage();
        StringBuffer tip = new StringBuffer("<html>").append(model.getJid()).
                append(", <b>").append(labels[model.getStatus()]).append("</b>");
        if (statusMessage.length() > 0) {
            for (StringTokenizer tok = new StringTokenizer(statusMessage,"\n"); tok.hasMoreTokens(); ) {
                tip.append("<br>").append(tok.nextToken());
            }
        }
        setShortDescription(tip.append("</html>").toString());
    }
    
    
    public Image getIcon (int type) {
        if (message) {
            return messageIcon;
        } else {
            return icons[model.getStatus()];
        }
    }

    protected Sheet createSheet () {
        Sheet sheet = new Sheet();
        Sheet.Set props = Sheet.createPropertiesSet();
        
        props.put(new Node.Property[] {
            new PropertySupport.ReadOnly("jid", String.class, "JID", "Jabber ID of the contact") {
                public Object getValue() {return model.getJid();}
            }
            
        });

        sheet.put(props);
        return sheet;
    }

    void updateMessage() {
        boolean newMessage = manager.getMessageQueue().getMessageCountFrom(model.getJid()) > 0;
        if (newMessage != message) {
            message = newMessage;
            fireIconChange();
        }
    }
    
    
}
