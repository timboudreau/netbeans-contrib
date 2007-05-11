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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;

/**
 *
 * @author Tim
 */
public abstract class Utils {
    private Utils() {}
    /*
    public static <T extends Tree> List <T> toTrees (List <? extends TreePathHandle> handles, CompilationInfo info) {
        List <T> result = new ArrayList <T> (handles.size());
        for (TreePathHandle handle : handles) {
            TreePath path = handle.resolve(info);
            T item = (T) path.getLeaf();
            result.add (item);
        }
        return result;
    }
    
    public static List <TreePathHandle> toHandles (TreePath parent, List <? extends Tree> trees, CompilationInfo info) {
        List <TreePathHandle> result = new ArrayList <TreePathHandle> (trees.size());
        for (Tree tree : trees) {
            TreePath path = TreePath.getPath(parent, tree);
            TreePathHandle handle = TreePathHandle.create(path, info);
            result.add (handle);
        }
        return result;
    }
    
    public static List <TreePath> toPaths (TreePath parent, List <? extends Tree> trees, CompilationInfo info) {
        List <TreePath> result = new ArrayList <TreePath> (trees.size());
        for (Tree tree : trees) {
            TreePath path = TreePath.getPath(parent, tree);
            result.add (path);
        }
        return result;
    }    
    
    public static List <TreePathHandle> toHandles(List <? extends Element> elements, CompilationInfo info) {
        List <TreePathHandle> result = new ArrayList <TreePathHandle> (elements.size());
        for (Element e : elements) {
            TreePathHandle handle = TreePathHandle.create(e, info);
            result.add (handle);
        }
        return result;
    }
    
    public static List <TreePath> toPaths(List <? extends Element> elements, Trees trees) {
        List <TreePath> result = new ArrayList <TreePath> (elements.size());
        for (Element e : elements) {
            TreePath path = trees.getPath(e);
            result.add (path);
        }
        return result;
    }    
    
    public static <T extends Element> List <T> toElements(List <? extends TreePathHandle> handles, CompilationInfo info) {
        List <T> result = new ArrayList <T> (handles.size());
        for (TreePathHandle handle : handles) {
            T element = (T) handle.resolveElement(info);
            result.add (element);
        }
        return result;
    }
     */ 
}
