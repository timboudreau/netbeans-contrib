/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints.batch;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.java.hints.errors.SuppressWarningsFixer;
import org.netbeans.modules.java.hints.infrastructure.HintsTask;
import org.netbeans.modules.java.hints.infrastructure.RulesManager;
import org.netbeans.modules.java.hints.options.HintsSettings;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.modules.java.hints.spi.AbstractHint.HintSeverity;
import org.netbeans.modules.java.hints.spi.TreeRule;
import org.netbeans.modules.javahints.epi.JavaFix;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class BatchApply {

    private static final RequestProcessor WORKER = new RequestProcessor("Batch Hint Apply");
    
    public static String applyFixes(final Lookup context, final Set<String> enabledHints, boolean progress) {
        assert !progress || SwingUtilities.isEventDispatchThread();

        if (progress) {
            final AtomicBoolean cancel = new AtomicBoolean();
            final ProgressHandle handle = ProgressHandleFactory.createHandle("Batch Hint Apply", new Cancellable() {
                public boolean cancel() {
                    cancel.set(true);

                    return true;
                }
            });
            
            try {
                DialogDescriptor dd = new DialogDescriptor(ProgressHandleFactory.createProgressComponent(handle),
                                                           "Batch Hint Apply",
                                                           true,
                                                           new Object[] {DialogDescriptor.CANCEL_OPTION},
                                                           DialogDescriptor.CANCEL_OPTION,
                                                           DialogDescriptor.DEFAULT_ALIGN,
                                                           null,
                                                           new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancel.set(true);
                    }
                });
                final Dialog d = DialogDisplayer.getDefault().createDialog(dd);
                final String[] result = new String[1];

                Runnable exec = new Runnable() {

                    public void run() {
                        result[0] = applyFixesImpl(context, enabledHints, handle, cancel);

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                d.setVisible(false);
                            }
                        });
                    }
                };

                WORKER.post(exec);

                d.setVisible(true);

                return result[0];
            } finally {
                handle.finish();
            }
        } else {
            return applyFixesImpl(context, enabledHints, null, new AtomicBoolean());
        }
    }

    private static String applyFixesImpl(Lookup context, Set<String> enabledHints, ProgressHandle h, AtomicBoolean cancel) {
        ProgressHandleWrapper handle = new ProgressHandleWrapper(h, new int[] {20, 40, 40});
        
        Map<String, Preferences> overlay = prepareOverlay(enabledHints);
        List<ErrorDescription> eds = new LinkedList<ErrorDescription>();
        Collection<FileObject> toProcess = toProcess(context);

        handle.startNextPart(toProcess.size());
        
        Collection<FileObject> allSources = findAllSources(toProcess);
        Map<ClasspathInfo, Collection<FileObject>> sortedFiles = sortFiles(allSources);

        handle.startNextPart(allSources.size());

        for (Entry<ClasspathInfo, Collection<FileObject>> e: sortedFiles.entrySet()) {
            if (cancel.get()) return null;
            
            eds.addAll(processFiles(e.getKey(), e.getValue(), overlay, handle, cancel));
        }

        Map<ErrorDescription, Fix> fixes = new IdentityHashMap<ErrorDescription, Fix>();
        Map<FileObject, List<JavaFix>> fastFixes = new HashMap<FileObject, List<JavaFix>>();
        List<ErrorDescription> edsWithSlowsFixes = new LinkedList<ErrorDescription>();

        //verify that there is exactly one fix for each ED:
        for (ErrorDescription ed : eds) {
            if (cancel.get()) return null;
            
            if (!ed.getFixes().isComputed()) {
                return "Not computed fixes for: " + ed.getDescription();
            }

            Fix fix = null;

            for (Fix f : ed.getFixes().getFixes()) {
                if (!(f instanceof SuppressWarningsFixer.FixImpl)) {
                    if (fix != null) {
                        fix = null;
                        break;
                    }

                    fix = f;
                }
            }

            if (fix == null) {
                return "Not exactly one fix for: " + ed.getDescription() + ", fixes=" + ed.getFixes().getFixes();
            }

            if (fix instanceof JavaFixImpl) {
                JavaFixImpl ajf = (JavaFixImpl) fix;
                FileObject file = JavaFixImpl.Accessor.INSTANCE.getFile(ajf.jf);
                List<JavaFix> fs = fastFixes.get(file);

                if (fs == null) {
                    fastFixes.put(file, fs = new LinkedList<JavaFix>());
                }

                fs.add(ajf.jf);
            } else {
                fixes.put(ed, fix);
                edsWithSlowsFixes.add(ed);
            }
        }

        handle.startNextPart(eds.size());

        try {
            List<ModificationResult> results = performFastFixes(fastFixes, handle, cancel);

            if (cancel.get()) return null;

            performSlowFixes(edsWithSlowsFixes, fixes, handle);

            if (cancel.get()) return null;

            for (ModificationResult r : results) {
                r.commit();
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return ex.getLocalizedMessage();
        }
        
        return null;
    }

    public static List<TreeRule> listHints() {
        List<TreeRule> hints = new LinkedList<TreeRule>();

        for (List<TreeRule> rules : RulesManager.getInstance().getHints().values()) {
            for (TreeRule r : rules) {
                if (r instanceof AbstractHint) {
                    try {
                        r.getId();
                        r.getDisplayName();
                        
                        hints.add(r);
                    } catch (Exception e) {
                        Logger.getLogger(BatchApply.class.getName()).log(Level.FINE, null, e);
                    }
                }
            }
        }

        Collections.sort(hints, new Comparator<TreeRule>() {
            public int compare(TreeRule o1, TreeRule o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });
        
        return hints;
    }

    private static List<ErrorDescription> processFiles(ClasspathInfo cpInfo, Collection<FileObject> toProcess, final Map<String, Preferences> preferencesOverlay, final ProgressHandleWrapper handle, final AtomicBoolean cancel) {
        final List<ErrorDescription> eds = new LinkedList<ErrorDescription>();
        JavaSource js = JavaSource.create(cpInfo, toProcess);

        try {
            js.runUserActionTask(new Task<CompilationController>() {
                public void run(CompilationController cc) throws Exception {
                    if (cancel.get()) return ;
                    
                    HintsSettings.setPreferencesOverride(preferencesOverlay);

                    DataObject d = DataObject.find(cc.getFileObject());
                    EditorCookie ec = d.getLookup().lookup(EditorCookie.class);
                    Document doc = ec.openDocument();

                    try {
                        if (cc.toPhase(JavaSource.Phase.RESOLVED).compareTo(JavaSource.Phase.RESOLVED) < 0) {
                            return;
                        }

                        eds.addAll(new HintsTask().computeHints(cc));
                    } finally {
                        HintsSettings.setPreferencesOverride(null);
                    }

                    handle.tick();
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return eds;
    }

    private static String performSlowFixes(List<ErrorDescription> edsWithSlowsFixes, Map<ErrorDescription, Fix> fixes, ProgressHandleWrapper handle) throws Exception {
        for (ErrorDescription ed : edsWithSlowsFixes) {
            try {
                DataObject d = DataObject.find(ed.getFile());
                EditorCookie ec = d.getLookup().lookup(EditorCookie.class);
                Document doc = ec.openDocument();

                fixes.get(ed).implement();

                SaveCookie sc = d.getLookup().lookup(SaveCookie.class);

                if (sc != null) {
                    sc.save();
                }
            } catch (Exception ex) {
                Exceptions.attachMessage(ex, FileUtil.getFileDisplayName(ed.getFile()));
                
                throw ex;
            }

            handle.tick();
        }
        return null;
    }

    private static List<ModificationResult> performFastFixes(Map<FileObject, List<JavaFix>> fastFixes, ProgressHandleWrapper handle, AtomicBoolean cancel) {
        Map<ClasspathInfo, Collection<FileObject>> sortedFilesForFixes = sortFiles(fastFixes.keySet());
        List<ModificationResult> results = new LinkedList<ModificationResult>();

        for (Entry<ClasspathInfo, Collection<FileObject>> e : sortedFilesForFixes.entrySet()) {
            if (cancel.get()) return null;
            
            Map<FileObject, List<JavaFix>> filtered = new HashMap<FileObject, List<JavaFix>>();

            for (FileObject f : e.getValue()) {
                filtered.put(f, fastFixes.get(f));
            }

            ModificationResult r = performFastFixes(e.getKey(), filtered, handle, cancel);
            
            if (r != null) {
                results.add(r);
            }
        }

        return results;
    }

    private static ModificationResult performFastFixes(ClasspathInfo cpInfo, final Map<FileObject, List<JavaFix>> toProcess, final ProgressHandleWrapper handle, final AtomicBoolean cancel) {
        JavaSource js = JavaSource.create(cpInfo, toProcess.keySet());

        try {
            return js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    if (cancel.get()) return ;
                    
                    if (wc.toPhase(Phase.RESOLVED).compareTo(Phase.RESOLVED) < 0)
                        return ;

                    for (JavaFix f : toProcess.get(wc.getFileObject())) {
                        if (cancel.get()) return ;
                        
                        JavaFixImpl.Accessor.INSTANCE.process(f, wc);
                    }

                    handle.tick();
                }
            });
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    private static Map<String, Preferences> prepareOverlay(Set<String> enabledHints) {
        Map<String, Preferences> preferencesOverlay = new HashMap<String, Preferences>();
        for (List<TreeRule> rules : RulesManager.getInstance().getHints().values()) {
            for (TreeRule r : rules) {
                String id = r.getId();

                if (r instanceof AbstractHint && !preferencesOverlay.containsKey(id)) {
                    OverridePreferences prefs = new OverridePreferences(((AbstractHint) r).getPreferences(null));

                    preferencesOverlay.put(r.getId(), prefs);
                    HintsSettings.setEnabled(prefs, enabledHints.contains(id));
                    HintsSettings.setSeverity(prefs, HintSeverity.WARNING);
                }
            }
        }

        return preferencesOverlay;
    }

    private static class OverridePreferences extends AbstractPreferences {

        private Preferences delegateTo;
        private Map<String, String> data;
        private Set<String> removed;

        public OverridePreferences(Preferences delegateTo) {
            super(null, "");
            this.data = new HashMap<String, String>();
            this.removed = new HashSet<String>();
        }

        protected void putSpi(String key, String value) {
            data.put(key, value);
            removed.remove(key);
        }

        protected String getSpi(String key) {
            if (data.containsKey(key)) {
                return data.get(key);
            } else {
                if (removed.contains(key)) {
                    return null;
                } else {
                    return delegateTo.get(key, null);
                }
            }
        }

        protected void removeSpi(String key) {
            removed.add(key);
        }

        protected void removeNodeSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected String[] keysSpi() throws BackingStoreException {
            Set<String> keys = new HashSet<String>(Arrays.asList(delegateTo.keys()));

            keys.removeAll(removed);
            keys.addAll(data.keySet());

            return keys.toArray(new String[0]);
        }

        protected String[] childrenNamesSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected AbstractPreferences childSpi(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void syncSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void flushSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static Collection<FileObject> toProcess(Lookup l) {
        List<FileObject> result = new LinkedList<FileObject>();

        result.addAll(l.lookupAll(FileObject.class));

        for (SourceGroup sg : l.lookupAll(SourceGroup.class)) {
            result.add(sg.getRootFolder());
        }
        
        for (Project p : l.lookupAll(Project.class)) {
            Sources s = ProjectUtils.getSources(p);

            for (SourceGroup sg : s.getSourceGroups("java")) {
                result.add(sg.getRootFolder());
            }
        }

        return result;
    }

    private static Collection<FileObject> findAllSources(Collection<FileObject> from) {
        List<FileObject> result = new LinkedList<FileObject>();
        Queue<FileObject> q = new LinkedList<FileObject>();

        q.addAll(from);

        while (!q.isEmpty()) {
            FileObject f = q.poll();

            if (f.isData() && "text/x-java".equals(FileUtil.getMIMEType(f))) {
                result.add(f);
            }

            if (f.isFolder()) {
                q.addAll(Arrays.asList(f.getChildren()));
            }
        }

        return result;
    }

    private static Map<ClasspathInfo, Collection<FileObject>> sortFiles(Collection<FileObject> from) {
        Map<List<ClassPath>, Collection<FileObject>> m = new HashMap<List<ClassPath>, Collection<FileObject>>();

        for (FileObject f : from) {
            List<ClassPath> cps = new ArrayList<ClassPath>(3);

            cps.add(ClassPath.getClassPath(f, ClassPath.BOOT));
            cps.add(ClassPath.getClassPath(f, ClassPath.COMPILE));
            cps.add(ClassPath.getClassPath(f, ClassPath.SOURCE));

            Collection<FileObject> files = m.get(cps);

            if (files == null) {
                m.put(cps, files = new LinkedList<FileObject>());
            }

            files.add(f);
        }

        Map<ClasspathInfo, Collection<FileObject>> result = new HashMap<ClasspathInfo, Collection<FileObject>>();

        for (Entry<List<ClassPath>, Collection<FileObject>> e : m.entrySet()) {
            result.put(ClasspathInfo.create(e.getKey().get(0), e.getKey().get(1), e.getKey().get(2)), e.getValue());
        }

        return result;
    }

    private static final class ProgressHandleWrapper {

        private static final int TOTAL = 1000;
        
        private final ProgressHandle handle;
        private final int[]          parts;

        private       int            currentPart = (-1);
        private       int            currentPartTotalWork;
        private       int            currentPartWorkDone;

        private       int            currentOffset;

        public ProgressHandleWrapper(int[] parts) {
            this(null, parts);
        }
        
        public ProgressHandleWrapper(ProgressHandle handle, int[] parts) {
            this.handle = handle;

            if (handle == null) {
                this.parts = null;
            } else {
                int total = 0;

                for (int i : parts) {
                    total += i;
                }

                this.parts = new int[parts.length];

                for (int cntr = 0; cntr < parts.length; cntr++) {
                    this.parts[cntr] = (TOTAL * parts[cntr]) / total;
                }
            }
        }

        public void startNextPart(int totalWork) {
            if (handle == null) return ;
            
            if (currentPart == (-1)) {
                handle.start(TOTAL);
            } else {
                currentOffset += parts[currentPart];
            }

            currentPart++;

            currentPartTotalWork = totalWork;
            currentPartWorkDone  = 0;
        }

        public void tick() {
            if (handle == null) return ;

            currentPartWorkDone++;

            handle.progress(currentOffset + (parts[currentPart] * currentPartWorkDone) / currentPartTotalWork);
        }

        public void setMessage(String message) {
            if (handle == null) return ;

            handle.progress(message);
        }
    }
}
