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


package org.netbeans.modules.j2ee.jetty;

import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

/**
 * This class represents a web module that can run on Jetty server
 * @author novakm
 */
public class JetModule implements TargetModuleID {

    private JetTarget target;
    private final String warPath;
    private final String contextPath;
    private final String name;
    private final String objectNameString;

    public String getName() {
        return name;
    }

 /**
  * Constructor that sets instance variables according to given parameters
  * @param target - target server
  * @param name - name of the web module
  * @param objectNameString - ObjectName of the webmodule on the server converted to String
  * @param contextPath - contextPath on which is this module running
  * @param warPath - path to the war archive
  */
    public JetModule(Target target, String name, String objectNameString, String contextPath, String warPath) {
        this.target = (JetTarget) target;
        this.warPath = warPath;
        this.contextPath = contextPath;
        this.name = name;
        this.objectNameString=objectNameString;
    }
    
  /**
   * Constructor called with some parameters unknown, they have to be
   * discovered later
   * @param target
   * @param contextPath
   */
    public JetModule(Target target, String contextPath) {
        this(target, null, null, contextPath, null);
    }

    /** 
     * Returns context path path of this module.
     * @return contextPath
     */
    public String getContextPath() {
        return contextPath;
    }
    
    /**
     * Returns target server
     * @return target
     */
    public JetTarget getTarget() {
        return target;
    }
    
    /**
     * Returns path to war archive
     * @return warPath
     */
    public String getWarPath() {
        return warPath;
    }
    
    /**
     * Returns unique identification of the module
     * @return webURL of the module
     */
    public String getModuleID() {
        return getWebURL();
    }

    /**
     * Returns webURL on which the module can be found
     * @return webURL of the module
     */
    public String getWebURL() {
        return "http://localhost:8080"+contextPath; //NOI18N
    }
    
    public String getObjectNameString() {
        return objectNameString;
    }

    /**
     * Only for EAR archives that are not supported
     * @return null
     */
    public TargetModuleID getParentTargetModuleID() {
		return null;
    }
    
    /**
     * Only for EAR archives that are not supported
     * @return null
     */
    public TargetModuleID[] getChildTargetModuleID() {
		return null;
    }

}