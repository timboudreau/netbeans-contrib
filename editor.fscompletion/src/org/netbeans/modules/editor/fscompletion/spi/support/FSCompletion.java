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
package org.netbeans.modules.editor.fscompletion.spi.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.queries.VisibilityQuery;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class FSCompletion {

    private FSCompletion() {}

    public static List/*<CompletionItem>*/ completion(FileObject root, FileObject relative, String prefix, int anchor) throws IOException {
        return completion(root, relative, prefix, anchor, DEFAULT_FILTER);
    }

    /**TODO: root=null, relative=null should mean root of all filesystems!
     */
    public static List/*<CompletionItem>*/ completion(FileObject root, FileObject relative, String prefix, int anchor, FileObjectFilter filter) throws IOException {
        if (relative == null && root == null) {
            throw new IllegalArgumentException("root == null && relative == null currently not supported!");
        }
        
        boolean isAbsolute = relative == null;
        
        List result = new ArrayList();
        
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
            return Collections.EMPTY_LIST;
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
    
    public static List/*<CompletionItem>*/ completion(FileObject[] root, FileObject[] relative, String prefix, int anchor) throws IOException {
        return completion(root, relative, prefix, anchor, DEFAULT_FILTER);
    }

    public static List/*<CompletionItem>*/ completion(FileObject[] root, FileObject[] relative, String prefix, int anchor, FileObjectFilter filter) throws IOException {
        if (root.length != relative.length) {
            throw new IllegalArgumentException();
        }
        
        Set result = new LinkedHashSet();
        
        for (int cntr = 0; cntr < root.length; cntr++) {
            result.addAll(completion(root[cntr], relative[cntr], prefix, anchor));
        }
        
        return new ArrayList(result);
    }

    private static final AcceptAllFileObjectFilter DEFAULT_FILTER = new AcceptAllFileObjectFilter();

    private static class AcceptAllFileObjectFilter implements FileObjectFilter {

        public boolean accept(FileObject file) {
            return true;
        }

    }

}
