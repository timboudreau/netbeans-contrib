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

package org.netbeans.modules.corba;

import org.openide.nodes.Node;

/** Cookie for IDL Node.
*
* @author Karel Gardas
* @version May 21, 1999
*/
public interface IDLNodeCookie extends Node.Cookie {

    public void GenerateImpl (IDLDataObject ido);
}

/*
 * <<Log>>
 *  10   Gandalf   1.9         11/4/99  Karel Gardas    - update from CVS
 *  9    Gandalf   1.8         11/4/99  Karel Gardas    update from CVS
 *  8    Gandalf   1.7         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  7    Gandalf   1.6         10/1/99  Karel Gardas    updates from CVS
 *  6    Gandalf   1.5         8/3/99   Karel Gardas    
 *  5    Gandalf   1.4         7/10/99  Karel Gardas    
 *  4    Gandalf   1.3         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  3    Gandalf   1.2         5/28/99  Karel Gardas    
 *  2    Gandalf   1.1         5/28/99  Karel Gardas    
 *  1    Gandalf   1.0         5/22/99  Karel Gardas    
 * $
 */
