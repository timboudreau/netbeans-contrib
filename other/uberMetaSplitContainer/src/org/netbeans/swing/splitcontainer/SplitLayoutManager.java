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

    public void layoutContainer(Containercontainer) {
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
