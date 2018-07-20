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

package org.netbeans.modules.jabber;

import java.awt.Font;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import org.jivesoftware.smack.XMPPConnection;

/**
 *
 * @author  nenik
 */
public class Main {

    public static void initCustomFontSize (int uiFontSize) {
        Font nbDialogPlain = new FontUIResource("Dialog", Font.PLAIN, uiFontSize); // NOI18N
        Font nbDialogBold = new FontUIResource("Dialog", Font.BOLD, uiFontSize); // NOI18N
        Font nbSerifPlain = new FontUIResource("Serif", Font.PLAIN, uiFontSize); // NOI18N
        Font nbSansSerifPlain = new FontUIResource("SansSerif", Font.PLAIN, uiFontSize); // NOI18N
        Font nbMonospacedPlain = new FontUIResource("Monospaced", Font.PLAIN, uiFontSize); // NOI18N
        UIManager.put("controlFont", nbDialogPlain); // NOI18N
        UIManager.put("Button.font", nbDialogPlain); // NOI18N
        UIManager.put("ToggleButton.font", nbDialogPlain); // NOI18N
        UIManager.put("RadioButton.font", nbDialogPlain); // NOI18N
        UIManager.put("CheckBox.font", nbDialogPlain); // NOI18N
        UIManager.put("ColorChooser.font", nbDialogPlain); // NOI18N
        UIManager.put("ComboBox.font", nbDialogPlain); // NOI18N
        UIManager.put("Label.font", nbDialogPlain); // NOI18N
        UIManager.put("List.font", nbDialogPlain); // NOI18N
        UIManager.put("MenuBar.font", nbDialogPlain); // NOI18N
        UIManager.put("MenuItem.font", nbDialogPlain); // NOI18N
        UIManager.put("MenuItem.acceleratorFont", nbDialogPlain); // NOI18N
        UIManager.put("RadioButtonMenuItem.font", nbDialogPlain); // NOI18N
        UIManager.put("CheckBoxMenuItem.font", nbDialogPlain); // NOI18N
        UIManager.put("Menu.font", nbDialogPlain); // NOI18N
        UIManager.put("PopupMenu.font", nbDialogPlain); // NOI18N
        UIManager.put("OptionPane.font", nbDialogPlain); // NOI18N
        UIManager.put("Panel.font", nbDialogPlain); // NOI18N
        UIManager.put("ProgressBar.font", nbDialogPlain); // NOI18N
        UIManager.put("ScrollPane.font", nbDialogPlain); // NOI18N
        UIManager.put("Viewport.font", nbDialogPlain); // NOI18N
        UIManager.put("TabbedPane.font", nbDialogPlain); // NOI18N
        UIManager.put("Table.font", nbDialogPlain); // NOI18N
        UIManager.put("TableHeader.font", nbDialogPlain); // NOI18N
        UIManager.put("TextField.font", nbSansSerifPlain); // NOI18N
        UIManager.put("PasswordField.font", nbMonospacedPlain); // NOI18N
        UIManager.put("TextArea.font", nbDialogPlain); // NOI18N
        UIManager.put("TextPane.font", nbDialogPlain); // NOI18N
        UIManager.put("EditorPane.font", nbSerifPlain); // NOI18N
        UIManager.put("TitledBorder.font", nbDialogPlain); // NOI18N
        UIManager.put("ToolBar.font", nbDialogPlain); // NOI18N
        UIManager.put("ToolTip.font", nbSansSerifPlain); // NOI18N
        UIManager.put("Tree.font", nbDialogPlain); // NOI18N
        UIManager.put("InternalFrame.titleFont", nbDialogBold); // NOI18N
        UIManager.put("windowTitleFont", nbDialogBold); // NOI18N
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String user = args.length == 1 ? args[0] : "default";
        Preferences app = Preferences.userNodeForPackage(Settings.class);

        Settings set = new PreferencesSettings(app.node(user));
        
        initCustomFontSize(11);
//        XMPPConnection.DEBUG_ENABLED = true;

        Manager man = new Manager(set);

        javax.swing.JPanel panel = new org.netbeans.modules.jabber.ui.JabberUI(man);
        
        javax.swing.JFrame frame = new javax.swing.JFrame( "Jabber test");
//        frame.setBounds(1450,800, 120, 400);
        frame.getContentPane().add(panel);
        frame.addWindowListener(new ExitLst(man));
        frame.pack();
        frame.show();
    }
    
    private static class PreferencesSettings extends Settings {
        Preferences pref;
        PreferencesSettings(Preferences pref) {
            this.pref = pref;
        }
        
        public String getUserJid() {
            return pref.get("jid", "");
        }
    
        public void setUserJid(String jid) {
            pref.put("jid", jid);
        }

        public String getPassword() {
            return pref.get("pwd", "");
        }
    
        public void setPassword(String password) {
            pref.put("pwd", password);
        }
    

        public String getResource() {
            return pref.get("res", "NetBeans");
        }
    
        public void setResource(String resource) {
            pref.put("res", resource);
        }

    
        public long getTimeout() {
            return pref.getLong("timeout", 0);
        }
    
        public void setTimeout(int timeout) {
            pref.putLong("timeout", timeout);
        }
    
        public int getAutologin() {
            return pref.getInt("autologin", 0);
        }

        public void setAutologin(int state) {
            pref.putInt("autologin", state);
        }        
    }
    
    private static class ExitLst extends java.awt.event.WindowAdapter {
        Manager man;
        
        ExitLst(Manager man) {
            this.man = man;
        }
        
        public void windowClosing(java.awt.event.WindowEvent e) {
            man.shutdown();
            System.exit(0);
        }
    }  

}
