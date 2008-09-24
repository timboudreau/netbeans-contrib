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

import java.io.IOException;
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
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.text.Document;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
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
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class BatchApply {

    public static String applyFixesToProjects(Collection<Project> projects, Set<String> enabledHints) {
        Map<String, Preferences> overlay = prepareOverlay(enabledHints);
        List<ErrorDescription> eds = new LinkedList<ErrorDescription>();

        for (Project p : projects) {
            Sources s = ProjectUtils.getSources(p);

            for (SourceGroup sg : s.getSourceGroups("java")) {
                eds.addAll(processFolder(sg.getRootFolder(), overlay));
            }
        }

        Map<ErrorDescription, Fix> fixes = new IdentityHashMap<ErrorDescription, Fix>();

        //verify that there is exactly one fix for each ED:
        for (ErrorDescription ed : eds) {
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

            fixes.put(ed, fix);
        }

        for (ErrorDescription ed : eds) {
            try {
                DataObject d = DataObject.find(ed.getFile());
                EditorCookie ec = d.getLookup().lookup(EditorCookie.class);
                Document doc = ec.openDocument();

                fixes.get(ed).implement();

                d.getLookup().lookup(SaveCookie.class).save();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                return ex.getMessage();
            }
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

    private static List<ErrorDescription> processFolder(FileObject folder, final Map<String, Preferences> preferencesOverlay) {
        List<FileObject> toProcess = new LinkedList<FileObject>();
        Queue<FileObject> q = new LinkedList<FileObject>();

        q.add(folder);

        while (!q.isEmpty()) {
            FileObject f = q.poll();

            if (f.isData() && "text/x-java".equals(FileUtil.getMIMEType(f))) {
                toProcess.add(f);
            }

            if (f.isFolder()) {
                q.addAll(Arrays.asList(f.getChildren()));
            }
        }

        final List<ErrorDescription> eds = new LinkedList<ErrorDescription>();
        
        if (!toProcess.isEmpty()) {
            ClasspathInfo cpInfo = ClasspathInfo.create(toProcess.get(0));
            JavaSource js = JavaSource.create(cpInfo, toProcess);

            try {
                js.runUserActionTask(new Task<CompilationController>() {
                    public void run(CompilationController cc) throws Exception {
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
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return eds;
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
}
