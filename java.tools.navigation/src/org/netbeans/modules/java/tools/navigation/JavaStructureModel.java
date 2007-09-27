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

package org.netbeans.modules.java.tools.navigation;

import com.sun.javadoc.Doc;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.UiUtils;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class JavaStructureModel extends DefaultTreeModel {
    static Element[] EMPTY_ELEMENTS_ARRAY = new Element[0];
    static ElementHandle[] EMPTY_ELEMENTHANDLES_ARRAY = new ElementHandle[0];

    /**
     * Holds value of property pattern.
     */
    private String pattern = ""; // NOI18N
    private String patternLowerCase = ""; // NOI18N
    private FileObject fileObject;
    private ElementHandle[] elementHandles;

    /** Creates a new instance of JavaStructureModel */
    public JavaStructureModel(FileObject fileObject, Element[] elements,
        CompilationInfo compilationInfo) {
        super(null);
        this.fileObject = fileObject;

        if ((elements == null) || (elements.length == 0)) {
            elementHandles = EMPTY_ELEMENTHANDLES_ARRAY;
        } else {
            List<ElementHandle> elementHandlesList = new ArrayList<ElementHandle>(elements.length);

            for (Element element : elements) {
                elementHandlesList.add(ElementHandle.create(element));
            }

            elementHandles = elementHandlesList.toArray(EMPTY_ELEMENTHANDLES_ARRAY);
        }

        update(elements, compilationInfo);
    }

    /**
     * Getter for property pattern.
     * @return Value of property pattern.
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * Setter for property pattern.
     * @param pattern New value of property pattern.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;

        if (pattern == null) {
            patternLowerCase = null;
        } else {
            patternLowerCase = pattern.toLowerCase();
        }
    }

    public void update() {
        update(elementHandles);
    }

    private void update(final ElementHandle[] elementHandles) {
        if ((elementHandles == null) && (elementHandles.length == 0)) {
            return;
        }

        JavaSource javaSource = JavaSource.forFileObject(fileObject);

        if (javaSource != null) {
            try {
                javaSource.runUserActionTask(new CancellableTask<CompilationController>() {
                        public void cancel() {
                        }

                        public void run(
                            CompilationController compilationController)
                            throws Exception {
                            compilationController.toPhase(Phase.ELEMENTS_RESOLVED);

                            List<Element> elementsList = new ArrayList<Element>(elementHandles.length);

                            for (ElementHandle elementHandle : elementHandles) {
                                elementsList.add(elementHandle.resolve(
                                        compilationController));
                            }

                            Element[] elements = elementsList.toArray(EMPTY_ELEMENTS_ARRAY);
                            update(elements, compilationController);
                        }
                    }, false);

                return;
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }
        }
    }

    private void update(final Element[] elements,
        CompilationInfo compilationInfo) {
        if ((elements == null) && (elements.length == 0)) {
            return;
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        for (Element element : elements) {
            if (element.getKind() == ElementKind.PACKAGE) {
                root.add(new PackageTreeNode(fileObject,
                        ((PackageElement) element), compilationInfo));
            } else if ((element.getKind() == ElementKind.CLASS) ||
                    (element.getKind() == ElementKind.INTERFACE) ||
                    (element.getKind() == ElementKind.ENUM)) {
                root.add(new TypeTreeNode(fileObject, ((TypeElement) element),
                        compilationInfo));
            } else if (element.getKind() == ElementKind.CONSTRUCTOR) {
                root.add(new ConstructorTreeNode(fileObject,
                        ((ExecutableElement) element), compilationInfo));
            } else if (element.getKind() == ElementKind.METHOD) {
                root.add(new MethodTreeNode(fileObject,
                        ((ExecutableElement) element), compilationInfo));
            } else if (element.getKind() == ElementKind.FIELD) {
                root.add(new FieldTreeNode(fileObject,
                        ((VariableElement) element), compilationInfo));
            } else if (element.getKind() == ElementKind.ENUM_CONSTANT) {
                root.add(new EnumConstantTreeNode(fileObject,
                        ((VariableElement) element), compilationInfo));
            }
        }

        setRoot(root);
    }

    public boolean patternMatch(JavaToolsJavaElement javaFileStructureElement) {
        String patternRegexpString = pattern;

        if (pattern == null) {
            return true;
        }

        if (pattern.trim().length() == 0) {
            patternRegexpString = pattern + ".*";
        } else {
            patternRegexpString = pattern.replaceAll("\\Q*\\E", ".*")
                                         .replaceAll("\\Q?\\E", ".") +
                (pattern.endsWith("$") ? "" : ".*");
        }

        String name = javaFileStructureElement.getName();

        try {
            Pattern compiledPattern = Pattern.compile(patternRegexpString,
                    JavaStructureOptions.isCaseSensitive() ? 0
                                                           : Pattern.CASE_INSENSITIVE);
            Matcher m = compiledPattern.matcher(name);

            return m.matches();
        } catch (PatternSyntaxException pse) {
            if (JavaStructureOptions.isCaseSensitive()) {
                return name.startsWith(pattern);
            }

            return name.toLowerCase().startsWith(patternLowerCase);
        }
    }

    private static String getClassName(String className) {
        // Handle generic type names i.e. strip off parameters
        int firstLessThan = className.indexOf('<');

        if (firstLessThan != -1) {
            className = className.substring(0, firstLessThan);
        }

        if (!JavaStructureOptions.isShowFQN()) {
            int lastDot = className.lastIndexOf('.');

            if (lastDot != -1) {
                className = className.substring(lastDot + 1);
            }
        }

        return className;
    }

    private static String getClassNameSansPackage(String className) {
        // Handle generic type names i.e. strip off parameters
        int firstLessThan = className.indexOf('<');

        if (firstLessThan != -1) {
            className = className.substring(0, firstLessThan);
        }

        int lastDot = className.lastIndexOf('.');

        if (lastDot != -1) {
            className = className.substring(lastDot + 1);
        }

        return className;
    }

    private abstract class AbstractJavaFileStructureTreeNode
        extends DefaultMutableTreeNode implements JavaToolsJavaElement {
        private FileObject fileObject;
        private ElementHandle<?extends Element> elementHandle;
        private ElementKind elementKind;
        private Set<Modifier> modifiers;
        private String name = "";
        private String label = "";
        private String tooltip = null;
        private Icon icon = null;
        private String javaDoc = "";

        AbstractJavaFileStructureTreeNode(FileObject fileObject,
            Element element, CompilationInfo compilationInfo) {
            this.fileObject = fileObject;
            this.elementHandle = ElementHandle.create(element);
            this.elementKind = element.getKind();
            this.modifiers = element.getModifiers();

            setName(element.getSimpleName().toString());
            setIcon(UiUtils.getElementIcon(element.getKind(),
                    element.getModifiers()));
            setLabel(Utils.format(element));
            setToolTip(Utils.format(element, true));
            Doc doc = compilationInfo.getElementUtilities().javaDocFor(element);
            if (doc != null) {
                StringBuilder stringBuilder = new StringBuilder();
                setJavaDoc(doc.getRawCommentText());
            }
            loadChildren(element, compilationInfo);
        }

        public FileObject getFileObject() {
            return fileObject;
        }

        public String getName() {
            return name;
        }

        protected void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        protected void setLabel(String label) {
            this.label = label;
        }

        public String getTooltip() {
            return tooltip;
        }

        protected void setToolTip(String tooltip) {
            this.tooltip = tooltip;
        }

        public Icon getIcon() {
            return icon;
        }

        protected void setIcon(Icon icon) {
            this.icon = icon;
        }

        protected void setElementHandle(
            ElementHandle<?extends Element> elementHandle) {
            this.elementHandle = elementHandle;
        }

        public String getJavaDoc() {
            return javaDoc;
        }

        public void setJavaDoc(String javaDoc) {
            this.javaDoc = javaDoc;
        }

        public Set<Modifier> getModifiers() {
            return modifiers;
        }

        public ElementHandle getElementHandle() {
            return elementHandle;
        }

        public void gotoElement() {
            openElementHandle();
        }

        protected abstract void loadChildren(Element element,
            CompilationInfo compilationInfo);

        public String toString() {
            return getLabel();
        }

        protected void openElementHandle() {
            if (elementHandle == null) {
                return;
            }

            UiUtils.open(fileObject, elementHandle);
        }
    }

    private class PackageTreeNode extends AbstractJavaFileStructureTreeNode {
        PackageTreeNode(FileObject fileObject, PackageElement packageElement,
            CompilationInfo compilationInfo) {
            super(fileObject, packageElement, compilationInfo);
        }

        public boolean isLeaf() {
            return true;
        }

        protected void loadChildren(Element element,
            CompilationInfo compilationInfo) {
        }
    }

    private class TypeTreeNode extends AbstractJavaFileStructureTreeNode {
        private boolean inSuperClassRole;

        TypeTreeNode(FileObject fileObject, TypeElement typeElement,
            CompilationInfo compilationInfo) {
            this(fileObject, typeElement, compilationInfo, false);
        }

        TypeTreeNode(FileObject fileObject, TypeElement typeElement,
            CompilationInfo compilationInfo, boolean inSuperClassRole) {
            super(fileObject, typeElement, compilationInfo);
            this.inSuperClassRole = inSuperClassRole;
        }

        public boolean isLeaf() {
            return false;
        }

        protected void loadChildren(Element element,
            CompilationInfo compilationInfo) {
            loadChildren(element, compilationInfo, 0);
        }

        protected int loadChildren(Element element,
            CompilationInfo compilationInfo, int index) {
            TypeElement typeElement = (TypeElement) element;

            List<?extends Element> enclosedElements = typeElement.getEnclosedElements();

            for (Element enclosedElement : enclosedElements) {
                AbstractJavaFileStructureTreeNode node = null;

                if (enclosedElement.getKind() == ElementKind.CLASS) {
                    if (!JavaStructureOptions.isShowInner()) {
                        continue;
                    }

                    if (JavaStructureOptions.isShowInherited()) {
                        continue;
                    }

                    node = new TypeTreeNode(getFileObject(),
                            (TypeElement) enclosedElement, compilationInfo);
                } else {
                    Set<Modifier> modifiers = enclosedElement.getModifiers();

                    if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                        if (!JavaStructureOptions.isShowConstructors()) {
                            continue;
                        }

                        ExecutableElement constructor = (ExecutableElement) enclosedElement;

                        if ((!modifiers.contains(Modifier.PUBLIC) &&
                                !modifiers.contains(Modifier.PROTECTED) &&
                                !modifiers.contains(Modifier.PRIVATE)) &&
                                !JavaStructureOptions.isShowPackage()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PROTECTED) &&
                                !JavaStructureOptions.isShowProtected()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PRIVATE) &&
                                !JavaStructureOptions.isShowPrivate()) {
                            continue;
                        }

                        node = new ConstructorTreeNode(getFileObject(),
                                constructor, compilationInfo);
                    } else if (enclosedElement.getKind() == ElementKind.METHOD) {
                        if (!JavaStructureOptions.isShowMethods()) {
                            continue;
                        }

                        ExecutableElement method = (ExecutableElement) enclosedElement;

                        if ((!modifiers.contains(Modifier.PUBLIC) &&
                                !modifiers.contains(Modifier.PROTECTED) &&
                                !modifiers.contains(Modifier.PRIVATE)) &&
                                !JavaStructureOptions.isShowPackage()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PROTECTED) &&
                                !JavaStructureOptions.isShowProtected()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PRIVATE) &&
                                !JavaStructureOptions.isShowPrivate()) {
                            continue;
                        }

                        if (modifiers.contains(Modifier.STATIC) &&
                                !JavaStructureOptions.isShowStatic()) {
                            continue;
                        }

                        node = new MethodTreeNode(getFileObject(), method,
                                compilationInfo);
                    } else if (enclosedElement.getKind() == ElementKind.FIELD) {
                        if (!JavaStructureOptions.isShowFields()) {
                            continue;
                        }

                        VariableElement field = (VariableElement) enclosedElement;

                        if ((!modifiers.contains(Modifier.PUBLIC) &&
                                !modifiers.contains(Modifier.PROTECTED) &&
                                !modifiers.contains(Modifier.PRIVATE)) &&
                                !JavaStructureOptions.isShowPackage()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PROTECTED) &&
                                !JavaStructureOptions.isShowProtected()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PRIVATE) &&
                                !JavaStructureOptions.isShowPrivate()) {
                            continue;
                        }

                        if (modifiers.contains(Modifier.STATIC) &&
                                !JavaStructureOptions.isShowStatic()) {
                            continue;
                        }

                        node = new FieldTreeNode(getFileObject(), field,
                                compilationInfo);
                    } else if (enclosedElement.getKind() == ElementKind.ENUM_CONSTANT) {
                        if (!JavaStructureOptions.isShowEnumConstants()) {
                            continue;
                        }

                        VariableElement enumConstant = (VariableElement) enclosedElement;

                        if ((!modifiers.contains(Modifier.PUBLIC) &&
                                !modifiers.contains(Modifier.PROTECTED) &&
                                !modifiers.contains(Modifier.PRIVATE)) &&
                                !JavaStructureOptions.isShowPackage()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PROTECTED) &&
                                !JavaStructureOptions.isShowProtected()) {
                            continue;
                        } else if (modifiers.contains(Modifier.PRIVATE) &&
                                !JavaStructureOptions.isShowPrivate()) {
                            continue;
                        }

                        if (modifiers.contains(Modifier.STATIC) &&
                                !JavaStructureOptions.isShowStatic()) {
                            continue;
                        }

                        node = new EnumConstantTreeNode(getFileObject(),
                                enumConstant, compilationInfo);
                    }

                    if ((node == null) || !patternMatch(node)) {
                        continue;
                    }
                }

                if (node == null) {
                    continue;
                }

                insert(node, index++);
            }

            if (JavaStructureOptions.isShowInherited()) {
                TypeMirror superClassTypeMirror = typeElement.getSuperclass();

                if (superClassTypeMirror.getKind() == TypeKind.NONE) {
                    //
                } else {
                    TypeElement superClass = (TypeElement) ((DeclaredType) typeElement.getSuperclass()).asElement();

                    if ((superClass != null) &&
                            !superClass.getQualifiedName().toString()
                                           .equals(Object.class.getName())) {
                        FileObject fileObject = SourceUtils.getFile(superClass,
                                compilationInfo.getClasspathInfo());
                        insert(new TypeTreeNode(fileObject, superClass,
                                compilationInfo, true), index++);
                    }
                }

                List<?extends TypeMirror> interfaces = typeElement.getInterfaces();

                for (TypeMirror interfaceTypeMirror : interfaces) {
                    TypeElement anInterface = (TypeElement) ((DeclaredType) interfaceTypeMirror).asElement();
                    FileObject fileObject = SourceUtils.getFile(anInterface,
                            compilationInfo.getClasspathInfo());
                    insert(new TypeTreeNode(fileObject, anInterface,
                            compilationInfo, true), index++);
                }
            }

            if (JavaStructureOptions.isShowInner()) {
                if (!inSuperClassRole &&
                        JavaStructureOptions.isShowInherited()) {
                    for (Element enclosedElement : enclosedElements) {
                        if (enclosedElement.getKind() == ElementKind.CLASS) {
                            AbstractJavaFileStructureTreeNode node = new TypeTreeNode(getFileObject(),
                                    (TypeElement) enclosedElement,
                                    compilationInfo);
                            insert(node, index++);
                        }
                    }
                }
            }

            return index;
        }
    }

    private class ConstructorTreeNode extends AbstractJavaFileStructureTreeNode {
        ConstructorTreeNode(FileObject fileObject,
            ExecutableElement contructorElement, CompilationInfo compilationInfo) {
            super(fileObject, contructorElement, compilationInfo);
        }

        public boolean isLeaf() {
            return true;
        }

        protected void loadChildren(Element element,
            CompilationInfo compilationInfo) {
        }
    }

    private class MethodTreeNode extends AbstractJavaFileStructureTreeNode {
        MethodTreeNode(FileObject fileObject, ExecutableElement methodElement,
            CompilationInfo compilationInfo) {
            super(fileObject, methodElement, compilationInfo);
        }

        public boolean isLeaf() {
            return true;
        }

        protected void loadChildren(Element element,
            CompilationInfo compilationInfo) {
        }
    }

    private class FieldTreeNode extends AbstractJavaFileStructureTreeNode {
        FieldTreeNode(FileObject fileObject, VariableElement variableElement,
            CompilationInfo compilationInfo) {
            super(fileObject, variableElement, compilationInfo);
        }

        public boolean isLeaf() {
            return true;
        }

        protected void loadChildren(Element element,
            CompilationInfo compilationInfo) {
        }
    }

    private class EnumConstantTreeNode extends AbstractJavaFileStructureTreeNode {
        EnumConstantTreeNode(FileObject fileObject,
            VariableElement variableElement, CompilationInfo compilationInfo) {
            super(fileObject, variableElement, compilationInfo);
        }

        public boolean isLeaf() {
            return true;
        }

        protected void loadChildren(Element element,
            CompilationInfo compilationInfo) {
        }
    }
}
