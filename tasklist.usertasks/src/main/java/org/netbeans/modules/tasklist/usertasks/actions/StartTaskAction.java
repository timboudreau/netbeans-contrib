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

package org.netbeans.modules.tasklist.usertasks.actions;

import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.model.StartedUserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListEvent;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Starts a task
 *
 * @author tl
 */
public class StartTaskAction extends UTViewAction implements 
        PropertyChangeListener, ObjectListListener {
    private UserTask ut;
    
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     */
    public StartTaskAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(StartTaskAction.class, 
                "StartTask")); // NOI18N
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/actions/" + // NOI18N
                "startTask.gif"))); // NOI18N
    }
    
    public void actionPerformed(ActionEvent e) {
        if (StartedUserTask.getInstance().getStarted() != null)
            StartedUserTask.getInstance().start(null);
        if (ut.getOwner().trim().length() == 0)
            ut.setOwner(System.getProperty("user.name"));
        ut.start();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (ut != null) {
            ut.removePropertyChangeListener(this);
            ut.getDependencies().removeListener(this);
            ut = null;
        }
        
        TreePath[] paths = utv.getTreeTable().getSelectedPaths();
        if (paths.length == 1) {
            Object last = paths[0].getLastPathComponent();
            if (last instanceof UTBasicTreeTableNode) {
                UserTask ut = ((UTBasicTreeTableNode) last).getUserTask();
                this.ut = ut;
                this.ut.addPropertyChangeListener(this);
                this.ut.getDependencies().addListener(this);
            }
        }
        updateEnabled();
    }

    private void updateEnabled() {
        setEnabled(ut != null && ut.isStartable() && !ut.isStarted());
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "started" || 
                evt.getPropertyName() == "spentTimeComputed" ||
                evt.getPropertyName() == "progress") {
            setEnabled(ut.isStartable() && !ut.isStarted());
        }
    }

    public void listChanged(ObjectListEvent e) {
        updateEnabled();
    }
}
