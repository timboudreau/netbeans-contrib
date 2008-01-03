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


