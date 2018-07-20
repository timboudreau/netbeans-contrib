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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 *
 * @author Satya
 */
public class AMObjectSearchHandler {

    private Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    public static final String ORGANIZATION = "organization";

    private String objType = null;
    private Map objectTypes = null;
    private String searchFilter = "";
    private JMXHelper jmxHelper;
    private PSConfigObject psconfig;
    private LogPublisher logPublisher;

    /** Creates a new instance of AMObjectSearchHandler */
    public AMObjectSearchHandler(JMXHelper jmxHelper, PSConfigObject config, LogPublisher logPublisher) throws Exception{
         this.psconfig = config;
        this.jmxHelper = jmxHelper;
        objectTypes = queryObjectTypes();

        this.logPublisher = logPublisher;
        if(psconfig != null)
            logger.log(Level.FINEST,"PSConfig :"+psconfig.toString());
        else
            logger.log(Level.SEVERE,"PSConfig object is null");

    }

     private  Map queryObjectTypes() throws Exception{
        Map objs = null;
        MBeanServerConnection msc = null;
        try {
            logger.log(Level.FINEST,"JMXHelper : **"+jmxHelper);
            msc = jmxHelper.getMBeanServerConnection();
            logger.log(Level.FINEST,"PSCONFIG::: "+psconfig);
            ObjectName objName = JMXAdminHelperUtil.getAMObjectSearchMBeanObjectName(psconfig.getDefaultDomain());
            Object[] params = new Object[] { };
            String[] signature = new String[] { };
            objs = (Map)msc.invoke(objName, "queryObjectTypes", params, signature);
        } catch (InstanceNotFoundException infe) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),infe);
            logPublisher.log( "Exception in AMObjectSearchBean.queryObjectTypes() " + infe.getMessage());
            throw infe;
        } catch (MBeanException me) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),me);
            logPublisher.log("Exception in AMObjectSearchBean.queryObjectTypes() "+me.getMessage());
            throw me;
        } catch (ReflectionException re) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),re);
            logPublisher.log("Exception in AMObjectSearchBean.queryObjectTypes() "+re.getMessage());
            throw re;
        } catch (IOException ioe) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),ioe);
            logPublisher.log("Exception in AMObjectSearchBean.queryObjectTypes() "+ioe.getMessage());
            throw ioe;
        }catch(SecurityException se){
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),se);
            logPublisher.log("Security Exception : "+se.getMessage());
            throw se;
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),e);
            logPublisher.log("Exception in AMObjectSearchBean.queryObjectTypes() "  + e.getMessage());

            throw e;
        }
        if(objs == null)
            objs = new HashMap();
        return objs;
    }

      private String getRootSuffix() {
        String orgDN = null;
        MBeanServerConnection msc = null;
        try {
            msc = jmxHelper.getMBeanServerConnection();
            ObjectName objName = JMXAdminHelperUtil.getAMObjectSearchMBeanObjectName(psconfig.getDefaultDomain());
            Object[] params = new Object[] { };
            String[] signature = new String[] { };
            orgDN = (String)msc.invoke(objName, "queryRootSuffix", params, signature);
        } catch (InstanceNotFoundException infe) {
            logPublisher.log("Exception in AMObjectSearchBean.getDefaultOrgDN()"   + infe.getMessage());
            //throw infe
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),infe);
        } catch (MBeanException me) {
            logPublisher.log("Exception in AMObjectSearchBean.getDefaultOrgDN()" +  me.getMessage());
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),me);
            //throw me;
        } catch (ReflectionException re) {
            logPublisher.log("Exception in AMObjectSearchBean.getDefaultOrgDN()" + re.getMessage());
            //throw re;
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),re);
        } catch (IOException ioe) {
            logPublisher.log("Exception in AMObjectSearchBean.getDefaultOrgDN()" + ioe.getMessage());
            //throw ioe
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),ioe);
        } catch (Exception e) {
            logPublisher.log("Exception in AMObjectSearchBean.getDefaultOrgDN()" +  e.getMessage());
            //throw e;
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),e);
        }
        return orgDN;

    }


    public Map getAMObjects(String type,String searchFilter, String baseDN) {
        Map resultMap = null;
        int iotype = 1;
        int scope = 1;
        if(baseDN == null)
        {
            baseDN = getRootSuffix();
            scope = 0;
        }
        logger.log(Level.FINE,"BASE DN : "+baseDN);
        try {

           // if ( objType != null ) {
           //     iotype = Integer.parseInt(objType);
           // }
            //logger.log(Level.FINE,objectTypes);
            //New code added for 7.2
            Object t = objectTypes.get(type);
            if(t == null)
            {
                if(type.equals(NodeTypeConstants.ORGANIZATION))
                    iotype = 2;
                
                //Newly added code
                MBeanServerConnection msc = jmxHelper.getMBeanServerConnection();
                ObjectName objName = JMXAdminHelperUtil.getAMObjectSearchMBeanObjectName(psconfig.getDefaultDomain());
                Object[] params = new Object[] {baseDN,searchFilter, new Integer(iotype),new Integer(scope)};
                String[] signature = new String[] {  "java.lang.String", "java.lang.String", "java.lang.Integer", "java.lang.Integer"};
                resultMap = (Map)msc.invoke(objName, "searchObjects", params, signature);
                
            } else{
                try{
                    iotype = ((Integer) objectTypes.get(type)).intValue();
                }catch(Exception e){
                    logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),e);
                    iotype = 1;
                }
                
                boolean alert=false;

                MBeanServerConnection msc = jmxHelper.getMBeanServerConnection();
                ObjectName objName = JMXAdminHelperUtil.getAMObjectSearchMBeanObjectName(psconfig.getDefaultDomain());
                Object[] params = new Object[] { baseDN, searchFilter, new Integer(iotype), new Integer(scope) };
                String[] signature = new String[] { "java.lang.String", "java.lang.String", "java.lang.Integer", "java.lang.Integer"};
                resultMap = (Map)msc.invoke(objName, "searchObjects", params, signature);
            }

           
        }// catch (InstanceNotFoundException infe) {
        catch(Exception infe){
            logPublisher.log("Exception in AMObjectSearchBean.getAMObjects()"+ infe.getMessage());
            //throw infe
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(AMObjectSearchHandler.class).getString("Error"),infe);
            resultMap = new HashMap();
        }
    /*
    catch (MBeanException me) {
            log(Level.SEVERE, "Exception in AMObjectSearchBean.getAMObjects()", me);
            //throw me;
        } catch (ReflectionException re) {
            log(Level.SEVERE, "Exception in AMObjectSearchBean.getAMObjects()", re);
            //throw re;
        } catch (IOException ioe) {
            log(Level.SEVERE, "Exception in AMObjectSearchBean.getAMObjects()", ioe);
            //throw ioe
        } catch (Exception e) {
            log(Level.SEVERE, "Exception in AMObjectSearchBean.getAMObjects()", e);
            //throw e;
        }*/
        return resultMap;
    }

    public LogPublisher getLogger() {
        return logPublisher;
    }


}
