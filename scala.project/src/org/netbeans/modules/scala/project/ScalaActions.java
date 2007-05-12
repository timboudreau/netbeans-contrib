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

package org.netbeans.modules.scala.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.JSeparator;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.actions.FindAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public final class ScalaActions implements ActionProvider {
    
    private final ScalaProject project;
    
    private final Map<String,String[]> globalCommands = new HashMap<String, String[]>();
    private final String[] supportedActions;
    
    ScalaActions(final ScalaProject project) {
        this.project = project;
        Set<String> supportedActionsSet = new HashSet<String>();
        globalCommands.put(COMMAND_BUILD, new String[] {"compile"}); // NOI18N
        globalCommands.put(COMMAND_CLEAN, new String[] {"clean"}); // NOI18N
        globalCommands.put(COMMAND_REBUILD, new String[] {"clean", "compile"}); // NOI18N
        globalCommands.put(COMMAND_RUN_SINGLE, new String[] {"run-single"}); // NOI18N
        supportedActionsSet.addAll(globalCommands.keySet());
        supportedActions = supportedActionsSet.toArray(new String[supportedActionsSet.size()]);
    }
    
    public static Action[] getProjectActions(final ScalaProject project) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(CommonProjectActions.newFileAction());
        actions.add(null);
        actions.add(ProjectSensitiveActions.projectCommandAction(COMMAND_BUILD, getMessage("ACTION_build"), null));
        actions.add(ProjectSensitiveActions.projectCommandAction(COMMAND_REBUILD, getMessage("ACTION_rebuild"), null));
        actions.add(ProjectSensitiveActions.projectCommandAction(COMMAND_CLEAN, getMessage("ACTION_clean"), null));
        actions.add(null);
        actions.add(CommonProjectActions.setAsMainProjectAction());
        actions.add(CommonProjectActions.closeProjectAction());
        actions.add(null);
        actions.add(SystemAction.get(FindAction.class));
        
        // Honor #57874 contract:
        Lookup.Template<Object> query = new Lookup.Template<Object>(Object.class);
        Lookup lookup = Lookups.forPath("Projects/Actions"); // NOI18N
        Iterator<? extends Object> it = lookup.lookup(query).allInstances().iterator();
        if (it.hasNext()) {
            actions.add(null);
        }
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof Action) {
                actions.add((Action) next);
            } else if (next instanceof JSeparator) {
                actions.add(null);
            }
        }
        
        actions.add(null);
        actions.add(SystemAction.get(ToolsAction.class));
        actions.add(null);
        actions.add(CommonProjectActions.customizeProjectAction());
        return actions.toArray(new Action[actions.size()]);
    }
    
    public String[] getSupportedActions() {
        return supportedActions;
    }
    
    public void invokeAction(final String command, final Lookup context)
            throws IllegalArgumentException {
        
        Properties p;
        
        String[] targetNames = globalCommands.get(command);
        if (targetNames == null) {
            throw new IllegalArgumentException(command);
        }
        
        if (command.equals(COMMAND_RUN_SINGLE)) {
            FileObject[] files = findSources(context);
            if (files == null) {
                return;
            }
            String clazz = FileUtil.getRelativePath(project.getSourceDirectory(), files[0]);
            p = new Properties();
            p.setProperty("javac.includes", clazz); // NOI18N
            // Convert foo/FooTest.java -> foo.FooTest
            if (clazz.endsWith(".scala")) { // NOI18N
                clazz = clazz.substring(0, clazz.length() - 6);
            }
            clazz = clazz.replace('/','.');
            p.setProperty("run.class", clazz); // NOI18N
        } else {
            p = null;
        }
        try {
            ActionUtils.runTarget(findBuildXml(), targetNames, p);
        } catch (IOException e) {
            Util.LOG.log(Level.WARNING, "Cannot invoke target " + Arrays.asList(targetNames), e);
        }
    }
    
    public boolean isActionEnabled(final String command, final Lookup context)
            throws IllegalArgumentException {
        // TODO: implement
        return findBuildXml() != null;
    }
    
    private FileObject findBuildXml() {
        return project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
    }
    
    private static String getMessage(final String key) {
        return NbBundle.getMessage(ScalaActions.class, key);
    }
    
    /**
     * Find selected sources which has to be under single source root.
     *
     * @param context the lookup in which files should be found.
     */
    private FileObject[] findSources(final Lookup context) {
        FileObject[] files = null;
        FileObject srcDir = project.getSourceDirectory();
        if (srcDir != null) {
            files = ActionUtils.findSelectedFiles(context, srcDir, ".scala", true); // NOI18N
        }
        return files;
    }
    
}
