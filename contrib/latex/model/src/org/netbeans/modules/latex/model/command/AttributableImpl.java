/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Jan Lahoda
 */
/*package private*/ class AttributableImpl implements Attributable, Descriptionable {
    
    private Map attributes;
    private String description;
    
    /*package private*/synchronized Map getAttributes() {
        if (attributes == null)
            attributes = new HashMap();
        
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
        
        return (String) attributes.get(name);
    }
    
    public synchronized String getDescription() {
        return description;
    }
    
}
