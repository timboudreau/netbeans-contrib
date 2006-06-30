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

package org.netbeans.modules.corba.wizard;

import org.openide.nodes.Node;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.actions.*;
import java.util.ResourceBundle;
import org.netbeans.modules.corba.IDLDataObject;

/**
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public class CorbaWizardAction extends NodeAction {

    public static final String ICON = "/org/netbeans/modules/corba/wizard/resources/CorbaWizard.gif";
    private static ResourceBundle bundle = null;
  

    /** Creates new CorbaWizardAction */
    public CorbaWizardAction() {
    }
  
    public String getName () {
        return getLocalizedString("CLT_CorbaWizardAction");
    }
  
    /** No help jet */
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }
  
    public boolean enable (Node[] nodes) {
        return true;
    }
    
    public void performAction (Node[] nodes) {
        CorbaWizard wizard;
        
        
        if (nodes == null || nodes.length != 1) {
            wizard = new CorbaWizard();
        } else { 
            DataObject obj = (DataObject) nodes[0].getCookie (DataObject.class);
            if (obj instanceof IDLDataObject) {
                wizard = new CorbaWizard ((IDLDataObject)obj);
            }
            else {
                wizard = new CorbaWizard ();
            }
        }
        wizard.run();
    }
  
    protected String iconResource () {
        return ICON;
    }
  
    public static String getLocalizedString (String text){
        if (bundle == null)
            bundle = NbBundle.getBundle(CorbaWizardAction.class);
        return bundle.getString(text);
    }
  
}
