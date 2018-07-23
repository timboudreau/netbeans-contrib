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

import java.io.File;
import java.util.Map;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;

/**
 * This class represents a command whose behavior is described by VcsCommand.
 *
 * @author  Martin Entlicher
 */
public interface VcsDescribedCommand extends Command, TextOutputCommand,
                                             RegexOutputCommand, FileReaderCommand,
                                             ChainingCommand {

    /**
     * Set the VcsCommand instance associated with this command.
     * @param cmd the VcsCommand.
     */
    public void setVcsCommand(VcsCommand cmd);
    
    /**
     * Get the VcsCommand instance associated with this command.
     * @return The VcsCommand.
     */
    public VcsCommand getVcsCommand();
    
    /**
     * Set additional variables for the command execution.
     * @param vars The map of variable names and values.
     */
    public void setAdditionalVariables(Map vars);
    
    /**
     * Get additional variables for the command execution.
     * @return the map of variable names and values.
     */
    public Map getAdditionalVariables();
    
    /**
     * Set a preferred execution string, which might have some variables
     * or patterns expanded.
     * @param preferredExec the preferred execution string
     */
    public void setPreferredExec(String preferredExec);
    
    /**
     * Get a preferred execution string, which might have some variables
     * or patterns expanded.
     * @return the preferred execution string
     */
    public String getPreferredExec();
    
    /**
     * Set a preferred structured execution property, which might have some variables
     * or patterns expanded.
     * @param preferredSExec the preferred structured execution property
     */
    public void setPreferredStructuredExec(StructuredExec preferredSExec);
    
    /**
     * Get a preferred structured execution property, which might have some variables
     * or patterns expanded.
     * @return the preferred structured execution property
     */
    public StructuredExec getPreferredStructuredExec();
    
    /**
     * Set the executor, which was already created to take care about executing
     * of this command.
     * @deprecated This is needed only for the compatibility with the old "API".
     */
    public void setExecutor(VcsCommandExecutor executor);
    
    /**
     * Get the executor, which was already created to take care about executing
     * of this command.
     * @deprecated This is needed only for the compatibility with the old "API".
     */
    public VcsCommandExecutor getExecutor();
    
    /**
     * Sometimes the FileObject can not be found for a desired file. In this
     * case this method should be used to specify directly the disk files to act on.
     * The command is expected to act on the union of all set FileObjects and java.io.Files.
     * <p>
     * Some commands require here directories only! TODO assert it rather then leaving
     * them fail for misterious reasons. Typicaly OS 'cd file.txt;' failure.
     * @param files The array of files to act on.
     */
    public void setDiskFiles(File[] files);

    /**
     * Sometimes the FileObject can not be found for a desired file. In this
     * case this method should be used to specify directly the disk files to act on.
     * The command is expected to act on the union of all set FileObjects and java.io.Files.
     * @return The array of files to act on.
     */
    public File[] getDiskFiles();
    
    /**
     * Set a wrapper for the visualizer.
     * A GUI output of the command (visualizer) will be displayed in this wrapper,
     * if any GUI output is available.
     * @param wrapper The wrapper for the command's GUI visualizer
     */
    public void setVisualizerWrapper(VcsCommandVisualizer.Wrapper wrapper);

    /**
     * Get a wrapper for the visualizer.
     * If there is a wrapper defined, the command visualizer will be displayed
     * in this wrapper.
     */
    public VcsCommandVisualizer.Wrapper getVisualizerWrapper();
    
    public Object clone();

}
