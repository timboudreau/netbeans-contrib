package org.netbeans.modules.vcscore.actions;

/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

import java.text.*;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import org.openide.awt.JMenuPlus;
import javax.swing.*;
import javax.swing.event.*;

import org.openide.awt.Actions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.util.actions.*;
import org.openide.nodes.*;
import org.openide.cookies.*;
import org.openide.DialogDescriptor;
import org.openide.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;

import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.actions.*;

/** 
 * Action that displays a submenu of actions when presented in a menu or popup.
 * It reads the menu structure from the default filesystem.
 * Example of layer definition:
 * <PRE>
           <folder name="PopupMenu">
              <attr name="SystemFileSystem.localizingBundle" stringvalue="org.netbeans.modules.cvsclient.Bundle"/>

                <file name="Refresh.instance">
                   <attr name="instanceClass"             stringvalue="org.netbeans.modules.cvsclient.actions.CallBackCommandAction"/>
                   <attr name="instanceCreate"            methodvalue="org.netbeans.modules.cvsclient.actions.CallBackCommandAction.createCommandActionInstance"/>
                   <attr name="CommandActionDefinition"   stringvalue="org.netbeans.modules.cvsclient.actions.JRefreshCommandAction"/>
                   <attr name="instanceOf"                stringvalue="org.openide.util.SystemAction, org.openide.util.actions.NodeAction, org.netbeans.modules.vcscore.actions.ClusterItemVisualizer"/>
                </file>   
                <attr boolvalue="true" name="Refresh.instance/RecRefresh.instance"/>

                <file name="RecRefresh.instance">
                   <attr name="instanceClass"             stringvalue="org.netbeans.modules.cvsclient.actions.CallBackCommandAction"/>
                   <attr name="instanceCreate"            methodvalue="org.netbeans.modules.cvsclient.actions.CallBackCommandAction.createCommandActionInstance"/>
                   <attr name="CommandActionDefinition"   stringvalue="org.netbeans.modules.cvsclient.actions.JRecRefreshCommandAction"/>
                   <attr name="instanceOf"                stringvalue="org.openide.util.SystemAction, org.openide.util.actions.NodeAction, org.netbeans.modules.vcscore.actions.ClusterItemVisualizer"/>
                </file>
 *</PRE>
 *
 * @author  mkleint
 */

public class ClusteringAction extends GeneralCommandAction  {

    
    public static final String POPUP_DEFINITION_FOLDER = "Popup_Definition_Folder";  //NOI18N
    public static final String IS_SWITCHABLE_POPUP = "IsSwitchable";
    
    protected static final String CLIENT_PROP_ACTION = "SELECTED_COMMAND_ACTION"; //NOI18N
    protected transient boolean  CTRL_Down = false;
    
    private transient ArrayList switchableList;
    
    protected transient boolean isMenu;
    
//TODO    
    static final long serialVersionUID = 0;

    private transient JMenu lazyMenu = null;
    private transient MenuListener menuListener = new MenuListener() {
        
                public void menuDeselected(MenuEvent e) {
 //                   deselectedMenu();
                }
                public void menuCanceled(MenuEvent e) {
                    //                deselectedMenu();
                    //                System.out.println("menu canceled");
                }
                public void menuSelected(MenuEvent e) {
                    deselectedMenu();
                    //                System.out.println("Selected menu");
                }
            };

    private transient  javax.swing.event.MenuKeyListener menuKeyListener = 
                                      new javax.swing.event.MenuKeyListener() {

        public void menuKeyTyped(javax.swing.event.MenuKeyEvent p1) {
        }
        public void menuKeyPressed(javax.swing.event.MenuKeyEvent p1) {
            boolean CTRL_IsDown = p1.getKeyCode() == javax.swing.event.MenuKeyEvent.VK_CONTROL; // NOI18N            
            if (CTRL_IsDown) {
                changeCtrlSigns(CTRL_IsDown);
                CTRL_Down = CTRL_IsDown;
            }
        }
        public void menuKeyReleased(javax.swing.event.MenuKeyEvent p1) {
            boolean CTRL_IsUp = p1.getKeyCode() == javax.swing.event.MenuKeyEvent.VK_CONTROL; // NOI18N            
            if (CTRL_IsUp) {
                changeCtrlSigns(!CTRL_IsUp);
                CTRL_Down = !CTRL_IsUp;
            }
        }
    };
            
    
    public ClusteringAction() {
        super();
    }

    
    protected String iconResource () {
        return null; //"SampleActionIcon.gif";
    }
    
    public String getName() {
        return NbBundle.getBundle(ClusteringAction.class).getString("ClusteringAction.displayName");
    }
    
    /**
     * This method decides where the definition of the popup should be taken from, 
     * default implementation takes the value of POPUP_DEFINITION_FOLDER property of the action.
     * @return path to the root folder within the default filesystem.
     *
     */
    
    protected String getClusterRootPath() {
        return this.getValue(POPUP_DEFINITION_FOLDER).toString();
    }

    /**
     * Returns if the CTRL key switching is enabled on items with 
     * ClusterItemVisualizer.isSwitchable() == true.
     * By default reads the property(value) IS_SWITCHABLE_POPUP
     */
    protected boolean isSwitchingEnabled() {
        Boolean toReturn = (Boolean)getValue(IS_SWITCHABLE_POPUP);
        if (toReturn != null) {
            return toReturn.booleanValue();
        }
        return false;
    }
    
    /**
     *  Indicates wheather the CTRL key is pressed. For use in subclasses' processing.
     */
    protected boolean isCTRLDown() {
        return CTRL_Down;
    }
    /**
     * Get a menu item that can present this action in a <code>JMenu</code>.
     */
    public JMenuItem getMenuPresenter() {
        if (lazyMenu != null) {
            lazyMenu.removeMenuListener(menuListener);
            lazyMenu.removeMenuKeyListener(menuKeyListener);
        }
        lazyMenu = new LazyPopup(true);
        return lazyMenu;
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        if (lazyMenu != null) {
            lazyMenu.removeMenuListener(menuListener);
            lazyMenu.removeMenuKeyListener(menuKeyListener);
        }
        lazyMenu = new LazyPopup(false);
        return lazyMenu;
    }
    
    void createPresenter(boolean isMenu, JMenu menu) {
        switchableList = new ArrayList();
        this.isMenu = isMenu;
        menu.addMenuKeyListener(menuKeyListener);
        menu.addMenuListener(menuListener);
        createPresenter(menu, getClusterRootPath());
    }

/**
 * Creates one level of submenu items based on the default filesystem definition.
 * The method is recursive on folders found.
 * @param menuPath the resource path within the default filesystem, pointing to the
 *  root folder of the menu.
 */    
    protected void createPresenter(JMenu menu, String menuPath){
        
        //JMenu menu=new JMenuPlus(g("CvsClientAction.displayName")); // NOI18N
        JMenuItem item=null;
        JMenu submenu = null;

        FileSystem defFs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject menuRoot = defFs.findResource(menuPath);
        if (menuRoot == null) {
            return;
        }
        DataFolder dataFolder = null;
        try {
            dataFolder = (DataFolder)DataObject.find(menuRoot);
        } catch (DataObjectNotFoundException exc) {
            return;
        }
        DataObject[] children = dataFolder.getChildren();
        if (children != null && children.length > 0) {
            boolean lastWasSeparator = false;
            for (int i = 0; i < children.length; i++) {
                if (children[i] instanceof DataFolder) {
                    submenu = new JMenu();
                    Actions.setMenuText(submenu, children[i].getName(), isMenu);
                    String path = menuPath + "/" + children[i].getPrimaryFile().getName(); //NOI18N
                    createPresenter(submenu, path);
                    if (submenu.getItemCount() > 0) {
                        submenu.addActionListener(this);
//                        submenu.setIcon(null);
                        menu.add(submenu);
                    }
                    lastWasSeparator = false;
                }
                InstanceCookie.Of cookie = (InstanceCookie.Of)children[i].getCookie(InstanceCookie.Of.class);
                if (cookie != null) {
                    try {
                        if (cookie.instanceOf(ClusterItemVisualizer.class)) {
                            Object obj = cookie.instanceCreate();
                            if (obj != null) {
                                ClusterItemVisualizer act = (ClusterItemVisualizer)obj;
                                if (checkItemEnable(act)) {
                                    item = createItem(act);
                                    menu.add(item);
                                    lastWasSeparator = false;
                                }
                            }
                        }
                        if (cookie.instanceOf(JSeparator.class) && !lastWasSeparator) {
                            menu.addSeparator();
                            lastWasSeparator = true;
                        }
                    } catch (Exception exc) {
                        ErrorManager.getDefault().notify(ErrorManager.ERROR, exc);
                    }
                }
            }
        }
        
    }

    /**
     * Checks  wheather the item in menu is enabled or not.
     * By default lets the ClusterItemVisualizer decide itself, by asking it's
     * isItemEnabled() method.
     */
    protected boolean checkItemEnable(ClusterItemVisualizer item) {
        return item.isItemEnabled(this);
    }
    
    

    
    //-------------------------------------------
    private JMenuItem createItem(ClusterItemVisualizer action) {
        JMenuItem item = new JMenuItem ();
        String nm = action.getName();
        if (!this.isSwitchingEnabled() &&  action.isSwitchable()) {
            nm = nm + NbBundle.getBundle(ClusteringAction.class).getString("ClusteringAction.DialogDots"); // NOI18N
        }
        Actions.setMenuText (item, nm, isMenu);
        if (isMenu) {
            item.setIcon(action.getIcon());
        }
        item.putClientProperty(CLIENT_PROP_ACTION, action);
        item.addActionListener(this);
        assignHelp(item, action.getClass().getName());
        if (this.isSwitchingEnabled() && action.isSwitchable()) {
            switchableList.add(item);
        }    
        return item;
    }
    
    private void assignHelp (JMenuItem item, String commandName) {
        HelpCtx.setHelpIDString (item, ClusteringAction.class.getName () + "." + commandName); // NOI18N
    }
    

    public void actionPerformed(java.awt.event.ActionEvent e){
//        System.out.println("performing cvs client action..");
        Object obj = e.getSource();
        if (obj instanceof JMenuItem) {
            JMenuItem item = (JMenuItem)obj;
            ClusterItemVisualizer action = (ClusterItemVisualizer)item.getClientProperty(CLIENT_PROP_ACTION);
            action.setSwitched(CTRL_Down);
//            CTRL_Down = false; // HACK :(
            action.performAction(getActivatedNodes(), this);
        }
    }
    
/*    public boolean showCustomizer() {
        //TODO based on
        return true;
    }
 */
    
        

    private void deselectedMenu() {
        changeCtrlSigns(false);
        CTRL_Down = false;
    }   

    private void changeCtrlSigns(boolean newValue) {
        if (newValue == CTRL_Down) return;
//        System.out.println("new value = " + newValue);
        String plusValue = NbBundle.getBundle(ClusteringAction.class).getString("ClusteringAction.DialogDots"); // NOI18N
        Iterator it = switchableList.iterator();
        while (it.hasNext()) {
            JMenuItem item = (JMenuItem)it.next();
            String text = item.getText();
            if (newValue) {
                // do turn ctrl sign on
                if (!text.endsWith(plusValue)) {text = text + plusValue;}
            }   else { 
                // turn it off - ctrl released
                if (text.endsWith(plusValue)) {
                   text = text.substring(0,text.length() - plusValue.length());
                }
            }    
            item.setText(text);
        }    
    }    

    protected boolean clearSharedData() {
        if (lazyMenu != null) {
            lazyMenu.removeMenuListener(menuListener);
            lazyMenu.removeMenuKeyListener(menuKeyListener);
        }
        lazyMenu = null;
        menuListener = null;
        return super.clearSharedData();
    }    
    
    /** Menu item which will create its items lazilly when the popup will becomming visible.
     * Performance savings.*/
    class LazyPopup extends JMenuPlus {

        /** Icon. */
        private Icon icon = null;
        
        /** Indicates if is part of menu, i.e. if should have icons. */
        private boolean isMenu;
        
        /** Indicates whether menu items were created. */
        private boolean created = false;

        
        /** Constructor. */
        LazyPopup(boolean isMenu) {
            this.isMenu = isMenu;
            Actions.setMenuText(this,
                ClusteringAction.this.getName(), isMenu); // NOI18N
            if (isMenu) {
                setIcon(ClusteringAction.this.getIcon());
            }
            HelpCtx.setHelpIDString(this, ClusteringAction.class.getName());
        }
        
        
        /** Gets popup menu. Overrides superclass. Adds lazy menu items creation. */
        public JPopupMenu getPopupMenu() {
            if(!created)
                createMenuItems();
            
            return super.getPopupMenu();
        }

        /** Creates items when actually needed. */
        private void createMenuItems() {
            created = true;
            removeAll();

            ClusteringAction.this.createPresenter(isMenu, this);
        }
    } // End of class LazyPopup.    
  
}
