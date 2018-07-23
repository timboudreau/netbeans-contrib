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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.make2netbeans.test;

import java.io.File;
import java.io.IOException;
import org.netbeans.modules.cnd.make2netbeans.api.SubProjectGenerator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * The class tests DividerImpl.java by determing subprojects of given project.
 * To start a test select Tools->Divide Project
 * @author Andrey Gubichev
 */
public final class TestAction extends CallableSystemAction {

    /**
     * Actually perform the test.
     */
    public void performAction() {
        TestWizardAction t = new TestWizardAction();
        t.performAction();
        String projectFolder = (String) t.getValue("projectFolder"); // NOI18N
        String makefilePath = (String) t.getValue("makefilePath"); // NOI18N
        String buildCommand = (String) t.getValue("buildCommand"); // NOI18N
        String cleanCommand = (String) t.getValue("cleanCommand"); // NOI18N
        String output = (String) t.getValue("output"); // NOI18N
        String prefix = (String) t.getValue("prefix"); // NOI18N
        int depth = ((Integer)t.getValue("depth")).intValue(); // NOI18N
        boolean dwarf = ((Boolean)t.getValue("dwarf")).booleanValue(); // NOI18N
        if (projectFolder != null && makefilePath != null) {
            File makefile = new File(makefilePath);
            makefilePath = makefile.getPath();
            File project = new File(projectFolder);
            projectFolder = project.getPath();
            String mainWorkingDir = makefilePath.substring(0, makefilePath.lastIndexOf(File.separator));
            SubProjectGenerator p = Lookup.getDefault().lookup(SubProjectGenerator.class);
            p.init(projectFolder, mainWorkingDir, makefilePath);
            p.setBuildCommand(buildCommand);
            p.setCleanCommand(cleanCommand);
            p.setOutput(output);
            p.setPrefixName(prefix);
            p.setDepthLevel(depth);
            p.setInvokeDwarfProvider(dwarf);
            try {
                p.generate();
                NotifyDescriptor d = new NotifyDescriptor.Message("The project has been created. It is located in " + projectFolder + ".", NotifyDescriptor.INFORMATION_MESSAGE); // NOI18N
                DialogDisplayer.getDefault().notify(d);
            } catch (IllegalArgumentException e) {
                NotifyDescriptor d = new NotifyDescriptor.Message("There is already a project in specified folder", NotifyDescriptor.INFORMATION_MESSAGE); // NOI18N
                DialogDisplayer.getDefault().notify(d);
            } catch (IOException e) {
                System.out.println("TestAction.performAction: Exception: " + e); // NOI18N
            }
        }
    }

    /**
     *
     * @return a human presentable name of the action
     */
    public String getName() {
        return NbBundle.getMessage(TestAction.class, "CTL_TestAction");
    }

    /**
     * initialize
     */
    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    /**
     *
     * @return  a help context for the action
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     *
     * @return  true, if this action should be performed asynchronously in a private thread.
     */
    @Override
    protected boolean asynchronous() {
        return false;
    }
}