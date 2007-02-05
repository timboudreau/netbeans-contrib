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
package org.netbeans.modules.j2ee.oc4j.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Michal Mocnak
 */
public class OC4JTargetNode extends AbstractNode {
    
    public OC4JTargetNode(Lookup lookup) {
        super(new Children.Array());
        
        // J2EE Applications
        getChildren().add(new Node[] {
            new OC4JItemNode(lookup,
                    new OC4JJ2EEApplicationsChildren(lookup),
                    NbBundle.getMessage(OC4JTargetNode.class, "LBL_Apps"),
                    OC4JItemNode.ItemType.J2EE_APPLICATION_FOLDER)
        });
        
        // Native Data Sources
        getChildren().add(new Node[] {
            new OC4JItemNode(lookup,
                    new OC4JNativeDataSourcesChildren(lookup),
                    NbBundle.getMessage(OC4JTargetNode.class, "LBL_NativeDataSources"),
                    OC4JItemNode.ItemType.REFRESHABLE_FOLDER)
        });
        
        // Managed Data Sources
        getChildren().add(new Node[] {
            new OC4JItemNode(lookup,
                    new OC4JManagedDataSourcesChildren(lookup),
                    NbBundle.getMessage(OC4JTargetNode.class, "LBL_ManagedDataSources"),
                    OC4JItemNode.ItemType.REFRESHABLE_FOLDER)
        });
        
        // Connection Pools
        getChildren().add(new Node[] {
            new OC4JItemNode(lookup,
                    new OC4JConnectionPoolsChildren(lookup),
                    NbBundle.getMessage(OC4JTargetNode.class, "LBL_ConnectionPools"),
                    OC4JItemNode.ItemType.REFRESHABLE_FOLDER)
        });
    }
}