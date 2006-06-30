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

package org.netbeans.modules.corba.wizard.nodes.keys;

/**
 *
 * @author  root
 * @version
 */
public class InterfaceKey extends NamedKey {

    private String baseInterfaces;
    private boolean abst;

    /** Creates new InterfaceKey
     *  @param int kind
     *  @param String name
     *  @param String[] baseInterfaces, not null
     */
    public InterfaceKey(int kind, String name, String baseInterfaces) {
        this (kind, name, baseInterfaces, false);
    }

    public InterfaceKey(int kind, String name, String baseInterfaces, boolean abstr) {
        super (kind, name);
        this.baseInterfaces = baseInterfaces;
        this.abst = abstr;
    }
  
    public String getbaseInterfaces () {
        return this.baseInterfaces;
    }
    
    public void setBaseInterfaces (String baseInterfaces) {
        this.baseInterfaces = baseInterfaces;
    }
    
    public boolean isAbstract () {
        return this.abst;
    }
    
    public void setAbstract (boolean abst) {
        this.abst = abst;
    }
  
    public String toString () {
        return "InterfaceKey:" + name;  // No I18N
    }
  
}
