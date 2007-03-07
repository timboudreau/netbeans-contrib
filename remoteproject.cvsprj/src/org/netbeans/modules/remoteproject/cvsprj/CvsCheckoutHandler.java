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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.remoteproject.cvsprj;

import java.io.File;
import java.lang.reflect.Field;
import org.netbeans.modules.versioning.system.cvss.executor.CheckoutExecutor;
import org.openide.filesystems.FileUtil;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.remoteproject.CheckoutHandler;
import org.netbeans.modules.versioning.system.cvss.ExecutorGroup;
import org.netbeans.modules.versioning.system.cvss.ui.actions.checkout.CheckoutAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Tim Boudreau
 */
public class CvsCheckoutHandler implements CheckoutHandler {
    
    /** Creates a new instance of CvsCheckoutHandler */
    public CvsCheckoutHandler() {
    }
    
    public boolean canCheckout(FileObject template) {
        return "cvs".equals (template.getAttribute("system")); //NOI18N
    }

    public String checkout(FileObject template, FileObject dest,
                           final ProgressHandle progress) {
        try {
        assert template != null;
        assert dest != null;
        assert dest.isFolder();
       CheckoutAction a = (CheckoutAction) SystemAction.get(CheckoutAction.class);
       
       String root = (String) template.getAttribute("cvsroot"); //NOI18N
       String modules = (String) template.getAttribute("what"); //NOI18N
       String tag = (String) template.getAttribute("tag"); //NOI18N
       if (tag == null) tag = "HEAD"; //NOI18N
       
       System.err.println("Will check out " + root + " " + modules + " " + tag);
       
       File f = FileUtil.toFile (dest);
       assert f != null : "Not a normal file: " + dest; //NOI18N
       String workingDir = f.getPath();
       
       System.err.println("Creating a group");
       ExecutorGroup group = new ExecutorGroup(NbBundle.getMessage(
               CvsCheckoutHandler.class, "MSG_Checkout", template.getName()), //NOI18N
               true);
       
       System.err.println("Group " + group);

       //XXX authentication
       
       CheckoutExecutor ce = a.checkout(root, modules, tag, workingDir, true,
                                         group);
       System.err.println("Created Checkout Executor " + ce);
       try {
           Field fld = ExecutorGroup.class.getDeclaredField("progressHandle"); //NOI18N
           fld.setAccessible (true);
           System.err.println("Set progress handle field");
           fld.set(group, progress);
       } catch (Exception e) {
           Exceptions.printStackTrace(e);
           e.printStackTrace();
       }
       progress.start();
       group.execute();
        } catch (RuntimeException re) {
            Exceptions.printStackTrace(re);
        }
       return null;
    }
}
