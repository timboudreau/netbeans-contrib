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

import java.util.EventListener;

import org.netbeans.api.vcs.commands.Command;

import org.netbeans.spi.vcs.VcsCommandsProvider;

/**
 * This listener is to be used to listen on commands preprocessing and execution.
 *
 * @author  Martin Entlicher
 */
public interface CommandProcessListener extends EventListener {

    /**
     * Get the commands provider. The listener gets events only from commands,
     * that are instances of ProvidedCommand and their provider equals to this
     * provider. If returns <code>null</code>, the listener gets events from all
     * commands.
     * @return The provider or <code>null</code>.
     */
    public VcsCommandsProvider getProvider();
    
    /**
     * Called when the command is just to be preprocessed.
     */
    public void commandPreprocessing(Command cmd);
    
    /**
     * Called when the preprocessing of the command finished.
     * @param cmd The command which was preprocessed.
     * @param status The status of preprocessing. If false, the command is not executed.
     */
    public void commandPreprocessed(Command cmd, boolean status);

    /**
     * This method is called when the command is just to be started.
     */
    public void commandStarting(CommandTaskInfo info);
    
    /**
     * This method is called when the command is done.
     */
    public void commandDone(CommandTaskInfo info);
}

