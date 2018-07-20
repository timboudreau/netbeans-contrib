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

package org.netbeans.modules.portalpack.servers.core.common;

/**
 *
 * @author Satya
 */
public class NetbeansServerConstant {
    
    public static NetbeansServerType[] AVAILABLE_SERVERS = { new NetbeansServerType("SUNWS_70","Sun WebServer 7.0"),
                                            new NetbeansServerType("SUNAPP_8_x","Sun AppServer 8.x")};
    public static String SUNWS_70 = "SUNWS_70";
    public static String SUNAPP_8_x = "SUNAPP_8_x";
    
    public static String PS_OS = "PS_OS";
    public static String PS_OS_URI_PREFIX = "sun:osps";
    
    public final static String J2EE_1_3 = "J2EE 1.3";
    public final static String J2EE_1_4 = "J2EE 1.4";
    
}
