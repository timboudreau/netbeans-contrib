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

package org.netbeans.modules.corba.wizard.panels.util;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import java.util.ArrayList;
/**
 *
 * @author  tzezula
 * @version 
 */
public class InterfaceListModel extends AbstractListModel {

    private ArrayList list;
    
    /** Creates new InterfaceListModel */
    public InterfaceListModel() {
        this.list = new ArrayList();
    }
    
    
    public Object getElementAt (int index) {
        if (index < 0 || index >= list.size())
            return null;
        return list.get(index);
    }
    
    public String getValue (int index) {
        if (index < 0 || index >= this.list.size())
            return null;
        Object o = this.list.get (index);
        if ( ! (o instanceof String))
            return null;
        return (String) o;
    }
    
    public int indexOf (String str) {
        for (int i=0; i< list.size(); i++) {
            Object o = list.get (i);
            if (o.equals(str)) {
                    return i;
            }
        }
        return -1;
    }
    
    public void clear () {
        int size = this.list.size();
        size = (size == 0)?0:size-1;
        this.list.clear();
        this.fireIntervalRemoved (this,0,size);
    }
    
    public void remove (Object o) {
        int index = this.list.indexOf (o);
        this.remove (index);
    }
    
    public void remove (int index) {
        if (index < 0 || index >= list.size())
            return;
        this.list.remove (index);
        this.fireIntervalRemoved (this,index,index);
    }
    
    public void add (Object o) {
        this.list.add (o);
        this.fireIntervalAdded (this,this.list.size()-1,list.size()-1);
    }
    
    public int getSize () {
        return this.list.size();
    }

}
