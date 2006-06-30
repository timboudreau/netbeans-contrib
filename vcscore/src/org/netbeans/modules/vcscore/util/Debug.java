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

package org.netbeans.modules.vcscore.util;

import java.io.*;

/** Debugging class.
 * @deprecated Use ErrorManager instead.
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class Debug implements Serializable {

    private String debClass;
    private boolean debEnabled;
    public static boolean debGeneralEnabled = false;

    static final long serialVersionUID =-2570656225846594430L;

    public Debug(String debClass, boolean debEnable){
        this.debClass = debClass;
        this.debEnabled = debEnable;
    }

    //-------------------------------------------
    public void deb(String prefix,String msg){
        if (!debGeneralEnabled || !debEnabled) return;
        System.err.println(prefix+": "+msg);
        System.err.flush();
    }

    //-------------------------------------------
    public void deb(String msg){
        deb(debClass,msg);
    }

    //-------------------------------------------
    public void err(String prefix, Exception exc, String msg){
        System.out.println(prefix+": ERROR:"+msg); // NOI18N
        if(exc!=null){
            System.out.print("-------------------------------------------"); // NOI18N
            System.out.println("-------------------------------------------"); // NOI18N
            exc.printStackTrace(System.out);
            System.out.print("-------------------------------------------"); // NOI18N
            System.out.println("-------------------------------------------"); // NOI18N
            System.out.flush();
        }
    }

    //-------------------------------------------
    public void err(Exception exc,String msg){
        err(debClass,exc,msg);
    }

    //-------------------------------------------
    public  void err(Exception exc){
        err(exc,""); // NOI18N
    }

    //-------------------------------------------
    public void err(String msg){
        err(null,msg);
    }

}


