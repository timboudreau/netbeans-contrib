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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.erlang.platform.index;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.gsf.api.Index.SearchResult;
import org.netbeans.modules.gsf.api.Index.SearchScope;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.modules.erlang.editing.Erlang;
import org.netbeans.modules.erlang.editing.semantic.ErlMacro;
import org.netbeans.modules.erlang.editing.semantic.ErlFunction;
import org.netbeans.modules.erlang.editing.semantic.ErlInclude;
import org.netbeans.modules.erlang.editing.semantic.ErlRecord;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider;
import org.netbeans.modules.gsf.api.Index;
import org.netbeans.napi.gsfret.source.ClasspathInfo;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlangLuceneIndex implements ErlangIndexProvider.I {

    private static final Set<SearchScope> ALL_SCOPE = EnumSet.allOf(SearchScope.class);
    private Index index;

    /** For service load */
    public ErlangLuceneIndex() {
    }

    /** Creates a new instance of ErlangLuceneIndex */
    private ErlangLuceneIndex(Index index) {
        this.index = index;
    }

    public ErlangIndexProvider.I get(FileObject fo) {
        Index indexEngine = ClasspathInfo.create(fo).getClassIndex(Erlang.MIME_TYPE);
        return new ErlangLuceneIndex(indexEngine);
    }

    private boolean search(String key, String name, NameKind kind, Set<SearchResult> result) {
        try {
            assert index != null : "index is null, are you using ErlangIndexPrivider.getDefault() ?, if so, use ErlangIndexPrivider.getDefault().get(fo) instead!";
            index.search(key, name, kind, ALL_SCOPE, result, null);
            return true;
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
            return false;
        }
    }

    private boolean search(String key, String name, NameKind kind, Set<SearchResult> result,
            Set<SearchScope> scope) {
        try {
            assert index != null : "index is null, are you using ErlangIndexPrivider.getDefault() ?, if so, use ErlangIndexPrivider.getDefault().get(fo) instead!";
            index.search(key, name, kind, scope, result, null);
            return true;
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);

            return false;
        }
    }

    private Set<SearchResult> searchFile(String name, NameKind kind) {
        final Set<SearchResult> result = new HashSet<SearchResult>();

        String field = ErlangIndexer.FIELD_FQN_NAME;

        switch (kind) {
            // No point in doing case insensitive searches on method names because
            // method names in Ruby are always case insensitive anyway
            //            case CASE_INSENSITIVE_PREFIX:
            //            case CASE_INSENSITIVE_REGEXP:
            //                field = RubyIndexer.FIELD_CASE_INSENSITIVE_METHOD_NAME;
            //                break;
            case EXACT_NAME:
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            //kind = NameKind.PREFIX;
            case PREFIX:
            case CAMEL_CASE:
            case REGEXP:
            case CASE_INSENSITIVE_PREFIX:
            case CASE_INSENSITIVE_REGEXP:
                break;
            default:
                throw new UnsupportedOperationException(kind.toString());
        }

        search(field, name, kind, result);

        return result;
    }

    public ErlFunction getFunction(String fqn, String functionName, int arity) {
        for (SearchResult map : searchFile(fqn, NameKind.EXACT_NAME)) {
            String[] signatures = map.getValues(ErlangIndexer.FIELD_FUNCTION);
            if (signatures == null) {
                continue;
            }
            String urlStr = map.getPersistentUrl();
            for (String signature : signatures) {
                ErlFunction function = createFuntion(urlStr, signature);
                if (function.getName().equals(functionName) && function.getArity() == arity) {
                    return function;
                }
            }
        }
        return null;
    }

    public ErlMacro getMacro(Collection<ErlInclude> includes, String macroName) {
         /** search including headfiles */       
        for (ErlInclude include : includes) {
            for (SearchResult map : searchFile(include.getPath(), NameKind.EXACT_NAME)) {
                String[] signatures = map.getValues(ErlangIndexer.FIELD_MACRO);
                if (signatures == null) {
                    continue;
                }
                String urlStr = map.getPersistentUrl();
                for (String signature : signatures) {
                    ErlMacro define = createMacro(urlStr, signature);
                    if (define.getName().equals(macroName)) {
                        return define;
                    }
                }
            }
        }
        return null;
    }
    private static Map<String, URL> moduleToUrlBuf = new HashMap<String, URL>();

    public URL getPersistentUrl(String fqn) {
        URL url = moduleToUrlBuf.get(fqn);
        if (url != null) {
            return url;
        }
        for (SearchResult map : searchFile(fqn, NameKind.EXACT_NAME)) {
            String urlStr = map.getPersistentUrl();
            if (urlStr == null) {
                continue;
            }
            try {
                url = new URL(urlStr);
                moduleToUrlBuf.put(fqn, url);
                break;
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        return url;
    }
    private static List<ErlFunction> functionsBuf = new ArrayList<ErlFunction>();

    private List<ErlFunction> getFunctions(String fqn) {
        functionsBuf.clear();
        for (SearchResult map : searchFile(fqn, NameKind.EXACT_NAME)) {
            String[] signatures = map.getValues(ErlangIndexer.FIELD_FUNCTION);
            if (signatures == null) {
                continue;
            }
            String urlStr = map.getPersistentUrl();
            for (String signature : signatures) {
                ErlFunction function = createFuntion(urlStr, signature);
                functionsBuf.add(function);
            }
        }
        return functionsBuf;
    }

    private static List<ErlInclude> includesBuf = new ArrayList<ErlInclude>();
    public List<ErlInclude> getIncludes(String fqn) {
        includesBuf.clear();
        for (SearchResult map : searchFile(fqn, NameKind.EXACT_NAME)) {
            String[] signatures = map.getValues(ErlangIndexer.FIELD_INCLUDE);
            if (signatures == null) {
                continue;
            }
            for (String signature : signatures) {
                ErlInclude include = createInclude(signature);
                includesBuf.add(include);
            }
        }
        return includesBuf;
    }
    
    private static List<ErlRecord> recordsBuf = new ArrayList<ErlRecord>();
    private List<ErlRecord> getRecords(String fqn) {
        recordsBuf.clear();
        /** search my module first */
        for (SearchResult map : searchFile(fqn, NameKind.EXACT_NAME)) {
            String[] signatures = map.getValues(ErlangIndexer.FIELD_RECORD);
            if (signatures == null) {
                continue;
            }
            String urlStr = map.getPersistentUrl();
            for (String signature : signatures) {
                ErlRecord record = createRecord(urlStr, signature);
                recordsBuf.add(record);
            }
        }
        /** search including headfiles */
        for (ErlInclude include : getIncludes(fqn)) {
            for (SearchResult map : searchFile(include.getPath(), NameKind.EXACT_NAME)) {
                String[] signatures = map.getValues(ErlangIndexer.FIELD_RECORD);
                if (signatures == null) {
                    continue;
                }
                String urlStr = map.getPersistentUrl();
                for (String signature : signatures) {
                    ErlRecord record = createRecord(urlStr, signature);
                    recordsBuf.add(record);
                }
            }
        }
        return recordsBuf;
    }
    private static List<ErlMacro> definesBuf = new ArrayList<ErlMacro>();

    private List<ErlMacro> getMacros(String fqn) {
        definesBuf.clear();
        /** search my module first */
        for (SearchResult map : searchFile(fqn, NameKind.EXACT_NAME)) {
            String[] signatures = map.getValues(ErlangIndexer.FIELD_MACRO);
            if (signatures == null) {
                continue;
            }
            String urlStr = map.getPersistentUrl();
            for (String signature : signatures) {
                ErlMacro define = createMacro(urlStr, signature);
                definesBuf.add(define);
            }
        }

        /** search including headfiles */
        for (ErlInclude include : getIncludes(fqn)) {
            for (SearchResult map : searchFile(include.getPath(), NameKind.EXACT_NAME)) {
                String[] signatures = map.getValues(ErlangIndexer.FIELD_MACRO);
                if (signatures == null) {
                    continue;
                }
                String urlStr = map.getPersistentUrl();
                for (String signature : signatures) {
                    ErlMacro define = createMacro(urlStr, signature);
                    definesBuf.add(define);
                }
            }
        }

        return definesBuf;
    }
    private static List<CompletionItem> completionItemsBuf = new ArrayList<CompletionItem>();

    public List<CompletionItem> getModuleCompletionItems(String fqnPrefix) {
        completionItemsBuf.clear();
        if (fqnPrefix.endsWith("'")) {
            /** remove last "'" of no-complete quoted atom */
            fqnPrefix = fqnPrefix.substring(0, fqnPrefix.length() - 1);
        }
        for (SearchResult map : searchFile(fqnPrefix, NameKind.PREFIX)) {
            String[] fqns = map.getValues(ErlangIndexer.FIELD_FQN_NAME);
            if (fqns == null) {
                continue;
            }
            for (String fqn : fqns) {
                completionItemsBuf.add(CompletionItem.create(fqn, null, "Module", CompletionItem.Type.CLASS, 3));
            }
        }
        return completionItemsBuf;
    }

    public List<CompletionItem> getFunctionCompletionItems(String fqn) {
        completionItemsBuf.clear();
        for (ErlFunction function : getFunctions(fqn)) {
            Collection<String> argumentsOpts = function.getArgumentsOpts();
            if (argumentsOpts.size() == 0) {
                completionItemsBuf.add(CompletionItem.create(function.getName() + "()", "/" + function.getArity(), "", CompletionItem.Type.METHOD, 1));
            } else {
                for (String argumentsOpt : argumentsOpts) {
                    completionItemsBuf.add(CompletionItem.create(function.getName() + "(" + argumentsOpt + ")", "/" + function.getArity(), "", CompletionItem.Type.METHOD, 1));
                }
            }
        }
        return completionItemsBuf;
    }

    public List<CompletionItem> getRecordCompletionItems(String fqn) {
        completionItemsBuf.clear();
        for (ErlRecord record : getRecords(fqn)) {
            completionItemsBuf.add(CompletionItem.create(record.getName(), "record", "", CompletionItem.Type.CONSTANT, 1));
        }
        return completionItemsBuf;
    }

    public List<CompletionItem> getMacroCompletionItems(String fqn) {
        completionItemsBuf.clear();
        for (ErlMacro macro : getMacros(fqn)) {
            completionItemsBuf.add(CompletionItem.create(macro.getName(), macro.getBody(), "", CompletionItem.Type.CONSTANT, 1));
        }
        return completionItemsBuf;
    }

    public List<CompletionItem> getRecordFieldsCompletionItems(String fqn, String recordName) {
        completionItemsBuf.clear();
        ErlRecord foundRecord = null;
        for (ErlRecord record : getRecords(fqn)) {
            if (record.getName().equals(recordName)) {
                foundRecord = record;
                break;
            }
        }
        if (foundRecord != null) {
            for (String fieldName : foundRecord.getFields()) {
                completionItemsBuf.add(CompletionItem.create(fieldName, "", foundRecord.getName(), CompletionItem.Type.CONSTANT, 1));
            }
        }
        return completionItemsBuf;
    }

    private ErlFunction createFuntion(String urlStr, String signature) {
        String[] groups = signature.split(";");
        String name = groups[0];
        int arity = 0;
        int offset = 0;
        int endOffset = 0;
        if (groups.length >= 5) {
            arity = Integer.parseInt(groups[2]);
            offset = Integer.parseInt(groups[3]);
            endOffset = Integer.parseInt(groups[4]);
        }
        ErlFunction function = new ErlFunction(name, offset, endOffset, arity);
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        function.setSourceFileUrl(url);
        for (int i = 5; i < groups.length; i++) {
            function.addArgumentsOpt(groups[i]);
        }
        return function;
    }

    private ErlInclude createInclude(String signature) {
        String[] groups = signature.split(";");
        String path = groups[0];
        boolean isLib = false;
        int offset = 0;
        int endOffset = 0;
        if (groups.length >= 5) {
            isLib = Boolean.parseBoolean(groups[2]);
            offset = Integer.parseInt(groups[3]);
            endOffset = Integer.parseInt(groups[4]);
        }
        ErlInclude include = new ErlInclude(offset, endOffset);
        URL url = getPersistentUrl(path);
        include.setSourceFileUrl(url);
        include.setPath(path);
        return include;
    }

    private ErlRecord createRecord(String urlStr, String signature) {
        String[] groups = signature.split(";");
        String name = groups[0];
        int arity = 0;
        int offset = 0;
        int endOffset = 0;
        if (groups.length >= 5) {
            arity = Integer.parseInt(groups[2]);
            offset = Integer.parseInt(groups[3]);
            endOffset = Integer.parseInt(groups[4]);
        }
        ErlRecord record = new ErlRecord(name, offset, endOffset);
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        record.setSourceFileUrl(url);
        for (int i = 5; i < groups.length; i++) {
            record.addField(groups[i]);
        }
        return record;
    }

    private ErlMacro createMacro(String urlStr, String signature) {
        String[] groups = signature.split(";");
        String name = groups[0];
        int arity = 0;
        int offset = 0;
        int endOffset = 0;
        if (groups.length >= 5) {
            arity = Integer.parseInt(groups[2]);
            offset = Integer.parseInt(groups[3]);
            endOffset = Integer.parseInt(groups[4]);
        }
        ErlMacro macro = new ErlMacro(name, offset, endOffset);
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        macro.setSourceFileUrl(url);
        for (int i = 5; i < groups.length - 1; i++) {
            macro.addParam(groups[i]);
        }
        macro.setBody(groups[groups.length - 1]);
        return macro;
    }
}
