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

import org.openide.util.*;

import org.netbeans.modules.vcscore.cmdline.exec.NoRegexListener;
import org.netbeans.modules.vcscore.cmdline.exec.RegexListener;

/**
 *
 * @author  Martin Entlicher
 * @version 
 */
public interface VcsAdditionalCommand {

    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                                 NoRegexListener stdoutNRListener, NoRegexListener stderrNRListener,
                                 RegexListener stdoutListener, String dataRegex,
                                 RegexListener stderrListener, String errorRegex);
}
/*
 * Log
 *  4    Gandalf   1.3         12/14/99 Martin Entlicher Listeners added
 *  3    Gandalf   1.2         10/25/99 Pavel Buzek     
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         10/7/99  Martin Entlicher initial revision
 * $
 */
