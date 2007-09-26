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

package org.netbeans.modules.apisupport.beanbrowser;

import java.awt.Container;
import java.awt.datatransfer.Clipboard;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/** The fun stuff.
 * Represents all the children of a wrapper node, including
 * lots of special items for certain node types.
 */
class WrapperKids extends Children.Keys implements Cloneable {
    
    // Special keys:
    private static final Object instanceKey = new Object() {
        public String toString() {
            return "Key for instance cookie.";
        }
    };
    private static final Object rawBeanPropsKey = new Object() {
        public String toString() {
            return "Key for raw bean properties.";
        }
    };
    private static final class NormalChildKey {
        private final Node child;
        public NormalChildKey(Node child) {
            this.child = child;
        }
        public Node wrap() {
            return Wrapper.make(child);
        }
    }
    private static final class LookupProviderKey {
        public final Lookup.Provider p;
        public LookupProviderKey(Lookup.Provider p) {
            this.p = p;
        }
    }
    
    private Node original;
    private NodeListener nListener = null;
    private PropertyChangeListener fsListener = null;
    private FileSystem fileSystemToListenOn = null;
    
    WrapperKids(Node orig) {
        original = orig;
    }
    // Probably not needed:
    public Object clone() {
        return new WrapperKids(original);
    }
    
    /** Update all keys.
     * Keys may be:
     * <ol>
     * <li> normalKey, for the original node's children.
     * <li> A node property set--i.e. Properties, Expert.
     * <li> instanceKey, if it is an instance.
     * <li> A {@link Method} for cookies, representing the method to get a cookie from the object.
     * <li> Itself (the instance) if a Node, Container, FileSystem, FileObject, or Clipboard.
     * </ol>
     */
    private void updateKeys(final boolean addListeners) {
        //Thread.dumpStack ();
        //System.err.println ("original's class: " + original.getClass ().getName ());
        Children.MUTEX.postWriteRequest(new Runnable() { public void run() {
            List newkeys = new ArrayList();
            Node[] children = original.getChildren().getNodes(/*intentionally:*/false);
            for (int i = 0; i < children.length; i++) {
                newkeys.add(new NormalChildKey(children[i]));
            }
            newkeys.addAll(makePSKeys());
            // For BeanNode, we assume that we already are displaying the "instance" right here anyway.
            if (! (original instanceof BeanNode) && original.getCookie(InstanceCookie.class) != null)
                newkeys.add(instanceKey);
            // BeanNode's which are actually representing interesting objects:
            if (original instanceof BeanNode) {
                newkeys.add(rawBeanPropsKey);
                try {
                    InstanceCookie cookie = (InstanceCookie) original.getCookie(InstanceCookie.class);
                    Object instance = cookie.instanceCreate();
                    Class[] recognized = { Node.class, Container.class, FileSystem.class, FileObject.class, Clipboard.class };
                    for (int i = 0; i < recognized.length; i++)
                        if (recognized[i].isInstance(instance))
                            newkeys.add(instance);
                    // Special listener handling:
                    if (instance instanceof FileSystem)
                        fileSystemToListenOn = (FileSystem) instance;
                    if (instance instanceof Lookup.Provider) {
                        newkeys.add(new LookupProviderKey((Lookup.Provider)instance));
                    }
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            } else {
                newkeys.add(new LookupProviderKey(original));
            }
            setKeys(newkeys);
            //System.err.println ("Setting keys for wrapper of " + original.getDisplayName () + "; count: " + newkeys.size ());
            if (addListeners) {
                nListener = new NodeAdapter() {
                    public void propertyChange(PropertyChangeEvent ev) {
                        if (Node.PROP_PROPERTY_SETS.equals(ev.getPropertyName())) {
                            updateKeys(false);
                        }
                    }
                    // Could instead override filterChildren* methods:
                    public void childrenAdded(NodeMemberEvent ev) {
                        updateKeys(false);
                    }
                    public void childrenRemoved(NodeMemberEvent ev) {
                        updateKeys(false);
                    }
                    public void childrenReordered(NodeReorderEvent ev) {
                        updateKeys(false);
                    }
                };
                original.addNodeListener(nListener);
                if (fileSystemToListenOn != null) {
                    fsListener = new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent ev) {
                            if (FileSystem.PROP_ROOT.equals(ev.getPropertyName())) {
                                updateKeys(false);
                            }
                        }
                    };
                    fileSystemToListenOn.addPropertyChangeListener(fsListener);
                }
            }
        }});
    }
    
    /** Set the keys and attach a listener to the original node.
     * If its list of children is changed, the normalKey
     * may be added or removed.
     */
    protected void addNotify() {
        //System.err.println ("addNotify called for wrapper of " + original.getDisplayName ());
        updateKeys(true);
    }
    
    protected void removeNotify() {
        if (nListener != null) {
            original.removeNodeListener(nListener);
            nListener = null;
        }
        if (fsListener != null) {
            fileSystemToListenOn.removePropertyChangeListener(fsListener);
            fsListener = null;
            fileSystemToListenOn = null;
        }
        setKeys(Collections.EMPTY_SET);
    }
    
    /** Make a list of property set keys.
     * One key (a Node.PropertySet) is added for every property set
     * which contains at least one property which is not a primitive
     * or of String or Class type.
     * <p> Note that it is possible for a property to be of e.g. Object type,
     * and have a displayed node, even though the actual value is a String, e.g.
     * @return a list of keys
     */
    private Collection makePSKeys() {
        Collection toret = new ArrayList();
        Node.PropertySet[] pss = original.getPropertySets();
        for (int i = 0; i < pss.length; i++) {
            Node.PropertySet ps = pss[i];
            Node.Property[] props = ps.getProperties();
            boolean useme = false;
            for (int j = 0; j < props.length; j++) {
                Node.Property prop = props[j];
                if (prop.canRead()) {
                    Class type = prop.getValueType();
                    if (! (type.isPrimitive() || type == String.class || type == Class.class)) {
                        useme = true;
                    }
                }
            }
            if (useme) toret.add(ps);
        }
        return toret;
    }
    
    /** Actual interpret a key.
     * Creates a node representing each key, e.g. a BeanNode for instanceKey,
     * or for a Node.PropertySet, a PropSet node.
     * @param key the key to interpret
     * @return the (one) node to display for it
     */
    protected Node[] createNodes(Object key) {
        if (key instanceof NormalChildKey) {
            return new Node[] {((NormalChildKey) key).wrap()};
        } else if (key instanceof Node.PropertySet) {
            // A property set with subnodes for the properties.
            return new Node[] { new PropSet(original, (Node.PropertySet) key) };
        } else if (key == rawBeanPropsKey) {
            // Raw bean properties, unfiltered by BeanInfo etc.
            try {
                InstanceCookie inst = (InstanceCookie) original.getCookie(InstanceCookie.class);
                AbstractNode n = new AbstractNode(new RawBeanPropKids(inst.instanceCreate())) {
                    public HelpCtx getHelpCtx() {
                        return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                    }
                };
                n.setName("Raw bean properties...");
                n.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
                return new Node[] { n };
            } catch (Exception e) {
                return new Node[] { Wrapper.make(PropSetKids.makeErrorNode(e)) };
            }
        } else if (key == instanceKey) {
            // Something which can provide an instance object--e.g. the deserialized object
            // from a .ser file.
            try {
                InstanceCookie inst = (InstanceCookie) original.getCookie(InstanceCookie.class);
                Node node = new RefinedBeanNode(inst.instanceCreate());
                node.setShortDescription("Instance name: `" + inst.instanceName() +
                        "'; normal node name: `" + node.getDisplayName() + "'; normal description: `" +
                        node.getShortDescription() + "'");
                node.setDisplayName("Instance of class " + inst.instanceClass().getName());
                return new Node[] { Wrapper.make(node) };
            } catch (Exception e) {
                return new Node[] { Wrapper.make(PropSetKids.makeErrorNode(e)) };
            }
        } else if (key instanceof Node) {
            List toret = new LinkedList(); // List<Node>
            // Show the actual node itself.
            AbstractNode marker = new AbstractNode(new Children.Array()) {
                public HelpCtx getHelpCtx() {
                    return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                }
            };
            marker.setName("An actual node here:");
            marker.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
            marker.getChildren().add(new Node[] { Wrapper.make((Node) key) });
            toret.add(marker);
            if (key instanceof FilterNode) {
                // Try to separately show the original too:
                try {
                    Method m = FilterNode.class.getDeclaredMethod("getOriginal", new Class[] { });
                    m.setAccessible(true);
                    try {
                        Node orig = (Node) m.invoke(key, new Object[] { });
                        AbstractNode marker2 = new AbstractNode(new Children.Array()) {
                            public HelpCtx getHelpCtx() {
                                return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                            }
                        };
                        marker2.setName("The original from the filter node:");
                        marker2.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
                        marker2.getChildren().add(new Node[] { PropSetKids.makeObjectNode(orig) });
                        toret.add(marker2);
                    } finally {
                        m.setAccessible(false);
                    }
                } catch (Exception e) {
                    toret.add(Wrapper.make(PropSetKids.makeErrorNode(e)));
                }
            }
            return (Node[]) toret.toArray(new Node[toret.size()]);
            // XXX should show getActions(true/false) rather than getActions()/getContextActions()
        } else if (key instanceof Container) {
            // An AWT Container with its subcomponents.
            Children kids = new ContainerKids((Container) key);
            AbstractNode n = new AbstractNode(kids) {
                public HelpCtx getHelpCtx() {
                    return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                }
            };
            n.setName("Components...");
            n.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
            return new Node[] { n };
        } else if (key instanceof FileSystem) {
            // "root" is not a declared Bean property of FileSystem's, so specially display it.
            try {
                Node fsn = new RefinedBeanNode(((FileSystem) key).getRoot());
                fsn.setDisplayName("[root] " + fsn.getDisplayName());
                return new Node[] { Wrapper.make(fsn) };
            } catch (IntrospectionException e) {
                return new Node[] { Wrapper.make(PropSetKids.makeErrorNode(e)) };
            }
        } else if (key instanceof FileObject) {
            FileObject fo = (FileObject)key;
            // Try to show: data object; attributes; possibly provenance.
            List l = new LinkedList(); // List<Node>
            // Display the corresponding DataObject.
            // The node delegate is also available as a Bean property of the DO.
            try {
                Node fsn = new RefinedBeanNode(DataObject.find(fo));
                fsn.setDisplayName("[data object] " + fsn.getDisplayName());
                l.add(Wrapper.make(fsn));
            } catch (Exception e) { // DataObjectNotFoundException, IntrospectionException
                l.add(Wrapper.make(PropSetKids.makeErrorNode(e)));
            }
            Children kids = new FileAttrKids(fo);
            AbstractNode attrnode = new AbstractNode(kids) {
                public HelpCtx getHelpCtx() {
                    return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                }
            };
            attrnode.setName("Attributes...");
            attrnode.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
            l.add(attrnode);
            try {
                FileSystem apparentFS = fo.getFileSystem();
                if (apparentFS instanceof MultiFileSystem) {
                    // #18698: try to show provenance of the file object.
                    FileSystem fs = apparentFS;
                    FileObject workingFO = fo;
                    boolean project = false;
                    while (fs instanceof MultiFileSystem) {
                        if (fs.getClass().getName().equals("org.netbeans.core.projects.FilterFileSystem")) {
                            // We could traverse it to the original filesystem, but that would
                            // be the $userdir/system/ LFS, which is not what we want to see.
                            project = true;
                            break;
                        }
                        Method m = MultiFileSystem.class.getDeclaredMethod("findSystem", new Class[] {FileObject.class});
                        m.setAccessible(true);
                        try {
                            //FileObject workingFO = fs.findResource(path);
                            FileSystem foundFS = (FileSystem)m.invoke(fs, new Object[] {workingFO});
                            if (foundFS == fs) {
                                // no delegate
                                //System.err.println("no delegate for " + workingFO + " on " + fs);
                                break;
                            } else {
                                Method m2 = MultiFileSystem.class.getDeclaredMethod("findResourceOn", new Class[] {FileSystem.class, String.class});
                                m2.setAccessible(true);
                                try {
                                    FileObject newFO = (FileObject)m2.invoke(fs, new Object[] {foundFS, workingFO.getPath()});
                                    if (newFO != null) {
                                        //System.err.println("delegating " + fs + "/" + workingFO + " -> " + foundFS + "/" + newFO);
                                        fs = foundFS;
                                        workingFO = newFO;
                                    } else {
                                        //System.err.println("findResourceOn for " + fs + " with " + foundFS + " and " + workingFO + " -> null");
                                    }
                                } finally {
                                    m2.setAccessible(false);
                                }
                            }
                        } finally {
                            m.setAccessible(false);
                        }
                    }
                    if (project || fs != apparentFS) {
                        String provenance = null;
                        if (project) {
                            provenance = "project";
                        } else if (fs instanceof LocalFileSystem) {
                            File dir = ((LocalFileSystem)fs).getRootDirectory();
                            // #27151: netbeans.dirs
                            StringTokenizer tok = new StringTokenizer(System.getProperty("netbeans.dirs", ""), File.pathSeparator);
                            while (tok.hasMoreTokens()) {
                                File system = new File(new File(tok.nextToken()), "system"); // NOI18N
                                if (dir.equals(system)) {
                                    provenance = "NetBeans installation";
                                    break;
                                }
                            }
                            if (provenance == null) {
                                File system1 = new File(new File(System.getProperty("netbeans.home")), "system");
                                File system2 = new File(new File(System.getProperty("netbeans.user")), "system");
                                if (dir.equals(system1)) {
                                    provenance = "NetBeans installation";
                                } else if (dir.equals(system2)) {
                                    provenance = "user directory";
                                }
                            }
                        } else if (fs instanceof XMLFileSystem) {
                            // Well, a good guess at least. Note merged XMLFS's and
                            // module cache mean we cannot easily do better.
                            provenance = "module";
                        }
                        if (provenance != null) {
                            //System.err.println("filesystem: " + fs + " path: " + workingFO);
                            l.add(PropSetKids.makePlainNode("Provenance: " + provenance));
                        } else {
                            // No well-known origin, just show the FS.
                            Node rfsn = new RefinedBeanNode(fs);
                            rfsn.setDisplayName("[original file system] " + rfsn.getDisplayName());
                            l.add(Wrapper.make(rfsn));
                        }
                    }
                }
            } catch (Exception e) {
                l.add(Wrapper.make(PropSetKids.makeErrorNode(e)));
            }
            return (Node[])l.toArray(new Node[l.size()]);
        } else if (key instanceof Clipboard) {
            Children kids = new ClipboardKids((Clipboard) key);
            AbstractNode n = new AbstractNode(kids) {
                public HelpCtx getHelpCtx() {
                    return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                }
            };
            n.setName("Transferables...");
            n.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
            return new Node[] { n };
        } else if (key instanceof LookupProviderKey) { // #26617
            Node n = LookupNode.localLookupNode(Lookups.proxy(((LookupProviderKey) key).p));
            n.setDisplayName("Cookies...");
            return new Node[] { n };
        } else {
            throw new RuntimeException("Weird key: " + key);
        }
    }
    
}
