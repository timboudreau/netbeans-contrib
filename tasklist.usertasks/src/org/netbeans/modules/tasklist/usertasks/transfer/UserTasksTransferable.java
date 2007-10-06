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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
