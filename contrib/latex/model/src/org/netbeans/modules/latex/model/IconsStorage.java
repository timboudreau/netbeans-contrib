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
package org.netbeans.modules.latex.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import org.openide.ErrorManager;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public abstract class IconsStorage {
    
    public static final String DEFAULT_CATHEGORY = "<default>";
    
    /** Creates a new instance of IconsStorage */
    protected IconsStorage() {
    }
    
    public static IconsStorage getDefault() {
        return (IconsStorage) Lookup.getDefault().lookup(IconsStorage.class);
    }
    
    public abstract List/*<String>*/ getAllIconNames();
    
    public abstract List/*<String>*/ getIconNamesForCathegory(String catName);
    
    public abstract Collection/*<String>*/ getCathegories();
    
    public abstract String getCathegoryDisplayName(String catName);
    
    public abstract ChangeableIcon getIcon(String command);
    
    public abstract boolean getIconsInstalled();
    
    public abstract ChangeableIcon getIconForExpression(String expression);
    
    public static interface ChangeableIcon extends Icon {
        
        public void addChangeListener(ChangeListener l);
        
        public void removeChangeListener(ChangeListener l);
        
    }

}
