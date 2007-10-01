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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
                n.setName(prettify(nv.name.type) + " " + nv.name.name + " = " + n.getName()); // NOI18N
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
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, spce);
                            return new Node[] {};
                        } catch (IOException ioe) {
                            ErrorManager.getDefault().notify(ioe);
                            return new Node[] {};
                        } catch (RuntimeException re) {
                            ErrorManager.getDefault().notify(re);
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
