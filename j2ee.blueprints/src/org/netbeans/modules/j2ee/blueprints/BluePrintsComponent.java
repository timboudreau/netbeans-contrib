/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.blueprints;

import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.awt.*;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.accessibility.*;
import org.openide.ErrorManager;

/**
 * The BluePrints screen.
 * @author  Ludo
 */
class BluePrintsComponent extends TopComponent{
    static final long serialVersionUID=6021472310161712674L;
    private static BluePrintsComponent component = null;
    private JComponent panel;

    private boolean initialized = false;
    
    private BluePrintsComponent(){
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(BluePrintsComponent.class, "LBL_Tab_Title"));   //NOI18N             
        panel = null;
        initialized = false;
    }
    
    protected String preferredID(){
        return "BluePrintsComponent";    //NOI18N
    }
    
    /**
     * #38900 - lazy addition of GUI components
     */    
    
    private void doInitialize() {
        initAccessibility();
        
        try{
            panel =(JComponent)Class.forName(NbBundle.getMessage(BluePrintsComponent.class,"CLASS_content_panel")).newInstance();
        }catch(Exception e){
            ErrorManager.getDefault().notify(e);
        }
        if(panel == null)
            return;

        // Removed code that added scrollbars - we do not want them here.
        add(panel);
        setFocusable(true);
    }
        
    /* Singleton accessor. As BluePrintsComponent is persistent singleton this
     * accessor makes sure that BluePrintsComponent is deserialized by window system.
     * Uses known unique TopComponent ID "BluePrints" to get BluePrintsComponent instance
     * from window system. "BluePrints" is name of settings file defined in module layer.
     */
    public static BluePrintsComponent findComp() {
        if (component == null) {
            TopComponent tc = WindowManager.getDefault().findTopComponent("BluePrints"); // NOI18N
            if (tc != null) {
                if (tc instanceof BluePrintsComponent) {
                    component = (BluePrintsComponent) tc;
                } else {
                    //Incorrect settings file?
                    IllegalStateException exc = new IllegalStateException
                    ("Incorrect settings file. Unexpected class returned." // NOI18N
                    + " Expected:" + BluePrintsComponent.class.getName() // NOI18N
                    + " Returned:" + tc.getClass().getName()); // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    //Fallback to accessor reserved for window system.
                    BluePrintsComponent.createComp();
                }
            } else {
                //BluePrintsComponent cannot be deserialized
                //Fallback to accessor reserved for window system.
                BluePrintsComponent.createComp();
            }
        }
        return component;
    }
    
    /* Singleton accessor reserved for window system ONLY. Used by window system to create
     * BluePrintsComponent instance from settings file when method is given. Use <code>findComp</code>
     * to get correctly deserialized instance of BluePrintsComponent. */
    public static BluePrintsComponent createComp() {
        if(component == null)
            component = new BluePrintsComponent();
        return component;
    }
    
    /** Overriden to explicitely set persistence type of BluePrintsComponent
     * to PERSISTENCE_ALWAYS */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    static void clearRef(){
        component = null;
    }
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(BluePrintsComponent.class, "ACS_BluePrints_DESC")); // NOI18N
    }
    
    public void requestFocus(){
        super.requestFocus();
        panel.requestFocus();
    }
    
    public boolean requestFocusInWindow(){
        super.requestFocusInWindow();
        return panel.requestFocusInWindow();
    }
    

    /**
     * #38900 - lazy addition of GUI components
     */    
    public void addNotify() {
        if (!initialized) {
            initialized = true;
            doInitialize();
        }
        super.addNotify();
    }
    
    /**
     * Called when <code>TopComponent</code> is about to be shown.
     * Shown here means the component is selected or resides in it own cell
     * in container in its <code>Mode</code>. The container is visible and not minimized.
     * <p><em>Note:</em> component
     * is considered to be shown, even its container window
     * is overlapped by another window.</p>
     * @since 2.18
     *
     * #38900 - lazy addition of GUI components
     *
     */
    protected void componentShowing() {
        if (!initialized) {
            initialized = true;
            doInitialize();
        }
        super.componentShowing();
    }
    
}

