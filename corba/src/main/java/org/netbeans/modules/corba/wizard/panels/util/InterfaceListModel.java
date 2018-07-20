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
