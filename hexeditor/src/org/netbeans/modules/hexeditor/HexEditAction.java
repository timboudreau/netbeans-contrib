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

