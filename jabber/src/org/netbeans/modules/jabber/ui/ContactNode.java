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
