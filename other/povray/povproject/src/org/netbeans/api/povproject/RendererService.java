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
 * RendererService.java
 *
 * Created on February 17, 2005, 3:17 PM
 */

package org.netbeans.api.povproject;

import java.io.File;
import java.util.Properties;

/**
 * Interface available from a povray project's lookup, which can be used to
 * render a scene file or ther project.
 *
 * @author Timothy Boudreau
 */
public interface RendererService {
    /** 
     * Render a scene with the passed Properties as renderer properties
     * for povray.  The keys and values should simply be POV-Ray line 
     * switches without the leading + character.  I.e.
     * <pre>
     * settings.setProperty ("W", "320");
     * settings.setProperty ("H", "200");
     * </pre>
     * to set 320x200 resolution for a render.  The full set of arguments
     * required to do a render must be included, <i>except</i> +I and +O
     * for input and output files (you're passing the file anyway, the
     * project knows where to put the output), and +L for the standard
     * include files dir (the service will ask for it and store it).
     */
    File render (File scene, Properties renderSettings);
    
    /**
     * Render a file using default render settings.
     */
    File render (File scene);
    
    /**
     * Render the main file of the project using default settings.
     */
    File render();
    
    /**
     * Gets the renderer settings (which may have been customized by the
     * project).  The Properties object returned is a copy which can be
     * modified freely.
     */
    Properties getProjectRenderProperties();
    
    /** 
     * Get the global default renderer settings (what is used if there are
     * no customizations).  The Properties object returned is a copy which
     * can be modified freely.
     */
    Properties getGlobalRenderProperties();
}
