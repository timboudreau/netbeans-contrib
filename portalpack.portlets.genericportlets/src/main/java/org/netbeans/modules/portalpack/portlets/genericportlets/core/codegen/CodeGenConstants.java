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

package org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen;

/**
 * @author Satya
 */
public interface CodeGenConstants {

    public static final String PACKAGE = "PACKAGE_NAME";
    public static final String CLASSNAME = "CLASS_NAME";
    public static final String WEB_INF_DIR = "WEB_INF_DIR";
    
    

    public static final String JAVA_MODULE_TYPE = "JAVA";
    public static final String WEB_MODULE_TYPE = "WEB";


    public static String J2EE_MODULE = "J2EE";
    public static String PORTLET_SPEC_VERSION = "PORTLET_SPEC_VERSION";
    
    
    //portlet.xml specific constants
    public static final String PORTLET_NAME = "PORTLET_NAME";
    public static final String PORTLET_CLASS = "PORTLET_CLASS";
    public static final String PORTLET_DISPLAY_NAME="PORTLET_DISPLAY_NAME";
    public static final String PORTLET_DESCRIPTION = "PORTLET_DESCRIPTION";
    public static final String PORTLET_TITLE = "PORTLET_TITLE";
    public static final String PORTLET_SHORT_TITLE = "PORTLET_SHORT_TITLE";
    
    //filter types
    public static final String ACTION_FILTER_TYPE = "Action Filter";
    public static final String EVENT_FILTER_TYPE = "Event Filter";
    public static final String RENDER_FILTER_TYPE = "Render Filter";
    public static final String RESOURCE_FILTER_TYPE = "Resource Filter";
    
    //lifecycle phases
    public static final String ACTION_PHASE="ACTION_PHASE";
    public static final String EVENT_PHASE="EVENT_PHASE";
    public static final String RENDER_PHASE="RENDER_PHASE";
    public static final String RESOURCE_PHASE="RESOURCE_PHASE";
}
