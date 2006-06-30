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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
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
