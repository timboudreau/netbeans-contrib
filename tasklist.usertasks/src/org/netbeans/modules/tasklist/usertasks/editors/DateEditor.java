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

// HACK - read the comment at the beginning of the Task class

// Also see core/src/org/netbeans/beaninfo/editors and
// openide/src/org/openide/explorer/propertysheet/editors

package org.netbeans.modules.tasklist.usertasks.editors;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;
import org.netbeans.modules.tasklist.usertasks.DateSelectionPanel;
import org.openide.ErrorManager;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

// bugfix# 9219 for attachEnv() method
import org.openide.nodes.Node;
import org.openide.util.NbBundle;


/** 
 * A property editor for the Date class. 
 *
 * @author Tor Norbye 
 * @author Trond Norbye
 */
public class DateEditor extends PropertyEditorSupport 
implements ExPropertyEditor {
    private static SimpleDateFormat format = new SimpleDateFormat();
    
    // bugfix# 9219 added editable field and isEditable() "getter" to be used 
    // in StringCustomEditor    
    private boolean editable = true;   

    /**
     * gets information if the text in editor should be editable or not
     *
     * @return true = editable
     */
    public boolean isEditable(){
        return editable;
    }
                
    public void setAsText(String s) throws java.lang.IllegalArgumentException {
        if (s.trim().length() == 0) {
            setValue(null);
            return;
        }
        try {
            setValue(format.parse(s));
        } catch (ParseException e) {
            String msg = NbBundle.getMessage(DateEditor.class,
                "IllegalDateValue", new Object[] {s}); //NOI18N
            RuntimeException iae = new IllegalArgumentException(msg); 
            ErrorManager.getDefault().annotate(iae, ErrorManager.USER, msg,
                msg, e, new java.util.Date());
            throw iae;
        }
    }

    public String getAsText() {
        Object val = getValue();
        if (val instanceof Date) {
            return format.format((Date) val);
        } else if (val instanceof Long) {
            return format.format(new Date(((Long) val).longValue()));
        } else {
            return ""; // NOI18N
        }
    }

    public boolean supportsCustomEditor () {
        return true;
    }

    public Component getCustomEditor() {
        Date d;
        if (getValue() instanceof Date) {
            d = (Date) getValue();
        } else if (getValue() instanceof Long) {
            d = new Date(((Long) getValue()).longValue());
        } else {
            d = new Date();
            setValue(d);
        }
	return new DateSelectionPanel(d);
    }

    public void attachEnv(PropertyEnv env) {        
        FeatureDescriptor desc = env.getFeatureDescriptor();
        if (desc instanceof Node.Property){
            Node.Property prop = (Node.Property)desc;
            editable = prop.canWrite();
        }
    }
}
