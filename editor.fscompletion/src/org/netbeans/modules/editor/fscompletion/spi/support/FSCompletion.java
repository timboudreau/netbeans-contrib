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
package org.netbeans.modules.editor.fscompletion.spi.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class FSCompletion {

    private FSCompletion() {}

    public static List<? extends CompletionItem> completion(FileObject root, FileObject relative, String prefix, int anchor) throws IOException {
        return completion(root, relative, prefix, anchor, DEFAULT_FILTER);
    }

    /**TODO: root=null, relative=null should mean root of all filesystems!
     */
    public static List<? extends CompletionItem> completion(FileObject root, FileObject relative, String prefix, int anchor, FileObjectFilter filter) throws IOException {
        if (relative == null && root == null) {
            throw new IllegalArgumentException("root == null && relative == null currently not supported!");
        }
        
        boolean isAbsolute = relative == null;
        
        List<CompletionItem> result = new LinkedList<CompletionItem>();
        
        if (relative != null && relative.isData())
            relative = relative.getParent();
        
        int lastSlash = prefix.lastIndexOf('/');
        String pathPrefix;
        String filePrefix;
        
        if (lastSlash != (-1)) {
            pathPrefix = prefix.substring(0, lastSlash);
            filePrefix = prefix.substring(lastSlash + 1);
        } else {
            pathPrefix = null;
            filePrefix = prefix;
        }
        
        if (root == null) {
            root = relative.getFileSystem().getRoot(); //TODO: what about drive letters?
        }
        
        if (relative == null) {
            relative = root;
        }
        
        if (pathPrefix != null) {
            relative = relative.getFileObject(pathPrefix);
        }
        
        if (relative == null) {
            return Collections.emptyList();
        }
        
        FileObject[] children = relative.getChildren();
        
        for (int cntr = 0; cntr < children.length; cntr++) {
            FileObject current = children[cntr];
            
            if (VisibilityQuery.getDefault().isVisible(current) && current.getNameExt().startsWith(filePrefix) && filter.accept(current)) {
                result.add(new FSCompletionItem(current, pathPrefix != null ? pathPrefix + "/" : isAbsolute ? "/" : "", anchor));
            }
        }
        
        return result;
    }
    
    public static List<? extends CompletionItem> completion(FileObject[] root, FileObject[] relative, String prefix, int anchor) throws IOException {
        return completion(root, relative, prefix, anchor, DEFAULT_FILTER);
    }

    public static List<? extends CompletionItem> completion(FileObject[] root, FileObject[] relative, String prefix, int anchor, FileObjectFilter filter) throws IOException {
        if (root.length != relative.length) {
            throw new IllegalArgumentException();
        }
        
        Set<CompletionItem> result = new LinkedHashSet<CompletionItem>();
        
        for (int cntr = 0; cntr < root.length; cntr++) {
            result.addAll(completion(root[cntr], relative[cntr], prefix, anchor));
        }
        
        return new ArrayList<CompletionItem>(result);
    }

    private static final AcceptAllFileObjectFilter DEFAULT_FILTER = new AcceptAllFileObjectFilter();

    private static class AcceptAllFileObjectFilter implements FileObjectFilter {

        public boolean accept(FileObject file) {
            return true;
        }

    }

}
