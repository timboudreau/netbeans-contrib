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

package org.netbeans.modules.looks;

import org.netbeans.spi.looks.DefaultLook;
import org.netbeans.spi.looks.Look;
import org.openide.util.Lookup;

/**
 * Look to provide basic properties of a Look
 *
 * @author Jaroslav Tulach
 */
public final class LookLook extends DefaultLook {

    /** Creates new NodeProxySupport */
    public LookLook() {
        super ("LookLook");
    }

    // General methods ---------------------------------------------------------

    public void attachTo (Object representedObject) {
        /*
        return representedObject instanceof Look ? new Look.NodeSubstitute (
            representedObject, this
        ) : null;
         */
    }
    
    // Methods for STYLE -------------------------------------------------------
    
    public String getDisplayName (Object representedObject, Lookup env ) {
        return ((Look)representedObject).getDisplayName ();
    }
    
    public String getName (Object representedObject, Lookup env ) {
        return ((Look)representedObject).getName ();
    }
    
}
