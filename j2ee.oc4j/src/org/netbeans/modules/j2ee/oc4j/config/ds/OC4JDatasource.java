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

package org.netbeans.modules.j2ee.oc4j.config.ds;

import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.openide.util.NbBundle;

/**
 *
 * @author Michal Mocnak
 */
public class OC4JDatasource implements Datasource {
    
    private String jndiName;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String minPoolSize = "5"; // NOI18N
    private String maxPoolSize = "20"; // NOI18N
    private String idleTimeoutMinutes = "5"; // NOI18N
    private String description;
    
    private volatile int hash = -1;
    
    /** Creates a new instance of OC4JDatasource */
    public OC4JDatasource(String jndiName, String url, String username, String password, String driverClassName) {
        this.jndiName = jndiName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }
    
    public String getJndiName() {
        return jndiName;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getDriverClassName() {
        return driverClassName;
    }
    
    public String getMinPoolSize() {
        return minPoolSize;
    }
    
    public String getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public String getIdleTimeoutMinutes() {
        return idleTimeoutMinutes;
    }
    
    public String getDisplayName() {
        if (description == null) {
            description = getJndiName() + " [" + getUrl() + "]";
        }
        return description;
    }
    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof OC4JDatasource))
            return false;
        
        OC4JDatasource ds = (OC4JDatasource) obj;
        if (jndiName == null && ds.getJndiName() != null || jndiName != null && !jndiName.equals(ds.getJndiName()))
            return false;
        if (url == null && ds.getUrl() != null || url != null && !url.equals(ds.getUrl()))
            return false;
        if (username == null && ds.getUsername() != null || username != null && !username.equals(ds.getUsername()))
            return false;
        if (password == null && ds.getPassword() != null || password != null && !password.equals(ds.getPassword()))
            return false;
        if (driverClassName == null && ds.getDriverClassName() != null || driverClassName != null && !driverClassName.equals(ds.getDriverClassName()))
            return false;
        if (minPoolSize == null && ds.getMinPoolSize() != null ||  minPoolSize != null && !minPoolSize.equals(ds.getMinPoolSize()))
            return false;
        if (maxPoolSize == null && ds.getMaxPoolSize() != null || maxPoolSize != null && !maxPoolSize.equals(ds.getMaxPoolSize()))
            return false;
        if (idleTimeoutMinutes == null && ds.getIdleTimeoutMinutes() != null || idleTimeoutMinutes != null && !idleTimeoutMinutes.equals(ds.getIdleTimeoutMinutes()))
            return false;
        
        return true;
    }
    
    public int hashCode() {
        if (hash == -1) {
            int result = 17;
            result += 37 * result + (jndiName == null ? 0 : jndiName.hashCode());
            result += 37 * result + (url == null ? 0 : url.hashCode());
            result += 37 * result + (username == null ? 0 : username.hashCode());
            result += 37 * result + (password == null ? 0 : password.hashCode());
            result += 37 * result + (driverClassName == null ? 0 : driverClassName.hashCode());
            result += 37 * result + (minPoolSize == null ? 0 : minPoolSize.hashCode());
            result += 37 * result + (maxPoolSize == null ? 0 : maxPoolSize.hashCode());
            result += 37 * result + (idleTimeoutMinutes == null ? 0 : idleTimeoutMinutes.hashCode());
            
            hash = result;
        }
        
        return hash;
    }
    
    public String toString() {
        return "[ " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_JNDI") + ": '" + jndiName + "', " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_URL") + ": '" + url +  "', " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_USER") + ": '" +  username +  "', " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_PASS") + ": '" + password +  "', " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_DRV") + ": '" + driverClassName +  "', " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_MINPS") + ": '" + minPoolSize +  "', " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_MAXPS") + ": '" + maxPoolSize +  "', " + // NOI18N
                NbBundle.getMessage(OC4JDatasource.class, "LBL_DS_IDLE") + ": '" + idleTimeoutMinutes +  "' ]"; // NOI18N
    }
}