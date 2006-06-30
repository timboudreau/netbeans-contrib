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

package data.poasupport;

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

