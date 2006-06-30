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
