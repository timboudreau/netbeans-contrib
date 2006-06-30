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
package org.netbeans.modules.mdr;

import org.netbeans.mdr.persistence.btreeimpl.btreestorage.MDRCache;
import org.openide.modules.ModuleInstall;
import org.netbeans.api.mdr.MDRManager;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;

/** Module installation class for MDR module
 *
 * @author Petr Hrebejk
 */
public class MdrModule extends ModuleInstall {

    /** Module was uninstalled.
     */
    public void uninstalled () {
        close();
        MDRManagerImpl.uninstall(); // cleanup
    }

    /** Module is being closed.
     * @return True if the close is O.K.
     */
    public void close () {
        final MDRManagerImpl manager = (MDRManagerImpl) MDRManager.getDefault();
        Task task = RequestProcessor.getDefault().post(new Runnable () {
            public void run() {
                manager.shutdownAll();
            }
        });
        manager.preShutdownAll();
        task.waitFinished();
    }
}