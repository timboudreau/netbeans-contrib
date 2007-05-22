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

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim
 */
public abstract class Utils {
    private Utils() {}
    
    public static List <TypeMirror> toTypeMirrors (Iterable <? extends TypeMirrorHandle> types, CompilationInfo info) {
        List <TypeMirror> result = new ArrayList <TypeMirror> ();
        for (TypeMirrorHandle h : types) {
            result.add (h.resolve(info));
        }
        return result;
    }
    
    public static List <TypeMirrorHandle> toTypeMirrorHandles (Iterable <? extends TypeMirror> types) {
        List <TypeMirrorHandle> result = new ArrayList <TypeMirrorHandle> ();
        for (TypeMirror h : types) {
            result.add (TypeMirrorHandle.create(h));
        }
        return result;
    }
        
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
            assert handle.resolve(info) != null : "Newly created TreePathHandle resolves to null"; //NOI18N
            assert handle.resolve(info).getLeaf() != null : "Newly created TreePathHandle.getLeaf() resolves to null"; //NOI18N            
        }
        return result;
    }
    
    public static TypeMirror hackFqn (TypeMirror mirror, TreeMaker maker, Trees trees, CompilationInfo info) {
        //Hack to eliminate FQNs
        Tree typeTree = maker.Type(mirror);
        TreePath path = TreePath.getPath (info.getCompilationUnit(), typeTree);
        TypeMirror result = null;
        if (path != null) {
            try {
                result = trees.getTypeMirror(path);
            } catch (NullPointerException npe) {
                Exceptions.printStackTrace(npe);
            }
        } else {
            System.err.println("Null tree path for " + mirror);
        }
        return result == null ? mirror : result;
    }
    
    public static Tree cloneTree (Tree old, Element element, TreeMaker maker, WorkingCopy wc) {
        Tree result = null;
        switch (element.getKind()) {
            case METHOD :
            {
                MethodTree mt = (MethodTree) old;
                List <? extends VariableTree> parameters = new ArrayList <VariableTree> (mt.getParameters());
                List <? extends TypeParameterTree> typeParameters = new ArrayList <TypeParameterTree> (mt.getTypeParameters());
                List <? extends ExpressionTree> throes = new ArrayList <ExpressionTree> (mt.getThrows());
                Tree ret = mt.getReturnType();
                BlockTree body = mt.getBody();
                ExpressionTree defaultValue = (ExpressionTree) 
                        mt.getDefaultValue();
                String name = mt.getName().toString();
                ModifiersTree modifiers = mt.getModifiers();
                result = maker.Method(
                        modifiers, name, ret, typeParameters, parameters, 
                        throes, body, defaultValue);
            }   
                break;
            case CONSTRUCTOR :
            {
                MethodTree mt  = (MethodTree) old;
                List <? extends VariableTree> parameters = new ArrayList <VariableTree> (mt.getParameters());
                List <? extends TypeParameterTree> typeParameters = new ArrayList <TypeParameterTree> (mt.getTypeParameters());
                List <? extends ExpressionTree> throes = new ArrayList <ExpressionTree> (mt.getThrows());
                BlockTree body = mt.getBody();
                
                CompilationUnitTree cut = wc.getCompilationUnit();
                SourcePositions sp = wc.getTrees().getSourcePositions();
                int start = (int) sp.getStartPosition(cut, body);
                int end = (int) sp.getEndPosition(cut, body);
                // get body text from source text
                String bodyText = wc.getText().substring(start, end);                
                
                ExpressionTree defaultValue = (ExpressionTree) 
                        mt.getDefaultValue();
                ModifiersTree modifiers = mt.getModifiers();
                result = maker.Method(
                        modifiers, "<init>", null, 
                        typeParameters, parameters, 
                        throes, bodyText, defaultValue);
            }
                break;
            case FIELD :
                VariableTree ft = (VariableTree) old;
                VariableElement ve = (VariableElement) element;
                //XXX bug in Retouche:  If we use ft.getType(), we will
                //get an FQN;  if we create a new type object, works correctly.
                Tree typeTree = maker.Type(ve.asType());
                ModifiersTree mt = ft.getModifiers();
                result = maker.Variable(mt, ft.getName().toString(), 
                        typeTree, ft.getInitializer());
                break;
            case CLASS :
                ClassTree ct = (ClassTree) old;
                ModifiersTree modifiers = ct.getModifiers();
                String simpleName = ct.getSimpleName().toString();
                List <? extends TypeParameterTree> typeParameters = 
                        new ArrayList <TypeParameterTree> (ct.getTypeParameters());
                List <? extends Tree> implementsClauses = 
                        new ArrayList <Tree> (ct.getImplementsClause());
                List <? extends Tree> memberDecls = 
                        new ArrayList <Tree> (ct.getMembers());
                
                result = maker.Class(modifiers, 
                        simpleName, 
                        typeParameters, 
                        result, 
                        implementsClauses, 
                        memberDecls);
                
                break;
            default :
                result = null;
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
    
    public static boolean isStatic (Element el, Scope scope, CompilationController cc) {
        return cc.getTreeUtilities().isStaticContext(scope) ||
                el.getModifiers().contains (Modifier.STATIC);
    }
    
    public static BlockTree findBlockTree (Tree tree) {
        BlockTree result = null;
        if (tree instanceof MethodTree) {
            MethodTree mt = (MethodTree) tree;
            result = mt.getBody();
        } else if (tree instanceof BlockTree) {
            result = (BlockTree) tree;
        }
        return result;
    }
}
