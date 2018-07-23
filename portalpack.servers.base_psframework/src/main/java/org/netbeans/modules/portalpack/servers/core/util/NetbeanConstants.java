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

package org.netbeans.modules.portalpack.servers.core.util;

import java.io.File;

/**
 *
 * @author Satya
 */
public class NetbeanConstants {

    public static final String PROJECT_XML = "project.xml.template";
    public static final String PROJECT_PROPERTIES = "project.properties.template";
    public static final String AJAX_PAGE_TEMPLATE = "ajaxpage_template.html";
    public static final String AJAX_REQUEST_JS = "AjaxRequest.js";
    public static final String CONTEXT_XML = "context.xml";
    
    public static final String CONFIG_DIR = System.getProperty("netbeans.user") + File.separator + "portalpacklib";
    
    public static final String CHANNEL_PREFIX = "_channel_";
    
    public static final String PORTAL_LOGGER = "nb_portal_plugin";
          
}
