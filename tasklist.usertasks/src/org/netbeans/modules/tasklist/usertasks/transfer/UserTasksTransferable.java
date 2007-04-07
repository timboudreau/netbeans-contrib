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
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.netbeans.modules.tasklist.usertasks.*;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.util.io.ReaderInputStream;

/** 
 * Transferable for user tasks.
 *
 * @author Tor Norbye
 * @author tl
 */
public final class UserTasksTransferable implements Transferable, Serializable {
    private static final long serialVersionUID = 1;
    
    /** Flavor for tasks on the clipboard */    
    public static final DataFlavor USER_TASKS_FLAVOR = new DataFlavor(
        DataFlavor.javaJVMLocalObjectMimeType + "; class=" + 
        java.util.ArrayList.class.getName(), 
        NbBundle.getMessage(UserTasksTransferable.class, 
            "UserTasks")); // NOI18N

    private transient UserTask[] tasks;
    
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
                flavor.equals(USER_TASKS_FLAVOR) || 
                flavor.equals(DataFlavor.getTextPlainUnicodeFlavor());
    }

    public Object getTransferData(DataFlavor flavor) throws 
            UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.stringFlavor)) {
            return toString(tasks);
        } else if (flavor.equals(USER_TASKS_FLAVOR)) {
            ArrayList al = new ArrayList();
            al.addAll(Arrays.asList(tasks));
            return al;
        } else if (flavor.equals(DataFlavor.getTextPlainUnicodeFlavor())) {
            String charset = DataFlavor.getTextPlainUnicodeFlavor().
                    getParameter("charset");
            if (charset == null)
                return new ReaderInputStream(
                        new StringReader(toString(tasks)));
            else
                return new ReaderInputStream(
                        new StringReader(toString(tasks)), charset);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    private String toString(UserTask[] tasks) {
        StringBuilder sb = new StringBuilder();
        String lf = System.getProperty("line.separator"); // NOI18N
        for (int i = 0; i < tasks.length; i++) {
            if (i != 0)
                sb.append(lf);
            sb.append("- ").append(tasks[i].getSummary()); // NOI18N
        }
        return sb.toString();
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
            DataFlavor.stringFlavor,
            USER_TASKS_FLAVOR
        };
    }
}
