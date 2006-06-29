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
