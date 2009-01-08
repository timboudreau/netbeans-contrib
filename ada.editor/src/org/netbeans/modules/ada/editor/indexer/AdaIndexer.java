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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.ada.editor.AdaLanguage;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.modules.ada.editor.CodeUtils;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.FunctionDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.ProcedureDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Statement;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultTreePathVisitor;
import org.netbeans.modules.ada.editor.parser.AdaParseResult;
import org.netbeans.modules.gsf.api.IndexDocument;
import org.netbeans.modules.gsf.api.IndexDocumentFactory;
import org.netbeans.modules.gsf.api.Indexer;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * 
 * @author Andrea Lucarelli
 */
public class AdaIndexer implements Indexer {

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

    public boolean isIndexable(ParserFile file) {
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
        if (INDEXABLE_EXTENSIONS.contains(file.getExtension().toLowerCase())) {
            return true;
        }

        return isAdaFile(file);
    }

    private boolean isAdaFile(ParserFile file) {
        FileObject fo = null;
        String ext = file.getExtension();
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

    public List<IndexDocument> index(ParserResult result, IndexDocumentFactory factory) throws IOException {
        AdaParseResult r = (AdaParseResult) result;

        if (r.getProgram() == null) {
            return Collections.<IndexDocument>emptyList();
        }

        TreeAnalyzer analyzer = new TreeAnalyzer(r, factory);
        analyzer.analyze();

        return analyzer.getDocuments();
    }

    public String getIndexVersion() {
        return "1.0"; // NOI18N
    }

    public String getIndexerName() {
        return "ada"; // NOI18N
    }

    private static class TreeAnalyzer {

        private final ParserFile file;
        private String url;
        private final AdaParseResult result;
        private Program root;
        //private final BaseDocument doc;
        private IndexDocumentFactory factory;
        private List<IndexDocument> documents = new ArrayList<IndexDocument>();

        private TreeAnalyzer(AdaParseResult result, IndexDocumentFactory factory) {
            this.result = result;
            this.file = result.getFile();
            this.factory = factory;

            /*FileObject fo = file.getFileObject();

            if (fo != null) {
            this.doc = NbUtilities.getBaseDocument(fo, true);
            } else {
            this.doc = null;
            }
             */
            try {
                url = file.getFile().toURI().toURL().toExternalForm();

                // Make relative URLs for urls in the libraries
                url = AdaIndex.getPreindexUrl(url);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        List<IndexDocument> getDocuments() {
            return documents;
        }

        private void indexFieldsDeclaration(FieldsDeclaration fieldsDeclaration, IndexDocument document) {
            for (SingleFieldDeclaration field : fieldsDeclaration.getFields()) {
                if (field.getName().getName() instanceof Identifier) {
                    Identifier identifier = (Identifier) field.getName().getName();
                    String signature = createFieldsDeclarationRecord(identifier.getName(), field.getStartOffset());
                    document.addPair(FIELD_FIELD, signature, false);
                }
            }
        }

        private String createFieldsDeclarationRecord(String name, int offset) {
            StringBuilder fieldSignature = new StringBuilder();
            fieldSignature.append(name + ";"); //NOI18N
            fieldSignature.append(offset + ";"); //NOI18N
            fieldSignature.append(";"); //NOI18N
            return fieldSignature.toString();
        }

        private void indexTypeDeclaration(TypeDeclaration typeDeclaration, IndexDocument document) {
            if (typeDeclaration.getTypeName() instanceof Identifier) {
                Identifier identifier = (Identifier) typeDeclaration.getTypeName();
                String signature = createTypeDeclarationRecord(identifier.getName(), typeDeclaration.getStartOffset());
                document.addPair(FIELD_FIELD, signature, false);
            }
        }

        private String getBaseSignatureForFunctionDeclaration(FunctionDeclaration functionDeclaration){
            String fncName = functionDeclaration.getIdentifier().getName();
            int paramCount = functionDeclaration.getFormalParameters().size();
            int offset = (functionDeclaration != null) ? functionDeclaration.getStartOffset() : 0;
            return getBaseSignatureForFunctionDeclaration(fncName, paramCount, offset, functionDeclaration);
        }

        private String getBaseSignatureForProcedureDeclaration(ProcedureDeclaration procedureDeclaration){
            String fncName = procedureDeclaration.getIdentifier().getName();
            int paramCount = procedureDeclaration.getFormalParameters().size();
            int offset = (procedureDeclaration != null) ? procedureDeclaration.getStartOffset() : 0;
            return getBaseSignatureForProcedureDeclaration(fncName, paramCount, offset, procedureDeclaration);
        }

        /**
         * @param fncName
         * @param paramCount
         * @param offset
         * @param functionDeclaration maybe null just in case when paramCount == 0
         * @return
         */
        private String getBaseSignatureForFunctionDeclaration(String fncName, int paramCount, int offset,
                FunctionDeclaration functionDeclaration) {
            assert functionDeclaration != null || paramCount == 0;
            StringBuilder signature = new StringBuilder();
            signature.append(fncName + ";");
            StringBuilder defaultArgs = new StringBuilder();
            for (int i = 0; i < paramCount; i++) {
                assert functionDeclaration != null;
                FormalParameter param = functionDeclaration.getFormalParameters().get(i);
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

        private String getBaseSignatureForProcedureDeclaration(String fncName, int paramCount, int offset,
                ProcedureDeclaration procedureDeclaration) {
            assert procedureDeclaration != null || paramCount == 0;
            StringBuilder signature = new StringBuilder();
            signature.append(fncName + ";");
            StringBuilder defaultArgs = new StringBuilder();
            for (int i = 0; i < paramCount; i++) {
                assert procedureDeclaration != null;
                FormalParameter param = procedureDeclaration.getFormalParameters().get(i);
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

        private void indexFunction(FunctionDeclaration functionDeclaration, IndexDocument document) {
            StringBuilder signature = new StringBuilder(functionDeclaration.getIdentifier().getName().toLowerCase() + ";");
            signature.append(getBaseSignatureForFunctionDeclaration(functionDeclaration));

            document.addPair(FIELD_BASE, signature.toString(), true);
            document.addPair(FIELD_TOP_LEVEL, functionDeclaration.getIdentifier().getName().toLowerCase(), true);
        }

        private void indexProcedure(ProcedureDeclaration procedureDeclaration, IndexDocument document) {
            StringBuilder signature = new StringBuilder(procedureDeclaration.getIdentifier().getName().toLowerCase() + ";");
            signature.append(getBaseSignatureForProcedureDeclaration(procedureDeclaration));

            document.addPair(FIELD_BASE, signature.toString(), true);
            document.addPair(FIELD_TOP_LEVEL, procedureDeclaration.getIdentifier().getName().toLowerCase(), true);
        }

        private void indexMethod(FunctionDeclaration functionDeclaration, int modifiers, IndexDocument document) {
            StringBuilder signature = new StringBuilder();
            signature.append(getBaseSignatureForFunctionDeclaration(functionDeclaration));
            signature.append(modifiers + ";"); //NOI18N

            document.addPair(FIELD_METHOD, signature.toString(), false);
        }

        private void indexMethod(ProcedureDeclaration procedureDeclaration, int modifiers, IndexDocument document) {
            StringBuilder signature = new StringBuilder();
            signature.append(getBaseSignatureForProcedureDeclaration(procedureDeclaration));
            signature.append(modifiers + ";"); //NOI18N

            document.addPair(FIELD_METHOD, signature.toString(), false);
        }

        private String createTypeDeclarationRecord(String name, int offset) {
            StringBuilder fieldSignature = new StringBuilder();
            fieldSignature.append(name + ";"); //NOI18N
            fieldSignature.append(offset + ";"); //NOI18N
            fieldSignature.append(";"); //NOI18N
            return fieldSignature.toString();
        }

        private class IndexerVisitor extends DefaultTreePathVisitor {

            private List<IndexDocument> documents;
            private IndexDocument defaultDocument;
            private final IndexDocument identifierDocument = factory.createDocument(10);
            private Map<String, IdentifierSignature> identifiers = new HashMap<String, IdentifierSignature>();

            public IndexerVisitor(List<IndexDocument> documents, IndexDocument defaultDocument) {
                this.documents = documents;
                this.defaultDocument = defaultDocument;
                documents.add(identifierDocument);
            }

            public void addIdentifierPairs() {
                Collection<IdentifierSignature> values = identifiers.values();
                for (IdentifierSignature idSign : values) {
                    identifierDocument.addPair(FIELD_IDENTIFIER, idSign.getSignature(), true);
                }
            }

            @Override
            public void visit(Identifier node) {
                IdentifierSignature.add(node, identifiers);
                super.visit(node);
            }

            @Override
            public void visit(PackageSpecification node) {
                // create a new document for each class
                IndexDocument pkgDocument = factory.createDocument(10);
                documents.add(pkgDocument);
                indexPkgSpec((PackageSpecification) node, pkgDocument);
                List<IdentifierSignature> idSignatures = new ArrayList<IdentifierSignature>();
                for (IdentifierSignature idSign : idSignatures) {
                    identifierDocument.addPair(FIELD_IDENTIFIER_DECLARATION, idSign.getSignature(), true);
                }
                super.visit(node);
            }

            @Override
            public void visit(PackageBody node) {
                // create a new document for each class
                IndexDocument pkgDocument = factory.createDocument(10);
                documents.add(pkgDocument);
                indexPkgBody((PackageBody) node, pkgDocument);
                List<IdentifierSignature> idSignatures = new ArrayList<IdentifierSignature>();
                for (IdentifierSignature idSign : idSignatures) {
                    identifierDocument.addPair(FIELD_IDENTIFIER_DECLARATION, idSign.getSignature(), true);
                }
                super.visit(node);
            }

            @Override
            public void visit(FunctionDeclaration node) {
                if (getPath().get(0) instanceof MethodDeclaration){
                    super.visit(node);
                    return;
                }

                indexFunction((FunctionDeclaration)node, defaultDocument);
                super.visit(node);
            }

            @Override
            public void visit(ProcedureDeclaration node) {
                if (getPath().get(0) instanceof MethodDeclaration){
                    super.visit(node);
                    return;
                }

                indexProcedure((ProcedureDeclaration)node, defaultDocument);
                super.visit(node);
            }
        }

        public void analyze() throws IOException {

            IndexDocument defaultDocument = factory.createDocument(40); // TODO - measure!
            documents.add(defaultDocument);

            root = result.getProgram();
            IndexerVisitor indexerVisitor = new IndexerVisitor(documents, defaultDocument);
            root.accept(indexerVisitor);
            indexerVisitor.addIdentifierPairs();

            String processedFileURL = null;

            try {
                processedFileURL = result.getFile().getFileObject().getURL().toExternalForm();

            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }

            assert processedFileURL.startsWith("file:");
            String processedFileAbsPath = processedFileURL.substring("file:".length());
            StringBuilder with = new StringBuilder();

            // TODO: insert with processing

            defaultDocument.addPair(FIELD_WITH, with.toString(), false);
        }

        private void indexPkgSpec(PackageSpecification pkgSpecification, IndexDocument document) {
            StringBuilder classSignature = new StringBuilder();
            classSignature.append(pkgSpecification.getName().getName().toLowerCase() + ";"); //NOI18N
            classSignature.append(pkgSpecification.getName().getName() + ";"); //NOI18N
            classSignature.append(pkgSpecification.getStartOffset() + ";"); //NOI18N

            String superClass = ""; //NOI18N

            classSignature.append(superClass + ";"); //NOI18N
            document.addPair(FIELD_PKGSPC, classSignature.toString(), true);
            document.addPair(FIELD_TOP_LEVEL, pkgSpecification.getName().getName().toLowerCase(), true);

            for (Statement statement : pkgSpecification.getBody().getStatements()) {
                if (statement instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) statement;
                    String methName = CodeUtils.extractMethodName(methodDeclaration);
//                    if (PredefinedSymbols.MAGIC_METHODS.keySet().contains(methName) &&
//                            "__construct".equalsIgnoreCase(methName)) {//NOI18N
//                        isConstructor = true;
//                        indexConstructor(classDeclaration, methodDeclaration.getFunction(), methodDeclaration.getModifier(), document);
//                    }
                     if (methodDeclaration.getKind() == MethodDeclaration.Kind.FUNCTION) {
                        indexMethod(methodDeclaration.getFunction(), methodDeclaration.getModifier(), document);
                     } else {
                        indexMethod(methodDeclaration.getProcedure(), methodDeclaration.getModifier(), document);
                     }
                }
                else if (statement instanceof FieldsDeclaration) {
                    FieldsDeclaration fieldsDeclaration = (FieldsDeclaration) statement;
                    indexFieldsDeclaration(fieldsDeclaration, document);
                }
                else if (statement instanceof TypeDeclaration) {
                    TypeDeclaration typeDeclaration = (TypeDeclaration) statement;
                    indexTypeDeclaration(typeDeclaration, document);
                }
            }
        }

        private void indexPkgBody(PackageBody pkgBody, IndexDocument document) {
            StringBuilder classSignature = new StringBuilder();
            classSignature.append(pkgBody.getName().getName().toLowerCase() + ";"); //NOI18N
            classSignature.append(pkgBody.getName().getName() + ";"); //NOI18N
            classSignature.append(pkgBody.getStartOffset() + ";"); //NOI18N

            String superClass = ""; //NOI18N

            classSignature.append(superClass + ";"); //NOI18N
            document.addPair(FIELD_PKGBDY, classSignature.toString(), true);
            document.addPair(FIELD_TOP_LEVEL, pkgBody.getName().getName().toLowerCase(), true);

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

}
