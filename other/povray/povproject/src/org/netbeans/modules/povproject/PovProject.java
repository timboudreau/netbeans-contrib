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
 * PovProject.java
 *
 * Created on February 16, 2005, 3:43 PM
 */

package org.netbeans.modules.povproject;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;
import javax.swing.ImageIcon;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.povproject.RendererService;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;

import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

/**
 * A simple povray project.
 *
 * @author Timothy Boudreau
 */
public final class PovProject implements Project {
    /** Properties key for the main file, the one which will render on build project */
    public static final String KEY_MAINFILE = "main.file";
    /** Properties key for the version, in case we make changes to what's in the 
     * properties in the future */
    public static final String KEY_VERSION = "nbpov.version";
    /** Current version */
    public static final int VALUE_VERSION = 1;
    /** Project subdirectory where .pov & .inc files live */
    public static final String SCENES_DIR = "scenes";
    /** Project subdirectory where output images live */
    public static final String IMAGES_DIR = "images";
    
    private final FileObject projectDir;

    //Stuff that lives in the lookup for outside code to use
    private final MainFileProviderImpl mainFile;
    private final RendererServiceImpl renderer;
    private final ViewServiceImpl viewer;
    private final ProjectState state;
    
    public PovProject(FileObject projectDir, ProjectState state) {
        this.projectDir = projectDir;
        this.state = state;
        mainFile = new MainFileProviderImpl();
        renderer = new RendererServiceImpl(this);
        viewer = new ViewServiceImpl(this);
    }

    public FileObject getProjectDirectory() {
        return projectDir;
    }
    
    public Lookup getLookup() {
        return Lookups.fixed(new Object[] {
            this,  //project docs require a project be in its lookup
            state, //allow outside code to mark the project as needing saving
            mainFile, //allow outside code to change which file is the main file
            viewer, //allow outside code to ask the project to show an image file
            renderer, //allow outside code to tell the project to render a file
            new ActionProviderImpl(), //Provides standard actions like Build
            loadProperties(), //The project properties
            new Info(), //Project information implementation
            new PrivilegedTemplatesImpl(), //Privileged templates on new menu implementation
            new PovrayLogicalView(this), //Logical view of project implementation
        }); 
    }
    
    /**
     * Fetches the scenes folder, creating it if necessary. 
     */
    FileObject getScenesFolder(boolean create) {
        FileObject result = 
            projectDir.getFileObject(PovProjectFactory.SCENES_DIR);
        if (result == null && create) {
            try {
                result = projectDir.createFolder (PovProjectFactory.SCENES_DIR);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }
        }
        return result;
    }

    /**
     * Finds and loads $PROJECT_ROOT/povproject/project.properties, where we
     * store configuration info. 
     */
    private Properties loadProperties() {
        FileObject fob = projectDir.getFileObject(PovProjectFactory.PROJECT_DIR + "/" + PovProjectFactory.PROJECT_PROPFILE);
        Properties result = new Properties();
        if (fob != null) {
            try {
                result.load(fob.getInputStream());
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        return result;
    }
    
    /** Implementation of project system's ProjectInformation class */
    private final class Info implements ProjectInformation {
        public Icon getIcon() {
            return new ImageIcon (Utilities.loadImage("org/netbeans/modules/povproject/resources/PovRayIcon.gif"));
        }
        
        public String getName() {
            return getProjectDirectory().getName();
        }
        
        public String getDisplayName() {
            return getName();
        }
        
        public void addPropertyChangeListener (PropertyChangeListener pcl) {
            //do nothing, won't change
        }
        
        public void removePropertyChangeListener (PropertyChangeListener pcl) {
            //do nothing, won't change
        }
        
        public Project getProject() {
            return PovProject.this;
        }
    }
    
    /**
     * Implementation of our own API, MainFileProvider.  Nodes will use this
     * service to allow a menu item to mark which file should get rendered when
     * you render a project.
     */
    private final class MainFileProviderImpl implements MainFileProvider {
        private FileObject mainFile = null;
        boolean checked = false;
        public FileObject getMainFile() {
            if (mainFile == null && !checked) {
                checked = true;
                Properties props = (Properties) getLookup().lookup(Properties.class);
                String path = props.getProperty(KEY_MAINFILE);
                if (path != null) {
                    mainFile = projectDir.getFileObject(path);
                }
            }
            if (mainFile != null && !mainFile.isValid()) {
                return null;
            }
            return mainFile;
        }

        public void setMainFile(FileObject file) {
            assert file != null && file.getPath().startsWith(getProjectDirectory().getPath()) : "Main file not under project";
            mainFile = file;
            if (mainFile != null) {
                state.markModified();
            }
        }
    }

    /**
     * Action provider implementation that executes common project actions like
     * Build.
     */
    private final class ActionProviderImpl implements ActionProvider {
        public String[] getSupportedActions() {
            return new String[] {
                COMMAND_BUILD, COMMAND_CLEAN, COMMAND_REBUILD,
            };
        }

        public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
            if (ActionProvider.COMMAND_BUILD.equals(command)) {

                renderer.render();
                
            } else if (ActionProvider.COMMAND_CLEAN.equals(command)) {
                FileObject images = getProjectDirectory().getFileObject (IMAGES_DIR);
                if (images != null) {
                    try {
                        images.delete();
                    } catch (IOException ioe) {
                        ErrorManager.getDefault().notify(
                                ErrorManager.INFORMATIONAL, ioe);
                        
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            } else if (ActionProvider.COMMAND_REBUILD.equals(command)) {
                invokeAction (ActionProvider.COMMAND_CLEAN, context);
                invokeAction (ActionProvider.COMMAND_BUILD, context);
            }
        } 

        public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
            if (COMMAND_BUILD.equals(command)) {
                return mainFile.getMainFile() != null;
            } else if (COMMAND_CLEAN.equals(command)) {
                return getProjectDirectory().getFileObject (IMAGES_DIR) != null;

            }
            return true;
        }
    }
        

    /** Adds some stuff to the new menu by default */
    private static final class PrivilegedTemplatesImpl implements PrivilegedTemplates {

        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/Povray/Empty.pov",
            "Templates/Povray/Empty.inc",
        };

        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }

    }
}
