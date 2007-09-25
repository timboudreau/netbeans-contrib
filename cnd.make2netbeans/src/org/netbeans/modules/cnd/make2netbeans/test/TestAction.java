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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
        String projectFolder = (String) t.getValue("projectFolder");
        String makefilePath = (String) t.getValue("makefilePath");
        String buildCommand = (String) t.getValue("buildCommand");
        String cleanCommand = (String) t.getValue("cleanCommand");
        String output = (String) t.getValue("output");
        String prefix = (String) t.getValue("prefix");
        int depth = ((Integer)t.getValue("depth")).intValue();
        boolean dwarf = ((Boolean)t.getValue("dwarf")).booleanValue();
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
                NotifyDescriptor d = new NotifyDescriptor.Message("The project has been created. It is located in " + projectFolder + ".", NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            } catch (IllegalArgumentException e) {
                NotifyDescriptor d = new NotifyDescriptor.Message("There is already a project in specified folder", NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            } catch (IOException e) {
                System.out.println("TestAction.performAction: Exception: " + e);
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
        putValue("noIconInMenu", Boolean.TRUE);
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