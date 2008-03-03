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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.visualweb.removebindings;

import com.sun.rave.designtime.DesignBean;
import com.sun.rave.designtime.DesignContext;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.modules.visualweb.insync.UndoEvent;
import org.netbeans.modules.visualweb.insync.beans.BeansUnit;
import org.netbeans.modules.visualweb.insync.faces.FacesBean;
import org.netbeans.modules.visualweb.insync.faces.FacesPageUnit;
import org.netbeans.modules.visualweb.insync.java.JavaClass.UsageStatus;
import org.netbeans.modules.visualweb.insync.live.FacesDesignBean;
import org.netbeans.modules.visualweb.insync.live.LiveUnit;
import org.netbeans.modules.visualweb.insync.models.FacesModel;
import org.netbeans.modules.visualweb.insync.models.FacesModelSet;
import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Sandip V. Chitale
 */
public class RemoveUnusedBindingsAction extends AbstractAction implements ContextAwareAction {

    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new RemoveUnusedBindingsContextAwareAction(actionContext);
    }

    private static class RemoveUnusedBindingsContextAwareAction extends AbstractAction {
        private Project project;

        public RemoveUnusedBindingsContextAwareAction(Lookup actionContext) {
            super(NbBundle.getMessage(RemoveUnusedBindingsAction.class, "CTL_RemoveUnusedBindingsAction")); // NOI18N
            this.project = actionContext.lookup(Project.class);
            setEnabled(JsfProjectUtils.isJsfProject(project));
        }

        public void actionPerformed(ActionEvent e) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    FacesModelSet facesModelSet = FacesModelSet.getInstance(project);
                    if (facesModelSet != null) {
                        DesignContext[] designContexts = facesModelSet.getDesignContexts();
                        OutputWriter out = IOProvider.getDefault().getStdOut();
                        for (DesignContext designContext : designContexts) {
                            LiveUnit liveUnit = (LiveUnit) designContext;
                            BeansUnit beansUnit = liveUnit.getBeansUnit();
                            if (!(beansUnit instanceof FacesPageUnit)) {
                                continue;
                            }
                            FacesModel facesModel = liveUnit.getModel();
                            if (facesModel == null) {
                                continue;
                            }
                            out.println(NbBundle.getMessage(RemoveUnusedBindingsAction.class,
                                    "MSG_ProcessingFacesModel",  // NOI18N
                                    facesModel.getFile().getPath()));
                            if (facesModel.isBusted()) {
                                out.println(NbBundle.getMessage(RemoveUnusedBindingsAction.class,
                                        "MSG_SkippingFacesModel",  // NOI18N
                                        facesModel.getFile().getPath()));
                                continue;
                            }
                            UndoEvent undo = null;
                            try {
                                undo = facesModel.writeLock(
                                        NbBundle.getMessage(RemoveUnusedBindingsAction.class,
                                        "CTL_RemoveUnusedBindingsAction"));  // NOI18N
                                FacesPageUnit facesPageUnit = (FacesPageUnit) beansUnit;
                                DesignBean[] beans = liveUnit.getBeans();
                                for (DesignBean bean : beans) {
                                    if (bean instanceof FacesDesignBean) {
                                        FacesDesignBean facesDesignBean = (FacesDesignBean) bean;
                                        FacesBean.UsageInfo usageInfo = facesDesignBean.getUsageInfo();
                                        if (usageInfo.getUsageStatus() == UsageStatus.NOT_USED) {
                                            facesDesignBean.removeBinding();
                                            out.println(NbBundle.getMessage(RemoveUnusedBindingsAction.class,
                                                "MSG_RemovedBindingsForBean",  // NOI18N
                                                facesDesignBean.getInstanceName()));
                                        }
                                    }
                                }
                            } finally {
                                facesModel.writeUnlock(undo);
                            }
                            out.println(NbBundle.getMessage(RemoveUnusedBindingsAction.class,
                                    "MSG_ProcessingFacesModelDone", // NOI18N
                                    facesModel.getFile().getPath()));
                        }
                    }
                }
            });
        }
    }
}
