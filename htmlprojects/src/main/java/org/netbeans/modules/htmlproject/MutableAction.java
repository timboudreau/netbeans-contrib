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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

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
    
    private static final int DELETE_PROJECT = 1;
    private static final int DELETE_PROJECT_AND_FOLDER = 2;
    private static final int DO_NOTHING = 0;
    public int confirmDelete() {
        String file = FileUtil.toFile(proj.getProjectDirectory()).getPath();
        String project = proj.getDisplayName();
        
        JPanel jp = new JPanel();
        jp.setLayout (new GridLayout (2, 1));
        final JLabel lbl = new JLabel (NbBundle.getMessage (MutableAction.class,
                "LBL_ConfirmDelete", project)); //NOI18N
        final JCheckBox box = new JCheckBox(NbBundle.getMessage (MutableAction.class,
                "LBL_AlsoDeleteSources", file)); //NOI18N
        jp.add (lbl);
        jp.add (box);
        boolean confirmed;
        boolean mac = (Utilities.getOperatingSystem() & Utilities.OS_MAC) != 0;
        if (mac) {
            String del = NbBundle.getMessage(MutableAction.class, "LBL_Delete");//NOI18N
            String nodel = NbBundle.getMessage(MutableAction.class, "LBL_DontDelete");//NOI18N
            jp.setBorder (BorderFactory.createEmptyBorder (5,5,5,5));
            confirmed = del.equals(DialogDisplayer.getDefault().notify(
                    new DialogDescriptor (jp,
                    NbBundle.getMessage(MutableAction.class, "TTL_ConfirmDelete" //NOI18N
                    ), true, new Object[] {nodel, del}, del, DialogDescriptor.BOTTOM_ALIGN,
                    HelpCtx.DEFAULT_HELP, null)));
        } else {
            confirmed = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(jp, 
                NbBundle.getMessage(MutableAction.class, "TTL_ConfirmDelete"), //NOI18N
                NotifyDescriptor.YES_NO_OPTION)) == NotifyDescriptor.YES_OPTION;
        }
        if (confirmed) {
            return box.isSelected() ? DELETE_PROJECT_AND_FOLDER :
                DELETE_PROJECT;
        } else {
            return DO_NOTHING;
        }
    }

    public void actionPerformed(ActionEvent ae) {
        switch(ix) {
            case CLOSE:
                OpenProjects.getDefault().close(
                        new Project[] { proj });
                break;
            case DELETE:
                try {
                    int val = confirmDelete();
                    if (val != DO_NOTHING) {
                        OpenProjects.getDefault().close(new Project[] { proj });
                    }
                    if (val == DELETE_PROJECT_AND_FOLDER) {
                        DataObject.find(proj.getProjectDirectory()).delete();
                    }
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