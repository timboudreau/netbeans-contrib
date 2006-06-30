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

package org.netbeans.modules.j2ee.ejbfreeform;

/**
 * Contains constants for various properties used in the EJB project
 *
 * @author Andrei Badea
 */
public class EjbFreeformProperties {

    public static final String RESOURCE_DIR = "resource.dir"; // NOI18N
    public static final String J2EE_SERVER_INSTANCE = "j2ee.server.instance"; // NOI18N
    public static final String J2EE_SERVER_TYPE = "j2ee.server.type"; // NOI18N
    public static final String J2EE_PLATFORM = "j2ee.platform"; // NOI18N
    /**
     * JPDA debug session name
     */
    public final static String JPDA_SESSION_NAME = "jpda.session.name"; // NOI18N
    
    /**
     * JPDA transport type
     */
    public final static String JPDA_TRANSPORT = "jpda.transport"; // NOI18N
    
    /**
     * JPDA host to connect to
     */
    public final static String JPDA_HOST = "jpda.host"; // NOI18N
    
    public final static String JPDA_ADDRESS = "jpda.address"; // NOI18N
    
    public final static String DEBUG_SOURCEPATH = "debug.sourcepath"; // NOI18N
    
    private EjbFreeformProperties() {
    }
    
}
