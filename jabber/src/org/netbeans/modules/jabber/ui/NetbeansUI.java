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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.jivesoftware.smack.XMPPConnection;
import org.netbeans.modules.jabber.Manager;
import org.netbeans.modules.jabber.Settings;
import org.openide.windows.TopComponent;

/**
 *
 * @author  nenik
 */
public class NetbeansUI extends TopComponent {

    private static NetbeansUI INSTANCE;    

    /** Creates a new instance of NetbeansUI */
    NetbeansUI() {
        setLayout(new BorderLayout());
        
        Settings set = NbSettings.getDefault();

        
        Manager man = new Manager(set);

        JabberUI ui = new JabberUI(man);
        
        add(ui, BorderLayout.CENTER);
    }
    
    public static synchronized Object getTopComponent() {
        if (INSTANCE == null) {
            INSTANCE = new NetbeansUI();
        }
        return INSTANCE;
    }
}
