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

import java.util.EventListener;

/**
 * The listener to get the data output of a command obtained from a regular
 * expression parser usually applied on the standard text output of the command.
 * It can be used to obtain only specific data from the command output.
 * The command is expected to have some meaningfull regex expression preset.
 *
 * @author  Martin Entlicher
 */
public interface RegexOutputListener extends EventListener {

    /**
     * This method is called, with elements of the parsed data.
     * @param elements the elements of parsed data.
     */
    public void outputMatchedGroups(String[] elements);

}
