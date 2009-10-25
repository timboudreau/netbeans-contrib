/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.fuse.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.modules.php.spi.commands.FrameworkCommand;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.fuse.FuseFramework;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * @author Martin Fousek
 */
public final class FuseCommandSupport extends FrameworkCommandSupport {
    static final Pattern COMMAND_PATTERN = Pattern.compile("^\\:(\\S+)\\s+(.+)$"); // NOI18N
    static final Pattern PREFIX_PATTERN = Pattern.compile("^(\\w+)$"); // NOI18N

    public static String[][] FUSE_GENERATING_COMMANDS = {
        {NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerDisplayName"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerDescription"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerCommand"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerHelp")},
        {NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelDisplayName"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelDescription"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelCommand"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelHelp")},
        {NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelViewDisplayName"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelViewDescription"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelViewCommand"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateControllerModelViewHelp")},
        {NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateModelDisplayName"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateModelDescription"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateModelCommand"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateModelHelp")},
        {NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateViewDisplayName"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateViewDescription"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateViewCommand"),
         NbBundle.getMessage(FuseCommandSupport.class, "CMD_GenerateViewHelp")}
    };


    public static String[] getFuseGeneratingScripts() {
        ArrayList<String> listOfScripts = new ArrayList<String>();
        for (int i = 0; i < FUSE_GENERATING_COMMANDS.length; i++) {
            listOfScripts.add(FUSE_GENERATING_COMMANDS[i][2]);
        }
        return listOfScripts.toArray(new String[]{});
    }

    public static String getHelp(String command) {
        String help = "";
        for (int i = 0; i < FUSE_GENERATING_COMMANDS.length; i++) {
            if (FUSE_GENERATING_COMMANDS[i][2].equals(command))
                help = FUSE_GENERATING_COMMANDS[i][3];
        }
        return help;
    }

    public FuseCommandSupport(PhpModule phpModule) {
        super(phpModule);
    }

    public static FuseCommandSupport forCreatingProject(PhpModule phpModule) {
        return new FuseCommandSupport(phpModule);
    }

    @Override
    public String getFrameworkName() {
        return NbBundle.getMessage(FuseCommandSupport.class, "MSG_Fuse");
    }

    @Override
    protected String getOptionsPath() {
        return FuseFramework.getOptionsPath();
    }

    @Override
    protected ExternalProcessBuilder getProcessBuilder(boolean warnUser) {
        ExternalProcessBuilder externalProcessBuilder = super.getProcessBuilder(warnUser);
        if (externalProcessBuilder == null) {
            return null;
        } 
         externalProcessBuilder = externalProcessBuilder
                .workingDirectory(FileUtil.toFile(phpModule.getSourceDirectory()));
        return externalProcessBuilder;
    }

    protected List<FrameworkCommand> getFrameworkCommandsInternal() {
        ArrayList<FrameworkCommand> commandList = new ArrayList<FrameworkCommand>();
        for (int i = 0; i < FUSE_GENERATING_COMMANDS.length; i++) {
            commandList.add(new FuseCommand(FUSE_GENERATING_COMMANDS[i][2], FUSE_GENERATING_COMMANDS[i][1], FUSE_GENERATING_COMMANDS[i][0]));
        }

        return commandList;
    }

    @Override
    public void runCommand(CommandDescriptor commandDescriptor) {
        Callable<Process> callable = createCommand(commandDescriptor.getFrameworkCommand().getCommand(), commandDescriptor.getCommandParams());
        ExecutionDescriptor descriptor = getDescriptor();
        String displayName = getOutputTitle(commandDescriptor);
        ExecutionService service = ExecutionService.newService(callable, descriptor, displayName);
        service.run();
    }

    @Override
    protected File getPluginsDirectory() {
        return null;
    }
}
