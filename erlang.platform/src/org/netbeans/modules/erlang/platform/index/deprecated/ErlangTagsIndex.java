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
package org.netbeans.modules.erlang.platform.index.deprecated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.modules.erlang.editing.semantic.ErlFunction;
import org.netbeans.modules.erlang.editing.semantic.ErlInclude;
import org.netbeans.modules.erlang.editing.semantic.ErlMacro;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider.I;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlangTagsIndex implements ErlangIndexProvider.I {
    private static Map<String, String> moduleToFileUrls;
    private static Map<String, Map<String, ErlFunction>> moduleToFunctions;
    
    /**
     * file pattern
     * c:/erl/doc/efficiency_guide/bench.erl,501
     */
    private static final String fileRegrex = "^((\\[|\\]|\\-|\\:|[0-9]|\\s|\\,)*)(\\S.*\\.(erl|hrl))\\,\\s*([0-9]+).*";
    private static final Pattern filePattern = Pattern.compile(fileRegrex);
    
    /**
     * form pattern
     * -define(RANGE_MAX29,951
     * run(41,1416
     * compiler_options(53,1895
     */
    private static final String formRegrex = "^((\\-define)|(\\w+))\\((\\w+)?.([0-9]+)\\,([0-9]+).*";
    private static final Pattern formPattern = Pattern.compile(formRegrex);

    public I get(FileObject fo) {
        return this;
    }

    
    public ErlFunction getFunction(String module, String functionName, int arity) {
        if (moduleToFunctions == null) {
            create();
        }
        
        Map<String, ErlFunction> functions = moduleToFunctions.get(module);
        if (functions != null) {
            return functions.get(functionName);
        }
        return null;
    }
    
    public ErlMacro getMacro(Collection<ErlInclude> includes, String macroName) {
        return null;
    }
    

    public URL getPersistentUrl(String module) {
        if (moduleToFunctions == null) {
            create();
        }

        String urlStr = moduleToFileUrls.get(module);
        
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return url;
    }
    
    private void create() {
        moduleToFunctions = new HashMap<String, Map<String, ErlFunction>>();
        moduleToFileUrls  = new HashMap<String, String>();
        
        String tagsFileUrl = "C:" + File.separator + "erl" + File.separator + "lib" + File.separator + "TAGS";
        File file = new File(tagsFileUrl);
        if (! file.exists()) {
            return;
        }
        
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if (is == null) {
            return;
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        String s = null;
        
        /**
         * !NOTICE
         * Reader such as BufferedRead won't throw EOFException, but InputStream will,
         * so use readLine() != null to control this while circle in case of Reader
         * instead of catching EOFException.
         * .
         */
        try {
            String module = "unknowm";
            while ((s = reader.readLine()) != null) {
                if (s.startsWith("\f")) {
                    continue;
                }
                Matcher matcher = formPattern.matcher(s);
                if (matcher.matches()) {
                    Map<String, ErlFunction> functions = moduleToFunctions.get(module);
                    if (functions == null) {
                        functions = new HashMap<String, ErlFunction>();
                        moduleToFunctions.put(module, functions);
                    }
                    
                    boolean isDefine = matcher.group(1).equals("-define");
                    
                    String name = isDefine ? matcher.group(4) : matcher.group(3);
                    String lineStr = matcher.group(5);
                    String offsetStr = matcher.group(6);
                    
                    int line = Integer.parseInt(lineStr);
                    int offset = Integer.parseInt(offsetStr);
                    
                    addModuleFunction(functions, offset, 0, module, name, 0);
                } else {
                    matcher = filePattern.matcher(s);
                    if (matcher.matches()) {
                        module = "unknown";
                        String fileUrl = matcher.group(3).trim();
                        int sepIdx = fileUrl.lastIndexOf("\\");
                        if (sepIdx != -1) {
                            fileUrl.replace('\\', File.separatorChar);
                        } else {
                            sepIdx = fileUrl.lastIndexOf("/");
                            if (sepIdx != -1) {
                                fileUrl.replace('/', File.separatorChar);
                            }
                        }
                        int dotIdx = fileUrl.lastIndexOf(".");
                        if (dotIdx != -1) {
                            module = fileUrl.substring(sepIdx + 1, dotIdx);
                            moduleToFileUrls.put(module, fileUrl);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                is.close();
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static void addModuleFunction(Map<String, ErlFunction> functions,
            int offset, int endOffset, String module, String name, int arity) {
        functions.put(name, new ErlFunction(name, offset, endOffset, arity));
    }
    
    private static List<CompletionItem> completionItemsBuf = new ArrayList<CompletionItem>();
    public List<CompletionItem> getFunctionCompletionItems(String module) {
        completionItemsBuf.clear();
        Map<String, ErlFunction> functions = moduleToFunctions.get(module);
        if (functions != null) {
            for (ErlFunction function : functions.values()) {
                completionItemsBuf.add(CompletionItem.create(
                        function.getName(),
                        null,
                        module,
                        CompletionItem.Type.METHOD,
                        1
                        ));
            }
        }
        return completionItemsBuf;
    }

    public List<CompletionItem> getRecordCompletionItems(String module) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<CompletionItem> getMacroCompletionItems(String module) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<CompletionItem> getRecordFieldsCompletionItems(String moduleName, String recordName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<CompletionItem> getModuleCompletionItems(String modulePrefix) {
        completionItemsBuf.clear();
        for (String module : moduleToFileUrls.keySet()) {
                completionItemsBuf.add(CompletionItem.create(
                        module,
                        null,
                        "Module",
                        CompletionItem.Type.CLASS,
                        3
                        ));
        }
        return completionItemsBuf;
    }
}
