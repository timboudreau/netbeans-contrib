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
