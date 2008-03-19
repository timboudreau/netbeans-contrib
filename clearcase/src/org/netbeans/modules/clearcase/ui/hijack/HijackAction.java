/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.clearcase.ui.hijack;

import org.netbeans.modules.clearcase.FileStatusCache;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;

import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.FileInformation;
import org.netbeans.modules.clearcase.util.ClearcaseUtils;

import org.openide.util.NbBundle;

/**
 * Hijacks all files/folders in the context, making them editable by the user.
 * 
 * @author Maros Sandor
 */
public class HijackAction extends AbstractAction {

    private static final int STATUS_DISABLED    = 0;
    private static final int STATUS_HIJACK      = 1;
    private static final int STATUS_UNHIJACK    = 2;
    
    private static int ALLOW_HIJACK     = FileInformation.STATUS_VERSIONED_UPTODATE;
    private static int ALLOW_UNHIJACK   = FileInformation.STATUS_VERSIONED_HIJACKED;
    
    private final VCSContext    context;
    private final int           status;

    public HijackAction(VCSContext context) {
        this.context = context;
        status = getActionStatus();
        putValue(Action.NAME, status == STATUS_UNHIJACK ? NbBundle.getMessage(HijackAction.class, "Action_Unhijack_Name") : NbBundle.getMessage(HijackAction.class, "Action_Hijack_Name")); //NOI18N
    }

    private int getActionStatus() {
        if (!ClearcaseUtils.containsSnapshot(context)) return STATUS_DISABLED;
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();
        int actionStatus = STATUS_DISABLED;
        Set<File> files = context.getFiles();
        for (File file : files) {
            if ((cache.getInfo(file).getStatus() & ALLOW_HIJACK) != 0) {
                if (actionStatus == STATUS_UNHIJACK) return STATUS_DISABLED;
                actionStatus = STATUS_HIJACK;
            }                
            if ((cache.getInfo(file).getStatus() & ALLOW_UNHIJACK) != 0) {
                if (actionStatus == STATUS_HIJACK) return STATUS_DISABLED;
                actionStatus = STATUS_UNHIJACK;
            }
        }
        return actionStatus;
    }
    
    @Override
    public boolean isEnabled() {
        return status != STATUS_DISABLED;
    }
    
    public void actionPerformed(ActionEvent ev) {
        Set<File> roots = context.getFiles();
        switch (status) {
        case STATUS_HIJACK:
            performHijack(roots.toArray(new File[roots.size()]));
            break;
        case STATUS_UNHIJACK:
            performUnhijack(roots.toArray(new File[roots.size()]));
            break;
        }
    }
    
    private void performUnhijack(File [] files) {
    }

    private static void performHijack(File[] files) {
        for (File file : files) {
            if (file.isFile() && !file.canWrite()) {
                Utils.setReadOnly(file, false);
            }
        }
    }        
}
