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
