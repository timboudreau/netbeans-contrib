/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
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
