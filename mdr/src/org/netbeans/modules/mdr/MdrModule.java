/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
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