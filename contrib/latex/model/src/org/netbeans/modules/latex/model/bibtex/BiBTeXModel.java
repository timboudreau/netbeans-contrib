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

import java.util.Iterator;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/** This is going to be the central class of the BiBTeX API.
 *  <HR>**CURRENTLY VERY UNSTABLE**</HR>
 * @author Jan Lahoda
 */
public abstract class BiBTeXModel {
    
    /** Creates a new instance of BiBTeXModel */
    protected BiBTeXModel() {
    }
    
    public abstract void addBiBTexModelChangeListener(BiBTeXModelChangeListener l);
    
    public abstract void removeBiBTexModelChangeListener(BiBTeXModelChangeListener l);
    
    public static synchronized BiBTeXModel getModel(Object file) {
//        Lookup.Result r = Lookup.getDefault().lookup(new Lookup.Template(Object.class));
//        
//        for (Iterator i = r.allInstances().iterator(); i.hasNext(); ) {
//            System.err.println(i.next());
//        }
        
        BiBTeXModelFactory factory = (BiBTeXModelFactory) Lookup.getDefault().lookup(BiBTeXModelFactory.class);
        
        return factory.get(file);
    }
    
    public abstract List/*<Entry>*/ getEntries();
    
    public abstract void addEntry(Entry e);
    
    public abstract void removeEntry(Entry e);
    
    public static BiBTeXModelChangeListener createWeakListeners(BiBTeXModelChangeListener l, BiBTeXModel source) {
        return (BiBTeXModelChangeListener) WeakListeners.create(BiBTeXModelChangeListener.class, l, source);
    }
}
