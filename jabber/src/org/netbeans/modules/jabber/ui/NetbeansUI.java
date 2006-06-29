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

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jivesoftware.smack.XMPPConnection;
import org.netbeans.modules.jabber.Manager;
import org.netbeans.modules.jabber.MessageQueue;
import org.netbeans.modules.jabber.Settings;
import org.openide.windows.TopComponent;

/**
 *
 * @author  nenik
 */
public class NetbeansUI extends TopComponent {

    private static NetbeansUI INSTANCE;    
    
    private IconUpdater iconUpdater = new IconUpdater();
    Manager man;

    /** Creates a new instance of NetbeansUI */
    NetbeansUI() {
        setDisplayName("Jabber");
        setLayout(new BorderLayout());
        
        Settings set = NbSettings.getDefault();

        
        man = new Manager(set);
        
        man.addListener(iconUpdater);
        man.getMessageQueue().addListener(iconUpdater);

        JabberUI ui = new JabberUI(man);
        
        add(ui, BorderLayout.CENTER);
	
        INSTANCE = this;
    }
    
    public static synchronized Object getTopComponent() {
        if (INSTANCE == null) {
            INSTANCE = new NetbeansUI();
        }
        return INSTANCE;
    }
    
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }
    
    protected String preferredID() {
        return "Jabber";
    }
    
    private ImageIcon[] SMALL_ICONS = new ImageIcon[] {
        createIcon("error24.png"),  // XXX: will be error
        createIcon("offline.png"),
        createIcon("anim.gif"),
        createIcon("xa.png"),
        createIcon("away.png"),
        createIcon("dnd.png"),
        createIcon("online.png"),
        createIcon("chat.png"),
        createIcon("message.png"),
        createIcon("mmessage.png")
    };

    // XXX - should manage icons more centrally, this way there are more instances
    private ImageIcon createIcon(String name) {
            return new ImageIcon(getClass().getResource("/org/netbeans/modules/jabber/resources/" + name));
    }

    
    /*
     * The TopComponent icon should serve several purposes.
     * When idle, it shows current connection status/user status.
     * When there is a single message waiting, it shows the envelope icon,
     * For more messages it shows the double envelope icon.
     */
    private void updateIcon() {
        int offset = 0;
        int msgCount = man.getMessageQueue().getMessageCount();
        
        if (msgCount == 1) {
            offset = 8;
        } else if (msgCount > 1) {
            offset = 9;
        } else switch (man.getConnectionStatus()) {
            case Manager.CONNECTION_ERROR:
                offset = 0;
                break;
            case Manager.CONNECTION_DISCONNECTED:
                offset = 1;
                break;
            case Manager.CONNECTION_CONNECTING:
            case Manager.CONNECTION_LOGGING:
            case Manager.CONNECTION_DATAPHASE:
                offset = 2;
                break;
            case Manager.CONNECTION_READY:
                switch (man.getStatus()) {
                    case Manager.STATUS_XA:
                        offset = 3;
                        break;
                    case Manager.STATUS_AWAY:
                        offset = 4;
                        break;
                    case Manager.STATUS_DND:
                        offset = 5;
                        break;
                    case Manager.STATUS_ONLINE:
                        offset = 6;
                        break;
                    case Manager.STATUS_CHATTY:
                        offset = 7;
                        break;
                }
                break;
        }
        setIcon(SMALL_ICONS[offset].getImage());
    }

    private class IconUpdater implements Manager.Listener, MessageQueue.Listener, Runnable {        
        public void stateChanged() {
            SwingUtilities.invokeLater(this);
        }
        
        public void messageReceived(String fromJID) {
            SwingUtilities.invokeLater(this);
            NetbeansUI.this.requestAttention(true);
        }
        
        public void messageRemoved(String fromJID) {
            SwingUtilities.invokeLater(this);
        }                
        
        public void run() {
            updateIcon();
        }
        
    }

}
