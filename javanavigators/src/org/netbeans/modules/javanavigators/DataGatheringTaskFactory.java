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
