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

package org.netbeans.modules.a11y;

import org.openide.windows.TopComponent;                                                                                                                                        
import org.openide.windows.Mode;                                                                                                                                                

import java.awt.Rectangle;                                                                                                                                                      
import java.awt.BorderLayout;                                                                                                                                                   
import java.awt.Dimension;

import javax.swing.JLabel;

import org.netbeans.a11y.ui.AccessibilityPanel;
import javax.swing.JPanel;
                                                                                                                                                                                
/** A dialog to customize and run the accessibility test.
 *  @author  Marian.Mirilovic@sun.com
 */
public class A11YTesterTopComponent extends TopComponent {                                                                                                                         
        
    private static A11YTesterTopComponent instance;                                                                                                                                
    private static AccessibilityPanel accPanel;
    private static final String iconURL = "/org/netbeans/modules/a11y/resources/a11ytest.gif"; // NOI18N 
                                                                                                                                                                                
    /** Creates new A11YTesterTopComponent */                                                                                                                                      
    private A11YTesterTopComponent() {
        setName("A11YTester");  // NOI18N
        setIcon(org.openide.util.Utilities.loadImage(iconURL));
        // Force winsys to not show tab when this comp is alone                                                                                                                 
        putClientProperty("TabPolicy", "HideWhenAlone");                                                                                                                        
        setToolTipText("A11YTester testing accessibility of designed form.");                                                                                               
    }                                                                                                                                                                           
        
    
    /** Add Notify.
     */
    public void addNotify() {
        
        setLayout(new BorderLayout());                                                                                                                                          
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        accPanel = new AccessibilityPanel();
        
        add(accPanel, java.awt.BorderLayout.CENTER);
        
        super.addNotify();                                                                                                                                                      
    }                                                                                                                                                                           

    
    /** Return instance, if it doesn't exist create it.
     * @return instance of A11YTesterTopComponent */    
    public static A11YTesterTopComponent getInstance() {                                                                                                                           
        if (instance == null)
            instance = new A11YTesterTopComponent();
        return instance;
    }

    /** replaces this in object stream
     * @return  */                                                                                                                                       
    public Object writeReplace() {                                                                                                                                              
        return new ResolvableHelper();                                                                                                                                          
    }                                                                                                                                                                           
     
    
    /** Return panel to AccessibilityPanel
     * @return panel */    
    public AccessibilityPanel getPanel() {
        return accPanel;
    }
    
    
    final public static class ResolvableHelper implements java.io.Serializable {                                                                                                
        public Object readResolve() {                                                                                                                                           
            return A11YTesterTopComponent.getInstance();                                                                                                                           
        }                                                                                                                                                                       
    }                                                                                                                                                                           
}             

