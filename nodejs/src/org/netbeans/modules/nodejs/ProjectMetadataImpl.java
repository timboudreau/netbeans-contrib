/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.nodejs.json.SimpleJSONParser;
import org.netbeans.modules.nodejs.json.SimpleJSONParser.JsonException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem.AtomicAction;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tim Boudreau
 */
public final class ProjectMetadataImpl extends FileChangeAdapter implements ProjectMetadata {

    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);
    private final Project project;
    private static final RequestProcessor rp = new RequestProcessor("node.js project metadata saver", 1, true);

    public ProjectMetadataImpl(Project project) {
        this.project = project;
    }

    public String getValue(String key) {
        if (key.indexOf('.') > 0) {
            List<String> keys = new ArrayList<String>(Arrays.asList(key.split("\\.")));
            Object result = getValue(getMap(), keys);
            synchronized (this) {
                if (map.isEmpty()) {
                    map = null;
                }
            }
            return toString(result);
        } else {
            return toString(getMap().get(key));
        }
    }

    public List<?> getValues(String key) {
        Object result = null;
        if (key.indexOf('.') > 0) {
            List<String> keys = new ArrayList<String>(Arrays.asList(key.split("\\.")));
            result = getValue(getMap(), keys);
        } else {
            result = getMap().get(key);
        }
        synchronized (this) {
            if (map.isEmpty()) {
                map = null;
            }
        }
        if (result instanceof List) {
            return (List<?>) result;
        } else if (result instanceof Map) {
            return Arrays.asList(toString(result));
        } else if (result instanceof String) {
            return Arrays.asList((String) result);
        } else {
            return Collections.emptyList();
        }
    }

    private String toString(Object o) {
        if (o instanceof Map) {
            return SimpleJSONParser.out((Map<String, Object>) o).toString();
        } else if (o instanceof List) {
            StringBuilder sb = new StringBuilder();
            for (Iterator<?> it = ((List<?>) o).iterator(); it.hasNext();) {
                sb.append(toString(it.next()));
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        } else if (o instanceof CharSequence) {
            return o.toString();
        } else if (o == null) {
            return "";
        }
        return o.toString();
    }

    private Object getValue(Map<String, Object> m, List<String> keys) {
        String next = keys.remove(0);
        if (keys.isEmpty()) {
            Object result = m.get(next);
            return result;
        } else {
            Object o = m.get(next);
            if (o instanceof Map) {
                return getValue((Map<String, Object>) o, keys);
            } else {
                return toString(o);
            }
        }
    }
    private volatile Map<String, Object> map;
    private volatile boolean hasErrors;
    private volatile boolean listening;
    private final ReentrantLock lock = new ReentrantLock();

    private Map<String, Object> load(FileObject fo) throws IOException {
        lock.lock();
        boolean err = false;
        try {
            synchronized (this) {
                if (map != null) {
                    return map;
                }
            }
            FileLock fileLock = fo.lock();
            try {
                SimpleJSONParser p = new SimpleJSONParser(true); //permissive mode - will parse as much as it can
                Map<String, Object> m = p.parse(fo);
                ProjectMetadataImpl.this.hasErrors = err = p.hasErrors();
                synchronized (this) {
                    map = Collections.synchronizedMap(m);
                    return map;
                }
            } catch (JsonException ex) {
                Logger.getLogger(ProjectMetadataImpl.class.getName()).log(Level.INFO,
                        "Bad package.json in " + fo.getPath(), ex);
                return new LinkedHashMap<String, Object>();
            } finally {
                fileLock.releaseLock();
            }
        } finally {
            lock.unlock();
            if (err) {
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ProjectMetadataImpl.class, "ERROR_PARSING_PACKAGE_JSON", project.getLookup().lookup(ProjectInformation.class).getDisplayName()), 3);
            }
        }
    }

    private final Map<String, Object> getMap() {
        Map<String, Object> result = map;
        if (result == null) {
            synchronized (this) {
                result = map;
            }
        }
        if (result == null) {
            final FileObject fo = project.getProjectDirectory().getFileObject("package.json");
            if (fo == null) {
                return new LinkedHashMap<String, Object>();
            }
            if (!listening) {
                listening = true;
                fo.addFileChangeListener(FileUtil.weakFileChangeListener(this, fo));
            }
            try {
                result = load(fo);
                synchronized (this) {
                    map = result;
                }
            } catch (IOException ioe) {
                Logger.getLogger(ProjectMetadataImpl.class.getName()).log(Level.INFO,
                        "Problems loading " + fo.getPath(), ioe);
                result = new LinkedHashMap<String,Object>();
            }
        }
        return result;
    }
    volatile int saveCount;

    @Override
    public void fileChanged(FileEvent fe) {
        if (saveCount > 0) {
            saveCount--;
            return;
        }
        map = null;
    }

    public void setValue(String key, List<String> values) {
        Object oldValue;
        if (key.indexOf('.') > 0) {
            List<String> keys = new ArrayList<String>(Arrays.asList(key.split("\\.")));
            oldValue = setValues(getMap(), keys, values);
        } else {
            oldValue = getMap().put(key, values);
        }
        if (unequal(oldValue, values)) {
            queueSave();
            supp.firePropertyChange(key, toString(oldValue), values);
        }
    }

    public void setValue(String key, String value) {
        Object oldValue;
        if (key.indexOf('.') > 0) {
            List<String> keys = new ArrayList<String>(Arrays.asList(key.split("\\.")));
            oldValue = setValue(getMap(), keys, value);
        } else {
            oldValue = getMap().put(key, value);
        }
        if (unequal(oldValue, value)) {
            queueSave();
            supp.firePropertyChange(key, toString(oldValue), value);
        }
    }

    private boolean unequal(Object a, Object b) {
        if (a == null && b == null) {
            return false;
        }
        if (a == null || b == null) {
            return false;
        }
        return !a.equals(b);
    }

    private Object setValue(Map<String, Object> m, List<String> keys, String value) {
        String nextKey = keys.remove(0);
        if (keys.isEmpty()) {
            Object result = m.put(nextKey, value);
            if (unequal(value, result)) {
                queueSave();
            }
            return result;
        } else {
            Object o = m.get(nextKey);
            Map<String, Object> nue = null;
            if (o instanceof Map) {
                nue = (Map<String, Object>) o;
            }
            if (nue == null) {
                //if the value was a string, we clobber it here - careful
                nue = new LinkedHashMap<String, Object>();
                queueSave();
            }
            if (value == null) {
                m.remove(nextKey);
            } else {
                m.put(nextKey, nue);
            }
            return setValue(nue, keys, value);
        }
    }

    private Object setValues(Map<String, Object> m, List<String> keys, List<String> values) {
        String nextKey = keys.remove(0);
        if (keys.isEmpty()) {
            Object result = m.put(nextKey, values);
            if (unequal(values, result)) {
                queueSave();
            }
            return result;
        } else {
            Map<String, Object> nue = (Map<String, Object>) m.get(nextKey);
            if (nue == null) {
                nue = new LinkedHashMap<String, Object>();
                queueSave();
            }
            if (values == null) {
                m.remove(nextKey);
            } else {
                m.put(nextKey, nue);
            }
            return setValues(nue, keys, values);
        }
    }

    public String toString() {
        return SimpleJSONParser.out(getMap()).toString();
    }

    public void save() throws IOException {
        if (this.map != null) {
            if (hasErrors) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(NbBundle.getMessage(ProjectMetadataImpl.class, "OVERWRITE_BAD_JSON", project.getLookup().lookup(ProjectInformation.class).getDisplayName()));
                if (!DialogDisplayer.getDefault().notify(nd).equals(nd.OK_OPTION)) {
                    synchronized (this) {
                        map = null;
                    }
                    return;
                }
            }
            final FileObject fo = project.getProjectDirectory().getFileObject("package.json");
            project.getProjectDirectory().getFileSystem().runAtomicAction(new AtomicAction() {

                @Override
                public void run() throws IOException {
                    FileObject save = fo;
                    if (save == null) {
                        save = project.getProjectDirectory().createData("package.json");
                    }
                    final FileObject writeTo = save;
                    try {
                        ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {

                            @Override
                            public Void run() throws Exception {
                                CharSequence seq = SimpleJSONParser.out(map);
                                OutputStream out = writeTo.getOutputStream();
                                try {
                                    ByteArrayInputStream in = new ByteArrayInputStream(seq.toString().getBytes("UTF-8"));
                                    FileUtil.copy(in, out);
                                } catch (FileAlreadyLockedException e) {
                                    Logger.getLogger(ProjectMetadataImpl.class.getName()).log(
                                            Level.INFO, "Could not save properties for {0} - queue for later",
                                            project.getProjectDirectory().getPath());
                                    queueSave();
                                } finally {
                                    out.close();
                                    synchronized (ProjectMetadataImpl.this) { //tests
                                        ProjectMetadataImpl.this.notifyAll();
                                    }
                                    saveCount++;
                                }
                                hasErrors = false;
                                return null;
                            }
                        });
                    } catch (MutexException e) {
                        if (e.getCause() instanceof IOException) {
                            throw (IOException) e.getCause();
                        } else if (e.getCause() instanceof RuntimeException) {
                            throw (RuntimeException) e.getCause();
                        } else if (e.getCause() instanceof Error) {
                            throw (Error) e.getCause();
                        } else {
                            throw new AssertionError(e);
                        }
                    }
                }
            });
        }
    }

    class R implements Runnable {

        public void run() {
            try {
                save();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    private final Task task = rp.create(new R());

    private void queueSave() {
        task.schedule(1000);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        supp.addPropertyChangeListener(WeakListeners.propertyChange(pcl, supp));
    }
}
