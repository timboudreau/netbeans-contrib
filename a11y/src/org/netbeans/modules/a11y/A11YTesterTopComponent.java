/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.a11y;

import org.openide.windows.TopComponent;

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
        
        setLayout(new java.awt.BorderLayout());
        
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

