/*
 * ThemeBuilderRecommendedTemplates.java
 *
 * Created on March 2, 2007, 5:35 PM
 *
 */

package org.netbeans.modules.themebuilder.project;

import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;

/**
 * List of template types supported by a Theme Builder project when making a new file.
 * An instance should be placed in Project Lookup to affect the recommended template list
 * of Theme Builder Project.
 * @author winstonp
 */
public class ThemeBuilderRecommendedTemplates implements RecommendedTemplates, PrivilegedTemplates{
    
    /** Creates a new instance of ThemeBuilderRecommendedTemplates */
    public ThemeBuilderRecommendedTemplates() {
    }
    
    public String[] getRecommendedTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getPrivilegedTemplates() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
