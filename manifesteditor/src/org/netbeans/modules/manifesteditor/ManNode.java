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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;


/** A node to represent on set of Manifest attributes.
 */
class ManNode extends AbstractNode {
    private ManNode(String name, Attributes attrs) {
        super(Children.LEAF);
        setName(name);
        
        if ("Main".equals(name)) {
            setDisplayName(NbBundle.getMessage(ManNode.class, "CTL_MainAttributes"));
        } else {
            setDisplayName(NbBundle.getMessage(ManNode.class, "CTL_Attributes", name));
        }
    }
    
    public static Node createManifestModel(Manifest mf) {
        Map<String, Attributes> en;
        en = new LinkedHashMap<String, Attributes>();
        en.put("Main", mf.getMainAttributes());
        en.putAll(mf.getEntries());
        return new AbstractNode(new Entries(en));
    }

    protected Sheet createSheet() {
        Sheet retValue;
        retValue = super.createSheet();
        return retValue;
    }
    
    
    private static class Entries extends Children.Keys<String> {
        private java.util.Map<String,Attributes> entries;
        
        public Entries(java.util.Map<String,Attributes> entries) {
            this.entries = entries;
            setKeys(entries.keySet());
        }

        protected Node[] createNodes(String key) {
            return new Node[] { new ManNode(key, entries.get(key)) };
        }
    }
}
