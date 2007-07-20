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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editorthemes.export;

import beans2nbm.gen.FileModel;
import beans2nbm.gen.ModuleInfoModel;
import beans2nbm.gen.ModuleModel;
import beans2nbm.gen.NbmFileModel;
import com.sun.java_cup.internal.version;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tim Boudreau
 */
public class ExportAction extends AbstractAction {

    public ExportAction() {
        putValue (NAME, NbBundle.getMessage(ExportAction.class, "LBL_ACTION")); //NOI18N
    }

    public void actionPerformed(ActionEvent e) {
        final SelectThemesPanel pnl = new SelectThemesPanel(getThemeNames());
        final DialogDescriptor dd = new DialogDescriptor (pnl, getValue(NAME).toString());
        dd.setValid(false);
        pnl.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dd.setValid(pnl.isValidData());
            }
        });
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION)) {
            Collection <String> themes = pnl.getSelectedThemes();
            if (themes.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(ExportAction.class,
                "MSG_PROGRESS")); //NOI18N
            File outfile = pnl.getOutfile();
            if (outfile.exists()) {
                String ttl = NbBundle.getMessage(ExportAction.class, 
                        "TTL_FILE_EXISTS");
                String msg = NbBundle.getMessage(ExportAction.class,
                        "MSG_FILE_EXISTS", outfile.getPath());
                DialogDescriptor dlg = new DialogDescriptor(msg, ttl);
                if (DialogDisplayer.getDefault().notify(dlg) != DialogDescriptor.OK_OPTION) {
                    return;
                }
            }
            R r = new R(themes, handle, pnl.getAuthor(), pnl.getDisplayName(), pnl.getCodeName(), outfile, pnl.getVersion());
            RequestProcessor.getDefault().post(r);
        }
    }
    
    private static Iterable <String> getThemeNames() {
        FileObject fob = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Editors/FontsColors");
        FileObject[] kids = fob.getChildren();
        List <String> names = new ArrayList<String>();
        for (FileObject f : kids) {
            if (f.isFolder())
                names.add (f.getName());
        }
        return names;
    }
    
    private static class R implements Runnable {
        private final Collection <String> themes;
        private final ProgressHandle handle;

        public R(Collection<String> themes, ProgressHandle handle, String author, String displayName, String codeName, File outfile, String version) {
            this.themes = themes;
            this.handle = handle;
            this.author = author;
            this.codeName = codeName;
            this.outfile = outfile;
            this.displayName = displayName;
            this.version = version;
        }

        public void run() {
            handle.start(themes.size());
            int ix = 0;
            FileObject fob = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Editors/FontsColors");
            String codeNameSlashes = codeName.replace(".", "/");
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
//                File f = new File ("/tmp/foo.xml");
//                if (!f.exists()) f.createNewFile();
//                FileOutputStream s = new FileOutputStream (f);
//                try {
//                    mdl.write(s);
//                } finally {
//                    s.close();
//                }
                List <FileModel> fls = mdl.getEmbeddedFiles();
                String codeNameDashes = codeName.replace(".", "-");
                String moduleJarName = codeNameDashes + ".jar";
                NbmFileModel nbm = new NbmFileModel (outfile.getPath());
                ModuleModel module = new ModuleModel ("netbeans/modules/" + moduleJarName, codeName, "", version, displayName, "1.4");
                module.setCategory ("Editor");
                ModuleInfoModel infoXml = new ModuleInfoModel (module, "http://www.netbeans.org", author, "[FIXME]");
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


        private List<FileObject> scanForPerMimeThemeDefs(FileObject f) {
            String nm = f.getName();
            FileObject fob = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Editors");
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
        private final String author;
        private final String codeName;
        private final File outfile;
        private final String displayName;
        private final String version;
    }

/*
    public Set<String> getLanguages() {
        return getLanguageToMimeTypeMap().keySet();
    }

    private Map<String, String> languageToMimeType;
    private Map<String, String> getLanguageToMimeTypeMap() {
        if (languageToMimeType == null) {
            languageToMimeType = new HashMap<String, String>();
            Set<String> mimeTypes = EditorSettings.getDefault().getMimeTypes();
            for(String mimeType : mimeTypes) {
                languageToMimeType.put(
                    EditorSettings.getDefault().getLanguageName(mimeType),
                    mimeType
                );
            }
            languageToMimeType.put(
                    ALL_LANGUAGES,
                    "Defaults" //NOI18N
                    );
        }
        return languageToMimeType;
    }

    public static final String ALL_LANGUAGES = NbBundle.getMessage(ColorModel.class, "CTL_All_Languages"); //NOI18N
*/

}
