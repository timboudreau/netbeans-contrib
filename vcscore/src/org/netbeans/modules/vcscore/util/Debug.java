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
public class Debug implements Serializable {

    private String debClass=null;
    private boolean debEnabled=true;
    private boolean debGeneralEnabled=true;

    //-------------------------------------------
    static final long serialVersionUID =-2570656225846594430L;
    public Debug(String debClass, boolean debEnabled){
        this.debClass=debClass;
        this.debEnabled=debEnabled;
    }

    //-------------------------------------------
    public void deb(String prefix,String msg){
        if(debEnabled && debGeneralEnabled){
            System.err.println(prefix+": "+msg);
            System.err.flush();
        }
    }

    //-------------------------------------------
    public void deb(String msg){
        deb(debClass,msg);
    }

    //-------------------------------------------
    public void err(String prefix, Exception exc, String msg){
        System.out.println(prefix+": Error: "+msg); // NOI18N
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

/*
 * Log
 *  6    Gandalf   1.5         1/15/00  Ian Formanek    NOI18N
 *  5    Gandalf   1.4         11/27/99 Patrik Knakal   
 *  4    Gandalf   1.3         10/25/99 Pavel Buzek     copyright and log
 *  3    Gandalf   1.2         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  2    Gandalf   1.1         10/13/99 Pavel Buzek     
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */
