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
package org.netbeans.modules.latex.bibtex;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModelFactory;

/**
 *
 * @author Jan Lahoda
 */
public class BiBTeXModelFactoryImpl extends BiBTeXModelFactory {
    
    /** Creates a new instance of BiBTeXModelFactoryImpl */
    public BiBTeXModelFactoryImpl() {
        System.err.println("BiBTeXModelFactoryImpl constructor");
    }
    
    public BiBTeXModel get(Object file) {
        if (file2Model == null) {
            file2Model = new HashMap();
        }
        
        BiBTeXModel result = (BiBTeXModel) file2Model.get(file);
        
        if (result == null) {
            file2Model.put(file, result = new BiBTeXModelImpl(file));
        }
        
        return result;
    }
    
    private static Map file2Model;
    
}
