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
 * Action that opens a Hex Editor
 *
 * @author Tim Boudreau
 */
public class HexEditAction extends CookieAction {
    
    public HexEditAction() {
    }
    
    public void performAction (Node[] n) {
        DataObject dob = (DataObject) n[0].getLookup().lookup (DataObject.class);
        FileObject fileObject = dob.getPrimaryFile();
        File f = FileUtil.toFile (fileObject);
        if (f != null && f.isFile()) {
            TopComponent tc = new TopComponent (n[0].getLookup());
            tc.setDisplayName (n[0].getDisplayName());
            tc.setLayout (new BorderLayout());
            try {
                tc.add (new HexEditPanel (f), BorderLayout.CENTER);
                tc.open();
                tc.requestActive();
            } catch (FileNotFoundException fe) {
                ErrorManager.getDefault().notify (fe);
            }
        }
    }
    
    protected Class[] cookieClasses() {
        return new Class[] { DataObject.class };
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
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

