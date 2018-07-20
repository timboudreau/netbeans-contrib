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
package org.netbeans.modules.vcscore.turbo.local;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.ErrorManager;
import org.netbeans.modules.vcscore.turbo.log.Statistics;

import java.util.*;
import java.io.File;

/**
 * Extensible support for file attributes. It delegates
 * to registered {@link FileAttributeProvider}s and if no
 * one matches it uses default attribute database.
 *
 * @author Petr Kuzel
 */
public final class FileAttributeQuery {

    private static Lookup.Result providers;

    private static FileAttributeQuery defaultInstance;

    private List listeners = new ArrayList(100);

    private static Environment env;

    /**
     * Returns default instance.
     */
    public static synchronized FileAttributeQuery getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new FileAttributeQuery();
            defaultInstance.init();
        }
        return defaultInstance;
    }

    private FileAttributeQuery() {
    }

    private synchronized void init() {
        if (providers == null) {
            Lookup.Template t = new Lookup.Template(FileAttributeProvider.class);
            synchronized(FileAttributeQuery.class) {
                if (env == null) env = new Environment();
            }
            providers = env.getLookup().lookup(t);
        }
    }

    /** Tests can set different environment. Must be called before {@link #getDefault}. */
    static synchronized void initEnvironment(Environment environment) {
        assert env == null;
        env = environment;
        providers = null;
    }

    /**
     * Reads given attribute for given fileobject.
     * @param fo identifies source FileObject, never <code>null</code>
     * @param name identifies requested attribute, never <code>null</code>
     * @return attribute value or <code>null</code> if it does not exist.
     */
    public Object readAttribute(FileObject fo, String name) {

        Statistics.attributeRequest();

        // check memory cache

        if (Memory.existsEntry(fo, name)) {
            Object value = Memory.get(fo, name);
            Statistics.memoryHit();
            return value;
        }

        // iterate over providers
        List speculative = new ArrayList(57);
        Object value = loadAttribute(fo, name, speculative);
        Memory.put(fo, name, value != null ? value : Memory.NULL);
        // XXX should fire here?  yes if attribute avalability changes should be
        // dispatched to clients that have not called prepare otherwise NO.

        // refire speculative results, can be optinized later on to fire
        // them lazilly on prepareAttribute or isPrepared calls
        Iterator it = speculative.iterator();
        while (it.hasNext()) {
            Object[] next = (Object[]) it.next();
            FileObject fileObject = (FileObject) next[0];
            String attrName = (String) next[1];
            Object attrValue = next[2];
            assert fileObject != null;
            assert attrName != null;
            fireAttributeChange(fileObject, attrName, attrValue);
        }

        return value;
    }

    private Iterator providers() {
        Collection plugins = providers.allInstances();
        List all = new ArrayList(plugins.size() +1);
        all.addAll(plugins);
        all.add(DefaultFileAttributeProvider.getDefault());
        return all.iterator();
    }

    /**
     * Iterate over providers asking for attribute values
     */
    private Object loadAttribute(FileObject fo, String name, List speculative) {

        FileAttributeProvider provider;
        Iterator it = providers();
        while (it.hasNext()) {
            provider = (FileAttributeProvider) it.next();
            try {
                if (provider.recognizesAttribute(name) && provider.recognizesFileObject(fo)) {
                    FileAttributeProvider.MemoryCache cache = FileAttributeProvider.MemoryCache.getDefault(speculative);
                    Object value = provider.readAttribute(fo, name, cache);
                    Statistics.diskHit();
                    return value;
                }
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable t) {
                // error in provider
                ErrorManager.getDefault().annotate(t, "Error in provider " + provider + ", skipping... "); // NOI18N
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, t);  // XXX in Junit mode writes to stdout ommiting annotation!!!
            }
        }

        return null;
    }

    /**
     * Writes given attribute and notifies all listeners.
     * <p>
     * A client calling this method is reponsible for passing
     * valid attribute value that is accepted by providers.
     * It's not an issue in typical case when the caller
     * is often the provider itself. In other cases the caller
     * must be provider friend and it should be assured by
     * invoking this method using following code snippet:
     * <pre>
     *    boolean success = faq.writeAttribute(fo, name, value);
     *    assert success : "Unexpected attribute[" + name + "] value[" + value + "] denial for " + fo + "!";
     * </pre>
     *
     * @param fo identifies target file object, never <code>null</code>
     * @param name identifies attribute, never <code>null</code>
     * @param value actual attribute value that should be stored, <code>null</code> behaviour
     * is defined specificaly for each attribute, commonly it invalidates the value
     * @return <code>false</code> on write failure if provider denies the value. On I/O error it
     * returns <code>true</code>.
     */
    public boolean writeAttribute(FileObject fo, String name, Object value) {

        if (value != null) {
            Object oldValue = Memory.get(fo, name);
            if (oldValue != null && oldValue.equals(value)) return true;  // XXX assuming provider has the same value, assert it!
        }

        int result = storeAttribute(fo, name, value);
        if (result >= 0) {
            // no one denied keep at least in memory cache
            Memory.put(fo, name, value);
            fireAttributeChange(fo, name, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Stores directly to providers.
     * @return 0 success, -1 contract failure, 1 other failure
     */
    int storeAttribute(FileObject fo, String name, Object value) {
        FileAttributeProvider provider;
        Iterator it = providers();
        while (it.hasNext()) {
            provider = (FileAttributeProvider) it.next();
            try {
                if (provider.recognizesAttribute(name) && provider.recognizesFileObject(fo)) {
                    if (provider.writeAttribute(fo, name, value)) {
                        return 0;
                    } else {
                        // for debugging purposes log which provider rejected defined attribute contract
                        IllegalArgumentException ex = new IllegalArgumentException("Attribute[" + name + "] value rejected by " + provider);
                        ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                        return -1;
                    }
                }
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable t) {
                // error in provider
                ErrorManager.getDefault().annotate(t, "Error in provider " + provider + ", skipping... "); // NOI18N
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, t);
            }
        }
        return 1;
    }

    /**
     * Explicit support for FS.createData & FS.createFolder.
     * It adds speculative entry into memory layer.
     *
     * @deprecated Internal contract: it allows the VCSFS to address <code>FileObject</code> prenatal situations.
     */
    public void writeAttribute(File file, String id, Object value) {
        file = FileUtil.normalizeFile(file);
        // It cannot write attribute down to providers until having FileObject that is used in SPI
        // assuming FileUtil.toFileObject is dangerous here otherwise
        // it was done by caller

        Memory.putSpeculativeEntry(file.getAbsolutePath(), id, value);
    }


    /**
     * Checks attribute instant availability and schedules its loading.
     * @return <code>false</code> if not ready and providers must be consulted. It'll
     * fire event on completion if attribute exists. If <code>true</code> it's
     * ready and stays ready until <code>fo</code> reference released.
     */
    public boolean prepareAttribute(FileObject fo, String name) {

        Statistics.attributeRequest();

        // check memory cache

        if (Memory.existsEntry(fo, name)) {
            Statistics.memoryHit();
            return true;
        }

        // start asynchronous providers queriing
        scheduleLoad(fo, name);
        return false;
    }

    /**
     * Checks attribute instant availability. Note that attribute
     * value may be still <code>null</code>.
     * @return false if not yet computed.
     */
    public boolean isPrepared(FileObject fo, String name) {
        return Memory.existsEntry(fo, name);
    }

    public void addFileAttributeListener(FileAttributeListener l) {
        synchronized(listeners) {
            List copy = new ArrayList(listeners);
            copy.add(l);
            listeners = copy;
        }
    }

    public void removeFileAttributeListener(FileAttributeListener l) {
        synchronized(listeners) {
            List copy = new ArrayList(listeners);
            copy.remove(l);
            listeners = copy;
        }

    }

    protected void fireAttributeChange(FileObject fo, String name, Object value) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            FileAttributeListener next = (FileAttributeListener) it.next();
            next.attributeChange(fo, name, value);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("FileAttributeQuery delegating to:");  // NOI18N
        Iterator it = providers();
        while (it.hasNext()) {
            FileAttributeProvider provider = (FileAttributeProvider) it.next();
            sb.append(" [" + provider + "]");   // NOI18N
        }
        return sb.toString();
    }

    /** Defines binding to external world. Used by tests. */
    static class Environment {
        /** Lookup that serves providers. */
        public Lookup getLookup() {
            return Lookup.getDefault();
        }
    }

    // Background loading ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /** Stop all threads, unregister... */
    public void cancel() {
        synchronized(prepareRequests) {
            if (preparationTask != null) {
                preparationTask.notifyFinish();
                preparationTask = null;
            }
        }
    }


    /** Holds fileobject that were requested for background status retrieval. */
    private final Set prepareRequests = Collections.synchronizedSet(new LinkedHashSet(27));

    private static PreparationTask preparationTask;

    /** Tries to locate meta on disk on failure it forward to repository */
    private void scheduleLoad(FileObject fileObject, String name) {
        synchronized(prepareRequests) {
            if (preparationTask == null) {
                preparationTask = new PreparationTask(prepareRequests);
                RequestProcessor.getDefault().post(preparationTask);
                Statistics.backgroundThread();
            }
            preparationTask.notifyNewRequest(new Request(fileObject, name));
        }
    }

    /** Requests queue entry featuring value based identity. */
    private final static class Request {
        private final FileObject fileObject;
        private final String attribute;

        public Request(FileObject fileObject, String attribute) {
            this.attribute = attribute;
            this.fileObject = fileObject;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Request)) return false;

            final Request request = (Request) o;

            if (attribute != null ? !attribute.equals(request.attribute) : request.attribute != null) return false;
            if (fileObject != null ? !fileObject.equals(request.fileObject) : request.fileObject != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (fileObject != null ? fileObject.hashCode() : 0);
            result = 29 * result + (attribute != null ? attribute.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "Request[fo=" + fileObject + ", attr=" + attribute  + "]";
        }
    }

    /**
     * On background fetches data from reporitory.
     */
    private final class PreparationTask implements Runnable {

        private final Set requests;

        public PreparationTask(Set requests) {
            this.requests = requests;
        }

        public void run() {
            try {
                Thread.currentThread().setName("FAQ Fetcher");  // NOI18N
                while (waitForRequests()) {
                    Request request;
                    synchronized (requests) {
                        request = (Request) requests.iterator().next();
                        requests.remove(request);
                    }
                    FileObject fo = request.fileObject;
                    String name = request.attribute;
                    Object value;
                    boolean fire;
                    FileObject nativeFO = (FileObject) fo.getAttribute("VCS-Native-FileObject");  // NOI18N
                    if (nativeFO != null) fo = nativeFO;
                    if (Memory.existsEntry(fo, name)) {
                        synchronized (fo) {
                            synchronized(Memory.class) {
                                fire = Memory.isLiveEntry(fo)  == false;
                                value = Memory.get(fo, name);
                            }
                        }
                        if (fire) {
                            Statistics.diskHit(); // from our perpective we achieved hit
                        }
                    } else {
                        value = loadAttribute(fo, name, null);
                        // possible thread switch, so atomic fire test must be used
                        synchronized (fo) {
                            synchronized(Memory.class) {
                                fire = Memory.isLiveEntry(fo)  == false;
                                Object oldValue = Memory.get(fo, name);
                                Memory.put(fo, name, value != null ? value : Memory.NULL);
                                fire |= (oldValue != null && !oldValue.equals(value))
                                     || (oldValue == null && value != null);
                            }
                        }
                    }

                    // some one was faster, probably previous disk read that silently fetched whole directory
                    // our contract was to fire event once loading, stick to it. Note that get()
                    // silently populates stable memory area
//                    if (fire) {   ALWAYS because of above loadAttribute(fo, name, null);
                        fireAttributeChange(request.fileObject, name, value);  // notify as soon as available in memory
//                    }

                }
            } catch (InterruptedException ex) {
                synchronized(requests) {
                    // forget about recent requests
                    requests.clear();
                }
            } finally {
                synchronized(requests) {
                    preparationTask = null;
                }
            }
        }

        /**
         * Wait for requests, it no request comes until timeout
         * it kills preparation task.
         */
        private boolean waitForRequests() throws InterruptedException {
            synchronized(requests) {
                if (requests.size() == 0) {
                    requests.wait(123 * 1000);  // 123 sec
                }
                return requests.size() > 0;
            }
        }

        public void notifyNewRequest(Request request) {
            synchronized(requests) {
                if (requests.add(request)) {
                    Statistics.queueSize(requests.size());
                    requests.notify();
                } else {
                    Statistics.duplicate();
                    Statistics.diskHit();
                }
            }
        }
        
        public void notifyFinish() {
            synchronized(requests) {
                requests.clear();
                requests.notify();
            }
        }

        public String toString() {
            return "FileAttributesQuery.PreparationTask queue=[" + requests +"]";  // NOI18N
        }
    }


}
