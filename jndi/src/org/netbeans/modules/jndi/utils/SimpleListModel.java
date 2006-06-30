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

package org.netbeans.modules.jndi.utils;


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
    
    /** Changes object on given index
     *  @param int index
     *  @param Object new value
     *  @return Object old value
     */
    public Object changeElementAt (int index, Object obj) {
        Object result = this.data.remove (index);
        this.data.add (index, obj);
        this.fireContentsChanged (this,index,index);
        return result;
    }

    /**  Returns number of elements in Data Model
     *   
     *   @return int number of elements
     */
    public int getSize(){
        return this.data.size();
    }

}