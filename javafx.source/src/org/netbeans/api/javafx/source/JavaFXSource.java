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

package org.netbeans.api.javafx.source;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.sun.javafx.api.JavafxcTask;
import com.sun.javafx.api.JavafxcTool;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.util.JavacFileManager;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenHierarchyEvent;
import org.netbeans.api.lexer.TokenHierarchyEventType;
import org.netbeans.api.lexer.TokenHierarchyListener;
import javax.tools.JavaFileObject;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

/**
 * A class representing JavaFX source.
 * 
 * @author nenik
 */
public final class JavaFXSource {

    public static enum Phase {
        MODIFIED,
        PARSED,
        ELEMENTS_RESOLVED,
        RESOLVED,   
        UP_TO_DATE;
        
        public boolean lessThan(Phase p) {
            return compareTo(p) < 0;
        }
    };
    public static enum Priority {
        MAX,
        HIGH,
        ABOVE_NORMAL,
        NORMAL,
        BELOW_NORMAL,
        LOW,
        MIN
    };

    JavafxcTask createJavafxcTask() {
        JavafxcTool tool = JavafxcTool.create();
        JavacFileManager fileManager = tool.getStandardFileManager(null, null, Charset.defaultCharset());
        JavaFileObject jfo = SourceFileObject.create(files.iterator().next(), null); // XXX
        JavafxcTask task = tool.getTask(null, fileManager, null, null, Collections.singleton(jfo));
//            Context context = task.getContext();
        
        return task;
  }

    Phase moveToPhase(Phase phase, CompilationController cc, boolean b) throws IOException {
        if (cc.phase.lessThan(Phase.PARSED)) {
                Iterable<? extends CompilationUnitTree> trees = cc.getJavafxcTask().parse();
//                new JavaFileObject[] {currentInfo.jfo});

                System.err.println("Parsed to: ");
                for (CompilationUnitTree cut : trees) {
                    System.err.println("  cut:" + cut);
                }
                
                /*                assert trees != null : "Did not parse anything";        //NOI18N
                Iterator<? extends CompilationUnitTree> it = trees.iterator();
                assert it.hasNext();
                CompilationUnitTree unit = it.next();
                currentInfo.setCompilationUnit(unit);
*/
        }
        return phase;
    }

    private static Map<FileObject, Reference<JavaFXSource>> file2Source = new WeakHashMap<FileObject, Reference<JavaFXSource>>();
    private static final Logger LOGGER = Logger.getLogger(JavaFXSource.class.getName());
    private final ClasspathInfo cpInfo;
    private final Collection<? extends FileObject> files;
    
    private JavaFXSource(ClasspathInfo cpInfo, Collection<? extends FileObject> files) throws IOException {
        this.cpInfo = cpInfo;
        this.files = Collections.unmodifiableList(new ArrayList<FileObject>(files));   //Create a defensive copy, prevent modification
    }
    
    
    /**
     * Returns a {@link JavaFXSource} instance associated with given
     * {@link org.openide.filesystems.FileObject}.
     * It returns null if the file doesn't represent JavaFX source file.
     * 
     * @param fileObject for which the {@link JavaFXSource} should be found/created.
     * @return {@link JavaFXSource} or null
     * @throws {@link IllegalArgumentException} if fileObject is null
     */
    public static JavaFXSource forFileObject(FileObject fileObject) throws IllegalArgumentException {
        if (fileObject == null) {
            throw new IllegalArgumentException ("fileObject == null");  //NOI18N
        }
        if (!fileObject.isValid()) {
            return null;
        }

        try {
            if (   fileObject.getFileSystem().isDefault()
                && fileObject.getAttribute("javax.script.ScriptEngine") != null
                && fileObject.getAttribute("template") == Boolean.TRUE) {
                return null;
            }
            DataObject od = DataObject.find(fileObject);
            
            EditorCookie ec = od.getLookup().lookup(EditorCookie.class);           
        } catch (FileStateInvalidException ex) {
            LOGGER.log(Level.FINE, null, ex);
            return null;
        } catch (DataObjectNotFoundException ex) {
            LOGGER.log(Level.FINE, null, ex);
            return null;
        }
        
        Reference<JavaFXSource> ref = file2Source.get(fileObject);
        JavaFXSource source = ref != null ? ref.get() : null;
        if (source == null) {
            if (!"text/x-fx".equals(FileUtil.getMIMEType(fileObject)) && !"fx".equals(fileObject.getExt())) {  //NOI18N
                return null;
            }
            source = create(ClasspathInfo.create(fileObject), Collections.singletonList(fileObject));
            file2Source.put(fileObject, new WeakReference<JavaFXSource>(source));
        }
        return source;
    }

    private static JavaFXSource create(final ClasspathInfo cpInfo, final Collection<? extends FileObject> files) throws IllegalArgumentException {
        try {
            return new JavaFXSource(cpInfo, files);
        } catch (DataObjectNotFoundException donf) {
            Logger.getLogger("global").warning("Ignoring non existent file: " + FileUtil.getFileDisplayName(donf.getFileObject()));     //NOI18N
        } catch (IOException ex) {            
            Exceptions.printStackTrace(ex);
        }        
        return null;
    }

    public void runUserActionTask( final Task<CompilationInfo> task, final boolean shared) throws IOException {
        if (task == null) {
            throw new IllegalArgumentException ("Task cannot be null");     //NOI18N
        }

        // XXX: check access an threading
        
        if (this.files.size()<=1) {                        
            // XXX: cancel pending tasks

            CompilationInfo currentInfo = null;
            // XXX: validity check
            final CompilationController clientController = createCurrentInfo (this, null);
            try {
                task.run(clientController);
            } catch (Exception ex) {
                // XXX better handling
                Exceptions.printStackTrace(ex);
            } finally {
                if (shared) {
                    clientController.invalidate();
                }
            }
        }
    }

    private static CompilationController createCurrentInfo (final JavaFXSource js, final String javafxc) throws IOException {                
        CompilationController info = new CompilationController(js);//js, binding, javac);
        return info;
    }

    /** Adds a task to given compilation phase. The tasks will run sequentially by
     * priority after given phase is reached.
     * @see CancellableTask for information about implementation requirements 
     * @task The task to run.
     * @phase In which phase should the task run
     * @priority Priority of the task.
     */
    void addPhaseCompletionTask( CancellableTask<CompilationInfo> task, Phase phase, Priority priority ) throws IOException {
        if (task == null) {
            throw new IllegalArgumentException ("Task cannot be null");     //NOI18N
        }
        if (phase == null || phase == Phase.MODIFIED) { 
            throw new IllegalArgumentException (String.format("The %s is not a legal value of phase",phase));   //NOI18N
        }
        if (priority == null) {
            throw new IllegalArgumentException ("The priority cannot be null");    //NOI18N
        }
        final String taskClassName = task.getClass().getName();
        if (excludedTasks != null && excludedTasks.matcher(taskClassName).matches()) {
            if (includedTasks == null || !includedTasks.matcher(taskClassName).matches())
            return;
        }        
        handleAddRequest (new Request (task, this, phase, priority, true));
    }
    
    /** Removes the task from the phase queue.
     * @task The task to remove.
     */
    void removePhaseCompletionTask( CancellableTask<CompilationInfo> task ) {
        final String taskClassName = task.getClass().getName();
        if (excludedTasks != null && excludedTasks.matcher(taskClassName).matches()) {
            if (includedTasks == null || !includedTasks.matcher(taskClassName).matches()) {
                return;
            }
        }
        synchronized (INTERNAL_LOCK) {
            toRemove.add (task);
            Collection<Request> rqs = finishedRequests.get(this);
            if (rqs != null) {
                for (Iterator<Request> it = rqs.iterator(); it.hasNext(); ) {
                    Request rq = it.next();
                    if (rq.task == task) {
                        it.remove();
                    }
                }
            }
        }
    }
    
    /**Rerun the task in case it was already run. Does nothing if the task was not already run.
     *
     * @task to reschedule
     */
    void rescheduleTask(CancellableTask<CompilationInfo> task) {
        synchronized (INTERNAL_LOCK) {
            JavaFXSource.Request request = this.currentRequest.getTaskToCancel (task);
            if ( request == null) {                
out:            for (Iterator<Collection<Request>> it = finishedRequests.values().iterator(); it.hasNext();) {
                    Collection<Request> cr = it.next ();
                    for (Iterator<Request> it2 = cr.iterator(); it2.hasNext();) {
                        Request fr = it2.next();
                        if (task == fr.task) {
                            it2.remove();
                            JavaFXSource.requests.add(fr);
                            if (cr.size()==0) {
                                it.remove();
                            }
                            break out;
                        }
                    }
                }
            }
            else {
                currentRequest.cancelCompleted(request);
            }
        }        
    }

    private int flags = 0;   

    private static final int INVALID = 1;
    
    private static final Pattern excludedTasks;
    private static final Pattern includedTasks;
    private static final Object INTERNAL_LOCK = new Object();
    private final static PriorityBlockingQueue<Request> requests = new PriorityBlockingQueue<Request> (10, new RequestComparator());
    private final static Map<JavaFXSource,Collection<Request>> finishedRequests = new WeakHashMap<JavaFXSource,Collection<Request>>();
    private final static Map<JavaFXSource,Collection<Request>> waitingRequests = new WeakHashMap<JavaFXSource,Collection<Request>>();
    private final static Collection<CancellableTask> toRemove = new LinkedList<CancellableTask> ();
    private final static SingleThreadFactory factory = new SingleThreadFactory ();
    private final static CurrentRequestReference currentRequest = new CurrentRequestReference ();
//    private final static EditorRegistryListener editorRegistryListener = new EditorRegistryListener ();
//    private final static List<DeferredTask> todo = Collections.synchronizedList(new LinkedList<DeferredTask>());    
//    //Only single thread can operate on the single javac
    private final static ReentrantLock javacLock = new ReentrantLock (true);
    
    private static class Request {
        private final CancellableTask<? extends CompilationInfo> task;
        private final JavaFXSource JavaFXSource;        //XXX: Maybe week, depends on the semantics
        private final Phase phase;
        private final Priority priority;
        private final boolean reschedule;
        
        public Request (final CancellableTask<? extends CompilationInfo> task, final JavaFXSource JavaFXSource,
            final Phase phase, final Priority priority, final boolean reschedule) {
            assert task != null;
            this.task = task;
            this.JavaFXSource = JavaFXSource;
            this.phase = phase;
            this.priority = priority;
            this.reschedule = reschedule;
        }
        
        public @Override String toString () {            
            if (reschedule) {
                return String.format("Periodic request for phase: %s with priority: %s to perform: %s", phase.name(), priority, task.toString());   //NOI18N
            }
            else {
                return String.format("One time request for phase: %s with priority: %s to perform: %s", phase != null ? phase.name() : "<null>", priority, task.toString());   //NOI18N
            }
        }
        
        public @Override int hashCode () {
            return this.priority.ordinal();
        }
        
        public @Override boolean equals (Object other) {
            if (other instanceof Request) {
                Request otherRequest = (Request) other;
                return priority == otherRequest.priority
                    && reschedule == otherRequest.reschedule
                    && (phase == null ? otherRequest.phase == null : phase.equals (otherRequest.phase))
                    && task.equals(otherRequest.task);                       
            }
            else {
                return false;
            }
        }        
    }
    
    private static class RequestComparator implements Comparator<Request> {
        public int compare (Request r1, Request r2) {
            assert r1 != null && r2 != null;
            return r1.priority.compareTo (r2.priority);
        }
    }
    private static void handleAddRequest (final Request nr) {
        assert nr != null;
        //Issue #102073 - removed running task which is readded is not performed
        synchronized (INTERNAL_LOCK) {            
            toRemove.remove(nr.task);
            requests.add (nr);
        }
        JavaFXSource.Request request = currentRequest.getTaskToCancel(nr.priority);
        try {
            if (request != null) {
                request.task.cancel();
            }
        } finally {
            currentRequest.cancelCompleted(request);
        }
    }
    /**
     *  Only encapsulates current request. May be trasformed into 
     *  JavaFXSource private static methods, but it may be less readable.
     */
    private static final class CurrentRequestReference {                        
        
        private static JavaFXSource.Request DUMMY_RQ = new JavaFXSource.Request (new CancellableTask<CompilationInfo>() { public void cancel (){}; public void run (CompilationInfo info){}},null,null,null,false);
        
        private JavaFXSource.Request reference;
        private JavaFXSource.Request canceledReference;
        private long cancelTime;
        private final AtomicBoolean canceled;
        private boolean mayCancelJavac;
        
        CurrentRequestReference () {
            this.canceled = new AtomicBoolean();
        }
        
        boolean setCurrentTask (JavaFXSource.Request reference) throws InterruptedException {
            boolean result = false;
            synchronized (INTERNAL_LOCK) {
                while (this.canceledReference!=null) {
                    INTERNAL_LOCK.wait();
                }
                result = this.canceled.getAndSet(false);
                this.mayCancelJavac = false;
                this.cancelTime = 0;
                this.reference = reference;                
            }
            return result;
        }
        
        /**
         * Prevents race-condition in runWhenScanFinished. This method may be called only from
         * the Java-Source-Worker-Thread right after the initial scan finished. The problem was
         * that the task was added into the todo after the todo was drained into the list of pending
         * tasks but the getTaskToCancel thought that the task is still the RepositoryUpdater. So the
         * Java-Source-Worker-Thread has to clean the task after calling RU.run but before draining the
         * pending tasks into the array, it cannot use setCurrentTaks (null) since it is under javac lock
         * and the setCurrentTaks methods may block the caller thread => deadlock.
         */ 
        void clearCurrentTask () {
            synchronized (INTERNAL_LOCK) {
                this.reference = null;
            }
        }
        
        JavaFXSource.Request getTaskToCancel (final Priority priority) {
            JavaFXSource.Request request = null;
            if (!factory.isDispatchThread(Thread.currentThread())) {
                synchronized (INTERNAL_LOCK) {
                    if (this.reference != null && priority.compareTo(this.reference.priority) < 0) {
                        assert this.canceledReference == null;
                        request = this.reference;
                        this.canceledReference = request;
                        this.reference = null;
                        this.canceled.set(true);                    
                        this.cancelTime = System.currentTimeMillis();
                    }
                }
            }
            return request;
        }
        
        JavaFXSource.Request getTaskToCancel (final boolean mayCancelJavac) {
            JavaFXSource.Request request = null;
            if (!factory.isDispatchThread(Thread.currentThread())) {
                synchronized (INTERNAL_LOCK) {
                    if (this.reference != null) {
                        assert this.canceledReference == null;
                        request = this.reference;
                        this.canceledReference = request;
                        this.reference = null;
                        this.canceled.set(true);
                        this.mayCancelJavac = mayCancelJavac;
                        this.cancelTime = System.currentTimeMillis();
                    }
                    else if (canceledReference == null)  {
                        request = DUMMY_RQ;
                        this.canceledReference = request;
                        this.mayCancelJavac = mayCancelJavac;
                        this.cancelTime = System.currentTimeMillis();
                    }
                }
            }
            return request;
        }
        
        JavaFXSource.Request getTaskToCancel (final CancellableTask task) {
            JavaFXSource.Request request = null;
            if (!factory.isDispatchThread(Thread.currentThread())) {
                synchronized (INTERNAL_LOCK) {
                    if (this.reference != null && task == this.reference.task) {
                        assert this.canceledReference == null;
                        request = this.reference;
                        this.canceledReference = request;
                        this.reference = null;
                        this.canceled.set(true);
                    }
                }
            }
            return request;
        }
        
        JavaFXSource.Request getTaskToCancel () {
            JavaFXSource.Request request = null;
            if (!factory.isDispatchThread(Thread.currentThread())) {                
                synchronized (INTERNAL_LOCK) {
                     request = this.reference;
                    if (request != null) {
                        assert this.canceledReference == null;
                        this.canceledReference = request;
                        this.reference = null;
                        this.canceled.set(true);
                        this.cancelTime = System.currentTimeMillis();
                    }
                }
            }
            return request;
        }
        
        /**
         * Called by {@link JavaFXSource#runWhenScanFinished} to find out which
         * task is currently running. Returns true when the running task in backgroud
         * scan otherwise returns false. The caller is expected not to call cancel on
         * the background scanner, so this method do not reset reference and do not set
         * cancelled flag when running task is background scan. But it sets the canceledReference
         * to prevent java source thread to dispatch next queued task.
         * @param request is filled by currently running task or null when there is no running task.
         * @return true when running task is background scan
         */
        boolean getUserTaskToCancel (JavaFXSource.Request[] request) {
            assert request != null;
            assert request.length == 1;
            boolean result = false;
            if (!factory.isDispatchThread(Thread.currentThread())) {                
                synchronized (INTERNAL_LOCK) {
                     request[0] = this.reference;
                    if (request[0] != null) {
                        result = request[0].phase == null;
                        assert this.canceledReference == null;                        
                        if (!result) {
                            this.canceledReference = request[0];
                            this.reference = null;                        
                        }
                        this.canceled.set(result);
                        this.cancelTime = System.currentTimeMillis();
                    }
                }
            }
            return result;
        }
        
        boolean isCanceled () {
            synchronized (INTERNAL_LOCK) {
                return this.canceled.get();
            }
        }
        
        AtomicBoolean getCanceledRef () {
            return this.canceled;
        }
        
        boolean isInterruptJavac () {
            synchronized (INTERNAL_LOCK) {
                boolean ret = this.mayCancelJavac && 
                        this.canceledReference != null &&
                        this.canceledReference.JavaFXSource != null &&
                        (this.canceledReference.JavaFXSource.flags & INVALID) != 0;
                return ret;
            }
        }
        
        long getCancelTime () {
            synchronized (INTERNAL_LOCK) {
                return this.cancelTime;
            }
        }
        
        void cancelCompleted (final JavaFXSource.Request request) {
            if (request != null) {
                synchronized (INTERNAL_LOCK) {
                    assert request == this.canceledReference;
                    this.canceledReference = null;
                    INTERNAL_LOCK.notify();
                }
            }
        }
    }
    
    private static class SingleThreadFactory implements ThreadFactory {
        
        private Thread t;
        
        public Thread newThread(Runnable r) {
            assert this.t == null;
            this.t = new Thread (r,"Java Source Worker Thread");     //NOI18N
            return this.t;
        }
        
        public boolean isDispatchThread (Thread t) {
            assert t != null;
            return this.t == t;
        }
    }
/**
     * Init the maps
     */
    static {
//        phase2Message.put (Phase.PARSED,"Parsed");                              //NOI18N
//        phase2Message.put (Phase.ELEMENTS_RESOLVED,"Signatures Attributed");    //NOI18N
//        phase2Message.put (Phase.RESOLVED, "Attributed");                       //NOI18N
        
        //Initialize the excludedTasks
        Pattern _excludedTasks = null;
        try {
            String excludedValue= System.getProperty("org.netbeans.api.java.source.JavaFXSource.excludedTasks");      //NOI18N
            if (excludedValue != null) {
                _excludedTasks = Pattern.compile(excludedValue);
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        excludedTasks = _excludedTasks;
        Pattern _includedTasks = null;
        try {
            String includedValue= System.getProperty("org.netbeans.api.java.source.JavaFXSource.includedTasks");      //NOI18N
            if (includedValue != null) {
                _includedTasks = Pattern.compile(includedValue);
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        includedTasks = _includedTasks;
    }   
}
