/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.packager;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntBasedProjectType;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 * Factory for packager projects
 * @author Tim Boudreau
 */
public final class PackagerProjectType implements AntBasedProjectType {
    
    public static final String TYPE = "org.netbeans.modules.packager.PackagerProjectType";
    private static final String PROJECT_CONFIGURATION_NAME = "data";
    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/packager-project/1";
    
    /** Do nothing, just a service. */
    public PackagerProjectType() {}
    
    public String getType() {
        return TYPE;
    }
    
    public Project createProject(AntProjectHelper helper) throws IOException {
        return new PackagerProject(helper);
    }

    public String getPrimaryConfigurationDataElementName(boolean shared) {
        return PROJECT_CONFIGURATION_NAME;
    }
    
    public String getPrimaryConfigurationDataElementNamespace(boolean shared) {
        return PROJECT_CONFIGURATION_NAMESPACE;
    }
    
}
