/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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