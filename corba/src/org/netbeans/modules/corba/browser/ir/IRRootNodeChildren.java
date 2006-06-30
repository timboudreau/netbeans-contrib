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

package org.netbeans.modules.corba.browser.ir;


import org.omg.CORBA.*;
import java.util.Vector;
import java.io.*;
import org.openide.nodes.*;
import org.openide.*;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.browser.ir.nodes.IRRepositoryNode;
import org.netbeans.modules.corba.browser.ir.nodes.IRFailedRepositoryNode;


import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class IRRootNodeChildren extends Children.Keys {

    private CORBASupportSettings css;

    //private ContextNode _context_node;
    private IRRootNode _root_node;

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    public IRRootNodeChildren () {
        super ();
    }

    public void addNotify () {
        if (DEBUG)
            System.out.println ("addNotify ()");
        createKeys ();
    }


    public void createKeys () {
        //ORB orb = ORB.init ();
        if (DEBUG)
            System.out.println ("createKeys ()");
        if (!getRootNode ().loaded ())
            getRootNode ().restore ();
	if (css == null)
	    css = (CORBASupportSettings) CORBASupportSettings.findObject (CORBASupportSettings.class, true);
        Vector repositories = css.getInterfaceRepositoryChildren ();
        setKeys (repositories);
    }


    public void setRootNode (IRRootNode node) {
        _root_node = node;
    }

    public IRRootNode getRootNode () {
        return _root_node;
    }

    public org.openide.nodes.Node[] createNodes (java.lang.Object key) {
        if (key != null){
            if (key instanceof Repository){
                Node[] nodes = new Node[1];
                if (!((Repository)key).failed()){
                    nodes[0] = new IRRepositoryNode (((Repository)key).getName (),
                                                     ((Repository)key).getRepository ());
                }
                else{
                    nodes[0] = new IRFailedRepositoryNode(((Repository)key).getName());
                }
                return nodes;
            }
        }
        return new Node[0];
    }

}


/*
 * $Log
 * $
 */
