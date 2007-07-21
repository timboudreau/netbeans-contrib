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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan Lahoda
 */
/*package private*/ class AttributableImpl implements Attributable, Descriptionable {

    private Map<String, String> attributes;
    private String description;

    /*package private*/synchronized Map<String, String> getAttributes() {
        if (attributes == null)
            attributes = new HashMap<String, String>();

        return attributes;
    }
    
    /*package private*/synchronized void setDescription(String description) {
        this.description = description;
    }
   
    /** Creates a new instance of Option */
    /*package private*/ AttributableImpl() {
    }
    
    public synchronized boolean hasAttribute(String name) {
        if (attributes == null)
            return false;
        
        return attributes.get(name) != null;
    }
    
    public synchronized String getAttribute(String name) {
        if (attributes == null)
            return null;
        
        return attributes.get(name);
    }
    
    public synchronized String getDescription() {
        return description;
    }
    
}
