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

package org.netbeans.modules.languages.powershell;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseAction;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.ErrorManager;
import org.openide.awt.Actions;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 * This actions runs the powershell script being edited currently.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class RunPowershellScript extends BaseAction implements Presenter.Toolbar, ActionListener {

    private static final String ICON_PATH = "org/netbeans/modules/languages/powershell/powershell.gif";
    private static final Icon ICON = new ImageIcon(Utilities.loadImage(ICON_PATH));

    public RunPowershellScript() {
        super(NbBundle.getMessage(RunPowershellScript.class, "CTL_RunPowershellScript"));
        putValue(SMALL_ICON, ICON);
    }

    public void actionPerformed(ActionEvent actionEvent, JTextComponent textComponent) {
        FileObject fileObject = NbEditorUtilities.getFileObject(textComponent.getDocument());
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

    public Component getToolbarPresenter() {
        JButton button = new JButton();
        Actions.connect(button, this);
        return button;
    }

}

