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

package org.netbeans.modules.j2ee.sun.ws7;

public interface Constants {
    static final String WAIT_NODE = "wait_node"; // NOI18N

    // J2EE TYPE Names
    static final String WEB_MOD = "WebModule"; // NOI18N

    // CONFIG - Resource Names
    static final String JDBC_RESOURCE = "jdbc-resource"; // NOI18N
    static final String JNDI_RESOURCE = "external-jndi-resource"; // NOI18N
    static final String CUSTOM_RESOURCE = "custom-resource"; // NOI18N
    static final String MAIL_RESOURCE = "mail-resource"; // NOI18N

    // Operations
    static final String OP_DISABLE = "disable"; // NOI18N
    static final String OP_ENABLE = "enable"; // NOI18N
    static final String OP_RESTART = "restart"; // NOI18N
    static final String OP_UNDEPLOY = "undeploy"; // NOI18N
    static final String OP_DELETE_RESOURCES = "delete"; // NOI18N

    static final String[] JVM_STR_TO_ARR = {"server-class-path", "class-path-suffix", "class-path-prefix" }; // NOI18N
    static final String[] JVM_BOOLEAN_VALS = {"sticky-attach", "debug", "enabled", "env-class-path-ignored" }; //NOI18N
    static final String[] RESOURCE_BOOLEAN_VALS = {"isolation-level-guaranteed", "fail-all-connections", "enabled" }; //NOI18N

    static final String[] READ_ONLY_PROPS_RESOURCES = {"jndi-name"}; // NOI18N
    static final String[] READ_ONLY_PROPS_APPS = {"uri", "path"}; // NOI18N
    static final String RES_PROPERTY="property";
    static final String JDBC_RES_CONN_CREATION_PROPERTY="connection-creation-property";// NOI18N
    static final String JDBC_RES_CONN_LEASE_PROPERTY="connection-lease-property";// NOI18N
    
    static final String JVM_DEBUG="debug";// NOI18N
    static final String JVM_DEBUG_OPTION="debug-jvm-options";// NOI18N
    static final String DEBUG_OPTIONS_ADDRESS = "address="; //NOI18N      
    static final String ISSHMEM = "transport=dt_shmem"; //NOI18N
    static final String ISDTSOCKET = "transport=dt_socket"; //NOI18N    
    
}
