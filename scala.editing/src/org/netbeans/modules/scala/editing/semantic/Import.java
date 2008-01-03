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

import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.modules.languages.features.DatabaseDefinition;

/**
 *
 * @author Caoyuan Deng
 */
public class Import extends DatabaseDefinition {
    
    private Collection<Function> functions = new ArrayList<Function>();
    
    public Import(int offset, int endOffset) {
        super("import", null, offset, endOffset);
    }
    
    public void addFunction(Function function) {
        functions.add(function);
    }
    
    public Collection<Function> getFunctions() {
        return functions;
    }
    
    @Override
    public String toString() {
        return "Import " + functions.toString();
    }
}



