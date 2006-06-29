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
 * The Original Software is the Accelerators module.
 * The Initial Developer of the Original Software is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.terminal;

import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 *
 * @author Andrei Badea
 */
public class Util {

    private Util() {
    }

    public static FileObject findFileObject(Node[] activatedNodes) {
        // XXX should look in all nodes
        if (activatedNodes.length < 1)
            return null;

        Node node = activatedNodes[0];
        
        FileObject result = getProjectFolder(node);
        if (result != null)
            return result;
        
        return getFileObject(node);
    }
    

    private static FileObject getProjectFolder(Node node) {
        Project p = (Project)node.getLookup().lookup(Project.class);
        if (p != null)
            return p.getProjectDirectory();
        
        return null;
    }
    
    private static FileObject getFileObject(Node node) {
        DataObject dataObject = (DataObject)node.getLookup().lookup(DataObject.class);
        if (dataObject != null)
            return dataObject.getPrimaryFile();
        
        return null;
    }
}
