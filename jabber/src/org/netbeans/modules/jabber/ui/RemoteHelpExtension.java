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
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.netbeans.modules.jabber.Manager;
import org.openide.awt.HtmlBrowser;

/**
 *
 * Provides a displayer for extensions in the format:
 * <pre>
 *   &lt;x xmlns="netbeans:x:remote:help">
 *     &lt;socket>127.0.0.1:40126&lt;/socket>
 *     &lt;key>4fa36c780bba243f&lt;/key>
 *   &lt;/x>
 * </pre>
 *
 * @author  nenik
 */
public final class RemoteHelpExtension implements ExtensionDisplayer {
    
    /** Creates a new instance of UrlExtension */
    public RemoteHelpExtension() {
    }
    
    public String extensionNamespace() {
        return "netbeans:x:remote:help";
    }
    
    public boolean accept(Manager man, Message msg) {
        return false; // should not be called
    }

    
    // replace 
    public Object preferredPosition() {
        return java.awt.BorderLayout.CENTER;
    }
    
    public JComponent createPanel(Manager man, Message msg) {
        return new Display(man, msg);
    }
    
    
    private class Display extends JPanel implements ActionListener {
        private Manager man;
        private Message msg;
        
        private String socket;
        private String key;
        
        
        
        public Display(Manager man, Message msg) {
            this.man = man;
            this.msg = msg;

            PacketExtension ext = msg.getExtension("x", extensionNamespace());
            if (ext instanceof DefaultPacketExtension) {
                DefaultPacketExtension def = (DefaultPacketExtension)ext;
                
                socket = def.getValue("socket");
                key = def.getValue("key");
            }

            if (socket != null && key != null) {
                setLayout(new BorderLayout());
                JLabel label = new JLabel();
                label.setText("<html><b>" + 
                    man.getContactList().getLongDisplayName(msg.getFrom()) +
                    "</b> invites you to control his UI. Do you want to connect?");
                add(label, BorderLayout.NORTH);
                
                JButton accept = new JButton("Accept");
                accept.addActionListener(this);
                add(accept, BorderLayout.EAST);
            }
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) { // accept
            new Thread(new Runnable() {
                public void run() {
                    byte[] keyData = StringUtils.decodeBase64(key);
                    
                    try {
                        org.netbeans.modules.jabber.remote.Client.createSocketClient(socket, keyData);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }).start();
        }
        
    }
    
    
}
