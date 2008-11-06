/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.project;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.ada.project.ui.actions.BuildCommand;
import org.netbeans.modules.ada.project.ui.actions.RebuildCommand;
import org.netbeans.modules.ada.project.ui.actions.CleanCommand;
import org.netbeans.modules.ada.project.ui.actions.Command;
import org.netbeans.modules.ada.project.ui.actions.CopyCommand;
import org.netbeans.modules.ada.project.ui.actions.DebugCommand;
import org.netbeans.modules.ada.project.ui.actions.DeleteCommand;
import org.netbeans.modules.ada.project.ui.actions.MoveCommand;
import org.netbeans.modules.ada.project.ui.actions.RenameCommand;
import org.netbeans.modules.ada.project.ui.actions.RunCommand;
import org.netbeans.spi.project.ActionProvider;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaActionProvider implements ActionProvider {

    /**
     * Standard command for running Adadoc on a project.
     * @see org.netbeans.spi.project.ActionProvider
     */
    public static final String COMMAND_ADADOC = "adadoc"; // NOI18N

    final AdaProject project;
    
    private final Map<String,Command> commands;

    /**
     * 
     * @param project
     */
    public AdaActionProvider(AdaProject project) {
        assert project != null;
        this.project = project;
        
        commands = new LinkedHashMap<String, Command>();
        Command[] commandArray = new Command[] {
            new DeleteCommand(project),
            new CopyCommand(project),
            new MoveCommand(project),
            new RenameCommand(project),
            new RunCommand(project),
            new DebugCommand(project) ,
            new BuildCommand(project) ,
            new CleanCommand(project) ,
            new RebuildCommand(project)
        };
        for (Command command : commandArray) {
            commands.put(command.getCommandId(), command);
        }
    }

    /**
     *
     * @return
     */
    public String[] getSupportedActions() {
        final Set<String> names = commands.keySet();
        return names.toArray(new String[names.size()]);
    }

    /**
     *
     * @param commandName
     * @param context
     * @throws IllegalArgumentException
     */
    public void invokeAction(final String commandName, final Lookup context) throws IllegalArgumentException {
        final Command command = findCommand(commandName);
        assert command != null;
        if (command.saveRequired()) {
            LifecycleManager.getDefault().saveAll();
        }
        if (!command.asyncCallRequired()) {
            command.invokeAction(context);
        } else {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    command.invokeAction(context);
                }
            });
        }
    }

    /**
     * 
     * @param commandName
     * @param context
     * @return
     * @throws IllegalArgumentException
     */
    public boolean isActionEnabled(String commandName, Lookup context) throws IllegalArgumentException {
        final Command command = findCommand (commandName);
        assert command != null;
        return command.isActionEnabled(context);
    }
    
    /**
     * 
     * @param commandName
     * @return
     */
    private Command findCommand (final String commandName) {
        assert commandName != null;
        return commands.get(commandName);
    }

}
