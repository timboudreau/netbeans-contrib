/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.terminal;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

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
