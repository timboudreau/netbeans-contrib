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

package org.netbeans.api.vcs.commands;

/**
 * This interface represents the VCS check in command. This command will add
 * a new file revision into the version control repository. It's supposed,
 * that the processed files already have the corresponding repository representations
 * and new revisions will be created for them.
 * <p>
 * This command is not supposed to add new files into the repository. Use the
 * {@link AddCommand} for that purpose.
 *
 * @author  Martin Entlicher
 */
public interface CheckInCommand extends MessagingCommand {
    
}

