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
import org.openide.util.HelpCtx;

public class JndiFailedNode extends JndiLeafNode {

    /** Creates new JndiFailedNode */
    public JndiFailedNode(JndiKey key, CompositeName offset) throws javax.naming.InvalidNameException {
        super (key, offset);
        this.setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(JndiDisabledNode.DISABLED_CONTEXT_ICON));
    }
    
    /** Returns help context for the failed JNDI node,
     *  the node inside some JNDI context, which can not
     *  be accessed, e.g. for security reasons
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (JndiFailedNode.class.getName());
    }
}
