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

package org.netbeans.modules.corba.settings;

import java.beans.*;

import org.openide.util.NbBundle;

/** property editor for viewer property AppletSettings class
*
* @author Karel Gardas
* @version 0.01 April 16, 1999
*/

import org.netbeans.modules.corba.*;

public class ClientBindingPropertyEditor extends PropertyEditorSupport
    implements PropertyChangeListener {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;


    /** array of orbs */
    /*
    private static final String[] choices = {CORBASupport.CLIENT_NS, 
    		   CORBASupport.CLIENT_IOR_FROM_FILE, 
    		   CORBASupport.CLIENT_IOR_FROM_INPUT,
    		   CORBASupport.CLIENT_BINDER};
    */

    private static String[] choices = {""};


    public ClientBindingPropertyEditor () {
        if (DEBUG)
            System.out.println ("ClientBindingPropertyEditor () ...");
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                   (CORBASupportSettings.class, true);
        choices = css.getClientBindingsChoices ();
        css.addPropertyChangeListener (this);
        //css.setClientBinding (choices[0]);
        for (int i=0; i<choices.length; i++)
            if (DEBUG)
                System.out.println ("choice: " + choices[i]);
    }


    /** @return names of the supported orbs*/
    public String[] getTags () {
        return choices;
    }

    public void setTags (String[] s) {
        String[] old = choices;
        choices = s;
        //firePropertyChange ("choices", (Object)old, (Object)choices);
        //CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
        //	 (CORBASupportSettings.class, true);
        //css.fireChangeChoices ();
    }

    public void setChoices (String[] s) {
        setTags (s);
    }

    public void propertyChange (PropertyChangeEvent event) {

        if (event == null || event.getPropertyName () == null)
            return;

        if (DEBUG)
            System.out.println ("propertyChange in CBPE: " + event.getPropertyName ());
        if (event.getPropertyName ().equals ("orb")) {
            CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                       (CORBASupportSettings.class, true);
            setChoices (css.getClientBindingsChoices ());
            css.setClientBinding (getTags ()[0]);
            if (DEBUG) {
                for (int i=0; i<choices.length; i++)
                    System.out.println ("choice[" + i + "] in cb-editor: " + choices[i]);
            }

        }

    }


    /** @return text for the current value */
    public String getAsText () {
        return (String) getValue();
    }

    /** @param text A text for the current value. */
    public void setAsText (String text) {
        setValue(text);
    }
}

/*
 * <<Log>>
 *  14   Gandalf   1.13        3/7/00   Karel Gardas    naming service browser 
 *       bugfix
 *  13   Gandalf   1.12        11/4/99  Karel Gardas    - update from CVS
 *  12   Gandalf   1.11        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  11   Gandalf   1.10        10/1/99  Karel Gardas    updates from CVS
 *  10   Gandalf   1.9         8/3/99   Karel Gardas    
 *  9    Gandalf   1.8         7/10/99  Karel Gardas    
 *  8    Gandalf   1.7         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    fixed for reading 
 *       configuration from implementations files
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */






