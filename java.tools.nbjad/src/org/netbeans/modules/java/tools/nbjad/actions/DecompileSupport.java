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

package org.netbeans.modules.java.tools.nbjad.actions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class DecompileSupport {
    public static final String NBJAD_JAD_LOCATION = "org.netbeans.modules.java.tools.nbjad.jad.location"; // NOI18N
    public static final String NBJAD_JAD_OPTIONS  = "org.netbeans.modules.java.tools.nbjad.jad.options";  // NOI18N

    public static String JAD_LOCATION = DecompileSupport.class.getPackage().getName() + "." + "JAD_EXECUTABLE_LOCATION";

    private static long latestFileDate = System.currentTimeMillis() - 500;

    private static String nbjadOutputDirectory=
            System.getProperty("org.netbeans.modules.java.tools.nbjad.output.directory",
                               System.getProperty("user.home") + File.separator + ".nbjad");

    private static Preferences preferences = NbPreferences.forModule(DecompileSupport.class);

    static void decompile(FileObject fileObject) {
        // Is it a .class file
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

            addToFavorites(nbjadOutputDirectoryFileObject);

            try {
                // Check for Jar file system
                FileSystem fileSystem = fileObject.getFileSystem();
                if (fileSystem instanceof JarFileSystem) {
                    // Need to handle inner classes
                    String fileObjectName = fileObject.getName();
                    String fileObjectExt  = fileObject.getExt();

                    int dollarAt = fileObjectName.indexOf('$');
                    if (dollarAt != -1) {
                        // Strip dollar sign if inner class
                        fileObjectName = fileObjectName.substring(0, dollarAt);
                    }

                    // Get parent folder so that we can copy all inner classes
                    //
                    FileObject folderFileObject = fileObject.getParent();
                    FileObject[] childrenFileObjects = folderFileObject.getChildren();
                    for (int i = 0; i < childrenFileObjects.length; i++) {
                        FileObject aSibling = childrenFileObjects[i];
                        if (aSibling.isData() &&                                        // is a file
                                aSibling.getExt().equals(fileObjectExt) &&              // is .classs
                                (aSibling.getName().equals(fileObjectName) ||           // top level
                                aSibling.getName().startsWith(fileObjectName + '$'))) { // inner class
                            try {
                                
                                FileObject copiedFileObject = nbjadOutputDirectoryFileObject.getFileObject(
                                        aSibling.getNameExt());
                                if (copiedFileObject != null && copiedFileObject.isValid()) {
                                    copiedFileObject.delete();
                                }
                                // Freshly copy contents of .class file in jar file to output directory
                                copiedFileObject =
                                        FileUtil.copyFile(aSibling,
                                        nbjadOutputDirectoryFileObject,
                                        aSibling.getName(),
                                        aSibling.getExt());
                                // Top level class
                                if (aSibling.getName().equals(fileObjectName)) {
                                    // That is the one we decompile
                                    fileObject = copiedFileObject;
                                }
                            } catch (IOException ex) {
                                ErrorManager.getDefault().log("Could not create physical class file on disk.");
                                return;
                            }
                        }
                    }
                    if (fileObject.getFileSystem() instanceof JarFileSystem) {
                        // Could not copy .class file from jar to
                        DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message("Could not copy '"
                                + fileObject.getPath()
                                + "' to '" + nbjadOutputDirectoryFileObject.getPath() + "'." ));
                        return;
                    }
                }
            } catch (FileStateInvalidException fsie) {
                ErrorManager.getDefault().log(fsie.getMessage());
            }

            // Now get the java.io.File for the class file
            File file = FileUtil.toFile(fileObject);
            if (file != null) {
                // Log
                InputOutput io = IOProvider.getDefault().getIO("Decompile", false);
                PrintWriter pw = new PrintWriter(io.getOut());

                // Build the jad command
                List jadCommand = new ArrayList();
                jadCommand.add(preferences.get(DecompileSupport.NBJAD_JAD_LOCATION, "jad").trim()); // JAD executable
                jadCommand.add("-o");                      // overwrite
                jadCommand.add("-r");                      // create package directory structure
                jadCommand.add("-s");                      // suffix .java
                jadCommand.add(".java");
                jadCommand.add("-d");                      //  output directoryo
                jadCommand.add(nbjadOutputDirectory);

                // Additional options
                String jadOptions = preferences.get(DecompileSupport.NBJAD_JAD_OPTIONS, " ").trim();
                if (jadOptions != null && jadOptions.length() > 0) {
                    jadCommand.addAll(Arrays.asList(jadOptions.split(" ")));
                }
                jadCommand.add(file.getAbsolutePath());

                pw.println(jadCommand);

                try {
                    // Run jad decompiler
                    FilterProcess filterProcess =
                            new FilterProcess((String[])jadCommand.toArray(new String[0]));
                    PrintWriter in = filterProcess.exec();
                    in.close();


                    // Did it exit normally
                    int status = filterProcess.waitFor();
                    if (status == 0) {

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
                    } else {
                        // Failed
                        pw = new PrintWriter(io.getErr());
                        pw.println("Decompilation exited with status: " + status);
                    }

                    // Cleanup process
                    filterProcess.destroy();

                    // Open newser files
                    openNewFilesIn(nbjadOutputDirectoryFileObject, nbjadOutputDirectoryFile);

                } catch (IOException fe) {
                    // Failed
                    pw = new PrintWriter(io.getErr());
                    pw.println("Decompilation failed.");
                    pw.println(fe.getMessage());
                    pw.println("Make sure you have configured the location of Jad correctly using Tools:Options:Decompile panel.");
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(fe.getMessage() +
                            "\nMake sure you have configured the location of Jad correctly using Tools:Options:Decompile panel."));
                }
            }
        }
    }

    private static void openNewFilesIn(FileObject nbjadOutputDirectoryFileObject, File nbjadOutputDirectoryFile) {
        openNewFilesIn(nbjadOutputDirectoryFileObject, nbjadOutputDirectoryFile, latestFileDate);
        latestFileDate = System.currentTimeMillis() - 500;
    }

    // Recursively open files later than last sampled time
    private static void openNewFilesIn(FileObject root, File directory, long laterThan) {
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                // Recurse into child folders
                openNewFilesIn(root, file, laterThan);
                continue;
            }

            // Get FileObject
            FileObject fileObject = FileUtil.toFileObject(file);

            // Is it a .java file
            if (fileObject != null && fileObject.getExt().equals("java")) {
                long fileLastModified = fileObject.lastModified().getTime();
                // Is it newer
                if (fileLastModified > laterThan) {
                    // Not required really as we are recursing from the root
                    if (FileUtil.isParentOf(root, fileObject)) {
                        DataObject dataObject;
                        try {
                            // Configure to remove the file on exit.
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

    private static void addToFavorites(FileObject nbjadOutputDirectoryFileObject) {
         try {
            FileObject favoritesFileObject = FileUtil.createFolder (
                Repository.getDefault().getDefaultFileSystem().getRoot(),
                "Favorites" // NOI18N
            );

            if (favoritesFileObject == null) {
                return;
            }

            FileObject nbjadOutputDirectoryFileObjectShadow = favoritesFileObject.getFileObject(nbjadOutputDirectoryFileObject.getNameExt(), "shadow");
            if (nbjadOutputDirectoryFileObjectShadow == null) {
                DataFolder favoritesFolder = DataFolder.findFolder(favoritesFileObject);

                DataFolder nbjadOutputDirectoryDataFolder = (DataFolder) DataFolder.find(nbjadOutputDirectoryFileObject);
                if (nbjadOutputDirectoryDataFolder != null) {
                    nbjadOutputDirectoryDataFolder.createShadow(favoritesFolder);
                }
            }
        } catch (IOException ex) {
        }
    }
}
