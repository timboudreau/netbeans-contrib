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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.api.gsf.Index;
import org.netbeans.api.gsf.Indexer;
import org.netbeans.api.gsf.ParserFile;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.editor.BaseDocument;
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
    static final String FIELD_HEADERFILE_NAME = "headerfile";
    static final String FIELD_CASE_INSENSITIVE_HEADFILE_NAME = "headfile-ig"; //NOI18N
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
    
    public ErlangIndexer() {
    }

    public void updateIndex(Index index, ParserResult result) throws IOException {

        ParserFile file = result.getFile();
	long start = System.currentTimeMillis();
        if (file.isPlatform()) io.getOut().print("Indexing: ");

        ErlangLanguageParserResult r = (ErlangLanguageParserResult) result;
        ASTNode root = r.getRootNode();

        if (root == null) {
            return;
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
        TreeAnalyzer analyzer = new TreeAnalyzer(index, r);
        analyzer.analyze();
	if (file.isPlatform()) io.getOut().println((System.currentTimeMillis() - start) + "ms");
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

    /** Travel through parsed result (ErlRoot), and index meta-data */
    private static class TreeAnalyzer {

        private ParserFile file;
        private String url;
        private String imports;
        private ErlangLanguageParserResult result;
        private BaseDocument doc;
        private Index index;
        private ErlangIndexProvider.Type type = ErlangIndexProvider.Type.Header;

        private TreeAnalyzer(Index index, ErlangLanguageParserResult result) {
            this.index = index;
            this.result = result;
            this.file = result.getFile();

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

        public void analyze() throws IOException {
            // Delete old contents of this file - iff we're dealing with a user source file
            if (!file.isPlatform()) {
                Set<Map<String, String>> indexedList = Collections.emptySet();
                Set<Map<String, String>> notIndexedList = Collections.emptySet();
                Map<String, String> toDelete = new HashMap<String, String>();
                toDelete.put(FIELD_FILEURL, url);

                try {
                    index.gsfStore(indexedList, notIndexedList, toDelete);
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }
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
            Set<Map<String, String>> indexedList = new HashSet<Map<String, String>>();
            Set<Map<String, String>> notIndexedList = new HashSet<Map<String, String>>();

            // Add indexed info
            Map<String, String> indexed = new HashMap<String, String>();
            indexedList.add(indexed);

            Map<String, String> notIndexed = new HashMap<String, String>();
            notIndexedList.add(notIndexed);

            String attributes = type == ErlangIndexProvider.Type.Module ? "m" : "h";

            /** @TODO */
            //boolean isDocumented = isDocumented(node);
            //int documentSize = getDocumentSize(node);
            //
            //if (documentSize > 0) {
            //    attributes = attributes + "d(" + documentSize + ")";
            //}
            notIndexed.put(FIELD_ATTRS, attributes);

            switch (type) {
                case Module:
                    indexed.put(FIELD_MODULE_NAME, name);
                    indexed.put(FIELD_CASE_INSENSITIVE_MODULE_NAME, name.toLowerCase());
                    break;
                case Header:
                    indexed.put(FIELD_HEADERFILE_NAME, name);
                    indexed.put(FIELD_CASE_INSENSITIVE_HEADFILE_NAME, name.toLowerCase());
                    break;
            }

            // TODO:
            //addIncluded(indexed);
            //if (requires != null) {
            //    notIndexed.put(FIELD_INCLUDES, incudles);
            //}
            // Indexed so we can locate these documents when deleting/updating
            indexed.put(FIELD_FILEURL, url);

            for (ErlInclude include : includes) {
                indexInclude(include, indexedList, notIndexedList);
            }

            /** we only index exported functions */
            for (ErlExport export : exports) {
                for (ErlFunction function : export.getFunctions()) {
                    indexFunction(function, indexedList, notIndexedList);
                }
            }

            for (ErlRecord record : records) {
                indexRecord(record, indexedList, notIndexedList);
            }

            for (ErlMacro define : macros) {
                indexMacro(define, indexedList, notIndexedList);
            }

            try {
                Map<String, String> toDelete = Collections.emptyMap();
                index.gsfStore(indexedList, notIndexedList, toDelete);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        private void indexFunction(ErlFunction function, Set<Map<String, String>> indexedList, Set<Map<String, String>> notIndexedList) {
            Map<String, String> functionFields;
            functionFields = new HashMap<String, String>();
            indexedList.add(functionFields);

            StringBuilder sb = new StringBuilder();

            Map<String, String> aritys;
            aritys = new HashMap<String, String>();
            notIndexedList.add(aritys);

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

            functionFields.put(FIELD_FUNCTION, sb.toString());


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }

        private void indexInclude(ErlInclude include, Set<Map<String, String>> indexedList, Set<Map<String, String>> notIndexedList) {
            Map<String, String> includeFields;
            includeFields = new HashMap<String, String>();
            indexedList.add(includeFields);

            StringBuilder sb = new StringBuilder();

            Map<String, String> aritys;
            aritys = new HashMap<String, String>();
            notIndexedList.add(aritys);

            sb.append(include.getPath());

            boolean isDocumented = false; // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(":").append("d");
            } else {
                sb.append(":").append("");
            }

            sb.append(":" + include.isLib());

            sb.append(":").append(include.getOffset() + ":" + include.getEndOffset());

            includeFields.put(FIELD_INCLUDE, sb.toString());


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }

        private void indexRecord(ErlRecord record, Set<Map<String, String>> indexedList, Set<Map<String, String>> notIndexedList) {
            Map<String, String> recordFields;
            recordFields = new HashMap<String, String>();
            indexedList.add(recordFields);

            StringBuilder sb = new StringBuilder();

            Map<String, String> aritys;
            aritys = new HashMap<String, String>();
            notIndexedList.add(aritys);

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

            recordFields.put(FIELD_RECORD, sb.toString());


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }

        private void indexMacro(ErlMacro macro, Set<Map<String, String>> indexedList, Set<Map<String, String>> notIndexedList) {
            Map<String, String> macroFields;
            macroFields = new HashMap<String, String>();
            indexedList.add(macroFields);

            StringBuilder sb = new StringBuilder();

            Map<String, String> aritys;
            aritys = new HashMap<String, String>();
            notIndexedList.add(aritys);

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

            macroFields.put(FIELD_MACRO, sb.toString());


            // Storing a lowercase method name is kinda pointless in
            // Ruby because the convention is to use all lowercase characters
            // (using _ to separate words rather than camel case) so we're
            // bloating the database for very little practical use here...
            //ru.put(FIELD_CASE_INSENSITIVE_METHOD_NAME, name.toLowerCase());
        }
    } // end of inner class TreeAnalyzer

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
