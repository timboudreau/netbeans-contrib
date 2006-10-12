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
 * The Original Software is NetBeans. 
 *
 * Portions Copyrighted 2006 Sun Microsystems, Inc.
 */
package org.netbeans.modules.manifesteditor;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class ManDataObject extends MultiDataObject 
implements Lookup.Provider {
    final InstanceContent ic;
    private AbstractLookup lookup;
    
    public ManDataObject(FileObject pf, ManDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        
        ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
        ic.add(ManEditor.create(this));
        ic.add(this);
    }
    
    protected Node createNodeDelegate() {
        return new ManDataNode(this);
    }

    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public <T extends Cookie> T getCookie(Class<T> type) {
        return lookup.lookup(type);
    }
    
}
