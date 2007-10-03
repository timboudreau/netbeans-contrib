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

/*
 * WS70Resources.java

 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.dd;

import org.netbeans.modules.schema2beans.Schema2BeansRuntimeException;

/**
 * Code reused from Appserver common API module
 */
public interface WS70Resources extends  org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean {

        public static final String CUSTOM_RESOURCE = "CustomResource";	// NOI18N
	public static final String EXTERNAL_JNDI_RESOURCE = "ExternalJndiResource";	// NOI18N
	public static final String JDBC_RESOURCE = "JdbcResource";	// NOI18N
	public static final String MAIL_RESOURCE = "MailResource";	// NOI18N
        
	public void setWS70CustomResource(int index, WS70CustomResource value); 
	public WS70CustomResource getWS70CustomResource(int index);
	public int sizeWS70CustomResource();
	public void setWS70CustomResource(WS70CustomResource[] value);
	public WS70CustomResource[] getWS70CustomResource();
	public int addWS70CustomResource(WS70CustomResource value);
	public int removeWS70CustomResource(WS70CustomResource value);
	public WS70CustomResource newWS70CustomResource();

	public void setWS70ExternalJndiResource(int index, WS70ExternalJndiResource value);
	public WS70ExternalJndiResource getWS70ExternalJndiResource(int index);
	public int sizeWS70ExternalJndiResource();
	public void setWS70ExternalJndiResource(WS70ExternalJndiResource[] value);
	public WS70ExternalJndiResource[] getWS70ExternalJndiResource();
	public int addWS70ExternalJndiResource(WS70ExternalJndiResource value);
	public int removeWS70ExternalJndiResource(WS70ExternalJndiResource value);
	public WS70ExternalJndiResource newWS70ExternalJndiResource();

	public void setWS70JdbcResource(int index, WS70JdbcResource value);
	public WS70JdbcResource getWS70JdbcResource(int index);
	public int sizeWS70JdbcResource();
	public void setWS70JdbcResource(WS70JdbcResource[] value);
	public WS70JdbcResource[] getWS70JdbcResource();
	public int addWS70JdbcResource(WS70JdbcResource value);
	public int removeWS70JdbcResource(WS70JdbcResource value);
	public WS70JdbcResource newWS70JdbcResource();

	public void setWS70MailResource(int index, WS70MailResource value);
	public WS70MailResource getWS70MailResource(int index);
	public int sizeWS70MailResource();
	public void setWS70MailResource(WS70MailResource[] value);
	public WS70MailResource[] getWS70MailResource();
	public int addWS70MailResource(WS70MailResource value);
	public int removeWS70MailResource(WS70MailResource value);
	public WS70MailResource newWS70MailResource();

        
        public void write(java.io.File f) throws java.io.IOException, Schema2BeansRuntimeException;
}
