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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
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
    
    public abstract List<String> getAllIconNames();
    
    public abstract List<String> getIconNamesForCathegory(String catName);
    
    public abstract Collection<String> getCathegories();
    
    public abstract String getCathegoryDisplayName(String catName);

    /**Prefered size:
     */
    public abstract ChangeableIcon getIcon(String command);
    
    /**Given size:
     */
    public abstract ChangeableIcon getIcon(String command, int sizeX, int sizeY);

    public abstract boolean getIconsInstalled();
    
    public static interface ChangeableIcon extends Icon {
        
        public void addChangeListener(ChangeListener l);
        
        public void removeChangeListener(ChangeListener l);
        
    }

}
