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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
