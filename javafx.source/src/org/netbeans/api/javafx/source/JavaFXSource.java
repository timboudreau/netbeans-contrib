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

import com.sun.javafx.api.JavafxcTask;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.util.JavacFileManager;
import com.sun.tools.javafx.api.JavafxcTool;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.tools.JavaFileObject;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenHierarchyEvent;
import org.netbeans.api.lexer.TokenHierarchyListener;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.netbeans.lib.editor.util.swing.PositionRegion;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.util.RequestProcessor;
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
        JavaFileObject jfo = (JavaFileObject) SourceFileObject.create(files.iterator().next(), null); // XXX
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
        
        this.reparseDelay = REPARSE_DELAY;
        this.fileChangeListener = new FileChangeListenerImpl ();
        boolean multipleSources = this.files.size() > 1, filterAssigned = false;
        for (Iterator<? extends FileObject> it = this.files.iterator(); it.hasNext();) {
            FileObject file = it.next();
            try {
                Logger.getLogger("TIMER").log(Level.FINE, "JavaSource",
                    new Object[] {file, this});
                if (!multipleSources) {
                    file.addFileChangeListener(FileUtil.weakFileChangeListener(this.fileChangeListener,file));
                    this.assignDocumentListener(DataObject.find(file));
                    this.dataObjectListener = new DataObjectListener(file);                                        
                }
            } catch (DataObjectNotFoundException donf) {
                if (multipleSources) {
                    LOGGER.warning("Ignoring non existent file: " + FileUtil.getFileDisplayName(file));     //NOI18N
                    it.remove();
                }
                else {
                    throw donf;
                }
            }
        }
        this.cpInfo.addChangeListener(WeakListeners.change(this.listener, this.cpInfo));
        
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

    public void runUserActionTask( final Task<? super CompilationController> task, final boolean shared) throws IOException {
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
            JavaFXSource.Request request = currentRequest.getTaskToCancel (task);
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
    
    /**
     * Not synchronized, only sets the atomic state and clears the listeners
     *
     */
    private void resetStateImpl() {
        if (!k24) {
            Request r = rst.getAndSet(null);
            currentRequest.cancelCompleted(r);
            synchronized (INTERNAL_LOCK) {
                boolean reschedule, updateIndex;
                synchronized (this) {
                    reschedule = (this.flags & RESCHEDULE_FINISHED_TASKS) != 0;
                    updateIndex = (this.flags & UPDATE_INDEX) != 0;
                    this.flags&=~(RESCHEDULE_FINISHED_TASKS|CHANGE_EXPECTED|UPDATE_INDEX);
                }            
                Collection<Request> cr;            
                if (reschedule) {                
                    if ((cr=JavaFXSource.finishedRequests.remove(this)) != null && cr.size()>0)  {
                        JavaFXSource.requests.addAll(cr);
                    }
                }
                if ((cr=JavaFXSource.waitingRequests.remove(this)) != null && cr.size()>0)  {
                    JavaFXSource.requests.addAll(cr);
                }
            }          
        }
    }
    
    
    private static final RequestProcessor RP = new RequestProcessor ("JavaSource-event-collector",1);       //NOI18N
    
    private final RequestProcessor.Task resetTask = RP.create(new Runnable() {
        public void run() {
            resetStateImpl();
        }
    });

    private void resetState(boolean invalidate, boolean updateIndex) {
        boolean invalid;
        synchronized (this) {
            invalid = (this.flags & INVALID) != 0;
            this.flags|=CHANGE_EXPECTED;
            if (invalidate) {
                this.flags|=(INVALID|RESCHEDULE_FINISHED_TASKS);
                if (this.currentInfo != null) {
//                    this.currentInfo.setChangedMethod (changedMethod);
                }
            }
            if (updateIndex) {
                this.flags|=UPDATE_INDEX;
            }            
        }
        Request r = currentRequest.getTaskToCancel (invalidate);
        if (r != null) {
            r.task.cancel();
            Request oldR = rst.getAndSet(r);
            assert oldR == null;
        }
        if (!k24) {
            resetTask.schedule(reparseDelay);
        }
    }
    private final AtomicReference<Request> rst = new AtomicReference<JavaFXSource.Request> ();
    private volatile boolean k24;

    private int flags = 0;   

    private static final int INVALID = 1;
    private static final int CHANGE_EXPECTED = INVALID<<1;
    private static final int RESCHEDULE_FINISHED_TASKS = CHANGE_EXPECTED<<1;
    private static final int UPDATE_INDEX = RESCHEDULE_FINISHED_TASKS<<1;
    private static final int IS_CLASS_FILE = UPDATE_INDEX<<1;
    
    private static final int REPARSE_DELAY = 500;
    private int reparseDelay;
    
    private static final Pattern excludedTasks;
    private static final Pattern includedTasks;
    private static final Object INTERNAL_LOCK = new Object();
    private final static PriorityBlockingQueue<Request> requests = new PriorityBlockingQueue<Request> (10, new RequestComparator());
    private final static Map<JavaFXSource,Collection<Request>> finishedRequests = new WeakHashMap<JavaFXSource,Collection<Request>>();
    private final static Map<JavaFXSource,Collection<Request>> waitingRequests = new WeakHashMap<JavaFXSource,Collection<Request>>();
    private final static Collection<CancellableTask> toRemove = new LinkedList<CancellableTask> ();
    private final static SingleThreadFactory factory = new SingleThreadFactory ();
    private final static CurrentRequestReference currentRequest = new CurrentRequestReference ();
    private final static EditorRegistryListener editorRegistryListener = new EditorRegistryListener ();
    private final static List<DeferredTask> todo = Collections.synchronizedList(new LinkedList<DeferredTask>());    
//    //Only single thread can operate on the single javac
    private final static ReentrantLock javacLock = new ReentrantLock (true);
    
    private final FileChangeListener fileChangeListener;
    private DocListener listener;
    private DataObjectListener dataObjectListener;
    
    private CompilationController currentInfo;
    private java.util.Stack<CompilationInfo> infoStack = new java.util.Stack<CompilationInfo> ();
    
    static {
        Executors.newSingleThreadExecutor(factory).submit (new CompilationJob());
    }  
    private void assignDocumentListener(final DataObject od) throws IOException {
        EditorCookie.Observable ec = od.getCookie(EditorCookie.Observable.class);            
        if (ec != null) {
            this.listener = new DocListener (ec);
        } else {
            LOGGER.log(Level.WARNING,String.format("File: %s has no EditorCookie.Observable", FileUtil.getFileDisplayName (od.getPrimaryFile())));      //NOI18N
        }
    }
    
    
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
    
    private static class CompilationJob implements Runnable {        
        
        @SuppressWarnings ("unchecked") //NOI18N
        public void run () {
            try {
                while (true) {                   
                    try {
                        synchronized (INTERNAL_LOCK) {
                            //Clean up toRemove tasks
                            if (!toRemove.isEmpty()) {
                                for (Iterator<Collection<Request>> it = finishedRequests.values().iterator(); it.hasNext();) {
                                    Collection<Request> cr = it.next ();
                                    for (Iterator<Request> it2 = cr.iterator(); it2.hasNext();) {
                                        Request fr = it2.next();
                                        if (toRemove.remove(fr.task)) {
                                            it2.remove();
                                        }
                                    }
                                    if (cr.size()==0) {
                                        it.remove();
                                    }
                                }
                            }
                        }
                        Request r = JavaFXSource.requests.poll(2,TimeUnit.SECONDS);
                        if (r != null) {
                            currentRequest.setCurrentTask(r);
                            try {                            
                                JavaFXSource js = r.JavaFXSource;
                                if (js == null) {
                                    assert r.phase == null;
                                    assert r.reschedule == false;
                                    javacLock.lock ();
                                    try {
                                        try {
                                            r.task.run (null);
                                        } finally {
                                            currentRequest.clearCurrentTask();
                                            boolean cancelled = requests.contains(r);
                                            if (!cancelled) {
                                                DeferredTask[] _todo;
                                                synchronized (todo) {
                                                    _todo = todo.toArray(new DeferredTask[todo.size()]);
                                                    todo.clear();
                                                }
                                                for (DeferredTask rq : _todo) {
                                                    try {
                                                        rq.js.runUserActionTask(rq.task, rq.shared);                                                        
                                                    } finally {
                                                        rq.sync.taskFinished();
                                                    }
                                                }
                                            }
                                        }
                                    } catch (RuntimeException re) {
                                        Exceptions.printStackTrace(re);
                                    }
                                    finally {
                                        javacLock.unlock();
                                    }
                                }
                                else {
                                    assert js.files.size() <= 1;
                                    boolean jsInvalid;
                                    CompilationController ci;
                                    synchronized (INTERNAL_LOCK) {
                                        //jl:what does this comment mean?
                                        //Not only the finishedRequests for the current request.JavaFXSource should be cleaned,
                                        //it will cause a starvation
                                        if (toRemove.remove(r.task)) {
                                            continue;
                                        }
                                        synchronized (js) {                     
                                            boolean changeExpected = (js.flags & CHANGE_EXPECTED) != 0;
                                            if (changeExpected) {
                                                //Skeep the task, another invalidation is comming
                                                Collection<Request> rc = JavaFXSource.waitingRequests.get (r.JavaFXSource);
                                                if (rc == null) {
                                                    rc = new LinkedList<Request> ();
                                                    JavaFXSource.waitingRequests.put (r.JavaFXSource, rc);
                                                }
                                                rc.add(r);
                                                continue;
                                            }
                                            jsInvalid = js.currentInfo == null || (js.flags & INVALID)!=0;
                                            ci = js.currentInfo;
                                            
                                        }
                                    }
                                    try {
                                        //createCurrentInfo has to be out of synchronized block, it aquires an editor lock                                    
                                        if (jsInvalid) {
                                            ci = createCurrentInfo (js, null);
                                            synchronized (js) {
                                                if (js.currentInfo == null || (js.flags & INVALID) != 0) {
                                                    js.currentInfo = ci;
                                                    js.flags &= ~INVALID;
                                                }
                                                else {
                                                    ci = js.currentInfo;
                                                }
                                            }
                                        }                                    
                                        assert ci != null;
                                        javacLock.lock();
                                        try {
                                            boolean shouldCall;
                                            try {
                                                final Phase phase = js.moveToPhase (r.phase, ci, true);
                                                shouldCall = phase.compareTo(r.phase)>=0;
                                            } finally {
                                            }                                            
                                            if (shouldCall) {
                                                synchronized (js) {
                                                    shouldCall &= (js.flags & INVALID)==0;
                                                }
                                                if (shouldCall) {
                                                    //The state (or greater) was reached and document was not modified during moveToPhase
                                                    try {
                                                        final long startTime = System.currentTimeMillis();
                                                        final CompilationInfo clientCi = new CompilationInfo(js);
                                                        try {
                                                            ((CancellableTask<CompilationInfo>)r.task).run (clientCi); //XXX: How to do it in save way?
                                                        } finally {
//                                                            clientCi.invalidate();
                                                        }
                                                        final long endTime = System.currentTimeMillis();
                                                        if (LOGGER.isLoggable(Level.FINEST)) {
                                                            LOGGER.finest(String.format("executed task: %s in %d ms.",  //NOI18N
                                                                r.task.getClass().toString(), (endTime-startTime)));
                                                        }
                                                    } catch (Exception re) {
                                                        Exceptions.printStackTrace (re);
                                                    }
                                                }
                                            }
                                        } finally {
                                            javacLock.unlock();
                                        }

                                        if (r.reschedule) {                                            
                                            synchronized (INTERNAL_LOCK) {
                                                boolean canceled = currentRequest.setCurrentTask(null);
                                                synchronized (js) {
                                                    if ((js.flags & INVALID)!=0 || canceled) {
                                                        //The JavaFXSource was changed or canceled rechedule it now
                                                        JavaFXSource.requests.add(r);
                                                    }
                                                    else {
                                                        //Up to date JavaFXSource add it to the finishedRequests
                                                        Collection<Request> rc = JavaFXSource.finishedRequests.get (r.JavaFXSource);
                                                        if (rc == null) {
                                                            rc = new LinkedList<Request> ();
                                                            JavaFXSource.finishedRequests.put (r.JavaFXSource, rc);
                                                        }
                                                        rc.add(r);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (IOException invalidFile) {
                                        //Ideally the requests should be removed by JavaFXSourceTaskFactory and task should be put to finishedRequests,
                                        //but the reality is different, the task cannot be put to finished request because of possible memory leak
                                    }
                                }
                            } finally {
                                currentRequest.setCurrentTask(null);                   
                            }
                        } 
                    } catch (Throwable e) {
                        if (e instanceof InterruptedException) {
                            throw (InterruptedException)e;
                        }
                        else if (e instanceof ThreadDeath) {
                            throw (ThreadDeath)e;
                        }
                        else {
                            Exceptions.printStackTrace(e);
                        }
                    }                    
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                // stop the service.
            }
        }                        
    }
    
    private class DocListener implements PropertyChangeListener, ChangeListener, TokenHierarchyListener {
        
        private EditorCookie.Observable ec;
        private TokenHierarchyListener lexListener;
        private volatile Document document;
        
        public DocListener (EditorCookie.Observable ec) {
            assert ec != null;
            this.ec = ec;
            this.ec.addPropertyChangeListener(WeakListeners.propertyChange(this, this.ec));
            Document doc = ec.getDocument();            
            if (doc != null) {
                TokenHierarchy th = TokenHierarchy.get(doc);
                th.addTokenHierarchyListener(lexListener = WeakListeners.create(TokenHierarchyListener.class, this,th));
                document = doc;
            }            
        }
                                   
        public void propertyChange(PropertyChangeEvent evt) {
            if (EditorCookie.Observable.PROP_DOCUMENT.equals(evt.getPropertyName())) {
                Object old = evt.getOldValue();                
                if (old instanceof Document && lexListener != null) {
                    TokenHierarchy th = TokenHierarchy.get((Document) old);
                    th.removeTokenHierarchyListener(lexListener);
                    lexListener = null;
                }                
                Document doc = ec.getDocument();                
                if (doc != null) {
                    TokenHierarchy th = TokenHierarchy.get(doc);
                    th.addTokenHierarchyListener(lexListener = WeakListeners.create(TokenHierarchyListener.class, this,th));
                    this.document = doc;    //set before rescheduling task to avoid race condition
                    resetState(true, false);
                }
                else {
                    //reset document
                    this.document = doc;
                }
            }
        }

        public void stateChanged(ChangeEvent e) {
            JavaFXSource.this.resetState(true, false);
        }
        
        public void tokenHierarchyChanged(TokenHierarchyEvent evt) {
            JavaFXSource.this.resetState(true, true);
        }        
    }
    
    private static class EditorRegistryListener implements CaretListener, PropertyChangeListener {
                        
        private Request request;
        private JTextComponent lastEditor;
        
        public EditorRegistryListener () {
            EditorRegistry.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    editorRegistryChanged();
                }
            });
            editorRegistryChanged();
        }
                
        public void editorRegistryChanged() {
            final JTextComponent editor = EditorRegistry.lastFocusedComponent();
            if (lastEditor != editor) {
                if (lastEditor != null) {
                    lastEditor.removeCaretListener(this);
                    lastEditor.removePropertyChangeListener(this);
                    final Document doc = lastEditor.getDocument();
                    JavaFXSource js = null;
                    if (doc != null) {
                        js = forDocument(doc);
                    }
                    if (js != null) {
                        js.k24 = false;
                    }                   
                }
                lastEditor = editor;
                if (lastEditor != null) {                    
                    lastEditor.addCaretListener(this);
                    lastEditor.addPropertyChangeListener(this);
                }
            }
        }
        
        public void caretUpdate(CaretEvent event) {
            if (lastEditor != null) {
                Document doc = lastEditor.getDocument();
                if (doc != null) {
                    JavaFXSource js = forDocument(doc);
                    if (js != null) {
                        js.resetState(false, false);
                    }
                }
            }
        }

        public void propertyChange(final PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if ("completion-active".equals(propName)) {
                JavaFXSource js = null;
                final Document doc = lastEditor.getDocument();
                if (doc != null) {
                    js = forDocument(doc);
                }
                if (js != null) {
                    Object rawValue = evt.getNewValue();
                    assert rawValue instanceof Boolean;
                    if (rawValue instanceof Boolean) {
                        final boolean value = (Boolean)rawValue;
                        if (value) {
                            assert this.request == null;
                            this.request = currentRequest.getTaskToCancel(false);
                            if (this.request != null) {
                                this.request.task.cancel();
                            }
                            js.k24 = true;
                        }
                        else {                    
                            Request _request = this.request;
                            this.request = null;                            
                            js.k24 = false;
                            js.resetTask.schedule(js.reparseDelay);
                            currentRequest.cancelCompleted(_request);
                        }
                    }
                }
            }
        }
        
    }
    
    private class FileChangeListenerImpl extends FileChangeAdapter {                
        
        public @Override void fileChanged(final FileEvent fe) {
            JavaFXSource.this.resetState(true, false);
        }        

        public @Override void fileRenamed(FileRenameEvent fe) {
            JavaFXSource.this.resetState(true, false);
        }        
    }
    
    private final class DataObjectListener implements PropertyChangeListener {
        
        private DataObject dobj;
        private final FileObject fobj;
        private PropertyChangeListener wlistener;
        
        public DataObjectListener(FileObject fo) throws DataObjectNotFoundException {
            this.fobj = fo;
            this.dobj = DataObject.find(fo);
            wlistener = WeakListeners.propertyChange(this, dobj);
            this.dobj.addPropertyChangeListener(wlistener);
        }
        
        public void propertyChange(PropertyChangeEvent pce) {
            DataObject invalidDO = (DataObject) pce.getSource();
            if (invalidDO != dobj)
                return;
            if (DataObject.PROP_VALID.equals(pce.getPropertyName())) {
                handleInvalidDataObject(invalidDO);
            } else if (pce.getPropertyName() == null && !dobj.isValid()) {
                handleInvalidDataObject(invalidDO);
            }
        }
        
        private void handleInvalidDataObject(final DataObject invalidDO) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    handleInvalidDataObjectImpl(invalidDO);
                }
            });
        }
        
        private void handleInvalidDataObjectImpl(DataObject invalidDO) {
            invalidDO.removePropertyChangeListener(wlistener);
            if (fobj.isValid()) {
                // file object still exists try to find new data object
                try {
                    DataObject dobjNew = DataObject.find(fobj);
                    synchronized (DataObjectListener.this) {
                        if (dobjNew == dobj) {
                            return;
                        }
                        dobj = dobjNew;
                        dobj.addPropertyChangeListener(wlistener);
                    }
                    assignDocumentListener(dobjNew);
                    resetState(true, true);
                } catch (DataObjectNotFoundException e) {
                    //Ignore - invalidated after fobj.isValid () was called
                } catch (IOException ex) {
                    // should not occur
                    LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
                }
            }
        } 
    }
    
    static final class DeferredTask {
        final JavaFXSource js;
        final Task<CompilationController> task;
        final boolean shared;
        final ScanSync sync;
        
        public DeferredTask (final JavaFXSource js, final Task<CompilationController> task, final boolean shared, final ScanSync sync) {
            assert js != null;
            assert task != null;
            assert sync != null;
            
            this.js = js;
            this.task = task;
            this.shared = shared;
            this.sync = sync;
        }
    }
    static final class DocPositionRegion extends PositionRegion {
        
        private final Document doc;
        
        public DocPositionRegion (final Document doc, final int startPos, final int endPos) throws BadLocationException {
            super (doc,startPos,endPos);
            assert doc != null;
            this.doc = doc;
        }
        
        public Document getDocument () {
            return this.doc;
        }
        
        public String getText () {
            final String[] result = new String[1];
            this.doc.render(new Runnable() {
                public void run () {
                    try {
                        result[0] = doc.getText(getStartOffset(), getLength());
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
            return result[0];
        }
        
    }        
    static final class ScanSync implements Future<Void> {
        
        private Task<CompilationController> task;
        private final CountDownLatch sync;
        private final AtomicBoolean canceled;
        
        public ScanSync (final Task<CompilationController> task) {
            assert task != null;
            this.task = task;
            this.sync = new CountDownLatch (1);
            this.canceled = new AtomicBoolean (false);
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            if (this.sync.getCount() == 0) {
                return false;
            }
            synchronized (todo) {
                boolean _canceled = canceled.getAndSet(true);
                if (!_canceled) {
                    for (Iterator<DeferredTask> it = todo.iterator(); it.hasNext();) {
                        DeferredTask task = it.next();
                        if (task.task == this.task) {
                            it.remove();
                            return true;
                        }
                    }
                }
            }            
            return false;
        }

        public boolean isCancelled() {
            return this.canceled.get();
        }

        public synchronized boolean isDone() {
            return this.sync.getCount() == 0;
        }

        public Void get() throws InterruptedException, ExecutionException {
            this.sync.await();
            return null;
        }

        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            this.sync.await(timeout, unit);
            return null;
        }
        
        private void taskFinished () {
            this.sync.countDown();
        }            
    }
    /**
     * Returns a {@link JavaSource} instance associated to the given {@link javax.swing.Document},
     * it returns null if the {@link Document} is not
     * associated with data type providing the {@link JavaSource}.
     * @param doc {@link Document} for which the {@link JavaSource} should be found/created.
     * @return {@link JavaSource} or null
     * @throws {@link IllegalArgumentException} if doc is null
     */
    public static JavaFXSource forDocument(Document doc) throws IllegalArgumentException {
        if (doc == null) {
            throw new IllegalArgumentException ("doc == null");  //NOI18N
        }
        Reference<?> ref = (Reference<?>) doc.getProperty(JavaFXSource.class);
        JavaFXSource js = ref != null ? (JavaFXSource) ref.get() : null;
        if (js == null) {
            Object source = doc.getProperty(Document.StreamDescriptionProperty);
            
            if (source instanceof DataObject) {
                DataObject dObj = (DataObject) source;
                if (dObj != null) {
                    js = forFileObject(dObj.getPrimaryFile());
                }
            }
        }
        return js;
    }
}
