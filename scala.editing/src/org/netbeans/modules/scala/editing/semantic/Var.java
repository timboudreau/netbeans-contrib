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

import org.netbeans.modules.languages.features.DatabaseDefinition;

/**
 *
 * @author Jan Jancura
 */
public class Var extends DatabaseDefinition {

    public static enum Scope {
        GLOBAL,
        LOCAL,
        PARAMETER
    }
    
    private Scope scope;
    
    private boolean val;
    
    private Type scalaType;
    
    public Var (String name, int offset, int endOffset, Scope contextType) {
        super(name, null, offset, endOffset);
        this.scope = contextType;
    }


    public Scope getContextType () {
        return scope;
    }
    
    public void setVal(boolean val) {
        this.val = val;
    }
    
    public boolean isVal() {
        return val;
    }
    
    public void setScalaType(Type type) {
        this.scalaType = type;
    }
    
    public Type getScalaType() {
        return scalaType;
    }
    
    @Override
    public String toString () {
        switch (scope) {
            case LOCAL:
                return "Local variable " + getName() + " : " + getScalaType();
            case GLOBAL:
                return "Global variable " + getName() + " : " + getScalaType();
            case PARAMETER:
                return "Parameter " + getName() + " : " + getScalaType();
        }
        return "?";
    }
}
