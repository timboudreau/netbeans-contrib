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
public class HelpContentEditor extends PropertyEditorSupport{
    
    private static final java.util.ResourceBundle bundle = NbBundle.getBundle(SearchTypeEditor.class);
    
    private final static String CONTENT_EDITOR = bundle.getString("CONTENT_EDITOR");
    private final static String CONTENT_HELP = bundle.getString("CONTENT_HELP");
    
    private static final String[] types = {CONTENT_EDITOR,CONTENT_HELP};
    
    public String[] getTags(){
        return types;
    }
    
    /** @return text for the current value */
    public String getAsText () {
        Integer type = (Integer) getValue();
        int index = type.intValue();
        if (index == AssistantSettings.CONTENT_EDITOR)
            return CONTENT_EDITOR;
        else
            return CONTENT_HELP;
    }
     
    /** @param text A text for the current value. */
    public void setAsText (String text) {
        if (text.equals(CONTENT_EDITOR)) {
            setValue(new Integer(AssistantSettings.CONTENT_EDITOR));
            return;
        }
        if (text.equals(CONTENT_HELP)) {
            setValue(new Integer(AssistantSettings.CONTENT_HELP));
            return;
        }
        throw new IllegalArgumentException ();
    }

    public void setValue(Object value) {
        super.setValue(value);
    }
  
}
