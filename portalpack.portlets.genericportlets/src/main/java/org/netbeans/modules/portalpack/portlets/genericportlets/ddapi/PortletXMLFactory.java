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


package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.schema2beans.Schema2BeansException;

/**
 *
 * @author Satyaranjan
 */
public class PortletXMLFactory {
    
    /** Creates a new instance of PortletXMLFactory */
    public PortletXMLFactory() {
    }
    
    public static PortletApp createGraph(File portletXml) throws Exception{
        
        try{
            String version = getPortletSpecVersion(portletXml);
            String implClass = getPortletAppImplementationClass(version);
            Class clazz = PortletXMLFactory.class.getClassLoader().loadClass(implClass);
//TODO            Method createGraphMethod = clazz.getMethod("createGraph", new Class[]{File.class});
                        Method createGraphMethod = clazz.getMethod("read", new Class[]{File.class});
            Object[] parameters = new Object[]{portletXml};
            Object returnObj = createGraphMethod.invoke(clazz, parameters);
            if(returnObj == null)
                return null;
            else if(returnObj instanceof org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp)
            {
//TODO                String version1 = ((BaseBean)returnObj).getAttributeValue("version");
                String version1 = ((org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp)returnObj).getVersion();
                return (org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp)returnObj;
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw ex;
        }catch(NoSuchMethodException ex){
            ex.printStackTrace();
            throw ex;
        }catch(IllegalAccessException ex){
            ex.printStackTrace();
            throw ex;
        }catch(InvocationTargetException ex){
            ex.printStackTrace();
            throw ex;
        }
        return null;
        //return org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.model_1_0.PortletApp.createGraph(portletXml);
        //return null;
    }
    public static PortletApp createGraph(InputStream in) throws Exception{
        
        try {
            java.lang.Class clazz = org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletXMLFactory.class.getClassLoader().loadClass("org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.model_1_0.PortletApp");
          //TODO  java.lang.reflect.Method createGraphMethod = clazz.getMethod("createGraph", new java.lang.Class[] {java.io.InputStream.class});
              java.lang.reflect.Method createGraphMethod = clazz.getMethod("read", new java.lang.Class[] {java.io.InputStream.class});
            java.lang.Object[] parameters = new java.lang.Object[] {in};
            java.lang.Object returnObj = createGraphMethod.invoke(clazz, parameters);
            if (returnObj == null) {
                return null;
            } else if (returnObj instanceof org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp) {
                return (org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp) returnObj;
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw ex;
        }catch(NoSuchMethodException ex){
            ex.printStackTrace();
            throw ex;
        }catch(IllegalAccessException ex){
            ex.printStackTrace();
            throw ex;
        }catch(InvocationTargetException ex){
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }
    public static void merge(PortletApp oldPortletApp,PortletApp newPortletApp,int mode) {
       ((BaseBean)oldPortletApp).merge((BaseBean)(newPortletApp), mode);
       // oldPortletApp = newPortletApp;
    }
    
    public static void write(PortletApp portletApp,Writer out) throws Schema2BeansException, IOException {
        ((BaseBean)portletApp).write(out);
        ///((org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.model_1_0.PortletApp)portletApp).write(out);
    }
    
    public static String getPortletSpecVersion(File portletXml)
    {
         try{
            Class clazz = PortletXMLFactory.class.getClassLoader().loadClass("org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.model_1_0.PortletApp");
           //TODO Method createGraphMethod = clazz.getMethod("createGraph", new Class[]{File.class});
             Method createGraphMethod = clazz.getMethod("read", new Class[]{File.class});
            Object[] parameters = new Object[]{portletXml};
            Object returnObj = createGraphMethod.invoke(clazz, parameters);
            if(returnObj == null)
                return null;
            else if(returnObj instanceof org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp)
            {
              //TODO  String version = ((BaseBean)returnObj).getAttributeValue("version");
                  String version = ((org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp)returnObj).getVersion();
                return version;
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }catch(NoSuchMethodException ex){
            ex.printStackTrace();
        }catch(IllegalAccessException ex){
            ex.printStackTrace();
        }catch(InvocationTargetException ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    private static String getPortletAppImplementationClass(String version)
    {
        if(version == null)
            version = PortletApp.VERSION_1_0;
        if(version.equals(PortletApp.VERSION_2_0))
        {
            return "org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.model_2_0.PortletApp";
        }else
            return "org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.model_1_0.PortletApp";
    }
    
}
