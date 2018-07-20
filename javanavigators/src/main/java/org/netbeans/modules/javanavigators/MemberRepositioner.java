/*
 * MemberRepositioner.java
 * 
 * Created on Apr 16, 2007, 10:06:57 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javanavigators;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.swing.SwingUtilities;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 * Moves a class member somewhere else in the source
 * file.
 *
 * @author Tim Boudreau
 */
final class MemberRepositioner implements CancellableTask <WorkingCopy> {
    private final Description toMove;
    private final Description relativeTo;
    private final boolean above;
    public MemberRepositioner(Description toMove, Description relativeTo, boolean above) {
        this.toMove = toMove;
        this.relativeTo = relativeTo;
        this.above = above;
    }
    
    public void go() {
        try     {
            ModificationResult res = JavaSource.forFileObject(
                    toMove.fileObject).runModificationTask(
                    this);
            if (res != null) {
                res.commit();
            }
        }
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
}

    volatile boolean cancelled = false;
    public void cancel() {
        cancelled = true;
    }

    public void run(WorkingCopy wc) throws Exception {
        wc.toPhase(Phase.RESOLVED);
        Element moveElement = 
                toMove.elementHandle.resolve(wc);
        Element nextTo = 
                relativeTo.elementHandle.resolve(wc);
        
        ElementUtilities elUtils = wc.getElementUtilities();
        
        TypeElement moveParent = elUtils.enclosingTypeElement(moveElement);
        TypeElement destParent = elUtils.enclosingTypeElement(nextTo);
        
        boolean cantMove = destParent != moveParent;
        
        if (cantMove && ElementKind.CLASS.equals(moveElement.getKind())) {
            //XXX don't allow move of inner class to another parent
            cantMove = false;
        }
        
        if (cantMove) {
            Toolkit.getDefaultToolkit().beep();
            return;    
        }
        if (moveElement != null && nextTo != null) {
            TreeMaker maker = wc.getTreeMaker();
            Trees trees = wc.getTrees();
            Tree nextToTree = trees.getTree(nextTo);
            Tree moveTree = trees.getTree(moveElement);
            Tree ct = trees.getTree(destParent);
            if (ct instanceof ClassTree) {
                ClassTree classTree = (ClassTree) ct;
                int index = classTree.getMembers().indexOf(nextToTree);
                if (above) {
                    index = Math.max (0, index - 1);
                }
                
                Tree replaceTree = cloneTree (moveTree, moveElement, maker, wc);
                
                ClassTree nue = maker.removeClassMember(classTree, moveTree);
                if (cancelled || replaceTree == null) {
                    return;
                }
                nue = maker.insertClassMember(nue, index, replaceTree);
                
                wc.rewrite(classTree, nue);
                
                FileObject ob = toMove.fileObject;
                DataObject dob = DataObject.find (ob);
                EditorCookie ck = (EditorCookie) dob.getCookie(EditorCookie.class);
                if (ck != null && ck.getOpenedPanes() != null && ck.getOpenedPanes().length > 0) {
                    TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(
                            TopComponent.class, ck.getOpenedPanes()[0]);
                    if (tc != null) {
                        tc.requestActive();
                    }
                }
            }
        }
    }
    
    private Tree cloneTree (Tree old, Element element, TreeMaker maker, WorkingCopy wc) {
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
}
