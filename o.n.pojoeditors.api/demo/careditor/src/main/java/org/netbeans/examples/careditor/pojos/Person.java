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
 * 
 * Contributor(s): Tom Wheeler
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.examples.careditor.pojos;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class Person implements Serializable {
    
    private transient PropertyChangeSupport pcs  = new PropertyChangeSupport(this);
    
    public static final String PROP_FIRST_NAME = "firstName";
    public static final String PROP_LAST_NAME = "lastName";
    public static final String PROP_AGE = "age";
    
    private String firstName;
    private String lastName;
    private int age;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        String oldFirstName = this.firstName;
        this.firstName = firstName;
        if (pcs != null) {
            pcs.firePropertyChange(PROP_FIRST_NAME, oldFirstName, firstName);
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        String oldLastName = this.lastName;
        this.lastName = lastName;
        if (pcs != null) {
            pcs.firePropertyChange(PROP_LAST_NAME, oldLastName, lastName);
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        int oldAge = this.age;
        this.age = age;
        if (pcs != null) {
            pcs.firePropertyChange(PROP_AGE, oldAge, age);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(pcl);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        System.err.println("Person " + firstName + " " + lastName + " " + age + 
                " finalized");
    }
    
    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        
        buff.append("Person: ["); 
        buff.append("First Name: "  + firstName + ", ");
        buff.append("Last Name: "  + lastName + ", ");
        buff.append("Age: "  + age + " ");
        buff.append("]");
        
        return buff.toString();
    }
}
