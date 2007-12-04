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


package org.netbeans.modules.j2ee.hk2;

import java.io.File;
import java.io.InputStream;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.hk2.ide.FastDeploy;
import org.netbeans.modules.j2ee.hk2.ide.Hk2ManagerImpl;
import org.netbeans.modules.j2ee.hk2.ide.Hk2PluginProperties;
import org.netbeans.modules.j2ee.hk2.ide.Hk2Target;
import org.netbeans.modules.j2ee.hk2.ide.Hk2TargetModuleID;
/**
 *
 * @author Ludo
 */
public class Hk2DeploymentManager implements DeploymentManager {

    
    
    private InstanceProperties instanceProperties;

    private Hk2PluginProperties ip;

    private String uname;

    private String passwd;

    private String uri;
    
    /**
     * 
     * @param uri 
     * @param uname 
     * @param passwd 
     */
    public Hk2DeploymentManager(String uri, String uname, String passwd) {
        this.uri = uri;
        this.uname = uname;
        this.passwd = passwd;
        ip = new Hk2PluginProperties(this);
    }
        
    
    /**
     * 
     * @param target 
     * @param file 
     * @param file2 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject distribute(Target[] target, File file, File file2) throws IllegalStateException {
        return null;
    }

    /**
     * 
     * @param deployableObject 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.InvalidModuleException 
     */
    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
//        System.out.println("in createConfiguration"+deployableObject);
        return new Hk2Configuration(deployableObject);
    }

    /**
     * 
     * @param targetModuleID 
     * @param inputStream 
     * @param inputStream2 
     * @return 
     * @throws java.lang.UnsupportedOperationException 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, InputStream inputStream, InputStream inputStream2) throws UnsupportedOperationException, IllegalStateException {
        return null;
    }

    /**
     * 
     * @param target 
     * @param inputStream 
     * @param inputStream2 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject distribute(Target[] target, InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        return null;
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject undeploy(TargetModuleID[] targetModuleID) throws IllegalStateException {
        Hk2ManagerImpl g= new Hk2ManagerImpl(this);
        g.undeploy((Hk2TargetModuleID)targetModuleID[0]);
        return g;
 
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject stop(TargetModuleID[] targetModuleID) throws IllegalStateException {
        FastDeploy g= new FastDeploy(this);
        return g.dummyProgressObject((Hk2TargetModuleID)targetModuleID[0]);
       
    }

    /**
     * 
     * @param targetModuleID 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject start(TargetModuleID[] targetModuleID) throws IllegalStateException {
        FastDeploy g= new FastDeploy(this);
        return g.dummyProgressObject((Hk2TargetModuleID)targetModuleID[0]);
    }

    /**
     * 
     * @param locale 
     * @throws java.lang.UnsupportedOperationException 
     */
    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
    }

    /**
     * 
     * @param locale 
     * @return 
     */
    public boolean isLocaleSupported(java.util.Locale locale) {
        return false;
    }

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        Hk2ManagerImpl g= new Hk2ManagerImpl(this);
        return g.getTargetModuleID(target[0]);
    }

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        return null;
    }

    /**
     * 
     * @param moduleType 
     * @param target 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException 
     * @throws java.lang.IllegalStateException 
     */
    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        Hk2ManagerImpl g= new Hk2ManagerImpl(this);
        return g.getTargetModuleID(target[0]);
    }

    /**
     * 
     * @param targetModuleID 
     * @param file 
     * @param file2 
     * @return 
     * @throws java.lang.UnsupportedOperationException 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, File file2) throws UnsupportedOperationException, IllegalStateException {
        return null;
    }

    /**
     * 
     * @param dConfigBeanVersionType 
     * @throws javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException 
     */
    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType) throws DConfigBeanVersionUnsupportedException {
    }

    /**
     * 
     * @param dConfigBeanVersionType 
     * @return 
     */
    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        return false;
    }

    /**
     * 
     */
    public void release() {
    }

    /**
     * 
     * @return 
     */
    public boolean isRedeploySupported() {
        return true;
    }

    /**
     * 
     * @return 
     */
    public java.util.Locale getCurrentLocale() {
        return null;
    }

    /**
     * 
     * @return 
     */
    public DConfigBeanVersionType getDConfigBeanVersion() {
        return null;
    }

    /**
     * 
     * @return 
     */
    public java.util.Locale getDefaultLocale() {
        return null;
    }

    /**
     * 
     * @return 
     */
    public java.util.Locale[] getSupportedLocales() {
        return null;
    }

    /**
     * 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public Target[] getTargets() throws IllegalStateException {
       String s=  "http://" + getInstanceProperties().getProperty(Hk2PluginProperties.PROPERTY_HOST) + ":" + getInstanceProperties().getProperty(InstanceProperties.HTTP_PORT_NUMBER ); //NOI18N

        Hk2Target target = new Hk2Target(s);
        Hk2Target targets[] = {target};
        return targets;
    }

    /**
     * 
     * @return 
     */
    public String getUri() {
        return uri;
    }
    
    /**
     * 
     * @return 
     */
    public Hk2PluginProperties getProperties() {
        return ip;
    }
    
    /**
     * 
     * @return 
     */
    public InstanceProperties getInstanceProperties() {
        if (instanceProperties == null)
            instanceProperties = InstanceProperties.getInstanceProperties(getUri());
        
        return instanceProperties;
    }
    
    
        
    /** Returns URI of GF (manager application).
     * @return URI without home and base specification
     */
    public String getPlainUri () {
       
    
       String s=  "http://" + getInstanceProperties().getProperty(Hk2PluginProperties.PROPERTY_HOST) + ":" + getInstanceProperties().getProperty(InstanceProperties.HTTP_PORT_NUMBER )+ "/__asadmin/"; //NOI18N
//System.out.println("getPlainUri"+s);
return s;
    }
    
    /** Returns URI of hk2.
     * @return URI without home and base specification
     */
    public String getServerUri () {
        return "http://" + getInstanceProperties().getProperty(Hk2PluginProperties.PROPERTY_HOST) + ":" + getInstanceProperties().getProperty(Hk2PluginProperties.PROPERTY_ADMIN_PORT); //NOI18N
    }

    /**
     * 
     * @param arg0 
     * @param arg1 
     * @param arg2 
     * @param arg3 
     * @return 
     * @throws java.lang.IllegalStateException 
     */
    public ProgressObject distribute(Target[] arg0, ModuleType arg1,
                                     InputStream arg2, InputStream arg3) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
