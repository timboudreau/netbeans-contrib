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
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.servers.sunps7.impl;

import com.sun.security.sasl.Provider;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.management.MBeanServerConnection;

import javax.management.remote.JMXConnector;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * This class provides misc. utility methods for Portal Admin Server clients.
 */
public class JMXAdminHelperUtil {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    /**
     * The JMX domain used by Portal Admin Server.
     */
    public static final String JMX_DOMAIN = "com.sun.portal";

    /**
     * The ID of the default portal domain.
     */
    public static final String DEFAULT_DOMAIN = "defaultDomain";

    /**
     * The ID of the default portal.
     */
    public static final String DEFAULT_PORTAL = "defaultPortal";

    /**
     * The ID of the an upgraded (jes3 or earlier) portal.
     */
    public static final String UPGRADED_PORTAL = "Upgraded";

    /**
     * The ID of the default portal server instance.
     */
    public static final String DEFAULT_INSTANCE = "defaultInstance";

    // Keys used in the portal MBean ObjectNames
    public static final String KEY_TYPE = "type";
    public static final String KEY_ID = "name";

    // MBean Names
    public static final String PORTAL_DOMAIN_MBEAN = "PortalDomain";
    public static final String AMOBJECTSEARCH_MBEAN = "AMObjectSearch";
    public static final String SSOADAPTER_MBEAN = "SSOAdapter";
    public static final String SCHEDULER_MBEAN = "Scheduler";
    public static final String PORTAL_MBEAN = "Portal";
    public static final String DESKTOP_DYNAMIC_MBEAN = "DesktopDynamic";
    public static final String PORTAL_SERVER_INSTANCE_MBEAN = "ServerInstance";
    public static final String DISPLAYPROFILE_MBEAN = "DisplayProfile";
    public static final String DPADMINWRAPPER_MBEAN = "DPAdminWrapper";
    public static final String MONITORING_MBEAN = "Monitoring";
    public static final String PORTLET_ADMIN_MBEAN = "PortletAdmin";
    public static final String SRA_MBEAN = "SRA";
    public static final String REWRITER_MBEAN = "Rewriter";
    public static final String SRA_MONITORING_MBEAN = "SRAMonitoring";

    public static final String SEARCHSERVER_MBEAN = "SearchServer";
    public static final String SEARCH_DATABASE_MBEAN = "Database";
    public static final String SEARCH_ROBOT_MBEAN = "Robot";
    public static final String SEARCH_SITEPROBE_MBEAN = "SiteProbe";
    public static final String SEARCH_AUTOCLASSIFY_MBEAN = "Autoclassify";
    public static final String SEARCH_CATEGORY_MBEAN = "Category";
    public static final String PROFILER_MBEAN = "Profiler";
    public static final String FILE_UPLOAD_DOWNLOAD_MANAGER = "UploadDownloadFileManager";

    // The MBeans/resource types of the portal fabric MBeans.
    public static final String PORTAL_DOMAIN_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN;
    public static final String SEARCHSERVER_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + SEARCHSERVER_MBEAN;
    public static final String FILE_UPLOAD_DOWNLOAD_MANAGER_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + FILE_UPLOAD_DOWNLOAD_MANAGER;
    public static final String AMOBJECTSEARCH_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + AMOBJECTSEARCH_MBEAN;
    public static final String SSOADAPTER_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + SSOADAPTER_MBEAN;
    public static final String SCHEDULER_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + SCHEDULER_MBEAN;
    public static final String PORTAL_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + PORTAL_MBEAN;
    public static final String PORTAL_SERVER_INSTANCE_MBEAN_TYPE = PORTAL_MBEAN_TYPE + "." + PORTAL_SERVER_INSTANCE_MBEAN;
    public static final String DESKTOP_DYNAMIC_MBEAN_TYPE = PORTAL_MBEAN_TYPE + "." + DESKTOP_DYNAMIC_MBEAN;
    public static final String DISPLAYPROFILE_MBEAN_TYPE = PORTAL_MBEAN_TYPE + "." + DISPLAYPROFILE_MBEAN;
    public static final String DPADMINWRAPPER_MBEAN_TYPE = PORTAL_MBEAN_TYPE + "." + DPADMINWRAPPER_MBEAN;
    public static final String PORTLET_ADMIN_MBEAN_TYPE = PORTAL_MBEAN_TYPE  + "." + PORTLET_ADMIN_MBEAN;
    public static final String MONITORING_MBEAN_TYPE = PORTAL_SERVER_INSTANCE_MBEAN_TYPE + "." + MONITORING_MBEAN;
    public static final String SRA_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + SRA_MBEAN ;
    public static final String REWRITER_MBEAN_TYPE  = PORTAL_DOMAIN_MBEAN_TYPE + "." + REWRITER_MBEAN;
    public static final String SRA_MONITORING_MBEAN_TYPE = PORTAL_DOMAIN_MBEAN_TYPE + "." + SRA_MONITORING_MBEAN;

    public static final String SEARCH_DATABASE_MBEAN_TYPE = SEARCHSERVER_MBEAN_TYPE + "." + SEARCH_DATABASE_MBEAN;
    public static final String SEARCH_ROBOT_MBEAN_TYPE = SEARCHSERVER_MBEAN_TYPE + "." + SEARCH_ROBOT_MBEAN;
    public static final String SEARCH_SITEPROBE_MBEAN_TYPE = SEARCHSERVER_MBEAN_TYPE + "." + SEARCH_SITEPROBE_MBEAN;
    public static final String SEARCH_AUTOCLASSIFY_MBEAN_TYPE = SEARCHSERVER_MBEAN_TYPE + "." + SEARCH_AUTOCLASSIFY_MBEAN;
    public static final String SEARCH_CATEGORY_MBEAN_TYPE = SEARCHSERVER_MBEAN_TYPE + "." + SEARCH_CATEGORY_MBEAN ;
    public static final String PROFILER_MBEAN_TYPE = PORTAL_MBEAN_TYPE + "." + PROFILER_MBEAN;

    public static final int DEFAULT_UPLOAD_SIZE = 4096;
    public static final int  DEFAULT_DOWNLOAD_SIZE  = 4096;

    private String cacaoConfigDir = null;
    private TrustManager[] tms = null;

    /**
     * Returns the encoded SASL authenticationID containing the given
     * user name and domainID.
     *
     * @param userName user name of the user.
     * @param domainID ID of the portal domain the user belongs to.
     * @return the encoded SASL authenticationID.
     */

    public static String encodeAuthenticationId(String userName,
            String domainID) {

        return JMX_DOMAIN + "\001" + userName
                + "\001"  + domainID;
        //return "com.sun.portal\0001" + userName +"\0001" + domainID;
    }

    /**
     * Returns the encoded SASL password containing the given password.
     *
     * @param password the password to log into the portal domain.
     * @return the encoded SASL password.
     */
    public static String encodePassword(String password) {
        return password;
    }

    /**
     * Returns a connected JMXConnector to the connector server
     * running on the given host.  The connection is authenticated
     * against the portal domain with the given domain ID using the
     * given user ID and password.  The given user ID can either be
     * the user DN, or the uid attribute value (in that case, the
     * default org in the portal domain is assumed).
     * <p>
     * The caller is responsible for obtaining the
     * MBeanServerConnection and closing the returned JMXConnector.
     *
     * @param  host  the host where the JMX connector server is running on.
     * @param  domainID  the ID of the portal domain to authenticate against.
     * @param  userID  the user ID (user DN or the uid attribute value).
     * @param  password  the user password.
     * @return a connected JMXConnector.
     * @exception NullPointerException if host, domainID, userID or
     *                                 password is null.
     * @exception SecurityException if an authentication error occurs.
     * @exception IOException if a connection error occurs.
     */
    public JMXConnector getJMXConnector(String host,int jmxConnectorPort, String domainID,
            String userID, String password)
            throws SecurityException, IOException {

        if (host == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_HOST_IS _NULL"));
        }

        if (domainID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_DomainID_Is_Null."));
        }

        if (userID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_UserID_Is_null."));
        }

        if (password == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_Password_Is_Null"));
        }

        String id = encodeAuthenticationId(userID, domainID);
        String passwd = encodePassword(password);
        return getConnector(host, jmxConnectorPort, id, passwd);
    }

    /**
     * Returns a connected JMXConnector to the connector server
     * running on the local host.  The connection is authenticated
     * against the portal domain with the given domain ID using the
     * given user ID and password.  The given user ID can either be
     * the user DN, or the uid attribute value (in that case, the
     * default org in the portal domain is assumed).
     * <p>
     * The caller is responsible for obtaining the
     * MBeanServerConnection and closing the returned JMXConnector.
     *
     * @param  domainID  the ID of the portal domain to authenticate against.
     * @param  userID  the user ID (user DN or the uid attribute value).
     * @param  password  the user password.
     * @return a connected JMXConnector.
     * @exception NullPointerException if domainID, userID or password is null.
     * @exception SecurityException if an authentication error occurs.
     * @exception IOException if a connection error occurs.
     */
    /*public static JMXConnector getJMXConnector(String domainID, String userID,
            String password)
            throws SecurityException, IOException {

        return getJMXConnector("localhost", domainID, userID, password);
    }*/

    /**
     * Returns a connected JMXConnector to the connector server
     * running on the local host.  The connection is authenticated
     * against the default portal domain using the given user ID and
     * password.  The given user ID can either be the user DN, or the
     * uid attribute value (in that case, the default org in the
     * default portal domain is assumed).
     * <p>
     * The caller is responsible for obtaining the
     * MBeanServerConnection and closing the returned JMXConnector.
     *
     * @param  userID  the user ID (user DN or the uid attribute value).
     * @param  password  the user password.
     * @return a connected JMXConnector.
     * @exception NullPointerException if userID or password is null.
     * @exception SecurityException if an authentication error occurs.
     * @exception IOException if a connection error occurs.
     */
    /*public static JMXConnector getJMXConnector(String userID, String password)
    throws SecurityException, IOException {

        return getJMXConnector(DEFAULT_DOMAIN, userID, password);
    }*/


    protected synchronized JMXConnector getConnector(String host,int jmxConnectorPort,
            String authID,
            String password)
            throws SecurityException, IOException {

        if (cacaoConfigDir == null) {
            //  try {
            /////  PSConfigContext pscc = new PSConfigContextImpl(DEFAULT_DOMAIN);
            // cacaoConfigDir = pscc.getCacaoConfigDir();
            //    } catch (IOException ioe) {
            // Most likely reason is psconsole is deployed on a host where
            // portal is not installed. Just ignore it and let cacao default
            // to its OS specific config directory
            //  }

            // cacaoConfigDir = "/etc/cacao/instances/default";
        }


        if (tms == null) {
            String psConfigDir = null;

            //  try {
            //// PSConfigContext pscc = new PSConfigContextImpl(DEFAULT_DOMAIN);
            ///// psConfigDir = pscc.getPSConfigDir();
            //     } catch (IOException e) {
            //   }

            tms = new TrustManager[] {new PSX509TrustManager(NetbeanConstants.CONFIG_DIR)};
        }

         // Prepare connection environment map for secure JMXMP connection
        Map connectionEnv = new HashMap();

        // Cacao uses TLS for encryption and SASL/PLAIN for authentication
        connectionEnv.put("jmx.remote.profiles", "TLS SASL/PLAIN");
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLSv1");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        try {
            // Initialize SSLCOntext with our truststore managers
            ctx.init(null, tms, null);
        } catch (KeyManagementException ex) {
            ex.printStackTrace();
        }

        // Create and set SSLSocketFactory for JMXMP
        SSLSocketFactory ssf = ctx.getSocketFactory();

         connectionEnv.put("jmx.remote.tls.socket.factory", ssf);

         //System.out.println("Auth ID is ....."+authID);
        // Set credentials
        String saslAuthenticationId = authID;//"com.sun.cacao.user\u0001" + authID;
        String saslPassword = password;
        String[] creds = { saslAuthenticationId, saslPassword };
        connectionEnv.put(JMXConnector.CREDENTIALS, creds);

        // Add SASL/PLAIN mechanism client provider
        Security.addProvider(new Provider());
        /*
        Map env = new HashMap();
        //env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.cacao.agent.impl.CacaoJmxConnectorProvider");
        env.put(JmxClient.BASE_MAP_KEY + JmxClient.WELLKNOWN_KEY, "false");
        env.put(JmxClient.BASE_MAP_KEY + JmxClient.SASLID_KEY, authID);
        env.put(JmxClient.BASE_MAP_KEY + JmxClient.SASLPASS_KEY, password);
        env.put("jmx.remote.tls.socket.factory", tms);

        env.put("com.sun.cacao.rmi.registry.port","11164");
        env.put("com.sun.cacao.jmxmp.connector.port","11162");*/

        if (cacaoConfigDir != null) {
            // Set the value of the cacao config dir into the env map
            //    env.put(JmxClient.CACAO_CONFIG_DIR_KEY, cacaoConfigDir);
        }

        JMXServiceURL url = null;
        try {
            url = new JMXServiceURL("service:jmx:jmxmp" +
                "://" + host + ":" + jmxConnectorPort);
            // try RMI first
            //url = new JMXServiceURL("service:jmx:" + JmxClient.RMI_PROTOCOL
              //      + "://" + host);
           
            return JMXConnectorFactory.connect(url, connectionEnv);
        } catch (Exception e) {
            e.printStackTrace();
            // fallback to JMXMP
          //  url = new JMXServiceURL("service:jmx:" + JmxClient.JMXMP_PROTOCOL
            //        + "://" + host);
            //logger.log(Level.INFO,url.toString());
           // return JMXConnectorFactory.connect(url, env);
        }
        return null;
    }



    public static ObjectName getPortalMBeanObjectName(String domainID,
            String portalID)
            throws MalformedObjectNameException {

        if (domainID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_DomainID_Is_Null."));
        }

        if (portalID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_PortalID_Is_Null"));
        }

        LinkedList path = new LinkedList();
        path.addFirst(domainID);
        path.addFirst(portalID);
        return getResourceMBeanObjectName(PORTAL_MBEAN_TYPE, path);
    }

    public static ObjectName getResourceMBeanObjectName(String type, List path)
    throws MalformedObjectNameException {

        if (type == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_Type_Is_Null"));
        }

        if (path == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_Path_Is_Null"));
        }

        if (path.isEmpty()) {
            throw new IllegalArgumentException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_Path_Cannot_Be_Empty."));
        }

        Hashtable keyProperties = new Hashtable();
        keyProperties.put(KEY_TYPE, type);
        String resourceID = (String)path.get(0);
        keyProperties.put(KEY_ID, resourceID.toLowerCase());
        StringTokenizer st = new StringTokenizer(type, ".");

        if (st.countTokens() != path.size()) {
            String message = "type is inconsistent with path's length.";
            throw new IllegalArgumentException(message);
        }

        for (int i = path.size() - 1; i > 0; i--) {
            resourceID = (String)path.get(i);
            keyProperties.put(st.nextToken(), resourceID.toLowerCase());
        }

        return new ObjectName(JMX_DOMAIN, keyProperties);
    }


    public static ObjectName getPortalDomainMBeanObjectName(String domainID)
    throws MalformedObjectNameException {

        if (domainID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_DomainID_Is_Null."));
        }

        return getResourceMBeanObjectName(PORTAL_DOMAIN_MBEAN_TYPE,
                Collections.singletonList(domainID));
    }


    public static ObjectName getInstanceMBeanObjectName(String domainID,
            String portalID,
            String instanceID)
            throws MalformedObjectNameException {

        if (domainID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("domainID_is_null."));
        }

        if (portalID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_PortalID_Is_Null"));
        }

        if (instanceID == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_InstanceID_Is_Null."));
        }

        LinkedList path = new LinkedList();
        path.addFirst(domainID);
        path.addFirst(portalID);
        path.addFirst(instanceID);

        return getResourceMBeanObjectName(PORTAL_SERVER_INSTANCE_MBEAN_TYPE,
                path);
    }



    //new methods

    /**
     * Returns the MBean/resource type of the parent of the
     * MBean/resource with the given type.  A Portal Server
     * MBean/resource type has the form "aaa.bbb.ccc".  In this case
     * "aaa.bbb" is returned.
     *
     * @param  type  the type of the MBean/resource whose parent
     *               MBean/resource type is to be returned.
     * @return the MBean/resource type of the parent;
     *         <code>null</code> if the given MBean/resource has no
     *         parent, e.g. PortalDomain.
     * @exception NullPointerException if type is <code>null</code>.
     */
    public static String getParentType(String type) {
        if (type == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_Type_Is_Null"));
        }

        int index = type.lastIndexOf(".");
        return (index >= 0) ? type.substring(0, index) : null;
    }

    /**
     * Returns the full path of a portal server resource instance
     * given the full path of its parent and its ID.
     *
     * @param  parentPath  the full path of the parent resource instance.
     * @param  childID  the ID of the resource instance.
     * @return the full path of the portal server resource instance.
     * @exception NullPointerException if parentPath or childID is
     *                                 <code>null</code>.
     */
    public static List getChildPath(List parentPath, String childID) {
        if (parentPath == null) {
            throw new NullPointerException(org.openide.util.NbBundle.getBundle(JMXAdminHelperUtil.class).getString("MSG_ParentPath_Is_Null."));
        }

        if (childID == null) {
            throw new NullPointerException("childID is null.");
        }

        LinkedList childPath = new LinkedList(parentPath);
        childPath.addFirst(childID);
        return childPath;
    }

    /**
     * Returns the ObjectName pattern to search for the portal
     * resource MBeans of the given MBean/resource type under a parent
     * resource instance with the given path.  The returned value can
     * be used in <code>MBeanServerConnection.queryMBeans()</code> or
     * <code>MBeanServerConnection.queryNames()</code> to obtain the
     * portal resource MBeans.
     *
     * @param  type  the type of the MBeans to be searched.
     * @param  parentPath  the full path of the parent resource instance.
     * @return an ObjectName pattern.
     * @exception NullPointerException if type or parentPath is
     *                                 <code>null</code>.
     * @exception IllegalArgumentException if type and parentPath's
     *                                     length are inconsistent.
     * @exception MalformedObjectNameException if type or any element
     *                                         in parentPath contains
     *                                         an illegal character or
     *                                         if it does not follow
     *                                         the rules for quoting.
     */
    public static ObjectName getResourcesPattern(String type, List parentPath)
    throws MalformedObjectNameException {

        if (type == null) {
            throw new NullPointerException("type is null.");
        }

        if (parentPath == null) {
            throw new NullPointerException("parentPath is null.");
        }

        StringBuffer name = new StringBuffer(JMX_DOMAIN);
        name.append(":");
        name.append(KEY_TYPE + "=" + type);
        StringTokenizer st = new StringTokenizer(type, ".");

        if (st.countTokens() != (parentPath.size() + 1)) {
            String message = "type is inconsistent with parentPath's length.";
            throw new IllegalArgumentException(message);
        }

        for (int i = parentPath.size() - 1; i >= 0; i--) {
            name.append(",");
            String resourceID = (String)parentPath.get(i);
            name.append(st.nextToken() + "=" + resourceID.toLowerCase());
        }

        name.append(",*");
        return new ObjectName(name.toString());
    }

    /**
     * Returns the ObjectName pattern to search for all the portal
     * resource MBeans, regardless of MBean/resource type, under a
     * parent MBean with the given type and path.  The returned value
     * can be used in <code>MBeanServerConnection.queryMBeans()</code>
     * or <code>MBeanServerConnection.queryNames()</code> to obtain
     * the portal resource MBeans.
     *
     * @param  parentType  the type of the parent resource instance.
     * @param  parentPath  the full path of the parent resource instance.
     * @return an ObjectName pattern.
     * @exception NullPointerException if parentType or parentPath is
     *                                 <code>null</code>.
     * @exception IllegalArgumentException if parentType and parentPath's
     *                                     length are inconsistent.
     * @exception MalformedObjectNameException if parentType or any element
     *                                         in parentPath contains
     *                                         an illegal character or
     *                                         if it does not follow
     *                                         the rules for quoting.
     */
    public static ObjectName getAllResourcesPattern(String parentType,
            List parentPath)
            throws MalformedObjectNameException {

        if (parentType == null) {
            throw new NullPointerException("parentType is null.");
        }

        if (parentPath == null) {
            throw new NullPointerException("parentPath is null.");
        }

        StringBuffer name = new StringBuffer(JMX_DOMAIN);
        name.append(":");
        StringTokenizer st = new StringTokenizer(parentType, ".");

        if (st.countTokens() != parentPath.size()) {
            String message = "parentType is inconsistent with parentPath's length.";
            throw new IllegalArgumentException(message);
        }

        for (int i = parentPath.size() - 1; i >= 0; i--) {
            String resourceID = (String)parentPath.get(i);
            name.append(st.nextToken() + "=" + resourceID.toLowerCase());
            name.append(",");
        }

        name.append("*");
        return new ObjectName(name.toString());
    }



    /**
     * Returns the ObjectName pattern to search for the portal
     * domains.  The returned value can be used in
     * <code>MBeanServerConnection.queryMBeans()</code> or
     * <code>MBeanServerConnection.queryNames()</code> to obtain the
     * PortalDomainMBeans
     *
     * @return an ObjectName pattern.
     */
    public static ObjectName getPortalDomainsPattern()
    throws MalformedObjectNameException {

        return getResourcesPattern(PORTAL_DOMAIN_MBEAN_TYPE,
                Collections.EMPTY_LIST);
    }


    /**
     * Returns the ObjectName pattern to search for the portals in the
     * portal domain with the given ID.  The returned value can be
     * used in <code>MBeanServerConnection.queryMBeans()</code> or
     * <code>MBeanServerConnection.queryNames()</code> to obtain the
     * PortalMBeans in the portal domain with the given ID.
     *
     * @param  domainID  domain ID of the portal domain.
     * @return an ObjectName pattern.
     * @exception NullPointerException if domainID is <code>null</code>.
     * @exception MalformedObjectNameException if domainID contains an
     *                                         illegal character or if
     *                                         it does not follow the
     *                                         rules for quoting.
     */
    public static ObjectName getPortalsPattern(String domainID)
    throws MalformedObjectNameException {

        if (domainID == null) {
            throw new NullPointerException("domainID is null.");
        }

        return getResourcesPattern(PORTAL_MBEAN_TYPE,
                Collections.singletonList(domainID));
    }

    /**
     * Returns the ObjectName pattern to search for the searchserver in the
     * portal domain with the given ID.  The returned value can be
     * used in <code>MBeanServerConnection.queryMBeans()</code> or
     * <code>MBeanServerConnection.queryNames()</code> to obtain the
     * PortalMBeans in the portal domain with the given ID.
     *
     * @param  domainID  domain ID of the portal domain.
     * @return an ObjectName pattern.
     * @exception NullPointerException if domainID is <code>null</code>.
     * @exception MalformedObjectNameException if domainID contains an
     *                                         illegal character or if
     *                                         it does not follow the
     *                                         rules for quoting.
     */
    public static ObjectName getSearchServerPattern(String domainID) throws MalformedObjectNameException {
        if (domainID == null) {
            throw new NullPointerException("domainID is null.");
        }

        return getResourcesPattern(SEARCHSERVER_MBEAN_TYPE, Collections.singletonList(domainID));
    }

    /**
     * Returns the ObjectName pattern to search for the search database
     * in the portal domain with the given ID. The returned value can be
     * used in <code>MBeanServerConnection.queryMBeans()</code> or
     * <code>MBeanServerConnection.queryNames()</code> to obtain the
     * PortalMBeans in the portal domain with the given ID.
     *
     * @param  domainID  domain ID of the portal domain.
     * @param  searchserverID  search server ID.
     * @return an ObjectName pattern.
     * @exception NullPointerException if domainID is <code>null</code>.
     * @exception MalformedObjectNameException if domainID contains an
     *                                         illegal character or if
     *                                         it does not follow the
     *                                         rules for quoting.
     */
    public static ObjectName getSearchDatabasePattern(String domainID, String searchServerID) throws MalformedObjectNameException {
        if (domainID == null) {
            throw new NullPointerException("domainID is null.");
        }
        if (searchServerID == null) {
            throw new NullPointerException("searchServerID is null.");
        }

        LinkedList path = new LinkedList();
        path.addFirst(domainID);
        path.addFirst(searchServerID);
        return getResourcesPattern(SEARCH_DATABASE_MBEAN_TYPE, path);
    }

    /**
     * Returns the ObjectName pattern to search for the portal server
     * instances in the portal with the given portal ID and in the
     * portal domain with the given domain ID.  The returned value can
     * be used in <code>MBeanServerConnection.queryMBeans()</code> or
     * <code>MBeanServerConnection.queryNames()</code> to obtain the
     * PortalServerInstanceMBeans in the portal with the given portal
     * ID and in the portal domain with the given domain ID.
     *
     * @param  domainID  domain ID of the portal domain.
     * @param  portalID  portal ID of the portal.
     * @return an ObjectName pattern.
     * @exception NullPointerException if domainID or portalID is
     *                                 <code>null</code>.
     * @exception MalformedObjectNameException if domainID or portalID
     *                                         contains an illegal
     *                                         character or if it does
     *                                         not follow the rules
     *                                         for quoting.
     */
    public static ObjectName getPortalServerInstancesPattern(String domainID,
            String portalID)
            throws MalformedObjectNameException {

        if (domainID == null) {
            throw new NullPointerException("domainID is null.");
        }

        if (portalID == null) {
            throw new NullPointerException("portalID is null.");
        }

        LinkedList path = new LinkedList();
        path.addFirst(domainID);
        path.addFirst(portalID);
        return getResourcesPattern(PORTAL_SERVER_INSTANCE_MBEAN_TYPE, path);
    }


    /**
     * Returns the ObjectName of the DisplayProfileMBean for the given portalId
     * and is in the portal domain with the given domain ID.
     *
     * @param  domainID  domain ID of the portal domain.
     * @param  portalID  portal ID of the PortalMBean.
     * @return the ObjectName of the PortalMBean.
     * @exception NullPointerException if domainID or portalID is
     *                                 <code>null</code>.
     * @exception MalformedObjectNameException if domainID or portalID
     *                                         contains an illegal
     *                                         character or if it does
     *                                         not follow the rules
     *                                         for quoting.
     */
    public static ObjectName getDisplayProfileMBeanObjectName(String domainId,
            String portalId)
            throws MalformedObjectNameException  {

        if (domainId == null) {
            throw new NullPointerException("domainID is null.");
        }

        if (portalId == null) {
            throw new NullPointerException("portalID is null.");
        }

        ObjectName objName = null;
        String resourceId = "DisplayProfile";
        LinkedList path = new LinkedList();
        path.addFirst(domainId);
        path.addFirst(portalId);
        path.addFirst(resourceId);

        try {
            objName = JMXAdminHelperUtil.getResourceMBeanObjectName(
                    DISPLAYPROFILE_MBEAN_TYPE, path);
        } catch (MalformedObjectNameException me) {
            throw me;
        }
        return objName;
    }

    /**
     * Determines if the hosts with the given names are the same.
     *
     * @param  host1  the first host to be tested for equality.
     * @param  host2  the second host to be tested for equality.
     * @return <code>true</code> if the hosts are the same;
     *         <code>false</code> otherwise.
     * @exception UnknownHostException if no IP address for the hosts
     *                                 could be found.
     */
    public static boolean isSameHost(String host1, String host2)
    throws UnknownHostException {

        if (host1.equals(host2)) {
            return true;
        }

        InetAddress[] addresses1 = InetAddress.getAllByName(host1);
        InetAddress[] addresses2 = InetAddress.getAllByName(host2);

        for (int i = 0; i < addresses1.length; i++) {
            for (int j = 0; j < addresses2.length; j++) {
                if (addresses1[i].equals(addresses2[j])) {
                    return true;
                }
            }
        }

        return false;
    }
//AM Related Beans

    public static ObjectName getAMObjectSearchMBeanObjectName(String domain) {
        ObjectName objName = null;
        try {
            LinkedList path = new LinkedList();
            path.addFirst(domain);
            path.addFirst("amobjsearch");
            objName = getResourceMBeanObjectName(AMOBJECTSEARCH_MBEAN_TYPE, path);
        } catch (MalformedObjectNameException mone)  {
            mone.printStackTrace();
            //throw mone
            //log(Level.SEVERE, "Exception getting MBean Object in  AMObjectSearchBean.getAMObjectSearchMBeanObjectName()", mone);
        }
        return objName;
    }



        /*
         * This method makes connection to UploadDownloadFileManagerMBean and invokes upload command .
         * @returns 2 elements array where first element is objectid and Second element is filename .
         */
    public static String[]  uploadFile(MBeanServerConnection msc, String domainId, File file, int uploadSize) throws UploadDownloadException{
        String remoteFileName = null;
        ObjectName objectName = null;
        String uploadId = null;
        String[] nameIdArray = null;
        long fileSize = file.length();
        FileInputStream fi = null;

        if (uploadSize == 0){
            uploadSize = DEFAULT_UPLOAD_SIZE; //set the default uploadsize to 4K
        }
        try{

            LinkedList path = new LinkedList();
            path.addFirst(domainId);
            path.addFirst(FILE_UPLOAD_DOWNLOAD_MANAGER);
            //get the UploadDownloadFileManagerMbean
            objectName = JMXAdminHelperUtil.getResourceMBeanObjectName(FILE_UPLOAD_DOWNLOAD_MANAGER_MBEAN_TYPE, path);
            if (!(msc.isRegistered(objectName) )){
                //            logger.log(Level.SEVERE, "PSPL_CSPACU0004");
                throw new UploadDownloadException("mbean not registered");
            }
            //invoke initiateFileUpload on   UploadDownloadFileManagerMbean
            Object[] params = {file.getName(),new Long(fileSize)};
            String[] signature = {"java.lang.String","java.lang.Long"};
            nameIdArray = (String[])msc.invoke(objectName,"initiateFileUpload",params,signature);
            uploadId = nameIdArray[0];
            remoteFileName = (String)nameIdArray[1];

            //invoke uploadBytes on  UploadDownloadFileManagerMbean with a chunk size of "uploadSize"

            fi = new  FileInputStream(file);
            byte[] bytesToUpload = null ; //array holding bytes read from local file
            long offset = 0;
            int readSize = 0;   //no. of bytes read from local file

            String nameByteArray = (new byte[0]).getClass().getName();
            signature[0] = "java.lang.String";
            signature[1] = nameByteArray;
            params[0]  = uploadId;

            readSize = fileSize < uploadSize ? new Long(fileSize).intValue(): uploadSize;
            bytesToUpload =  new byte[readSize];

            while (fi.read(bytesToUpload) != -1 ){
                params[1] = bytesToUpload;
                msc.invoke(objectName,"uploadBytes",params,signature);

                offset = offset + readSize;
                readSize =  new Long(fileSize - offset).intValue() > uploadSize ? uploadSize : new Long(fileSize - offset).intValue();
                if (readSize == 0){
                    break;
                }
                bytesToUpload =  new byte[readSize];
            }

        }catch(MalformedObjectNameException moe){
            //logger.log(Level.SEVERE, "PSPL_CSPACU0002",moe);
            throw new UploadDownloadException(moe);

        }catch(IOException e){
            /// logger.log(Level.SEVERE, "PSPL_CSPACU0001",e);
            throw new UploadDownloadException(e);
        } catch(Exception e){
            ///logger.log(Level.SEVERE, "PSPL_CSPACU0003",e);
            throw new UploadDownloadException(e);
        }finally{
            try{
                fi.close();
            } catch(IOException ioe){
                ///logger.log(Level.SEVERE, "PSPL_CSPACU0001",ioe);
                ioe.printStackTrace();
            }
        }
        return nameIdArray;
    }

   /*
    * This method makes connection to UploadDownloadFileManagerMBean and makes a clean up for upload/download
    * @params id object id for the file uploaded or downloaded
    */
    public static void uploadDownloadCleanUp(MBeanServerConnection msc,String domainId,String id)throws UploadDownloadException{
        ObjectName objectName;
        try{

            LinkedList path = new LinkedList();
            path.addFirst(domainId);
            path.addFirst(FILE_UPLOAD_DOWNLOAD_MANAGER);
            //get the UploadDownloadFileManagerMbean
            objectName = JMXAdminHelperUtil.getResourceMBeanObjectName("PortalDomain.UploadDownloadFileManager", path);
            if (!(msc.isRegistered(objectName) )){
                // logger.log(Level.SEVERE, "PSPL_CSPACU0004");
                throw new UploadDownloadException("mbean not registered");
            }
            String[] params = {id};
            String[] signature = {"java.lang.String"};
            msc.invoke(objectName,"cleanUp",params,signature);

        }catch(MalformedObjectNameException moe){
            //  logger.log(Level.SEVERE, "PSPL_CSPACU0002",moe);
            throw new UploadDownloadException(moe);
        }catch(IOException e){
            e.printStackTrace();
            // logger.log(Level.SEVERE, "PSPL_CSPACU0001",e);
        }catch(Exception e){
            // logger.log(Level.SEVERE, "PSPL_CSPACU0003",e);
            throw new UploadDownloadException(e);
        }
    }
    public static void closeConnector(JMXConnector connector){
        if (connector != null) {
            try {
                connector.close();
            } catch (Exception e) {
                String message = "Can't close JMX connector";
                //logger.log(Level.WARNING, message, e);
            }
        }
    }

}

