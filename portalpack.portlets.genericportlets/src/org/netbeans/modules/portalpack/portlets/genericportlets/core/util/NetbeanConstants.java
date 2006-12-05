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

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

import java.io.File;

/**
 *
 * @author Satya
 */
public class NetbeanConstants {
    public static final String PROJECT_XML = "project.xml.template";
    public static final String PROJECT_PROPERTIES = "project.properties.template";
    public static final String CONTEXT_XML = "context.xml";
    
    public static final String[] TEMPLATES = {PROJECT_XML,
                                                  PROJECT_PROPERTIES, 
                                                 // AJAX_PAGE_TEMPLATE,
                                                  //AJAX_REQUEST_JS,
                                                  CONTEXT_XML};
    public static  String CONFIG_DIR = "";
    
    public static final String CHANNEL_PREFIX = "_channel_";
    
    public static final String PORTAL_LOGGER = "nb_portal_plugin";
    
    public final static String J2EE_1_3 = "J2EE 1.3";
    public final static String J2EE_1_4 = "J2EE 1.4";
    
    static{
        String userBase = System.getProperty("netbeans.user");
        if(userBase == null || userBase.trim().length() == 0)
        {
            userBase = System.getProperty("user.home");
        }
        CONFIG_DIR = userBase + File.separator + "psnetbeans";
    }
    
}
