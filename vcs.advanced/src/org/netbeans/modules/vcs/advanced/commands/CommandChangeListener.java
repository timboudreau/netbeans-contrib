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

package org.netbeans.modules.vcs.advanced.commands;

import org.netbeans.modules.vcscore.commands.VcsCommand;
/**
 *
 * @author  Martin Entlicher
 */
public interface CommandChangeListener {

    /**
     * Called when the command is changed.
     */
    public void changed(VcsCommand cmd);

    /**
     * Called when new command is added.
     */
    public void added(VcsCommand cmd);

    /**
     * Called when the command is removed.
     */
    public void removed(VcsCommand cmd);
}
