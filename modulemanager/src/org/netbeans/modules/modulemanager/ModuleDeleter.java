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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.modulemanager;

import java.io.IOException;
import org.openide.modules.ModuleInfo;

/** <code>ModuleDeleter</code> deletes module's files from installation if possible.
 * Checks if all information about files are known and deletes file from disk.
 * This interface is implemented in <code>Autoupdate</code> module.
 *
 * @author Jirka Rechtacek (jrechtacek@netbeans.org)
 */
public interface ModuleDeleter {

    /** Are all information about files of the given module known.
     *
     * @param module
     * @return true if info is available
     */
    public boolean canDelete (ModuleInfo module);
    
    /** Deletes all module's file from installation.
     * 
     * @param module 
     * @throws java.io.IOException 
     */
    public void delete (ModuleInfo... modules) throws IOException;
    
}
