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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editorthemes.export;

import beans2nbm.gen.FileModel;
import beans2nbm.gen.ModuleInfoModel;
import beans2nbm.gen.ModuleModel;
import beans2nbm.gen.NbmFileModel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tim Boudreau
 */
public class ExportAction extends AbstractAction {
    public enum Kind {
        FONTS_AND_COLORS, KEYBINDINGS,
    }

    private Kind kind;
    private ExportAction(Kind kind) {
        this.kind = kind;
        System.err.println("CREATE " + kind);
        putValue (NAME, NbBundle.getMessage(ExportAction.class, kind == 
                Kind.FONTS_AND_COLORS ? "LBL_ACTION" : //NOI18N
                    "LBL_KEYBINDINGS_ACTION")); //NOI18N
    }
    
    public static Action createExportColorThemesAction() {
        return new ExportAction (Kind.FONTS_AND_COLORS);
    }
    
    public static Action createExportKeyboardProfilesAction() {
        return new ExportAction (Kind.KEYBINDINGS);
    }

    public void actionPerformed(ActionEvent e) {
        System.err.println("INVOKE " + kind);
        final SelectThemesPanel pnl = new SelectThemesPanel(kind == 
                Kind.FONTS_AND_COLORS ?
                getThemeNames() : getKeyboardProfileNames());
        final DialogDescriptor dd = new DialogDescriptor (pnl, 
                getValue(NAME).toString());
        dd.setValid(false);
        pnl.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dd.setValid(pnl.isValidData());
            }
        });
        if (DialogDisplayer.getDefault().notify(dd).equals(
                DialogDescriptor.OK_OPTION)) {
            Collection <String> themes = pnl.getSelectedThemes();
            if (themes.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            ProgressHandle handle = ProgressHandleFactory.createHandle(
                    NbBundle.getMessage(ExportAction.class,
                "MSG_PROGRESS")); //NOI18N
            File outfile = pnl.getOutfile();
            if (outfile.exists()) {
                String ttl = NbBundle.getMessage(ExportAction.class, 
                        "TTL_FILE_EXISTS"); //NOI18N
                String msg = NbBundle.getMessage(ExportAction.class,
                        "MSG_FILE_EXISTS", outfile.getPath()); //NOI18N
                DialogDescriptor dlg = new DialogDescriptor(msg, ttl);
                Object dlgResult = DialogDisplayer.getDefault().notify(dlg);
                if (dlgResult != DialogDescriptor.OK_OPTION) {
                    return;
                }
            }
            R r = new R(themes, handle, pnl.getAuthor(), pnl.getDisplayName(), 
                    pnl.getCodeName(), outfile, pnl.getVersion(), kind);
            RequestProcessor.getDefault().post(r);
        }
    }
    
    private Iterable <String> getThemeNames() {
        return getNames ("Editors/FontsColors"); //NOI18N
    }
    
    private Iterable <String> getKeyboardProfileNames() {
        return getNames ("Editors/Keybindings"); //NOI18N
    }
        
    private Iterable <String> getNames(String folder) {
        FileObject fob = Repository.getDefault().getDefaultFileSystem().
                getRoot().getFileObject(folder);
        FileObject[] kids = fob.getChildren();
        List <String> names = new ArrayList<String>();
        for (FileObject f : kids) {
            if (f.isFolder()) {
                names.add (f.getName());
            }
        }
        return names;
    }
    
    private static class R implements Runnable {
        private final Collection <String> themes;
        private final ProgressHandle handle;
        private final Kind kind;
        private final String author;
        private final String codeName;
        private final File outfile;
        private final String displayName;
        private final String version;

        public R(Collection<String> themes, ProgressHandle handle, String author,
                String displayName, String codeName, File outfile, 
                String version, Kind kind) {
            this.themes = themes;
            this.handle = handle;
            this.author = author;
            this.codeName = codeName;
            this.outfile = outfile;
            this.displayName = displayName;
            this.version = version;
            this.kind = kind;
        }

        public void run() {
            handle.start(themes.size());
            int ix = 0;
            FileObject fob = Repository.getDefault().getDefaultFileSystem().
                    getRoot().getFileObject(
                    kind == Kind.FONTS_AND_COLORS ? 
                        "Editors/FontsColors" : "Editors/Keybindings");
            String codeNameSlashes = codeName.replace(".", "/"); //NOI18N
            XmlFsFileModel mdl = new XmlFsFileModel(codeNameSlashes);
            for (String theme : themes) {
                handle.progress(ix);
                FileObject f = fob.getFileObject(theme);
                mdl.add(f);
                List <FileObject> related = scanForPerMimeThemeDefs (f);
                for (FileObject r : related) {
                    mdl.add (r);
                }
                ix++;
            }
            try {
                List <FileModel> fls = mdl.getEmbeddedFiles();
                String codeNameDashes = codeName.replace(".", "-"); //NOI18N
                String moduleJarName = codeNameDashes + ".jar"; //NOI18N
                NbmFileModel nbm = new NbmFileModel (outfile.getPath());
                ModuleModel module = new ModuleModel ("netbeans/modules/" + //NOI18N
                        moduleJarName, codeName, "", version, displayName, 
                        "1.4"); //NOI18N
                module.setCategory ("Editor"); //NOI18N
                ModuleInfoModel infoXml = new ModuleInfoModel (module, 
                        "http://www.netbeans.org", author, getLicenseText()); //NOI18N
                nbm.add(module);
                nbm.add(infoXml);
                module.addFileEntry(mdl);
                for (FileModel fm : fls) {
                    module.addFileEntry(fm);
                }
                FileOutputStream out = new FileOutputStream (outfile);
                try {
                    nbm.write(out);
                } finally {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                handle.finish();
            }
        }

        private static String getLicenseText() throws IOException {
            InputStream in = ExportAction.class.getResourceAsStream(
                    "license.txt"); //NOI18N
            ByteArrayOutputStream out = new ByteArrayOutputStream(17614);
            FileUtil.copy (in, out);
            in.close();
            return new String (out.toByteArray(), "UTF-8"); //NOI18N
        }

        private List<FileObject> scanForPerMimeThemeDefs(FileObject f) {
            String nm = f.getName();
            FileObject fob = Repository.getDefault().getDefaultFileSystem().
                    getRoot().getFileObject("Editors"); //NOI18N
            List <FileObject> result = new ArrayList <FileObject> ();
            scan (fob, nm, result);
            return result;
        }

        private void scan(FileObject fob, String nm, List<FileObject> result) {
            if (fob.getName().equals(nm) && fob.isFolder()) {
                result.add (fob);
            } else if (fob.isFolder()) {
                FileObject[] kids = fob.getChildren();
                for (FileObject k : kids) {
                    scan (k, nm, result);
                }
            }
        }
    }
}
