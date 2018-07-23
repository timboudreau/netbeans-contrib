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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.ada.editor.ast.nodes.BodyDeclaration.Modifier;
import org.netbeans.modules.ada.editor.parser.AdaParseResult;
import org.netbeans.modules.ada.project.api.AdaSourcePath;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomasz.Slota@Sun.COM
 */
public class AdaIndex {

    private static final Logger LOG = Logger.getLogger(AdaIndex.class.getName());
    /** Set property to true to find ALL functions regardless of file includes */
    //private static final boolean ALL_REACHABLE = Boolean.getBoolean("javascript.findall");
    public static final int ANY_ATTR = 0xFFFFFFFF;
    private static String clusterUrl = null;
    private static final String CLUSTER_URL = "cluster:"; // NOI18N
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
    private static final String[] TOP_LEVEL_TERMS = new String[]{AdaIndexer.FIELD_BASE,
        AdaIndexer.FIELD_CONST, AdaIndexer.FIELD_PKGSPC, AdaIndexer.FIELD_PKGBDY, AdaIndexer.FIELD_VAR};
    private final QuerySupport index;

    /** Creates a new instance of JsIndex */
    public AdaIndex(QuerySupport index) {
        this.index = index;
    }

//    public static AdaIndex get(QuerySupport index) {
//        return new AdaIndex(index);
//    }

    public static AdaIndex get(Collection<FileObject> roots) {
        try {
            return new AdaIndex(QuerySupport.forRoots(AdaIndexer.Factory.NAME,
                    AdaIndexer.Factory.VERSION,
                    roots.toArray(new FileObject[roots.size()])));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return new AdaIndex(null);
        }

    }

    public static AdaIndex get(ParserResult info){
        // TODO: specify the claspath ids to improve performance and avoid conflicts
        return get(QuerySupport.findRoots(info.getSnapshot().getSource().getFileObject(), Collections.singleton(AdaSourcePath.SOURCE_CP), Collections.singleton(AdaSourcePath.BOOT_CP), Collections.<String>emptySet()));
    }

    public Collection<IndexedElement> getAllTopLevel(AdaParseResult context, String prefix, QuerySupport.Kind nameKind) {
        final Collection<IndexedElement> elements = new ArrayList<IndexedElement>();
        Collection<IndexedPackageSpec> classes = new ArrayList<IndexedPackageSpec>();
        Collection<IndexedVariable> vars = new ArrayList<IndexedVariable>();

        // search through the top leve elements
        final Collection<? extends IndexResult> result = search(AdaIndexer.FIELD_TOP_LEVEL,
                prefix.toLowerCase(), QuerySupport.Kind.PREFIX, TOP_LEVEL_TERMS);

        findPackageSpec(result, nameKind, prefix, classes);
        findTopVariables(result, nameKind, prefix, vars);
        elements.addAll(classes);
        elements.addAll(vars);
        return elements;
    }

    protected void findPackageSpec(final Collection<? extends IndexResult> result, QuerySupport.Kind kind, String name, Collection<IndexedPackageSpec> packages) {
        for (IndexResult map : result) {
            String[] signatures = map.getValues(AdaIndexer.FIELD_PKGSPC);
            if (signatures == null) {
                continue;
            }
            for (String signature : signatures) {
                Signature sig = Signature.get(signature);
                String packageName = sig.string(1);
                if (kind == QuerySupport.Kind.PREFIX || kind == QuerySupport.Kind.CASE_INSENSITIVE_PREFIX) {
                    //case sensitive
                    if (!packageName.toLowerCase().startsWith(name.toLowerCase())) {
                        continue;
                    }
                } else if (kind == QuerySupport.Kind.EXACT) {
                    if (!packageName.toLowerCase().equals(name.toLowerCase())) {
                        continue;
                    }
                }
                //TODO: handle search kind
                int offset = sig.integer(2);
                String superClass = sig.string(3);
                superClass = superClass.length() == 0 ? null : superClass;
                IndexedPackageSpec clazz = new IndexedPackageSpec(packageName, null, this, map.getUrl().toString(), offset, 0);
                //clazz.setResolved(context != null && isReachable(context, map.getUrl().toString()));
                packages.add(clazz);
            }
        }
    }

    protected void findPackageBody(final Collection<? extends IndexResult> result, QuerySupport.Kind kind, String name, Collection<IndexedPackageBody> packages) {
        for (IndexResult map : result) {
            String[] signatures = map.getValues(AdaIndexer.FIELD_PKGBDY);
            if (signatures == null) {
                continue;
            }
            for (String signature : signatures) {
                Signature sig = Signature.get(signature);
                String packageName = sig.string(1);
                if (kind == QuerySupport.Kind.PREFIX || kind == QuerySupport.Kind.CASE_INSENSITIVE_PREFIX) {
                    //case sensitive
                    if (!packageName.toLowerCase().startsWith(name.toLowerCase())) {
                        continue;
                    }
                } else if (kind == QuerySupport.Kind.EXACT) {
                    if (!packageName.toLowerCase().equals(name.toLowerCase())) {
                        continue;
                    }
                }
                //TODO: handle search kind
                int offset = sig.integer(2);
                String superClass = sig.string(3);
                superClass = superClass.length() == 0 ? null : superClass;
                IndexedPackageBody clazz = new IndexedPackageBody(packageName, null, this, map.getUrl().toString(), offset, 0);
                //clazz.setResolved(context != null && isReachable(context, map.getUrl().toString()));
                packages.add(clazz);
            }
        }
    }

    protected void findTopVariables(final Collection<? extends IndexResult> result, QuerySupport.Kind kind, String name, Collection<IndexedVariable> vars) {
        for (IndexResult map : result) {
            if (map.getUrl().toString() != null) {
                String[] signatures = map.getValues(AdaIndexer.FIELD_VAR);
                if (signatures == null) {
                    continue;
                }
                for (String signature : signatures) {
                    Signature sig = Signature.get(signature);
                    //sig.string(0) is the case insensitive search key
                    String constName = sig.string(1);
                    if (kind == QuerySupport.Kind.PREFIX || kind == QuerySupport.Kind.CASE_INSENSITIVE_PREFIX) {
                        //case sensitive
                        if (!constName.startsWith(name)) {
                            continue;
                        }
                    } else if (kind == QuerySupport.Kind.EXACT) {
                        if (!constName.equals(name)) {
                            continue;
                        }
                    }
                    String typeName = sig.string(2);
                    typeName = typeName.length() == 0 ? null : typeName;
                    int offset = sig.integer(3);
                    IndexedVariable var = new IndexedVariable(constName, null, this,
                            map.getUrl().toString(), offset, 0, typeName);
                    //var.setResolved(context != null && isReachable(context, map.getUrl().toString()));
                    vars.add(var);
                }
            }
        }
    }

    private Collection<? extends IndexResult> search(String key, String name, QuerySupport.Kind kind, String... terms) {
        try {
            Collection<? extends IndexResult> results = index.query(key, name, kind, terms);

            if (LOG.isLoggable(Level.FINE)) {
                String msg = "PHPIndex.search(" + key + ", " + name + ", " + kind + ", " //NOI18N
                        + (terms == null || terms.length == 0 ? "no terms" : Arrays.asList(terms)) + ")"; //NOI18N
                LOG.fine(msg);

                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.log(Level.FINEST, null, new Throwable(msg));
                }

                for (IndexResult r : results) {
                    LOG.fine("Fields in " + r + " (" + r.getFile().getPath() + "):"); //NOI18N
                    for (String field : AdaIndexer.ALL_FIELDS) {
                        String value = r.getValue(field);
                        if (value != null) {
                            LOG.fine(" <" + field + "> = <" + value + ">"); //NOI18N
                        }
                    }
                    LOG.fine("----"); //NOI18N
                }

                LOG.fine("===="); //NOI18N
            }

            return results;
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
            return Collections.<IndexResult>emptySet();
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

    /** returns all methods of a package. */
    public Collection<IndexedFunction> getAllMethods(AdaParseResult context, String typeName, String name, QuerySupport.Kind kind, int attrMask) {
        Map<String, IndexedFunction> methods = new TreeMap<String, IndexedFunction>();

        // #147730 - prefer the current file
        FileObject currentFile = context != null ? context.getSnapshot().getSource().getFileObject() : null;
        Set<String> currentFileClasses = new HashSet<String>();

        for (String className : getClassAncestors(context, typeName)) {
            int mask = className.equals(typeName) ? attrMask : (attrMask & (~Modifier.PRIVATE));

            for (IndexedFunction method : getMethods(context, className, name, kind, mask)) {
                String methodName = method.getName();

                if (!methods.containsKey(methodName) || className.equals(typeName)) {
                    methods.put(methodName, method);
                    if (currentFile != null && currentFile.equals(method.getFilenameUrl())) {
                        currentFileClasses.add(className);
                    }
                }
            }
        }

        Collection<IndexedFunction> result = methods.values();
        filterClassMembers(result, currentFileClasses, currentFile);
        return result;
    }

    /** returns all fields of a package. */
    public Collection<IndexedVariable> getAllFields(AdaParseResult context, String typeName, String name, QuerySupport.Kind kind, int attrMask) {
        Map<String, IndexedVariable> fields = new TreeMap<String, IndexedVariable>();

        // #147730 - prefer the current file
        FileObject currentFile = context != null ? context.getSnapshot().getSource().getFileObject() : null;
        Set<String> currentFileClasses = new HashSet<String>();

        for (String className : getClassAncestors(context, typeName)) {
            for (IndexedVariable field : getFields(context, className, name, kind)) {
                String fieldName = field.getName();

                if (!fields.containsKey(fieldName) || className.equals(typeName)) {
                    fields.put(fieldName, field);
                }

                if (currentFile != null && field != null && currentFile.equals(field.getFilenameUrl())) {
                    currentFileClasses.add(className);
                }
            }
        }

        Collection<IndexedVariable> result = fields.values();
        filterClassMembers(result, currentFileClasses, currentFile);
        return result;
    }

    /** returns all fields of a package. */
    public Collection<IndexedType> getAllTypes(AdaParseResult context, String typeName, String name, QuerySupport.Kind kind, int attrMask) {
        Map<String, IndexedType> types = new TreeMap<String, IndexedType>();

        // #147730 - prefer the current file
        FileObject currentFile = context != null ? context.getSnapshot().getSource().getFileObject() : null;
        Set<String> currentFileClasses = new HashSet<String>();

        for (String className : getClassAncestors(context, typeName)) {
            for (IndexedType field : getTypes(context, className, name, kind)) {
                String fieldName = field.getName();

                if (!types.containsKey(fieldName) || className.equals(typeName)) {
                    types.put(fieldName, field);
                }

                if (currentFile != null && field != null && currentFile.equals(field.getFilenameUrl())) {
                    currentFileClasses.add(className);
                }
            }
        }

        Collection<IndexedType> result = types.values();
        filterClassMembers(result, currentFileClasses, currentFile);
        return result;
    }

    /** Current file for the context or <code>null</code> */
//    private File getCurrentFile(AdaParseResult context) {
//        if (context != null && context.getFile() != null) {
//            return context.getFile().getFile();
//        }
//        return null;
//    }

    // #147730 - prefer the current file
    private void filterClassMembers(Collection<? extends IndexedElement> elements, Set<String> currentFileClasses, FileObject currentFile) {
        if (elements.size() > 0 && currentFileClasses.size() > 0) {
            for (Iterator<? extends IndexedElement> it = elements.iterator(); it.hasNext();) {
                IndexedElement method = it.next();
                if (currentFileClasses.contains(method.getIn()) && !currentFile.equals(method.getFilenameUrl())) {
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
        Collection<IndexedPackageSpec> classes = getPackageSpec(context, className, QuerySupport.Kind.EXACT);

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

    /** returns methods of a package. */
    public Collection<IndexedFunction> getMethods(AdaParseResult context, String typeName, String name, QuerySupport.Kind kind, int attrMask) {
        Collection<IndexedFunction> methods = new ArrayList<IndexedFunction>();
        Map<String, IndexResult> signaturesMap = getTypeSpecificSignatures(typeName, AdaIndexer.FIELD_METHOD, name, kind);

        for (String signature : signaturesMap.keySet()) {
            //items are not indexed, no case insensitive search key user
            Signature sig = Signature.get(signature);
            int flags = sig.integer(5);

            if ((flags & (Modifier.PUBLIC | Modifier.PRIVATE)) == 0) {
                flags |= Modifier.PUBLIC; // default modifier
            }

            if ((flags & attrMask) != 0) {
                String funcName = sig.string(0);
                String args = sig.string(1);
                int offset = sig.integer(2);

                IndexedFunction func = new IndexedFunction(funcName, typeName,
                        this, signaturesMap.get(signature).getUrl().toString(), args, offset, flags, ElementKind.METHOD);

                int optionalArgs[] = extractOptionalArgs(sig.string(3));
                func.setOptionalArgs(optionalArgs);
                String retType = sig.string(4);
                retType = retType.length() == 0 ? null : retType;
                func.setReturnType(retType);
                methods.add(func);
            }

        }

        return methods;
    }

    /** returns fields of a package. */
    public Collection<IndexedVariable> getFields(AdaParseResult context, String typeName, String name, QuerySupport.Kind kind) {
        Collection<IndexedVariable> fields = new ArrayList<IndexedVariable>();
        Map<String, IndexResult> signaturesMap = getTypeSpecificSignatures(typeName, AdaIndexer.FIELD_FIELD, name, kind);

        for (String signature : signaturesMap.keySet()) {
            Signature sig = Signature.get(signature);
            int flags = sig.integer(2);

            if ((flags & (Modifier.PUBLIC | Modifier.PRIVATE)) == 0) {
                flags |= Modifier.PUBLIC; // default modifier
            }

            /*
            if ((flags & attrMask) != 0) {
            String propName = "$" + sig.string(0);
            int offset = sig.integer(1);
            String type = sig.string(3);

            if (type.length() == 0){
            type = null;
            }

            IndexedConstant prop = new IndexedConstant(propName, typeName,
            this, signaturesMap.get(signature), offset, flags, type,ElementKind.FIELD);

            fields.add(prop);
            }
             */
        }

        return fields;
    }

    /** returns fields of a package. */
    public Collection<IndexedType> getTypes(AdaParseResult context, String typeName, String name, QuerySupport.Kind kind) {
        Collection<IndexedType> types = new ArrayList<IndexedType>();
        Map<String, IndexResult> signaturesMap = getTypeSpecificSignatures(typeName, AdaIndexer.FIELD_TYPE, name, kind);
        return types;
    }

    private Map<String, IndexResult> getTypeSpecificSignatures(String typeName, String fieldName, String name, QuerySupport.Kind kind) {
        return getTypeSpecificSignatures(typeName, fieldName, name, kind, false);
    }

    private Map<String, IndexResult> getTypeSpecificSignatures(String typeName, String fieldName, String name,
            QuerySupport.Kind kind, boolean forConstructor) {
        Map<String, IndexResult> signatures = new HashMap<String, IndexResult>();
        for (String indexField : new String[]{AdaIndexer.FIELD_PKGSPC, AdaIndexer.FIELD_PKGBDY}) {
            final Collection<? extends IndexResult> indexResult = search(indexField, typeName.toLowerCase(), QuerySupport.Kind.PREFIX,
                                                    new String [] {indexField, fieldName, AdaIndexer.FIELD_BASE});

            for (IndexResult typeMap : indexResult) {
                String[] typeSignatures = typeMap.getValues(indexField);
                String[] rawSignatures = typeMap.getValues(fieldName);

                if (typeSignatures == null || rawSignatures == null) {
                    continue;
                }

                assert typeSignatures.length == 1;
                String foundTypeName = getSignatureItem(typeSignatures[0], 1);
                foundTypeName = (foundTypeName != null) ? foundTypeName.toLowerCase() : null;

                if (!typeName.toLowerCase().equals(foundTypeName)) {
                    continue;
                }

                for (String signature : rawSignatures) {
                    String elemName = getSignatureItem(signature, 0);

                    // TODO: now doing IC prefix search only, handle other search types
                    // according to 'kind'
                    if ((kind == QuerySupport.Kind.CASE_INSENSITIVE_PREFIX && elemName.toLowerCase().startsWith(name.toLowerCase())) || (kind == QuerySupport.Kind.PREFIX && elemName.startsWith(name)) || (kind == QuerySupport.Kind.EXACT && elemName.equals(name))) {
                            signatures.put(signature, typeMap);
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

    public Collection<IndexedVariable> getTopLevelVariables(AdaParseResult context, String name, QuerySupport.Kind kind) {
        Collection<IndexedVariable> vars = new ArrayList<IndexedVariable>();
        Collection<? extends IndexResult> result = search(AdaIndexer.FIELD_VAR, name.toLowerCase(), QuerySupport.Kind.PREFIX, AdaIndexer.FIELD_VAR);
        findTopVariables(result, kind, name, vars);
        return vars;
    }

    public Set<FileObject> filesWithIdentifiers(String identifierName) {
        final Set<FileObject> result = new HashSet<FileObject>();

        Collection<? extends IndexResult> idIndexResult = search(AdaIndexer.FIELD_IDENTIFIER, identifierName.toLowerCase(), QuerySupport.Kind.PREFIX, AdaIndexer.FIELD_BASE);
        for (IndexResult indexResult : idIndexResult) {
            URL url = indexResult.getUrl();
            FileObject fo = null;
            try {
                fo = "file".equals(url.getProtocol()) ? //NOI18N
                    FileUtil.toFileObject(new File(url.toURI())) : URLMapper.findFileObject(url);
            } catch (URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (fo != null) {
                result.add(fo);
            }
        }
        return result;
    }

    public Set<String> typeNamesForIdentifier(String identifierName, ElementKind kind,QuerySupport.Kind nameKind) {
        final Set<String> result = new HashSet<String>();
        Collection<? extends IndexResult> idIndexResult = search(AdaIndexer.FIELD_IDENTIFIER_DECLARATION, identifierName.toLowerCase(), QuerySupport.Kind.PREFIX);
        for (IndexResult IndexResult : idIndexResult) {
            String[] signatures = IndexResult.getValues(AdaIndexer.FIELD_IDENTIFIER_DECLARATION);
            if (signatures == null) {
                continue;
            }
            for (String sign : signatures) {
                IdentifierSignature idSign = IdentifierSignature.createDeclaration(Signature.get(sign));
                if ((!idSign.isPackageMember() && !idSign.isIfaceMember()) ||
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
        return result;
    }

    public Collection<IndexedPackageSpec> getPackageSpec(AdaParseResult context, String name, QuerySupport.Kind kind) {
        Collection<IndexedPackageSpec> packages = new ArrayList<IndexedPackageSpec>();
        final Collection<? extends IndexResult> result = search(AdaIndexer.FIELD_PKGSPC, name.toLowerCase(), QuerySupport.Kind.PREFIX);
        findPackageSpec(result, kind, name, packages);

        return packages;
    }

    public Collection<IndexedPackageBody> getPackageBody(AdaParseResult context, String name, QuerySupport.Kind kind) {
        Collection<IndexedPackageBody> packages = new ArrayList<IndexedPackageBody>();
        final Collection<? extends IndexResult> result = search(AdaIndexer.FIELD_PKGBDY, name.toLowerCase(), QuerySupport.Kind.PREFIX);
        findPackageBody(result, kind, name, packages);

        return packages;
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

    private int[] extractOptionalArgs(String optionalParamsStr) {
        if (optionalParamsStr.length() == 0) {
            return new int[0];
        }

        String optionalParamsStrParts[] = optionalParamsStr.split(",");
        int optionalArgs[] = new int[optionalParamsStrParts.length];

        for (int i = 0; i < optionalParamsStrParts.length; i++) {
            try {
                optionalArgs[i] = Integer.parseInt(optionalParamsStrParts[i]);
            } catch (NumberFormatException e) {
                System.err.println(String.format("*** couldnt parse '%s', part %d", optionalParamsStr, i));
            }
        }

        return optionalArgs;
    }
}
