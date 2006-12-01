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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.ant.moduleinfotask.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JFileChooser;
import org.netbeans.modules.ant.moduleinfotask.ModuleInfoGenerator;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.Dependency;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInfo;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;


/**
 * This action invokes the module dependencies crossreference generator.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ModuleInfoAction extends CallableSystemAction {
    private static ResourceBundle bundle = NbBundle.getBundle(ModuleInfoAction.class);
    
    public void performAction() {        
        JFileChooser fd = new JFileChooser();
        fd.setSelectedFile(new File(bundle.getString("DEFAULT_HTML_OUTPUT_FILE_NAME")));
        
        switch (fd.showSaveDialog(WindowManager.getDefault().getMainWindow())) {
            case JFileChooser.APPROVE_OPTION:
                ModuleInfoGenerator.generateHTML(fd.getSelectedFile());
                break;
        }
    }
    
    public String getName() {
        return bundle.getString("LBL_Generate_Module_Info");
    }
    
    public String iconResource() {
        return "org/netbeans/modules/ant/moduleinfotask/ui/module.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }    
    
    protected boolean asynchronous() {
        return false;
    }            
}
