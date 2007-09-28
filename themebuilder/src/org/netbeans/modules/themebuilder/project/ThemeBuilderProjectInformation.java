/*
 * ThemeBuilderProjectInformation.java
 *
 * Created on February 12, 2007, 6:50 PM
 */

package org.netbeans.modules.themebuilder.project;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.openide.util.Utilities;

/**
 * Theme Builder Project Information implementation
 * An instance is placed in the Project Lookup
 * @author Winston Prakash
 * @version 1.0
 */
public final class ThemeBuilderProjectInformation implements ProjectInformation{
    
    Project themeBuilderProject;
    
    /**
     * 
     * @param project 
     */
    public ThemeBuilderProjectInformation(Project project) {
        themeBuilderProject = project;
    }

    /**
     * 
     * @return 
     */
    public String getName() {
        return themeBuilderProject.getProjectDirectory().getName();
    }

    /**
     * 
     * @return 
     */
    public String getDisplayName() {
        return getName();
        
    }

    /**
     * 
     * @return 
     */
    public Icon getIcon() {
        return new ImageIcon (Utilities.loadImage(
                    "org/netbeans/modules/themebuilder/resources/themebuilder.png"));

    }

    /**
     * 
     * @return 
     */
    public Project getProject() {
        return themeBuilderProject;
    }

    /**
     * 
     * @param propertyChangeListener 
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        // Do nothing yet
    }

    /**
     * 
     * @param propertyChangeListener 
     */
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        // Do nothing yet
    }
    
}
