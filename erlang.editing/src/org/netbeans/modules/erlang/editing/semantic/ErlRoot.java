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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;

/**
 * It's acutally a rootContext wrapper.
 *
 * @author Daniel Prusa
 * @author Caoyuan Deng
 */
public class ErlRoot {
    
    private ErlContext rootContext;
    private Map<ASTToken, ErlDefinition> usedTokenToDefinition = new WeakHashMap<ASTToken, ErlDefinition>();
    
    ErlRoot(ASTNode AstRoot) {
        if (AstRoot == null) {
	    throw new NullPointerException();
	}
	rootContext = new ErlContext(AstRoot.getOffset(), AstRoot.getEndOffset());
        rootContext.setName("S");
    }
    
    public ErlContext getRootContext() {
        return rootContext;
    }

    protected void registerUsage(ASTToken token, ErlDefinition definition) {
        definition._addUsage(token);
        usedTokenToDefinition.put(token, definition);
    }
    
    public Collection<ErlDefinition> getDefinitionsInScope(int offset) {
        ErlContext bestContext = rootContext.getBestContextAt(offset);
        Collection<ErlDefinition> scopeDefinitions = new ArrayList<ErlDefinition>();
        bestContext.collectDefinitionsInScopeTo(scopeDefinitions);
	return scopeDefinitions;
    }

    public Set<ASTToken> getOccurrentUsages(int offset) {
	ErlDefinition definition = getDefinitionOfTokenAt(offset);
	if (definition != null) {
	    return definition.getUsages();
	} else {
            return Collections.<ASTToken>emptySet();
	}
    }

    public ErlDefinition getDefinitionOfTokenAt(int offset) {
	for (Map.Entry<ASTToken, ErlDefinition> entry : usedTokenToDefinition.entrySet()) {
	    ASTToken token = entry.getKey();
            if (token.getOffset() <= offset && offset <= token.getEndOffset()) {
		return entry.getValue();
	    }
        }
        return null;
    }

    public ErlDefinition getDefinitionAt(int offset) {
	for (ErlDefinition definition : usedTokenToDefinition.values()) {
            if (definition.getOffset() <= offset && offset <= definition.getEndOffset()) {
		return definition;
	    }
        }
        return null;
    }
        
    public ErlDefinition getDefinition(ASTToken token) {
        return usedTokenToDefinition.get(token);
    }
    
    public ErlModule getModule() {
        return rootContext.getFirstDefinition(ErlModule.class);
    }
    
    public Collection<ErlInclude> getIncludes() {
        return rootContext.getDefinitions(ErlInclude.class);
    }

    public Collection<ErlExport> getExports() {
        return rootContext.getDefinitions(ErlExport.class);
    }

    public Collection<ErlRecord> getRecords() {
	return rootContext.getDefinitions(ErlRecord.class);
    }
    
    public Collection<ErlMacro> getMacros() {
	return rootContext.getDefinitions(ErlMacro.class);
    }

    public Collection<ErlFunction> getFunctions() {
        return rootContext.getDefinitions(ErlFunction.class);
    }
    

}