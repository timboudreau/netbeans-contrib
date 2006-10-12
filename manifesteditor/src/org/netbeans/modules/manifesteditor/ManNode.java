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
class ManNode extends AbstractNode {
    private Attributes attrs;
    
    private ManNode(String name, Attributes attrs) {
        super(Children.LEAF);
        setName(name);
        
        if ("Main".equals(name)) {
            setDisplayName(NbBundle.getMessage(ManNode.class, "CTL_MainAttributes"));
        } else {
            setDisplayName(NbBundle.getMessage(ManNode.class, "CTL_Attributes", name));
        }
        
        this.attrs = attrs;
    }
    
    public static Node createManifestModel(Manifest mf) {
        Map<String, Attributes> en;
        en = new LinkedHashMap<String, Attributes>();
        en.put("Main", mf.getMainAttributes());
        en.putAll(mf.getEntries());
        return new AbstractNode(new Entries(en));
    }

    protected synchronized Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set ps = new Sheet.Set();
        ps.setName("Attributes");
        ps.setDisplayName(NbBundle.getMessage(ManNode.class, "PROP_Attributes"));
        ps.setShortDescription(NbBundle.getMessage(ManNode.class, "HINT_Attributes"));
        
        
        refreshSheet(ps);

        sheet.put(ps);
        
        return sheet;
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
        
        public Entries(java.util.Map<String,Attributes> entries) {
            this.entries = entries;
            setKeys(entries.keySet());
        }

        protected Node[] createNodes(String key) {
            return new Node[] { new ManNode(key, entries.get(key)) };
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
            throw new IllegalArgumentException("Not implemented yet");
        }
    }
}
