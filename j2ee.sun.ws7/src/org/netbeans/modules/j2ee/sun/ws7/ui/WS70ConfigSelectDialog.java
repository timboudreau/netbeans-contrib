/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70ConfigSelectDialog.java 
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Administrator
 */
public class WS70ConfigSelectDialog extends NotifyDescriptor {
    protected WS70ConfigSelectorPanel panel;    
    
    /** Creates a new instance of WS70ConfigSelectDialog */
    public WS70ConfigSelectDialog(String[] args) {        
        super(null, NbBundle.getMessage(WS70ConfigSelectDialog.class, "LBL_TITLE"),
              NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.PLAIN_MESSAGE, null, null);
        panel = new WS70ConfigSelectorPanel(args);
        super.setMessage(panel);
    }
    public String getSelectedConfig(){
        return panel.getSelectedConfig();
    }
    
}
