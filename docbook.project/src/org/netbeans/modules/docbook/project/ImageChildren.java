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
/*
 * ImageChildren.java
 *
 * Created on October 16, 2006, 11:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.docbook.project;

import java.util.Arrays;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Tim Boudreau
 */
public class ImageChildren extends FilterNode.Children {
    private static final List exts = Arrays.asList(new String[] {
        "jpg", "jpeg", "gif", "png" //XXX get from 1.6 imageio method
    });
    public ImageChildren(FileObject folder) throws DataObjectNotFoundException {
        super (DataObject.find (folder).getNodeDelegate());
    }

    protected Node[] createNodes(Node key) {
        DataObject ob = key.getLookup().lookup(DataObject.class);
        if (ob != null) {
            FileObject fob = ob.getPrimaryFile();
            if (exts.contains(fob.getExt().toLowerCase())) {
                return super.createNodes (key);
            }
        }
        return new Node[0];
    }
    
    
    
}
