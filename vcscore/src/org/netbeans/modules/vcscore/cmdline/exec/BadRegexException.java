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
    
    public String getLocalizedMessage() {
        if (e != null) {
            return org.openide.util.NbBundle.getMessage(BadRegexException.class, "MSG_BadRegexMessageInfo", e.getLocalizedMessage());
        } else {
            return org.openide.util.NbBundle.getMessage(BadRegexException.class, "MSG_BadRegexMessage");
        }
    }

    //-------------------------------------------
    public String toString(){
        return "BadRegexException "+e; // NOI18N
    }

}
