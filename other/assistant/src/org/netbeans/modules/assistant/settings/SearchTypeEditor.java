/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
 

package org.netbeans.modules.assistant.settings;

import java.beans.PropertyEditorSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author  Richard Gregor
 *
 * Created on November 21, 2002, 11:39 AM
 */
public class SearchTypeEditor extends PropertyEditorSupport{
    
    private static final java.util.ResourceBundle bundle = NbBundle.getBundle(SearchTypeEditor.class);
    
    private final static String SEARCH_KEYWORDS = bundle.getString("SEARCH_KEYWORDS");
    private final static String SEARCH_FULL_TEXT = bundle.getString("SEARCH_FULL_TEXT");
    
    private static final String[] types = {SEARCH_KEYWORDS,SEARCH_FULL_TEXT};
    
    public String[] getTags(){
        return types;
    }
    
    /** @return text for the current value */
    public String getAsText () {
        Integer type = (Integer) getValue();
        int index = type.intValue();
        if (index == AssistantSettings.SEARCH_FULL_TEXT)
            return SEARCH_FULL_TEXT;
        else
            return SEARCH_KEYWORDS;
    }
     
    /** @param text A text for the current value. */
    public void setAsText (String text) {
        if (text.equals(SEARCH_FULL_TEXT)) {
            setValue(new Integer(AssistantSettings.SEARCH_FULL_TEXT));
            return;
        }
        if (text.equals(SEARCH_KEYWORDS)) {
            setValue(new Integer(AssistantSettings.SEARCH_KEYWORDS));
            return;
        }
        throw new IllegalArgumentException ();
    }

    public void setValue(Object value) {
        super.setValue(value);
    }
  
}
