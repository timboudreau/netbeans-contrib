/*
 * BiBTeXModelChangedEvent.java
 *
 * Created on May 25, 2004, 9:37 AM
 */

package org.netbeans.modules.latex.model.bibtex;

import java.util.Collection;
import java.util.EventObject;

/**
 *
 * @author Jan Lahoda
 */
public class BiBTeXModelChangedEvent extends EventObject {
    
    public static final int ENTRIES_ADDED = 1;
    public static final int ENTRIES_REMOVED = 2;
    
    private int type;
    private Collection entries;
    
    /** Creates a new instance of BiBTeXModelChangedEvent */
    public BiBTeXModelChangedEvent(BiBTeXModel source, int type, Collection entries) {
        super(source);
        this.type = type;
        this.entries = entries;
    }
    
    public int getType() {
        return type;
    }
    
    public Collection getEntries() {
        return entries;
    }
    
}
