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

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;


/** A node to represent on set of Manifest attributes.
 */
final class ManNode extends AbstractNode {
    private Attributes attrs;
    private boolean sheet;
    private final ChangeCallback callback;
    
    private ManNode(String name, Attributes attrs, ChangeCallback callback) {
        super(Children.LEAF);
        setName(name);
        
        if ("Main".equals(name)) {
            setDisplayName(NbBundle.getMessage(ManNode.class, "CTL_MainAttributes"));
            setIconBaseWithExtension("org/netbeans/modules/manifesteditor/manifest.png");
        } else {
            setDisplayName(NbBundle.getMessage(ManNode.class, "CTL_Attributes", name));
            setIconBaseWithExtension("org/netbeans/modules/manifesteditor/section.png");
        }
        
        this.attrs = attrs;
        this.callback = callback;
    }
    
    public static interface ChangeCallback {
        public void change(String section, String name, String oldValue, String newValue)
        throws IllegalArgumentException;
    }
    
    public static Node createManifestModel(Manifest mf, ChangeCallback callback) {
        return new AbstractNode(new Entries(attributes(mf), callback));
    }

    private static Map<String, Attributes> attributes(final Manifest mf) {
        Map<String, Attributes> en;
        en = new LinkedHashMap<String, Attributes>();
        en.put("Main", mf.getMainAttributes());
        en.putAll(mf.getEntries());
        return en;
    }
    
    public static void refresh(Node n, Manifest mf) {
        assert n.getChildren() instanceof Entries;
        
        Entries e = (Entries)n.getChildren();
        e.refresh(attributes(mf));
    }

    protected synchronized Sheet createSheet() {
        this.sheet = true;
        
        Sheet sheet = new Sheet();
        Sheet.Set ps = new Sheet.Set();
        ps.setName("Attributes"); // NOI18N
        ps.setDisplayName(NbBundle.getMessage(ManNode.class, "PROP_Attributes"));
        ps.setShortDescription(NbBundle.getMessage(ManNode.class, "HINT_Attributes"));
        
        
        refreshSheet(ps);

        sheet.put(ps);
        
        return sheet;
    }
    
    public synchronized void refresh(Attributes attrs) {
        this.attrs = attrs;
        refreshSheet(getSheet().get("Attributes"));
    }
    
    private void refreshSheet(Sheet.Set set) {
        assert Thread.holdsLock(this);
        
        Property[] clone = (Property[])set.getProperties().clone();
        for (Property p : set.getProperties()) {
            if (!attrs.containsKey(p.getName())) {
                set.remove(p.getName());
            }
        }
        
        for (Object name : attrs.keySet()) {
            Prop p = new Prop(((Attributes.Name)name).toString());
            set.put(p);
        }
    }
    
    
    private static class Entries extends Children.Keys<String> {
        private java.util.Map<String,Attributes> entries;
        private ChangeCallback callback;
        
        public Entries(java.util.Map<String,Attributes> entries, ChangeCallback callback) {
            this.entries = entries;
            this.callback = callback;
            setKeys(entries.keySet());
        }

        protected Node[] createNodes(String key) {
            return new Node[] { new ManNode(key, entries.get(key), callback) };
        }
        
        public void refresh(java.util.Map<String,Attributes> entries) {
            this.entries = entries;
            setKeys(entries.keySet());
            
            if (isInitialized()) {
                for (Node n : getNodes()) {
                    ManNode m = (ManNode)n;
                    m.refresh(entries.get(m.getName()));
                }
            } 
        }
    }
    
    private class Prop extends PropertySupport<String> {
        public Prop(String name) {
            super(name, String.class, name, name, true, true);
        }
        
        
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return attrs.getValue(getName());
        }

        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            callback.change(
                ManNode.this.getName(),
                getName(),
                getValue(),
                val
            );
        }
    }
    
    
}
