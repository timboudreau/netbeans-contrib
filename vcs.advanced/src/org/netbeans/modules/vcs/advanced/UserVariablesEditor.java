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

package org.netbeans.modules.vcs.advanced;

import java.awt.*;
import java.util.*;
import java.beans.*;

import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;

/** Property editor for user variables.
 *
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserVariablesEditor implements PropertyEditor {

    private PropertyChangeSupport changeSupport=null;
    private Vector variables=new Vector(10);

    //-------------------------------------------
    public UserVariablesEditor(){
        // each PropertyEditor should have a null constructor...
        changeSupport=new PropertyChangeSupport(this);
    }

    //-------------------------------------------
    public String getAsText(){
        // null if the value can't be expressed as an editable string...
        return NbBundle.getMessage(UserVariablesEditor.class, "PROP_variables"); // NOI18N
    }

    //-------------------------------------------
    public void setAsText(String text) {
    }

    //-------------------------------------------
    public boolean supportsCustomEditor() {
        return true ;
    }

    //-------------------------------------------
    public Component getCustomEditor(){
        return new UserVariablesPanel (this);
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
        return variables ;
    }

    //-------------------------------------------
    public void setValue(Object value) {
        if (value == null) {
            variables = new Vector(10);
        }
        if( !(value instanceof Vector) ){
            throw new IllegalArgumentException ();
        }
        // make local copy of value - deep copy using clone
        variables=new Vector();
        Vector vect = (Vector) value;
        for(int i=0;i<vect.size (); i++) {
            VcsConfigVariable var = (VcsConfigVariable)vect.get (i);
            variables.add (var.clone ());
        }
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

/*
 * <<Log>>
 *  2    Gandalf   1.1         1/27/00  Martin Entlicher NOI18N
 *  1    Gandalf   1.0         11/24/99 Martin Entlicher 
 * $
 */
