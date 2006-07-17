/*
The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
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
    private final String trimmedPath;
    private final File f;
    private final FileObject projDir;
    private final HtmlLogicalView.Kids kids;
    /** Creates a new instance of ColocatedHtmlFileAction */
    public ColocatedHtmlFileAction(File f, String trimmedPath, FileObject projDir, HtmlLogicalView.Kids kids) {
        this.f = f;
        this.trimmedPath = trimmedPath;
        this.projDir = projDir;
        this.kids = kids;
        assert f.isFile();
        //well, this is clunky
        putValue (NAME, "New HTML File Beside This");
    }

    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor.InputLine line = new NotifyDescriptor.InputLine(
                "Create new HTML file in " + trimmedPath, "New HTML File");
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
