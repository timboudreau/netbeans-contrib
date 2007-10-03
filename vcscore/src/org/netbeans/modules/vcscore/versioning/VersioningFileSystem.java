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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.vcscore.versioning;

import java.beans.*;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.io.IOException;
import java.util.*;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.Turbo;

/**
 * Adds support for versioned input streams to VcsFileSystems.
 * <p>
 * Subclasses note: this object stores it's setting directly
 * into peer filesystem. Properties must be explicitly loaded/stored
 * using special purpose load/store methods.
 *
 * @author  Martin Entlicher
 * @author  Petr Kuzel (removed extends FileSystem)
 */
public abstract class VersioningFileSystem {

    private static final SystemAction[] NO_ACTIONS = new SystemAction[0];

    /** wrapped filesystem */
    private final AbstractFileSystem fileSystem;

    /** Keeps trace of supported filesystems. */
    private static final Map fs2versiong = new WeakHashMap(10);

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public VersioningFileSystem(AbstractFileSystem underlyingFs) {
        fileSystem = underlyingFs;
        fs2versiong.put(fileSystem, this);
    }

    /** Finds existing versioning support for file system or null. */
    public static VersioningFileSystem findFor(FileSystem fileSystem) {
        return (VersioningFileSystem) fs2versiong.get(fileSystem);
    }

    /** Support for versioned access to file streams. */
    public abstract VersioningFileSystem.Versions getVersions();

    public static interface Versions extends Serializable {

        public RevisionList getRevisions(String name, boolean refresh);

        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException;

    }

    /** @deprecated for identity purposes use VersionFileSytem directly. */
    public String getSystemName() {
        return fileSystem.getSystemName();
    }

    public FileObject getRoot() {
        return fileSystem.getRoot();
    }

    /** Callback called on adding to VersioningRepository. */
    protected void addNotify() {
    }

    /** Callback called after removal from VersioningRepository. */
    protected void removeNotify() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected final boolean storeProperty(String name, Serializable value) {
        try {
            getRoot().setAttribute("Versioning-Property-" + name, value);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    protected final Serializable loadProperty(String name, Serializable defaultValue) {
        Serializable ret = (Serializable) getRoot().getAttribute("Versioning-Property-" + name);
        return ret != null ? ret : defaultValue;
    }

    protected final void firePropertyChange(PropertyChangeEvent e) {
        pcs.firePropertyChange(e);
    }

    protected final void firePropertyChange(String name, Object oldVal, Object newVal) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldVal, newVal);
        firePropertyChange(e);
    }

    public String getDisplayName() {
        return fileSystem.getDisplayName();
    }

    // unused or called by reflection?
    public SystemAction[] getActions(Set vfoSet) {
        SystemAction[] actions = fileSystem.getActions(vfoSet);
        SystemAction myAction = SystemAction.get(VersioningExplorerAction.class);
        int index = 0;
        for (; index < actions.length; index++) {
            if (myAction.equals(actions[index])) break;
        }
        if (index < actions.length) {
            SystemAction[] actions1 = new SystemAction[actions.length - 1];
            if (index > 0) {
                System.arraycopy(actions, 0, actions1, 0, index);
            }
            if (index < actions1.length) {
                System.arraycopy(actions, index + 1, actions1, index, actions1.length - index);
            }
            actions = actions1;
        }
        return actions;
    }
    
    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        return NO_ACTIONS;
    }

    protected final FileObject findResource(String name) {
        return fileSystem.findResource(name);
    }

    protected void refreshExistingFolders() {
        // TODO require extended contract of FS passed in constructor
        // anyway I'm uncertain if it;s necessary at all
        if (fileSystem instanceof VcsFileSystem) {
            VcsFileSystem fs = (VcsFileSystem) fileSystem;
            fs.refreshExistingFolders();
        }
    }
    
    /**
     * The filter of file names that should not be presented in GUI, redefining visibilityquery.
     */
    public abstract FilenameFilter getFileFilter();
    
}
