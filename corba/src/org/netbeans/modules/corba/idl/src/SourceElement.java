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

package org.netbeans.modules.corba.idl.src;

import java.beans.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openide.nodes.Node;
import org.openide.util.Task;
/*
 * @author Karel Gardas
 */

public class SourceElement extends SimpleNode {

    static int STATUS_OK = 0;
    static int STATUS_ERROR = 1;
    static int STATUS_PARTIAL = 2;
    static int STATUS_NOT = 3;

    public SourceElement (int i) {
        super (i);
    }

    public SourceElement (IDLParser p, int i) {
        super (p, i);
    }


    public int getStatus () {
        return STATUS_NOT;
    }

    public Task prepare () {
        return null;
    }


}

/*
 * <<Log>>
 *  5    Gandalf   1.4         11/4/99  Karel Gardas    - update from CVS
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/5/99  Karel Gardas    
 *  2    Gandalf   1.1         8/3/99   Karel Gardas    
 *  1    Gandalf   1.0         7/10/99  Karel Gardas    initial revision
 * $
 */



