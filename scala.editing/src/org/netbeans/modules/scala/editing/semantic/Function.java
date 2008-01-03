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
package org.netbeans.modules.scala.editing.semantic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.netbeans.modules.languages.features.DatabaseDefinition;

/**
 *
 * @author Jan Jancura
 * @author Caoyuan Deng
 */
public class Function extends DatabaseDefinition {
    
    private int line;
    private String moduleName;
    private int arity;
    private Map<String, String> params;
    private Set<String> argumentsOpts; // use Set here to avoid reduntant.
    
    /** For Built-In functions */
    public Function(String name, int arity) {
        super(name, "built-in", 0, 0);
        this.arity = arity;
    }
    
    public Function(String name, int offset, int endOffset, int arity) {
        super(name, null, offset, endOffset);
        this.arity = arity;
    }

    public Function(String name, int offset, int endOffset) {
        super(name, null, offset, endOffset);
    }

    
    public Function(String moduleName, String name, int offset, int endOffset, int arity) {
        this(name, offset, endOffset, arity);
	this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }
    
    public int getArity() {
        return arity;
    }
    
    public Map<String, String> getParams() {
        if (params == null) {
            return Collections.emptyMap();
        } else {
            return params;
        }
    }
    
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    
    
    public void addArgumentsOpt(String arguments) {
        if (argumentsOpts == null) {
            argumentsOpts = new HashSet<String>();
        }
        String[] tokens = arguments.split(",");
        StringBuilder normalizedArguments = new StringBuilder();
        for (int i = 0, n = tokens.length; i < n; i++) {
            String token = tokens[i];
            normalizedArguments.append(token.trim());
            if (i < n - 1) {
                normalizedArguments.append(", ");
            }
        }
        argumentsOpts.add(normalizedArguments.toString());
    }
    
    public String getIdentity() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("(");
        for (Entry<String, String> param : params.entrySet()) {
            sb.append(param.getKey()).append(" :");
            sb.append(param.getValue());
        }
        sb.append(")");
        return sb.toString();
    }
    
    public int getLine() {
        return line;
    }
    
    public String getFileName() {
        return null;
    }
    
    public String toString() {
        return "Function " + getName() + "/" + arity;
    }
}
