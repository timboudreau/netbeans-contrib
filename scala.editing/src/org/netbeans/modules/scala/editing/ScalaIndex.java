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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Index;
import org.netbeans.modules.gsf.api.Index.SearchResult;
import org.netbeans.modules.gsf.api.Index.SearchScope;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.nodes.tmpls.Template;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.URLMapper;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 * A wrapper of gsf index and JavaIndex
 *
 * @author Tor Norbye
 * @author Caoyuan Deng
 */
public class ScalaIndex {

    /** Set property to true to find ALL functions regardless of file includes */
    //private static final boolean ALL_REACHABLE = Boolean.getBoolean("javascript.findall");
    private static final boolean ALL_REACHABLE = !Boolean.getBoolean("javascript.checkincludes");
    private static String clusterUrl = null;
    private static final String CLUSTER_URL = "cluster:"; // NOI18N
    public static final Set<SearchScope> ALL_SCOPE = EnumSet.allOf(SearchScope.class);
    public static final Set<SearchScope> SOURCE_SCOPE = EnumSet.of(SearchScope.SOURCE);
    public static final Set<String> TERMS_FQN = Collections.singleton(ScalaIndexer.FIELD_FQN);
    public static final Set<String> TERMS_BASE = Collections.singleton(ScalaIndexer.FIELD_BASE);
    public static final Set<String> TERMS_EXTEND = Collections.singleton(ScalaIndexer.FIELD_EXTENDS_NAME);
    public static final Set<String> TERMS_IMPORT = Collections.singleton(ScalaIndexer.FIELD_IMPORT);
    public static final Set<String> TERMS_CLASS = Collections.singleton(ScalaIndexer.FIELD_CASE_INSENSITIVE_CLASS_NAME);
    private final Index index;
    private JavaIndex javaIndex;

    /** Creates a new instance of ScalaIndex */
    private ScalaIndex(Index index, JavaIndex javaIndex) {
        this.index = index;
        this.javaIndex = javaIndex;
    }

    private void setJavaIndex(JavaIndex javaIndex) {
        this.javaIndex = javaIndex;
    }

    public static ScalaIndex get(CompilationInfo info) {
        Index index = info.getIndex(ScalaMimeResolver.MIME_TYPE);
        ScalaIndex scalaIndex = new ScalaIndex(index, null);

        JavaIndex javaIndex = JavaIndex.get(info, scalaIndex);

        scalaIndex.setJavaIndex(javaIndex);

        return scalaIndex;
    }

    private boolean search(String key, String name, NameKind kind, Set<SearchResult> result,
            Set<SearchScope> scope, Set<String> terms) {
        try {
            index.search(key, name, kind, scope, result, terms);

            return true;
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);

            return false;
        }
    }

    static void setClusterUrl(String url) {
        clusterUrl = url;
    }

    static String getPreindexUrl(String url) {
        String s = getClusterUrl();

        if (url.startsWith(s)) {
            return CLUSTER_URL + url.substring(s.length());
        }

        return url;
    }

    /** Get the FileObject corresponding to a URL returned from the index */
    public static FileObject getFileObject(String url) {
        try {
            if (url.startsWith(CLUSTER_URL)) {
                url = getClusterUrl() + url.substring(CLUSTER_URL.length()); // NOI18N

            }

            return URLMapper.findFileObject(new URL(url));
        } catch (MalformedURLException mue) {
            Exceptions.printStackTrace(mue);
        }

        return null;
    }

    static String getClusterUrl() {
        if (clusterUrl == null) {
            File f =
                    InstalledFileLocator.getDefault().locate("modules/org-netbeans-modules-javascript-editing.jar", null, false); // NOI18N

            if (f == null) {
                throw new RuntimeException("Can't find cluster");
            }

            f = new File(f.getParentFile().getParentFile().getAbsolutePath());

            try {
                f = f.getCanonicalFile();
                clusterUrl = f.toURI().toURL().toExternalForm();
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        return clusterUrl;
    }

    @SuppressWarnings("unchecked")
    public Set<IndexedElement> getConstructors(final String name, NameKind kind,
            Set<SearchScope> scope) {
        // TODO - search by the FIELD_CLASS thingy
        return getUnknownFunctions(name, kind, scope, true, null, true, false);
    }

    @SuppressWarnings("unchecked")
    public Set<IndexedElement> getAllNames(final String name, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context) {
        // TODO - search by the FIELD_CLASS thingy
        return getUnknownFunctions(name, kind, scope, false, context, true, true);
    }

    public Map<String, String> getAllExtends() {
        final Set<SearchResult> result = new HashSet<SearchResult>();
        search(ScalaIndexer.FIELD_EXTENDS_NAME, "", NameKind.CASE_INSENSITIVE_PREFIX, result, ScalaIndex.ALL_SCOPE, TERMS_EXTEND);
        Map<String, String> classes = new HashMap<String, String>();
        for (SearchResult map : result) {
            String[] exts = map.getValues(ScalaIndexer.FIELD_EXTENDS_NAME);

            if (exts != null) {
                for (String ext : exts) {
                    int clzBegin = ext.indexOf(';') + 1;
                    int superBegin = ext.indexOf(';', clzBegin) + 1;

                    String clz = ext.substring(clzBegin, superBegin - 1);
                    String superClz = ext.substring(superBegin);
                    classes.put(clz, superClz);
                }
            }
        }

        return classes;
    }

    private String getExtends(String className, Set<Index.SearchScope> scope) {
        final Set<SearchResult> result = new HashSet<SearchResult>();
        search(ScalaIndexer.FIELD_EXTENDS_NAME, className.toLowerCase(), NameKind.CASE_INSENSITIVE_PREFIX, result, scope, TERMS_EXTEND);
        String target = className.toLowerCase() + ";";
        for (SearchResult map : result) {
            String[] exts = map.getValues(ScalaIndexer.FIELD_EXTENDS_NAME);

            if (exts != null) {
                for (String ext : exts) {
                    if (ext.startsWith(target)) {
                        // Make sure it's a case match
                        int caseIndex = target.length();
                        int end = ext.indexOf(';', caseIndex);
                        if (className.equals(ext.substring(caseIndex, end))) {
                            return ext.substring(end + 1);
                        }
                    }
                }
            }
        }

        return null;
    }

    public Set<String> getImports(String className, Set<Index.SearchScope> scope) {
        final Set<SearchResult> result = new HashSet<SearchResult>();
        search(ScalaIndexer.FIELD_IMPORT, className.toLowerCase(), NameKind.CASE_INSENSITIVE_PREFIX, result, scope, TERMS_IMPORT);
        String target = className.toLowerCase() + ";";
        for (SearchResult map : result) {
            String[] importAttrs = map.getValues(ScalaIndexer.FIELD_IMPORT);

            if (importAttrs != null) {
                Set<String> imports = new HashSet<String>();
                for (String importAttr : importAttrs) {
                    if (importAttr.startsWith(target)) {
                        // Make sure it's a case match
                        int caseIndex = target.length();
                        int caseEnd = importAttr.indexOf(';', caseIndex);
                        if (className.equals(importAttr.substring(caseIndex, caseEnd))) {
                            int pkgNameEnd = importAttr.indexOf(";", caseEnd + 1);
                            String pkgName = importAttr.substring(caseEnd + 1, pkgNameEnd);
                            int typeNameEnd = importAttr.indexOf(";", pkgNameEnd + 1);
                            String typeName = importAttr.substring(pkgNameEnd + 1, typeNameEnd);
                            // @todo only wild "_" ?
                            imports.add(pkgName);
                        }
                    }
                }
                return imports;
            }
        }

        return Collections.<String>emptySet();
    }

    /** Return both functions and properties matching the given prefix, of the
     * given (possibly null) type
     */
    public Set<IndexedElement> getElements(String prefix, String type,
            NameKind kind, Set<Index.SearchScope> scope, ScalaParserResult context,
            boolean onlyConstructors) {

        Set<IndexedElement> elements = getByFqn(prefix, type, kind, scope, onlyConstructors, context, true, true, false);
        // Is there at least one non-inheried member?
        boolean ofScala = false;
        for (IndexedElement element : elements) {
            if (!element.inherited) {
                ofScala = true;
                break;
            }
        }

        /** @TODO we need a better way to check if it's of scala */
        if (!ofScala) {
            elements = javaIndex.getByFqn(prefix, type, toJavaNameKind(kind), toJavaSearchScope(scope), onlyConstructors, context, true, true, false);
        }

        if (elements.size() == 0) {
            elements = javaIndex.getByFqn(prefix, "java.lang.Object", toJavaNameKind(kind), toJavaSearchScope(scope), onlyConstructors, context, true, true, false);
        }

        return elements;
    }

    public Set<IndexedElement> getAllElements(String prefix, String type,
            NameKind kind, Set<SearchScope> scope, ScalaParserResult context) {

        return getByFqn(prefix, type, kind, scope, false, context, true, true, true);
    }

    @SuppressWarnings("unchecked")
    public Set<IndexedFunction> getFunctions(String name, String in, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context, boolean includeMethods) {
        return (Set<IndexedFunction>) (Set) getByFqn(name, in, kind, scope, false, context, includeMethods, false, false);
    }

    public Set<IndexedElement> getPackageContent(String fqnPrefix, NameKind kind, Set<SearchScope> scope) {

        Set<IndexedElement> idxElements = getTypesByFqn(fqnPrefix, kind, scope, null, false, false, true);

        idxElements.addAll(javaIndex.getPackageContent(fqnPrefix));

        return idxElements;
    }

    public Set<IndexedElement> getPackagesAndContent(String fqnPrefix, NameKind kind, Set<SearchScope> scope) {

        Set<IndexedElement> idxElements = getTypesByFqn(fqnPrefix, kind, scope, null, false, false, false);

        idxElements.addAll(javaIndex.getPackages(fqnPrefix));
        idxElements.addAll(javaIndex.getPackageContent(fqnPrefix));

        return idxElements;
    }

    private Set<IndexedElement> getUnknownFunctions(String name, NameKind kind,
            Set<SearchScope> scope, boolean onlyConstructors, ScalaParserResult context,
            boolean includeMethods, boolean includeProperties) {

        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_BASE;
        Set<String> terms = TERMS_BASE;

        NameKind originalKind = kind;
        if (kind == NameKind.EXACT_NAME) {
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            kind = NameKind.PREFIX;
        }

        String lcname = name.toLowerCase();
        search(field, lcname, kind, result, scope, terms);

        final Set<IndexedElement> elements = new HashSet<IndexedElement>();
        String searchUrl = null;
        if (context != null) {
            try {
                searchUrl = context.getFile().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        for (SearchResult map : result) {
            String[] signatures = map.getValues(field);

            if (signatures != null) {
                // Check if this file even applies
                if (context != null) {
                    String fileUrl = map.getPersistentUrl();
                    if (searchUrl == null || !searchUrl.equals(fileUrl)) {
                        boolean isLibrary = fileUrl.indexOf("jsstubs") != -1; // TODO - better algorithm

                        if (!isLibrary && !isReachable(context, fileUrl)) {
                            continue;
                        }
                    }
                }

                for (String signature : signatures) {
                    // Lucene returns some inexact matches, TODO investigate why this is necessary
                    if ((kind == NameKind.PREFIX) && !signature.startsWith(lcname)) {
                        continue;
                    } else if (kind == NameKind.CASE_INSENSITIVE_PREFIX && !signature.regionMatches(true, 0, lcname, 0, lcname.length())) {
                        continue;
                    } else if (kind == NameKind.CASE_INSENSITIVE_REGEXP) {
                        int end = signature.indexOf(';');
                        assert end != -1;
                        String n = signature.substring(0, end);
                        try {
                            if (!n.matches(lcname)) {
                                continue;
                            }
                        } catch (Exception e) {
                            // Silently ignore regexp failures in the search expression
                        }
                    } else if (originalKind == NameKind.EXACT_NAME) {
                        // Make sure the name matches exactly
                        // We know that the prefix is correct from the first part of
                        // this if clause, by the signature may have more
                        if (((signature.length() > lcname.length()) &&
                                (signature.charAt(lcname.length()) != ';'))) {
                            continue;
                        }
                    } // TODO - check camel case here too!

                    // XXX THIS DOES NOT WORK WHEN THERE ARE IDENTICAL SIGNATURES!!!
                    assert map != null;

                    String elementName = null;
                    int nameEndIdx = signature.indexOf(';');
                    assert nameEndIdx != -1;
                    elementName = signature.substring(0, nameEndIdx);
                    nameEndIdx++;

                    String funcIn = null;
                    int inEndIdx = signature.indexOf(';', nameEndIdx);
                    assert inEndIdx != -1;
                    if (inEndIdx > nameEndIdx + 1) {
                        funcIn = signature.substring(nameEndIdx, inEndIdx);
                    }
                    inEndIdx++;

                    int startCs = inEndIdx;
                    inEndIdx = signature.indexOf(';', startCs);
                    assert inEndIdx != -1;
                    if (inEndIdx > startCs) {
                        // Compute the case sensitive name
                        elementName = signature.substring(startCs, inEndIdx);
                        if (kind == NameKind.PREFIX && !elementName.startsWith(name)) {
                            continue;
                        } else if (kind == NameKind.EXACT_NAME && !elementName.equals(name)) {
                            continue;
                        }
                    }
                    inEndIdx++;

                    // Filter out methods on other classes
                    if (!includeMethods && (funcIn != null)) {
                        continue;
                    }

                    String fqn = null; // Compute lazily

                    IndexedElement element = IndexedElement.create(signature, map.getPersistentUrl(), fqn, elementName, funcIn, inEndIdx, this, false);
                    boolean isFunction = element instanceof IndexedFunction;
                    if (isFunction && !includeMethods) {
                        continue;
                    } else if (onlyConstructors) {
                        if (element.getKind() == ElementKind.PROPERTY && funcIn == null && Character.isUpperCase(elementName.charAt(0))) {
                            //element.setKind(ElementKind.CONSTRUCTOR);
                        } else if (element.getKind() != ElementKind.CONSTRUCTOR) {
                            continue;
                        }
                    } else if (!isFunction && !includeProperties) {
                        continue;
                    }
                    elements.add(element);
                }
            }
        }

        return elements;
    }

    private Set<IndexedElement> getByFqn(String prefix, String type, NameKind kind,
            Set<SearchScope> scope, boolean onlyConstructors, ScalaParserResult context,
            boolean includeMethods, boolean includeProperties, boolean includeDuplicates) {
        //assert in != null && in.length() > 0;

        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_FQN;
        Set<String> terms = TERMS_FQN;
        NameKind originalKind = kind;
        if (kind == NameKind.EXACT_NAME) {
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            kind = NameKind.PREFIX;
        }

        if (kind == NameKind.CASE_INSENSITIVE_PREFIX || kind == NameKind.CASE_INSENSITIVE_REGEXP) {
            // TODO - can I do anything about this????
            //field = ScalaIndexer.FIELD_BASE_LOWER;
            //terms = FQN_BASE_LOWER;
        }

        final Set<IndexedElement> elements = includeDuplicates ? new DuplicateElementSet() : new HashSet<IndexedElement>();
        String searchUrl = null;
        if (context != null) {
            try {
                searchUrl = context.getFile().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        Set<String> seenTypes = new HashSet<String>();
        seenTypes.add(type);
        boolean haveRedirected = false;
        boolean inheriting = type == null;

        while (true) {

            String fqn = type != null && type.length() > 0
                    ? type + "." + prefix
                    : prefix;

            String lcfqn = fqn.toLowerCase();
            search(field, lcfqn, kind, result, scope, terms);

            for (SearchResult map : result) {
                String[] signatures = map.getValues(field);

                if (signatures != null) {
                    // Check if this file even applies
                    if (context != null) {
                        String fileUrl = map.getPersistentUrl();
                        if (searchUrl == null || !searchUrl.equals(fileUrl)) {
                            boolean isLibrary = fileUrl.indexOf("jsstubs") != -1; // TODO - better algorithm

                            if (!isLibrary && !isReachable(context, fileUrl)) {
                                continue;
                            }
                        }
                    }

                    for (String signature : signatures) {
                        // Lucene returns some inexact matches, TODO investigate why this is necessary
                        if ((kind == NameKind.PREFIX) && !signature.startsWith(lcfqn)) {
                            continue;
                        } else if (kind == NameKind.CASE_INSENSITIVE_PREFIX && !signature.regionMatches(true, 0, lcfqn, 0, lcfqn.length())) {
                            continue;
                        } else if (kind == NameKind.CASE_INSENSITIVE_REGEXP) {
                            int end = signature.indexOf(';');
                            assert end != -1;
                            String n = signature.substring(0, end);
                            try {
                                if (!n.matches(lcfqn)) {
                                    continue;
                                }
                            } catch (Exception e) {
                                // Silently ignore regexp failures in the search expression
                            }
                        } else if (originalKind == NameKind.EXACT_NAME) {
                            // Make sure the name matches exactly
                            // We know that the prefix is correct from the first part of
                            // this if clause, by the signature may have more
                            if (((signature.length() > lcfqn.length()) &&
                                    (signature.charAt(lcfqn.length()) != ';'))) {
                                continue;
                            }
                        }

                        // XXX THIS DOES NOT WORK WHEN THERE ARE IDENTICAL SIGNATURES!!!
                        assert map != null;

                        String elementName = null;
                        int nameEndIdx = signature.indexOf(';');
                        assert nameEndIdx != -1 : signature;
                        elementName = signature.substring(0, nameEndIdx);
                        nameEndIdx++;

                        String funcIn = null;
                        int inEndIdx = signature.indexOf(';', nameEndIdx);
                        assert inEndIdx != -1 : signature;
                        inEndIdx++;

                        int startCs = inEndIdx;
                        inEndIdx = signature.indexOf(';', startCs);
                        assert inEndIdx != -1;
                        if (inEndIdx > startCs) {
                            // Compute the case sensitive name
                            elementName = signature.substring(startCs, inEndIdx);
                            if (kind == NameKind.PREFIX && !elementName.startsWith(fqn)) {
                                continue;
                            } else if (kind == NameKind.EXACT_NAME && !elementName.equals(fqn)) {
                                continue;
                            }
                        }
                        inEndIdx++;

                        int lastDot = elementName.lastIndexOf('.');
                        IndexedElement element = null;
                        if (prefix.length() < lastDot) {
                            int nextDot = elementName.indexOf('.', fqn.length());
                            if (nextDot != -1) {
                                int flags = IndexedElement.decodeFlags(signature, inEndIdx, 0);
                                ElementKind k = ElementKind.PACKAGE;
                                // If there are no more dots after this one, it's a class, not a package
                                int nextNextDot = elementName.indexOf('.', nextDot + 1);
                                if (nextNextDot == -1) {
                                    k = ElementKind.CLASS;
                                }
                                if (type != null && type.length() > 0) {
                                    String pkg = elementName.substring(type.length() + 1, nextDot);
                                    element = new IndexedPackage(null, pkg, null, this, map.getPersistentUrl(), signature, flags, ElementKind.PACKAGE);
                                } else {
                                    String pkg = elementName.substring(0, nextDot);
                                    element = new IndexedPackage(null, pkg, null, this, map.getPersistentUrl(), signature, flags, ElementKind.PACKAGE);
                                }
                            } else {
                                funcIn = elementName.substring(0, lastDot);
                                elementName = elementName.substring(lastDot + 1);
                            }
                        } else if (lastDot != -1) {
                            funcIn = elementName.substring(0, lastDot);
                            elementName = elementName.substring(lastDot + 1);
                        }
                        if (element == null) {
                            element = IndexedElement.create(signature, map.getPersistentUrl(), null, elementName, funcIn, inEndIdx, this, false);
                        }
                        boolean isMethod = element instanceof IndexedFunction;
                        if (isMethod && !includeMethods) {
                            continue;
                        } else if (!isMethod && !includeProperties) {
                            continue;
                        }
                        if (onlyConstructors && element.getKind() != ElementKind.CONSTRUCTOR) {
                            continue;
                        }
                        if (!haveRedirected) {
                            element.setSmart(true);
                        }
                        if (!inheriting) {
                            element.setInherited(false);
                        }
                        elements.add(element);
                    }
                }
            }

            if (type == null || "scala.AnyRef".equals(type)) { // NOI18N
                break;
            }
            type = getExtends(type, scope);
            if (type == null) {
                type = "scala.AnyRef"; // NOI18N
                haveRedirected = true;
            }
            // Prevent circularity in types
            if (seenTypes.contains(type)) {
                break;
            } else {
                seenTypes.add(type);
            }
            inheriting = true;
        }

        return elements;
    }

    private Set<IndexedElement> getMembers(String prefix, String typeQName, 
            NameKind kind, Set<SearchScope> scope, ScalaParserResult pResult,
            boolean onlyConstructors, boolean includeMethods, boolean includeFields, boolean includeDuplicates) {

        assert typeQName != null && typeQName.length() > 0;

        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_FQN;
        Set<String> terms = TERMS_FQN;
        NameKind originalKind = kind;
        if (kind == NameKind.EXACT_NAME) {
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            kind = NameKind.PREFIX;
        }

        if (kind == NameKind.CASE_INSENSITIVE_PREFIX || kind == NameKind.CASE_INSENSITIVE_REGEXP) {
            // TODO - can I do anything about this????
            //field = ScalaIndexer.FIELD_BASE_LOWER;
            //terms = FQN_BASE_LOWER;
        }

        final Set<IndexedElement> elements = includeDuplicates ? new DuplicateElementSet() : new HashSet<IndexedElement>();
        String searchUrl = null;
        if (pResult != null) {
            try {
                searchUrl = pResult.getFile().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        Set<String> seenTypes = new HashSet<String>();
        seenTypes.add(typeQName);
        boolean haveRedirected = false;
        boolean inheriting = typeQName == null;

        while (true) {

            String fqn = typeQName != null && typeQName.length() > 0
                    ? typeQName
                    : "scala.AnyRef";

            String lcfqn = fqn.toLowerCase();
            search(field, lcfqn, kind, result, scope, terms);

            for (SearchResult map : result) {
                String[] signatures = map.getValues(field);

                if (signatures == null) {
                    continue;
                }

                String fileUrl = map.getPersistentUrl();

                FileObject fo = ScalaIndex.getFileObject(fileUrl);
                if (fo == null) {
                    continue;
                }

                // Check if this file even applies
                if (pResult != null) {
                    if (searchUrl == null || !searchUrl.equals(fileUrl)) {
                        boolean isLibrary = fileUrl.indexOf("jsstubs") != -1; // TODO - better algorithm

                        if (!isLibrary && !isReachable(pResult, fileUrl)) {
                            continue;
                        }
                    }
                }

                for (String signature : signatures) {
                    // Lucene returns some inexact matches, TODO investigate why this is necessary
                    if ((kind == NameKind.PREFIX) && !signature.startsWith(lcfqn)) {
                        continue;
                    } else if (kind == NameKind.CASE_INSENSITIVE_PREFIX && !signature.regionMatches(true, 0, lcfqn, 0, lcfqn.length())) {
                        continue;
                    } else if (kind == NameKind.CASE_INSENSITIVE_REGEXP) {
                        int end = signature.indexOf(';');
                        assert end != -1;
                        String n = signature.substring(0, end);
                        try {
                            if (!n.matches(lcfqn)) {
                                continue;
                            }
                        } catch (Exception e) {
                            // Silently ignore regexp failures in the search expression
                            }
                    } else if (originalKind == NameKind.EXACT_NAME) {
                        // Make sure the name matches exactly
                        // We know that the prefix is correct from the first part of
                        // this if clause, by the signature may have more
                        if (((signature.length() > lcfqn.length()) &&
                                (signature.charAt(lcfqn.length()) != ';'))) {
                            continue;
                        }
                    }

                    // XXX THIS DOES NOT WORK WHEN THERE ARE IDENTICAL SIGNATURES!!!
                    assert map != null;

                    String elementName = null;
                    int nameEndIdx = signature.indexOf(';');
                    assert nameEndIdx != -1 : signature;
                    elementName = signature.substring(0, nameEndIdx);
                    nameEndIdx++;

                    String funcIn = null;
                    int inEndIdx = signature.indexOf(';', nameEndIdx);
                    assert inEndIdx != -1 : signature;
                    inEndIdx++;

                    int startCs = inEndIdx;
                    inEndIdx = signature.indexOf(';', startCs);
                    assert inEndIdx != -1;
                    if (inEndIdx > startCs) {
                        // Compute the case sensitive name
                        elementName = signature.substring(startCs, inEndIdx);
                        if (kind == NameKind.PREFIX && !elementName.startsWith(fqn)) {
                            continue;
                        } else if (kind == NameKind.EXACT_NAME && !elementName.equals(fqn)) {
                            continue;
                        }
                    }
                    inEndIdx++;


                    List<Template> templates = ScalaParser.resolve(fo, elementName);


                    int lastDot = elementName.lastIndexOf('.');
                    IndexedElement element = null;
                    if (prefix.length() < lastDot) {
                        int nextDot = elementName.indexOf('.', fqn.length());
                        if (nextDot != -1) {
                            int flags = IndexedElement.decodeFlags(signature, inEndIdx, 0);
                            ElementKind k = ElementKind.PACKAGE;
                            // If there are no more dots after this one, it's a class, not a package
                            int nextNextDot = elementName.indexOf('.', nextDot + 1);
                            if (nextNextDot == -1) {
                                k = ElementKind.CLASS;
                            }
                            if (typeQName != null && typeQName.length() > 0) {
                                String pkg = elementName.substring(typeQName.length() + 1, nextDot);
                                element = new IndexedPackage(null, pkg, null, this, fileUrl, signature, flags, ElementKind.PACKAGE);
                            } else {
                                String pkg = elementName.substring(0, nextDot);
                                element = new IndexedPackage(null, pkg, null, this, fileUrl, signature, flags, ElementKind.PACKAGE);
                            }
                        } else {
                            funcIn = elementName.substring(0, lastDot);
                            elementName = elementName.substring(lastDot + 1);
                        }
                    } else if (lastDot != -1) {
                        funcIn = elementName.substring(0, lastDot);
                        elementName = elementName.substring(lastDot + 1);
                    }
                    if (element == null) {
                        element = IndexedElement.create(signature, fileUrl, null, elementName, funcIn, inEndIdx, this, false);
                    }

                    boolean isMethod = element instanceof IndexedFunction;
                    if (isMethod && !includeMethods) {
                        continue;
                    } else if (!isMethod && !includeFields) {
                        continue;
                    }
                    if (onlyConstructors && element.getKind() != ElementKind.CONSTRUCTOR) {
                        continue;
                    }
                    if (!haveRedirected) {
                        element.setSmart(true);
                    }
                    if (!inheriting) {
                        element.setInherited(false);
                    }
                    elements.add(element);

                }

            }

            if (typeQName == null || "scala.AnyRef".equals(typeQName)) { // NOI18N
                break;
            }
            typeQName = getExtends(typeQName, scope);
            if (typeQName == null) {
                typeQName = "scala.AnyRef"; // NOI18N
                haveRedirected = true;
            }
            // Prevent circularity in types
            if (seenTypes.contains(typeQName)) {
                break;
            } else {
                seenTypes.add(typeQName);
            }
            inheriting = true;
        }

        return elements;
    }

    private Set<IndexedElement> getTypesByFqn(String fqnPrefix, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context,
            boolean onlyConstructors, boolean includeDuplicates, boolean onlyContent) {

        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_CASE_INSENSITIVE_CLASS_NAME;
        String valueField = ScalaIndexer.FIELD_FQN;
        Set<String> terms = TERMS_FQN;
        NameKind originalKind = kind;
        if (kind == NameKind.EXACT_NAME) {
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            kind = NameKind.PREFIX;
        }

        if (kind == NameKind.CASE_INSENSITIVE_PREFIX || kind == NameKind.CASE_INSENSITIVE_REGEXP) {
            // TODO - can I do anything about this????
            //field = ScalaIndexer.FIELD_BASE_LOWER;
            //terms = FQN_BASE_LOWER;
        }

        final Set<IndexedElement> elements = includeDuplicates ? new DuplicateElementSet() : new HashSet<IndexedElement>();
        String searchUrl = null;
        if (context != null) {
            try {
                searchUrl = context.getFile().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        String lcfqn = fqnPrefix.toLowerCase();
        search(field, lcfqn, kind, result, scope, terms);

        for (SearchResult map : result) {
            String[] signatures = map.getValues(valueField);

            if (signatures != null) {
                // Check if this file even applies
                if (context != null) {
                    String fileUrl = map.getPersistentUrl();
                    if (searchUrl == null || !searchUrl.equals(fileUrl)) {
                        boolean isLibrary = fileUrl.indexOf("jsstubs") != -1; // TODO - better algorithm

                        if (!isLibrary && !isReachable(context, fileUrl)) {
                            continue;
                        }
                    }
                }

                for (String signature : signatures) {
                    // Lucene returns some inexact matches, TODO investigate why this is necessary
                    if ((kind == NameKind.PREFIX) && !signature.startsWith(lcfqn)) {
                        continue;
                    } else if (kind == NameKind.CASE_INSENSITIVE_PREFIX && !signature.regionMatches(true, 0, lcfqn, 0, lcfqn.length())) {
                        continue;
                    } else if (kind == NameKind.CASE_INSENSITIVE_REGEXP) {
                        int end = signature.indexOf(';');
                        assert end != -1;
                        String n = signature.substring(0, end);
                        try {
                            if (!n.matches(lcfqn)) {
                                continue;
                            }
                        } catch (Exception e) {
                            // Silently ignore regexp failures in the search expression
                            }
                    } else if (originalKind == NameKind.EXACT_NAME) {
                        // Make sure the name matches exactly
                        // We know that the prefix is correct from the first part of
                        // this if clause, by the signature may have more
                        if (((signature.length() > lcfqn.length()) &&
                                (signature.charAt(lcfqn.length()) != ';'))) {
                            continue;
                        }
                    }

                    // XXX THIS DOES NOT WORK WHEN THERE ARE IDENTICAL SIGNATURES!!!
                    assert map != null;

                    String elementName = null;
                    int nameEndIdx = signature.indexOf(';');
                    assert nameEndIdx != -1 : signature;
                    elementName = signature.substring(0, nameEndIdx);
                    nameEndIdx++;

                    String funcIn = null;
                    int inEndIdx = signature.indexOf(';', nameEndIdx);
                    assert inEndIdx != -1 : signature;
                    inEndIdx++;

                    int startCs = inEndIdx;
                    inEndIdx = signature.indexOf(';', startCs);
                    assert inEndIdx != -1;
                    if (inEndIdx > startCs) {
                        // Compute the case sensitive name
                        elementName = signature.substring(startCs, inEndIdx);
                        if (kind == NameKind.PREFIX && !elementName.startsWith(fqnPrefix)) {
                            continue;
                        } else if (kind == NameKind.EXACT_NAME && !elementName.equals(fqnPrefix)) {
                            continue;
                        }
                    }
                    inEndIdx++;

                    int flags = IndexedElement.decodeFlags(signature, inEndIdx, 0);
                    if (!IndexedElement.isTemplate(flags)) {
                        continue;
                    }

                    IndexedElement element = null;

                    int lastDot = elementName.lastIndexOf('.');
                    if (lastDot == -1) {
                        // should be class, under default package
                        element = IndexedElement.create(signature, map.getPersistentUrl(), elementName, elementName, "", inEndIdx, this, false);
                    } else {
                        String pkgName = elementName.substring(0, lastDot);
                        if ((pkgName + ".").equals(fqnPrefix)) { // "java", we should return a class
                            String simpleName = elementName.substring(lastDot + 1, elementName.length());
                            element = IndexedElement.create(signature, map.getPersistentUrl(), elementName, simpleName, "", inEndIdx, this, false);
                        } else {
                            if (onlyContent) {
                                continue;
                            }
                            // we should return a package
                            int dotAfterFqnPrefix = pkgName.indexOf('.', fqnPrefix.length());
                            if (dotAfterFqnPrefix == -1) {
                                element = new IndexedPackage(null, pkgName, null, this, map.getPersistentUrl(), signature, flags, ElementKind.PACKAGE);
                            } else { // "java.lang", it's sub folder of wanted, we should fetch "java" only                                
                                pkgName = pkgName.substring(0, dotAfterFqnPrefix);
                                element = new IndexedPackage(null, pkgName, null, this, map.getPersistentUrl(), signature, flags, ElementKind.PACKAGE);
                            }
                        }
                    }

                    if (element != null) {
                        elements.add(element);
                    }

                }
            }
        }


        return elements;
    }

    public String getTypeString(String fqn) {
        int baseIndex = fqn.lastIndexOf('.');
        if (baseIndex == -1) {
            return getSimpleType(fqn);
        }
        String clz = fqn.substring(0, baseIndex);
        List<String> ancestors = ClassCache.INSTANCE.getAncestors(clz, this);
        if (ancestors.size() <= 1) {
            return getSimpleType(fqn);
        }

        String base = fqn.substring(baseIndex + 1);
        int baseLength = base.length();

        // Look for inheritance too, e.g. if you're searching for HTMLDocument.createElement
        // and no such entry is found it will look at Document.createElement and return it provided
        // Document looks related to HTMLDocument through inheritance
        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_BASE;
        Set<String> terms = TERMS_BASE;
        String lcsymbol = base.toLowerCase();
        assert lcsymbol.length() == baseLength;
        search(field, lcsymbol, NameKind.PREFIX, result, ALL_SCOPE, terms);

        for (SearchResult map : result) {
            String[] signatures = map.getValues(field);

            if (signatures != null) {
                for (String signature : signatures) {
                    // Lucene returns some inexact matches, TODO investigate why this is necessary
                    // Make sure the name matches exactly
                    // We know that the prefix is correct from the first part of
                    // this if clause, by the signature may have more
                    if (!signature.startsWith(lcsymbol) || signature.charAt(baseLength) != ';') {
                        continue;
                    }

                    // Make sure the containing document is one of the superclasses
                    assert signature.charAt(baseLength) == ';';
                    int inBegin = baseLength + 1;
                    int inEnd = signature.indexOf(';', inBegin);
                    if (inEnd == inBegin) {
                        // No in - only qualifies if the target has no in
                        // However, we're currently processing those separately so
                        // this is not a match
                        continue;
                    }
                    String in = signature.substring(inBegin, inEnd);
                    for (String ancestor : ancestors) {
                        if (ancestor.equals(in)) {
                            // This is a good one

                            String type = getTypeInSignature(signature);
                            if (type != null) {
                                return type;
                            }
                        }
                    }
                }
            }
        }

        return null;

    }

    private static String getTypeInSignature(String signature) {
        // Look for the type
        int typeIndex = 0;
        int section = IndexedElement.TYPE_INDEX;
        for (int i = 0; i <
                section; i++) {
            typeIndex = signature.indexOf(';', typeIndex + 1);
        }

        typeIndex++;
        int endIndex = signature.indexOf(';', typeIndex);
        if (endIndex > typeIndex) {
            return signature.substring(typeIndex, endIndex);
        }

        return null;
    }

    /** 
     * Try to find the type of a symbol and return it. This method does not look for overridden
     * methods etc, it matches by exact signature.
     */
    private String getSimpleType(String fqn) {
        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_FQN;
        Set<String> terms = TERMS_BASE;
        String lcsymbol = fqn.toLowerCase();
        int symbolLength = fqn.length();
        search(field, lcsymbol, NameKind.PREFIX, result, ALL_SCOPE, terms);

        for (SearchResult map : result) {
            String[] signatures = map.getValues(field);

            if (signatures != null) {
                for (String signature : signatures) {
                    // Lucene returns some inexact matches, TODO investigate why this is necessary
                    // Make sure the name matches exactly
                    // We know that the prefix is correct from the first part of
                    // this if clause, by the signature may have more
                    if (!signature.startsWith(lcsymbol) || signature.charAt(symbolLength) != ';') {
                        continue;
                    }

                    String type = getTypeInSignature(signature);
                    if (type != null) {
                        return type;
                    }

                }
            }
        }

        return null;
    }

    /** 
     * Decide whether the given url is included from the current compilation
     * context.
     * This will typically return true for all library files, and false for
     * all source level files unless that file is reachable through include-mechanisms
     * from the current file.
     * 
     * @todo Add some smarts here to correlate remote URLs (http:// pointers to dojo etc)
     *   with local copies of these.
     * @todo Do some kind of transitive check? Probably not - there isn't a way to do
     *    includes of files that contain other files (you can import a .js file, but that
     *    js file can't include other files)
     */
    public boolean isReachable(ScalaParserResult result, String url) {
        if (ALL_REACHABLE) {
            return true;
        }

        List<String> imports = Collections.emptyList();// @TODO result.getStructure().getImports();

        if (imports.size() > 0) {
            // TODO - do some heuristics to deal with relative paths here,
            // e.g.   <script src="../../foo.js"></script>

            for (int i = 0, n = imports.size(); i <
                    n; i++) {
                String imp = imports.get(i);
                if (imp.indexOf("../") != -1) {
                    int lastIndex = imp.lastIndexOf("../");
                    imp =
                            imp.substring(lastIndex + 3);
                    if (imp.length() == 0) {
                        continue;
                    }

                }
                if (url.endsWith(imp)) {
                    return true;
                }

            }
        }

        return false;
    }

    private Set<org.netbeans.api.java.source.ClassIndex.SearchScope> toJavaSearchScope(Set<SearchScope> scope) {
        Set<org.netbeans.api.java.source.ClassIndex.SearchScope> javaScope = new HashSet<org.netbeans.api.java.source.ClassIndex.SearchScope>();

        for (SearchScope _scope : scope) {
            javaScope.add(org.netbeans.api.java.source.ClassIndex.SearchScope.valueOf(_scope.name()));
        }

        return javaScope;
    }

    private org.netbeans.api.java.source.ClassIndex.NameKind toJavaNameKind(NameKind kind) {
        switch (kind) {
            case CAMEL_CASE:
                return org.netbeans.api.java.source.ClassIndex.NameKind.CAMEL_CASE;
            case CASE_INSENSITIVE_PREFIX:
                return org.netbeans.api.java.source.ClassIndex.NameKind.CASE_INSENSITIVE_PREFIX;
            case CASE_INSENSITIVE_REGEXP:
                return org.netbeans.api.java.source.ClassIndex.NameKind.CASE_INSENSITIVE_REGEXP;
            case EXACT_NAME:
                return org.netbeans.api.java.source.ClassIndex.NameKind.SIMPLE_NAME;
            case PREFIX:
                return org.netbeans.api.java.source.ClassIndex.NameKind.PREFIX;
            case REGEXP:
                return org.netbeans.api.java.source.ClassIndex.NameKind.REGEXP;
            default:
                return org.netbeans.api.java.source.ClassIndex.NameKind.SIMPLE_NAME;
        }
    }
}
