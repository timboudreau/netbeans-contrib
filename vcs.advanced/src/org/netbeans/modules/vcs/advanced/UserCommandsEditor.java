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

package com.netbeans.enterprise.modules.vcs.cmdline;
import java.awt.*;
import java.util.*;
import java.beans.*;

import com.netbeans.developer.modules.vcs.util.*;
import com.netbeans.developer.modules.vcs.cmdline.*;

/** Property editor for UserCommand.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserCommandsEditor implements PropertyEditor {
  private Debug E=new Debug("UserCommandsEditor", false); // NOI18N
  private Debug D=E;

  private Vector commands=new Vector(10);

  private PropertyChangeSupport changeSupport=null;

  //-------------------------------------------
  public UserCommandsEditor(){
    // each PropertyEditor should have a null constructor...
    changeSupport=new PropertyChangeSupport(this);
  }

  //-------------------------------------------
  public String getAsText(){
    // null if the value can't be expressed as an editable string...
    return ""+commands; // NOI18N
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
    return new UserCommandsPanel(this);
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
    return commands ;
  }

  //-------------------------------------------
  public void setValue(Object value) {
    if( !(value instanceof Vector) ){
      E.err("Vector expected instead of "+value); // NOI18N
      throw new IllegalArgumentException("Vector expected instead of "+value);
    }
    // make local copy of value - deep copy using clone
    commands=new Vector();
    Vector vect = (Vector) value;
    for(int i=0;i<vect.size (); i++) {
      com.netbeans.developer.modules.vcs.cmdline.UserCommand cmd = (com.netbeans.developer.modules.vcs.cmdline.UserCommand) vect.get (i);
      commands.add (cmd.clone ());
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
 *  13   Gandalf   1.12        1/27/00  Martin Entlicher NOI18N
 *  12   Gandalf   1.11        10/25/99 Pavel Buzek     copyright
 *  11   Gandalf   1.10        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  10   Gandalf   1.9         9/30/99  Pavel Buzek     
 *  9    Gandalf   1.8         9/8/99   Pavel Buzek     
 *  8    Gandalf   1.7         9/8/99   Pavel Buzek     class model changed, 
 *       customization improved, several bugs fixed
 *  7    Gandalf   1.6         8/31/99  Pavel Buzek     
 *  6    Gandalf   1.5         8/31/99  Pavel Buzek     
 *  5    Gandalf   1.4         5/4/99   Michal Fadljevic 
 *  4    Gandalf   1.3         5/4/99   Michal Fadljevic 
 *  3    Gandalf   1.2         4/26/99  Michal Fadljevic 
 *  2    Gandalf   1.1         4/22/99  Michal Fadljevic 
 *  1    Gandalf   1.0         4/21/99  Michal Fadljevic 
 * $
 */
