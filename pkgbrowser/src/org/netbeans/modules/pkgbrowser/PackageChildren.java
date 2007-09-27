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
package org.netbeans.modules.pkgbrowser;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.JavaPackage;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Displays children of a JavaPackage.
 *
 * @author Timothy Boudreau
 */
class PackageChildren extends Children.Keys implements Runnable, Comparator {
    private final String pkg;
    private final RequestProcessor rp;
    private final Object lock = new Object();
    
    public PackageChildren(String pkg, RequestProcessor rp) {
        this.pkg = pkg;
        this.rp = rp;
    }
    
    private Task task = null;
    public void addNotify() {
        setKeys (new String[] { NbBundle.getMessage(PackageChildren.class, 
                "LBL_WAIT") });
        synchronized (lock) {
            if (task == null) {
                task = rp.post(this);
            }
        }
    }
    
    public void removeNotify() {
        synchronized (lock) {
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
    }
    
    protected Node[] createNodes(Object key) {
        if (key instanceof String) {
            Node result = new AbstractNode (Children.LEAF);
            result.setDisplayName(key.toString());
            return new Node[] { result };
        } else {
            return new Node[] { (Node) key };
        }
    }

    public void run() {
        synchronized (lock) {
            task = null;
        }
        setKeys (findClasses());
    }
    
    private List findClasses() {
        assert !EventQueue.isDispatchThread();
        JavaMetamodel.getManager().waitScanFinished();
        JavaMetamodel.getDefaultRepository().beginTrans(false); 
        try {
            JavaPackage pkg = findPackage();
            if (pkg != null) {
                List keys = new ArrayList();
                Collection res = pkg.getResources();
                for (Iterator i = res.iterator(); i.hasNext();) {
                    Resource resource = (Resource) i.next();
                    List contents = resource.getClassifiers();
                    for (Iterator j = contents.iterator(); j.hasNext();) {
                        Object o = (Object) j.next();
                        if (o instanceof ClassDefinition) {
                            keys.add (o);
                        }
                        if (Thread.interrupted()) {
                            return Collections.EMPTY_LIST;
                        }
                    }
                }
                if (keys.isEmpty()) {
                    keys.add (NbBundle.getMessage(PackageChildren.class,
                            "LBL_NO_CLASSES")); //NOI18N
                }
                Collections.sort(keys, this);
                return createNodes(keys);
            } else {
                return Collections.singletonList(NbBundle.getMessage(
                        PackageChildren.class, "LBL_NO_CLASSES")); //NOI18N
            }
        } finally {
            JavaMetamodel.getDefaultRepository().endTrans();
        }
    }
    
    private List createNodes (List cds) {
        //Have to create our nodes here while holding the transaction lock.
        //Pity, it's expensive and we don't know that we really need them
        
        List result = new ArrayList (cds.size());
        for (Iterator i = cds.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof String) {
                //We had an empty list and just put <no classes> in it
                return cds;
            }
            ClassDefinition clazz = (ClassDefinition) o;
            Node nd = nodeFor(clazz);
            
            //Ensure the ClassDefinition is in the Lookup for our node
            Lookup lookup = new ProxyLookup(new Lookup[] { 
                    Lookups.singleton(clazz),
                    nd.getLookup(),
            });
            
            Node clazzNode = new FilterNode (nd, new FlatteningChildren(2, nd, rp),
                    lookup);
            result.add (clazzNode);
            if (Thread.interrupted()) {
                return Collections.EMPTY_LIST;
            }
        }
        return result;
    }
    
    private JavaPackage findPackage() {
        return PackagesRootChildren.resolvePackage(pkg);
    }
    
    private static String simpleName (String nm) {
        int ix = nm.lastIndexOf('.');
        if (ix > 0 && ix != nm.length() - 1) {
            return nm.substring (ix + 1);
        } else {
            return nm;
        }
    }
    
    private Node nodeFor (ClassDefinition clazz) {
        Resource r = clazz.getResource();
        boolean set = false;
        FileObject fob = JavaModel.getFileObject(r);
        if (fob != null) {
            DataObject ob;
            try {
                ob = DataObject.find(fob);
                FilterNode result = new FilterNode(ob.getNodeDelegate());
                result.setDisplayName (simpleName(clazz.getName()));
                return result;
            } catch (DataObjectNotFoundException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return new AbstractNode (Children.LEAF);
    }

    public int compare(Object o1, Object o2) {
        ClassDefinition a = (ClassDefinition) o1;
        ClassDefinition b = (ClassDefinition) o2;
        return simpleName(a.getName()).compareToIgnoreCase(simpleName(
                b.getName()));
    }
}
