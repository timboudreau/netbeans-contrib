/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.helpbuilder.plaf.basic;

/**
 * Cell Renderer.
 *
 * @author Richard Gregor
 * @version	1.1
 */
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.net.URL;
import java.util.Locale;
import org.netbeans.modules.helpbuilder.tree.*;

public class BasicHelpCellRenderer extends DefaultTreeCellRenderer {
    static private Color color = new JLabel().getForeground();
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean sel,
    boolean expanded,
    boolean leaf, int row,
    boolean hasFocus) {
        
        HelpTreeItem item;
        
        Object o = ((DefaultMutableTreeNode)value).getUserObject();
        String stringValue = "";
                
        item = (HelpTreeItem) o;
        if (item != null) {
            stringValue = item.getName();            
        }
        
       
        // Set the locale of this if there is a lang value
        if (item != null) {
            Locale locale = item.getLocale();
            if (locale != null) {
                setLocale(locale);
            }
        }
        
        if((item != null) && (item instanceof TocTreeItem)){
            TocTreeItem tocItem = (TocTreeItem) item;
            if(tocItem.isHomeID())
                stringValue += " (home page)";            
        }        
        
        setText(stringValue);
        
        if (sel)
            setForeground(getTextSelectionColor());
        else
            setForeground(getTextNonSelectionColor());

        
        if(leaf)
            setIcon(getDefaultLeafIcon());
        else if(expanded)
            setIcon(getDefaultOpenIcon());
        else
            setIcon(getDefaultClosedIcon());        
       
        return this;
    }    
}
