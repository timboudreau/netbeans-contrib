/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.metrics.options;

import org.netbeans.modules.metrics.ClassMetrics;

import java.lang.reflect.*;
import org.openide.options.*;
import org.openide.util.*;

public class MetricsContextOption extends ContextSystemOption {

    private static final long serialVersionUID = 2225708962229466540L;

    /** Singleton instance */
    private static MetricsContextOption singleton;

    /** Creates new MetricsContextOption */
    public MetricsContextOption() {
    	// Add metrics children.  Introspection is used because
	// Java doesn't support metaclasses.
	Class[] metricClasses = ClassMetrics.getMetricClasses();
	Class[] noParamCls = new Class[0];
	Class[] oneParamCls = new Class[] {
	    ClassMetrics.class
	};
	Object[] noParams = new Object[0];
	Object[] nullParam = new Object[1];
	MetricsContextOption mco = getDefault();
	for (int i = 0; i < metricClasses.length; i++) {
	    Class cls = metricClasses[i];
	    try {
		/* Create an instance of each metric and get its
                 * associated SystemOption singleton.  By passing
                 * in a null constructor parameter, all classfile
                 * loading and metrics calcucation is avoided 
                 * (which would slow down node creation).
                 */
		Constructor c = cls.getConstructor(oneParamCls);
		Object metric = c.newInstance(nullParam);
		Method m = cls.getMethod("getSettings", noParamCls);
		SystemOption option = (SystemOption)m.invoke(metric, noParams);
		mco.addOption(option);
	    } catch (Exception e) {
		System.err.println("couldn't add metrics option: " + 
				   cls.getName());
		e.printStackTrace();
	    }
	}
    }

    /** Returns default instance of jar system option */
    public static MetricsContextOption getDefault () {
	if (singleton == null) {
	    singleton = (MetricsContextOption) 
                SharedClassObject.findObject (MetricsContextOption.class, true);
	}
	return singleton;
    }
	
    /** Get a human presentable name of the action.
    * This may be presented as an item in a menu.
    * @return the name of the option
    */
    public String displayName () {
        return NbBundle.getBundle(MetricsContextOption.class).
               getString("CTL_RootOptionParent");
    }   

    /** get the help context for the option */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (MetricsContextOption.class);
    }
	
}
