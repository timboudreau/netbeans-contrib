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
