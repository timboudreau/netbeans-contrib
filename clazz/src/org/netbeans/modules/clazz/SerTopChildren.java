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

package org.netbeans.modules.clazz;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.util.Collections;

import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.nodes.*;
import org.openide.src.nodes.*;

/** Children for a SerDataNode, including the SourceChildren and
 * a node for the serialized structure.
 * @author Jesse Glick
 */
public class SerTopChildren extends Children.Keys implements NodeListener {

    private final static Object SUPER_KEY = "sourcechildren"; // NOI18N
    private final static Object ADDED_KEY = "added"; // NOI18N
    
    private final DataObject obj;
    private final SourceChildren src;
    private Node dummy;
    
    public SerTopChildren(DataObject obj, SourceChildren src) {
        this.obj = obj;
        this.src = src;
    }
    
    public final SourceChildren getSourceChildren() {
        return src;
    }
    
    protected void addNotify() {
        super.addNotify();
        setKeys(new Object[] {SUPER_KEY, ADDED_KEY});
        if (dummy == null) {
            dummy = new AbstractNode(src);
        }
        dummy.addNodeListener(this);
    }
    
    protected void removeNotify() {
        dummy.removeNodeListener(this);
        setKeys(Collections.EMPTY_SET);
        super.removeNotify();
    }
    
    protected Node[] createNodes(Object key) {
        if (key == SUPER_KEY) {
            Node[] supe = src.getNodes();
            Node[] mine = new Node[supe.length];
            for (int i = 0; i < supe.length; i++) {
                mine[i] = supe[i].cloneNode();
            }
            return mine;
        } else if (key == ADDED_KEY) {
            try {
                InputStream is = obj.getPrimaryFile().getInputStream();
                try {
                    SerParser.Stream stream = new SerParser(is).parse();
                    return new Node[] {new SerStructureNode.StreamNode(stream)};
                } finally {
                    is.close();
                }
            } catch (SerParser.CorruptException spce) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, spce);
                return new Node[] {};
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
                return new Node[] {};
            } catch (RuntimeException re) {
                ErrorManager.getDefault().notify(re);
                return new Node[] {};
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public void childrenAdded(NodeMemberEvent ev) {
        refreshKey(SUPER_KEY);
    }
    public void childrenRemoved(NodeMemberEvent ev) {
        refreshKey(SUPER_KEY);
    }
    public void childrenReordered(NodeReorderEvent ev) {
        refreshKey(SUPER_KEY);
    }
    public void nodeDestroyed(NodeEvent ev) {
        // ignore
    }
    public void propertyChange(PropertyChangeEvent evt) {
        // ignore
    }
    
}
