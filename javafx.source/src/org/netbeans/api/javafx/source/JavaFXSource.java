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
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.tools.JavaFileObject;
import org.netbeans.api.lexer.TokenHierarchy;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
import org.netbeans.modules.javafx.source.scheduler.DocListener;
import org.netbeans.modules.javafx.source.scheduler.DataObjectListener;
import org.netbeans.modules.javafx.source.scheduler.CompilationJob;
import org.netbeans.modules.javafx.source.scheduler.EditorRegistryListener;
import org.netbeans.modules.javafx.source.scheduler.Request;


/**
 * A class representing JavaFX source.
 * 
 * @author nenik
 * @author David Strupl
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

    // flags:
    public static final int INVALID = 1;
    public static final int CHANGE_EXPECTED = INVALID<<1;
    public static final int RESCHEDULE_FINISHED_TASKS = CHANGE_EXPECTED<<1;
    public static final int UPDATE_INDEX = RESCHEDULE_FINISHED_TASKS<<1;
    public static final int IS_CLASS_FILE = UPDATE_INDEX<<1;
    
    private static Map<FileObject, Reference<JavaFXSource>> file2Source = new WeakHashMap<FileObject, Reference<JavaFXSource>>();
    private static final RequestProcessor RP = new RequestProcessor ("JavaFXSource-event-collector",1);       //NOI18N
    static final Logger LOGGER = Logger.getLogger(JavaFXSource.class.getName());
    
    private static final int REPARSE_DELAY = 500;
    private static final Pattern excludedTasks;
    private static final Pattern includedTasks;

    // all the following should be private:
    public int flags = 0;
    public volatile boolean k24;
    public CompilationController currentInfo;
    
    public final Collection<? extends FileObject> files;
    public final int reparseDelay;
    
    private final ClasspathInfo cpInfo;
    private final AtomicReference<Request> rst = new AtomicReference<Request> ();
    private final FileChangeListener fileChangeListener;
    
    private DocListener listener;
    private DataObjectListener dataObjectListener;
    
    public final RequestProcessor.Task resetTask = RP.create(new Runnable() {
        public void run() {
            resetStateImpl();
        }
    });
    
    static {
        // Start listening on the editor registry:
        EditorRegistryListener.singleton.toString();
//        Init the maps
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

    JavafxcTask createJavafxcTask() {
        JavafxcTool tool = JavafxcTool.create();
        JavacFileManager fileManager = tool.getStandardFileManager(null, null, Charset.defaultCharset());
        JavaFileObject jfo = (JavaFileObject) SourceFileObject.create(files.iterator().next(), null); // XXX
        JavafxcTask task = tool.getTask(null, fileManager, null, null, Collections.singleton(jfo));
//            Context context = task.getContext();
        
        return task;
  }

    public Phase moveToPhase(Phase phase, CompilationController cc, boolean b) throws IOException {
        if (cc.phase.lessThan(Phase.PARSED)) {
                Iterable<? extends CompilationUnitTree> trees = cc.getJavafxcTask().parse();
//                new JavaFileObject[] {currentInfo.jfo});

                for (CompilationUnitTree cut : trees) {
                    cc.setCompilationUnit(cut);
                }
                /*                assert trees != null : "Did not parse anything";        //NOI18N
                Iterator<? extends CompilationUnitTree> it = trees.iterator();
                assert it.hasNext();
                CompilationUnitTree unit = it.next();
                currentInfo.setCompilationUnit(unit);
*/
                cc.phase = Phase.PARSED;
        }
        return phase;
    }

    
    private JavaFXSource(ClasspathInfo cpInfo, Collection<? extends FileObject> files) throws IOException {
        this.cpInfo = cpInfo;
        this.files = Collections.unmodifiableList(new ArrayList<FileObject>(files));   //Create a defensive copy, prevent modification
        
        this.reparseDelay = REPARSE_DELAY;
        this.fileChangeListener = new FileChangeListenerImpl ();
        boolean multipleSources = this.files.size() > 1, filterAssigned = false;
        for (Iterator<? extends FileObject> it = this.files.iterator(); it.hasNext();) {
            FileObject file = it.next();
            try {
                Logger.getLogger("TIMER").log(Level.FINE, "JavaFXSource",
                    new Object[] {file, this});
                if (!multipleSources) {
                    file.addFileChangeListener(FileUtil.weakFileChangeListener(this.fileChangeListener,file));
                    assignDocumentListener(DataObject.find(file));
                    dataObjectListener = new DataObjectListener(file,this);                                        
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
        this.cpInfo.addChangeListener(WeakListeners.change(listener, this.cpInfo));
        
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

    public static CompilationController createCurrentInfo (final JavaFXSource js, final String javafxc) throws IOException {                
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
        synchronized (CompilationJob.INTERNAL_LOCK) {
            CompilationJob.toRemove.add (task);
            Collection<Request> rqs = CompilationJob.finishedRequests.get(this);
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
        synchronized (CompilationJob.INTERNAL_LOCK) {
            Request request = CompilationJob.currentRequest.getTaskToCancel (task);
            if ( request == null) {                
out:            for (Iterator<Collection<Request>> it = CompilationJob.finishedRequests.values().iterator(); it.hasNext();) {
                    Collection<Request> cr = it.next ();
                    for (Iterator<Request> it2 = cr.iterator(); it2.hasNext();) {
                        Request fr = it2.next();
                        if (task == fr.task) {
                            it2.remove();
                            CompilationJob.requests.add(fr);
                            if (cr.size()==0) {
                                it.remove();
                            }
                            break out;
                        }
                    }
                }
            }
            else {
                CompilationJob.currentRequest.cancelCompleted(request);
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
            CompilationJob.currentRequest.cancelCompleted(r);
            synchronized (CompilationJob.INTERNAL_LOCK) {
                boolean reschedule, updateIndex;
                synchronized (this) {
                    reschedule = (this.flags & RESCHEDULE_FINISHED_TASKS) != 0;
                    updateIndex = (this.flags & UPDATE_INDEX) != 0;
                    this.flags&=~(RESCHEDULE_FINISHED_TASKS|CHANGE_EXPECTED|UPDATE_INDEX);
                }            
                Collection<Request> cr;            
                if (reschedule) {                
                    if ((cr=CompilationJob.finishedRequests.remove(this)) != null && cr.size()>0)  {
                        CompilationJob.requests.addAll(cr);
                    }
                }
                if ((cr=CompilationJob.waitingRequests.remove(this)) != null && cr.size()>0)  {
                    CompilationJob.requests.addAll(cr);
                }
            }          
        }
    }
    

    public void resetState(boolean invalidate, boolean updateIndex) {
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
        Request r = CompilationJob.currentRequest.getTaskToCancel (invalidate);
        if (r != null) {
            r.task.cancel();
            Request oldR = rst.getAndSet(r);
            assert oldR == null;
        }
        if (!k24) {
            resetTask.schedule(reparseDelay);
        }
    }
    
    public void assignDocumentListener(final DataObject od) throws IOException {
        EditorCookie.Observable ec = od.getCookie(EditorCookie.Observable.class);            
        if (ec != null) {
            listener = new DocListener (ec,this);
        } else {
            LOGGER.log(Level.WARNING,String.format("File: %s has no EditorCookie.Observable", FileUtil.getFileDisplayName (od.getPrimaryFile())));      //NOI18N
        }
    }
    
    public TokenHierarchy getTokenHierarchy() {
        if ((listener == null) || (listener.getDocument() == null)) {
            return null;
        }
        TokenHierarchy th = TokenHierarchy.get(listener.getDocument());
        return th;
    }
    
    String getText() {
        if ((listener == null) || (listener.getDocument() == null)) {
            return "";
        }
        try {
            return listener.getDocument().getText(0, listener.getDocument().getLength());
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }
    
    private static void handleAddRequest (final Request nr) {
        assert nr != null;
        //Issue #102073 - removed running task which is readded is not performed
        synchronized (CompilationJob.INTERNAL_LOCK) {            
            CompilationJob.toRemove.remove(nr.task);
            CompilationJob.requests.add (nr);
        }
        Request request = CompilationJob.currentRequest.getTaskToCancel(nr.priority);
        try {
            if (request != null) {
                request.task.cancel();
            }
        } finally {
            CompilationJob.currentRequest.cancelCompleted(request);
        }
    }
    
        /**
     * Performs the given task when the scan finished. When no background scan is running
     * it performs the given task synchronously. When the background scan is active it queues
     * the given task and returns, the task is performed when the background scan completes by
     * the thread doing the background scan.
     * @param task to be performed
     * @param shared if true the java compiler may be reused by other {@link org.netbeans.api.java.source.CancellableTasks},
     * the value false may have negative impact on the IDE performance.
     * @return {@link Future} which can be used to find out the sate of the task {@link Future#isDone} or {@link Future#isCancelled}.
     * The caller may cancel the task using {@link Future#cancel} or wait until the task is performed {@link Future#get}.
     * @throws IOException encapsulating the exception thrown by {@link CancellableTasks#run}
     * @since 0.12
     */
    public Future<Void> runWhenScanFinished (final Task<CompilationController> task, final boolean shared) throws IOException {
        return CompilationJob.runWhenScanFinished(this, task, shared);
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
    
    private class FileChangeListenerImpl extends FileChangeAdapter {                
        
        public @Override void fileChanged(final FileEvent fe) {
            resetState(true, false);
        }        

        public @Override void fileRenamed(FileRenameEvent fe) {
            resetState(true, false);
        }        
    }

}
