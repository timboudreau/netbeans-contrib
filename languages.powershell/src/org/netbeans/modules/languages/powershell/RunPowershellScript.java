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

package org.netbeans.modules.languages.powershell;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.openide.ErrorManager;
import org.openide.awt.Actions;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 * This actions runs the powershell script being edited currently.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class RunPowershellScript extends AbstractAction implements Presenter.Toolbar, ActionListener {
    
    private static final String ICON_PATH = "org/netbeans/modules/languages/powershell/powershell.gif";
    private static final Icon ICON = new ImageIcon(Utilities.loadImage(ICON_PATH));
    
    public RunPowershellScript() {
        super(NbBundle.getMessage(RunPowershellScript.class, "CTL_RunPowershellScript"));
        putValue(SMALL_ICON, ICON);
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        Lookup lookup = Utilities.actionsGlobalContext();
        DataObject dataObject = lookup.lookup(DataObject.class);
        if (dataObject != null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject != null && fileObject.getMIMEType().equals("text/x-ps1")){
                File file = FileUtil.toFile(fileObject);
                if (file != null) {                    
                    try {
                        NbProcessDescriptor nbProcessDescriptor = new NbProcessDescriptor("cmd.exe",
                                "/c start C:\\WINDOWS\\system32\\windowspowershell\\v1.0\\powershell.exe -NoLogo -Command \". '" + file.getAbsolutePath() + "'\"");
                        Process process = nbProcessDescriptor.exec();
                        process.waitFor();
                    } catch (IOException ex) {
                        ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                    } catch (InterruptedException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        }
    }
    
    public Component getToolbarPresenter() {
        JButton button = new JButton();
        Actions.connect(button, this);
        return button;
    }
}

