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
/*
 * SplitLayoutManager.java
 *
 * Created on May 2, 2004, 4:47 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 *
 * @author  tim
 */
public class SplitLayoutManager implements LayoutManager {
    /** Creates a new instance of SplitLayoutManager */
    public SplitLayoutManager() {
    }
    
    public void addLayoutComponent(String str, Component component) {
    }
    
    public void layoutContainer(Container container) {
        SplitLayoutModel mdl = ((SplitContainer) container).getLayoutModel();
        Component[] c = container.getComponents();
        
        Rectangle r = new Rectangle();
        for (int i=0; i < c.length; i++) {
            mdl.getBounds (c[i], r);
//            System.err.println(r);
            c[i].setBounds (r.x, r.y, r.width, r.height);
        }
    }
    
    public Dimension minimumLayoutSize(Container container) {
        return container.getSize();
    }
    
    public Dimension preferredLayoutSize(Container container) {
        return container.getSize();
    }
    
    public void removeLayoutComponent(Component component) {
    }
}
