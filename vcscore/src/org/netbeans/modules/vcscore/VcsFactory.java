/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore;

import java.util.Hashtable;
import java.util.Collection;

import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;

/**
 *
 * @author  Pavel Buzek, Martin Entlicher
 */

public interface VcsFactory {

    //public VcsAdvancedCustomizer getVcsAdvancedCustomizer ();

    public FileStatusProvider getFileStatusProvider();

    /**
     * Get the VCS directory reader.
     *
    public VcsCommandExecutor getVcsDirReader (DirReaderListener listener, String path);

    /**
     * Get the VCS directory reader that reads the whole directory structure.
     *
    public VcsCommandExecutor getVcsDirReaderRecursive (DirReaderListener listener, String path);
    
    /*
     * Get the VCS action for a collection of <code>FileObject</code>s.
     * If the collection is null, it should get the <code>FileObject</code>s from
     * currently selected nodes.
     * @param fos the collection of <code>FileObject</code>s or null.
     *
    public VcsAction getVcsAction (Collection fos); 
     */
    
    /*
     * Get the VCS action on the VCS filesystem for a specified <code>FileObject</code>.
     *
    public VcsAction getVcsAction (org.openide.filesystems.FileObject fo); 
     */
    
    /**
     * Get the array of VCS actions for a collection of <code>FileObject</code>s.
     * If the collection is null, it should get the <code>FileObject</code>s from
     * currently selected nodes.
     * @param fos the collection of <code>FileObject</code>s or null.
     */
    public SystemAction[] getActions (Collection fos);
    
    /**
     * Get the command executor for the command.
     * @param command the command to get the executor for
     * @param variables the <code>Hashtable</code> of (variable name, variable value) pairs
     * @return the command executor or null when no executor is found for that command.
     * @deprecated This method is retained for compatibility reasons. It may disappear
     *             after compatibility with old VCS "API" will not be needed.
     */
    public VcsCommandExecutor getCommandExecutor(VcsCommand command, Hashtable variables);

}
