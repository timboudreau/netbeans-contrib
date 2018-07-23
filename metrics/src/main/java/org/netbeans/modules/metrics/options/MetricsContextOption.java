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
