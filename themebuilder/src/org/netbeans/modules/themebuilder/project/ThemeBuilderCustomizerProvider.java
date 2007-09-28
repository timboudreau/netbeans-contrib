/*
 * ThemeBuilderCustomizerProvider.java
 *
 * Created on February 15, 2007, 10:18 AM
 */

package org.netbeans.modules.themebuilder.project;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.CustomizerProvider;

/**
 * Customizer for the Theme Builder Project
 * @author Winston Prakash
 * @version 1.0
 */
public final class ThemeBuilderCustomizerProvider implements CustomizerProvider{
    
    private ThemeBuilderProject  themeBuilderproject;
    /**
     * 
     * @param project 
     */
    public ThemeBuilderCustomizerProvider(Project project) {
        themeBuilderproject = (ThemeBuilderProject) project;
    }
    
    public void showCustomizer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
