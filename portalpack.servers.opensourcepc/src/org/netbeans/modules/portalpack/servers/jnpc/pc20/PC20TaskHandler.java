/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.jnpc.pc20;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.jnpc.impl.JNPCTaskHandler;

/**
 *
 * @author Satyaranjan
 */
public class PC20TaskHandler extends JNPCTaskHandler {

    private static String PORTLET_ADMIN_INTERFACE = "com.sun.portal.portletcontainer.admin.mbeans.PortletAdmin";
    private static String PORTLET_REGISTRY_CONTEXT_FACTORY = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContextFactory";
    private static String PORTLET_REGISTRY_CONTEXT_ABSTRACT_FACTORY = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContextAbstractFactory";
    private static String PORTLET_REGISTRY_CONTEXT = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContext";
    private static String PORTLET_REGISTRY_CACHE = "com.sun.portal.portletcontainer.admin.PortletRegistryCache";

    public PC20TaskHandler(PSDeploymentManager dm) {
        super(dm);
    }

    @Override
    protected void _deployOnPC(final String warfile) throws Exception {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            updateCache();
            Class clazz = loader.loadClass(PORTLET_ADMIN_INTERFACE);
            Object ob = clazz.newInstance();
            //System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
            Method method = null;
            Boolean isDeployed = Boolean.FALSE;
            try {
                method = clazz.getMethod("deploy", new Class[]{String.class, Properties.class, Properties.class, boolean.class});
                isDeployed = (Boolean) method.invoke(ob, new Object[]{warfile,new Properties(),new Properties(),Boolean.FALSE});
            } catch (NoSuchMethodException e) {
                logger.log(Level.SEVERE, "No deploy method is found : ", e);
            } catch (Exception ex) {
                throw ex;
            }

            if (isDeployed != null) {
                if (!isDeployed.booleanValue()) {
                    throw new Exception(org.openide.util.NbBundle.getMessage(PC20TaskHandler.class, "Deployment_failed"));
                }
            } else {
                logger.log(Level.INFO, "Problem Preparing war file");
            }
        } catch (Exception e) {
            writeErrorToOutput(uri, e);
            throw e;
        } finally {

            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Override
    protected void _undeployFromPC(final String portletAppName, boolean logError) throws Exception {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            updateCache();
            Class clazz = loader.loadClass(PORTLET_ADMIN_INTERFACE);
            Object ob = clazz.newInstance();

            Method method = null;
            Boolean isUnDeployed = Boolean.FALSE;
            try {
                method = clazz.getMethod("undeploy", new Class[]{String.class, boolean.class});
                isUnDeployed = (Boolean) method.invoke(ob, new Object[]{portletAppName,Boolean.FALSE});
            } catch (NoSuchMethodException e) {
                logger.log(Level.SEVERE, "No undeploy method is found : ", e);
            } catch (Exception e) {
                throw e;
            }

            if (isUnDeployed != null) {
                if (!isUnDeployed.booleanValue()) {
                    throw new Exception(org.openide.util.NbBundle.getMessage(PC20TaskHandler.class, "UNDEPLOYMENT_FAILED"));
                }
            } else {
                logger.log(Level.INFO, "Problem unregistering application from the portlet container");
            }
        } catch (Exception e) {
            if (logError) {
                writeErrorToOutput(uri, e);
            }
            throw e;
        } finally {

            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }


    @Override
    public String[] getPortlets(String dn) {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            Class clazz = null;
            Object factoryObj = null;
            Object portletRegistryContextObj = null;
            updateCache();

            try {
                Class absFactoryClazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_ABSTRACT_FACTORY);
                clazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_FACTORY);
                Object absFactoryObj = absFactoryClazz.newInstance();
                Method getPRCF = absFactoryClazz.getMethod("getPortletRegistryContextFactory", new Class[]{});
                factoryObj = getPRCF.invoke(absFactoryObj, new Object[]{});
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "Class Not Found Exception: ", ex);
                return new String[0];
            }

            Method method = clazz.getMethod("getPortletRegistryContext", new Class[]{});
            portletRegistryContextObj = method.invoke(factoryObj, new Object[]{});
            Class registryContextClazz = null;
            try {
                registryContextClazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT);
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "REGISTRY CONTEXT Class Not Found : ", ex);
            }
            Method getPortletsMethod = registryContextClazz.getMethod("getAvailablePortlets", new Class[]{});

            List list = (List) getPortletsMethod.invoke(portletRegistryContextObj,new Object[]{});

            if (list == null) {
                return new String[]{};
            } else {
                return (String[]) list.toArray(new String[0]);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting portlet lists ", e);
            writeErrorToOutput(uri, e);
            return new String[]{};
        } finally {
            updateCache();
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Override
    protected void updateCache() {
        try {
            Class cacheClazz = loader.loadClass(PORTLET_REGISTRY_CACHE);
            Method m = cacheClazz.getMethod("init", new Class[]{});
            m.invoke(null, null);
        } catch (Exception e) {
            logger.log(Level.INFO,"Error Updating Cache" + e.getMessage());
            //ignore exception incase of class not found.
        }
    }
}
