/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.erlang.platform.index;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.fpi.gsf.Index;
import org.netbeans.fpi.gsf.Indexer;
import org.netbeans.fpi.gsf.ParserFile;
import org.netbeans.fpi.gsf.ParserResult;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.editor.BaseDocument;
import org.netbeans.fpi.gsf.IndexDocument;
import org.netbeans.fpi.gsf.IndexDocumentFactory;
import org.netbeans.modules.erlang.editing.semantic.ErlContext;
import org.netbeans.modules.erlang.editing.semantic.ErlMacro;
import org.netbeans.modules.erlang.editing.semantic.ErlExport;
import org.netbeans.modules.erlang.editing.semantic.ErlFunction;
import org.netbeans.modules.erlang.editing.semantic.ErlInclude;
import org.netbeans.modules.erlang.editing.semantic.ErlModule;
import org.netbeans.modules.erlang.editing.semantic.ErlRecord;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider;
import org.netbeans.modules.erlang.platform.api.RubyPlatformManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * GSF Indexex will index project resources and resources of boot classess
 * Where boot classes usally will be get from org.netbeans.spi.gsfpath.classpath.ClassPathProvider
 * Project manager should provide an implementation of ClassPathProvider under META-INFO.services
 * for example:
 *     org.netbeans.modules.erlang.project.ProjectClassPathProvider
 *     org.netbeans.modules.erlang.project.BootClassPathProvider
 * The later one will init BootClassPathImplementation, which implemented infterface:
 *     org.netbeans.modules.erlang.project.classpath.BootClassPathImplementation#getResources
 * Since each project may use different platform, so the BootClassPathImplementation will be
 * put in project module, and will get active.platform from project.properties, thus know which
 * boot classes will be used.
 * 
 * @author Caoyuan Deng
 */
public class ErlangIndexer implements Indexer {

    //private static final boolean INDEX_UNDOCUMENTED = Boolean.getBoolean("ruby.index.undocumented");
    private static final boolean INDEX_UNDOCUMENTED = true;
    private static final boolean PREINDEXING = Boolean.getBoolean("gsf.preindexing");

    /** Fields of Module Document for Lucene */
    static final String FIELD_FQN_NAME = "fqn"; //NOI18N
    static final String FIELD_FILEURL = "source"; // NOI18N
    static final String FIELD_MODULE_NAME = "module"; //NOI18N
    static final String FIELD_CASE_INSENSITIVE_MODULE_NAME = "module-ig"; //NOI18N
    static final String FIELD_HEADER_NAME = "header";
    static final String FIELD_CASE_INSENSITIVE_HEADER_NAME = "header-ig"; //NOI18N
    static final String FIELD_EXPORT = "export"; //NOI18N
    static final String FIELD_EXPORTS = "exports"; //NOI18N
    static final String FIELD_IMPORT = "import"; //NOI18N
    static final String FIELD_IMPORTS = "imports"; //NOI18N
    /** Attributes: "i" -> private, "o" -> protected, ", "s" - static/notinstance, "d" - documented */
    static final String FIELD_INCLUDE = "include"; //NOI18N
    static final String FIELD_FUNCTION = "function"; //NOI18N
    static final String FIELD_RECORD = "record"; //NOI18N
    static final String FIELD_MACRO = "macro"; //NOI18N
    /** Attributes: "m" -> module, "d" -> documented, "d(nnn)" documented with n characters */
    static final String FIELD_ATTRS = "attrs"; //NOI18N


    private static InputOutput io = IOProvider.getDefault().getIO("Info", false);
    
    
    private IndexDocumentFactory factory;
    private List<IndexDocument> documents = new ArrayList<IndexDocument>();
    
    
    public ErlangIndexer() {
    }

    public String getPersistentUrl(File file) {
        String url;
        try {
            url = file.toURI().toURL().toExternalForm();
            // Make relative URLs for urls in the libraries
            //return RubyIndex.getPreindexUrl(url);
            return file.getPath();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return file.getPath();
        }

    }

    public List<IndexDocument> index(ParserResult result, IndexDocumentFactory factory) throws IOException {
        ParserFile file = result.getFile();
	long start = System.currentTimeMillis();
        if (file.isPlatform()) io.getOut().print("Indexing: ");

        ErlangLanguageParserResult r = (ErlangLanguageParserResult) result;
        ASTNode root = r.getRootNode();

        if (root == null) {
            return null;
        }

        // I used to suppress indexing files that have had automatic cleanup to
        // remove in-process editing. However, that makes code completion not
        // work for local classes etc. that are being queried. I used to handle
        // that by doing local AST searches but this had a lot of problems
        // (not handling scoping and inheritance well etc.) so now I'm using the
        // index for everything.
        //  if (r.getSanitizedRange() != OffsetRange.NONE) {
        //     return;
        //  }

        TreeAnalyzer analyzer = new TreeAnalyzer(r, factory);
        analyzer.analyze();
	if (file.isPlatform()) io.getOut().println((System.currentTimeMillis() - start) + "ms");
        
        return analyzer.getDocuments();
    }
    
    public static final String[] INDEXABLE_FOLDERS = new String[]{"src", "include", "test"};

    public boolean isIndexable(ParserFile file) {
        FileObject fo = file.getFileObject();
        if (fo == null) {
            /**
             * Not each kind of MIME files hava FileObject, for instance:
             * ParserFile with name as ".LCKxxxxx.erl~" etc will have none FileObject.
             */
            return false;
        }
        double maxMemoryInMBs = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0);
        String path = fo.getPath();
        String ext = file.getNameExt();
        if (ext.endsWith(".erl") || ext.endsWith("hrl")) {
            for (String indexableFolder : INDEXABLE_FOLDERS) {
                if (path.contains(indexableFolder)) {
                    /**
                     * @TODO: a bad hacking:
                     * try to ignore these big files according to max memory size */
                    double fileSizeInKBs = fo.getSize() / 1024.0;
                    /**
                     * 250M:  < 200KB
                     * 500M:  < 400KB
                     * 1500M: < 1200KB
                     */
                    double factor = (maxMemoryInMBs / 250.0) * 200;
                    if (fileSizeInKBs > factor) {
                        if (file.isPlatform()) io.getErr().println("Indexing: " + fo.getPath() + " (skipped due to too big!)");
                        return false;
                    }
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public String getIndexVersion() {
        return "6.102"; // NOI18N
    }

    public String getIndexerName() {
        return "erlang"; // NOI18N
    }
    
    
    /** Travel through parsed result (ErlRoot), and index meta-data */
    private static class TreeAnalyzer {

        private ParserFile file;
        private String url;
        private String imports;
        private ErlangLanguageParserResult result;
        private BaseDocument doc;
        private Index index;
        private ErlangIndexProvider.Type type = ErlangIndexProvider.Type.Header;
        private IndexDocumentFactory factory;
        private List<IndexDocument> documents = new ArrayList<IndexDocument>();

        private TreeAnalyzer(ErlangLanguageParserResult result, IndexDocumentFactory factory) {
            this.result = result;
            this.file = result.getFile();
            this.factory = factory;

            FileObject fo = file.getFileObject();
            try {
                this.doc = getBaseDocument(fo, true);
                String ext = fo.getExt();
                if (ext.equals("hrl")) {
                    this.type = ErlangIndexProvider.Type.Header;
                } else {
                    this.type = ErlangIndexProvider.Type.Module;
                }

                url = file.getFileObject().getURL().toExternalForm();
                if (PREINDEXING) {
                    // Make relative URLs for preindexed data structures
                    //url = ErlangIndex.getPreindexUrl(url);
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        List<IndexDocument> getDocuments() {
            return documents;
        }

        public void analyze() throws IOException {
            // Delete old contents of this file - if we're dealing with a user source file
            if (!file.isPlatform()) {
//                Set<Map<String, String>> indexedSet    = Collections.emptySet();
//                Set<Map<String, String>> notIndexedSet = Collections.emptySet();
//                Map<String, String> toDelete = new HashMap<String, String>();
//                toDelete.put(FIELD_FILEURL, url);
//
//                try {
//                    index.gsfStore(indexedSet, notIndexedSet, toDelete);
//                } catch (IOException ioe) {
//                    Exceptions.printStackTrace(ioe);
//                }
            }

            ErlContext rootCtx = result.getRootContext();
            if (rootCtx == null) {
                return;
            }

            FileObject fo = file.getFileObject();
            String name = null;
            if (type == ErlangIndexProvider.Type.Module) {
                ErlModule module = rootCtx.getFirstDefinition(ErlModule.class);
                if (module != null) {
                    name = module.getName();
                }
            } else {
                if (fo != null) {
                    if (file.isPlatform()) {
                        String libFolder = RubyPlatformManager.getDefaultPlatform().getLib();
                        File libFolderFile = new File(libFolder);
                        if (libFolderFile != null && libFolderFile.exists()) {
                            FileObject libFolderObj = FileUtil.createData(libFolderFile);
                            String relativePath = FileUtil.getRelativePath(libFolderObj, fo);
                            String[] groups = relativePath.split(File.separator);
                            String packageNameWithVersion = groups.length >= 1 ? groups[0] : relativePath;
                            int dashIdx = packageNameWithVersion.lastIndexOf('-');
                            String packageName = dashIdx != -1 ? packageNameWithVersion.substring(0, dashIdx) : packageNameWithVersion;
                            name = packageName;
                            for (int i = 1; i < groups.length; i++) {
                                name = name + "/" + groups[i];
                            }
                        } else {
                            /** Something must be wrong if this happens: */
                            name = null;
                        }
                    } else {
                        name = fo.getNameExt();
                    }
                }
            }
            if (name == null) {
                return;
            }
            /** we will index exported functions and, defined macros etc */
            Collection<ErlInclude> includes = rootCtx.getDefinitions(ErlInclude.class);
            Collection<ErlExport>  exports  = rootCtx.getDefinitions(ErlExport.class);
            Collection<ErlRecord>  records  = rootCtx.getDefinitions(ErlRecord.class);
            Collection<ErlMacro>   macros   = rootCtx.getDefinitions(ErlMacro.class);

            /** @ReferenceOnly used by sqlIndexEngine
             * if (isSqlIndexAvaialble(index)) {
             * long moduleId = sqlIndexEngine.storeModule(module.getName(), url);
             * if (moduleId != -1) {
             * for (ErlExport export : exports) {
             * sqlIndexEngine.storeFunctions(export.getFunctions(), moduleId);
             * }
             * }
             * }
             */

            /** The following code is currently for updating the timestamp only */
            analyzeModule(type, name, includes, exports, records, macros);
        }

        private void analyzeModule(ErlangIndexProvider.Type type, String name, Collection<ErlInclude> includes, Collection<ErlExport> exports, Collection<ErlRecord> records, Collection<ErlMacro> macros) {
            /** Add a Lucene document */
            IndexDocument document = factory.createDocument(40); // TODO - measure!
            documents.add(document);

            String attributes = type == ErlangIndexProvider.Type.Module ? "m" : "h";

            /** @TODO */
            //boolean isDocumented = isDocumented(node);
            //int documentSize = getDocumentSize(node);
            //
            //if (documentSize > 0) {
            //    attributes = attributes + "d(" + documentSize + ")";
            //}
            document.addPair(FIELD_ATTRS, attributes, false);

            switch (type) {
                case Module:
                    document.addPair(FIELD_MODULE_NAME, name, true);
                    document.addPair(FIELD_CASE_INSENSITIVE_MODULE_NAME, name.toLowerCase(), true);
                    break;
                case Header:
                    document.addPair(FIELD_HEADER_NAME, name, true);
                    document.addPair(FIELD_CASE_INSENSITIVE_HEADER_NAME, name.toLowerCase(), true);
                    break;
            }

            // TODO:
            //addIncluded(indexed);
            //if (requires != null) {
            //    document.addPair(FIELD_INCLUDES, incudles, false);
            //}
            // Indexed so we can locate these documents when deleting/updating
            document.addPair(FIELD_FILEURL, url, true);

            for (ErlInclude include : includes) {
                indexInclude(include, document);
            }

            /** we only index exported functions */
            for (ErlExport export : exports) {
                for (ErlFunction function : export.getFunctions()) {
                    indexFunction(function, document);
                }
            }

            for (ErlRecord record : records) {
                indexRecord(record, document);
            }

            for (ErlMacro define : macros) {
                indexMacro(define, document);
            }
        }

        private void indexFunction(ErlFunction function, IndexDocument document) {
            StringBuilder sb = new StringBuilder();
            sb.append(function.getName());

            boolean isDocumented = false; // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(":").append("d");
            } else {
                sb.append(":").append("");
            }

            sb.append(":" + function.getArity());

            sb.append(":").append(function.getOffset() + ":" + function.getEndOffset());

            for (String argumentsOpt : function.getArgumentsOpts()) {
                sb.append(":").append(argumentsOpt);
            }

            document.addPair(FIELD_FUNCTION, sb.toString(), true);


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }

        private void indexInclude(ErlInclude include, IndexDocument document) {
            StringBuilder sb = new StringBuilder();
            sb.append(include.getPath());

            boolean isDocumented = false; // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(":").append("d");
            } else {
                sb.append(":").append("");
            }

            sb.append(":" + include.isLib());

            sb.append(":").append(include.getOffset() + ":" + include.getEndOffset());

            document.addPair(FIELD_INCLUDE, sb.toString(), true);


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }

        private void indexRecord(ErlRecord record, IndexDocument document) {
            StringBuilder sb = new StringBuilder();
            sb.append(record.getName());

            boolean isDocumented = false; // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(":").append("d");
            } else {
                sb.append(":").append("");
            }

            sb.append(":" + record.getFields().size());

            sb.append(":").append(record.getOffset() + ":" + record.getEndOffset());

            for (String field : record.getFields()) {
                sb.append(":").append(field);
            }

            document.addPair(FIELD_RECORD, sb.toString(), true);


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }

        private void indexMacro(ErlMacro macro, IndexDocument document) {
            StringBuilder sb = new StringBuilder();
            sb.append(macro.getName());

            boolean isDocumented = false; // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(":").append("d");
            } else {
                sb.append(":").append("");
            }

            sb.append(":" + macro.getParams().size());

            sb.append(":").append(macro.getOffset() + ":" + macro.getEndOffset());

            for (String param : macro.getParams()) {
                sb.append(":").append(param);
            }

            sb.append(":").append(macro.getBody());

            document.addPair(FIELD_MACRO, sb.toString(), true);


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }
    } // end of inner class TreeAnalyzer

    private static FileObject preindexedDb;

    
    /** For testing only */
    public static void setPreindexedDb(FileObject preindexedDb) {
        ErlangIndexer.preindexedDb = preindexedDb;
    }
    
    public FileObject getPreindexedDb() {
        return null;
//        if (preindexedDb == null) {
//            File preindexed = InstalledFileLocator.getDefault().locate(
//                    "preindexed", "org.netbeans.modules.ruby", false); // NOI18N
//            if (preindexed == null || !preindexed.isDirectory()) {
//                throw new RuntimeException("Can't locate preindexed directory. Installation might be damaged"); // NOI18N
//            }
//            preindexedDb = FileUtil.toFileObject(preindexed);
//        }
//        return preindexedDb;
    }    
    
    
    /**
     * @Caoyuan moved from ruby's AstUtilities#getBaseDocument
     */
    public static BaseDocument getBaseDocument(FileObject fileObject, boolean forceOpen) {
        DataObject dobj;

        try {
            dobj = DataObject.find(fileObject);

            EditorCookie ec = dobj.getCookie(EditorCookie.class);

            if (ec == null) {
                throw new IOException("Can't open " + fileObject.getNameExt());
            }

            Document document;

            if (forceOpen) {
                document = ec.openDocument();
            } else {
                document = ec.getDocument();
            }

            if (document instanceof BaseDocument) {
                return (BaseDocument) document;
            }
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }

        return null;
    }

    /** @ReferenceOnly */
//    private static SqlIndexEngine sqlIndexEngine;
//    private static Index indexEngine;
//
//    private static boolean isSqlIndexAvaialble(Index lucenceIndex) {
//        if (sqlIndexEngine == null) {
//            sqlIndexEngine = SqlIndexEngine.create();
//            indexEngine = lucenceIndex;
//        }
//
//        return sqlIndexEngine != null;
//    }
}
