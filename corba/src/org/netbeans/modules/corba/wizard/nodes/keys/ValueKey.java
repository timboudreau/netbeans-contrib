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
public class ValueKey extends NamedKey {

    String type;
    String length;
    boolean pub;

    /** Creates new Value */
    public ValueKey(int kind, String name, String type, String length, boolean pub) {
        super (kind, name);
        this.type = type;
        this.length =length;
        this.pub = pub;
    }

    public boolean isPublic () {
        return this.pub;
    }

    public void setPublic (boolean pub) {
        this.pub = pub;
    }
    
    public String getType () {
        return this.type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    public String getLength () {
        return this.length;
    }
    
    public void setLength (String length) {
        this.length = length;
    }
    
    public String toString () {
        return "Value " + this.getName();
    }
    
    public int hashCode () {
        return this.kind();
    }
    
    public boolean equals (Object o) {
        if (!(o instanceof ValueKey))
            return false;
        ValueKey vk = (ValueKey) o;
        if (!vk.getName().equals (this.getName()))
            return false;
        if (!vk.getType().equals (this.getType()))
            return false;
        return true;
    }

}
