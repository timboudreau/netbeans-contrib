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

package org.netbeans.modules.vcscore.commands;

/**
 * The listener to get the output of a command line by line.
 * @deprecated Use {@link TextOutputListener} or {@link TextErrorListener} instead.
 *
 * @author  Martin Entlicher
 */
public interface CommandOutputListener extends TextErrorListener {
    
    /**
     * This method is called, with a line of the output data.
     * @param line one line of output data.
     */
    //public void outputLine(String line);

}

