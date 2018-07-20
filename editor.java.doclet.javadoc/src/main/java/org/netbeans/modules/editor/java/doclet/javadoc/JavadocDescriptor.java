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
 * Software is Leon Chiver. All Rights Reserved.
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

package org.netbeans.modules.editor.java.doclet.javadoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.ClassMember;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Feature;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.JavaPackage;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.NamedElement;
import org.netbeans.jmi.javamodel.Parameter;
import org.netbeans.modules.editor.java.JMIUtils;
import org.netbeans.modules.editor.java.doclet.support.DocletCompletionItem;
import org.netbeans.modules.editor.java.doclet.DocletDescriptor;
import org.netbeans.modules.editor.java.doclet.ast.Attribute;
import org.netbeans.modules.editor.java.doclet.ast.Javadoc;
import org.netbeans.modules.editor.java.doclet.ast.Tag;
import org.netbeans.modules.javacore.ClassIndex;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.filesystems.FileObject;

/**
 * @author leon chiver
 */
public class JavadocDescriptor implements DocletDescriptor {

    private final static int SORT_PRIORITY = -100;
    
    private static final String[] TAGS_OF_METHOD = {
        "param", "return", "throws", "exception", "see", "deprecated", "since" // NOI18N
    };
    
    private static final String[] INLINE_TAGS_OF_METHOD = {
        "code", "literal", "link", "linkplain", "inheritDoc", "docRoot" // NOI18N
    };
    
    private static final String[] TAGS_OF_CONSTRUCTOR = {
        "param", "throws", "exception", "see", "deprecated", "since" // NOI18N
    };
    
    private static final String[] TAGS_OF_FIELD = {
        "deprecated", "see", "since", "serial", "serialField" // NOI18N
    };
    
    private static final String[] INLINE_TAGS_OF_FIELD = {
        "code", "literal", "link", "linkplain", "value", "docRoot" // NOI18N
    };
    
    private static final String[] TAGS_OF_CLASS = {
        "author", "version", "see", "serial", "since", "deprecated" // NOI18N
    };
    
    private static final String[] INLINE_TAGS_OF_CLASS = {
        "code", "literal", "link", "linkplain", "docRoot" // NOI18N
    };
    
    private static Set singleAppearanceTags;
    
    static {
        singleAppearanceTags = new HashSet();
        singleAppearanceTags.add("deprecated");
        singleAppearanceTags.add("since");
        singleAppearanceTags.add("serial");
        singleAppearanceTags.add("return");
        singleAppearanceTags.add("version");
    }
    
    public JavadocDescriptor() {
    }
    
    public String getCompletionPrefix() {
        return null;
    }

    public void addTags(
            BaseDocument doc, int offset, Field field, 
            Tag currentTag, Javadoc jd, List result) {
        addTags(currentTag, jd, TAGS_OF_FIELD, result);
    }

    public void addContents(
            BaseDocument doc, int offset, Field field, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result) {
        // No contents for now
    }

    public void addAttributeValues(
            BaseDocument doc, int offset, Field field, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result) {
        // No attributes for now
    }

    public void addTags(
            BaseDocument doc, int offset, ClassDefinition def, 
            Tag currentTag, Javadoc jd, List result) {
        addTags(currentTag, jd, TAGS_OF_CLASS, result);
    }

    public void addContents(
            BaseDocument doc, int offset, ClassDefinition def, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result) {
        // For the author tag get the user.name system property
        // XXX - this could be set up in the preferences
        String tagName = currentTag.getName();
        String currentWord = currentAttribute != null ? currentAttribute.getName() : null;
        if ("author".equals(tagName)) {
            String firstAttr = getFirstAttributeValue(currentTag);
            String userName = System.getProperty("user.name");
            if (firstAttr != null && (!firstAttr.equals(currentWord) || !userName.startsWith(currentWord))) {
                return;
            }
            result.add(DocletCompletionItem.createTextItem(
                    userName, userName, 0));
        } else if ("see".equals(tagName)) {
            addSeeTagContents(jd, currentWord, def, doc, result);
        } 
    }

    public void addAttributeValues(
            BaseDocument doc, int offset, ClassDefinition def, 
            Tag currentTag, Attribute currentAttribute, Javadoc jd, List result) {
        // Not interesting for javadoc
    }

    public void addTags(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Javadoc jd, List result) {
        String[] tags = (callableFeat instanceof Constructor) ?
                TAGS_OF_CONSTRUCTOR : TAGS_OF_METHOD;
        addTags(currentTag, jd, tags, result);
    }
    
    private void addTags(Tag currentTag, Javadoc jd, String[] tags, List result) {
        String currentTagText = currentTag != null ? currentTag.getName() : null;
        for (int i = 0; i < tags.length; i++) {
            String tagName = tags[i];
            if (isValidCompletion(tagName, currentTagText, true) &&
                    canTagOccur(tagName, jd)) {
                tagName = "@" + tagName;
                result.add(DocletCompletionItem.createTextItem(
                        tagName, tagName, i));
            }
        }
    }
    
    private String getFirstAttributeValue(Tag tag) {
        List attrList = tag.getAttributeList();
        String firstAttr = attrList != null && attrList.size() > 0 ?
            ((Attribute) attrList.get(0)).getName() : null;
        return firstAttr;
    }

    public void addContents(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Attribute currentAttribute, Javadoc jd, List result) {
        String name = currentTag.getName();
        // If the current word (where completion was invoked) is different from the first attribute
        // it means that we're invoking completion after the tag's first argument; we shall not
        // offer any completion items then
        // For example @throws IOException | <-- invoke completion here
        String firstAttr = getFirstAttributeValue(currentTag);
        String currentWord = currentAttribute != null ? currentAttribute.getName() : null;
        if (firstAttr != null && !firstAttr.equals(currentWord)) {
            return;
        }
        if ("param".equals(name)) {
            Set existing = getTagArguments(jd, name);
            addMissingAttributes(
                    (NamedElement[]) callableFeat.getParameters().toArray(new Parameter[0]),
                    existing, result, currentWord);
        } else if ("throws".equals(name) || "exception".equals(name)) {
            Set existing = getTagArguments(jd, name);
            addMissingAttributes(
                    (NamedElement[]) callableFeat.getExceptions().toArray(new JavaClass[0]),
                    existing, result, currentWord);
        } else if ("see".equals(name)) {
            addSeeTagContents(jd, currentWord, callableFeat, doc, result);
        }
    }

    private void addSeeTagContents(Javadoc jd, String currentWord, Element elem, 
            BaseDocument doc, List result) {
        String className = null;
        String methodNamePrefix;
        int hashIndex = currentWord != null ? currentWord.lastIndexOf("#") : -1;
        if (hashIndex != -1) {
            className = currentWord.substring(0, hashIndex);
            methodNamePrefix = currentWord.substring(hashIndex + 1);
        } else {
            methodNamePrefix = currentWord;
        }
        addSeeTagClassMembers(jd, elem, className, methodNamePrefix, result);
        if (currentWord != null && currentWord.length() > 0) {
            // Don't add classes to the completion if no word is typed
            // because of performance reasons
            addSeeTagClasses(doc, null, jd, currentWord, result);
        }
    }
    
    public void addAttributeValues(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Attribute currentAttribute, Javadoc jd, List result) {
        // Not interesting for javadoc
    }
        
    private void addMissingAttributes(NamedElement[] elements, Set existingNames, List result, String wordStart) {
        for (int i = 0; i < elements.length; i++) {
            NamedElement ne = (NamedElement) elements[i];
            String name = ne.getName();
            if (!existingNames.contains(name) && isValidCompletion(name, wordStart, false)) {
                result.add(DocletCompletionItem.createTextItem(
                        " " + name, name, SORT_PRIORITY + i));
            }
        }
    }
        
    private static Set getTagArguments(Javadoc doc, String tagName) {
        List tags = doc.getTags();
        if (tags == null || tags.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        int sz = tags.size();
        Set names = new HashSet();
        for (int i = 0; i < sz; i++) {
            Tag t = (Tag) tags.get(i);
            if (!tagName.equals(t.getName())) {
                continue;
            }
            List attrList = t.getAttributeList();
            if (attrList != null && attrList.size() > 0) {
                Attribute attr = (Attribute) attrList.get(0);
                names.add(attr.getName());
            }
        }
        return names;
    }

    private static boolean canTagOccur(String possibleTag, Javadoc doc) {
        return !singleAppearanceTags.contains(possibleTag) ||
                !doc.containsTag(possibleTag);
    }
    
    private static boolean isValidCompletion(String completion, String typedText, boolean isTag) {
        return typedText == null || typedText.length() == 0 || 
                (isTag && completion.equals("@")) || 
                (completion.startsWith(typedText) && !typedText.equals(completion));
    }
    
    private void addSeeTagClasses(BaseDocument doc, JavaClass context, Javadoc jd, String namePrefix, List result) {
        ClassPath cp = JavaMetamodel.getManager().getClassPath();
        if (namePrefix == null) {
            namePrefix = "";
        }
        JMIUtils utils = JMIUtils.get(doc);
        JavaPackage pkg = null;
        int dotIndex = namePrefix.lastIndexOf(".");
        String pkgName = null;
        if (dotIndex != -1) {
            pkgName = namePrefix.substring(0, dotIndex);
            namePrefix = namePrefix.substring(dotIndex + 1);
            pkg = JavaModel.getDefaultExtent().getJavaPackage().resolvePackage(pkgName);
        }
        List classes = utils.findClasses(pkg, namePrefix, false, false, true, context, false, false);
        int sz = classes.size();
        for (int i = 0; i < sz; i++) {
            JavaClass clazz = (JavaClass) classes.get(i);
            String classPkgName = clazz.getResource().getPackageName();
            String name = "java.lang".equals(classPkgName) ? clazz.getSimpleName() : clazz.getName();
            String displayName = clazz.getSimpleName();
            if (classPkgName != null && classPkgName.length() > 0) {
                displayName += " (" + classPkgName + ")";
            }
            result.add(DocletCompletionItem.createSubstitutionItem(
                    name, 
                    pkgName != null ? pkgName + "." + namePrefix : namePrefix, 
                    displayName, SORT_PRIORITY));
        }
    }
    
    private void addSeeTagClassMembers(Javadoc jd, Element elem, String className, String memberPrefix, List result) {
        ClassDefinition def = null; 
        if (className != null && className.length() > 0) {
            def = (ClassDefinition) JavaModel.getDefaultExtent().getJavaClass().resolve(className);
        } else if (elem instanceof ClassDefinition) {
            def = (ClassDefinition) elem;
        } else if (elem instanceof ClassMember) {
            def = ((ClassMember) elem).getDeclaringClass();
        }
        if (def == null) {
            return;
        }
        List l = def.getChildren();
        Element[] children = (Element[]) l.toArray(new Element[0]);
        Set existing = getTagArguments(jd, "see");
        for (int i = 0; i < children.length; i++) {
            Element e = children[i];
            String completion = null;
            int priority = SORT_PRIORITY;
            if (e instanceof CallableFeature) {
                CallableFeature cf = (CallableFeature) e;
                if (cf.equals(elem)) {
                    continue;
                }
                String name;
                if (cf instanceof Constructor) {
                    name = ((JavaClass) ((Constructor) cf).getDeclaringClass()).getSimpleName();
                } else {
                    name = cf.getName();
                }
                if (isValidCompletion(name, memberPrefix, false)) {
                    completion = getSeeTagCompletionName(name, cf);
                }
                priority = SORT_PRIORITY - 1;
            } else if (e instanceof Field) {
                Field f = (Field) e;
                if (f.equals(elem)) {
                    continue;
                }
                String name = f.getName();
                if (isValidCompletion(name, memberPrefix, false)) {
                    completion = "#" + f.getName();
                }
                priority = SORT_PRIORITY;
            }
            if (completion == null || existing.contains(completion)) {
                continue;
            }
            result.add(DocletCompletionItem.createTextItem(
                    className != null ? completion : " " + completion, // If there is a class name we don't need a space before the item
                    completion, priority));
        }
    }
    
    private String getSeeTagCompletionName(String baseName, CallableFeature cf) {
        StringBuffer buff = new StringBuffer("#" + baseName);
        buff.append('(');
        Parameter[] params = (Parameter[]) cf.getParameters().toArray(new Parameter[0]);
        for (int i = 0; i < params.length; i++) {
            Parameter p = (Parameter) params[i];
            if (i != 0) {
                buff.append(", ");
            }
            String name = p.getType().getName();
            if (name.startsWith("java.lang.")) {
                name = name.substring(10);
            }
            buff.append(name);
        }
        buff.append(')');
        return buff.toString();
    }
}
