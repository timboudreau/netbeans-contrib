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
package org.netbeans.modules.latex.editor.completion.latex.help;
import java.awt.Dialog;
import org.netbeans.modules.latex.editor.completion.latex.help.DownloadHelpPanelImpl;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author Jan Lahoda
 */
public final class InstallHelp {

    public static void installHelp() {
        DownloadHelpPanelImpl panel = new DownloadHelpPanelImpl();

        DialogDescriptor dd = new DialogDescriptor(panel, "Install LaTeX Help");

        Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        
        d.setVisible(true);
        
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            PreprocessHelp.createHelpJar(panel.getHelpDirectory());
        }
    }
    
}
