package org.netbeans.modules.graphicclassview.javac;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.modules.graphicclassview.SceneElement;

public final class UsageFinder extends TreeScanner<Void, Void> {

    private final CompilationController cc;
    private final Map map;

    public UsageFinder(CompilationController cc, Map m) {
        this.cc = cc;
        map = m;
    }

    @Override
    public Void visitIdentifier(IdentifierTree tree, Void arg1) {
        addElement(tree);
        return super.visitIdentifier(tree, arg1);
    }

    @Override
    public Void visitVariable(VariableTree tree, Void arg1) {
        addElement(tree);
        return super.visitVariable(tree, arg1);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree tree, Void set) {
        addElement(tree);
        return super.visitMemberSelect(tree, set);
    }

    private void addElement(Tree tree) {
        TreePath path = TreePath.getPath(cc.getCompilationUnit(), tree);
        Element el = cc.getTrees().getElement(path);
        if (el == null || el.getKind() == ElementKind.PARAMETER) {
            return;
        }
        Element selectedElement = el;
        do {
            path = path.getParentPath();
        } while (path != null && path.getLeaf().getKind() != com.sun.source.tree.Tree.Kind.METHOD && path.getLeaf().getKind() != com.sun.source.tree.Tree.Kind.CLASS);
        if (path != null && path.getLeaf().getKind() == com.sun.source.tree.Tree.Kind.METHOD) {
            Element enclosingElement = cc.getTrees().getElement(path);
            SceneElement enclosing = (SceneElement) map.get(enclosingElement);
            SceneElement selected = (SceneElement) map.get(selectedElement);
            if (enclosing != null && selected != null) {
                enclosing.addOutboundReference(selected);
                selected.addInboundReference(enclosing);
            }
        }
    }
}
