/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.encoder.custom.aip;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;

/**
 * A tree table view for showing the delimiter list.
 *
 * @author Jun Xu
 */
public class DelimiterTreeTableView extends TreeTableView {

    private static final ResourceBundle _bundle =
            ResourceBundle.getBundle("org/netbeans/modules/encoder/custom/aip/Bundle");
    private static final int COL_TYPE = 0;
    private static final int COL_BYTES = 1;
    private static final int COL_PRECEDENCE = 2;
    private static final int COL_OPTIONAL_MODE = 3;
    private static final int COL_TERMINATOR_MODE = 4;
    private static final int COL_OFFSET = 5;
    private static final int COL_LENGTH = 6;
    private static final int COL_SKIP_LEADING = 7;
    private static final int COL_COLLAPSE = 8;
    
    /** Creates a new instance of DelimiterTreeTableView */
    public DelimiterTreeTableView() {
        super();
        
        PropertySupport.ReadWrite prop1 = new SimplePropertySupport("kind", String.class, _bundle.getString("delim_tree_tab.lbl.type"), _bundle.getString("delim_tree_tab.lbl.type_short")); //NOI18N
        PropertySupport.ReadWrite prop2 = new SimplePropertySupport("bytes", String.class, _bundle.getString("delim_tree_tab.lbl.delim_bytes"), _bundle.getString("delim_tree_tab.lbl.delim_bytes_short")); //NOI18N
        PropertySupport.ReadWrite prop3 = new SimplePropertySupport("precedence", short.class, _bundle.getString("delim_tree_tab.lbl.precedence"), _bundle.getString("delim_tree_tab.lbl.precedence_short")); //NOI18N
        PropertySupport.ReadWrite prop4 = new SimplePropertySupport("optionMode", String.class, _bundle.getString("delim_tree_tab.lbl.opt_mode"), _bundle.getString("delim_tree_tab.lbl.opt_mode_short")); //NOI18N
        PropertySupport.ReadWrite prop5 = new SimplePropertySupport("termMode", String.class, _bundle.getString("delim_tree_tab.lbl.term_mode"), _bundle.getString("delim_tree_tab.lbl.term_mode_short")); //NOI18N
        PropertySupport.ReadWrite prop6 = new SimplePropertySupport("offset", int.class, _bundle.getString("delim_tree_tab.lbl.offset"), _bundle.getString("delim_tree_tab.lbl.offset_short")); //NOI18N
        PropertySupport.ReadWrite prop7 = new SimplePropertySupport("length", short.class, _bundle.getString("delim_tree_tab.lbl.length"), _bundle.getString("delim_tree_tab.lbl.length_short")); //NOI18N
        PropertySupport.ReadWrite prop8 = new SimplePropertySupport("skipLeading", boolean.class, _bundle.getString("delim_tree_tab.lbl.skip_leading"), _bundle.getString("delim_tree_tab.lbl.skip_leading_short")); //NOI18N
        PropertySupport.ReadWrite prop9 = new SimplePropertySupport("collapse", boolean.class, _bundle.getString("delim_tree_tab.lbl.collapse"), _bundle.getString("delim_tree_tab.lbl.collapse_short")); //NOI18N
        setProperties(new Property[]{prop1, prop2, prop3, prop4, prop5, prop6, prop7, prop8, prop9});

        double ratio = getFont().getSize2D() / 12;
        
        setTableColumnPreferredWidth(COL_TYPE, (int) (ratio * 65));
        setTableColumnPreferredWidth(COL_BYTES, (int) (ratio * 60));
        setTableColumnPreferredWidth(COL_PRECEDENCE, (int) (ratio * 80));
        setTableColumnPreferredWidth(COL_OPTIONAL_MODE, (int) (ratio * 60));
        setTableColumnPreferredWidth(COL_TERMINATOR_MODE, (int) (ratio * 65));
        setTableColumnPreferredWidth(COL_OFFSET, (int) (ratio * 54));
        setTableColumnPreferredWidth(COL_LENGTH, (int) (ratio * 54));
        setTableColumnPreferredWidth(COL_SKIP_LEADING, (int) (ratio * 40));
        setTableColumnPreferredWidth(COL_COLLAPSE, (int) (ratio * 54));
    }

    private static class SimplePropertySupport extends PropertySupport.ReadWrite {
        
        private Object mValue;
        
        public SimplePropertySupport(String name, Class clazz, String shortDesc, String displayName) {
            super(name, clazz, displayName, shortDesc);
        }
        
        public Object getValue()
            throws IllegalAccessException, InvocationTargetException {
            
            return mValue;
        }

        public void setValue(Object object)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            
            mValue = object;
        }
    }
}
