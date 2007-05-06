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
package org.netbeans.modules.doap;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.Field;
import org.netbeans.modules.versioning.system.cvss.executor.CheckoutExecutor;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.versioning.system.cvss.ExecutorGroup;
import org.netbeans.modules.versioning.system.cvss.ui.actions.checkout.CheckoutAction;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Tim Boudreau
 */
final class CvsCheckoutHandler implements CheckoutHandler {
    private final String root;
    private final String modules;
    private final String tag;
    private final File dest;
    /** Creates a new instance of CvsCheckoutHandler */
    public CvsCheckoutHandler(String root, String modules, String tag, File dest) {
        this.root = root;
        this.modules = modules;
        this.tag = tag;
        this.dest = dest;
    }
    
    public String checkout(final ProgressHandle progress) {
        assert !EventQueue.isDispatchThread();
        try {
            CheckoutAction a = (CheckoutAction) SystemAction.get(CheckoutAction.class);
            String tag = this.tag;
            if (tag == null) tag = "HEAD"; //NOI18N
            
            ExecutorGroup group = new ExecutorGroup(NbBundle.getMessage(
                    CvsCheckoutHandler.class, "MSG_Checkout", root), //NOI18N
                    true);
            
            
            CheckoutExecutor ce = a.checkout(root, modules, tag, dest.getPath(), true,
                    group);
            try {
                Field fld = ExecutorGroup.class.getDeclaredField("progressHandle"); //NOI18N
                fld.setAccessible(true);
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
