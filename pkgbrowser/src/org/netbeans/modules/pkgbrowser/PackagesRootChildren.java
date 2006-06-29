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
package org.netbeans.modules.pkgbrowser;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.jmi.javamodel.JavaPackage;
import org.netbeans.jmi.javamodel.JavaPackageClass;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 * Provides a set of nodes representing all known Java packages;  can do
 * string filtering to limit results.
 *
 * @author Timothy Boudreau
 */
class PackagesRootChildren extends Children.Keys implements Runnable, Comparator, Filterable {
    final RequestProcessor rp = new RequestProcessor ("Package browser", //NOI18N
            Thread.MIN_PRIORITY, true);
    
    private final Object lock = new Object();
    private Task task = null;
    
    /**
     * Creates a new instance of PackagesRootChildren
     */
    public PackagesRootChildren() {
    }
    
    RequestProcessor getRequestProcessor() {
        return rp;
    }
    
    private String filter = null;
    private Filterable.Callback callback = null;
    public void setFilter (String filter, Filterable.Callback callback) {
        if ((filter != null && !filter.equals(this.filter)) || (
                filter == null && this.filter != null)) {
            this.filter = filter;
            synchronized (lock) {
                this.callback = callback;
                if (task != null) {
                    task.cancel();
                }
                task = rp.post (this);
            }
        }
    }
        
    String getFilter() {
        return filter;
    }
    
    public void addNotify() {
        List pkgs;
        setKeys (new Object[] { NbBundle.getMessage (PackagesRootChildren.class, 
                "LBL_WAIT") }); //NOI18N
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
            AbstractNode waitNode = new AbstractNode (Children.LEAF);
            waitNode.setDisplayName (key.toString());
            return new Node[] { waitNode };
        } else {
            JavaPackage pkg = (JavaPackage) key;
            AbstractNode nd = new AbstractNode (new PackageChildren(
                    pkg.getName(), rp));
            nd.setName(pkg.getName());
            nd.setDisplayName (pkg.getName());
            nd.setIconBaseWithExtension(
                    "org/netbeans/modules/pkgbrowser/resources/package.gif"); //NOI18N
            return new Node[] { nd };
        }
    }

    public void run() {
        List pkgs = findPackages();
        synchronized (lock) {
            task = null;
        }
        setKeys (pkgs);
    }
    
    private List findPackages() {
        assert !EventQueue.isDispatchThread();
        List result = new ArrayList();
        JavaMetamodel.getManager().waitScanFinished();
        JavaMetamodel.getDefaultRepository().beginTrans(false); 
        try {
            findPackages (result);
            if (Thread.interrupted()) {
                return Collections.EMPTY_LIST;
            }
            Collections.sort(result, this);
        } finally {
            JavaMetamodel.getDefaultRepository().endTrans();
        }
        return result;
    }
    
    public List findPackages(List result) {
        String name = filter == null ? "" : filter;
        List ret = new ArrayList();
        int index = name.lastIndexOf('.'); //NOI18N
        String prefix = index > 0 ? name.substring(0, index) : ""; //NOI18N
        JavaPackage pkg = resolvePackage(prefix);
        if (pkg != null) {
            Collection subpackages = pkg.getSubPackages();
            for (Iterator it = subpackages.iterator(); it.hasNext();) {
                JavaPackage subPackage = (JavaPackage) it.next();
                ret.add (subPackage);
                if (Thread.interrupted()) {
                    return Collections.EMPTY_LIST;
                }
            }
        }

        int size = ret.size ();
        for (int x = 0; x < size; x++) {
            JavaPackage sPkg = (JavaPackage) ret.get(x);
            addSubPackages(ret, sPkg);
            if (Thread.interrupted()) {
                return Collections.EMPTY_LIST;
            }
        }
        result.addAll(ret);
        synchronized (lock) {
            if (filter != null && callback != null) {
                callback.filterSet(filter, result.size() > 0);
            }
        }
        return result;
    }
    
    static JavaPackage resolvePackage(String packageName) {
        JavaPackageClass pkgProxy = JavaModel.getDefaultExtent().getJavaPackage();
        return pkgProxy.resolvePackage(packageName);
    }    
    
    private void addSubPackages (List list, JavaPackage pkg) {
        Iterator iter = pkg.getSubPackages ().iterator ();
        while (iter.hasNext ()) {
            JavaPackage p = (JavaPackage) iter.next ();
            list.add(p);
            addSubPackages (list, p);
            if (Thread.interrupted()) {
                break;
            }
        }
    }

    public int compare(Object o1, Object o2) {
        //String compare names, but filter com.sun* and java* towards the
        //bottom of the list
        JavaPackage a = (JavaPackage) o1;
        JavaPackage b = (JavaPackage) o2;
        String an = a.getName();
        String bn = b.getName();
        boolean aIsComSun = probablyReallyUnwanted(an); //NOI18N
        boolean bIsComSun = probablyReallyUnwanted(bn); //NOI18N
        int result = a.getName().compareTo(b.getName());
        if (aIsComSun != bIsComSun) {
            if (aIsComSun) {
                result += 200000;
            } else {
                result -= 200000;
            }
        }
        boolean aIsJava = probablyUnwanted(an); 
        boolean bIsJava = probablyUnwanted(bn);
        if (aIsJava != bIsJava) {
            if (aIsJava) {
                result += 100000;
            } else {
                result -= 100000;
            }
        }
        return result;
    }
    
    private boolean probablyUnwanted (String pkgName) {
        return  pkgName.startsWith ("java.") ||        //NOI18N 
                pkgName.startsWith("javax.") ||        //NOI18N
                pkgName.startsWith("org.omg.") ||      //NOI18N
                pkgName.startsWith ("junit.") ||       //NOI18N
                pkgName.startsWith ("org.jcp") ||      //NOI18N
                pkgName.startsWith ("org.ietf") ||      //NOI18N
                pkgName.startsWith ("org.w3c") ||      //NOI18N
                pkgName.startsWith ("org.xml") ||      //NOI18N
                pkgName.startsWith ("net.java.");      //NOI18N
    }
    
    private boolean probablyReallyUnwanted (String pkgName) {
        return "com.sun".equals(pkgName) ||             //NOI18N
                pkgName.startsWith ("com.sun.") ||      //NOI18N
                "java".equals(pkgName) ||               //NOI18N
                "javax".equals(pkgName) ||             //NOI18N
                "org".equals(pkgName) ||               //NOI18N
                "com".equals(pkgName) ||               //NOI18N
                "net".equals(pkgName) ||               //NOI18N
                "junit".equals(pkgName);               //NOI18N
    }
}
