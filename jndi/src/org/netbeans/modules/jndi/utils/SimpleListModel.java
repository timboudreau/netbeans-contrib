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

package com.netbeans.enterprise.modules.jndi.utils;


import javax.swing.AbstractListModel;
import java.util.Vector;


/** This class represent an Dat Model for JList component
 */ 
public class SimpleListModel extends javax.swing.AbstractListModel {
  
  /** Data pool*/
  private Vector data;
  
  /** Constructor*/
  public SimpleListModel() {
     this.data= new Vector();
  }
  
  /** Adding of element to Data Model
   *  @param Object object to be inserted
   *  @return void 
   */
  public void addElement(Object obj){
    this.data.addElement(obj);
    this.fireIntervalAdded(this,this.data.size()-1,this.data.size()-1);
  }
  
  
  /** Removing of element at given position
   *  @param int index of element
   *  @return void
   */
  public void removeElementAt(int index){
    this.data.removeElementAt(index);
    this.fireIntervalRemoved(this,index,index);
  }
  
  /** Clear the Data Model
   *  
   *  @return void
   */
  public void clear(){
    int upIndex=this.data.size()-1;
    if (upIndex<0) return; //Nothing to clear
    this.data.removeAllElements();
    this.fireIntervalRemoved(this,0,upIndex);
  }
  
  
  /** Sets the data
   *  @param Vector data
   */
  public void setData (Vector v) {
    int upindex = this.data.size();
    if (upindex >= 0 )
      this.fireIntervalRemoved(this,0,upindex);
    this.data=v;
    upindex = this.data.size();
    if (upindex >= 0 )
      this.fireIntervalAdded(this,0,upindex);
  }
  
  
  /** Returns Vector representation of data
   *
   *  @return Vector the data
   */
  public Vector asVector(){
      return this.data;
  }
  
  /** Returns element at given index
   *  @param int index of element
   *  @return Object object at index or null
   */
  public Object getElementAt(int index){
    return this.data.elementAt(index);
  }
  
  /**  Returns number of elements in Data Model
   *   
   *   @return int number of elements
   */
  public int getSize(){
    return this.data.size();
  }
  
}