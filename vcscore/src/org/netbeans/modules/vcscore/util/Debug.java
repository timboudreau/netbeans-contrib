/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import java.io.*;

/** Debugging class.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class Debug //implements Serializable 
{

    private String debClass=null;
    private boolean debEnabled;
    public static boolean debGeneralEnabled=false;

    static final long serialVersionUID = -2570656225846594430L;
   
    //-------------------------------------------
//    static final long serialVersionUID =-2570656225846594430L;
    public Debug(String debClass, boolean debEnable){
        this.debClass=debClass;
        debEnabled=debEnable;
    }

    //-------------------------------------------
    public void deb(String prefix,String msg){
        if (!debGeneralEnabled) return;
        if (!debEnabled) return;
        System.err.println(prefix+": "+msg);
        System.err.flush();
    }

    //-------------------------------------------
    public void deb(String msg){
        deb(debClass,msg);
    }

    //-------------------------------------------
    public void err(String prefix, Exception exc, String msg){
        System.out.println(prefix+":ERR:"+msg); // NOI18N
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


