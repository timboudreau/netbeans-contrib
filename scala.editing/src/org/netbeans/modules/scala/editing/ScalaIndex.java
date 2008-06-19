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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.Index;
import org.netbeans.modules.gsf.api.Index.SearchResult;
import org.netbeans.modules.gsf.api.Index.SearchScope;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.tmpls.Template;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.URLMapper;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 * A wrapper of gsf index and JavaIndex
 *
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
    /** @NOTE: TERMS is not used at all in current GSF's LuceneIndex */
    public static final Set<String> TERMS_NAME = add(new HashSet<String>(), new String[]{
                ScalaIndexer.FIELD_QUALIFIED_NAME,
                ScalaIndexer.FIELD_QUALIFIED_NAME_CASE_INSENSITIVE,
                ScalaIndexer.FIELD_SIMPLE_NAME,
                ScalaIndexer.FIELD_SIMPLE_NAME_CASE_INSENSITIVE,
                ScalaIndexer.FIELD_PACKAGE_NAME,
                ScalaIndexer.FIELD_ATTRIBUTES
            });
    public static final Set<String> TERMS_ATTRIBUTES = Collections.singleton(ScalaIndexer.FIELD_ATTRIBUTES);
    public static final Set<String> TERMS_EXTEND = Collections.singleton(ScalaIndexer.FIELD_EXTENDS_NAME);
    public static final Set<String> TERMS_IMPORT = Collections.singleton(ScalaIndexer.FIELD_IMPORT);
    private CompilationInfo info;
    private final Index index;
    private JavaIndex javaIndex;

    private static final <T> Set<T> add(Set<T> set, T[] values) {
        for (T v : values) {
            set.add(v);
        }
        return set;
    }

    /** Creates a new instance of ScalaIndex */
    private ScalaIndex(Index index, JavaIndex javaIndex, CompilationInfo info) {
        this.info = info;
        this.index = index;
        this.javaIndex = javaIndex;
    }

    private void setJavaIndex(JavaIndex javaIndex) {
        this.javaIndex = javaIndex;
    }

    public static ScalaIndex get(CompilationInfo info) {
        Index index = info.getIndex(ScalaMimeResolver.MIME_TYPE);
        ScalaIndex scalaIndex = new ScalaIndex(index, null, info);

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
        // TODO - search by Type
        return null;
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

    public Set<GsfElement> getDeclaredTypes(String type, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context) {
        Set<GsfElement> gsfElements = javaIndex.getDeclaredTypes(type, toJavaNameKind(kind), toJavaSearchScope(scope), context);
        return gsfElements;
    }

    /** Return both functions and properties matching the given prefix, of the
     * given (possibly null) type
     */
    public Set<GsfElement> getMembers(String prefix, String type,
            NameKind kind, Set<Index.SearchScope> scope, ScalaParserResult context,
            boolean onlyConstructors) {

        Set<GsfElement> gsfElements = getMembers(prefix, type, kind, scope, context, onlyConstructors, true, true, false);
        // Is there at least one non-inheried member?
        boolean ofScala = false;
        for (GsfElement gsfElement : gsfElements) {
            if (!gsfElement.isInherited()) {
                ofScala = true;
                break;
            }
        }

        /** @TODO we need a better way to check if it's of scala */
        if (!ofScala) {
            gsfElements = javaIndex.getMembers(prefix, type, toJavaNameKind(kind), toJavaSearchScope(scope), context, onlyConstructors, true, true, false);
        }

        if (gsfElements.size() == 0) {
            gsfElements = javaIndex.getMembers(prefix, "java.lang.Object", toJavaNameKind(kind), toJavaSearchScope(scope), context, onlyConstructors, true, true, false);
        }

        return gsfElements;
    }

    public Set<IndexedElement> getPackageContent(String fqnPrefix, NameKind kind, Set<SearchScope> scope) {

        Set<IndexedElement> idxElements = getTypesByQualifiedName(fqnPrefix, kind, scope, null, false, false, true);

        idxElements.addAll(javaIndex.getPackageContent(fqnPrefix));

        return idxElements;
    }

    public Set<IndexedElement> getPackagesAndContent(String fqnPrefix, NameKind kind, Set<SearchScope> scope) {

        Set<IndexedElement> idxElements = getTypesByQualifiedName(fqnPrefix, kind, scope, null, false, false, false);

        idxElements.addAll(javaIndex.getPackages(fqnPrefix));
        idxElements.addAll(javaIndex.getPackageContent(fqnPrefix));

        return idxElements;
    }

    public Set<IndexedElement> getImportedTypes(List<String> importedPkg, String ofPackage) {
        return null;
    }

    private Set<GsfElement> getMembers(String prefix, String typeQName,
            NameKind kind, Set<SearchScope> scope, ScalaParserResult pResult,
            boolean onlyConstructors, boolean includeMethods, boolean includeFields, boolean includeDuplicates) {

        assert typeQName != null && typeQName.length() > 0;

        final Set<SearchResult> results = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_QUALIFIED_NAME_CASE_INSENSITIVE;
        Set<String> terms = TERMS_NAME;
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

//        final Set<Element> elements = includeDuplicates ? new DuplicateElementSet() : new HashSet<Element>();
        final Set<GsfElement> gsfElements = new HashSet<GsfElement>();
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
        boolean inherited = typeQName == null;

        while (true) {
            String fqn = typeQName != null && typeQName.length() > 0
                    ? typeQName
                    : "scala.AnyRef";

            String lcfqn = fqn.toLowerCase();
            search(field, lcfqn, kind, results, scope, terms);

            for (SearchResult map : results) {
                String qName = map.getValue(ScalaIndexer.FIELD_QUALIFIED_NAME);
                String qName_ci = map.getValue(ScalaIndexer.FIELD_QUALIFIED_NAME_CASE_INSENSITIVE);
                String attrs = map.getValue(ScalaIndexer.FIELD_ATTRIBUTES);
                String sName = map.getValue(ScalaIndexer.FIELD_SIMPLE_NAME);


                if (qName == null) {
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

                // Lucene returns some inexact matches, TODO investigate why this is necessary
                if ((kind == NameKind.PREFIX) && !qName_ci.startsWith(lcfqn)) {
                    continue;
                } else if (kind == NameKind.CASE_INSENSITIVE_PREFIX && !qName_ci.startsWith(lcfqn)) {
                    continue;
                } else if (kind == NameKind.CASE_INSENSITIVE_REGEXP) {
                    try {
                        if (!qName_ci.matches(lcfqn)) {
                            continue;
                        }
                    } catch (Exception e) {
                        // Silently ignore regexp failures in the search expression
                    }
                } else if (originalKind == NameKind.EXACT_NAME) {
                    // Make sure the name matches exactly
                    // We know that the prefix is correct from the first part of
                    // this if clause, by the signature may have more
                    if (!qName.equals(lcfqn)) {
                        continue;
                    }
                }

                // XXX THIS DOES NOT WORK WHEN THERE ARE IDENTICAL SIGNATURES!!!
                assert map != null;

                int flags = IndexedElement.decodeFlags(attrs, 0, 0);
                if (!IndexedElement.isTemplate(flags)) {
                    continue;
                }

                CompilationInfo newInfo = ScalaUtils.getCompilationInfoForScalaFile(fo);
                List<Template> templates = ScalaUtils.resolveTemplate(newInfo, qName);

                for (Template tmpl : templates) {
                    for (AstElement element : tmpl.getEnclosedElements()) {
                        if (!prefix.equals("") && !element.getSimpleName().toString().startsWith(prefix)) {
                            continue;
                        }

                        boolean isMethod = element instanceof ExecutableElement;
                        if (isMethod && !includeMethods) {
                            continue;
                        } else if (!isMethod && !includeFields) {
                            continue;
                        }
                        if (onlyConstructors && element.getKind() != ElementKind.CONSTRUCTOR) {
                            continue;
                        }
                        GsfElement gsfElement = new GsfElement(element, fo, newInfo);
                        gsfElement.setInherited(inherited);
                        gsfElements.add(gsfElement);
                    }
                    break;
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
            inherited = true;
        }

        return gsfElements;
    }

    private Set<IndexedElement> getTypesByQualifiedName(String fqnPrefix, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context,
            boolean onlyConstructors, boolean includeDuplicates, boolean onlyContent) {

        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ScalaIndexer.FIELD_QUALIFIED_NAME_CASE_INSENSITIVE;
        String valueField = ScalaIndexer.FIELD_SIGNATURE;
        Set<String> terms = TERMS_NAME;
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
            String qName = map.getValue(ScalaIndexer.FIELD_QUALIFIED_NAME);
            String qName_ci = map.getValue(ScalaIndexer.FIELD_QUALIFIED_NAME_CASE_INSENSITIVE);
            String sName = map.getValue(ScalaIndexer.FIELD_SIMPLE_NAME);
            String attrs = map.getValue(ScalaIndexer.FIELD_ATTRIBUTES);

            if (qName != null) {
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

                // Lucene returns some inexact matches, TODO investigate why this is necessary
                if ((kind == NameKind.PREFIX) && !qName_ci.startsWith(lcfqn)) {
                    continue;
                } else if (kind == NameKind.CASE_INSENSITIVE_PREFIX && !qName_ci.startsWith(lcfqn)) {
                    continue;
                } else if (kind == NameKind.CASE_INSENSITIVE_REGEXP) {
                    try {
                        if (!qName_ci.matches(lcfqn)) {
                            continue;
                        }
                    } catch (Exception e) {
                        // Silently ignore regexp failures in the search expression
                    }
                } else if (originalKind == NameKind.EXACT_NAME) {
                    // Make sure the name matches exactly
                    // We know that the prefix is correct from the first part of
                    // this if clause, by the signature may have more
                    if (!qName.equals(lcfqn)) {
                        continue;
                    }
                }

                // XXX THIS DOES NOT WORK WHEN THERE ARE IDENTICAL SIGNATURES!!!
                assert map != null;

                int flags = IndexedElement.decodeFlags(attrs, 0, 0);
                if (!IndexedElement.isTemplate(flags)) {
                    continue;
                }

                IndexedElement element = null;

                int lastDot = qName.lastIndexOf('.');
                if (lastDot == -1) {
                    // should be class, under empty package
                    element = IndexedElement.create(qName, sName, "", attrs, map.getPersistentUrl(), this, false);
                } else {
                    String pkgName = qName.substring(0, lastDot);
                    if ((pkgName + ".").equals(fqnPrefix)) { // "java", we should return a class
                        String simpleName = qName.substring(lastDot + 1, qName.length());
                        element = IndexedElement.create(qName, simpleName, "", attrs, map.getPersistentUrl(), this, false);
                    } else {
                        if (onlyContent) {
                            continue;
                        }
                        // we should return a package
                        int dotAfterFqnPrefix = pkgName.indexOf('.', fqnPrefix.length());
                        if (dotAfterFqnPrefix == -1) {
                            element = new IndexedElement(pkgName, pkgName, "", attrs, flags, map.getPersistentUrl(), this, ElementKind.PACKAGE);
                        } else { // "java.lang", it's sub folder of wanted, we should fetch "java" only                                
                            pkgName = pkgName.substring(0, dotAfterFqnPrefix);
                            element = new IndexedElement(pkgName, pkgName, "", attrs, flags, map.getPersistentUrl(), this, ElementKind.PACKAGE);
                        }
                    }
                }

                if (element != null) {
                    elements.add(element);
                }

            }
        }


        return elements;
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

            for (int i = 0,  n = imports.size(); i <
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
