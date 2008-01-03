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
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.modules.scala.editing.semantic.Function;
import org.netbeans.modules.scala.util.ServiceLoader;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaIndexProvider {
    private static I i;

    public enum Type {
        Module, 
	Header 
    }

    
    public static I getDefault() {
        if (i == null) {
            Iterator<I> itr = ServiceLoader.load(I.class).iterator();
            if (itr.hasNext()) {
                i = itr.next();
            }
            if (i == null) {
                i = new DefaultScalaIndex();
            }
        }
        return i;
    }
    
    public static interface I {
        Function getFunction(String moduleName, String functionName, int arity);
	
        URL getModuleFileUrl(Type type, String moduleName);

        List<CompletionItem> getModuleCompletionItems(String modulePrefix);
        
	List<CompletionItem> getFunctionCompletionItems(String moduleName);

        List<CompletionItem> getRecordCompletionItems(String moduleName);
                
	List<CompletionItem> getMacroCompletionItems(String moduleName);

        List<CompletionItem> getRecordFieldsCompletionItems(String moduleName, String recordName);
    }
}
