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
