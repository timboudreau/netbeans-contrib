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

import com.netbeans.enterprise.modules.vcs.util.*;

/** Property editor for UserCommand.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserCommandsEditor implements PropertyEditor {
  private Debug E=new Debug("UserCommandsEditor",true);
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
    return ""+commands;
  }

  //-------------------------------------------
  public void setAsText(String text) {
    //D.deb("setAsText("+text+") ignored");
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
    return "";
  }
    
  //-------------------------------------------
  public Object getValue(){
    return commands ;
  }

  //-------------------------------------------
  public void setValue(Object value) {
    if( !(value instanceof Vector) ){
      E.err("Vector expected instead of "+value);
      throw new IllegalArgumentException("Vector expected instead of "+value);
    }
    commands = (Vector) value;
    changeSupport.firePropertyChange("",null,null);
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
 *  7    Gandalf   1.6         8/31/99  Pavel Buzek     
 *  6    Gandalf   1.5         8/31/99  Pavel Buzek     
 *  5    Gandalf   1.4         5/4/99   Michal Fadljevic 
 *  4    Gandalf   1.3         5/4/99   Michal Fadljevic 
 *  3    Gandalf   1.2         4/26/99  Michal Fadljevic 
 *  2    Gandalf   1.1         4/22/99  Michal Fadljevic 
 *  1    Gandalf   1.0         4/21/99  Michal Fadljevic 
 * $
 */
