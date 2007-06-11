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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.wsdlextensions.dcom;

/**
 * Represents the address element under the wsdl port for DCOM binding
 *
 * @author Chandrakanth Belde
 */
public interface DCOMAddress extends DCOMComponent {

    public static final String DCOM_DOMAIN = "domain";
    
    public static final String DCOM_SERVER = "server";
    
    public static final String DCOM_USERNAME = "username";
    
    public static final String DCOM_PASSWORD = "password";
    
    public String getDCOMDOMAIN();

    public void setDCOMDOMAIN(String domain);

    public String getDCOMSERVER();

    public void setDCOMSERVER(String server);

    public String getDCOMUSERNAME();

    public void setDCOMUSERNAME(String username);

    public String getDCOMPASSWORD();

    public void setDCOMPASSWORD(String password);
}
