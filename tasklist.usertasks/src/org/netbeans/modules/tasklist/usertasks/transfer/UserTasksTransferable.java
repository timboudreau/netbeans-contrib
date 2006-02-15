/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.netbeans.modules.tasklist.usertasks.*;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/** 
 * Transferable for user tasks.
 *
 * @author Tor Norbye
 * @author tl
 */
public final class UserTasksTransferable implements Transferable {
    /** Flavor for tasks on the clipboard */    
    public static final DataFlavor USER_TASKS_FLAVOR = new DataFlavor(
        DataFlavor.javaJVMLocalObjectMimeType, 
        NbBundle.getMessage(UserTasksTransferable.class, 
            "UserTasks")); // NOI18N

    private UserTask[] tasks;
    
    /** 
     * Construct a task transfer object 
     *
     * @param tasks dragged tasks
     */    
    public UserTasksTransferable(UserTask[] tasks) {
        this.tasks = tasks;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.stringFlavor) ||
                flavor.equals(USER_TASKS_FLAVOR);
    }

    public Object getTransferData(DataFlavor flavor) throws 
            UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.stringFlavor)) {
            StringBuffer sb = new StringBuffer();
            String lf = System.getProperty("line.delimiter"); // NOI18N
            for (int i = 0; i < tasks.length; i++) {
                if (i != 0)
                    sb.append(lf);
                sb.append(tasks[i].getSummary());
            }
            return sb.toString();
        } else if (flavor.equals(USER_TASKS_FLAVOR)) {
            return tasks;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
            DataFlavor.stringFlavor,
            USER_TASKS_FLAVOR
        };
    }
}
