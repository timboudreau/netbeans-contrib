/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views;

import org.netbeans.modules.vcscore.actions.*;
import org.openide.util.NbBundle;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.filesystems.*;
import java.util.*;

/**
 * 
 * @author  Milos Kleint
 */
public abstract class FileVcsInfoAction extends GeneralCommandAction {

    /** Creates new UpdateCommandAction */
    public FileVcsInfoAction() {
    }

     
     /**
      * Each supporter are checked if if they enable the action.
      * All supporters need to come to a concensus in order for the action to be enabled.
      * *experimental* annotates the toolbar tooltip according to the supporter's requests.
      */
     protected boolean enable(Node[] nodes) {
/*         if (nodes.length == 1) {
             FileVcsInfo info = (FileVcsInfo)nodes[0].getCookie(FileVcsInfo.class);
             if (info != null) {
                 return super.enable(nodes);
             }
         }
 */
         return super.enable(nodes);
     }
     
     /** This method doesn't extract the fileobjects from the activated nodes itself, but rather
      * consults delegates to  a list of supporters.
      */
     protected void performAction(Node[] nodes) {
/*         if (nodes.length == 1) {
             System.out.println("nodes=1");
             FileVcsInfo info = (FileVcsInfo)nodes[0].getCookie(FileVcsInfo.class);
             if (info != null) {
 */
                 super.performAction(nodes);
//             }
//         }
//         super.performAction(nodes);
     }
     
}
