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

package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.util;

import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;

/**
 *
 * @author Satyaranjan
 */
public class PortletXMLUtil {
    
    /** Creates a new instance of PortletXMLUtil */
    public PortletXMLUtil() {
    }
    
    public static String getPortletClassName(PortletApp portletApp,String portlet)
    {
        PortletType[] portlets = portletApp.getPortlet();
        for(int i=0;i<portlets.length;i++)
        {
            if(portlets[i].getPortletName() != null && portlet.equals(portlets[i].getPortletName()))
                    return portlets[i].getPortletClass();
        }
        return null;
    }

    
}
