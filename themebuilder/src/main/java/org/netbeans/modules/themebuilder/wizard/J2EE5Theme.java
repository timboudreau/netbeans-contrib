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
 * ThemeImpl.java
 *
 * Created on December 20, 2006, 5:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.themebuilder.wizard;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * A singleton implementation of theme for J2EE 1.4 projects.
 *
 * @author gjmurphy
 */
class J2EE5Theme extends Theme {
    
    final static private String[] propertiesAttributeNames = {
        "X-SJWUIC-Theme-ClassMapper",
        "X-SJWUIC-Theme-Messages",
        "X-SJWUIC-Theme-Images",
        "X-SJWUIC-Theme-JavaScript",
        "X-SJWUIC-Theme-Stylesheets",
        "X-SJWUIC-Theme-Templates"
    };
    
    private static Theme theme;
    
    public static Theme getTheme() {
        if (theme == null)
            theme = new J2EE5Theme();
        return theme;
    }
    
    private J2EE5Theme() {
    }
    
    Set<String> propertiesAttributeNameSet;
    
    public Set<String> getPropertiesAttributeNames() {
        if (propertiesAttributeNameSet == null) {
            propertiesAttributeNameSet = new AbstractSet<String>() {
                
                Iterator<String> iterator;
                
                public Iterator<String> iterator() {
                    if (iterator == null) {
                        iterator = Arrays.asList(propertiesAttributeNames).iterator();
                    }
                    return iterator;
                }

                public int size() {
                    return propertiesAttributeNames.length;
                }
                
            };
        }
        return propertiesAttributeNameSet;
    }

    public String getAttributeSectionName() {
        return "com/sun/webui/jsf/theme/";
    }
    
}
