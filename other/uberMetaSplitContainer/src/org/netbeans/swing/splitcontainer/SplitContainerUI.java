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
 * SplitContainerUI.java
 *
 * Created on May 2, 2004, 4:51 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author  tim
 */
public abstract class SplitContainerUI extends ComponentUI {
    protected SplitContainer container;
    protected SplitLayoutModel layoutModel = null;
    
    /** Creates a new instance of SplitContainerUI */
    public SplitContainerUI(SplitContainer c) {
        this.container = c;
    }
    
    public final void installUI (JComponent jc) {
        assert jc == container;
        container.setLayout (createLayoutManager());
        layoutModel = createLayoutModel();
        container.setLayoutModel (layoutModel);
        install();
    }
    
    public final void uninstallUI (JComponent jc) {
        assert jc == container;
        uninstall();
        container.setLayout(null);
        container.setLayoutModel(null);
    }
    
    protected void install() {
        //do nothing
    }
    
    protected void uninstall() {
        //do nothing
    }
    
    protected LayoutManager createLayoutManager() {
        return new SplitLayoutManager();
    }
    
    protected abstract SplitLayoutModel createLayoutModel();
    
}
