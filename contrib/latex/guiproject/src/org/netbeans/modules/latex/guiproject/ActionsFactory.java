/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;
import java.awt.event.ActionEvent;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;




import org.apache.tools.ant.module.api.AntProjectCookie;
import org.apache.tools.ant.module.api.AntTargetExecutor;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;

import org.netbeans.modules.latex.guiproject.ui.PropertiesDialogPanel;


import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;


import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;





//import org.openide.execution.ExecutorTask;




import org.openide.filesystems.FileObject;

import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;


import org.openide.loaders.DataObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.text.NbDocument;



import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class ActionsFactory {
    
    /** Creates a new instance of ActionsFactory */
    public ActionsFactory() {
    }
    
    private static final String BUILD_TARGET = "build";
    
    private static void fillInProperties(LaTeXGUIProject project, Properties p) throws IOException {
        URL tasksArchiveURL = new URL("nbinst://org-netbeans-modules-latex-guiproject/ant/extra/ant-latex.jar");
        File tasksArchiveFile = FileUtil.toFile(URLMapper.findFileObject(tasksArchiveURL));
        
        p.setProperty("libs.latextasks.classpath", tasksArchiveFile.getAbsolutePath());
        
        File editor = findLideClient();
        
        if (editor != null) {
            System.err.println("editor=" + editor.getAbsolutePath());
            
            p.setProperty("xdvi.editor.format", editor.getAbsolutePath() + " -- -folder " + project.getProjectInternalDir().getParentFile().getAbsolutePath() + " -scroll %f?*?%l?*?%c");
        } else {
            System.err.println("The lide-client binary not found!");
        }
        
        JEditorPane lastPane = Utilities.getDefault().getLastActiveEditorPane();
        
        if (lastPane != null) {
            Document    lastDocument = lastPane.getDocument();
//            LaTeXSource lastSource = Utilities.getDefault().getSource(lastDocument);
//            
//            if (source != null && source == lastSource) {
                File source = FileUtil.toFile((FileObject) Utilities.getDefault().getFile(lastDocument));
                int  line   = NbDocument.findLineNumber((StyledDocument) lastDocument, lastPane.getCaret().getDot());
                
                p.setProperty("xdvi.filename", source.getName());//TODO: relative path to the main file should probably go here...
                p.setProperty("xdvi.linenumber", String.valueOf(line));
//            }
        }
    }
    
    private static File findLideClient() {
        return InstalledFileLocator.getDefault().locate("modules/bin/lide-editor-client", null, false);
    }
    
    public static Action createShowAction() {
        return ProjectSensitiveActions.projectCommandAction(LaTeXGUIProject.COMMAND_SHOW, "Show Project Resulting Document", null);
    }
    
    public static Action createBuildAction() {
        return ProjectSensitiveActions.projectCommandAction(LaTeXGUIProject.COMMAND_BUILD, "Build Project", null);
    }
    
    public static Action createMainProjectShowAction() {
        return MainProjectSensitiveActions.mainProjectCommandAction(LaTeXGUIProject.COMMAND_SHOW, "Show Main Project Resulting Document", null);
    }
    
    public static void build(final LaTeXGUIProject project, final String target) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    Properties p = new Properties();
                    
                    fillInProperties(project, p);
                    
                    File buildScript = new File(project.getProjectInternalDir(), "build.xml");
                    FileObject fo = FileUtil.toFileObject(buildScript);
                    
                    ActionUtils.runTarget(fo, new String[] {target}, p);
////                    FileObject toRefresh = toCompile.getParent();
////                    
////                    toRefresh.refresh();
////                    
////                    FileObject[] fos = toRefresh .getChildren();
////                    
////                    for (int cntr = 0; cntr < fos.length; cntr++) {
////                        fos[cntr].refresh();
////                    }
                } catch (IOException e) {
                    e.printStackTrace(); //!!!!
                } catch (IllegalArgumentException e) {
                    e.printStackTrace(); //!!!!
                }
            }
        });
    }

    
}
