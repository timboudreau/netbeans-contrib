/*
 * ThemeBuilderProjectType.java
 *
 * Created on March 2, 2007, 6:24 PM
 */

package org.netbeans.modules.themebuilder.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntBasedProjectType;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 * Ant based project type for Theme Builder.
 * @author winstonp
 */
public class ThemeBuilderProjectType implements AntBasedProjectType{
    
    /**
     * The project type for Theme Builder
     */
    public static final String PROJECT_TYPE = "org.netbeans.modules.themebuilder.project";
    /**
     * Project Configuration name  
     */
    private static final String PROJECT_CONFIGURATION_NAME = "data"; // NOI18N
    /**
     * Project Configuration name Space
     */
    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/themebuilder/3"; // NOI18N
     
    
    public String getType() {
        return PROJECT_TYPE;
    }

    public Project createProject(AntProjectHelper helper) throws IOException {
        return new ThemeBuilderProject (helper);         
    }

    public String getPrimaryConfigurationDataElementName(boolean shared) {
        return PROJECT_CONFIGURATION_NAME;
    }

    public String getPrimaryConfigurationDataElementNamespace(boolean shared) {
        return PROJECT_CONFIGURATION_NAMESPACE;
    }

}
