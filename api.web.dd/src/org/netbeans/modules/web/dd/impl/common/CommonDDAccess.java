/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/**
 * Methods for accessing schema2beans objects in a bean-independent way.
 *
 * @author  Milan Kuchtiak
 */
package org.netbeans.modules.web.dd.impl.common;

import java.lang.reflect.*;
import org.openide.util.NbBundle;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.api.web.dd.WebApp;
import org.netbeans.api.web.dd.ServiceRef;
import org.netbeans.api.web.dd.common.CommonDDBean;

/**
 * Methods for accessing schema2beans objects in bean-independent and version-independent way.
 *
 * @author Milan Kuchtiak
 */

public class CommonDDAccess {

    public static final String SERVLET_2_3 = "2_3"; //NOI18N
    public static final String SERVLET_2_4 = "2_4"; //NOI18N

    public static final String PACKAGE_PREFIX = "org.netbeans.modules.web.dd.impl.model_"; //NOI18N
    public static final String DOT = "."; //NOI18N

    /**
     * Return a new instance of the specified type
     *
     * @param parent 	parent bean
     * @param beanName 	which bean to create
     * @param version	"2_3" or "2_4"
     * @return BaseBean object e.g. Servlet
     */

    public static BaseBean newBean(CommonDDBean parent, String beanName, String version)  throws ClassNotFoundException {
	beanName = getImplementationBeanName(parent, beanName, version);
	try {
	    Class beanClass = Class.forName(
				PACKAGE_PREFIX
				+ version + DOT
				+ beanName);
	    return (BaseBean) beanClass.newInstance();

	} catch (Exception e) {
            if (e instanceof ClassNotFoundException) 
                throw (ClassNotFoundException)e;
            else {
                // This is a programming error.
                e.printStackTrace();
                throw new RuntimeException(
                    NbBundle.getMessage(CommonDDAccess.class,
                        "MSG_COMMONDDACCESS_ERROR", "newBean",	
                        ", version = " + version + ", beanName = " + beanName, e+ ": " +e.getMessage()));
            }
	}
    }
    
    public static void addBean(CommonDDBean parent, CommonDDBean child, String beanName, String version) {
	beanName = getImplementationBeanName(parent, beanName, version);
	try {
            Class p = parent.getClass();
            Class ch = Class.forName("org.netbeans.api.web.dd."+beanName); //NOI18N
            Method setter=null;
            try {
                setter = p.getMethod("set" + beanName, new Class[]{ch}); //NOI18N
                setter.invoke(parent, new Object[]{child});
            } catch (NoSuchMethodException ex) {
            }
            if (setter==null) {
                setter = p.getMethod("add" + getNameForMethod(parent, beanName), new Class[]{ch}); //NOI18N
                setter.invoke(parent, new Object[]{child});
            }
	} catch (Exception e) {
            // This is a programming error.
            e.printStackTrace();
            throw new RuntimeException(
                NbBundle.getMessage(CommonDDAccess.class,
                    "MSG_COMMONDDACCESS_ERROR", "addBean",	
                    ", version = " + version + ", beanName = " + beanName, e+ ": " +e.getMessage()));
	}
    }

    /**
     * Get a BaseBean object from parent BaseBean
     *
     * @param parent            parent BaseBean
     * @param beanProperty 	name of child's BaseBean object e.g. "Servlet"
     * @param nameProperty      name of property e.g. ServletName
     * @param value             e.g. "ControllerServlet"
     */
    public static BaseBean findBeanByName(BaseBean parent, String beanProperty, String nameProperty, String value) {
	Class c = parent.getClass();
	Method getter;
	Object result;
	try {
	    getter = c.getMethod("get" + getNameForMethod((CommonDDBean)parent,beanProperty), null); //NOI18N
	    result = getter.invoke(parent, null);
	    if (result == null) {
		return null;
	    } else if (result instanceof BaseBean) {
		return null;
	    } else {
		BaseBean[] beans = (BaseBean[]) result;
                for (int i=0;i<beans.length;i++) {
                    Class c1 = beans[i].getClass();
                    Method getter1;
                    Object result1;
                    getter1 = c1.getMethod("get" + nameProperty, null); //NOI18N
                    result1 = getter1.invoke(beans[i], null);
                    if (result1 instanceof String) {
                        if (value.equals((String)result1)) {
                            return beans[i];
                        }
                    }
                }
                return null;
	    }
	} catch (Exception e) {
	    // This is a programming error
	    e.printStackTrace();
	    throw new RuntimeException(
		NbBundle.getMessage(CommonDDAccess.class,
		    "MSG_COMMONDDACCESS_ERROR", "getBeanByName",	
		    "parent = " + parent + ", beanProperty = " + beanProperty
                    + ", nameProperty = " + nameProperty
                    + ", value = " + value, 
		    e+ ": " +e.getMessage()));	
	}
    }
    
    /**
     * Handle special cases of version differences
     */
    private static String getImplementationBeanName (CommonDDBean parent, String beanName, String version) {

	if (version.equals(SERVLET_2_3)) {
            if ("InitParam".equals(beanName) && parent instanceof WebApp) return "ContextParam"; //NOI18N
            else if ("Handler".equals(beanName) && parent instanceof ServiceRef) return "ServiceRefHandler"; //NOI18N
            else return beanName;
	} else {
            return beanName;
	}
    }
    
    /**
     * Handle special cases of version differences
     */
    private static String getNameForMethod (CommonDDBean parent, String beanName) {

        if ("InitParam".equals(beanName) && parent instanceof WebApp) return "ContextParam"; //NOI18N
        else if ("ServiceRefHandler".equals(beanName)) return "Handler"; //NOI18N
	else {
            return beanName;
	}
    }
}
