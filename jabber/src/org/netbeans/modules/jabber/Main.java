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
