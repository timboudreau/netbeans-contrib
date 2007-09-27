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
    /** Prefix prepended to project properties that are really line
     * switches for POV-Ray.  The string is &quot;renderer.&quot; - so,
     * to change the width of the production quality render of a project,
     * include &quot;renderer.W=2048&quot; in the project.properties file.
     */
    public static final String PROJECT_RENDERER_KEY_PREFIX = "renderer.";
    
    /** 
     * Render a scene with the passed Properties as renderer properties
     * for povray.  The keys and values should simply be POV-Ray line 
     * switches without the leading + character.  I.e.
     * <pre>
     * settings.setProperty ("W", "320");
     * settings.setProperty ("H", "200");
     * </pre>
     * to set 320x200 resolution for a render (the result is that 
     * &quot;+W320 +H200&quot; are passed on the command line to POV-Ray.  
     * The full set of arguments
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
     * Get localized, human-readable names for the available settings.
     * The first element in the list is a special value, in english,
     * "Production".  The Production settings are a merge of the 
     * default 1024x768 high quality settings and any settings in the
     * project.properties file that override them.
     * <p>
     * In the project.properties file, to avoid conflicts, the text
     * &quot;renderer.&quot; is prepended to any production renderer 
     * settings.  So, to override the width and height of a production
     * render in the project settings, you would add, e.g.
     * <pre>
     * renderer.W=4000
     * renderer.H=3000
     * </pre>
     * and the result would be the default quality settings for 1024x768hq,
     * but with the width and height changed to 4000x3000.
     */
    public String[] getAvailableRendererSettings();
    
    /**
     * Fetch a properties object representing a named set of renderer settings.
     * <code>getRendererSettings (getAvailableRendererSettings()[0]) will return
     * the production settings, which are specific to the project.
     */
    public Properties getRendererSettings (String name);
    
    /**
     * Get the last-used renderer settings.
     */
    public String getPreferredConfigurationName();
    
}
