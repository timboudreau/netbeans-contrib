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

package org.netbeans.modules.vcs.advanced;

import java.awt.*;
import java.util.*;
import java.beans.*;

import org.openide.util.NbBundle;

import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;

/** Property editor for user variables.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserConditionedVariablesEditor implements PropertyEditor {

    private PropertyChangeSupport changeSupport=null;
    //private Vector variables=new Vector(10);
    private ConditionedVariables cvars;

    //-------------------------------------------
    public UserConditionedVariablesEditor(){
        // each PropertyEditor should have a null constructor...
        changeSupport=new PropertyChangeSupport(this);
    }

    //-------------------------------------------
    public String getAsText(){
        // null if the value can't be expressed as an editable string...
        return NbBundle.getMessage(UserConditionedVariablesEditor.class, "PROP_Variables"); // NOI18N
    }

    //-------------------------------------------
    public void setAsText(String text) {
        //D.deb("setAsText("+text+") ignored"); // NOI18N
    }

    //-------------------------------------------
    public boolean supportsCustomEditor() {
        return true ;
    }

    //-------------------------------------------
    public Component getCustomEditor(){
        return new UserConditionedVariablesPanel (this);
    }

    //-------------------------------------------
    public String[] getTags(){
        // this property cannot be represented as a tagged value..
        return null ;
    }

    //-------------------------------------------
    public String getJavaInitializationString() {
        return ""; // NOI18N
    }

    //-------------------------------------------
    public Object getValue(){
        return cvars;
    }

    //-------------------------------------------
    public void setValue(Object value) {
        if (value == null) {
            cvars = new ConditionedVariables(new ArrayList(), new HashMap(), new HashMap());
        }
        if (!(value instanceof ConditionedVariables)){
            throw new IllegalArgumentException ();
        }
        // make local copy of value - deep copy using clone
        cvars = (ConditionedVariables) value;
        cvars = (ConditionedVariables) cvars.clone();
        changeSupport.firePropertyChange("",null,null); // NOI18N
    }

    //-------------------------------------------
    public boolean isPaintable() {
        return false ;
    }

    //-------------------------------------------
    public void paintValue(Graphics gfx, Rectangle box){
        // silent noop
    }

    //-------------------------------------------
    public void addPropertyChangeListener (PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }

    //-------------------------------------------
    public void removePropertyChangeListener (PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }

}
