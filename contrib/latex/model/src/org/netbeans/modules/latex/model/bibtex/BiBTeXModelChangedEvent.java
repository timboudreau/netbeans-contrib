/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
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
