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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
            String lf = System.getProperty("line.separator"); // NOI18N
            for (int i = 0; i < tasks.length; i++) {
                if (i != 0)
                    sb.append(lf);
                sb.append("- ").append(tasks[i].getSummary()); // NOI18N
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
