/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * PovProjectFactory.java
 *
 * Created on February 16, 2005, 5:38 PM
 */

package org.netbeans.modules.povproject;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Mutex;

/**
 * Factory registered into the default Lookup via META-INF/services file entry
 * Responsible for recognizing and loading povray projects.  A povray project
 * is identified as being a directory with a subdirectory "povproject".
 *
 * @see org.openide.util.Lookup
 * @author Timothy Boudreau
 */
public class PovProjectFactory implements ProjectFactory {
    public static final String PROJECT_DIR = "povproject";
    public static final String PROJECT_PROPFILE = "project.properties";
    public static final String SCENES_DIR = "scenes";
    
    private Map projectCache = new HashMap();
    
    /** Creates a new instance of PovProjectFactory */
    public PovProjectFactory() {
    }

    public boolean isProject(FileObject projectDirectory) {
        return projectDirectory.getFileObject(PROJECT_DIR) != null;
    }
    
    /**
     * Scans from any directory down toward the root of the file system,
     * looking for some parent dir with a subdir "povproject" 
     */
    private Project projectFor (FileObject dir, ProjectState state) {
        synchronized (projectCache) {
            Reference ref = (Reference) projectCache.get(dir.getPath());
            Project result = null;
            if (ref != null) {
                result = (Project) ref.get();
            }
            if (result == null) {
                result = new PovProject (dir, state);
                ref = new WeakReference (result);
                projectCache.put (dir.getPath(), ref);
            }
            return result;
        }
    }

    /**
     * Incredibly non-obvious from the docs, but loadProject may be called
     * on any subdir of a project, and this method should track down to 
     * the root and find/return the appropriate project.
     */
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        while (dir != null) {
            if (isProject(dir)) {
                return projectFor(dir, state);
            } else {
                dir = dir.getParent();
            }
        }
        return null;
    }

    public void saveProject(final Project project) throws IOException, ClassCastException {
        FileObject dir = project.getProjectDirectory();
        if (dir.getFileObject(PROJECT_DIR) == null) {
            //If a subproject or something, user *could* manage to delete it.
            //Log a message, but don't show it to the user
            ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL,
                    new IOException ("Project dir " + dir.getPath() + " deleted," +
                    " cannot save project"));
            return;
        }
        
        FileObject scenesDir = dir.getFileObject (SCENES_DIR);
        if (scenesDir == null) {
            //User could delete the scenes dir, though not sure why they would
            FileObject scenes = dir.createFolder (SCENES_DIR);
        }
        
        FileObject propertiesFile = dir.getFileObject(PROJECT_DIR + "/" + PROJECT_PROPFILE);
        if (propertiesFile == null) {
            //Recreate the properties file if needed
            propertiesFile = dir.createData(PROJECT_DIR + "/" + PROJECT_PROPFILE);
        }

        Properties properties = (Properties) project.getLookup().lookup (Properties.class);
        if (!properties.containsKey(PovProject.KEY_VERSION)) {
            properties.setProperty(PovProject.KEY_VERSION, Integer.toString(PovProject.VALUE_VERSION));
        }
        
        MainFileProvider provider = (MainFileProvider) project.getLookup().lookup (MainFileProvider.class);
        FileObject mainFile = provider.getMainFile();
        if (mainFile != null) {
            String pdPath = dir.getPath();
            assert mainFile.getPath().startsWith(pdPath) : mainFile.getPath() + " not a substring of " + pdPath;

            String relPath = mainFile.getPath().substring(pdPath.length() + 1);
            assert dir.getFileObject (relPath).equals(mainFile) : relPath + " not path to " + mainFile.getPath();

            properties.setProperty(PovProject.KEY_MAINFILE, relPath);

            FileLock lock = propertiesFile.lock();
            if (!lock.isValid()) {
                throw new IOException ("Invalid lock!");
            }
            try {
                //Mysteriously, using FileObject.getOutputStream results in IOE
                //at org.netbeans.modules.masterfs.filebasedfs.fileobjects.MutualExclusion
                //Support.addResource(MutualExclusionSupport.java:67)
                //Hack with file for now
                File f = FileUtil.toFile(propertiesFile);
                properties.store(new FileOutputStream(f), "NetBeans Povray Project Properties");
            } finally {
                lock.releaseLock();
            }
        }
    }
    
 
    /** Path to the sample template in the system filesystem (see layer.xml) */
    private static final String SAMPLE_PROJECT_TEMPLATE = "Povray/NetBeansLogo.pov";
    
    /** Path to the empty template in the system filesystem (see layer.xml) */
    private static final String EMPTY_POVRAY_TEMPLATE = "Povray/Empty.pov";

    /** Name of the template itself (see layer.xml) - really we just use this as a marker
     * to decide which of the two possible templates to instantiate in any new project */
    private static final String SAMPLE_PROJECT_SFS_FILENAME = "samplePovrayProject";
    
    /** Utility method for creating a new povray project from scratch.  This is
     * called by NewProjectIterator.instantiate().
     *
     * @param name The name of the new project
     * @param template The template the user chose in the wizard (empty or sample project)
     * @param dest The parent folder for the new project
     * @param sceneFileName the name entered for the scene file to create
     * @return A DataFolder representing the newly created project directory
     */
    public static DataFolder createNewPovrayProject (final String name, final DataObject template, final DataFolder dest, final String sceneFileName) throws IOException {

        //Do this inside the write mutex - we don't want anything noticing
        //a new project while we're still writing its files to disk.
        
        //If we succeed, we will return the project folder;  if an exception
        //is encountered, we'll return that
        Object result = ProjectManager.mutex().writeAccess(new Mutex.Action () {
           public Object run() {
               
               //A list for files we'll create, so we can delete them in the
               //event of failure
               final List created = new ArrayList();
               
               try {
                   
                    //Create the project directory
                    DataFolder projectFolder = DataFolder.create (dest, name);
                    created.add (projectFolder);

                    //Create the subdirectory for scene files
                    DataFolder scenesDir = DataFolder.create (projectFolder, PovProject.SCENES_DIR);
                    created.add (scenesDir);

                    //Figure out which template we're using, based on the file 
                    //name of the project template the user chose
                    String sceneTemplateName = template.getName().equals(SAMPLE_PROJECT_SFS_FILENAME) ?
                        SAMPLE_PROJECT_TEMPLATE :
                        EMPTY_POVRAY_TEMPLATE;

                    //Instantiate whichever template should be inside the new project
                    DataObject sceneTemplate = null;
                    try {
                        sceneTemplate = DataObject.find(
                            Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(sceneTemplateName));

                    } catch (DataObjectNotFoundException e) {
                        IOException ioe = new IOException ("Could not find template " + sceneTemplateName);
                        ErrorManager.getDefault().annotate (ioe, e);
                        throw ioe;
                    }

                    DataObject scene = sceneTemplate.createFromTemplate(scenesDir, sceneFileName);
                    created.add (scene);

                    //Create the directory to hold project information
                    DataFolder projectData = DataFolder.create (projectFolder, PovProjectFactory.PROJECT_DIR);
                    created.add (projectData);

                    //Create the project.properties file inside the project data subdir
                    FileObject fob = projectData.getPrimaryFile();
                    FileObject projectProps = fob.createData(PovProjectFactory.PROJECT_PROPFILE);
                    created.add (projectProps);

                    //Create and populate a Properties object, and write it to the project properties file.
                    //We're just writing version info and the name of the template file that will be the main file initially
                    Properties properties = new Properties();
                    properties.setProperty(PovProject.KEY_MAINFILE, PovProject.SCENES_DIR + "/" + sceneFileName + ".pov");
                    properties.setProperty(PovProject.KEY_VERSION, Integer.toString (PovProject.VALUE_VERSION));
                    
                    //Write it to disk
                    FileLock lock = projectProps.lock();
                    try {
                        OutputStream stream = projectProps.getOutputStream(lock);
                        properties.store(stream, name + " povray project properties");
                        stream.flush();
                        stream.close();
                    } finally {
                        lock.releaseLock();
                    }
                    
                    //Return the newly created folder, to open it in the UI
                    return projectFolder;
               } catch (IOException ioe) {
                   
                   //Iterate any files created before the exception and 
                   //delete them
                   for (Iterator i=created.iterator(); i.hasNext();) {
                       try {
                           Object o = i.next();
                           FileObject fob;
                           if (o instanceof DataObject) {
                               fob = ((DataObject) o).getPrimaryFile();
                           } else {
                               fob = (FileObject) o;
                           }
                           fob.delete();
                       } catch (Exception e) {
                           ErrorManager.getDefault().notify(e);
                       }
                   }
                   
                   //Return the exception to rethrow it
                   return ioe;
               }
           } 
        });
        
        //Return or throw the exception
        if (result instanceof IOException) {
            throw ((IOException) result);
        } else {
            return (DataFolder) result;
        }
    }    
    
}
