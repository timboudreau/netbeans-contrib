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
import java.net.MalformedURLException;
import java.util.Locale;
import javax.swing.AbstractAction;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

class MutableAction extends AbstractAction {
    static final int CLOSE = 2;
    static final int DELETE = 1;
    static final int VIEW = 0;
    static final int ZIP = 3;
    static final int CLEAN = 4;
    static final int PROPS = 5;
    int ix;
    private HtmlProject proj;
    public MutableAction(int ix, HtmlProject proj) {
        this.ix = ix;
        this.proj = proj;
        String nm;
        switch(ix) {
            case CLOSE:
                // XXX bad, should use standard action
                nm = "Close Project";
                break;
            case DELETE:
                // XXX bad, should use standard action
                nm = "Delete Project";
                break;
            case VIEW:
                nm = "View";
                break;
            case ZIP :
                nm = "Zip Project (Build)";
                break;
            case CLEAN :
                // XXX bad, should use standard action
                nm = "Clean Project";
                break;
            case PROPS :
                // XXX bad, should use standard action
                nm = "Properties";
                break;
            default:
                throw new IllegalArgumentException();
        }
        putValue(NAME, nm);
    }

    public void actionPerformed(ActionEvent ae) {
        switch(ix) {
            case CLOSE:
                OpenProjects.getDefault().close(
                        new Project[] { proj });
                break;
            case DELETE:
                try {
                    OpenProjects.getDefault().close(new Project[] { proj });
                    DataObject.find(proj.getProjectDirectory()).delete();
                }  catch (DataObjectNotFoundException ex) {
                    ErrorManager.getDefault().notify(ex);
                }  catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
                break;
            case VIEW:
                proj.invokeAction(proj.COMMAND_RUN, proj.getLookup());
                break;
            case ZIP :
                proj.invokeAction(proj.COMMAND_BUILD, proj.getLookup());
                break;
            case CLEAN :
                proj.invokeAction(proj.COMMAND_CLEAN, proj.getLookup());
                break;
            case PROPS :
                ProjectPropertiesDlg.showDialog(proj);
                break;
            default:
                assert false;
        }
    }

    private File findFirstIndexFile() {
        FileObject fob = proj.getProjectDirectory();
        File f = FileUtil.toFile(fob);
        return search(f);
    }

    private File search(File fld) {
        File[] f = fld.listFiles();
        for (int i = 0; i < f.length; i++) {
            String s = f[i].getName().toUpperCase(Locale.ENGLISH);
            if ("INDEX.HTML".equals(s) || "INDEX.HTM".equals(s)) { //NOI18N
                return f[i];
            } else if (f[i].isDirectory()) {
                File result = search(f[i]);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}