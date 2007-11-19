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

import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import org.netbeans.modules.portalpack.servers.sunps7.PS71ServerConstant;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Satya
 */
public class JMXHelper {

   private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
   private JMXConnector  m_connector;

   public PSConfigObject psconfig;
   private JMXAdminHelperUtil adminUtilHelper;
   
    /** Creates a new instance of JMXHelper */
    public  JMXHelper(PSConfigObject psconfig) {
        this.psconfig = psconfig;
        adminUtilHelper = new JMXAdminHelperUtil();
    }
    /*
    public static JMXHelper getInstance()
    {
        if(instance == null)
            instance = new JMXHelper(psconfig);
        return instance;
    }*/

    public MBeanServerConnection getMBeanServerConnection() throws Exception
    {
        int port = 0;
        try{
            port = Integer.parseInt(psconfig.getProperty(PS71ServerConstant.JMX_CONNECTOR_PORT));
        }catch(Exception e){
            logger.severe(org.openide.util.NbBundle.getBundle(JMXHelper.class).getString("LBL_INVALID_JMX_PORT"));
        }
        //psconfig = PSConfigObject.getPSConfigObject();
        return getMBeanServerConnection(psconfig.getAdminUser(),psconfig.getAdminPassWord(),psconfig.getDefaultDomain(),psconfig.getHost(),port);
    }

    protected MBeanServerConnection getMBeanServerConnection(
            String id,
            String pwd,
            String domain,String host,int port) throws Exception {

        MBeanServerConnection m_mbsc;
        m_connector = null;
        try {
            // Unknown connection with our SASL/PLAIN authenticationID
            if (m_connector == null) {

                logger.log(Level.FINEST,"Host for jmx: "+psconfig.getHost());
                logger.log(Level.FINEST,"Port for jmx: "+psconfig.getPort());
                logger.log(Level.FINEST,"Instance Id for jmx : "+psconfig.getIntanceId());
                m_connector = adminUtilHelper.getJMXConnector(host,port,
                        domain,
                        id, pwd);
            }

            m_mbsc = m_connector.getMBeanServerConnection();

            return m_mbsc;
        } catch (SecurityException se) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(JMXHelper.class).getString("Error"),se);
             NotifyDescriptor notDesc = new NotifyDescriptor.Message(
                        org.openide.util.NbBundle.getBundle(JMXHelper.class).getString("MSG_Security_Exception"),
                        NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(notDesc);

        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(JMXHelper.class).getString("Error"),e);
            NotifyDescriptor notDesc = new NotifyDescriptor.Message(
                        org.openide.util.NbBundle.getBundle(JMXHelper.class).getString("MSG_Error_connecting_MBean_Server"),
                        NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(notDesc);

        }

        return null;

    }


    public void closeJMXConnector()
    {
           if (m_connector != null) {
            try {
                m_connector.close();
            } catch (Exception e) {
                String message = org.openide.util.NbBundle.getBundle(JMXHelper.class).getString("MSG_Cant_close_JMX_connector");
                logger.log(Level.WARNING,message,e);
            }
        }
    }

}
