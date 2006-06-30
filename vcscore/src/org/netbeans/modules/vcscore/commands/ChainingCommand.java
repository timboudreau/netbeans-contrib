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

package org.netbeans.modules.vcscore.commands;

import org.netbeans.api.vcs.commands.Command;

/**
 * A command, that permits chaining of several command. This is used e.g. when
 * customizing a command with extra data for each file or when the corresponding
 * task can run only on one file at a time.
 *
 * @author  Martin Entlicher
 */
public interface ChainingCommand extends Command {

    /**
     * Get the next command, that should be procesed after this one.
     * @return The next command to be processed.
     */
    public Command getNextCommand();
    
    /**
     * Set the next command, that should be procesed after this one.
     * @param cmd The next command to be processed.
     */
    public void setNextCommand(Command cmd);
    
}
