/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jndi.gui;

import java.awt.Component;
import java.text.MessageFormat;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import org.netbeans.modules.jndi.JndiRootNode;
/**
 *
 * @author  tz97951
 */
public class AttributeCellRenderer extends DefaultListCellRenderer {
    
    private MessageFormat format;
    
    /** Creates a new instance of AttributeCellRenderer */
    public AttributeCellRenderer() {
    }
    
    public Component getListCellRendererComponent(
        JList list,
	Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {
        String strValue = null;
        if (value instanceof String) {
            strValue = (String) value;
        }
        else if (value instanceof Attribute) {
            Attribute attr = (Attribute) value;
            String attrName = attr.getID ();
            String attrValue = new String ();
            try {
                for (int i=0; i < attr.size (); i++) {
                    if (i != 0)
                        attrValue = attrValue + JndiRootNode.getLocalizedString ("TXT_ValueSeparator");
                    attrValue = attrValue + attr.get (i);
                }
            }catch (NamingException ne) {
                attrValue = JndiRootNode.getLocalizedString ("TXT_Unknown");
            };
            if (this.format == null) {
                this.format = new MessageFormat (JndiRootNode.getLocalizedString ("TXT_Assign"));
            }
            strValue = this.format.format ( new Object[] { attrName, attrValue});
        }
        else {
            strValue = value.toString ();
        }
        return super.getListCellRendererComponent (list, strValue, index, isSelected, cellHasFocus);
    }
    
}
