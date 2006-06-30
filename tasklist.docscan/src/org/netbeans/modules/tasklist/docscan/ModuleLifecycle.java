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

package org.netbeans.modules.tasklist.docscan;

import org.openide.modules.ModuleInstall;
import org.openide.windows.TopComponent;
import org.openide.ErrorManager;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;


/**
 * Handles module lifecycle events.
 *
 * @author Petr Kuzel
 */
public final class ModuleLifecycle extends ModuleInstall {

    private static final long serialVersionUID = 1;

    public void uninstalled() {
        super.uninstalled();

        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    TopComponent tc = SourceTasksView.getTaskListView(SourceTasksView.CATEGORY);
                    if (tc != null) {
                        tc.close();
                    }
                }
            });
        } catch (InterruptedException e) {
            ErrorManager.getDefault().notify(e);
        } catch (InvocationTargetException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}
