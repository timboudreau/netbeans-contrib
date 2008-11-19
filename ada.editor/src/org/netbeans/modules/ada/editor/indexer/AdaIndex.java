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
package org.netbeans.modules.ada.editor.indexer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import org.netbeans.modules.ada.editor.parser.AdaParseResult;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Index;
import org.netbeans.modules.gsf.api.Index.SearchResult;
import org.netbeans.modules.gsf.api.Index.SearchScope;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.gsf.api.annotations.NonNull;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomasz.Slota@Sun.COM
 */
public class AdaIndex {

    /** Set property to true to find ALL functions regardless of file includes */
    //private static final boolean ALL_REACHABLE = Boolean.getBoolean("javascript.findall");
    public static final int ANY_ATTR = 0xFFFFFFFF;
    private static String clusterUrl = null;
    private static final String CLUSTER_URL = "cluster:"; // NOI18N
    static final Set<SearchScope> ALL_SCOPE = EnumSet.allOf(SearchScope.class);
    static final Set<SearchScope> SOURCE_SCOPE = EnumSet.of(SearchScope.SOURCE);
    private static final Set<String> TERMS_BASE = Collections.singleton(AdaIndexer.FIELD_BASE);
    private static final Set<String> TERMS_CONST = Collections.singleton(AdaIndexer.FIELD_CONST);
    private static final Set<String> TERMS_PKGSPC = Collections.singleton(AdaIndexer.FIELD_PKGSPC);
    private static final Set<String> TERMS_PKGBDY = Collections.singleton(AdaIndexer.FIELD_PKGBDY);
    private static final Set<String> TERMS_VAR = Collections.singleton(AdaIndexer.FIELD_VAR);
    private static final Set<String> TERMS_ALL = new HashSet<String>();


    {
        TERMS_ALL.add(AdaIndexer.FIELD_CONST);
        TERMS_ALL.add(AdaIndexer.FIELD_PKGSPC);
        TERMS_ALL.add(AdaIndexer.FIELD_PKGBDY);
        TERMS_ALL.add(AdaIndexer.FIELD_VAR);
    }
    private final Index index;

    /** Creates a new instance of JsIndex */
    public AdaIndex(Index index) {
        this.index = index;
    }

    public static AdaIndex get(Index index) {
        return new AdaIndex(index);
    }

    public Collection<IndexedElement> getAllTopLevel(AdaParseResult context, String prefix, NameKind nameKind) {
        final Set<SearchResult> result = new HashSet<SearchResult>();
        final Collection<IndexedElement> elements = new ArrayList<IndexedElement>();
        Collection<IndexedPackageSpec> classes = new ArrayList<IndexedPackageSpec>();
        Collection<IndexedVariable> vars = new ArrayList<IndexedVariable>();

        // search through the top leve elements
        search(AdaIndexer.FIELD_TOP_LEVEL, prefix.toLowerCase(), NameKind.PREFIX, result, ALL_SCOPE, TERMS_ALL);

        findPackageSpec(result, nameKind, prefix, classes);
        findTopVariables(result, nameKind, prefix, vars);
        elements.addAll(classes);
        elements.addAll(vars);
        return elements;
    }

    protected void findPackageSpec(final Set<SearchResult> result, NameKind kind, String name, Collection<IndexedPackageSpec> packages) {
        for (SearchResult map : result) {
            if (map.getPersistentUrl() != null) {
                String[] signatures = map.getValues(AdaIndexer.FIELD_PKGSPC);
                if (signatures == null) {
                    continue;
                }
                for (String signature : signatures) {
                    Signature sig = Signature.get(signature);
                    String packageName = sig.string(1);
                    if (kind == NameKind.PREFIX || kind == NameKind.CASE_INSENSITIVE_PREFIX) {
                        //case sensitive
                        if (!packageName.toLowerCase().startsWith(name.toLowerCase())) {
                            continue;
                        }
                    } else if (kind == NameKind.EXACT_NAME) {
                        if (!packageName.toLowerCase().equals(name.toLowerCase())) {
                            continue;
                        }
                    }
                    //TODO: handle search kind
                    int offset = sig.integer(2);
                    String superClass = sig.string(3);
                    superClass = superClass.length() == 0 ? null : superClass;
                    IndexedPackageSpec clazz = new IndexedPackageSpec(packageName, null, this, map.getPersistentUrl(), offset, 0);
                    //clazz.setResolved(context != null && isReachable(context, map.getPersistentUrl()));
                    packages.add(clazz);
                }
            }
        }
    }

    protected void findPackageBody(final Set<SearchResult> result, NameKind kind, String name, Collection<IndexedPackageBody> packages) {
        for (SearchResult map : result) {
            if (map.getPersistentUrl() != null) {
                String[] signatures = map.getValues(AdaIndexer.FIELD_PKGBDY);
                if (signatures == null) {
                    continue;
                }
                for (String signature : signatures) {
                    Signature sig = Signature.get(signature);
                    String packageName = sig.string(1);
                    if (kind == NameKind.PREFIX || kind == NameKind.CASE_INSENSITIVE_PREFIX) {
                        //case sensitive
                        if (!packageName.toLowerCase().startsWith(name.toLowerCase())) {
                            continue;
                        }
                    } else if (kind == NameKind.EXACT_NAME) {
                        if (!packageName.toLowerCase().equals(name.toLowerCase())) {
                            continue;
                        }
                    }
                    //TODO: handle search kind
                    int offset = sig.integer(2);
                    String superClass = sig.string(3);
                    superClass = superClass.length() == 0 ? null : superClass;
                    IndexedPackageBody clazz = new IndexedPackageBody(packageName, null, this, map.getPersistentUrl(), offset, 0);
                    //clazz.setResolved(context != null && isReachable(context, map.getPersistentUrl()));
                    packages.add(clazz);
                }
            }
        }
    }

    protected void findTopVariables(final Set<SearchResult> result, NameKind kind, String name, Collection<IndexedVariable> vars) {
        for (SearchResult map : result) {
            if (map.getPersistentUrl() != null) {
                String[] signatures = map.getValues(AdaIndexer.FIELD_VAR);
                if (signatures == null) {
                    continue;
                }
                for (String signature : signatures) {
                    Signature sig = Signature.get(signature);
                    //sig.string(0) is the case insensitive search key
                    String constName = sig.string(1);
                    if (kind == NameKind.PREFIX || kind == NameKind.CASE_INSENSITIVE_PREFIX) {
                        //case sensitive
                        if (!constName.startsWith(name)) {
                            continue;
                        }
                    } else if (kind == NameKind.EXACT_NAME) {
                        if (!constName.equals(name)) {
                            continue;
                        }
                    }
                    String typeName = sig.string(2);
                    typeName = typeName.length() == 0 ? null : typeName;
                    int offset = sig.integer(3);
                    IndexedVariable var = new IndexedVariable(constName, null, this,
                            map.getPersistentUrl(), offset, 0, typeName);
                    //var.setResolved(context != null && isReachable(context, map.getPersistentUrl()));
                    vars.add(var);
                }
            }
        }
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

    //public needed for tests (see org.netbeans.modules.php.editor.nav.TestBase):
    public static void setClusterUrl(String url) {
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
    public static FileObject getFileObject(String urlStr) {
        try {
            if (urlStr.startsWith(CLUSTER_URL)) {
                urlStr = getClusterUrl() + urlStr.substring(CLUSTER_URL.length()); // NOI18N

            }

            URL url = new URL(urlStr);
            return URLMapper.findFileObject(url);
        } catch (MalformedURLException mue) {
            Exceptions.printStackTrace(mue);
        }

        return null;
    }

    static String getClusterUrl() {
        if (clusterUrl == null) {
            File f =
                    InstalledFileLocator.getDefault().locate("modules/org-netbeans-modules-ada-editor.jar", null, false); // NOI18N

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

    /** returns all fields of a class or an interface. */
    public Collection<IndexedVariable> getAllFields(AdaParseResult context, String typeName, String name, NameKind kind, int attrMask) {
        Map<String, IndexedVariable> fields = new TreeMap<String, IndexedVariable>();

        // #147730 - prefer the current file
        File currentFile = getCurrentFile(context);
        Set<String> currentFileClasses = new HashSet<String>();

        for (String className : getClassAncestors(context, typeName)) {
            for (IndexedVariable field : getFields(context, className, name, kind)) {
                String fieldName = field.getName();

                if (!fields.containsKey(fieldName) || className.equals(typeName)) {
                    fields.put(fieldName, field);
                }

                if (currentFile != null && field != null && currentFile.equals(field.getFile().getFile())) {
                    currentFileClasses.add(className);
                }
            }
        }

        Collection<IndexedVariable> result = fields.values();
        filterClassMembers(result, currentFileClasses, currentFile);
        return result;
    }

    /** Current file for the context or <code>null</code> */
    private File getCurrentFile(AdaParseResult context) {
        if (context != null && context.getFile() != null) {
            return context.getFile().getFile();
        }
        return null;
    }

    // #147730 - prefer the current file
    private void filterClassMembers(Collection<? extends IndexedElement> elements, Set<String> currentFileClasses, File currentFile) {
        if (elements.size() > 0 && currentFileClasses.size() > 0) {
            for (Iterator<? extends IndexedElement> it = elements.iterator(); it.hasNext();) {
                IndexedElement method = it.next();
                if (currentFileClasses.contains(method.getIn()) && !currentFile.equals(method.getFile().getFile())) {
                    it.remove();
                }
            }
        }
    }

    /** return a list of all superclasses of the given class.
     *  The head item will be the queried class, otherwise it not safe to rely on the element order
     */
    @NonNull
    public Collection<String> getClassAncestors(AdaParseResult context, String className) {
        return getClassAncestors(context, className, new TreeSet<String>());
    }

    @NonNull
    private Collection<String> getClassAncestors(AdaParseResult context, String className, Collection<String> processedClasses) {
        Collection<String> ancestors = new TreeSet<String>();

        if (processedClasses.contains(className)) {
            return Collections.<String>emptyList(); //TODO: circular reference, warn the user
        }

        processedClasses.add(className);
        List<String> assumedParents = new LinkedList<String>();
        Collection<IndexedPackageSpec> classes = getPackageSpec(context, className, NameKind.EXACT_NAME);

        if (classes != null) {
            for (IndexedPackageSpec clazz : classes) {
                ancestors.add(clazz.getName());
            }
        }

        for (String parent : assumedParents) {
            ancestors.addAll(getClassAncestors(context, parent, processedClasses));
        }

        return ancestors;
    }

    /** returns fields of a class. */
    public Collection<IndexedVariable> getFields(AdaParseResult context, String typeName, String name, NameKind kind) {
        Collection<IndexedVariable> fields = new ArrayList<IndexedVariable>();
        Map<String, String> signaturesMap = getTypeSpecificSignatures(typeName, AdaIndexer.FIELD_FIELD, name, kind, ALL_SCOPE);

        return fields;
    }

    private Map<String, String> getTypeSpecificSignatures(String typeName, String fieldName, String name, NameKind kind, Set<SearchScope> scope) {
        final Set<SearchResult> searchResult = new HashSet<SearchResult>();
        Map<String, String> signatures = new HashMap<String, String>();
        for (String indexField : new String[]{AdaIndexer.FIELD_PKGSPC, AdaIndexer.FIELD_PKGBDY}) {
            search(indexField, typeName.toLowerCase(), NameKind.PREFIX, searchResult, scope, TERMS_BASE);

            for (SearchResult typeMap : searchResult) {
                String[] typeSignatures = typeMap.getValues(indexField);
                String[] rawSignatures = typeMap.getValues(fieldName);

                if (typeSignatures == null || rawSignatures == null) {
                    continue;
                }

                assert typeSignatures.length == 1;
                String foundTypeName = getSignatureItem(typeSignatures[0], 1);
                foundTypeName = (foundTypeName != null) ? foundTypeName.toLowerCase() : null;
                String persistentURL = typeMap.getPersistentUrl();

                if (!typeName.toLowerCase().equals(foundTypeName)) {
                    continue;
                }

                for (String signature : rawSignatures) {
                    String elemName = getSignatureItem(signature, 0);

                    // TODO: now doing IC prefix search only, handle other search types
                    // according to 'kind'
                    if ((kind == NameKind.CASE_INSENSITIVE_PREFIX && elemName.toLowerCase().startsWith(name.toLowerCase())) || (kind == NameKind.PREFIX && elemName.startsWith(name)) || (kind == NameKind.EXACT_NAME && elemName.equals(name))) {
                        signatures.put(signature, persistentURL);
                    }

                }
            }
        }

        return signatures;
    }

    //faster parsing of signatures.
    //use Signature class if you need to search in the same signature
    //multiple times
    static String getSignatureItem(String signature, int index) {
        int searchIndex = 0;
        for (int i = 0; i < signature.length(); i++) {
            char c = signature.charAt(i);

            if (searchIndex == index) {
                for (int j = i; j < signature.length(); j++) {
                    c = signature.charAt(j);
                    if (c == ';') {
                        return signature.substring(i, j);
                    }
                }
            }

            if (c == ';') {
                searchIndex++;
            }
        }
        return null;
    }

    public Collection<IndexedVariable> getTopLevelVariables(AdaParseResult context, String name, NameKind kind) {
        return getTopLevelVariables(context, name, kind, ALL_SCOPE);
    }

    public Collection<IndexedVariable> getTopLevelVariables(AdaParseResult context, String name, NameKind kind, Set<SearchScope> scope) {
        final Set<SearchResult> result = new HashSet<SearchResult>();
        Collection<IndexedVariable> vars = new ArrayList<IndexedVariable>();
        search(AdaIndexer.FIELD_VAR, name.toLowerCase(), NameKind.PREFIX, result, scope, TERMS_VAR);
        findTopVariables(result, kind, name, vars);
        return vars;
    }

    public Set<FileObject> filesWithIdentifiers(String identifierName) {
        final Set<FileObject> result = new HashSet<FileObject>();
        final Set<SearchResult> idSearchResult = new HashSet<SearchResult>();
        search(AdaIndexer.FIELD_IDENTIFIER, identifierName.toLowerCase(), NameKind.PREFIX, idSearchResult, ALL_SCOPE, TERMS_BASE);
        for (SearchResult searchResult : idSearchResult) {
            result.add(FileUtil.toFileObject(new File(URI.create(searchResult.getPersistentUrl()))));
        }
        return result;
    }

    public Set<String> typeNamesForIdentifier(String identifierName, ElementKind kind, NameKind nameKind) {
        return typeNamesForIdentifier(identifierName, kind, nameKind, ALL_SCOPE);
    }

    public Set<String> typeNamesForIdentifier(String identifierName, ElementKind kind, NameKind nameKind, Set<SearchScope> scope) {
        final Set<String> result = new HashSet<String>();
        final Set<SearchResult> idSearchResult = new HashSet<SearchResult>();
        search(AdaIndexer.FIELD_IDENTIFIER_DECLARATION, identifierName.toLowerCase(), NameKind.PREFIX, idSearchResult, scope, TERMS_BASE);
        for (SearchResult searchResult : idSearchResult) {
            if (searchResult.getPersistentUrl() != null) {
                String[] signatures = searchResult.getValues(AdaIndexer.FIELD_IDENTIFIER_DECLARATION);
                if (signatures == null) {
                    continue;
                }
                for (String sign : signatures) {
                    IdentifierSignature idSign = IdentifierSignature.createDeclaration(Signature.get(sign));
                    if ((!idSign.isClassMember() && !idSign.isIfaceMember()) ||
                            idSign.getTypeName() == null) {
                        continue;
                    }
                    switch (nameKind) {
                        case CASE_INSENSITIVE_PREFIX:
                            if (!idSign.getName().startsWith(identifierName.toLowerCase())) {
                                continue;
                            }
                            break;
                        case PREFIX:
                            if (!idSign.getName().startsWith(identifierName)) {
                                continue;
                            }
                            break;
                        default:
                            assert false : nameKind.toString();
                            continue;
                    }
                    if (kind == null) {
                        result.add(idSign.getTypeName());
                    } else if (kind.equals(ElementKind.FIELD) && idSign.isField()) {
                        result.add(idSign.getTypeName());
                    } else if (kind.equals(ElementKind.METHOD) && idSign.isMethod()) {
                        result.add(idSign.getTypeName());
                    } else if (kind.equals(ElementKind.CONSTANT) && idSign.isClassConstant()) {
                        result.add(idSign.getTypeName());
                    }
                }
            }
        }
        return result;
    }

    public Collection<IndexedPackageSpec> getPackageSpec(AdaParseResult context, String name, NameKind kind) {
        return getPackageSpec(context, name, kind, ALL_SCOPE);
    }

    public Collection<IndexedPackageSpec> getPackageSpec(AdaParseResult context, String name, NameKind kind, Set<SearchScope> scope) {
        final Set<SearchResult> result = new HashSet<SearchResult>();
        Collection<IndexedPackageSpec> pkg = new ArrayList<IndexedPackageSpec>();
        search(AdaIndexer.FIELD_PKGSPC, name.toLowerCase(), NameKind.PREFIX, result, scope, TERMS_PKGSPC);
        findPackageSpec(result, kind, name, pkg);

        return pkg;
    }

    public Collection<IndexedPackageBody> getPackageBody(AdaParseResult context, String name, NameKind kind) {
        return getPackageBody(context, name, kind, ALL_SCOPE);
    }

    public Collection<IndexedPackageBody> getPackageBody(AdaParseResult context, String name, NameKind kind, Set<SearchScope> scope) {
        final Set<SearchResult> result = new HashSet<SearchResult>();
        Collection<IndexedPackageBody> pkg = new ArrayList<IndexedPackageBody>();
        search(AdaIndexer.FIELD_PKGBDY, name.toLowerCase(), NameKind.PREFIX, result, scope, TERMS_PKGSPC);
        findPackageBody(result, kind, name, pkg);

        return pkg;
    }

    // copied from JspUtils
    /** Returns an absolute context URL (starting with '/') for a relative URL and base URL.
     *  @param relativeTo url to which the relative URL is related. Treated as directory iff
     *    ends with '/'
     *  @param url the relative URL by RFC 2396
     *  @exception IllegalArgumentException if url is not absolute and relativeTo
     * can not be related to, or if url is intended to be a directory
     */
    static String resolveRelativeURL(String relativeTo, String url) {
        //System.out.println("- resolving " + url + " relative to " + relativeTo);
        String result;
        if (url.startsWith("/")) { // NOI18N
            result = "/"; // NOI18N
            url = url.substring(1);
        } else {
            // canonize relativeTo
            if ((relativeTo == null) || (!relativeTo.startsWith("/"))) // NOI18N
            {
                throw new IllegalArgumentException();
            }
            relativeTo = resolveRelativeURL(null, relativeTo);
            int lastSlash = relativeTo.lastIndexOf('/');
            if (lastSlash == -1) {
                throw new IllegalArgumentException();
            }
            result = relativeTo.substring(0, lastSlash + 1);
        }

        // now url does not start with '/' and result starts with '/' and ends with '/'
        StringTokenizer st = new StringTokenizer(url, "/", true); // NOI18N
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            //System.out.println("token : \"" + tok + "\""); // NOI18N
            if (tok.equals("/")) { // NOI18N
                if (!result.endsWith("/")) // NOI18N
                {
                    result = result + "/"; // NOI18N
                }
            } else if (tok.equals("")) // NOI18N
            ; // do nohing
            else if (tok.equals(".")) // NOI18N
            ; // do nohing
            else if (tok.equals("..")) { // NOI18N
                String withoutSlash = result.substring(0, result.length() - 1);
                int ls = withoutSlash.lastIndexOf("/"); // NOI18N
                if (ls != -1) {
                    result = withoutSlash.substring(0, ls + 1);
                }
            } else {
                // some file
                result = result + tok;
            }
        //System.out.println("result : " + result); // NOI18N
        }
        //System.out.println("- resolved to " + result);
        return result;
    }
}
