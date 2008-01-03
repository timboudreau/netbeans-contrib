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

package org.netbeans.modules.scala.editing.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.languages.features.DatabaseDefinition;
import org.netbeans.modules.languages.features.DatabaseItem;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaContext extends DatabaseItem {
	
    private String name;
    private ScalaContext parent;
    private List<ScalaContext> contexts;
    private List<DatabaseDefinition> definitions;
	    
    ScalaContext(int offset, int endOffset) {
        super(offset, endOffset);
    }

    private void setParent(ScalaContext parent) {
        this.parent = parent;
    }
    
    protected ScalaContext getParent() {
        return parent;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public List<ScalaContext> getContexts() {
        if (contexts == null) {
            return Collections.<ScalaContext>emptyList();
        }
        return contexts;
    }
    
    public void addContext(ScalaContext context) {
        if (contexts == null) {
            contexts = new ArrayList<ScalaContext>();
	}
        context.setParent(this);
	contexts.add(context);
    }

    public void addDefinition(DatabaseDefinition definition) {
        if (definitions == null) {
            definitions = new ArrayList<DatabaseDefinition>();
	} 
        definitions.add(definition);
    }

    public ScalaContext getBestContextAt(int offset) {
        ScalaContext result = null;
        if (contexts != null) {
            /** search children first */
            for (ScalaContext context : contexts) {
                if (context.contains(offset)) {
                    result = context.getBestContextAt(offset);
		    break;
		}
	    }  
	}
	if (result != null) {
            return result;
	} else {
            if (this.contains(offset)) {
                return this;
	    } else {
                /* we should return null here, since it may under a parent context's call, 
		 * we shall tell the parent there is none in this and children of this
		 */
                return null; 
	    } 
	}
    }

    private boolean contains(int offset) {
        return offset >= getOffset() && offset < getEndOffset();
    }


    protected <T extends DatabaseDefinition> T getFirstDefinition(Class<T> type) {
        if (definitions == null) return null;
        for (DatabaseDefinition definition : definitions) {
            if (type.isInstance(definition)) {
                return (T)definition;
            }
        }
        return null;
    }
    
    protected <T extends DatabaseDefinition> Collection<T> getDefinitions(Class<T> type) {
        if (definitions == null) return Collections.<T>emptyList();
        Collection<T> result = new ArrayList<T>();
        for (DatabaseDefinition definition: definitions) {
            if (type.isInstance(definition)) {
                result.add((T)definition);
            }
        }
        return result;
    }
    
    protected void collectDefinitionsInScopeTo(Collection<DatabaseDefinition> scopeDefinitions) {
        if (definitions != null) {
            scopeDefinitions.addAll(definitions);
        } 
	if (parent != null) {
	    parent.collectDefinitionsInScopeTo(scopeDefinitions);
	}
    }
    
    Function getFunctionInScope(String name, int arity) {
        return getFunctionInScope(null, name, arity);
    }
    
    Function getFunctionInScope(String moduleName, String name, int arity) {
        Function result = null;
	if (definitions != null) {
	    for (DatabaseDefinition definition : definitions) {
                if (definition instanceof Function &&
		        name.equals(((Function) definition).getName()) &&
                        arity == ((Function) definition).getArity()) {
		    if (moduleName == null) {
                        result = (Function) definition;
			break;	        
                    } else {
                        if (moduleName.equals(((Function) definition).getModuleName())) {
                            result = (Function) definition;
			    break;	        
                        }
                    }
		}
	    }
	}
	if (result != null) {
            return result;
	} else {
            if (parent != null) {
                return parent.getFunctionInScope(moduleName, name, arity);
	    } else {
                return null;
	    }
	} 
    }
    
    Var getVariableInScope(String name) {
        return getDefinitionInScopeByName(Var.class, name);
    }
    
    ErlRecord getRecordInScope(String name) {
        return getDefinitionInScopeByName(ErlRecord.class, name);
    } 
    
    ErlMacro getMacroInScope(String name) {
	return getDefinitionInScopeByName(ErlMacro.class, name);
    }    

    public <T extends DatabaseDefinition> T getDefinitionInScopeByName(Class<T> type, String name) {
        T result = null;
	if (definitions != null) {
	    for (DatabaseDefinition definition : definitions) {
                if (type.isInstance(definition) && name.equals(definition.getName())) {
                    result = (T) definition;
		    break;
	        }
	    }
	}
	if (result != null) {
            return result;
	} else {
            if (parent != null) {
                return parent.getDefinitionInScopeByName(type, name);
	    } else {
                return null;
	    }
	} 
    }
    
}


