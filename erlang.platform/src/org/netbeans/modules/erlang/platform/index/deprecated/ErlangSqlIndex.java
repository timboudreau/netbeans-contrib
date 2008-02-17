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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.modules.erlang.editing.semantic.ErlFunction;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlangSqlIndex implements ErlangIndexProvider.I {
    private static SqlIndexEngine sqlIndexEngine;
    
    public ErlFunction getFunction(String module, String functionName, int arity) {
        if (! isIndexEngineAvaialble()) return null;
        Collection<ErlFunction> functions = sqlIndexEngine.searchFunctions(module);
        for (ErlFunction function : functions) {
            if (function.getName().equals(functionName) && function.getArity() == arity) {
                return function;
            }
        }
        return null;
    }
    
    public URL getModuleFileUrl(ErlangIndexProvider.Type type, String module) {
        if (! isIndexEngineAvaialble()) return null;
        String urlStr = sqlIndexEngine.searchModuleUrl(module);
        
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return url;
    }
    
    
    private static List<CompletionItem> completionItemsBuf = new ArrayList<CompletionItem>();
    public List<CompletionItem> getFunctionCompletionItems(String module) {
        completionItemsBuf.clear();
        if (! isIndexEngineAvaialble()) return completionItemsBuf;
        
        Collection<ErlFunction> functions = sqlIndexEngine.searchFunctions(module);
        for (ErlFunction function : functions) {
            String name = function.getName();
            completionItemsBuf.add(CompletionItem.create(
                    name,
                    null,
                    module,
                    CompletionItem.Type.METHOD,
                    1
                    ));
        }
        return completionItemsBuf;
    }
    
    public List<CompletionItem> getModuleCompletionItems(String modulePrefix) {
        completionItemsBuf.clear();
        if (! isIndexEngineAvaialble()) return completionItemsBuf;
        for (String module : sqlIndexEngine.searchModules()) {
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

    public List<CompletionItem> getRecordCompletionItems(String module) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<CompletionItem> getMacroCompletionItems(String module) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<CompletionItem> getRecordFieldsCompletionItems(String moduleName, String recordName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static boolean isIndexEngineAvaialble() {
        if (sqlIndexEngine == null) {
            sqlIndexEngine = SqlIndexEngine.create();
        }
        return sqlIndexEngine != null;
    }
    
}
