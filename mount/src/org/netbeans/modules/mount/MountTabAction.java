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

package org.netbeans.modules.mount;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.mount.MountTab;
import org.openide.windows.TopComponent;

public class MountTabAction extends AbstractAction {
    
    public MountTabAction() {
        super("Mount"/* XXX icon */);
    }
    
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = MountTab.findDefault();
        tc.open();
        tc.requestActive();
    }
    
}
