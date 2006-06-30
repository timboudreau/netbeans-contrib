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
 * @author  tzezula
 * @version
 */
public class ValueFactoryKey extends NamedKey {

    private String params;

    /** Creates new ValueFactory */
    public ValueFactoryKey(int kind,String name,String params) {
        super (kind, name);
        this.params = params;
    }

    public String getParams () {
        return this.params;
    }

    public void setParams (String params) {
        this.params = params;
    }

    public String toString () {
        return "factory " + this.getName();
    }
    
    public int hashCode () {
        return this.kind();
    }
    
    public boolean equals (Object o) {
        if (!(o instanceof ValueFactoryKey))
            return false;
        ValueFactoryKey vk = (ValueFactoryKey) o;
        return (vk.getName().equals(this.getName()));
    }

}
