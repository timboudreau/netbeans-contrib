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

package org.netbeans.modules.corba.poasupport.nodes;

import java.beans.*;
import java.util.*;
import org.openide.util.NbBundle;

/** 
*
* @author Dusan Balek
*/

public class ComboStringsPropertyEditor extends PropertyEditorSupport {

    private String[] values;
    
    public ComboStringsPropertyEditor(Vector _values) {
        values = new String[_values.size()];
        ListIterator it = _values.listIterator();
        int i = 0;
        while (it.hasNext())
            values[i++] = (String)it.next();
    }

    /** @return possible policy values*/
    public String[] getTags() {
        return values;
    }

    /** @return text for current value */
    public String getAsText () {
        return (String) getValue();
    }

    /** @param text A text for current value. */
    public void setAsText (String text) {
        setValue(text);
    }
}
