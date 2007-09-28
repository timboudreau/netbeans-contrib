/*
 * ThemeBuilderActionProvider.java
 *
 * Created on February 12, 2007, 6:47 PM
 */

package org.netbeans.modules.themebuilder.project.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.modules.themebuilder.project.ThemeBuilderProject;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Theme Builder Action Provider Implementation
 * Registered in a project's lookup and will be used by project UI infrastructure.
 * @author Winston Prakash
 * @version 1.0
 */
public final class ThemeBuilderActionProvider implements ActionProvider{
    
    ThemeBuilderProject project;
   
    /**
     *
     * @param prj
     */
    public ThemeBuilderActionProvider(ThemeBuilderProject prj){
        project = prj;
    }
    
    public String[] getSupportedActions() {
        return new String[] {
            COMMAND_BUILD,
            COMMAND_CLEAN,
            COMMAND_DELETE,
            COMMAND_COPY,
            COMMAND_MOVE,
            COMMAND_RENAME,
        };
    }
    
    public void invokeAction(final String command, final Lookup lookup) throws IllegalArgumentException {
        if (COMMAND_DELETE.equals(command)) {
            DefaultProjectOperations.performDefaultDeleteOperation(project);
            return ;
        }
        
        if (COMMAND_COPY.equals(command)) {
            DefaultProjectOperations.performDefaultCopyOperation(project);
            return ;
        }
        
        if (COMMAND_MOVE.equals(command)) {
            DefaultProjectOperations.performDefaultMoveOperation(project);
            return ;
        }
        
        if (COMMAND_RENAME.equals(command)) {
            DefaultProjectOperations.performDefaultRenameOperation(project, null);
            return ;
        }
        
        Runnable action = new Runnable() {
            public void run() {
                Properties p = new Properties();
                String[] targetNames;
                
                targetNames = getTargetNames(command, lookup, p);
                if (targetNames == null) {
                    return;
                }
                if (targetNames.length == 0) {
                    targetNames = null;
                }
                if (p.keySet().size() == 0) {
                    p = null;
                }
                try {
                    FileObject buildFo = findBuildXml();
                    if (buildFo == null || !buildFo.isValid()) {
                        //The build.xml was deleted after the isActionEnabled was called
                        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(ThemeBuilderActionProvider.class,
                                "LBL_No_Build_XML_Found"), NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                    } else {
                        ActionUtils.runTarget(buildFo, targetNames, p);
                    }
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        };
        action.run();
    }
    
    private String[] getTargetNames(String command, Lookup context, Properties p) throws IllegalArgumentException {
        String[] targetNames = new String[0];
        if (command.equals(COMMAND_BUILD)) {
            return  new String[] {"jar"}; // NOI18N
        }else if (command.equals(COMMAND_CLEAN)) {
            return  new String[] {"clean"}; // NOI18N
        }else if (command.equals(COMMAND_REBUILD)) {
            return  new String[] {"clean", "jar"}; // NOI18N
        }else{
            throw new IllegalArgumentException(command);
        }
    }
    
    private FileObject findBuildXml() {
        return project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
    }
    
    public boolean isActionEnabled(String string, Lookup lookup) throws IllegalArgumentException {
        int idx = Arrays.asList(getSupportedActions()).indexOf(string);
        boolean result;
        switch (idx) {
        case 0 : //build
            result = true;
            break;
        case 1 : //clean
        }
        return true;
    }
}
