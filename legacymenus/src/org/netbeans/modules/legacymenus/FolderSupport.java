/*
 * FolderSupport.java
 *
 * Created on May 24, 2004, 3:23 PM
 */

package org.netbeans.modules.legacymenus;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import java.util.*;
import org.openide.util.TopologicalSortException;
import org.openide.util.Utilities;

/**
 *
 * @author  tim
 */
public class FolderSupport {
    private static final char SEP = '/';
    
    /** Creates a new instance of FolderSupport */
    private FolderSupport() {
    }
    
    /** Read the list of intended partial orders from disk.
     * Each element is a string of the form "a<b" for a, b filenames
     * with extension, where a should come before b.
     */
    private static Set readPartials (FileObject folder) { // Set<String>
        Enumeration e = folder.getAttributes ();
        Set s = new HashSet ();
        while (e.hasMoreElements ()) {
            String name = (String) e.nextElement ();
            if (name.indexOf (SEP) != -1) {
                Object value = folder.getAttribute (name);
                if ((value instanceof Boolean) && ((Boolean) value).booleanValue ())
                    s.add (name);
            }
        }
        return s;
    }    
    
    /**
     * Get ordering constraints for this folder.
     * Returns a map from data objects to lists of data objects they should precede.
     * @param objects a collection of data objects known to be in the folder
     * @return a constraint map, or null if there are no constraints
     */
    private static Map getOrderingConstraints(Collection objects, FileObject f) {
        final Set partials = readPartials (f);
        if (partials.isEmpty ()) {
            return null;
        } else {
            Map objectsByName = new HashMap();
            Iterator it = objects.iterator();
            while (it.hasNext()) {
                FileObject fo = (FileObject) it.next();
                objectsByName.put(fo.getNameExt(), fo);
            }
            Map m = new HashMap();
            it = partials.iterator();
            while (it.hasNext()) {
                String constraint = (String)it.next();
                int idx = constraint.indexOf(SEP);
                String a = constraint.substring(0, idx);
                String b = constraint.substring(idx + 1);
//                if (ignorePartials && (order.containsKey(a) || order.containsKey(b))) {
//                    continue;
//                }
                FileObject ad = (FileObject)objectsByName.get(a);
                if (ad == null) {
                    continue;
                }
                FileObject bd = (FileObject)objectsByName.get(b);
                if (bd == null) {
                    continue;
                }
                List l = (List)m.get(ad);
                if (l == null) {
                    m.put(ad, l = new LinkedList());
                }
                l.add(bd);
            }
            return m;
        }
    }  
    
    public static FileObject[] getSortedChildren (FileObject fld) {
        List result = sortChildren(fld);
        return (FileObject[]) result.toArray (new FileObject[0]);
    }
    
    public static List sortChildren (FileObject fo) {
        FileObject[] children = fo.getChildren();
        List l = Arrays.asList(children);
        
        Map constraints = getOrderingConstraints(l, fo);
        if (constraints == null) {
            return null;
        } else {
            try {
                return Utilities.topologicalSort(l, constraints);
            } catch (TopologicalSortException ex) {
                List corrected = ex.partialSort();
                ex.printStackTrace();
                
                return corrected;
            }
        }
    }
    
}
