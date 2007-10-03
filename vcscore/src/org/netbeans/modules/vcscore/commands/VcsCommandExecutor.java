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
import java.util.Collection;

import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;

/**
 * The <code>VcsCommand</code> interface should be implemented by any class
 * whose instances are intended to be executed as version control commands.
 * Each command is executed in a separate thread.
 *
 * @author  Martin Entlicher
 */
public interface VcsCommandExecutor extends Runnable, TextInput {

    /** The exit status when the command terminated successfully. */
    public static final int SUCCEEDED = 0;
    /** The exit status when the command failed. */
    public static final int FAILED = 1;
    /** The exit status when the command was interrupted. */
    public static final int INTERRUPTED = 2;
    
    /**
     * The executed command.
     */
    public VcsCommand getCommand();
    
    /**
     * This method can be used to do some preprocessing of the command which is to be run.
     * The method is called before the prompt for user input is made and therefore can be used to
     * additionally specify the desired input.
     * @param vc the command to be preprocessed.
     * @param vars the variables
     * @param exec the updated execution string. It may contain user input from variable input dialog
     * @return the updated exec property
     */
    public String preprocessCommand(VcsCommand vc, Hashtable vars, String exec, StructuredExec sexec);
        
    /**
     * Get the variables used by this command execution.
     */
    public Hashtable getVariables();
    
    /**
     * Get the updated execution string. It may contain user input now.
     */
    public String getExec();
    
    /**
     * Get the set of files being processed by the command.
     * @return the collection of files paths of type <code>String</code> relative
     * to the file system root.
     */
    public Collection getFiles();
            
    /**
     * Get the exit status of the execution.
     * @return the exit value, it may be one of {@link SUCCEEDED}, {@link FAILED}, {@link INTERRUPTED}.
     */
    public int getExitStatus();
    
    /**
     * Get the graphical visualization of the command.
     * @return the visualizer or null when no visualization is desired.
     */
    public VcsCommandVisualizer getVisualizer();
    
    /**
     * Add the listener to the standard output of the command. The listeners should be
     * released by the implementing class, when the command finishes.
     */
    public void addTextOutputListener(TextOutputListener l);
    
    /**
     * Add the listener to the error output of the command. The listeners should be
     * released by the implementing class, when the command finishes.
     */
    public void addTextErrorListener(TextOutputListener l);
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners should be released by the implementing class,
     * when the command finishes.
     */
    public void addRegexOutputListener(RegexOutputListener l);
    
    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners should be released by the implementing class,
     * when the command finishes.
     */
    public void addRegexErrorListener(RegexOutputListener l);

    /**
     * Add a file reader listener, that gets the updated attributes of the
     * processed file(s). The listeners should be released by the implementing class,
     * when the command finishes.
     */
    public void addFileReaderListener(FileReaderListener l);
    
    /**
     * Add the listener to the standard output of the command. The listeners should be
     * released by the implementing class, when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addTextOutputListener} instead.
     */
    public void addOutputListener(CommandOutputListener l);
    
    /**
     * Add the listener to the error output of the command. The listeners should be
     * released by the implementing class, when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addTextErrorListener} instead.
     */
    public void addErrorOutputListener(CommandOutputListener l);
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners should be released by the implementing class,
     * when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addRegexOutputListener} instead.
     */
    public void addDataOutputListener(CommandDataOutputListener l);
    
    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners should be released by the implementing class,
     * when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addRegexErrorListener} instead.
     */
    public void addDataErrorOutputListener(CommandDataOutputListener l);
    
    /**
     * Add a listener to the standard output, that will be noified
     * immediately as soon as the output text is available. It does not wait
     * for the new line and does not send output line-by-line.
     */
    public void addImmediateTextOutputListener(TextOutputListener l);
    
    /**
     * Add a listener to the standard error output, that will be noified
     * immediately as soon as the output text is available. It does not wait
     * for the new line and does not send output line-by-line.
     */
    public void addImmediateTextErrorListener(TextOutputListener l);
}
