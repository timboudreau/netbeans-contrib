/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views;

import java.util.*;
import java.io.*;

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem; // override java.io.FileSystem
import org.openide.TopManager;
import org.openide.ErrorManager;
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;
import java.util.*;

import org.netbeans.modules.vcscore.versioning.*;


/** 
 * Children.
 * permitted keys are FileVcsInfo objects..
 *
 * @author Milos kleint
 */
public class FileVcsInfoChildren extends Children.Keys  {

    private boolean filteredListCreated;
    private boolean addNotifyCalled;
    private List keyList; 
    private ChildrenInfoFilter filter;
    private List filteredList;
    private WeakSet weakNodesSet;
    
    public FileVcsInfoChildren() {
        super();
        filteredListCreated = false;
        addNotifyCalled = false;
        keyList = Collections.synchronizedList(new ArrayList());
        filteredList = new ArrayList();
        weakNodesSet = new WeakSet();
        
    }
    
    
    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        if (!filteredListCreated) {
            Collections.sort(keyList, new FileVcsInfoComparator());
            createFilteredList();
        }
        setFilteredKeys();
        addNotifyCalled = true;
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
        addNotifyCalled = false;
        filteredListCreated = false;
        filteredList.clear();
    }

    public void updateFilter() {
        if (filteredListCreated) {
            Collections.sort(keyList, new FileVcsInfoComparator());
            createFilteredList();
            if (addNotifyCalled) {
                setFilteredKeys();
            }
        }
    }

    private List createFilteredList() {
        List toReturn = filteredList;
        toReturn.clear();
        Iterator it = keyList.iterator();
        while (it.hasNext()) {
            FileVcsInfo info = (FileVcsInfo)it.next();
            if (filter != null) {
                if (filter.checkFileInfo(info)) {
                    toReturn.add(info);
                } 
            } else {
                toReturn.add(info);
            }
        }
        filteredListCreated = true;
        return toReturn;
    }
    
    private void setFilteredKeys() {
//        System.out.println("setting keys for=" + this.hashCode());
        setKeys(filteredList);
    }
    
    
    public void addKey(FileVcsInfo info) {
        keyList.add(info);
        updateFilter();
    }
    
    public void removeKey(FileVcsInfo info) {
        keyList.remove(info);
        filteredList.remove(info);
        updateFilter();
    }
    
    public FileVcsInfo findKeyByFileName(String name) {
        List newList = new ArrayList(keyList);
        Iterator it = newList.iterator();
        while (it.hasNext()) {
            FileVcsInfo info = (FileVcsInfo)it.next();
            if (name.equals(info.getFile().getName())) {
                return info;
            }
        }
        return null;
    }
    
    
    /**
     * Returns the unfiltered list of keys..
     */
    public Iterator getAllKeys() {
        List newList = new ArrayList(keyList);
        return newList.iterator();
    }
    
    public Iterator getFilteredKeys() {
        if (!filteredListCreated) {
            Collections.sort(keyList, new FileVcsInfoComparator());
            createFilteredList();
        }
        return new ArrayList(filteredList).iterator();
    }
    
    public int getTotalKeyCount() {
        return keyList.size();
    }

    /**
     * PENDING.. is not always uptodate..
     */
    public int getFilteredKeyCount() {
        return filteredList.size();
    }
    
    
    void setChildrenNodesFilter(ChildrenInfoFilter filter) {
        this.filter = filter;
        if (filteredListCreated) {
            updateFilter();
        }
    }
    
    public void refreshThisKey(Object key) {
        super.refreshKey(key);
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {
        if (!(key instanceof FileVcsInfo)) {
            return new Node[0];
        }
        FileVcsInfo info = (FileVcsInfo)key;
        DataObject dobj = findVersioningDO(info);
        if (dobj != null) {
//            System.out.println("creating node for =" + info.getFile().getAbsolutePath());
            Node newNode = null;
            if (!info.getChildren().equals(Children.LEAF)) {
                info.replaceChildrenWithClone();
                newNode = new FileInfoNode(dobj, info);
                // iterate through the existing nodes..
/*                Iterator it = weakNodesSet.iterator();
                while (it.hasNext()) {
                    Node nd = (Node)it.next();
                    FileVcsInfo weakinfo = (FileVcsInfo)nd.getCookie(FileVcsInfo.class);
                    if (weakinfo != null && weakinfo.equals(info)) {
                        System.out.println("found older version..");
                        newNode = nd;
                        break;
                    }
                }
                if (newNode == null) {
                    newNode = new FileInfoNode(dobj, info);
                    weakNodesSet.add(newNode);
                }
 */
            } else {
                newNode = new FileInfoNode(dobj, info);
            }
            return new Node[] {newNode};
        } else {
            // try to replace the missing DataObject with something meaningful.
            //TODO
            if (!info.getChildren().equals(Children.LEAF)) {
                info.replaceChildrenWithClone();
            }
            Node nd = new FileInfoNode(new DummyNode(info), info);

            return new Node[] {nd};
        }
    }
    
  /**
   * Tries to find the right versioning dataobject for the filevcsInfo,
   * in order to construct the right node.
   */
    
    static DataObject findVersioningDO(FileVcsInfo info) {
        VersioningRepository rep = VersioningRepository.getRepository();
        File fl = info.getFile();
//        System.out.println("file=" + fl.getAbsolutePath());
        if (fl != null) {
            try {
                String fileName = fl.getCanonicalPath();
                File file = fl.getCanonicalFile();
                java.util.List fsList = rep.getVersioningFileSystems();
//                System.out.println("fslist.length=" + fsList.size());
                FileSystem fs = null;
                Iterator it = fsList.iterator();
                while (it.hasNext()) {
                    fs = (FileSystem)it.next();
                    if (fs != null) {
                        File root = FileUtil.toFile(fs.getRoot());
//                        System.out.println("checking a versioning fs.." + root);
                        if (root == null) continue;
                        String rootName = root.getCanonicalPath();
                        
                        /**root is parent of file*/
                        if (fileName.indexOf(rootName) == 0) {
                            String res = fileName.substring(rootName.length()).replace(File.separatorChar, '/');
                            FileObject fo = fs.findResource(res);
                            File  file2Fo = (fo != null)? FileUtil.toFile(fo) : null;
                            if (fo != null && file2Fo != null &&
                                file.equals(file2Fo.getCanonicalFile())) {
//                                    System.out.println("we found the fileobject...");
                                // now we've found the fileobject..
                                try {
                                    DataObject dobj = DataObject.find(fo);
                                    return dobj;
                                } catch (DataObjectNotFoundException exc) {
                                    exc.printStackTrace();
                                    continue;
                                }
                            }
                            
                        }
                    } else {
//                        System.out.println("a bad versioning filesystemm...");
//                        System.out.println("fs=" + fs);
                    }
                }
//                System.out.println("end of iteration of versioning filesystems..");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /** Special handling for clonning.
     */
    public Object clone() {
        FileVcsInfoChildren childs = new FileVcsInfoChildren();
        childs.initialize(keyList, filter, filteredList, filteredListCreated);
        return childs;
    }

    /**
     * call only from within clone()..
     */
    void initialize(List keyList, ChildrenInfoFilter filter, 
                    List filterList, boolean filterListCreated) {
        this.keyList = keyList;
        this.filter = filter;
        this.filteredListCreated = filteredListCreated;
        this.filteredList = filterList;
    }
    
    private static class FileVcsInfoComparator implements java.util.Comparator {
        
        /** Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         *
         * The implementor must ensure that <tt>sgn(compare(x, y)) ==
         * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
         * implies that <tt>compare(x, y)</tt> must throw an exception if and only
         * if <tt>compare(y, x)</tt> throws an exception.)<p>
         *
         * The implementor must also ensure that the relation is transitive:
         * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
         * <tt>compare(x, z)&gt;0</tt>.<p>
         *
         * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
         * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
         * <tt>z</tt>.<p>
         *
         * It is generally the case, but <i>not</i> strictly required that
         * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
         * any comparator that violates this condition should clearly indicate
         * this fact.  The recommended language is "Note: this comparator
         * imposes orderings that are inconsistent with equals."
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         * 	       first argument is less than, equal to, or greater than the
         * 	       second.
         * @throws ClassCastException if the arguments' types prevent them from
         * 	       being compared by this Comparator.
         */
        public int compare(Object o1, Object o2) {
            FileVcsInfo info1 = (FileVcsInfo)o1;
            FileVcsInfo info2 = (FileVcsInfo)o2;
//            System.out.println("comparing " + info1.getFile().getName() + " and" + info2.getFile().getName());
            if (info1.getFile().isDirectory() && (!info2.getFile().isDirectory())) {
//                System.out.println("1");
                return -1;
            }
            if ((!info1.getFile().isDirectory()) && info2.getFile().isDirectory()) {
//                System.out.println("2");
                return 1;
            }
            return info1.getFile().compareTo(info2.getFile());    
        }
        
    }
    
    private static class DummyNode extends AbstractNode {
        private FileVcsInfo info;
        
        public DummyNode(FileVcsInfo info) {
            super(info.getChildren());
            this.info = info;
            setName(info.getFile().getName());
        }
        
        public org.openide.nodes.Node.Cookie getCookie(Class clazz) {
            if (clazz.equals(FileVcsInfo.class)) {
                return info;
            }
            return super.getCookie(clazz);
        }        
        
    }
}
