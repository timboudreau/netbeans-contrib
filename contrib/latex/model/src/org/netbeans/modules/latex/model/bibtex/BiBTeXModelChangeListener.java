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
import java.util.EventListener;

/**
 *
 * @author Jan Lahoda
 */
public interface BiBTeXModelChangeListener extends EventListener {
    
    public void entriesRemoved(BiBTeXModelChangedEvent event);
    
    public void entriesAdded(BiBTeXModelChangedEvent event);
    
}
