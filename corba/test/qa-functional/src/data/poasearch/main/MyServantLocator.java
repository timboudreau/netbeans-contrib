/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package main;

import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.ServantLocatorPOA;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.PortableServer.ForwardRequest;

public class MyServantLocator extends org.omg.PortableServer.ServantLocatorPOA {

    public Servant preinvoke(byte[] oid, POA adapter, String operation, CookieHolder the_cookie)
	throws ForwardRequest {
            return null;
    }

    public void postinvoke(byte[] oid, POA adapter, String operation, Object the_cookie, Servant the_servant) {
    }	

}

