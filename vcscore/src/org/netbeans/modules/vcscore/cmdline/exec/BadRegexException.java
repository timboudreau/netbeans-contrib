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

package org.netbeans.modules.vcscore.cmdline.exec;

import java.util.regex.PatternSyntaxException;

import org.netbeans.modules.vcscore.util.*;

/** Malformed regular expression.
 *
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class BadRegexException extends Exception {

    private PatternSyntaxException e = null;

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
    public BadRegexException(String msg, PatternSyntaxException e){
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
