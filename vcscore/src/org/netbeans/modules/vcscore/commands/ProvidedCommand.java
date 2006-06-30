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

import org.netbeans.spi.vcs.VcsCommandsProvider;

/**
 * This class represents a command which knows about it's VcsCommandsProvider.
 * Either Command or CommandTask can implement this interface so that it can be
 * found where did they come from.
 *
 * @author  Martin Entlicher
 */
public interface ProvidedCommand {

    /**
     * Get the VCS commands provider, that provided this Command or CommandTask.
     */
    public VcsCommandsProvider getProvider();

    /**
     * Set the VCS commands provider, that provided this Command or CommandTask.
     * This method should be called just by the implementator.
     */
    public void setProvider(VcsCommandsProvider provider);
    
}
