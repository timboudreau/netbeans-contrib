/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s): */
package org.netbeans.modules.htmlproject;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Tim Boudreau
 */
public class ColocatedHtmlFileAction extends AbstractAction {
    private final File f;
    private final FileObject projDir;
    private final Kids kids;
    public ColocatedHtmlFileAction(File f, FileObject projDir, Kids kids) {
        this.f = f;
        this.projDir = projDir;
        this.kids = kids;
        assert f.isFile();
        //well, this is clunky
        putValue (NAME, "New HTML File Beside This");
    }

    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor.InputLine line = new NotifyDescriptor.InputLine(
                "Create new HTML file in " + f.getName(), "New HTML File");
        if (DialogDisplayer.getDefault().notify(line) == line.OK_OPTION) {
            String txt = line.getInputText();
            if (txt.contains(File.separator) || txt.contains(File.pathSeparator)) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(File.separatorChar +
                        " and " + File.pathSeparatorChar + " are not allowed in " +
                        " file names");
                DialogDisplayer.getDefault().notify(msg);
            } else {
                if (!txt.toUpperCase(Locale.ENGLISH).endsWith(".HTML") && !txt.toUpperCase(Locale.ENGLISH).endsWith(".HTM")) {
                    txt = txt + ".html";
                }
                final File nue = new File (f.getParentFile(), line.getInputText() + ".html");
                try {
                    if (!nue.createNewFile()) {
                        StatusDisplayer.getDefault().setStatusText("Could not " +
                                "create " + nue.getPath());
                    }
                    projDir.refresh();
                    FileObject fob = FileUtil.toFileObject (nue);
                    OpenCookie ck = (OpenCookie) DataObject.find (
                            fob).getNodeDelegate().getCookie(OpenCookie.class);
                    
                    if (ck != null) {
                        ck.open();
                    } else {
                        StatusDisplayer.getDefault().setStatusText("Could not" +
                                " open " + nue.getPath());
                    }
                    kids.upd(true);
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify (ErrorManager.USER, ioe);
                }
            }
        }
    }
}
