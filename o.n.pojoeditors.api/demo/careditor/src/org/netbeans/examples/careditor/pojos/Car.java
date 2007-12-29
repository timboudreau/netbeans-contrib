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
 * Contributor(s): Tom Wheeler, Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.examples.careditor.pojos;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Car implements Serializable {
    
    private static Logger logger = Logger.getLogger(Car.class.getName());
    
    private transient PropertyChangeSupport pcs  = new PropertyChangeSupport(this);
    
    public static final String PROP_MAKE = "make";
    public static final String PROP_MODEL = "model";
    public static final String PROP_YEAR = "year";
    public static final String PROP_PASSENGER_LIST = "passengerList";
    
    private String make;
    private String model;
    private int year;
    private List<Person> passengerList;
    
    public Car() {
        passengerList = new ArrayList<Person>();
    }

    public String getMake() {
        return make;
    }

    public void setMake(final String make) {
        String oldMake = this.make;
        this.make = make;
        if (pcs != null) {
            pcs.firePropertyChange(PROP_MAKE, oldMake, make);
        }
    }

    public String getModel() {
        return model;
    }

    @Override
    protected void finalize() throws Throwable {
        System.err.println("Car " + make + " " + model + " " + year + 
                " finalized");
    }

    public void setModel(final String model) {
        String oldModel = this.model;
        this.model = model;
        if (pcs != null) {
            pcs.firePropertyChange(PROP_MODEL, oldModel, model);
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        int oldYear = this.year;
        this.year = year;
        if (pcs != null) {
            pcs.firePropertyChange(PROP_YEAR, oldYear, year);
        }
    }
    
    public List<Person> getPassengerList() {
        return Collections.unmodifiableList(passengerList);
    }
    
    public void addPassenger(Person p) {
        assert(p != null);
        
        List<Person> oldPassengerList = new ArrayList<Person>();
        oldPassengerList.addAll(passengerList);
        
        passengerList.add(p);

        logger.info("adding passenger; has listeners? " + (pcs == null ? "false" : 
            ""+ pcs.hasListeners(Car.PROP_PASSENGER_LIST)));
        if (pcs != null) {
            pcs.firePropertyChange(PROP_PASSENGER_LIST, oldPassengerList, passengerList);
        }
    }
    
    public void removePassenger(Person p) {
        assert(p != null);
        assert(passengerList.contains(p));
        
        List<Person> oldPassengerList = new ArrayList<Person>();
        oldPassengerList.addAll(passengerList);
        
        passengerList.remove(p);
        if (pcs != null) {
            pcs.firePropertyChange(PROP_PASSENGER_LIST, oldPassengerList, passengerList);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        if (pcs == null) {
            pcs  = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        if (pcs == null) {
            return;
        }
        pcs.removePropertyChangeListener(pcl);
    }
    
    /**
     * Called by PersonNode when a passenger's properties change
     */
    public void fireForPersonChange() {
        //In practice one would probably override the serialization code to
        //listen on Person objects, and attach a listener to them on load/add
        if (pcs != null) {
            pcs.firePropertyChange("person", null, null);
        }
    }
    
    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        
        buff.append("Car: ["); 
        buff.append("Make: "  + make + ", ");
        buff.append("Model: "  + model + ", ");
        buff.append("Year: "  + year + ", ");
        
        if (passengerList == null || passengerList.size() == 0) {
            buff.append(" No passengers");
        } else {
            buff.append(passengerList.size() + " passengers: {");
            for (Person person : passengerList) {
                buff.append(person);
                buff.append(", ");
            }
            buff.append("}");
        }
        
        buff.append("]");
        
        return buff.toString();
    }
}
