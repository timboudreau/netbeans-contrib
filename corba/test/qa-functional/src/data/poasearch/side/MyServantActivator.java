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

package side;

import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.ServantActivatorPOA;
import org.omg.PortableServer.ForwardRequest;

public class MyServantActivator extends org.omg.PortableServer.ServantActivatorPOA {

    public Servant incarnate (byte[] oid, POA adapter)
        throws org.omg.PortableServer.ForwardRequest {
            return null;
    }

    public void etherealize (byte[] oid, POA adapter, Servant serv, boolean cleanup_in_progress, boolean remaining_activations) {
    }
}

