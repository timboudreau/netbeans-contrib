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
package org.netbeans.modules.hexeditor;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import org.netbeans.modules.hexedit.HexEditPanel;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.windows.TopComponent;

/**
 * Action that opens the Navigator window
 *
 * @author Tim Boudreau
 */
public class HexEditAction extends CallableSystemAction {
    
    public HexEditAction() {
    }
    
    public void performAction () {
        Node[] n = TopComponent.getRegistry().getActivatedNodes();
        if (n.length != 1) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        DataObject dob = (DataObject) n[0].getLookup().lookup (DataObject.class);
        if (dob != null && dob.isValid()) {
            FileObject fileObject = dob.getPrimaryFile();
            if (fileObject != null && fileObject.isValid() && !fileObject.isVirtual()) {
                File f = FileUtil.toFile (fileObject);
                if (f != null) {
                    TopComponent tc = new TopComponent (n[0].getLookup());
                    tc.setDisplayName (n[0].getDisplayName());
                    tc.setLayout (new BorderLayout());
                    try {
                        tc.add (new HexEditPanel (f), BorderLayout.CENTER);
                    } catch (FileNotFoundException fe) {
                        ErrorManager.getDefault().notify (fe);
                    }
                    tc.open();
                    tc.requestActive();
                }
            }
        }
    }

    public String getName () {
        return NbBundle.getMessage ( HexEditAction.class, "LBL_Action" ); //NOI18N
    }
    
    public String displayName () {
        return getName();
    }

    protected String iconResource () {
        return "org/netbeans/modules/hexeditor/resources/HexEditIcon.gif"; //NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean asynchronous () {
        return false;
    }
}

