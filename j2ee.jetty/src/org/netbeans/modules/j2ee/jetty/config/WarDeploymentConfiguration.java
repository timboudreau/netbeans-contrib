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
package org.netbeans.modules.j2ee.jetty.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ContextRootConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.jetty.config.gen.Configure;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 * Class responsible for creating and modifying jetty specific deployment 
 * descriptor for web module (war archive)
 * @author novakm
 */
public class WarDeploymentConfiguration implements ModuleConfiguration,
        ContextRootConfiguration, PropertyChangeListener {

    private final File jettyDD;
    private final J2eeModule j2eeModule;
    private final DataObject dataObject;
    private Configure configure;
    private static final Logger LOGGER = Logger.getLogger(WarDeploymentConfiguration.class.getName());

    /**
     * Constructor responsible for creating dataObject
     * representing deployment descriptor and
     * registering changeListener on it
     * @param j2eeModule - module for which we want to 
     * modify or create deployment descriptor
     */
    public WarDeploymentConfiguration(J2eeModule j2eeModule) {
        this.j2eeModule = j2eeModule;
        jettyDD = j2eeModule.getDeploymentConfigurationFile("WEB-INF/jetty-web.xml"); // NOI18N

        getConfigure();
        DataObject dataObj = null;
        try {
            dataObj = DataObject.find(FileUtil.toFileObject(jettyDD));
            dataObj.addPropertyChangeListener(this);
        } catch (DataObjectNotFoundException donfe) {
            LOGGER.log(Level.WARNING, "Exception in contructor ", donfe);
        }
        dataObject = dataObj;
    }

    /**
     * Method returning root element of DD or creating
     * one if the file doesn't exist
     * @return configure - root element representing DD
     */
    public synchronized Configure getConfigure() {
        if (configure == null) {
            try {
                if (jettyDD.exists()) {
                    // load configuration if already exists
                    try {
                        configure = Configure.createGraph(jettyDD);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    } catch (RuntimeException re) {
                        // jetty-web.xml is not parseable, do nothing
                    }
                } else {
                    // create jetty-web.xml if it does not exist yet
                    configure = generateConfigure();
                    writefile(jettyDD, configure);
                }
            } catch (ConfigurationException ce) {
                Exceptions.printStackTrace(ce);
            }
        }
        return configure;
    }

    /**
     * generate Context graph.
     */
    private Configure generateConfigure() {
        Configure config = new Configure();
        config._getSchemaLocation();
        config.setClass2("org.mortbay.jetty.webapp.WebAppContext");
        config.setSet(new String[]{"/dummy"});
        config.setSetName(0, "contextPath");
        return config;
    }

    /**
     * @return Lookup of this class
     */
    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    /**
     * @return j2eeModule for which this class represents its configuration
     */
    public J2eeModule getJ2eeModule() {
        return j2eeModule;
    }

    /**
     * removes propertyChangeListener from dataobject representing DD
     */
    public void dispose() {
        if (dataObject != null) {
            dataObject.removePropertyChangeListener(this);
        }
    }

    /**
     * Search for contextPath value in DD
     * @return contextPath of this configuration
     * @throws org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException
     * if the graph created from DD is not parseable
     */
    public String getContextRoot() throws ConfigurationException {
        Configure config = getConfigure();
        if (config == null) { // graph not parseable
            String msg = NbBundle.getMessage(WarDeploymentConfiguration.class, "MSG_CannotReadContextRoot", jettyDD.getPath());
            throw new ConfigurationException(msg);
        }
        return config.getContextRoot();
    }

    /**
     * Changes contextRoot value to given parameter or creates it if it doesn't exist
     * @param contextRoot - new contextRoot value
     * @throws org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException
     * if the graph created from DD is not parseable
     */
    public void setContextRoot(String contextRoot) throws ConfigurationException {
        if (!isCorrectCP(contextRoot)) {
            // hodit asi eror nebo enco
        }
        Configure config = getConfigure();
        if (config == null) { // graph not parseable
            String msg = NbBundle.getMessage(WarDeploymentConfiguration.class, "MSG_CannotReadContextRoot", jettyDD.getPath());
            throw new ConfigurationException(msg);
        }
        config.setContextRoot(contextRoot);
        writefile(jettyDD, config);
    }

    /**
     * Responds to PropertyChangeEvent, we know the graph is not synchronized
     * so we use null instead (and have to create new one when we want to 
     * read or modify it)
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == DataObject.PROP_MODIFIED &&
                evt.getNewValue() == Boolean.FALSE) {
            // dataobject has been modified, graph is out of sync
            configure = null;
        }
    }

    private static void writefile(final File file, final BaseBean bean) throws ConfigurationException {
        try {
            FileObject cfolder = FileUtil.toFileObject(file.getParentFile());
            if (cfolder == null) {
                File parentFile = file.getParentFile();
                try {
                    cfolder = FileUtil.toFileObject(parentFile.getParentFile()).createFolder(parentFile.getName());
                } catch (IOException ioe) {
                    String msg = NbBundle.getMessage(WarDeploymentConfiguration.class, "MSG_FailedToCreateConfigFolder", parentFile.getPath());
                    throw new ConfigurationException(msg, ioe);
                }
            }
            final FileObject folder = cfolder;
            FileSystem fs = folder.getFileSystem();
            fs.runAtomicAction(new FileSystem.AtomicAction() {

                public void run() throws IOException {
                    String name = file.getName();
                    FileObject configFO = folder.getFileObject(name);
                    if (configFO == null) {
                        configFO = folder.createData(name);
                    }
                    FileLock lock = configFO.lock();
                    try {
                        OutputStream os = new BufferedOutputStream(configFO.getOutputStream(lock), 4086);
                        try {
                            // TODO notification needed
                            if (bean != null) {
                                bean.write(os);
                            }
                        } finally {
                            os.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            });
        } catch (IOException e) {
            String msg = NbBundle.getMessage(WarDeploymentConfiguration.class, "MSG_writeToFileFailed", file.getPath());
            throw new ConfigurationException(msg, e);
        }
    }

    private boolean isCorrectCP(String contextPath) {
        boolean correct = true;
        if (!contextPath.equals("") && !contextPath.startsWith("/")) {
            correct = false;
        } else if (contextPath.endsWith("/")) {
            correct = false;
        } else if (contextPath.indexOf("//") >= 0) {
            correct = false;
        }
        return correct;
    }
}
