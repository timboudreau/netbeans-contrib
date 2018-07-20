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
