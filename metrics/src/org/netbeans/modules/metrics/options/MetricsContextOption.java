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

import org.netbeans.modules.metrics.Metric;
import org.netbeans.modules.metrics.MetricsLoader;

import java.lang.reflect.*;
import org.openide.options.*;
import org.openide.util.*;

public class MetricsContextOption extends ContextSystemOption {

    private static final long serialVersionUID = 2225708962229466540L;

    /** Singleton instance */
    private static MetricsContextOption singleton;

    /** Creates new MetricsContextOption */
    public MetricsContextOption() {
    	// A null ClassMetrics object is used to avoid classfile
	// loading or node creation, since this metrics set is
	// only used for introspection.
	Metric[] metrics = MetricsLoader.createMetricsSet(null);
	MetricsContextOption mco = getDefault();
	for (int i = 0; i < metrics.length; i++) {
	    Class cls = metrics[i].getClass();
	    try {
		// Get each metric's associated SystemOption singleton.  
		Method m = cls.getMethod("getSettings", new Class[0]);
		SystemOption option = 
		    (SystemOption)m.invoke(metrics[i], new Object[0]);
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
