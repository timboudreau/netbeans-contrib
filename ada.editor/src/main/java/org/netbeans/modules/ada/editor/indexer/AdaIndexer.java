/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.editor.indexer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.modules.ada.editor.CodeUtils;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.Assignment;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Statement;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultTreePathVisitor;
import org.netbeans.modules.ada.editor.parser.AdaParseResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * 
 * @author Andrea Lucarelli
 */
public final class AdaIndexer extends EmbeddingIndexer {

    private static final Logger LOG = Logger.getLogger(AdaIndexer.class.getName());
    static final boolean PREINDEXING = Boolean.getBoolean("gsf.preindexing");
    private static final FileSystem MEM_FS = FileUtil.createMemoryFileSystem();
    private static final Map<String, FileObject> EXT2FO = new HashMap<String, FileObject>();
    private static final Collection<String> INDEXABLE_EXTENSIONS = Arrays.asList(AdaMimeResolver.getEXTENSIONS());
    // I need to be able to search several things:
    // (1) by function root name, e.g. quickly all functions that start
    //    with "f" should find unknown.foo.
    // (2) by namespace, e.g. I should be able to quickly find all
    //    "foo.bar.b*" functions
    // (3) constructors
    // (4) global variables, preferably in the same way
    // (5) extends so I can do inheritance inclusion!
    // Solution: Store the following:
    // class:name for each class
    // extend:old:new for each inheritance? Or perhaps do this in the class entry
    // fqn: f.q.n.function/global;sig; for each function
    // base: function;fqn;sig
    // The signature should look like this:
    // ;flags;;args;offset;docoffset;browsercompat;types;
    // (between flags and args you have the case sensitive name for flags)
    static final String FIELD_BASE = "base"; //NOI18N
    static final String FIELD_PKGSPC = "pkgspc"; //NOI18N
    static final String FIELD_PKGBDY = "pkgbdy"; //NOI18N
    static final String FIELD_CONST = "const"; //NOI18N
    static final String FIELD_FIELD = "field"; //NOI18N
    static final String FIELD_TYPE = "type"; //NOI18N
    static final String FIELD_METHOD = "method"; //NOI18N
    static final String FIELD_WITH = "with"; //NOI18N
    static final String FIELD_IDENTIFIER = "identifier_used"; //NOI18N
    static final String FIELD_IDENTIFIER_DECLARATION = "identifier_declaration"; //NOI18N
    static final String FIELD_VAR = "var"; //NOI18N
    /** This field is for fast access top level elemnts */
    static final String FIELD_TOP_LEVEL = "top"; //NOI18N

    static final String [] ALL_FIELDS = new String [] {
        FIELD_BASE,
        FIELD_PKGSPC,
        FIELD_PKGBDY,
        FIELD_CONST,
        FIELD_FIELD,
        FIELD_METHOD,
        FIELD_WITH,
        FIELD_IDENTIFIER,
        FIELD_IDENTIFIER_DECLARATION,
        FIELD_VAR,
        FIELD_TOP_LEVEL,
    };

    public String getPersistentUrl(File file) {
        String url;
        try {
            url = file.toURI().toURL().toExternalForm();
            // Make relative URLs for urls in the libraries
            return AdaIndex.getPreindexUrl(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return file.getPath();
        }

    }

    @Override
    protected void index(Indexable indexable, Result parserResult, Context context) {
        try {
            AdaParseResult r = (AdaParseResult) parserResult;
            if (r.getProgram() == null) {
                return;
            }
            IndexingSupport support = IndexingSupport.getInstance(context);
            TreeAnalyzer analyzer = new TreeAnalyzer(indexable, r, support);
            analyzer.analyze();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public String getIndexVersion() {
        return "1.0"; // NOI18N
    }

    public String getIndexerName() {
        return "ada"; // NOI18N
    }

    private static class TreeAnalyzer {

        private final Indexable indexable;
        private final AdaParseResult result;
        private final IndexingSupport support;

        public TreeAnalyzer(Indexable indexable, AdaParseResult result, IndexingSupport support) {
            this.indexable = indexable;
            this.result = result;
            this.support = support;
        }

        public void analyze() throws IOException {
            Program root = result.getProgram();
            IndexerVisitor indexerVisitor = new IndexerVisitor(support, indexable, root);
            root.accept(indexerVisitor);
            indexerVisitor.addIdentifierPairs();

            List<? extends IndexDocument> documents = indexerVisitor.getAllDocuments();
            assert documents.size() > 0;
            final IndexDocument defaultDocument = documents.get(0);

            String processedFileURL = null;
            try {
                processedFileURL = result.getSnapshot().getSource().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }

            if (processedFileURL != null) {
                final String processedFileAbsPath = processedFileURL.substring("file:".length());
                final StringBuilder includes = new StringBuilder();
                for (Statement statement : root.getStatements()) {
                    new DefaultTreePathVisitor() {

                        @Override
                        public void visit(Assignment node) {
                            List<ASTNode> path = getPath();
                            boolean indexVariableEnabled = true;
                            for (ASTNode aSTNode : path) {
                                if (aSTNode instanceof MethodDeclaration) {
                                    indexVariableEnabled = false;
                                    break;
                                }
                            }
                            super.visit(node);
                        }
                    }.scan(statement);
                }

                defaultDocument.addPair(FIELD_WITH, includes.toString(), false, true);
            }

            // add all documents to the indexing support
            for (IndexDocument d : documents) {
                support.addDocument(d);
            }
        }
    }

    private static void indexFieldsDeclaration(FieldsDeclaration fieldsDeclaration, IndexDocument document) {
        for (SingleFieldDeclaration field : fieldsDeclaration.getFields()) {
            if (field.getName().getName() instanceof Identifier) {
                Identifier identifier = (Identifier) field.getName().getName();
                String signature = createFieldsDeclarationRecord(identifier.getName(), field.getStartOffset());
                document.addPair(FIELD_FIELD, signature, false, true);
            }
        }
    }

    private static String createFieldsDeclarationRecord(String name, int offset) {
        StringBuilder fieldSignature = new StringBuilder();
        fieldSignature.append(name + ";"); //NOI18N
        fieldSignature.append(offset + ";"); //NOI18N
        fieldSignature.append(";"); //NOI18N
        return fieldSignature.toString();
    }

    private static void indexTypeDeclaration(TypeDeclaration typeDeclaration, IndexDocument document) {
        if (typeDeclaration.getTypeName() instanceof Identifier) {
            Identifier identifier = (Identifier) typeDeclaration.getTypeName();
            String signature = createTypeDeclarationRecord(identifier.getName(), typeDeclaration.getStartOffset());
            document.addPair(FIELD_FIELD, signature, false, true);
        }
    }

    private static String getBaseSignatureForSubprogramSpecification(SubprogramSpecification subprogramSpecification, Program root) {
        String subprogName = subprogramSpecification.getSubprogramName().getName();
        int paramCount = subprogramSpecification.getFormalParameters().size();
        int offset = (subprogramSpecification != null) ? subprogramSpecification.getStartOffset() : 0;
        return getBaseSignatureForSubprogramSpecification(subprogName, paramCount, offset, subprogramSpecification, root);
    }

    private static String getBaseSignatureForSubprogramBody(SubprogramBody subprogramBody, Program root) {
        String subprogName = subprogramBody.getSubprogramSpecification().getSubprogramName().getName();
        int paramCount = subprogramBody.getSubprogramSpecification().getFormalParameters().size();
        int offset = (subprogramBody != null) ? subprogramBody.getStartOffset() : 0;
        return getBaseSignatureForSubprogramBody(subprogName, paramCount, offset, subprogramBody, root);
    }

    /**
     * @param fncName
     * @param paramCount
     * @param offset
     * @param functionDeclaration maybe null just in case when paramCount == 0
     * @return
     */
    private static String getBaseSignatureForSubprogramSpecification(String fncName, int paramCount, int offset,
            SubprogramSpecification subprogramSpecification, Program root) {
        assert subprogramSpecification != null || paramCount == 0;
        StringBuilder signature = new StringBuilder();
        signature.append(fncName + ";");
        StringBuilder defaultArgs = new StringBuilder();
        for (int i = 0; i < paramCount; i++) {
            assert subprogramSpecification != null;
            FormalParameter param = subprogramSpecification.getFormalParameters().get(i);
            String paramName = CodeUtils.getParamDisplayName(param);
            signature.append(paramName);
            if (i < paramCount - 1) {
                signature.append(",");
            }
            if (param.getDefaultValue() != null) {
                if (defaultArgs.length() > 0) {
                    defaultArgs.append(',');
                }
                defaultArgs.append(Integer.toString(i));
            }
        }
        signature.append(';');
        signature.append(offset + ";"); //NOI18N
        signature.append(defaultArgs + ";");
//            String type = functionDeclaration != null ? getReturnTypeFromPHPDoc(functionDeclaration) : null;
//            if (type != null && !PredefinedSymbols.MIXED_TYPE.equalsIgnoreCase(type)) {
//                signature.append(type);
//            }
        signature.append(";"); //NOI18N
        return signature.toString();
    }

    private static String getBaseSignatureForSubprogramBody(String fncName, int paramCount, int offset,
            SubprogramBody subprogramBody, Program root) {
        assert subprogramBody != null || paramCount == 0;
        StringBuilder signature = new StringBuilder();
        signature.append(fncName + ";");
        StringBuilder defaultArgs = new StringBuilder();
        for (int i = 0; i < paramCount; i++) {
            assert subprogramBody != null;
            FormalParameter param = subprogramBody.getSubprogramSpecification().getFormalParameters().get(i);
            String paramName = CodeUtils.getParamDisplayName(param);
            signature.append(paramName);
            if (i < paramCount - 1) {
                signature.append(",");
            }
            if (param.getDefaultValue() != null) {
                if (defaultArgs.length() > 0) {
                    defaultArgs.append(',');
                }
                defaultArgs.append(Integer.toString(i));
            }
        }
        signature.append(';');
        signature.append(offset + ";"); //NOI18N
        signature.append(defaultArgs + ";");
//            String type = functionDeclaration != null ? getReturnTypeFromPHPDoc(functionDeclaration) : null;
//            if (type != null && !PredefinedSymbols.MIXED_TYPE.equalsIgnoreCase(type)) {
//                signature.append(type);
//            }
        signature.append(";"); //NOI18N
        return signature.toString();
    }

    private static void indexSubprogSpec(SubprogramSpecification subprogramSpecification, IndexDocument document, Program root) {
        StringBuilder signature = new StringBuilder(subprogramSpecification.getSubprogramName().getName().toLowerCase() + ";");
        signature.append(getBaseSignatureForSubprogramSpecification(subprogramSpecification, root));

        document.addPair(FIELD_BASE, signature.toString(), true, true);
        document.addPair(FIELD_TOP_LEVEL, subprogramSpecification.getSubprogramName().getName().toLowerCase(), true, true);
    }

    private static void indexSubprogBody(SubprogramBody subprogramBody, IndexDocument document, Program root) {
        StringBuilder signature = new StringBuilder(subprogramBody.getSubprogramSpecification().getSubprogramName().getName().toLowerCase() + ";");
        signature.append(getBaseSignatureForSubprogramBody(subprogramBody, root));

        document.addPair(FIELD_BASE, signature.toString(), true, true);
        document.addPair(FIELD_TOP_LEVEL, subprogramBody.getSubprogramSpecification().getSubprogramName().getName().toLowerCase(), true, true);
    }

    private static void indexMethod(SubprogramSpecification subprogramSpecification, int modifiers, IndexDocument document, Program root) {
        StringBuilder signature = new StringBuilder();
        signature.append(getBaseSignatureForSubprogramSpecification(subprogramSpecification, root));
        signature.append(modifiers + ";"); //NOI18N

        document.addPair(FIELD_METHOD, signature.toString(), false, true);
    }

    private static void indexMethod(SubprogramBody subprogramBody, int modifiers, IndexDocument document, Program root) {
        StringBuilder signature = new StringBuilder();
        signature.append(getBaseSignatureForSubprogramBody(subprogramBody, root));
        signature.append(modifiers + ";"); //NOI18N

        document.addPair(FIELD_METHOD, signature.toString(), false, true);
    }

    private static String createTypeDeclarationRecord(String name, int offset) {
        StringBuilder fieldSignature = new StringBuilder();
        fieldSignature.append(name + ";"); //NOI18N
        fieldSignature.append(offset + ";"); //NOI18N
        fieldSignature.append(";"); //NOI18N
        return fieldSignature.toString();
    }

    private static class IndexerVisitor extends DefaultTreePathVisitor {

        private final IndexingSupport support;
        private final Indexable indexable;
        private final Program root;
        private final List<IndexDocument> documents = new LinkedList<IndexDocument>();
        private final IndexDocument defaultDocument;
        private final IndexDocument identifierDocument;
        private final Map<String, IdentifierSignature> identifiers = new HashMap<String, IdentifierSignature>();

        public IndexerVisitor(IndexingSupport support, Indexable indexable, Program root) {
            this.support = support;
            this.indexable = indexable;
            this.root = root;

            this.defaultDocument = support.createDocument(indexable);
            this.documents.add(defaultDocument);

            this.identifierDocument = support.createDocument(indexable);
            this.documents.add(identifierDocument);
        }

        public List<? extends IndexDocument> getAllDocuments() {
            addIdentifierPairs();
            return documents;
        }

        private void addIdentifierPairs() {
            for (IdentifierSignature idSign : identifiers.values()) {
                identifierDocument.addPair(FIELD_IDENTIFIER, idSign.getSignature(), true, true);
            }
            identifiers.clear();
        }

        @Override
        public void visit(Identifier node) {
            IdentifierSignature.add(node, identifiers);
            super.visit(node);
        }

        @Override
        public void visit(PackageSpecification node) {
            // create a new document for each class
            IndexDocument pkgDocument = support.createDocument(indexable);
            documents.add(pkgDocument);
            indexPkgSpec((PackageSpecification) node, pkgDocument, root);
            List<IdentifierSignature> idSignatures = new ArrayList<IdentifierSignature>();
            for (IdentifierSignature idSign : idSignatures) {
                identifierDocument.addPair(FIELD_IDENTIFIER_DECLARATION, idSign.getSignature(), true, true);
            }
            super.visit(node);
        }

        @Override
        public void visit(PackageBody node) {
            // create a new document for each class
            IndexDocument pkgDocument = support.createDocument(indexable);
            documents.add(pkgDocument);
            indexPkgBody((PackageBody) node, pkgDocument);
            List<IdentifierSignature> idSignatures = new ArrayList<IdentifierSignature>();
            for (IdentifierSignature idSign : idSignatures) {
                identifierDocument.addPair(FIELD_IDENTIFIER_DECLARATION, idSign.getSignature(), true, true);
            }
            super.visit(node);
        }

        @Override
        public void visit(SubprogramSpecification node) {
            if (getPath().get(0) instanceof MethodDeclaration) {
                super.visit(node);
                return;
            }

            indexSubprogSpec((SubprogramSpecification) node, defaultDocument, root);
            super.visit(node);
        }

        @Override
        public void visit(SubprogramBody node) {
            if (getPath().get(0) instanceof MethodDeclaration) {
                super.visit(node);
                return;
            }

            indexSubprogBody((SubprogramBody) node, defaultDocument, root);
            super.visit(node);
        }
    }

    private static void indexPkgSpec(PackageSpecification pkgSpecification, IndexDocument document, Program root) {
        StringBuilder classSignature = new StringBuilder();
        classSignature.append(pkgSpecification.getName().getName().toLowerCase() + ";"); //NOI18N
        classSignature.append(pkgSpecification.getName().getName() + ";"); //NOI18N
        classSignature.append(pkgSpecification.getStartOffset() + ";"); //NOI18N

        String superClass = ""; //NOI18N

        classSignature.append(superClass + ";"); //NOI18N
        document.addPair(FIELD_PKGSPC, classSignature.toString(), true, true);
        document.addPair(FIELD_TOP_LEVEL, pkgSpecification.getName().getName().toLowerCase(), true, true);

        for (Statement statement : pkgSpecification.getBody().getStatements()) {
            if (statement instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) statement;
                String methName = CodeUtils.extractMethodName(methodDeclaration);
//                    if (PredefinedSymbols.MAGIC_METHODS.keySet().contains(methName) &&
//                            "__construct".equalsIgnoreCase(methName)) {//NOI18N
//                        isConstructor = true;
//                        indexConstructor(classDeclaration, methodDeclaration.getFunction(), methodDeclaration.getModifier(), document);
//                    }
                if (methodDeclaration.getSubprogramBody() != null) {
                    indexMethod(methodDeclaration.getSubprogramBody(), methodDeclaration.getModifier(), document, root);
                } else {
                    indexMethod(methodDeclaration.getSubprogramSpecification(), methodDeclaration.getModifier(), document, root);
                }
            } else if (statement instanceof FieldsDeclaration) {
                FieldsDeclaration fieldsDeclaration = (FieldsDeclaration) statement;
                indexFieldsDeclaration(fieldsDeclaration, document);
            } else if (statement instanceof TypeDeclaration) {
                TypeDeclaration typeDeclaration = (TypeDeclaration) statement;
                indexTypeDeclaration(typeDeclaration, document);
            }
        }
    }

    private static void indexPkgBody(PackageBody pkgBody, IndexDocument document) {
        StringBuilder classSignature = new StringBuilder();
        classSignature.append(pkgBody.getName().getName().toLowerCase() + ";"); //NOI18N
        classSignature.append(pkgBody.getName().getName() + ";"); //NOI18N
        classSignature.append(pkgBody.getStartOffset() + ";"); //NOI18N

        String superClass = ""; //NOI18N

        classSignature.append(superClass + ";"); //NOI18N
        document.addPair(FIELD_PKGBDY, classSignature.toString(), true, true);
        document.addPair(FIELD_TOP_LEVEL, pkgBody.getName().getName().toLowerCase(), true, true);

        for (Statement statement : pkgBody.getBody().getStatements()) {
            if (statement instanceof FieldsDeclaration) {
                FieldsDeclaration fieldsDeclaration = (FieldsDeclaration) statement;
                indexFieldsDeclaration(fieldsDeclaration, document);
            }
            if (statement instanceof TypeDeclaration) {
                TypeDeclaration typeDeclaration = (TypeDeclaration) statement;
                indexTypeDeclaration(typeDeclaration, document);
            }
        }
    }

    static boolean isQuotedString(String txt) {
        if (txt.length() < 2) {
            return false;
        }

        char firstChar = txt.charAt(0);
        return firstChar == txt.charAt(txt.length() - 1) && firstChar == '\'' || firstChar == '\"';
    }

    static String dequote(String string) {
        assert isQuotedString(string);
        return string.substring(1, string.length() - 1);
    }

    public File getPreindexedData() {
        return null;
    }
    private static FileObject preindexedDb;

    /** For testing only */
    public static void setPreindexedDb(FileObject preindexedDb) {
        AdaIndexer.preindexedDb = preindexedDb;
    }

    public FileObject getPreindexedDb() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * As the above documentation states, this is a temporary solution / hack
     * for 6.1 only.
     */
    public boolean acceptQueryPath(String url) {
        // Filter out JavaScript stuff
        return url.indexOf("jsstubs") == -1 && // NOI18N
                // Filter out Ruby stuff
                url.indexOf("/ruby2/") == -1 && // NOI18N
                url.indexOf("/gems/") == -1 && // NOI18N
                url.indexOf("lib/ruby/") == -1; // NOI18N
    }

    public static final class Factory extends EmbeddingIndexerFactory {

        public static final String NAME = "ada"; // NOI18N
        public static final int VERSION = 1;

        @Override
        public EmbeddingIndexer createIndexer(final Indexable indexable, final Snapshot snapshot) {

            if (isIndexable(indexable, snapshot)) {
                return new AdaIndexer();
            } else {
                return null;
            }
        }

        @Override
        public String getIndexerName() {
            return NAME;
        }

        @Override
        public int getIndexVersion() {
            return VERSION;
        }

        private boolean isIndexable(Indexable indexable, Snapshot snapshot) {
            // Cannot call file.getFileObject().getMIMEType() here for several reasons:
            // (1) when cleaning up the index for deleted files, file.getFileObject().getMIMEType()
            //   may return "content/unknown", and in some cases, file.getFileObject() returns null
            // (2) file.getFileObject() can be expensive during startup indexing when we're
            //   rapidly scanning through lots of directories to determine which files are
            //   indexable. This is done using the java.io.File API rather than the more heavyweight
            //   FileObject, and each file.getFileObject() will perform a FileUtil.toFileObject() call.
            // Since the mime resolver for PHP is simple -- it's just based on the file extension,
            // we perform the same check here:
            //if (PHPLanguage.PHP_MIME_TYPE.equals(file.getFileObject().getMIMEType())) { // NOI18N

            FileObject fileObject = snapshot.getSource().getFileObject();

            if (INDEXABLE_EXTENSIONS.contains(fileObject.getExt().toLowerCase())) {
                return true;
            }

            return isAdaFile(fileObject);
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            try {
                IndexingSupport is = IndexingSupport.getInstance(context);
                for (Indexable i : deleted) {
                    is.removeDocuments(i);
                }
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, null, ioe);
            }
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
            try {
                IndexingSupport is = IndexingSupport.getInstance(context);
                for (Indexable i : dirty) {
                    is.markDirtyDocuments(i);
                }
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, null, ioe);
            }
        }

        private boolean isAdaFile(FileObject file) {
            FileObject fo = null;
            String ext = file.getExt();
            synchronized (EXT2FO) {
                fo = (ext != null) ? EXT2FO.get(ext) : null;
                if (fo == null) {
                    try {
                        fo = FileUtil.createData(MEM_FS.getRoot(), file.getNameExt());
                        if (ext != null && fo != null) {
                            EXT2FO.put(ext, fo);
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            assert fo != null;
            return AdaMimeResolver.ADA_MIME_TYPE.equals(FileUtil.getMIMEType(fo, AdaMimeResolver.ADA_MIME_TYPE));
        }

    } // End of Factory class
}
