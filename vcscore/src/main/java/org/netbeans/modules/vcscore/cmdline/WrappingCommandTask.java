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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.vcscore.cmdline;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.spi.vcs.commands.CommandTaskSupport;

/**
 * This class ias a wrapper of several <code>CommandTask</code>s.
 *
 * @author  Martin Entlicher
 */
public class WrappingCommandTask extends CommandTaskSupport {

    private UserCommandTask[] tasks;

    /** Creates a new instance of WrappingCommandTask */
    public WrappingCommandTask(UserCommandSupport cmdSupport, VcsDescribedCommand cmd) {//, List files) {
        super(cmdSupport, cmd);
        createSubTasks(cmdSupport, cmd);//, files);
    }
    
    private void createSubTasks(UserCommandSupport cmdSupport, VcsDescribedCommand cmd) {
        List tasksList = new ArrayList();
        for (VcsDescribedCommand c = cmd;
             c != null;
             c = (VcsDescribedCommand) c.getNextCommand()) {
            
            tasksList.add(new UserCommandTask(cmdSupport, c));
        }
        tasks = (UserCommandTask[]) tasksList.toArray(new UserCommandTask[tasksList.size()]);
    }
    
    /**
     * Run all wrapped tasks.
     */
    public void runTasks() {
        for (int i = 0; i < tasks.length; i++) {
            tasks[i].run();
        }
    }
    
    /**
     * Get all wrapped tasks.
     */
    public UserCommandTask[] getTasks() {
        UserCommandTask[] utasks = new UserCommandTask[tasks.length];
        System.arraycopy(tasks, 0, utasks, 0, tasks.length);
        return utasks;
    }
    
    /**
     * Wait for all wrapped tasks to finish.
     * @return the status of wrapped tasks. If one fails, this also fail.
     */
    public int waitForTasks() {
        int status = STATUS_SUCCEEDED;
        try {
            for (int i = 0; i < tasks.length; i++) {
                tasks[i].waitFinished(0);
                if (STATUS_SUCCEEDED != tasks[i].getExitStatus()) {
                    status = tasks[i].getExitStatus();
                }
            }
        } catch (InterruptedException intex) {
            for (int i = 0; i < tasks.length; i++) {
                if (!tasks[i].isFinished()) tasks[i].stop();
            }
            status = STATUS_INTERRUPTED;
        }
        return status;
    }
    
}
