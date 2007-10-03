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

package org.netbeans.modules.vcs.advanced;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.beans.*;

import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.advanced.variables.Condition;

/** Property editor for variable conditions.
 *
 * @author Martin Entlicher
 */
public class UserConditionsEditor implements PropertyEditor {

    private PropertyChangeSupport changeSupport=null;
    private List conditions = new ArrayList();

    public UserConditionsEditor(){
        changeSupport=new PropertyChangeSupport(this);
    }

    public String getAsText(){
        return NbBundle.getMessage(UserConditionsEditor.class, "PROP_Conditions"); // NOI18N
    }

    public void setAsText(String text) {
        //D.deb("setAsText("+text+") ignored"); // NOI18N
    }

    public boolean supportsCustomEditor() {
        return true ;
    }

    public Component getCustomEditor(){
        return new UserConditionsPanel (this);
    }

    public String[] getTags(){
        // this property cannot be represented as a tagged value..
        return null ;
    }

    public String getJavaInitializationString() {
        return ""; // NOI18N
    }

    public Object getValue() {
        return (Condition[]) conditions.toArray(new Condition[0]);
    }

    public void setValue(Object value) {
        if (value == null) {
            conditions = new ArrayList();
        }
        if (!(value instanceof Condition[])){
            throw new IllegalArgumentException ();
        }
        Condition[] newConditions = (Condition[]) value;
        // make local copy of value - deep copy using clone
        conditions = new ArrayList();
        for (int i = 0; i < newConditions.length; i++) {
            conditions.add(newConditions[i].clone());
        }
        changeSupport.firePropertyChange("",null,null); // NOI18N
    }

    public boolean isPaintable() {
        return false ;
    }

    public void paintValue(Graphics gfx, Rectangle box){
        // silent noop
    }

    public void addPropertyChangeListener (PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener (PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }

}
