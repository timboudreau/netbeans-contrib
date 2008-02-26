// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ElementFinder.java
package org.netbeans.modules.graphicclassview.javac;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import java.util.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.swing.ImageIcon;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.ui.ElementIcons;
import org.netbeans.modules.graphicclassview.SceneElement;
import org.netbeans.modules.graphicclassview.SceneObjectKind;

public final class ElementFinder extends TreeScanner <Void, Set<SceneElement>> {
    private final CompilationController cc;
    private final Map <Element, SceneElement> els2items = new HashMap<Element, SceneElement>();

    public ElementFinder(CompilationController cc) {
        this.cc = cc;
    }

    public Map<Element, SceneElement> getElementsMap() {
        return els2items;
    }

    @Override
    public Void visitMethod(MethodTree tree, Set<SceneElement> set) {
        String nm = tree.getName().toString();
        addItem(tree, set, SceneObjectKind.METHOD, nm);
        return super.visitMethod(tree, set);
    }

    @Override
    public Void visitVariable(VariableTree tree, Set<SceneElement> set) {
        String nm = tree.getName().toString();
        addItem(tree, set, SceneObjectKind.FIELD, nm);
        return super.visitVariable(tree, set);
    }

    private void addItem(Tree tree, Set<SceneElement> set, SceneObjectKind kind, String nm) {
        TreePath path = TreePath.getPath(cc.getCompilationUnit(), tree);
        Element el = cc.getTrees().getElement(path);
        if (el != null && (el.getKind() == ElementKind.FIELD || el.getKind() == ElementKind.METHOD)) {
            javax.lang.model.type.TypeMirror mirror = el.asType();
            String typeStr = mirror != null ? mirror.toString() : "void";
            SceneElement nue = new SceneElement(kind, TreePathHandle.create(path, cc), tree.toString(), nm, typeStr);
            java.awt.Image img = ((ImageIcon) ElementIcons.getElementIcon(el.getKind(), el.getModifiers())).getImage();
            nue.setImage(img);
            set.add(nue);
            els2items.put(el, nue);
        }
    }
}
