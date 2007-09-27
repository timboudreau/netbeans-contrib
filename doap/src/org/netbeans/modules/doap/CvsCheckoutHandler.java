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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
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
