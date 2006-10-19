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

package org.netbeans.modules.java.tools.nbjad.actions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.netbeans.modules.java.tools.nbjad.NbjadSettings;
import org.openide.ErrorManager;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class DecompileAction extends CookieAction {
    
    private static Date latestFileDate = new Date();
    
    private static String nbjadOutputDirectory=
            System.getProperty("org.netbeans.modules.java.tools.nbjad.output.directory",
            System.getProperty("user.home") + File.separator + ".nbjad");
    
    private NbjadSettings settings;
    
    public DecompileAction() {
        settings =(NbjadSettings)SharedClassObject.findObject(NbjadSettings.class, true);
    }
    
    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        if (dataObject != null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject != null && fileObject.getExt().equals("class")) {
                // Ensure output directory
                File nbjadOutputDirectoryFile = new File(nbjadOutputDirectory);
                if (nbjadOutputDirectoryFile.exists()) {
                    if (!nbjadOutputDirectoryFile.isDirectory()) {
                        ErrorManager.getDefault().log("Not a directory: " + nbjadOutputDirectory);
                        return;
                    }
                } else if (!nbjadOutputDirectoryFile.mkdirs()) {
                    ErrorManager.getDefault().log("Could not create NBJAD output directory: " + nbjadOutputDirectory);
                    return;
                }
                
                // Output directory FileObject
                FileObject nbjadOutputDirectoryFileObject = FileUtil.toFileObject(nbjadOutputDirectoryFile);
                
                try {
                    // Check for Jar file system
                    FileSystem fileSystem = fileObject.getFileSystem();
                    if (fileSystem instanceof JarFileSystem) {
                        try {
                            fileObject =
                                    FileUtil.copyFile(fileObject,
                                    nbjadOutputDirectoryFileObject,
                                    fileObject.getName(),
                                    fileObject.getExt());
                            FileUtil.toFile(fileObject).deleteOnExit();
                        } catch (IOException ex) {
                            ErrorManager.getDefault().log("Could not create physical class file on disk.");
                            return;
                        }
                    }
                } catch (FileStateInvalidException fsie) {
                    ErrorManager.getDefault().log(fsie.getMessage());
                }
                // Now get the java.io.File for the class file
                File file = FileUtil.toFile(fileObject);
                if (file != null) {
                    try {
                        List jadCommand = new ArrayList();
                        jadCommand.add(settings.getJadLocation()); // JAD executable
                        jadCommand.add("-o");                      // overwrite
                        jadCommand.add("-r");                      // create package directory structure
                        jadCommand.add("-s");                      // suffix .java
                        jadCommand.add(".java");
                        jadCommand.add("-d");                      //  output directoryo
                        jadCommand.add(nbjadOutputDirectory);
                        // Additional options
                        String jadOptions = settings.getJadOptions().trim();
                        if (jadOptions != null && jadOptions.length() > 0) {
                            jadCommand.addAll(Arrays.asList(jadOptions.split(" ")));
                        }
                        jadCommand.add(file.getAbsolutePath());
                                                
                        FilterProcess filterProcess =
                                new FilterProcess((String[])jadCommand.toArray(new String[0]));                        
                        PrintWriter in = filterProcess.exec();
                        in.close();
                        
                        if (filterProcess.waitFor() == 0) {
                            
                            InputOutput io = IOProvider.getDefault().getIO("Decompile: " + fileObject.getNameExt(), true);                        
                            PrintWriter pw = new PrintWriter(io.getOut());
                            pw.println(jadCommand);
                            String[] linesText = filterProcess.getStdOutOutput();
                            if (linesText != null) {
                                for (int i = 0; i < linesText.length; i++) {
                                    pw.println(linesText[i]);
                                }
                            }
                            linesText = filterProcess.getStdErrOutput();
                            if (linesText != null) {
                                pw = new PrintWriter(io.getErr());
                                for (int i = 0; i < linesText.length; i++) {
                                    pw.println(linesText[i]);
                                }
                            }
                        }
                        filterProcess.destroy();
                        
                        // Open newser files
                        openNewFilesIn(nbjadOutputDirectoryFileObject, nbjadOutputDirectoryFile);
                    } catch (IOException fe) {
                        ErrorManager.getDefault().notify(ErrorManager.ERROR, fe);
                    }
                }
            }
        }
    }
    
    private static void openNewFilesIn(FileObject nbjadOutputDirectoryFileObject, File nbjadOutputDirectoryFile) {
        openNewFilesIn(nbjadOutputDirectoryFileObject, nbjadOutputDirectoryFile, latestFileDate);
        latestFileDate = new Date(System.currentTimeMillis());
    }
    
    private static void openNewFilesIn(FileObject root, File directory, Date laterThan) {
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                openNewFilesIn(root, file, laterThan);
                continue;
            }
            FileObject fileObject = FileUtil.toFileObject(file);
            if (fileObject.lastModified().after(latestFileDate)) {
                if (fileObject.getPath().endsWith(".java")) {
                    if (FileUtil.isParentOf(root, fileObject)) {
                        DataObject dataObject;
                        try {
                            // Configure to remove the file on exit.
                            // file.deleteOnExit();
                            dataObject = DataObject.find(fileObject);
                            OpenCookie openCookie = (OpenCookie) dataObject.getCookie(OpenCookie.class);
                            openCookie.open();
                        } catch (DataObjectNotFoundException donfe) {
                            ErrorManager.getDefault().notify(ErrorManager.USER, donfe);
                        }
                    }
                }
            }
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(DecompileAction.class, "CTL_DecompileAction");
    }
    
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length > 0) {
            DataObject dataObject = (DataObject) activatedNodes[0].getCookie(DataObject.class);
            if (dataObject != null) {
                FileObject fileObject = dataObject.getPrimaryFile();
                if (fileObject != null && fileObject.getExt().equals("class")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
        };
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/java/tools/nbjad/actions/nbjad.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}
