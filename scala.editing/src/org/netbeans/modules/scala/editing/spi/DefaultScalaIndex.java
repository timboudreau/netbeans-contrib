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
package org.netbeans.modules.scala.editing.spi;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.modules.scala.editing.semantic.Function;

/**
 * DefaultScalaIndex just return null results.
 * 
 * @author Caoyuan Deng
 */
public class DefaultScalaIndex implements ScalaIndexProvider.I {
    
    public Function getFunction(String moduleName, String functionName, int arity) {
        return null;
    }
    
    public URL getModuleFileUrl(ScalaIndexProvider.Type type, String moduleName) {
        return null;
    }

    public List<CompletionItem> getModuleCompletionItems(String modulePrefix) {
        return Collections.<CompletionItem>emptyList();
    }
    
    public List<CompletionItem> getFunctionCompletionItems(String moduleName) {
        return Collections.<CompletionItem>emptyList();
    }

    public List<CompletionItem> getRecordCompletionItems(String moduleName) {
        return Collections.<CompletionItem>emptyList();
    }
     
    public List<CompletionItem> getMacroCompletionItems(String moduleName) {
        return Collections.<CompletionItem>emptyList();
    }

    public List<CompletionItem> getRecordFieldsCompletionItems(String moduleName, String recordName) {
        return Collections.<CompletionItem>emptyList();
    }

}