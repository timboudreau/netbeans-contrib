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

package org.netbeans.modules.vcscore.cmdline.exec;

import org.apache.regexp.*;

import org.netbeans.modules.vcscore.util.*;

/** Malformed regular expression.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class BadRegexException extends Exception {
    private Debug D=new Debug("BadRegexException", false); // NOI18N

    private RESyntaxException e=null;

    //-------------------------------------------
    static final long serialVersionUID =7191929174721239680L;
    public BadRegexException(){
        super();
    }

    //-------------------------------------------
    public BadRegexException(String msg){
        super(msg);
    }

    //-------------------------------------------
    public BadRegexException(String msg, RESyntaxException e){
        super(msg);
        this.e=e;
    }

    //-------------------------------------------
    public String toString(){
        return "BadRegexException "+e; // NOI18N
    }

}

/*
 * Log
 *  6    Jaga      1.4.1.0     4/11/00  Petr Jiricka    New RegExp in 
 *       org.openide.util
 *  5    Gandalf   1.4         1/6/00   Martin Entlicher 
 *  4    Gandalf   1.3         11/27/99 Patrik Knakal   
 *  3    Gandalf   1.2         10/25/99 Pavel Buzek     
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */
