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
import javax.swing.border.EmptyBorder;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.netbeans.modules.jabber.Manager;
import org.openide.awt.HtmlBrowser;

/**
 *
 * Provides a displayer for extensions in the format:
 * <pre>
 *   &lt;x xmlns="jabber:x:oob">
 *     &lt;url>http://jabber.netbeans.org&lt;/url>
 *     &lt;desc>Check the website&lt;/desc>
 *   &lt;/x>
 * </pre>
 *
 * @author  nenik
 */
public final class UrlExtension implements ExtensionDisplayer {
    
    /** Creates a new instance of UrlExtension */
    public UrlExtension() {
    }
    
    public String extensionNamespace() {
        return "jabber:x:oob";
    }
    
    public boolean accept(Manager man, Message msg) {
        return false; // should not be called
    }

    public Object preferredPosition() {
        return java.awt.BorderLayout.SOUTH;
    }
    
    public JComponent createPanel(Manager man, Message msg) {
        return new Display(man, msg);
    }
    
    
    private class Display extends JPanel implements ActionListener {
        private Manager man;
        private Message msg;
        private URL url;
        
        public Display(Manager man, Message msg) {
            this.man = man;
            this.msg = msg;
            
            PacketExtension ext = msg.getExtension("x", extensionNamespace());
            if (ext instanceof DefaultPacketExtension) {
                DefaultPacketExtension def = (DefaultPacketExtension)ext;
                String urlText = def.getValue("url");
                try {
                    url = new URL(urlText);
                } catch (MalformedURLException e) { // TODO: Log?
                }
            }
            
            if (url != null) { // fill in the UI
                setBorder(new EmptyBorder(12, 0, 0, 0));
                setLayout(new BorderLayout());

                JLabel label = new JLabel("Url:");
                JTextField text = new JTextField();
                
                label.setLabelFor(text);
                add(label, BorderLayout.WEST);
                
                
                text.setText(url.toString());
                text.setEditable(false);
                add(text, BorderLayout.CENTER);
                
                JButton button = new JButton("Show");
                button.addActionListener(this);
                // is there a browser configured?
                button.setEnabled(HtmlBrowser.URLDisplayer.getDefault() != null);
                add(button, BorderLayout.EAST);
            }
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        }
        
    }
    
    
}
