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
public class UnionMemberKey extends AliasKey {

    private String label;
    private boolean defaultValue;

    /** Creates new UnionMemberKey */
    public UnionMemberKey(int kind, String name, String type, String length, String label) {
        super (kind, name, type, length);
        if (label != null) {
            this.label = label;
            this.defaultValue = false;
        }
        else
            this.defaultValue = true;
    }

    public String getLabel () {
        return this.label;
    }
    
    public void setLabel (String label) {
        this.label = label;
    }

    public boolean isDefaultValue () {
        return this.defaultValue;
    }
    
    public void setDefaultValue (boolean dv) {
        this.defaultValue = dv;
    }
  
}
