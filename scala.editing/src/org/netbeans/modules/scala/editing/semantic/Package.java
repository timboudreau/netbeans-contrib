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
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.languages.features.DatabaseDefinition;

/**
 *
 * @author Caoyuan Deng
 */
public class Package extends DatabaseDefinition {
    
    private List<String> paths;
    
    
    public Package(String name, int offset, int endOffset) {
        super(name, null, offset, endOffset);
    }
    
    public void addPath(String packageName) {
        if (paths == null) {
            paths = new ArrayList<String>();
        }
        paths.add(packageName);
    }
    
    public List<String> getPaths() {
        if (paths == null) {
            return Collections.<String>emptyList();
        }
        return paths;
    }
    
    @Override
    public String toString() {
        return "Package " + getName();
    }
}




