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

package org.netbeans.insane;

import java.io.*;
import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author  nenik
 */
public class InsaneParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String file =  "/tmp/insane.xml";

        if (args.length >= 1) {
            file = args[0];
        }

        HeapModel model = parseModel(file, true);

        /*
        System.out.println("starting analyzing hashmaps");
        int cnt = 0;
        int hits = 0;
        for (Iterator it = getObjectsOfType(model, "java.util.HashMap"); it.hasNext(); cnt++) {
            HeapModel.Item itm = (HeapModel.Item)it.next();
            if (isStrangeHashMap (itm)) 
                hits++;
        }
        System.out.println("checked "+cnt+" strange "+hits);
         */
        for (Iterator it = getObjectsOfType(model, "[C"); it.hasNext();) {
            HeapModel.Item itm = (HeapModel.Item)it.next();
            if ("isValid()Z".equals(itm.value)) {
                System.out.println("instance "+itm.toString());
                for (Iterator it2 = reachableBy(itm).iterator(); it2.hasNext(); ) {
                    System.out.println("\theld by "+it2.next());
                }
            }
        }
//        Set all = new HashSet(model.items);
//        printDistribution(all);
//        for (Iterator it = getObjectsOfType(model, args[0]); it.hasNext(); ) {
//            HeapModel.Item itm = (HeapModel.Item)it.next();
//            System.err.println("");
//            findRoots(model, itm.id, false);
//        }
        

//        HeapModel.Item itm = getObjectAt(model, "org.openide.ErrorManager.current");
//        printDistribution(reachableFrom(itm));
        
//        computeObjectSize("/space/nenik/work/insane-dump/editor-2/insane.xml", 0x1dc);
//        computeObjectSize("/tmp/insane.xml", 0x916);
        HeapModel.Item itm = getObjectAt(model, "org.netbeans.mdr.handlers.gen.HandlerGenerator.customImplInfos");
        printDistribution(reachableFrom(itm));
        System.out.println("\n\n");

//        itm = getObjectAt(model, "org.netbeans.editor.ext.java.JavaCompletion.finder");
//        printDistribution(reachableFrom(itm));

//        printDistribution(reachableFrom(model.getItem(model.getIdFromString("555685", false))));

//        Set s1 = new HashSet();
/*        
        Set s1 = limitedReachableFrom(model.getItem(0x372), new HashSet(Arrays.asList(new Object[] {
            "[Lorg.netbeans.editor.ext.java.JCPackage;",
            "[Lorg.netbeans.editor.ext.java.JCClass;",
            "java.util.HashMap",
            "[Ljava.util.HashMap$Entry;",
            "java.util.HashMap$Entry",
            "org.netbeans.editor.ext.java.JavaCompletion$BasePackage",
            "[Lorg.netbeans.editor.ext.java.JCClass;",
            "org.netbeans.editor.ext.java.JCFileProvider$Cls",
            "java.lang.String",
            "[C"
        })));
*/
//        s1.addAll(reachableFrom(getObjectAt(model, "org.netbeans.editor.ext.java.JavaCompletion.classCache")));
//        printDistribution(s1);
        
//        HeapModel.Item itm = findObjectReferringString(model, "org.openide.loaders.DataFolder$FolderNode", "lookandfeel");
//        System.err.println("Found item: " + itm);
//        findRoots(model, itm.id, false);
        
//        findRoots(model, 0x17c26, false, new int[] {0x4a18a});
        
//        computeInstanceSizes("/space/nenik/work/insane-dump/editor-2/insane.xml",
//            "java.util.Properties");

//        computeStringKeys("/space/nenik/work/insane-dump/editor-2/insane.xml");

//        computePropertiesSize("/space/nenik/work/insane-dump/editor-2/insane.xml");
//        computePropertiesSize("/space/nenik/work/insane-dump/editor-2/insane.xml");

//        HeapModel model = new HeapModel(new InputSource("/tmp/insane.xml"));
//        System.err.println("parsed");
//        findRoots(model, 0xb715, false, null);

        
/*        List ids = new ArrayList(); // List<int>
        List skipids = new ArrayList(); // List<int>
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("!")) {
                skipids.add(Integer.decode(args[i].substring(1)));
            } else {
                ids.add(Integer.decode(args[i]));
            }
        }
        if (ids.isEmpty()) {
            System.err.println("Usage: java InsaneParser ID { ID | !SKIPID }*");
            System.err.println("where ID is a number, maybe in hex");
            System.err.println("SKIPID likewise skips some ID");
            System.err.println("E.g.: java InsaneParser 0x123 0x45a !0x883 !0x1d2");
            System.exit(2);
        }
        int[] skipidsA = new int[skipids.size()];
        for (int i = 0; i < skipidsA.length; i++) {
            skipidsA[i] = ((Integer)skipids.get(i)).intValue();
        }
        
        HeapModel model = new HeapModel(new InputSource(
            "/tmp/insane.xml"));
        System.err.println("parsed");
        
        Iterator it = ids.iterator();
        while (it.hasNext()) {
            int id = ((Integer)it.next()).intValue();
            findRoots(model, id, false, skipidsA);
        }
 */
    }
    
    
    /** Analyzes hashmap for bad distribution of entries */
    private static boolean isStrangeHashMap (HeapModel.Item itm) {
        HeapModel.Item table = getField (itm, "java.util.HashMap.table");
        if (table == null) { 
            System.err.println("Cannot find a table in HashMap "+itm.toString());
            return true;
        }
        
        int totalItems = 0;
        int arrayItems = 0;
        
        for (Enumeration en = table.outgoing(); en.hasMoreElements(); ) {
            Object o = en.nextElement();
            if (o instanceof HeapModel.Ref) {
                o = ((HeapModel.Ref)o).getObject();
                if (o instanceof HeapModel.Item
                && "java.util.HashMap$Entry".equals(((HeapModel.Item)o).getType())) {
                    arrayItems++;
                }
            }
        }
        Set entries = new HashSet (); 
        entries.add ("java.util.HashMap$Entry");
        Set s = limitedReachableFrom (table, entries);
        totalItems = s.size();
        if (arrayItems > 0 && totalItems > 3 && totalItems / arrayItems >= 2) {
            System.out.println("Found HashMap with "+totalItems+" items total and "+arrayItems+" in array");
            for (Iterator it = reachableBy(itm).iterator(); it.hasNext(); ) {
                System.out.println(" held by "+it.next());
            }
            try {
                boolean found = false;
                for (Enumeration en = table.outgoing(); en.hasMoreElements();) {
                    HeapModel.Item elem = (HeapModel.Item)((HeapModel.Ref)en.nextElement()).getObject();
                    HeapModel.Item key = getField(elem, "java.util.HashMap$Entry.key");
                    if (key != null) {
                        if ("java.lang.String".equals(key.getType())) {
                            System.out.println(" holding String "+valueOf(key)+", "+getField(elem, "java.util.HashMap$Entry.value"));
                        }
                        else {
                            found = true;
                            System.out.println(" holding "+key+", "+getField(elem, "java.util.HashMap$Entry.value"));
                            break;
                        }
                    }
                }
                if (!found) {
                    System.err.println(" cannot found entry in table "+table);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            
//            for (Enumeration en = itm.incomming(); en.hasMoreElements(); ) {
//                Object o = en.nextElement();
//                if (o instanceof HeapModel.Ref) {
//                    String f = ((HeapModel.Ref)o).getName();
//                    o = ((HeapModel.Ref)o).getObject();
//                    if (o instanceof HeapModel.Item) {
//                        System.out.println("\theld by "+f+" in "+((HeapModel.Item)o).getType());
//                    }
//                    else if (o instanceof String) {
//                        System.out.println("\theld by "+f+" in "+o);
//                    }
//                }
//            }
            return true;
        }
        return false;
    }
    
    /** Return object referenced by given field. 
     * @param itm analyzed object
     * @param fld name of field
     * @return referenced object or <CODE>null</CODE>
     */
    private static HeapModel.Item getField (HeapModel.Item itm, String fld) {
        HeapModel.Item result = null;
        for (Enumeration en = itm.outgoing(); en.hasMoreElements(); ) {
            Object o = en.nextElement();
            if (o instanceof HeapModel.Ref) {
//                o = ((HeapModel.Ref)o).getObject();
                HeapModel.Ref ref = (HeapModel.Ref)o;
                if (ref.getObject() instanceof HeapModel.Item
                && fld.equals(ref.getName())) {
                    result = (HeapModel.Item)ref.getObject();
                    break;
                }
            }
        }
        return result;
    }
    
    private static HeapModel.Item getObjectAt(HeapModel model, String staticRefName) {
        for (Iterator it = model.getAllItems(); it.hasNext(); ) {
            HeapModel.Item itm = (HeapModel.Item)it.next();
            for (Enumeration en = itm.incomming(); en.hasMoreElements(); ) {
                Object ref = ((HeapModel.Ref)en.nextElement()).getObject();
                if (staticRefName.equals(ref)) return itm;
            }
        }
        return null;
    }

    private static Iterator getObjectsOfType(HeapModel model, String type) {
        ArrayList all = new ArrayList();
        for (Iterator it = model.getAllItems(); it.hasNext(); ) {
            HeapModel.Item itm = (HeapModel.Item)it.next();
            if (itm.getType().equals(type)) all.add(itm);
        }
        return all.iterator();
    }

    
    /** Computes the size and distribution */
    private static void computeObjectSize(String file, int id) throws Exception {
        HeapModel model = parseModel(file, false);
        
        Set allObjects = new HashSet();
        HeapModel.Item itm = model.getItem(id);
        allObjects.addAll(reachableFrom(itm));
        
        printDistribution(allObjects);       
    }

        /** Computes the size and distribution */
    private static void computeObjectSize(HeapModel model, HeapModel.Item itm) throws Exception {
        Set allObjects = new HashSet();
        allObjects.addAll(reachableFrom(itm));
        printDistribution(allObjects);       
    }

    
    /** Computes the size and distribution of all java.util.Properties instances */
    private static void computePropertiesSize(String file) throws Exception {
        HeapModel model = new HeapModel(new InputSource(file));
        System.err.println("parsed");
        
        // find all interesting objects and their subgraphs
        Set allObjects = new HashSet();

        for (Iterator it=model.getAllItems(); it.hasNext(); ) {
            HeapModel.Item item = (HeapModel.Item)it.next();
            if ("java.util.Properties".equals(item.getType())) { // is interresting
                allObjects.addAll(reachableFrom(item));
            }
        }
        
        printDistribution(allObjects);
    }

    /** Computes the size and distribution of all java.util.Properties instances */
    private static Set limitedReachableFrom(HeapModel.Item from, Set accept) {
        return limitedReachableFrom(from, accept, true);
    }


    /** Computes the size and distribution of instances reachable from given object.
     *  Instances either have to be of a type contained in filter set or 
     *  have not to be of these types.
     */
    private static Set limitedReachableFrom(HeapModel.Item from, Set filter, boolean accept) {
        Set found = new HashSet();
        found.add(from);
        LinkedList queue = new LinkedList(found);
        
        while (!queue.isEmpty()) {
            HeapModel.Item act = (HeapModel.Item)queue.remove(0);
            Enumeration en = act.outgoing();
            while(en.hasMoreElements()) {
                HeapModel.Item itm = (HeapModel.Item)((HeapModel.Ref)en.nextElement()).getObject();
                if (filter.contains(itm.getType()) == accept) {
                    if (found.add(itm)) queue.add(itm);
                }
            }
        }
        
        return found;
    }


    /** Computes the size of all Strings referenced from HashMap$Entries */
    private static void computeStringKeys(String file) throws Exception {
        HeapModel model = new HeapModel(new InputSource(file));
        System.err.println("parsed");
        
        // find all interesting objects and their subgraphs
        Set allObjects = new HashSet();

        for (Iterator it=model.getAllItems(); it.hasNext(); ) {
            HeapModel.Item item = (HeapModel.Item)it.next();
            if ("java.lang.String".equals(item.getType())) { // is interresting
                for (Enumeration en = item.incomming(); en.hasMoreElements(); ) {
                    Object obj = ((HeapModel.Ref)en.nextElement()).getObject();
                    if (! (obj instanceof HeapModel.Item)) continue;
                    HeapModel.Item fr = (HeapModel.Item)obj;
                    if ("java.util.HashMap$Entry".equals(fr.getType())) {
                        allObjects.addAll(reachableFrom(item)); // the string and the array
                        allObjects.add(fr); // count the HM$E as well
                        break;
                    }
                }
            }
        }
        printDistribution(allObjects);
    }
    
    private static void printDistribution(Set of) {
//        of= new TreeSet(of);
        // compute the stats
        final Map typeCount = new HashMap(); // String->Integer
        Map typeSize = new HashMap(); // String->Integer
        int totalCount = 0;
        int totalSize = 0;
        for (Iterator it = of.iterator(); it.hasNext(); ) {
            HeapModel.Item item = (HeapModel.Item)it.next();
            add (typeCount, item.getType(), 1);
            add (typeSize, item.getType(), item.getSize());
            totalCount++;
            totalSize += item.getSize();
        }

        Map sortedTypeCount = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                int res = ((Comparable)typeCount.get(o1)).compareTo(typeCount.get(o2));
                return res != 0? res: ((Comparable)o1).compareTo(o2);
            }
        });
        
        sortedTypeCount.putAll(typeCount);
 
        
        // print the stats
        for (Iterator it = sortedTypeCount.keySet().iterator(); it.hasNext(); ) {
            String type = (String)it.next();
            int count = ((Integer)typeCount.get(type)).intValue();
            int size = ((Integer)typeSize.get(type)).intValue();
            System.out.println(type + ": " + count + "/" + size + "B");
        }
        System.out.println("Total: " + totalCount + "/" + totalSize + "B");        
    }

    
    /** Computes the transitive size of each instance of given type */
    private static void computeInstanceSizes(String file, String type) throws Exception {
        HeapModel model = new HeapModel(new InputSource(file));
        System.err.println("parsed");
        
        // find all interesting objects and their subgraphs
        for (Iterator it=model.getAllItems(); it.hasNext(); ) {
            HeapModel.Item item = (HeapModel.Item)it.next();
            if (type.equals(item.getType())) { // is interresting
                Set allObjects = new HashSet();
                allObjects.addAll(reachableFrom(item));

                int size = 0;
                
                for (Iterator objs = allObjects.iterator(); objs.hasNext(); ) {
                    HeapModel.Item ii = (HeapModel.Item)objs.next();
                    size += ii.getSize();
                }
                System.out.println("0x" + Integer.toHexString(item.id) + ": " + size/1024 + "KB");
            }
        }
    }
    
    private static void add(Map m, Object key, int count) {
        Integer i = (Integer)m.get(key);
        if (i == null) {
            i = new Integer(count);
        } else {
            i = new Integer(i.intValue() + count);
        }
        m.put(key, i); 
    }
    
    /** returns a set of all items reachable from given item */
    private static Set reachableFrom(HeapModel.Item item) {
        Set found = new HashSet();
        found.add(item);
        LinkedList queue = new LinkedList(found);
        
        while (!queue.isEmpty()) {
            HeapModel.Item act = (HeapModel.Item)queue.remove(0);
            Enumeration en = act.outgoing();
            while(en.hasMoreElements()) {
                HeapModel.Item itm = (HeapModel.Item)((HeapModel.Ref)en.nextElement()).getObject();
                // add to the queue iff new
                if (found.add(itm)) queue.add(itm);
            }
        }
        
        return found;
    }
    
    /** returns a set of all items keeping given item */
    private static Set reachableBy(HeapModel.Item item) {
        Set processed = new HashSet();
        Set results = new HashSet();
        processed.add(item);
        LinkedList queue = new LinkedList(processed);
        
        while (!queue.isEmpty()) {
            HeapModel.Item act = (HeapModel.Item)queue.remove(0);
            processed.add (act);
            Enumeration en = act.incomming();
            while(en.hasMoreElements()) {
                HeapModel.Ref ref = (HeapModel.Ref)en.nextElement();
                if (ref.getObject() instanceof HeapModel.Item) {
                    HeapModel.Item itm = (HeapModel.Item)ref.getObject();
                    String type = itm.getType();
                    if ("java.util.HashSet".equals(type)
                    || "java.util.Hashtable".equals(type)
                    || "java.util.Hashtable$Entry".equals(type)
                    || "[Ljava.util.Hashtable$Entry;".equals(type)
                    || "java.util.HashMap".equals(type)
                    || "java.util.HashMap$KeySet".equals(type)
                    || "java.util.HashMap$Entry".equals(type)
                    || "[Ljava.util.HashMap$Entry;".equals(type)
                    || "java.util.TreeMap$Entry".equals(type)
                    || "java.util.TreeMap".equals(type)
                    || "java.lang.String".equals(type)
                    || "java.util.Collections@SynchronizedMap".equals(type)) {
                        if (!queue.contains(itm) && !processed.contains(itm)) {
                            queue.add (itm);
                        }
                    }
                    else {
                        results.add(itm);
                    }
                }
                else {
                    // should be a string
                    results.add(ref.getObject());
                }
            }
        }
        
        return results;
    }
    
    private static String valueOf(HeapModel.Item string) {
        assert "java.lang.String".equals(string.getType()) : "Not a String item";
        
        for (Enumeration en = string.outgoing(); en.hasMoreElements(); ) {
            HeapModel.Item itm = (HeapModel.Item)((HeapModel.Ref)en.nextElement()).getObject();
            if ("[C".equals(itm.getType())) {
                return itm.value;
            }
        }
        
        return null;
    }
    
    private static HeapModel.Item findObjectReferringString(HeapModel model, String type, String str) {
        for (Iterator it = model.items.iterator(); it.hasNext(); ) {
            HeapModel.Item hmi = (HeapModel.Item)it.next();
            if (type.equals(hmi.getType())) { // is of given type
                for (Enumeration en = hmi.outgoing(); en.hasMoreElements(); ) {
                    HeapModel.Item sub = (HeapModel.Item)((HeapModel.Ref)en.nextElement()).getObject();
                    if ("java.lang.String".equals(sub.getType())) { // references String
                        if (str.equals(valueOf(sub))) return hmi; // Have requested value
                    }
                }
                
            }
        }
        return null;
    }
    
    
    private static void findRoots(HeapModel model, int id, boolean weak) {
        findRoots(model, id, weak, new int[0]);
    }
        
    /** BFS scan of incomming refs*/
    private static void findRoots(HeapModel model, int id, boolean weak, int[] _skip) {
int cnt = 0;
        Set skip = new HashSet();
        if (_skip != null) for (int j=0; j<_skip.length; j++) {
            skip.add(model.getItem(_skip[j]));
        }
        
        Set visited = new HashSet();
        visited.add(new PathElement(model.getItem(id), null));
        LinkedList queue = new LinkedList(visited);
        while (!queue.isEmpty()) {
            PathElement act = (PathElement)queue.remove(0);
            Enumeration en = act.getItem().incomming();
            while(en.hasMoreElements()) {
                Object o = ((HeapModel.Ref)en.nextElement()).getObject();
                if (skip.contains(o)) continue;
                if (o instanceof String) {
                    System.err.println("------------------------------");
                    System.err.println(o + "->\n" + act);
                    cnt++;
                    /*if (cnt > 20)*/ return;
                } else {
                    HeapModel.Item ref = (HeapModel.Item)o;
                    if ("java.lang.ref.WeakReference".equals(ref.getType()) ||
                        "javax.swing.AbstractActionPropertyChangeListener$OwnedWeakReference".equals(ref.getType()) ||
                        "org.openide.util.WeakListener$ListenerReference".equals(ref.getType()) ||
                        "org.openide.util.WeakListenerImpl$ListenerReference".equals(ref.getType()) ||
                        "org.openide.util.WeakSet$Entry".equals(ref.getType()) ||
                        "java.lang.ref.SoftReference".equals(ref.getType()) ||
                        "java.util.WeakHashMap$Entry".equals(ref.getType()) ||
//                        "org.netbeans.api.nodes2looks.LookNode$FirerImpl".equals(ref.getType()) ||
                        "org.openide.loaders.DataObjectPool$ItemReference".equals(ref.getType()) ||
                        "org.openide.util.IconManager$ActiveRef".equals(ref.getType())) {
                        // skip
                    } else {
                        // add to the queue if not new
                        if (visited.add(ref)) queue.add(new PathElement(ref, act));
                    }
                }
            }
        }
    }
  
    private static class PathElement {
        private HeapModel.Item item;
        private PathElement next; 
        public PathElement(HeapModel.Item item, PathElement next) {
            this.item = item;
            this.next = next;
        }
        public HeapModel.Item getItem() {
            return item;
        }
        public String toString() {
            if (next == null) {
                return item.toString();
            } else {
                return item.toString() + "->\n" + next.toString();
            }
        }
    }


    private static HeapModel parseModel(String name, boolean fast) throws Exception {
        System.gc();
        long time = System.currentTimeMillis();
        HeapModel model;
//        if (fast) {
            model = new HeapModel(new InputSource(name), new com.dautelle.xml.sax.XMLReaderImpl());
//        } else {
//            model = new HeapModel(new InputSource(name), new org.apache.crimson.parser.XMLReaderImpl());
//        }
        time = System.currentTimeMillis() - time;
        System.err.println("parsed in " + time + "ms");
        return model;
    }
    
    public static class HeapModel {
        private Map/*<Object,Integer>*/ idMap = new HashMap();
        private int idCounter = 0;

        public int getIdFromString(String s, boolean create) {
            Object key = s;
            if (s.indexOf('.') == -1) { // Integer only
                key = new Integer(Integer.parseInt(s, 16));
            }

            Integer val = (Integer)idMap.get(key);

            if (val == null) {
                if (!create) throw new IllegalArgumentException("Bad ID: " + s);
                val = new Integer(idCounter++);
                idMap.put(key, val);
            }
            return val.intValue();
        }
        
        private List items = new ArrayList();
        
        public HeapModel(InputSource is) throws Exception {
            Handler h = new Handler();
            SAXParserFactory fact = SAXParserFactory.newInstance();
            SAXParser parser = fact.newSAXParser();
            parser.getXMLReader().setContentHandler(h);
            parser.getXMLReader().parse(is);
        }

        public HeapModel(InputSource is, XMLReader reader) throws Exception {
            Handler h = new Handler();
            reader.setContentHandler(h);
            reader.parse(is);
        }
        
        public Item getItem(int id) {
            if (id >= items.size()) throw new IllegalArgumentException("Bad ID");
            return (Item)items.get(id);
        }
        
        public Iterator getAllItems() {
            return items.iterator();
        }
        
        Item createItem(int id, String type, int size, String val) {
            if (items.size() != id) throw new IllegalArgumentException("Existing ID");
            Item item = new Item(id, type, size, val);
            items.add(item);
            return item;
        }
        
        void addReference(int from, int to, String name) {
            Item _f = getItem(from);
            Item _t = getItem(to);
            _f.addOutgoing(_t, name);
            _t.addIncomming(_f, name);
        }
        void addReference(String stat, int to, String name) {
            Item _t = getItem(to);
            _t.addIncomming(stat, name);
        }
        
        public static class Item {
            // a list of Items, Strings and one null
            private Object[] refs = new Object[] {null};
            private String[] refNames = new String[] {null};
            private int id;
            private int size;
            private String type;
            private String value;
            
            Item(int id, String type, int size, String value) {
                this.id = id;
                this.type = type.intern();
                this.size = size;
                this.value = value;
            }
            
            public Enumeration incomming() {
                return new RefEnum(true, refs, refNames);
            }
            
            public Enumeration outgoing() {
                return new RefEnum(false, refs, refNames);
            }
            
            public String getType() {
                return type;
            }
            
            public int getSize() {
                return size;
            }
            
            public String toString() {
                return type + "@" + Integer.toHexString(id);
            }
            
            void addIncomming(Object incomming, String name) {
                Object[] nr = new Object[refs.length+1];
                nr[0] = incomming;
                System.arraycopy(refs, 0, nr, 1, refs.length);
                refs = nr;
                String[] nn = new String[refNames.length+1];
                nn[0] = name;
                System.arraycopy(refNames, 0, nn, 1, refNames.length);
                refNames = nn;
            }

            void addOutgoing(Object outgoing, String name) {
                Object[] nr = new Object[refs.length+1];
                nr[refs.length] = outgoing;
                System.arraycopy(refs, 0, nr, 0, refs.length);
                refs = nr;
                String[] nn = new String[refNames.length+1];
                nn[refNames.length] = name;
                System.arraycopy(refNames, 0, nn, 0, refNames.length);
                refNames = nn;
            }
            
            private static class RefEnum implements Enumeration {
                int ptr;
                Object[] items;
                String[] names;
                
                RefEnum(boolean first, Object[] data, String[] refnames) {
                    if (!first) {
                        while (data[ptr++] != null);
                    }
                    items = data;
                    names = refnames;
                }
                
                public boolean hasMoreElements() {
                    return ptr < items.length && items[ptr] != null;
                }
                
                public Object nextElement() {
                    if (hasMoreElements()) {
                        Object o = new HeapModel.Ref(names[ptr], items[ptr]);
                        ptr++;
                        return o;
                    }
                    throw new NoSuchElementException();
                }
            }
        }
        
        /** Named reference to some object. */
        public static class Ref {
            private String name;
            private Object itm;
            public Ref (String name, Object itm) {
                this.name = name;
                this.itm = itm;
            }
            
            public String getName () { return name; }
            
            public Object getObject () { return itm; }
        }
        
// <insane>
//   <object id='0' type='java.lang.String' size='40'/>
//   <ref name='org.openide.util.actions.SystemAction.PROP_ENABLED' to='0'/>
        private class Handler extends DefaultHandler {
            private int depth = 0;
            
            public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
//                System.err.println("startElement(" + qName + "), id=" + atts.getValue("id") +
//                ", depth=" + depth);
                if (depth == 0) {
                    if (! "insane".equals(qName)) throw new SAXException("format");
                } else if (depth != 1) {
                    throw new SAXException("format");
                } else {
                    if ("object".equals(qName)) {
                        String id = atts.getValue("id");
                        String type = atts.getValue("type");
                        String size = atts.getValue("size");
                        String val = atts.getValue("value");
                        createItem(getIdFromString(id, true), type, Integer.parseInt(size), val);
                    } else if ("ref".equals(qName)) {
                        String from = atts.getValue("from");
                        String name = atts.getValue("name");
                        String to = atts.getValue("to");
                        if (from != null) {
                            addReference(getIdFromString(from, false), getIdFromString(to, false), name);
                        } else {
                            addReference(name, getIdFromString(to, false), name);
                        }
                    } else {
                        throw new SAXException("format");
                    }
                }
                depth++;
            }
            

            public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
                depth--;
//                System.err.println("endElement");
            }
            
            
        }
    }
    
}
