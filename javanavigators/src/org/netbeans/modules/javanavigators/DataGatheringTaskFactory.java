/*
 * DataGatheringTaskFactory.java
 *
 * Created on February 9, 2007, 10:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javanavigators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.api.java.source.support.LookupBasedJavaSourceTaskFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author Tim Boudreau
 */
public class DataGatheringTaskFactory extends LookupBasedJavaSourceTaskFactory {
    private final GenerifiedListModel <Description> model = 
            new AsynchListModel <Description> ();
    
    public DataGatheringTaskFactory() {
        super (JavaSource.Phase.PARSED, JavaSource.Priority.LOW);
    }
    
    volatile boolean active = false;
    void activate() {
        System.err.println("activated");
        active = true;
        setLookup (Utilities.actionsGlobalContext());
    }
    
    void deactivate() {
        System.err.println("deactivated");
        active = false;
        setLookup (Lookup.EMPTY);
        model.setContents(Collections.<Description>emptyList(), true);
    }
    
    protected CancellableTask<CompilationInfo> createTask(FileObject file) {
        active = true;
        assert getDefault() == this;
        return active ? new ElementScanningTask( model ) : EMPTY_TASK;
    }
    
    static DataGatheringTaskFactory INSTANCE = null;
    static DataGatheringTaskFactory getDefault() {
        if (INSTANCE == null) {
            Collection <? extends JavaSourceTaskFactory> c = Lookup.getDefault().lookupAll (LookupBasedJavaSourceTaskFactory .class);
            for (JavaSourceTaskFactory  j : c) {
                if (j instanceof DataGatheringTaskFactory) {
                    INSTANCE = (DataGatheringTaskFactory) j;
                    break;
                }
            }
            if (INSTANCE == null) throw new AssertionError();
        }
        return INSTANCE;
    }
    
    static GenerifiedListModel <Description> getModel() {
        return getDefault().model;
    }
    
    public List<FileObject> getFileObjects() {
        List<FileObject> result = new ArrayList<FileObject>();

        // Filter uninteresting files from the lookup
        for( FileObject fileObject : super.getFileObjects() ) {
            if (!"text/x-java".equals(FileUtil.getMIMEType(fileObject)) && !"java".equals(fileObject.getExt())) {  //NOI18N
                continue;
            }
            result.add(fileObject);
        }
        
        if (result.size() == 1) {
            return result;
        }

        return Collections.emptyList();
    }
    
    private static final CancellableTask<CompilationInfo> EMPTY_TASK = new CancellableTask<CompilationInfo>() {
        public void cancel() {}
        public void run(CompilationInfo parameter) throws Exception {}
    };    
}
