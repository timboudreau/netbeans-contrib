/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.contrib.testng.actions;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.contrib.testng.api.TestNGSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ConvertAction extends CookieAction {

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (super.enable(activatedNodes)) {
            DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
            Project p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            if (TestNGSupport.isProjectSupported(p)) {
                return TestNGSupport.findTestNGSupport(p).isProjectSupported(p);
            }
        }
        return false;
    }


    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        //XXX - should check if a file to be converted is a JUnit test here...
        NotifyDescriptor nd = new NotifyDescriptor(
                NbBundle.getMessage(ConvertAction.class, "MSG_Overwrite_tests"),
                NbBundle.getMessage(ConvertAction.class, "TLT_Overwrite"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.WARNING_MESSAGE,
                null,
                NotifyDescriptor.OK_OPTION);
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
            FileObject fo = dataObject.getPrimaryFile();
            TestNGSupport.findTestNGSupport(FileOwnerQuery.getOwner(fo)).configureProject(fo);
            fo = FileOwnerQuery.getOwner(fo).getProjectDirectory();
            File f = FileUtil.toFile(fo);
            try {
                ActionUtils.runTarget(
                        fo.getFileObject("build.xml"),
                        new String[] {"convert"},
                        null);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
            FileUtil.refreshFor(f);
        }

    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(ConvertAction.class, "CTL_ConvertAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

