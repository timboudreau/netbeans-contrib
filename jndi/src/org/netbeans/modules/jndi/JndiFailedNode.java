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

package org.netbeans.modules.jndi;

/**
 *
 * @author  tzezula
 * @version 
 */
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.CompositeName;

public class JndiFailedNode extends JndiLeafNode {

    /** Creates new JndiFailedNode */
    public JndiFailedNode(Object key, Context ctx, CompositeName parentOffset, String name, String classname){
        super (key,ctx, parentOffset, name, classname);
        this.setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(JndiDisabledNode.DISABLED_CONTEXT_ICON));
    }
}