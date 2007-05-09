/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.openide.util.NbPreferences;
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
        boolean alphaSort = NbPreferences.forModule(JavaMembersNavigator.class).getBoolean(JavaMembersNavigator.KEY_SORT_POS, false);
        model.setComparator(alphaSort ? Description.ALPHA_COMPARATOR :
            Description.POSITION_COMPARATOR);
    }
    
    volatile boolean active = false;
    void activate() {
        active = true;
        setLookup (Utilities.actionsGlobalContext());
    }
    
    void deactivate() {
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
