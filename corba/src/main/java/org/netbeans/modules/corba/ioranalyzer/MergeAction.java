/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/*
 * MergeAction.java
 *
 * Created on November 8, 2000, 4:39 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import org.openide.*;
import org.openide.actions.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.nodes.*;
/**
 *
 * @author  tzezula
 * @version
 */
public class MergeAction extends CookieAction {

    private Dialog dlg;

    /** Creates new MergeAction */
    public MergeAction() {
    }
    
    
    protected Class[] cookieClasses () {
        return new Class[] {IORNode.class};
    }
    
    protected boolean enable (Node[] nodes) {
        if (nodes.length < 2)
            return false;
        for (int i=0; i< nodes.length; i++) {
            if (nodes[i].getCookie(IORNode.class) == null)
                return false;
        }
        return true;
    }
    
    protected int mode () {
        return org.openide.util.actions.CookieAction.MODE_ALL;
    }
    
    public void performAction (final Node[] node) {
        TopManager tm = TopManager.getDefault();
        final FileName panel = new FileName();
        DialogDescriptor dd = new DialogDescriptor (panel, NbBundle.getBundle(MergeAction.class).getString("TXT_NewIORFile"),true,
            new ActionListener () {
                public void actionPerformed (java.awt.event.ActionEvent event) {
                    if (event.getSource() == DialogDescriptor.OK_OPTION) {
                        String name = panel.getName();
                        DataObject folder = panel.getPackage();
                        if (folder == null) {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(MergeAction.class).getString("TXT_NoPackage"), NotifyDescriptor.ERROR_MESSAGE));
                            return;
                        }
                        if (name == null || name.length() == 0) {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(MergeAction.class).getString("TXT_NoName"), NotifyDescriptor.ERROR_MESSAGE));
                            return;
                        }
                        FileObject fileFolder = folder.getPrimaryFile();
                        java.io.PrintWriter out = null;
                        FileLock lock = null;
                        try {
                            FileObject destination = fileFolder.createData (name,"ior");
                            String[] iors = new String[node.length];
                            for (int i=0; i< iors.length; i++) {
                                IORNode iorNode = (IORNode) node[i].getCookie(IORNode.class);
                                if (node == null) {
                                    TopManager.getDefault().notify (new NotifyDescriptor.Message(NbBundle.getBundle(IORMerge.class).getString("TXT_BadNode"),NotifyDescriptor.ERROR_MESSAGE));
                                    return;
                                }
                                iors[i] = ((ProfileChildren)iorNode.getChildren()).dataObject.getContent();
                            }
                            String mergedIOR = IORMerge.merge (iors);
                            lock = destination.lock();
                            out = new java.io.PrintWriter ( new java.io.OutputStreamWriter ( destination.getOutputStream (lock)));
                            out.print ("IOR:");
                            out.println (mergedIOR);
                        }catch (org.omg.CORBA.BAD_PARAM badParam) {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(IORMerge.class).getString("TXT_BadType"), NotifyDescriptor.ERROR_MESSAGE));
                            return;
                        }
                        catch (java.io.IOException ioe) {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(IORMerge.class).getString("TXT_CanNotCreate"),NotifyDescriptor.ERROR_MESSAGE));
                            return;
                        }
                        finally {
                            if (out != null)
                                out.close();
                            if (lock != null)
                                lock.releaseLock();
                        }
                    }
                    dlg.setVisible (false);
                    dlg.dispose();
                }
        });
        dd.setClosingOptions (new Object[0]);
        dlg = tm.createDialog (dd);
        dlg.setVisible (true);
    }
    
    public String getName () {
        return NbBundle.getBundle(MergeAction.class).getString ("TXT_Merge");
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
}