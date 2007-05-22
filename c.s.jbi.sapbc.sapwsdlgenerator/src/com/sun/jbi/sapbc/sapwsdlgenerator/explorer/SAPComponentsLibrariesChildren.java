package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import java.io.File;
import java.util.Comparator;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * This class manages the list of nodes placed beneath the Library sub node
 * of the SAP Components node.
 */
public class SAPComponentsLibrariesChildren
    extends Children.SortedMap
    implements SAPComponentsChangeListener {
    
    public SAPComponentsLibrariesChildren() {
        SAPComponentsNotifier.addChangeListener(this);
    }

    public boolean add(Node[] nodes) {
        for (int i = 0; i < nodes.length; ++i) {
            SAPComponentsLibraryNode node = (SAPComponentsLibraryNode) nodes[i];
            File subject = (File) node.getDataObject();
            put(subject, node);
        }
        refresh();
        return true;
    }

    public boolean remove(Node[] nodes) {
        int removed = 0;
        
        for (int i = 0; i < nodes.length; ++i) {
            SAPComponentsLibraryNode node = (SAPComponentsLibraryNode) nodes[i];
            File subject = (File) node.getDataObject();
            if (super.nodes.containsKey(subject)) {
                remove(subject);
                removed++;
            }
        }
        if (removed > 0) {
            refresh();
        }
        return (removed == nodes.length);
    }

    public void added(SAPComponentsChangeEvent evt) {
        File subject = (File) evt.getSubject();
        add(new Node[] { new SAPComponentsLibraryNode(subject) } );
    }

    public void removed(SAPComponentsChangeEvent evt) {
        File subject = (File) evt.getSubject();
        if (super.nodes.containsKey(subject)) {
            remove(subject);
            refresh();
        }
    }

    public void changed(SAPComponentsChangeEvent evt) {
        File[] args = (File[]) evt.getSubject();
        File oldValue = args[0];
        File newValue = args[1];
        if (super.nodes.containsKey(oldValue)) {
            remove(oldValue);
            add(new Node[] { new SAPComponentsLibraryNode(newValue) } );
            refreshKey(newValue);
        }
    }
    
    protected void addNotify() {
        super.addNotify();
        
        // Initializing a custom comparator at construction time
        // causes an IllegalStateException in connection with
        // NetBeans issue/bug 10779; see
        // http://www.netbeans.org/issues/show_bug.cgi?id=10778
        setComparator(new Comparator<SAPComponentsLibraryNode>() {
            public int compare(
                SAPComponentsLibraryNode o1,
                SAPComponentsLibraryNode o2
                ) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                } else {
                    File file1 = (File) o1.getDataObject();
                    File file2 = (File) o2.getDataObject();
                    String path1 = file1.getName().toUpperCase();
                    String path2 = file2.getName().toUpperCase();
                    return path1.compareTo(path2);
                }
            }
        });
        
        // TODO: Generate nodes for persisted objects under "Libraries"
        // ...how to do it? What is he storage-retrieval mechanism?
    }

    protected void finalize() throws Throwable {
        SAPComponentsNotifier.removeChangeListener(this);
    }
}
