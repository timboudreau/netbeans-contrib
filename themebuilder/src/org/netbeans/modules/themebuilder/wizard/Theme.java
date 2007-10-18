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
 * Theme.java
 *
 * Created on December 11, 2006, 8:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.themebuilder.wizard;

import java.util.Set;

/**
 * The theme class serves as a container for data and metadata specific to a version
 * of a theme.
 *
 * @author gjmurphy
 */
public abstract class Theme {
    
    public static enum Version { 
        
        J2EE1_4("J2EE 1.4"), J2EE5("J2EE 5");
        
        private final String displayName;
        
        Version(String displayName) {
            this.displayName = displayName;
        }
        
        public String toString() {
            return this.displayName;
        }
        
    };
    
    
    public static Theme getTheme(Theme.Version version) {
        switch (version) {
            case J2EE1_4:
                return J2EE1_4Theme.getTheme();
            default:
                return J2EE5Theme.getTheme();
        }
    }
    
    /**
     * Returns the name of the theme attributes section that contains the defining
     * attributes of a theme manifest. A manifest is considered to be a valid
     * theme manifest if it contains this names attributes section.
     */
    abstract public String getAttributeSectionName();

    
    private static final String VERSION_ATTRIBUTE_NAME = "X-SJWUIC-Theme-Version";
    
    public String getVersionAttributeName() {
        return VERSION_ATTRIBUTE_NAME;
    }

    
    private static final String NAME_ATTRIBUTE_NAME = "X-SJWUIC-Theme-Name";
    
    public String getNameAttributeName() {
        return NAME_ATTRIBUTE_NAME;
    }
    
    private static final String PREFIX_ATTRIBUTE_NAME = "X-SJWUIC-Theme-Prefix";

    public String getPrefixAttributeName() {
        return PREFIX_ATTRIBUTE_NAME;
    }
    
    /**
     * Returns a set of the theme attribute names for all properties files used
     * to reference theme data.
     */
    abstract public Set<String> getPropertiesAttributeNames();
    
    
}
