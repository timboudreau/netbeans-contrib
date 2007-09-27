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
    /** Project subdirectory where output images live */
    public static final String IMAGES_DIR = "images";
    
    private final FileObject projectDir;

    //Stuff that lives in the lookup for outside code to use
    private final MainFileProviderImpl mainFile = new MainFileProviderImpl();
    private final RendererServiceImpl renderer = new RendererServiceImpl(this);
    private final ViewServiceImpl viewer = new ViewServiceImpl(this);

    private final ProjectState state;
    
    public PovProject(FileObject projectDir, ProjectState state) {
        this.projectDir = projectDir;
        this.state = state;
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
     * Finds and loads $PROJECT_ROOT/pvproject/project.properties, where we
     * store configuration info. 
     */
    private Properties loadProperties() {
        FileObject fob = projectDir.getFileObject(PovProjectFactory.PROJECT_DIR 
                + "/" + PovProjectFactory.PROJECT_PROPFILE);
        
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
            return new ImageIcon (Utilities.loadImage(
                    "org/netbeans/modules/povproject/resources/PovRayIcon.gif"));
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
            //Try to look up the main file in the project properties
            //the first time this is called;  no need to look it up every
            //time, either it's there or it's not and when the user sets it
            //we'll save it when the project is closed
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
            //Sanity check
            assert file != null && 
                    file.getPath().startsWith(getProjectDirectory().getPath()) :
                    "Main file not under project";
            
            boolean change = ((mainFile == null) != (file == null)) ||
                    (mainFile != null && !mainFile.equals(file));
            
            mainFile = file;
            if (change) {
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
