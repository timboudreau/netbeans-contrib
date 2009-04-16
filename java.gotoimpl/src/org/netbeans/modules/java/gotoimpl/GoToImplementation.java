/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008-2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.gotoimpl;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.java.editor.overridden.PopupUtil;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.TopologicalSortException;
import org.openide.util.Utilities;

public final class GoToImplementation extends AbstractAction implements PropertyChangeListener {

    public GoToImplementation() {
        putValue(NAME, NbBundle.getMessage(GoToImplementation.class, "CTL_GoToImplementation"));
        putValue("noIconInMenu", "false"); //NOI18N

        EditorRegistry.addPropertyChangeListener(this);

        Mutex.EVENT.readAccess(new Runnable() {
            public void run() {
                checkIsEnabled();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        final JTextComponent c = EditorRegistry.lastFocusedComponent();

        try {
            JavaSource.forDocument(c.getDocument()).runUserActionTask(new Task<CompilationController>() {
                public void run(CompilationController parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    
                    ExecutableElement el = resolveMethod(parameter, c.getCaretPosition());

                    if (el == null) {
                        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(GoToImplementation.class, "LBL_NoMethod"));
                        return ;
                    }
                    
                    List<ElementDescription> overriding = process(parameter, el);

                    if (overriding == null) {
                        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(GoToImplementation.class, "LBL_NoOverridingMethod"));
                        return;
                    }
                    
                    Point p = new Point(c.modelToView(c.getCaretPosition()).getLocation());

                    SwingUtilities.convertPointToScreen(p, c);
                    
                    performGoToAction(overriding, p);
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        checkIsEnabled();
    }

    private void checkIsEnabled() {
        JTextComponent c = EditorRegistry.lastFocusedComponent();

        setEnabled(c != null && JavaSource.forDocument(c.getDocument()) != null);
    }

    private static Set<URL> findReverseSourceRoots(final FileObject thisSourceRoot, final FileObject thisFile) {
        long startTime = System.currentTimeMillis();

        try {
            return SourceUtils.getDependentRoots(thisSourceRoot.getURL());
        } catch (FileStateInvalidException ex) {
            throw new IllegalStateException(ex);
        } finally {
            long endTime = System.currentTimeMillis();

            Logger.getLogger("TIMER").log(Level.FINE, "Find Reverse Source Roots", //NOI18N
                    new Object[]{thisFile, endTime - startTime});
        }
    }

    private static FileObject findSourceRoot(FileObject file) {
        final ClassPath cp = ClassPath.getClassPath(file, ClassPath.SOURCE);
        if (cp != null) {
            for (FileObject root : cp.getRoots()) {
                if (FileUtil.isParentOf(root, file)) {
                    return root;
                }
            }
        }
        //Null is a valid value for files which have no source path (default filesystem).
        return null;
    }

    private static List<ElementDescription> process(CompilationInfo info, ExecutableElement ee) {
        Set<URL> reverseSourceRoots;
        FileObject thisSourceRoot = findSourceRoot(info.getFileObject());
        if (thisSourceRoot == null) {
            return null;
        }

        reverseSourceRoots = findReverseSourceRoots(thisSourceRoot, info.getFileObject());

        //XXX: special case "this" source root (no need to create a new JS and load the classes again for it):
//        reverseSourceRoots.add(thisSourceRoot);

//        LOG.log(Level.FINE, "reverseSourceRoots: {0}", reverseSourceRoots); //NOI18N

//                if (LOG.isLoggable(Level.FINE)) {
//                    LOG.log(Level.FINE, "method: {0}", ee.toString()); //NOI18N
//                }


        final Map<ElementHandle<ExecutableElement>, List<ElementDescription>> overriding = new HashMap<ElementHandle<ExecutableElement>, List<ElementDescription>>();
        final List<ElementDescription> overridingClasses = new ArrayList<ElementDescription>();

        long startTime = System.currentTimeMillis();
        long[] classIndexTime = new long[1];
        ElementHandle<TypeElement> sourceTypeHandle = ElementHandle.create((TypeElement) /*!!!*/ ee.getEnclosingElement());
        final Map<URL, Set<ElementHandle<TypeElement>>> users = computeUsers(reverseSourceRoots, sourceTypeHandle, classIndexTime);
        long endTime = System.currentTimeMillis();

        if (users == null) {
            return null;
        }

//                Logger.getLogger("TIMER").log(Level.FINE, "Overridden Users Class Index", //NOI18N
//                    new Object[] {file, classIndexTime[0]});
//                Logger.getLogger("TIMER").log(Level.FINE, "Overridden Users", //NOI18N
//                    new Object[] {file, endTime - startTime});

        for (Map.Entry<URL, Set<ElementHandle<TypeElement>>> data : users.entrySet()) {
            findOverriddenAnnotations(data.getKey(), data.getValue(), sourceTypeHandle, Collections.singletonList(ElementHandle.create(ee)), overriding, overridingClasses);
        }

        if (overriding.values().isEmpty()) {
            return null;
        }
        
        return overriding.values().iterator().next();
    }
    private static final ClassPath EMPTY = ClassPathSupport.createClassPath(new URL[0]);

    private static Set<ElementHandle<TypeElement>> computeUsers(URL source, Set<ElementHandle<TypeElement>> base, long[] classIndexCumulative) {
        ClasspathInfo cpinfo = ClasspathInfo.create(/*source);/*/EMPTY, EMPTY, ClassPathSupport.createClassPath(source));

        long startTime = System.currentTimeMillis();

        try {
            List<ElementHandle<TypeElement>> l = new LinkedList<ElementHandle<TypeElement>>(base);
            Set<ElementHandle<TypeElement>> result = new HashSet<ElementHandle<TypeElement>>();

            while (!l.isEmpty()) {
                ElementHandle<TypeElement> eh = l.remove(0);

                result.add(eh);
                Set<ElementHandle<TypeElement>> typeElements = cpinfo.getClassIndex().getElements(eh, Collections.singleton(SearchKind.IMPLEMENTORS), EnumSet.of(ClassIndex.SearchScope.SOURCE));
                //XXX: Canceling
                if (typeElements != null) {
                    l.addAll(typeElements);
                }
            }
            return result;
        } finally {
            classIndexCumulative[0] += (System.currentTimeMillis() - startTime);
        }
    }

    private static Map<URL, Set<ElementHandle<TypeElement>>> computeUsers(Set<URL> sourceRootsSet, ElementHandle<TypeElement> base, long[] classIndexCumulative) {
        Map<URL, List<URL>> deps = getDependencies();

        if (deps == null) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(GoToImplementation.class, "ERR_NoDependencies"), NotifyDescriptor.ERROR_MESSAGE);
            
            DialogDisplayer.getDefault().notifyLater(nd);
            return null;
        }
        
        List<URL> sourceRoots;
        try {
            sourceRoots = new LinkedList<URL>(Utilities.topologicalSort(deps.keySet(), deps));
        } catch (TopologicalSortException ex) {
            Exceptions.attachLocalizedMessage(ex,NbBundle.getMessage(GoToImplementation.class, "ERR_CycleInDependencies"));
            Exceptions.printStackTrace(ex);
            return null;
        }

        sourceRoots.retainAll(sourceRootsSet);

        Map<URL, Set<ElementHandle<TypeElement>>> result = new HashMap<URL, Set<ElementHandle<TypeElement>>>();

        for (URL file : sourceRoots) {
            Set<ElementHandle<TypeElement>> baseTypes = new HashSet<ElementHandle<TypeElement>>();

            baseTypes.add(base);

            for (URL dep : deps.get(file)) {
                Set<ElementHandle<TypeElement>> depTypes = result.get(dep);

                if (depTypes != null) {
                    baseTypes.addAll(depTypes);
                }
            }

            Set<ElementHandle<TypeElement>> types = computeUsers(file, baseTypes, classIndexCumulative);

            types.removeAll(baseTypes);

            result.put(file, types);
        }

        return result;
    }

    private static void findOverriddenAnnotations(
            URL sourceRoot,
            final Set<ElementHandle<TypeElement>> users,
            final ElementHandle<TypeElement> originalType,
            final List<ElementHandle<ExecutableElement>> methods,
            final Map<ElementHandle<ExecutableElement>, List<ElementDescription>> overriding,
            final List<ElementDescription> overridingClasses) {
        if (!users.isEmpty()) {
            FileObject sourceRootFile = URLMapper.findFileObject(sourceRoot);
            ClasspathInfo cpinfo = ClasspathInfo.create(sourceRootFile);

            JavaSource js = JavaSource.create(cpinfo);

            try {
                js.runUserActionTask(new Task<CompilationController>() {

                    public void run(CompilationController controller) throws Exception {
                        Set<Element> seenElements = new HashSet<Element>();
                        Element resolvedOriginalType = originalType.resolve(controller);

                        for (ElementHandle<TypeElement> typeHandle : users) {
                            TypeElement type = typeHandle.resolve(controller);

                            if (!seenElements.add(type)) {
                                continue;
                            }

                            Types types = controller.getTypes();

                            if (types.isSubtype(types.erasure(type.asType()), types.erasure(resolvedOriginalType.asType()))) {
                                overridingClasses.add(new ElementDescription(controller, type));

                                for (ElementHandle<ExecutableElement> originalMethodHandle : methods) {
                                    ExecutableElement originalMethod = originalMethodHandle.resolve(controller);

                                    if (originalMethod != null) {
                                        ExecutableElement overrider = getImplementationOf(controller, originalMethod, type);

                                        if (overrider == null) {
                                            continue;
                                        }

                                        List<ElementDescription> overriddingMethods = overriding.get(originalMethodHandle);

                                        if (overriddingMethods == null) {
                                            overriding.put(originalMethodHandle, overriddingMethods = new ArrayList<ElementDescription>());
                                        }

                                        overriddingMethods.add(new ElementDescription(controller, overrider));
                                    } else {
                                        Logger.getLogger("global").log(Level.SEVERE, "IsOverriddenAnnotationHandler: originalMethod == null!"); //NOI18N
                                    }
                                }
                            }
                        }
                    }
                }, true);
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }
    }

    private static ExecutableElement getImplementationOf(CompilationInfo info, ExecutableElement overridee, TypeElement implementor) {
        for (ExecutableElement overrider : ElementFilter.methodsIn(implementor.getEnclosedElements())) {
            if (info.getElements().overrides(overrider, overridee, implementor)) {
                return overrider;
            }
        }

        return null;
    }

    static void performGoToAction(List<ElementDescription> declarations, Point position) {
        String caption = NbBundle.getMessage(GoToImplementation.class, "LBL_ImplementorsOverriders");
        
        PopupUtil.showPopup(new IsOverriddenPopup(caption, declarations), caption, position.x, position.y, true, 0);
    }

    private static ExecutableElement resolveMethod(CompilationInfo info, int caret) {
        TreePath tp = info.getTreeUtilities().pathFor(caret);

        Element  elementUnderCaret = info.getTrees().getElement(tp);

        if (elementUnderCaret != null && elementUnderCaret.getKind() == ElementKind.METHOD) {
            return (ExecutableElement) elementUnderCaret;
        }

        ExecutableElement ee = resolveMethodHeader(tp, info, caret);

        if (ee == null) {
            tp = info.getTreeUtilities().pathFor(caret + 1);
            ee = resolveMethodHeader(tp, info, caret);
        }

        return ee;
    }
    
    private static ExecutableElement resolveMethodHeader(TreePath tp, CompilationInfo info, int caret) {
        while (tp.getLeaf().getKind() != Kind.METHOD && tp.getLeaf().getKind() != Kind.COMPILATION_UNIT) {
            tp = tp.getParentPath();
        }

        if (tp.getLeaf().getKind() == Kind.COMPILATION_UNIT) {
            return null;
        }

        MethodTree mt = (MethodTree) tp.getLeaf();
        SourcePositions sp = info.getTrees().getSourcePositions();
        BlockTree body = mt.getBody();
        long bodyStart = body != null ? sp.getStartPosition(info.getCompilationUnit(), body) : Integer.MAX_VALUE;

        if (caret >= bodyStart) {
            return null;
        }

        Element el = info.getTrees().getElement(tp);

        if (el == null || el.getKind() != ElementKind.METHOD) {
            return null;
        }
        
        return (ExecutableElement) el;
    }

    private static Map<URL, List<URL>> getDependencies() {
        ClassLoader l = Lookup.getDefault().lookup(ClassLoader.class);

        if (l == null) {
            return null;
        }

        Class clazz = null;
        String method = null;
        
        try {
            clazz = l.loadClass("org.netbeans.modules.parsing.impl.indexing.friendapi.IndexingController");
            method = "getRootDependencies";
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, ex);
            try {
                clazz = l.loadClass("org.netbeans.modules.parsing.impl.indexing.RepositoryUpdater");
                method = "getDependencies";
            } catch (ClassNotFoundException inner) {
                Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, inner);
                return null;
            }
        }

        try {
            Method getDefault = clazz.getDeclaredMethod("getDefault");
            Object instance = getDefault.invoke(null);
            Method dependenciesMethod = clazz.getDeclaredMethod(method);

            return (Map<URL, List<URL>>) dependenciesMethod.invoke(instance);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, ex);
            return null;
        } catch (InvocationTargetException ex) {
            Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, ex);
            return null;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, ex);
            return null;
        } catch (SecurityException ex) {
            Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, ex);
            return null;
        } catch (ClassCastException ex) {
            Logger.getLogger(GoToImplementation.class.getName()).log(Level.FINE, null, ex);
            return null;
        }
    }

}
