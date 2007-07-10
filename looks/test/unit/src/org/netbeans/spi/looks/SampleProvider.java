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

package org.netbeans.spi.looks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.netbeans.api.nodes2looks.Nodes;
import org.openide.nodes.Node;

/** LookSelector for testing. Return SampleLook for SampleRepObject and
 * Looks.bean for other types.
 *
 * @author Petr Hrebejk
 */
public class SampleProvider implements LookProvider {

    private Look lookForSampleRepObject;

    private List sampleRepObjectLooks = new ArrayList();
    private List nodeLooks = new ArrayList();
    private List otherObjectsLooks = new ArrayList();
    
    
    public SampleProvider(Look lookForSampleRepObject) {
        
        if ( lookForSampleRepObject != null ) {
            sampleRepObjectLooks.add( lookForSampleRepObject ); 
        }        
        sampleRepObjectLooks.add( Looks.bean() );
        
        nodeLooks.add( Looks.bean() );
        nodeLooks.add( Nodes.nodeLook() );
        
        otherObjectsLooks.add( Looks.bean() );
        
    }
        
    public Enumeration getLooksForObject(Object representedObject) {
        if ( representedObject instanceof SampleRepObject ) {
            return Collections.enumeration( sampleRepObjectLooks );
        }
        else if ( representedObject instanceof Node ) {
            return Collections.enumeration( nodeLooks );
        }
        else {
            return Collections.enumeration( otherObjectsLooks );
        }
    }
    
    public String getName() {
        return "Sample Look";
    }
    
    public String getDisplayName() {
        return getName();
    }
    
}
