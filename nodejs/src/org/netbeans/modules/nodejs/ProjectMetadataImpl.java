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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import static org.netbeans.modules.nodejs.NodeJSProject.*;

import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.EditableProperties;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Tim Boudreau
 */
final class ProjectMetadataImpl implements ProjectMetadata {

    private final NodeJSProject project;
    private volatile EditableProperties props;
    private long lastModifiedFileDate = Integer.MIN_VALUE;
    private boolean listening = false;

    ProjectMetadataImpl(NodeJSProject project) {
        this.project = project;
    }

    @Override
    public String getValue(String key) {
        return getProperties().getProperty(key);
    }

    @Override
    public void setValue(String key, String value) {
        Parameters.notNull("key", key);
        String oldValue;
        if (value == null) {
            oldValue = getProperties().remove(key);
        } else {
            oldValue = getProperties().setProperty(key, value);
        }
        if (different(oldValue, value)) {
            project.state().markModified();
            try {
                save();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                supp.firePropertyChange(key, oldValue, value);
            }
        }
    }

    private boolean different(Object a, Object b) {
        if ((a == null) != (b == null)) {
            return true;
        } else if (a != null && b != null) {
            return !a.equals(b);
        }
        return false;
    }

    private EditableProperties getProperties() {
        FileObject metadataFile = getMetadataFile(false);
        if (metadataFile == null || metadataFile.isFolder()) {
            //hosed
            return props = new EditableProperties(true);
        }
        if (props == null) {
            synchronized (this) {
                if (metadataFile == null || !metadataFile.isValid()) {
                    synchronized (this) {
                        return props = new EditableProperties(true);
                    }
                }
                if (props == null) {
                    props = loadProperties(metadataFile);
                }
            }
        } else {
            long newLastModified = metadataFile == null || !metadataFile.isValid() ? Long.MIN_VALUE : metadataFile.lastModified().getTime();
            synchronized (this) {
                if (newLastModified != lastModifiedFileDate) {
                    props = loadProperties(metadataFile);
                }
            }
        }
        return props;
    }

    private FileObject getMetadataFile(boolean create) {
        String fname = METADATA_DIR + '/' + METADATA_PROPERTIES_FILE;
        FileObject result = project.getProjectDirectory().getFileObject(fname);
        if (result == null && create) {
            try {
                result = FileUtil.createData(project.getProjectDirectory(), fname);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return result;
    }

    private EditableProperties loadProperties(FileObject metadataFile) {
        assert Thread.holdsLock(this);
        EditableProperties result = new EditableProperties(true);
        if (metadataFile != null) {
            lastModifiedFileDate = metadataFile.lastModified().getTime();
            try {
                InputStream in = metadataFile.getInputStream();
                try {
                    result.load(in);
                } finally {
                    in.close();
                }
                if (!listening) {
                    metadataFile.addFileChangeListener(FileUtil.weakFileChangeListener(changeListener, this));
                }
            } catch (IOException ioe) {
                LOGGER.log(Level.SEVERE, "Failure reading project metadata for {0}", project.getProjectDirectory().getPath());
                LOGGER.log(Level.SEVERE, null, ioe);
            }
        }
        return result;
    }
    private final FileChangeListener changeListener = new FileChangeAdapter() {

        @Override
        public void fileChanged(FileEvent fe) {
            synchronized (ProjectMetadataImpl.this) {
                props = null;
            }
        }
    };

    @Override
    public void save() throws IOException {
        EditableProperties properties;
        synchronized (this) {
            properties = props;
        }
        if (properties == null) {
            return;
        }
        FileObject metadataFile = getMetadataFile(true);
        OutputStream out = metadataFile.getOutputStream();
        try {
            properties.store(out);
            synchronized (this) {
                lastModifiedFileDate = metadataFile.lastModified().getTime();
            }
        } finally {
            out.close();
        }
    }
    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        supp.addPropertyChangeListener(pcl);
    }
}
