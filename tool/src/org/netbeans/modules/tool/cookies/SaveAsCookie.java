/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.tool.cookies;

import org.openide.nodes.Node;


/** 
 * The cookie for the save as operation.
 *
 */
public interface SaveAsCookie
             extends Node.Cookie
{
    /** 
     * Invoke the save as operation.
     * 
     * @throws IOException if the object could not be saved.
     */
    public void saveAs(  )
            throws java.io.IOException;
}
