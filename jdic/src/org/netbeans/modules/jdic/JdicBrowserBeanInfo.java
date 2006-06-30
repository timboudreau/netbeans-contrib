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

package org.netbeans.modules.jdic;

import java.awt.Image;
import java.beans.*;

import org.openide.util.NbBundle;

public class JdicBrowserBeanInfo extends SimpleBeanInfo {

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor descr = new BeanDescriptor(JdicBrowser.class);
        descr.setDisplayName (NbBundle.getMessage(JdicBrowserBeanInfo.class, "CTL_JdicBrowserName"));
        descr.setShortDescription (NbBundle.getMessage (JdicBrowserBeanInfo.class, "HINT_JdicBrowserName"));
        descr.setValue ("helpID", "org.netbeans.modules.extbrowser.ExtWebBrowser");  // NOI18N
	return descr;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return new PropertyDescriptor [0];
    }

    /**
    * Returns the IceBrowserSettings' icon. 
    */
    public Image getIcon (int type) {
        return loadImage("/org/netbeans/modules/extbrowser/resources/extbrowser.gif"); // NOI18N
    }
    
    /**
     * Claim there are no other relevant BeanInfo objects.  You
     * may override this if you want to (for example) return a
     * BeanInfo for a base class.
     */
    public BeanInfo[] getAdditionalBeanInfo () {
        return new BeanInfo[] { };
    }
    
}
