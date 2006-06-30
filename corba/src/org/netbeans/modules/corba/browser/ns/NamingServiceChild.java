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

package org.netbeans.modules.corba.browser.ns;

import java.io.*;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;


import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class NamingServiceChild implements Serializable {

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    public String name;
    public String kind;
    public String url;
    public String ior;

    static final long serialVersionUID =-5051797421901475341L;
    public NamingServiceChild () {
    }


    //public Object writeReplace () {
    //   System.out.println ("serialization of " + this);
    //   return "NamingServiceChild";
    //}

    public NamingServiceChild (String n, String k, String u, String i) {
        if (DEBUG)
            System.out.println ("NamingServiceChild (" + n + ", " + k + ", " + u + ", " + i + ");");
        name = n;
        kind = k;
        url = u;
        ior = i;
    }

    public String getName () {
        return name;
    }

    public String getKind () {
        return kind;
    }

    public String getURL () {
        return url;
    }

    public String getIOR () {
        return ior;
    }

}


/*
 * $Log
 * $
 */
