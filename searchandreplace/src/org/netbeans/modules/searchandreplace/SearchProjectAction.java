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

package org.netbeans.modules.searchandreplace;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.netbeans.api.project.Project;

import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Timothy Boudreau
 */
public class SearchProjectAction extends AbstractAction implements ContextAwareAction {
    public SearchProjectAction() {
        putValue (Action.NAME, NbBundle.getMessage (getClass(),
                "LBL_SearchProject")); //NOI18N
    }

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException();
    }

    public Action createContextAwareInstance(final Lookup actionContext) {
        return new Wrapper(actionContext);
    }

    private static final class Wrapper extends AbstractAction implements Runnable {
        private final Lookup actionContext;
        Wrapper (Lookup actionContext) {
            this.actionContext = actionContext;
            putValue (Action.NAME, NbBundle.getMessage (getClass(),
                    "LBL_SearchProject")); //NOI18N
        }

        public boolean isEnabled() {
            return !getProjects().isEmpty();
        }

        public void actionPerformed(ActionEvent e) {
            //Must not be run in EQ
            RequestProcessor.getDefault().post(this);
        }

        public void run() {
            SearchAndReplaceAction act = (SearchAndReplaceAction)
                SystemAction.get(SearchAndReplaceAction.class);

            act.performAction(new Lookup[] { Lookups.fixed( getDataObjects() ) });
        }

        private Collection getProjects() {
            Collection nds = actionContext.lookup(
                    new Lookup.Template(Node.class)).allInstances();

            List projects = new ArrayList(nds.size());
            Lookup.Template tpl = new Lookup.Template(Project.class);
            for (Iterator i = nds.iterator(); i.hasNext();) {
                Node nd = (Node) i.next();
                projects.addAll(nd.getLookup().lookup(tpl).allInstances());
            }
            return projects;
        }

        private DataObject[] getDataObjects() {
            Collection projs = getProjects();

            List l = new ArrayList(projs.size());
            for (Iterator i = projs.iterator(); i.hasNext();) {
                Project p = (Project) i.next();
                FileObject root = p.getProjectDirectory();
                if (root.isVirtual()) {
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(SearchProjectAction.class,
                            "MSG_NO_VIRTUAL")); //NOI18N
                    continue;
                }
                DataObject obj;
                try {
                    obj = DataObject.find(root);
                    l.add (obj);
                } catch (DataObjectNotFoundException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }

            DataObject[] objs = (DataObject[]) l.toArray(
                    new DataObject[l.size()]);
            return objs;
        }
    }
}
