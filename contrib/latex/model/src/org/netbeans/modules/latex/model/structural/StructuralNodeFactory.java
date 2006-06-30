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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNode;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public final class StructuralNodeFactory {
    
    /** Creates a new instance of StructuralNodeFactory */
    public StructuralNodeFactory() {
    }

    private static synchronized List getNodeProviders() {
        FileObject parsersFolder = Repository.getDefault().getDefaultFileSystem().findResource("latex/structural/nodes");
        
        try {
            DataObject od            = DataObject.find(parsersFolder);
            
            if (od instanceof DataFolder) {
                FolderLookup flookup = new FolderLookup((DataFolder) od);
                
                flookup.run();
                
                Lookup l = flookup.getLookup();
                Lookup.Result result = l.lookup(new Lookup.Template(NodeProvider.class));
                
                return new ArrayList(result.allInstances());
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        return Collections.EMPTY_LIST;
    }
    
    public static Node createNode(StructuralElement el) {
        try {
            for (Iterator i = getNodeProviders().iterator();  i.hasNext(); ) {
                NodeProvider provider = (NodeProvider) i.next();
                
                Node node = provider.createNode(el);
                
                if (node != null)
                    return node;
            }
            
            return new StructuralNode(el);
        } catch (IntrospectionException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return null;
    }
   
}
