/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.ioranalyzer;

/**
 *
 * @author  tzezula
 * @version 
 */
public class TaggedComponent extends Object {

    public int tag;
    public byte[] component_data;
        
    public TaggedComponent() {
    }
        
    public TaggedComponent(int tag, byte[] component_data) {
        this.tag = tag;
        this.component_data = component_data;
    }

}
