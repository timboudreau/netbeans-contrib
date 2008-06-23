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
import org.netbeans.modules.vcscore.VcsProvider;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
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

    /** Keeps trace of supported filesystems. */
    private static final Map fs2versiong = new WeakHashMap(10);

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public VersioningFileSystem() {
    }

    /** Finds existing versioning support for file system or null. */
    public static VersioningFileSystem findFor(FileObject fo) {
        VcsProvider p = VcsProvider.getProvider(fo);
        if (p != null) {
            return p.getVersioningSystem();
        } else {
            return null;
        }
    }

    /** Support for versioned access to file streams. */
    public abstract VersioningFileSystem.Versions getVersions();

    public static interface Versions extends Serializable {

        public RevisionList getRevisions(String name, boolean refresh);

        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException;

    }

    /** @deprecated for identity purposes use VersionFileSytem directly. */
    public abstract String getSystemName();

    public abstract FileObject getRoot();

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

    public abstract String getDisplayName();

    /* Was called by the filesystem before it's extension was removed
     * This filters the VersioningExplorerAction.
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
     */

    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        return NO_ACTIONS;
    }

    /**
     * The filter of file names that should not be presented in GUI, redefining visibilityquery.
     */
    public abstract FilenameFilter getFileFilter();

}
