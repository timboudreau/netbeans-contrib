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
package org.netbeans.modules.visual.examples.shapes.dataobject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;

public class ShapeDataObject extends MultiDataObject {

    private MyItemData data;
    
    private static final long serialVersionUID = 5776214949118746290L;
    
    public ShapeDataObject(FileObject pf, ShapeDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        
        //DND start
        //create the 'data' from properties file - something more sophisticated would be more appropriate here
        InputStream input = pf.getInputStream();
        Properties props = new Properties();
        props.load( input );
        input.close();
        data = new MyItemData( props );
        //DND end
    }
    
    protected Node createNodeDelegate() {
        return new ShapeDataNode(this, data );
    }
    
}
