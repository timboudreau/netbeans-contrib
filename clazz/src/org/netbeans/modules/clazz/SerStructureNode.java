/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import java.io.*;
import java.util.*;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.NbBundle;

/** Nodes representing pieces of a serialized file structurally.
 * @author Jesse Glick
 */
public abstract class SerStructureNode {
    
    private SerStructureNode() {/* do not instantiate me */}
    
    public static final class StreamNode extends AbstractNode {
        public StreamNode(SerParser.Stream s) {
            super(new GeneralChildren(s.contents));
            setName(NbBundle.getMessage(SerStructureNode.class, "LBL_ser_stream"));
            setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
        }
    }
    
    private static class GeneralChildren extends Children.Keys {
        private final List things;
        public GeneralChildren(List things) {
            this.things = things;
        }
        protected void addNotify() {
            super.addNotify();
            setKeys(things);
        }
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }
        protected Node[] createNodes(Object key) {
            return new Node[] {createNode(key)};
        }
        protected Node createNode(Object key) {
            if (key instanceof SerParser.NameValue) {
                SerParser.NameValue nv = (SerParser.NameValue)key;
                Node n = createNode(nv.value);
                n.setName(prettify(nv.name.type) + " " + nv.name.name + " = " + n.getName());
                return n;
            } else if (key instanceof SerParser.ObjectWrapper) {
                SerParser.ObjectWrapper ow = (SerParser.ObjectWrapper)key;
                String name = prettify(ow.classdesc.name);
                Children ch;
                if (name.equals("org.openide.util.io.NbMarshalledObject")) { // NOI18N
                    // This is special!
                    ch = new NbMarshalledObjectChildren(ow);
                } else {
                    ch = new GeneralChildren(ow.data);
                }
                AbstractNode n = new AbstractNode(ch);
                n.setName(NbBundle.getMessage(SerStructureNode.class, "LBL_instance_of", name));
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            } else if (key instanceof SerParser.ArrayWrapper) {
                SerParser.ArrayWrapper aw = (SerParser.ArrayWrapper)key;
                AbstractNode n = new AbstractNode(new GeneralChildren(aw.values));
                if (! aw.classdesc.name.startsWith("[")) throw new IllegalStateException("Strange array name: " + aw.classdesc.name); // NOI18N
                n.setName(prettify(aw.classdesc.name.substring(1, aw.classdesc.name.length())) + "[" + aw.values.size() + "]"); // NOI18N
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            } else if (key instanceof byte[]) {
                // Block data.
                AbstractNode n = new AbstractNode(Children.LEAF);
                n.setName(SerParser.hexify((byte[])key));
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            } else if (key instanceof SerParser.ClassDesc) {
                AbstractNode n = new AbstractNode(Children.LEAF);
                n.setName("class " + prettify(((SerParser.ClassDesc)key).name)); // NOI18N
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            } else if (key == SerParser.NULL) {
                AbstractNode n = new AbstractNode(Children.LEAF);
                n.setName("null"); // NOI18N
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            } else if (key instanceof String) {
                AbstractNode n = new AbstractNode(Children.LEAF);
                n.setName("\"" + (String)key + "\""); // NOI18N
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            } else if ((key instanceof Boolean) || (key instanceof Character) ||
                       (key instanceof Byte) || (key instanceof Short) ||
                       (key instanceof Integer) || (key instanceof Long) ||
                       (key instanceof Float) || (key instanceof Double)) {
                AbstractNode n = new AbstractNode(Children.LEAF);
                n.setName(key.toString());
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            } else {
                // ????
                AbstractNode n = new AbstractNode(Children.LEAF);
                n.setName("What is this? " + key + " [" + key.getClass().getName() + "]"); // NOI18N
                n.setIconBase("org/netbeans/modules/clazz/resources/serAlone"); // NOI18N
                return n;
            }
        }
    }
    
    private static String prettify(String type) {
        if (type.equals("B")) { // NOI18N
            return "byte"; // NOI18N
        } else if (type.equals("S")) { // NOI18N
            return "short"; // NOI18N
        } else if (type.equals("I")) { // NOI18N
            return "int"; // NOI18N
        } else if (type.equals("J")) { // NOI18N
            return "long"; // NOI18N
        } else if (type.equals("F")) { // NOI18N
            return "float"; // NOI18N
        } else if (type.equals("D")) { // NOI18N
            return "double"; // NOI18N
        } else if (type.equals("C")) { // NOI18N
            return "char"; // NOI18N
        } else if (type.equals("Z")) { // NOI18N
            return "boolean"; // NOI18N
        } else if (type.startsWith("L") && type.endsWith(";")) { // NOI18N
            String fqn = type.substring(1, type.length() - 1).replace('/', '.').replace('$', '.'); // NOI18N
            if (fqn.startsWith("java.lang.")) { // NOI18N
                fqn = fqn.substring(10, fqn.length());
            }
            return fqn;
        } else if (type.startsWith("[")) { // NOI18N
            return prettify(type.substring(1, type.length())) + "[]"; // NOI18N
        } else {
            // Should not happen.
            return "ILLEGAL<" + type + ">"; // NOI18N
        }
    }
    
    private static final class NbMarshalledObjectChildren extends Children.Keys {
        private final SerParser.ObjectWrapper ow;
        public NbMarshalledObjectChildren(SerParser.ObjectWrapper ow) {
            this.ow = ow;
        }
        protected void addNotify() {
            super.addNotify();
            setKeys(Collections.singleton(Boolean.TRUE));
        }
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }
        protected Node[] createNodes(Object key) {
            List pairs = ow.data;
            Iterator it = pairs.iterator();
            while (it.hasNext()) {
                Object pair = (Object)it.next();
                if (pair instanceof SerParser.NameValue) {
                    SerParser.NameValue nv = (SerParser.NameValue)pair;
                    if (nv.name.name.equals("objBytes") && nv.name.type.equals("[B")) { // NOI18N
                        SerParser.ArrayWrapper aw = (SerParser.ArrayWrapper)nv.value;
                        List vals = aw.values;
                        byte[] b = new byte[vals.size()];
                        for (int i = 0; i < b.length; i++) {
                            b[i] = ((Byte)vals.get(i)).byteValue();
                        }
                        InputStream is = new ByteArrayInputStream(b);
                        try {
                            SerParser.Stream stream = new SerParser(is).parse();
                            return new Node[] {new SerStructureNode.StreamNode(stream)};
                        } catch (SerParser.CorruptException spce) {
                            TopManager.getDefault().getErrorManager().notify(ErrorManager.INFORMATIONAL, spce);
                            return new Node[] {};
                        } catch (IOException ioe) {
                            TopManager.getDefault().getErrorManager().notify(ioe);
                            return new Node[] {};
                        } catch (RuntimeException re) {
                            TopManager.getDefault().getErrorManager().notify(re);
                            return new Node[] {};
                        }
                    }
                }
            }
            // Improper ser state.
            return new Node[] {};
        }
    }
    
}
