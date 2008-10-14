/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.websynergy.servicebuilder.helper;

import java.io.IOException;
import org.netbeans.modules.j2ee.dd.api.common.CommonDDBean;
import org.netbeans.modules.j2ee.dd.api.common.CreateCapability;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Listener;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class WebXmlHelper {
    
    private static String CONTEXT_LOADER_LISTENER = "com.liferay.portal.kernel.spring.context.PortletContextLoaderListener";
    private static String CONTEXT_CLASS = "com.liferay.portal.spring.context.PortletApplicationContext";
    private static String CONTEXT_CLASS_PARAM_NAME = "contextClass";
    private static String CONTEXT_CONFIG_LOC_NAME = "contextConfigLocation";
    //private static String CONTEXT_CONFIG_LOC_VALUE = "WEB-INF/classes/META-INF/portlet-spring.xml,WEB-INF/classes/META-INF/ext-spring.xml,WEB-INF/classes/META-INF/portlet-model-hints.xml,WEB-INF/classes/META-INF/infrastructure-spring.xml,WEB-INF/classes/META-INF/data-source-spring.xml";
    private static String CONTEXT_CONFIG_LOC_VALUE = "WEB-INF/classes/META-INF/misc-spring.xml,WEB-INF/classes/META-INF/data-source-spring.xml,WEB-INF/classes/META-INF/base-spring.xml,WEB-INF/classes/META-INF/hibernate-spring.xml,WEB-INF/classes/META-INF/infrastructure-spring.xml,WEB-INF/classes/META-INF/portlet-spring.xml,WEB-INF/classes/META-INF/ext-spring.xml";
    public WebXmlHelper() {
    }

    public static void addServiceBuilderParams(WebModule wm) {
        
        CreateWebXMLConfig createWebXMLConfig = new CreateWebXMLConfig(wm);
        FileObject webInf = wm.getWebInf();
        if (webInf != null) {
            try {
                FileSystem fs = webInf.getFileSystem();
                fs.runAtomicAction(createWebXMLConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class CreateWebXMLConfig implements FileSystem.AtomicAction {

        private WebModule wm;

        public CreateWebXMLConfig(WebModule wm) {

            this.wm = wm;
        }

        public void run() throws IOException {
            FileObject dd = wm.getDeploymentDescriptor();
            WebApp ddRoot = DDProvider.getDefault().getDDRoot(dd);
            
            boolean isAdded = false;
            if(addListener(ddRoot))
                isAdded = true;
            if(addContextClassParam(ddRoot))
                isAdded = true;
            if(addConfigLocationParam(ddRoot))
                 isAdded = true;
            
            if(isAdded)
                ddRoot.write(dd);
            
        }
        
        private boolean addListener(WebApp ddRoot) {
            
            Listener[] listeners = ddRoot.getListener();
            
            for(Listener l:listeners) {
                if(l.getListenerClass().equals(CONTEXT_LOADER_LISTENER)) {
                    return false;
                }
            }
            
            Listener listener = null; // NOI18N
            try {
                listener = (Listener) createBean(ddRoot, "Listener");
                listener.setListenerClass(CONTEXT_LOADER_LISTENER);
                ddRoot.addListener(listener);
            } catch (IOException ex) {
                //Exceptions.printStackTrace(ex);
                ex.printStackTrace();
            }
            return true;
        }
        
        private boolean addContextClassParam(WebApp ddRoot) {
            
            InitParam[] initParams = ddRoot.getContextParam();
            for(InitParam param:initParams) {
                
                if(param.getParamName().equals(CONTEXT_CLASS_PARAM_NAME)
                        && param.getParamValue().equals(CONTEXT_CLASS))
                    return false;
            }
            
            InitParam initParam = null;
            try {
                initParam = (InitParam) createBean(ddRoot, "InitParam");
                initParam.setParamName(CONTEXT_CLASS_PARAM_NAME);
                initParam.setParamValue(CONTEXT_CLASS);
                ddRoot.addContextParam(initParam);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
     
            return true;
        }
        
        private boolean addConfigLocationParam(WebApp ddRoot) {
            InitParam[] initParams = ddRoot.getContextParam();
            for(InitParam param:initParams) {
                
                if(param.getParamName().equals(CONTEXT_CONFIG_LOC_NAME)
                        && param.getParamValue().indexOf("portlet-spring") != -1)
                    return false;
            }
            
            InitParam initParam = null;
            try {
                
                initParam = (InitParam) createBean(ddRoot, "InitParam");
                initParam.setParamName(CONTEXT_CONFIG_LOC_NAME);
                initParam.setParamValue(CONTEXT_CONFIG_LOC_VALUE);
                ddRoot.addContextParam(initParam);
                
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            
            return true;
        }
        
        protected CommonDDBean createBean(CreateCapability creator, String beanName) throws IOException {
            CommonDDBean bean = null;
            try {
                bean = creator.createBean(beanName);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            return bean;
        }
    }
}
