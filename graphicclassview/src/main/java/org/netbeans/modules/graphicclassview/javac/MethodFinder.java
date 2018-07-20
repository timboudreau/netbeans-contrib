package org.netbeans.modules.graphicclassview.javac;

import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.*;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.graphicclassview.SceneElement;
import org.netbeans.modules.graphicclassview.SceneObjectKind;

public final class MethodFinder extends TreePathScanner<Void, Set<SceneElement>> {
    private final CompilationController cc;
    private final Map<Element, SceneElement> els2items = new HashMap<Element, SceneElement>();

    public MethodFinder(CompilationController cc) {
        this.cc = cc;
    }

    public Map<Element, SceneElement> getElementsMap() {
        return els2items;
    }

    @Override
    public Void visitMethod(MethodTree tree, Set<SceneElement> set) {
        String nm = tree.getName().toString();
        Tree typeTree = tree.getReturnType();
        //XXX typeTree.toString() is wrong
        addItem(tree, set, SceneObjectKind.METHOD, nm, typeTree.toString());
        return super.visitMethod(tree, set);
    }

    @Override
    public Void visitVariable(VariableTree tree, Set<SceneElement> set) {
        String nm = tree.getName().toString();
//        String typeString = tree.getType();
        Tree typeTree = tree.getType();
        //XXX typeTree.toString() is wrong
        addItem(tree, set, SceneObjectKind.FIELD, nm, typeTree.toString());
        return super.visitVariable(tree, set);
    }

    private void addItem(Tree tree, Set<SceneElement> set, SceneObjectKind kind, String nm, String type) {
        TreePath path = getCurrentPath();
        SceneElement nue = new SceneElement(kind, TreePathHandle.create(getCurrentPath(), cc), tree.toString(), nm, type);
        set.add(nue);
        Element el = cc.getTrees().getElement(path);
        els2items.put(el, nue);
    }
}
