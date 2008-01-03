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
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.languages.features.DatabaseDefinition;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlMacro extends DatabaseDefinition {

    private static Set<String> preDefineds;

    private Collection<String> params = new ArrayList<String>();
    private String body;

    public ErlMacro(String name, int offset, int endOffset) {
        super(name, null, offset, endOffset);
    }

    public static ErlMacro getPreDefined(String name) {
        if (preDefineds == null) {
            preDefineds = new HashSet<String>();
            
            preDefineds.add("FILE");
            preDefineds.add("MODULE");
            preDefineds.add("LINE");
        }
        
        if (preDefineds.contains(name)) {
            return new ErlMacro(name, 0, 0);
        } else {
            return null;
        }
    }

    public void addParam(String param) {
        params.add(param);
    }

    public Collection<String> getParams() {
        return params;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String toString() {
        return "Define " + params.toString() + " " + body;
    }
}
