/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.packager;

import java.awt.Dialog;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/** Action provider of the J2SE project. This is the place where to do
 * strange things to J2SE actions. E.g. compile-single.
 */
class PackagerActionProvider implements ActionProvider {
    
    // Commands available from J2SE project
    private static final String[] supportedActions = {
        COMMAND_BUILD, 
        COMMAND_CLEAN, 
        COMMAND_REBUILD, 
        COMMAND_RUN
    };
    
    // Project
    PackagerProject project;
    
    // Ant project helper of the project
    private AntProjectHelper antProjectHelper;
        
    /** Map from commands to ant targets */
    Map/*<String,String[]>*/ commands;
    
    public PackagerActionProvider(PackagerProject project, AntProjectHelper antProjectHelper) {
        
        commands = new HashMap();
            commands.put(COMMAND_BUILD, new String[] {"build"}); // NOI18N
            commands.put(COMMAND_RUN, new String[] {"run"}); // NOI18N
            commands.put(COMMAND_CLEAN, new String[] {"clean"}); // NOI18N
            commands.put(COMMAND_REBUILD, new String[] {"clean", "build"}); // NOI18N

//            commands.put(COMMAND_RUN, new String[] {"run"}); // NOI18N
        
        this.antProjectHelper = antProjectHelper;
        this.project = project;
    }
    
    private FileObject findBuildXml() {
        return project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
    }
    
    public String[] getSupportedActions() {
        return supportedActions;
    }
    
    public void invokeAction( String command, Lookup context ) throws IllegalArgumentException {
        String[] targetNames = (String[]) commands.get(command);
        
        try {
            ActionUtils.runTarget(findBuildXml(), targetNames, null);
        } 
        catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        
            
    }
    
    public boolean isActionEnabled( String command, Lookup context ) {
        return findBuildXml() != null;
    }
    
}
