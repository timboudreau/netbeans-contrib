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
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeListener;
import org.netbeans.modules.vcscore.turbo.log.Statistics;

import javax.swing.*;
import java.util.*;
import java.io.File;

/**
 * Client code main entry point. It's defacto implementation
 * of {@link org.netbeans.modules.vcscore.caching.FileStatusProvider}
 * interface (enhanced by being event source).
 * <p>
 * It enhances {@link org.netbeans.modules.vcscore.turbo.local}
 * contract by ability to synchronously and asynchronously issue
 * remote repository commands and translate their results to callbacks.
 *
 * @author Petr Kuzel
 */
public final class Turbo {

    private static TurboListener[] listeners = new TurboListener[0];

    private static final Turbo SINGLETON = new Turbo();

    private static final FileAttributeQuery FAQ = FileAttributeQuery.getDefault();

    /** If Boolean.TRUE the code is just firing events. */
    private static volatile ThreadLocal justFiring = new ThreadLocal();

    /** FileObjects which FileProperties.Id attribute is just being computed. */
    private static Set prepareRequests = new HashSet();

    static {
        FAQ.addFileAttributeListener(new FAQForwarder());
    }

    private Turbo() {
    }

    /**
     * Request last known status from first level that responds
     * (memory, disk or repository). As side effect it can
     * contact repository and using <tt>status</tt> command
     * load a subset of properties (related to status).
     * <p>
     * <b>Threading:</b> for convenience (or confusion) it protects
     * callers from introducing UI freezes. If called from GUI
     * thread it rather returns <code>null</code> and spawns
     * asynchronous thread than synchronously computing the value.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getMeta(FileObject fileObject) {

        if (fileObject == null) return null;

        Statistics.request();
        FileAttributeQuery faq = FileAttributeQuery.getDefault();

        if (SwingUtilities.isEventDispatchThread()) {
            if (prepareMetaImpl(fileObject)) {
                FileProperties fprops = (FileProperties) faq.readAttribute(fileObject, FileProperties.ID);
                return fprops;
            } else {
                // prepareMetaImpl fires event on asynchronous task completion
            }

        } else {
            FileProperties fprops = (FileProperties) faq.readAttribute(fileObject, FileProperties.ID);
            if (fprops != null) {
                return fprops;
            } else {
                Statistics.request();  // at our level we cannot accept reported disk hit
                return getRepositoryMeta(fileObject);
            }
        }

        return null;
    }

    /**
     * Request fresh status from repository caching it. May block forever.
     * Hence it must not be called from GUI thread.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getRepositoryMeta(FileObject fileObject) {

        assert SwingUtilities.isEventDispatchThread() == false;

        if (fileObject == null) return null;

        FileProperties fprops = Repository.get(fileObject);
        // do not invalidate cached data if repository returns null due to command failure
        if (fprops != null) setMeta(fileObject, fprops);
        return fprops;
    }

    /**
     * Instruct system to load data from slow layers. Data can be actualy delivered
     * sometimes in future. It's possible to attach a listener to get notified.
     *
     * @param fileObject
     * @return <code>true</code> on available data, no listener will fire
     */
    public static boolean prepareMeta(FileObject fileObject) {
        if (fileObject == null) return true;
        Statistics.request();
        return prepareMetaImpl(fileObject);
    }

    /**
     * Asynchronously prepares value at local or repository level.
     */
    private static boolean prepareMetaImpl(FileObject fileObject) {
        synchronized(prepareRequests) {
            if (prepareRequests.add(fileObject)) {
                if (FileAttributeQuery.getDefault().prepareAttribute(fileObject, FileProperties.ID)) {
                    prepareRequests.remove(fileObject);
                    boolean preparedAttr = FileAttributeQuery.getDefault().readAttribute(fileObject, FileProperties.ID) != null;
                    if (!preparedAttr) {
                        Repository.prepareMeta(fileObject);
                    }
                    return preparedAttr;
                } else {
                    // FAQForwarder catches event and eventually pass to repository
                }
            }
            return false;
        }
    }

    /**
     * Request cached status, it never connects to repository. Handled promptly
     * but can return unknown rather often.
     * <p>
     * <b>Threading:</b> for convenience (or confusion) it protects
     * callers from introducing UI freezes. If called from GUI
     * thread it rather returns <code>null</code> and spawns
     * asynchronous thread than synchronously computing the value.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getCachedMeta(FileObject fileObject) {
        if (fileObject == null) return null;

        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        Statistics.request();

        if (SwingUtilities.isEventDispatchThread()) {
            // synchronous acces to meory only,spawning async if neccessary to fetch data from disk
            if (faq.prepareAttribute(fileObject, FileProperties.ID)) {
                return (FileProperties) faq.readAttribute(fileObject, FileProperties.ID);
            } else {
                return null;
            }
        } else {
            // synchronous access to whatever layer
            FileProperties fprops = (FileProperties) faq.readAttribute(fileObject, FileProperties.ID);
            return fprops;
        }
    }


    /**
     * Access memory layer. On missing entry return null
     * and schedule background fetching at DISK level.
     *
     * @return status or <code>null</code> for unknown
     */
    public static FileProperties getMemoryMeta(FileObject fileObject) {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        if (faq.prepareAttribute(fileObject, FileProperties.ID)) {
            return (FileProperties) faq.readAttribute(fileObject, FileProperties.ID);
        }
        return null;
    }

    /**
     * Populate cache by given attributes. Makes them immutable. To
     * be used by commands that can determine fresh properties
     * as side effect of their execution.
     * 
     * @param status new status or <code<null</code> for invalidating
     */
    public static void setMeta(FileObject fileObject, FileProperties status) {
        assert justFiring.get() != Boolean.TRUE : "Setter must not be called from listener!"; // NOI18N
        if (fileObject == null) return;
        if (status != null) {
            status.freeze();
            assert fileObject.isFolder() == status.getName().endsWith("/") : "Bad properties for FileObject "+fileObject+": "+status;
            String name = status.getName();
            if (name.endsWith("/")) name = name.substring(0, name.length() - 1);
            assert fileObject.getNameExt().equals(name): "Bad properties for FileObject "+fileObject+": "+status;
        }
        FileAttributeQuery.getDefault().writeAttribute(fileObject, FileProperties.ID, status);
    }

    /**
     * Populates the cache by given attributes. It tries to locate
     * live fileobject. If it fails it silently stores the status
     * without distributing change event.
     * @param file
     * @deprecated Internal contract: it allows the VCSFS to address <code>FileObject</code> prenatal situations.
     */
    public static void setMeta(File file, FileProperties status) {
        assert justFiring.get() != Boolean.TRUE : "Setter must not be called from listener!"; // NOI18N
        if (status != null) status.freeze();
        FileAttributeQuery.getDefault().writeAttribute(file, FileProperties.ID, status);
    }

    // Events ~~~~~~~~~~~~~~~~~~~~~~

    public void addTurboListener(TurboListener l) {
        synchronized(listeners) {
            List clone = new ArrayList(Arrays.asList(listeners));
            clone.add(l);
            listeners = (TurboListener[]) clone.toArray(new TurboListener[clone.size()]);
        }
    }

    public void removeTurboListener(TurboListener l) {
        synchronized(listeners) {
            List clone = new ArrayList(Arrays.asList(listeners));
            clone.remove(l);
            listeners = (TurboListener[]) clone.toArray(new TurboListener[clone.size()]);
        }
    }

    static void fireTurboEvent(FileObject fileObject, FileProperties status) {
        try {
            justFiring.set(Boolean.TRUE);
            TurboListener[] targets = listeners;
            if (targets.length > 0) {
                TurboEvent e = new TurboEvent(fileObject, status);
                for (int i=0; i<targets.length; i++) {
                    targets[i].turboChanged(e);
                }
            }
        } finally {
            justFiring.set(Boolean.FALSE);
        }
    }

    /**
     * Forwards and pairs prepare requests with events.
     * If an event coresponds to prepare request it
     * triggers Repository preparation task.
     */
    private static class FAQForwarder implements FileAttributeListener {
        public void attributeChange(FileObject fo, String name, Object value) {

            if (FileProperties.ID.equals(name) == false) return;

            // handle unresolved prepare requests

            synchronized(prepareRequests) {
                if (value == null && prepareRequests.contains(fo)) {
                    Statistics.request();  // at our level we cannot accept reported disk hit
                    Repository.prepareMeta(fo);
                }
                prepareRequests.remove(fo);
            }

            // forward the event as TurboEvent
            fireTurboEvent(fo, (FileProperties) value);
        }
    }

    /**
     * You do not need this until you need add event listeners. There are static
     * methods for all other oprations. Listeners must be added on default
     * instance in order to support WeakListeners. WeakListeners
     * are crucial here as Turbo's lifetime (it's static) exceeds
     * lifetime of most potentional listeners.
     */
    public static Turbo singleton() {
        return SINGLETON;
    }

    /**
     * Notifies turbo tha t it's not needed anymore.
     * It comes on IDE shutdown.
     */
    public static void shutdown() {
        Statistics.shutdown();
    }

}
