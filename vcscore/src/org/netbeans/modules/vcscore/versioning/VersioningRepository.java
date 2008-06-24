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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.openide.util.Lookup;

/**
 *
 * @author  Martin Entlicher
 */
public final class VersioningRepository extends Object implements java.io.Serializable {

    private static VersioningRepository repository;

    private ArrayList verSystems;
    private ArrayList verSystemsCopy;

    private Hashtable names;

    private transient EventListenerList listenerList = new EventListenerList();

    /** property listener on systemName property of file system */
    private java.beans.PropertyChangeListener propListener = new java.beans.PropertyChangeListener () {
                /** @param ev event with changes */
                public void propertyChange (java.beans.PropertyChangeEvent ev) {
                    if (ev.getPropertyName ().equals ("systemName")) { //NOI18N
                        // assign the property to new name
                        String ov = (String)ev.getOldValue ();
                        String nv = (String)ev.getNewValue ();
                        VersioningFileSystem fs = (VersioningFileSystem)ev.getSource ();
                        // when a file system is valid then it is attached to a name
                        synchronized (this) {
                            names.remove (ov);
                            // register name of the file system
                            names.put (nv, fs);
                        }
                    }
                }
            };

    private static final long serialVersionUID = 8047724018983158285L;

    /** Creates new VersioningRepository */
    private VersioningRepository() {
        verSystems = new ArrayList();
        names = new Hashtable();
    }

    public static synchronized VersioningRepository getRepository() {
        if (repository == null) {
            initRepository();
        }
        return repository;
    }

    private static synchronized void initRepository() {
        if (repository != null) return;
        Lookup l = Lookup.getDefault();
        repository = (VersioningRepository) l.lookup(org.netbeans.modules.vcscore.versioning.VersioningRepository.class);
        if (repository == null) {
            repository = new VersioningRepository();
        }
    }

    public final void addVersioningFileSystem(VersioningFileSystem vfs) {
        synchronized (this) {
            String systemName = vfs.getSystemName();
            if (!names.containsKey(systemName)) {
                verSystems.add(vfs);
                verSystemsCopy = new ArrayList(verSystems);
                // mark as a listener on changes in the file system
                vfs.addPropertyChangeListener (propListener);
                names.put(systemName, vfs);
                vfs.addNotify();
            } else {
                throw new IllegalArgumentException("VersioningFileSystem of name '"+systemName+"' is already registered.");
            }
        }
        fireVerSystem(vfs, true);
    }

    public final void removeVersioningFileSystem(VersioningFileSystem vfs) {
        synchronized (this) {
            String systemName = vfs.getSystemName();
            if (names.containsKey(systemName)) {
                verSystems.remove(vfs);
                verSystemsCopy = new ArrayList(verSystems);
                vfs.removePropertyChangeListener (propListener);
                names.remove(systemName);
                vfs.removeNotify();
            } else {
                throw new IllegalArgumentException("VersioningFileSystem of name '"+systemName+"' is not registered.");
            }
        }
        fireVerSystem(vfs, false);
    }

    public final List getVersioningFileSystems() {
        ArrayList vfsl = new ArrayList(verSystems);
        return vfsl;
    }

    public final synchronized VersioningFileSystem getSystem(String systemName) {
        return (VersioningFileSystem) names.get(systemName);
    }

    public void addRepositoryListener(VersioningRepositoryListener listener) {
        synchronized (listenerList) {
            listenerList.add(VersioningRepositoryListener.class, listener);
        }
    }

    public void removeRepositoryListener(VersioningRepositoryListener listener) {
        synchronized (listenerList) {
            listenerList.remove(VersioningRepositoryListener.class, listener);
        }
    }

    private void fireVerSystem(VersioningFileSystem vfs, boolean added) {
        VersioningRepositoryListener[] listeners;
        synchronized (listenerList) {
            listeners = (VersioningRepositoryListener[]) listenerList.getListeners(VersioningRepositoryListener.class);
        }
        VersioningRepositoryEvent ev = new VersioningRepositoryEvent(this, vfs, added);
        if (added) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].versioningSystemAdded(ev);
            }
        } else {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].versioningSystemRemoved(ev);
            }
        }
    }

    public String toString() {
        StringBuffer str;
        synchronized (this) {
            str = new StringBuffer("VersioningRepository: names = "+names+", names.size() = "+names.size());
            for (java.util.Iterator it = names.keySet().iterator(); it.hasNext(); ) {
                String systemName = (String) it.next();
                str.append("systemName = "+systemName+", filesystem = "+names.get(systemName));
            }
        }
        return str.toString();
    }

}
