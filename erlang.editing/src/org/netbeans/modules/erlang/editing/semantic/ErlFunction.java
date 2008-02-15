/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.erlang.editing.semantic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.languages.database.DatabaseDefinition;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlFunction extends DatabaseDefinition {
    
    private int line;
    private String moduleName;
    private int arity;
    private Set<String> argumentsOpts; // use Set here to avoid reduntant.
    
    private String fileName;
    
    /** For Built-In functions */
    public ErlFunction(String name, int arity) {
        super(name, "built-in", 0, 0);
        this.arity = arity;
    }
    
    public ErlFunction(String name, int offset, int endOffset, int arity) {
        super(name, null, offset, endOffset);
        this.arity = arity;
    }

    public ErlFunction(String moduleName, String name, int offset, int endOffset, int arity) {
        this(name, offset, endOffset, arity);
	this.moduleName = moduleName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return fileName;
    }

    public String getModuleName() {
        return moduleName;
    }
    
    public int getArity() {
        return arity;
    }
    
    public Set<String> getArgumentsOpts() {
        if (argumentsOpts == null) {
            return Collections.emptySet();
        } else {
            return argumentsOpts;
        }
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
        return getName() + "/" + arity;
    }
    
    public int getLine() {
        return line;
    }
    
    @Override
    public String toString() {
        return "Function " + getName() + "/" + arity;
    }
}
