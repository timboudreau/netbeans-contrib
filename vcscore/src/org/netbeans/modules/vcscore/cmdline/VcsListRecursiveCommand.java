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

import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.cmdline.exec.NoRegexListener;
import org.netbeans.modules.vcscore.cmdline.exec.RegexListener;

/**
 *
 * @author  Martin Entlicher
 * @version 
 */
public abstract class VcsListRecursiveCommand extends Object {


    /**
     * This method is called when the content of the directory is to be listed recursively.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param filesByName return the files read from the directory content. Each element in
     *                    <code>VcsDirContainer</code> is supposed to be <code>Hashtable</code> object.
     *                    The keys in this Hashtable
     *                    are supposed to be file names, values are supposed to be an array
     *                    of String containing statuses.
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
    public abstract boolean listRecursively(Hashtable vars, String[] args, VcsDirContainer filesByName,
                                            NoRegexListener stdoutNRListener, NoRegexListener stderrNRListener,
                                            RegexListener stdoutListener, String dataRegex,
                                            RegexListener stderrListener, String errorRegex);

}
