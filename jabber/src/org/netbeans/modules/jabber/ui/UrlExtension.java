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
