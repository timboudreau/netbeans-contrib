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

package org.netbeans.modules.vcscore.cmdline;

import java.util.*;

import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

/**
 * An abstract list command, that every class has to implement to provide a listing information.
 *
 * @author  Martin Entlicher
 */
public abstract class VcsListCommand extends Object {

    /**
     * Makes a single String from the array of Strings.
     */
    protected String array2string(String[] sa){
        StringBuffer sb=new StringBuffer(255);
        if (sa != null)
            for(int i=0;i<sa.length;i++){
                sb.append(sa[i]+" "); // NOI18N
            }
        return new String(sb);
    }

    /**
     * This method is called when the content of the directory is to be listed.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param filesByName return the files read from the given directory, or the error
     *                    description when an error occures. The keys in this Hashtable
     *                    are supposed to be file names, values are supposed to be an array
     *                    of String containing statuses.
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                           satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                           satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     */
    public abstract boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                                 CommandOutputListener stdoutListener,
                                 CommandOutputListener stderrListener,
                                 CommandDataOutputListener stdoutDataListener, String dataRegex,
                                 CommandDataOutputListener stderrDataListener, String errorRegex);


}
/*
 * Log
 *  6    Jaga      1.4.1.0     3/8/00   Martin Entlicher 
 *  5    Gandalf   1.4         1/6/00   Martin Entlicher 
 *  4    Gandalf   1.3         12/14/99 Martin Entlicher Listeners added
 *  3    Gandalf   1.2         10/25/99 Pavel Buzek     
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */
