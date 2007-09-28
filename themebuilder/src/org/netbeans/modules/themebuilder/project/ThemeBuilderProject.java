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
    
    /**
     * Get the File Object corresponding to CSS folder
     * @param create
     * @return FileObject
     */
    public FileObject getCssFolder() {
        return findFolder(projectDir, CSS_DIR);
    }
    
    /**
     * Get the File Object corresponding to Image folder
     * @param create
     * @return
     */
    public FileObject getImagesFolder() {
        return findFolder(projectDir, IMAGES_DIR);
    }
    
    /**
     * Get the File Object corresponding to Java Script folder
     * @param create
     * @return
     */
    public FileObject getJavaScriptFolder() {
        return findFolder(projectDir, JAVASCRIPT_DIR);
    }
    
    /**
     * Get the File Object corresponding to Message folder
     * @param create
     * @return
     */
    public FileObject getMessagesFolder() {
        return  findFolder(projectDir, MESSAGES_DIR);
    }
    
    /**
     * Get the File Object corresponding to Properties folder
     * @param create
     * @return FileObject
     */
    public FileObject getPropertiesFolder() {
        return findFolder(projectDir, PROPERTIES_DIR);
    }
}
