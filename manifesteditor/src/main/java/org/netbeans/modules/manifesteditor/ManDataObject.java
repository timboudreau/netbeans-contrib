/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans.
 *
 * Portions Copyrighted 2006 Sun Microsystems, Inc.
 */
package org.netbeans.modules.manifesteditor;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.Children;
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
        DataNode n = new DataNode(this, Children.LEAF);
        n.setIconBaseWithExtension("org/netbeans/modules/manifesteditor/manifest.png");
        return n;
    }

    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public <T extends Cookie> T getCookie(Class<T> type) {
        return lookup.lookup(type);
    }
    
}
