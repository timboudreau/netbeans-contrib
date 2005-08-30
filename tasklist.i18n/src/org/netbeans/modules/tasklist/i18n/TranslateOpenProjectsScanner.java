package org.netbeans.modules.tasklist.i18n;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.tasklist.client.*;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.util.Queue;

/**
 * Scans text constants in all open projects.
 *
 * @author tl
 */
public class TranslateOpenProjectsScanner implements Runnable {
    /** <Suggestion> */
    private List errors = new ArrayList();
    
    private FileChangeListener listener;
    
    private Queue jobs = new Queue();
    private boolean stopScanning;
    
    /**
     * Creates a new instance of I18NScanner
     */
    public TranslateOpenProjectsScanner() {
        OpenProjects.getDefault().addPropertyChangeListener(
            new PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    if (evt.getPropertyName() == 
                        OpenProjects.PROPERTY_OPEN_PROJECTS) {
                        stopScanning = true;
                        jobs.put(OpenProjects.getDefault());
                    }
                }
            }
        );
        listener = new FileChangeAdapter() {
            public void fileDataCreated(FileEvent fe) {
                TranslateSuggestionProvider.LOGGER.fine("f");
                jobs.put(fe.getFile());
            }
            public void fileChanged(FileEvent fe) {
                TranslateSuggestionProvider.LOGGER.fine("f");
                jobs.put(fe.getFile());
            }
            public void fileDeleted(FileEvent fe) {
                TranslateSuggestionProvider.LOGGER.fine("f");
                removeForFile(fe.getFile());
            }
            public void fileRenamed(FileRenameEvent fe) {
                TranslateSuggestionProvider.LOGGER.fine("f");
                removeForNonExistentFiles();
                jobs.put(fe.getFile());
            }
        };
    }
    
    /**
     * Runs the scanner
     */
    public void run() {
        jobs.put(OpenProjects.getDefault());
        while (true) {
            Object obj = (Object) jobs.get();
            stopScanning = false;
            if (obj instanceof OpenProjects)
                scan((OpenProjects) obj);
            else if (obj instanceof Project)
                scan((Project) obj);
            else if (obj instanceof FileObject)
                scan((FileObject) obj);
            else
                throw new InternalError();
        }
    }

    /**
     * Scans open projects.
     *
     * @param p open projects
     */
    private void scan(OpenProjects p) {
        StaticSuggestions ss = StaticSuggestions.getDefault();
        
        for (int i = 0; i < errors.size(); i++) {
            Suggestion s = (Suggestion) errors.get(i);
            ss.remove(s);
        }
        errors.clear();
        
        Project[] prjs = p.getOpenProjects();
        for (int i = 0; i < prjs.length; i++) {
            if (stopScanning)
                return;
            scan(prjs[i]);
        }
    }
    
    /**
     * Scans a project
     *
     * @param p project to be scanned
     */
    private void scan(Project p) {
        TranslateSuggestionProvider.LOGGER.fine("search in prj " + p);
        Sources s = ProjectUtils.getSources(p);
        SourceGroup[] sgs = s.getSourceGroups(
            JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int j = 0; j < sgs.length; j++) {
            if (stopScanning)
                return;

            SourceGroup sg = sgs[j];
            scan(sgs[j].getRootFolder());
        }
    }
    
    /**
     * Scans a directory or a file
     *
     * @param fo a file object
     */
    private void scan(FileObject fo) {
        if (fo.isFolder())
            scanFolder(fo);
        else
            scanFile(fo);
    }
    
    /**
     * Scans a directory
     *
     * @param dir a directory
     */
    private void scanFolder(FileObject dir) {
        dir.removeFileChangeListener(listener);
        dir.addFileChangeListener(listener);
        FileObject[] f = dir.getChildren();
        for (int i = 0; i < f.length; i++) {
            if (stopScanning)
                return;
            
            if (!f[i].isValid() || f[i].isVirtual())
                continue;
            
            scan(f[i]);
        }
    }
    
    /**
     * Scans a file
     *
     * @param f a file to be scanned
     */
    private void scanFile(FileObject f) {
        if (!f.getExt().equalsIgnoreCase("java") ||
            f.existsExt("form"))
            return;
        
        StaticSuggestions ss = StaticSuggestions.getDefault();
        
        // remove old suggestions for this class
        removeForFile(f);
        
        TranslateFileChecker.Error[] err = new TranslateFileChecker(f).run();
        TranslateSuggestionProvider.LOGGER.fine(err.length + " search in " + f);
        
        SuggestionManager sm = SuggestionManager.getDefault();
        List e = new ArrayList();
        for (int i = 0; i < err.length; i++) {
            AddI18NCommentPerformer action = new AddI18NCommentPerformer();
            SuggestionAgent problem = 
                sm.createSuggestion(f, TranslateSuggestionProvider.TYPE, 
                    NbBundle.getMessage(TranslateOpenProjectsScanner.class, "ProblemText", err[i].constant), // NOI18N
                    action, null);
            try {
                DataObject dataObject = DataObject.find(f);
                problem.setLine(TLUtils.getLineByNumber(dataObject, 
                    err[i].line + 1));
            } catch (DataObjectNotFoundException er) {
                // ignore
                ErrorManager.getDefault().notify(er);
            }
            
            e.add(problem.getSuggestion());
            ss.add(problem.getSuggestion());
        }
        errors.addAll(e);
    }
    
    /**
     * Removes all created suggestions for the specified file 
     * and saves them in <code>removed</code>
     * 
     * @param f a FileObject
     */
    private void removeForFile(FileObject f) {
        List removed = new ArrayList(10);
        for (int i = 0; i < errors.size();) {
            Suggestion s = (Suggestion) errors.get(i);
            if (f.equals(s.getFileObject())) {
                if (removed != null)
                    removed.add(errors.get(i));
                errors.remove(i);
            } else {
                i++;
            }
        }
        StaticSuggestions ss = StaticSuggestions.getDefault();
        for (int i = 0; i < removed.size(); i++) {
            Suggestion s = (Suggestion) removed.get(i);
            ss.remove(s);
        }
    }
    
    /**
     * Removes suggestions for non-existent files.
     */
    private void removeForNonExistentFiles() {
        List removed = new ArrayList(10);
        for (int i = 0; i < errors.size();) {
            Suggestion s = (Suggestion) errors.get(i);
            FileObject fo = s.getFileObject();
            if (!fo.isValid()) {
                if (removed != null)
                    removed.add(errors.get(i));
                errors.remove(i);
            } else {
                i++;
            }
        }
        StaticSuggestions ss = StaticSuggestions.getDefault();
        for (int i = 0; i < removed.size(); i++) {
            Suggestion s = (Suggestion) removed.get(i);
            ss.remove(s);
        }
    }
}
