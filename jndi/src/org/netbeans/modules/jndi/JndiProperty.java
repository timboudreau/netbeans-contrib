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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jndi;

import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.jndi.utils.JndiPropertyMutator;


/** This class represents Property of JndiNodeObject
 *
 *  @author Tomas Zezula
 */
public final class JndiProperty extends PropertySupport{

    /** Value of property */
    String value;
    /** Mutator*/
    JndiPropertyMutator mutator;

    /** Constructor
     *  @param name name of property
     *  @param type class of value
     *  @param pname displayed name of property
     *  @param pvalue value of property
     */
    public JndiProperty(String name, Class type, String pName, Object pvalue){
        this (name,type,pName,null,pvalue);
    }

    /** Constructor
     *  @param name name of property
     *  @param type class of value
     *  @param pname displayed name of property
     *  @param pvalue value of property
     */
    public JndiProperty(String name, Class type, String pName, String shortDescription, Object pvalue){
        this (name,type,pName,null,pvalue,null,false);
    }

    /** Constructor
     *  @param name name of property
     *  @param type class of value
     *  @param pname displayed name of property
     *  @param pvalue value of property
      * @patam mutator mutator
     */  
    public JndiProperty(String name, Class type, String pName, String shortDescription, Object pvalue,JndiPropertyMutator mutator, boolean editable){
        super (name, type, pName,  shortDescription, true, editable);
        this.value=(String)pvalue;
        this.mutator = mutator;
    }

    /** Returns value of property
     *  @return Object value of this property
     */
    public Object getValue(){
        return this.value;
    }

    /** Sets the value of property
     *  @param Object value
     */
    public void setValue (Object value){
        if (this.mutator != null){
            if (this.mutator.changeJndiPropertyValue(this.getName(),value))
                this.value = (String) value;
        }
    }
}



/*
 * <<Log>>
 *  7    Gandalf   1.6         1/14/00  Tomas Zezula    
 *  6    Gandalf   1.5         12/17/99 Tomas Zezula    
 *  5    Gandalf   1.4         12/15/99 Tomas Zezula    
 *  4    Gandalf   1.3         12/15/99 Tomas Zezula    
 *  3    Gandalf   1.2         11/5/99  Tomas Zezula    
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         7/9/99   Ales Novak      
 * $
 */
