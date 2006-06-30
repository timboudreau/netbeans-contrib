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

package org.netbeans.modules.corba.settings;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class ORBSettingsErrorHandler implements ErrorHandler {

    public static final boolean DEBUG = false;

    public ORBSettingsErrorHandler () {
    }

    public void error (SAXParseException e) throws SAXParseException {
	if (DEBUG)
	    System.err.println ("SAX error");
	throw e;
    }

    public void fatalError (SAXParseException e) throws SAXParseException {
	if (DEBUG)
	    System.err.println ("SAXParseSrror");
	throw e;	
    }
    
    public void warning (SAXParseException e) throws SAXParseException {
	if (DEBUG)
	    System.err.println ("SAXParseSrror");
	throw e;
    }
    
}
