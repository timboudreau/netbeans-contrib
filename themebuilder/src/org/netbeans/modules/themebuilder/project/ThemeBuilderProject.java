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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
 * ThemeBuilderProject.java
 *
 * Created on February 12, 2007, 4:50 PM
 */

package org.netbeans.modules.themebuilder.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.themebuilder.project.action.ThemeBuilderActionProvider;
import org.netbeans.modules.themebuilder.project.view.ThemeBuilderViewProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Theme Builder Project
 * @author Winston Prakash
 * @version 1.0
 */
public final class ThemeBuilderProject implements Project{
    
    /**
     * Project sub directory containing src files
     */
    public static final String SRC_DIR = "src"; //NOI18N
    
    /**
     * Project sub directory containing CSS files
     */
    public static final String CSS_DIR = "css"; //NOI18N
    
    /**
     * Project sub directory containing JavaScript files
     */
    public static final String JAVASCRIPT_DIR = "javascript"; //NOI18N
    
    /**
     * Project sub directory containing Image files
     */
    public static final String IMAGES_DIR = "images"; //NOI18N
    
    /**
     * Project sub directory containing Message files
     */
    public static final String MESSAGES_DIR = "messages"; //NOI18N
    
    /**
     * Project sub directory containing Message files
     */
    public static final String PROPERTIES_DIR = "properties"; //NOI18N
    
    private Lookup projectLookup;
    private final FileObject projectDir;
    private final FileObject srcDir;
    
    private AntProjectHelper helper;
    
    /**
     *
     * @param helper
     * @throws java.io.IOException
     */
    public ThemeBuilderProject(final AntProjectHelper helper) throws IOException {
        this.helper = helper;
        projectDir = helper.getProjectDirectory();
        projectLookup = createLookup();
        srcDir = getSrcFolder();
    }
    
    /**
     *
     * @return
     */
    public FileObject getProjectDirectory() {
        return projectDir;
    }
    
    /**
     * Get the Project Look up
     * The Capabilities of the project are registered via Lookup
     * @return
     */
    public Lookup getLookup() {
        return projectLookup;
    }
    
    private Lookup createLookup() {
        Lookup baseLookup = Lookups.fixed(new Object[] {
            this,  //project spec requires a project be in its own Lookup
            new ThemeBuilderActionProvider(this), //Provides standard project actions
            new ThemeBuilderProjectInformation(this), //Project information implementation
            new ThemeBuilderViewProvider(this), //Logical View Provider implementation
            new ThemeBuilderProjectOpenedHook(this), // Project Open/Close hookup implementation
            //new ThemeBuilderCustomizerProvider(this), // Project Customizer implementation
            //new ThemeBuilderRecommendedTemplates() // // Recommended templates implementation
        });
        //return LookupProviderSupport.createCompositeLookup(base, "Projects/org-netbeans-modules-themebuilder-project/Lookup"); //NOI18N
        return baseLookup;
    }
    
    private FileObject findFolder(FileObject root, String folderName){
        FileObject folder = null;
        FileObject[] children = root.getChildren();
        for(FileObject child : children){
            if (child.isFolder()){
                if (child.getName().equals(folderName)){
                    return child;
                }else{
                  folder = findFolder(child, folderName);
                  if (folder != null){
                      return folder;
                  }
                }
            }
        }
        return folder;
    }
    
    public FileObject getSrcFolder() {
        return findFolder(projectDir, SRC_DIR);
    }
    
    /**
     * Get the File Object corresponding to CSS folder
     * @param create
     * @return FileObject
     */
    public FileObject getCssFolder() {
        return findFolder(srcDir, CSS_DIR);
    }
    
    /**
     * Get the File Object corresponding to Image folder
     * @param create
     * @return
     */
    public FileObject getImagesFolder() {
        return findFolder(srcDir, IMAGES_DIR);
    }
    
    /**
     * Get the File Object corresponding to Java Script folder
     * @param create
     * @return
     */
    public FileObject getJavaScriptFolder() {
        return findFolder(srcDir, JAVASCRIPT_DIR);
    }
    
    /**
     * Get the File Object corresponding to Message folder
     * @param create
     * @return
     */
    public FileObject getMessagesFolder() {
        return  findFolder(srcDir, MESSAGES_DIR);
    }
    
    /**
     * Get the File Object corresponding to Properties folder
     * @param create
     * @return FileObject
     */
    public FileObject getPropertiesFolder() {
        return findFolder(srcDir, PROPERTIES_DIR);
    }
}
