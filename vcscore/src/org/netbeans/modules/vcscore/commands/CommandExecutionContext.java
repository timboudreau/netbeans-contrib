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

package org.netbeans.modules.vcscore.commands;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.openide.filesystems.FileObject;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;

/**
 * A provider of execution context, that is necessary for the UserCommand
 * to be executed. If this information object is an instance of
 * VcsFileSystem, more information can be obtained from VcsFileSystem.
 *
 * @author  Martin Entlicher
 */
public interface CommandExecutionContext {
    
    /**
     */
    boolean isOffLine();
    
    boolean isExpertMode();
    
    //boolean isProcessUnimportantFiles();
    
    //boolean isImportant(String name);
    
    boolean isPromptForVarsForEachFile();
    
    void setPromptForVarsForEachFile(boolean promptForVarsForEachFile);
    
    boolean isCommandNotification();
    
    void setCommandNotification(boolean commandNotification);
    
    /**  If this is FileSystem, take it from there! If not, get abolute paths.
     * If the execution context is a filesystem, it may wish to 
     *
    String convertFileToPath(java.io.File file);
    
    String convertFileToPath(FileObject file);
     */
    
    Vector getVariables();
    
    void setVariables(Vector variables);
    
    Hashtable getVariablesAsHashtable();
    
    VariableValueAdjustment getVarValueAdjustment();
    
    String[] getEnvironmentVars();
    
    /**
     * Get the map of possible pairs of status name and corresponding FileStatusInfo object.
     */
    Map getPossibleFileStatusInfoMap();
    
    VcsCommand getCommand(String name);
    
    CommandSupport getCommandSupport(String name);
    
    VcsCommandsProvider getCommandsProvider();
    
    //String getCacheIdStr();
    
    //FileCacheProvider getCacheProvider();
    
    //FileStatusProvider getStatusProvider();
    
    String getPassword();
    
    void setPassword(String password);
    
    String getPasswordDescription();
    
    String[] getUserParamsLabels();
    
    String[] getUserLocalParamsLabels();
    
    String[] getUserParams();
    
    void setUserParams(String[] userParams);
    
    boolean isAcceptUserParams();
    
    /**
     * Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    void checkForModifications(String path);
    
    //VersioningFileSystem getVersioningFileSystem();
    
    //void fireRevisionsChanged(RevisionEvent rev);
    
    /**
     * Print a debug output. If the debug property is true, the message
     * is printed to the Output Window.
     * @param msg The message to print out.
     */
    void debug(String msg);
    
    /**
     * Print an error output. Force the message to print to the Output Window.
     * The debug property is not considered.
     * @param msg the message to print out.
     */
    void debugErr(String msg);
    
}
