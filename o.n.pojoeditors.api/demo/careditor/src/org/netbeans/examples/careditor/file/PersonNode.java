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
package org.netbeans.examples.careditor.file;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.Action;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.examples.careditor.pojos.Person;
import org.netbeans.modules.dynactions.nodes.DynamicActionsNode;
import org.netbeans.modules.dynactions.nodes.PropertiesFactory;
import org.openide.actions.CutAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tim Boudreau
 */
final class PersonNode extends DynamicActionsNode {
    private final InstanceContent content;
    private final L l = new L();
    private Car car;
    public PersonNode(Person person, Car car) {
        this (person, car, new InstanceContent());
    }
    
    private PersonNode (Person person, Car car, InstanceContent content) {
        this (content);
        this.car = car;
        content.add (person);
        content.add (new CarExchangerImpl());
        content.add (new PersonRemoverImpl());
        updateDisplayName();
        //Listen for name changes
        person.addPropertyChangeListener(WeakListeners.propertyChange(l, person));
    }
    
    private PersonNode (InstanceContent content) {
        super (Children.LEAF, new AbstractLookup(content), "actioncontext");
        this.content = content;
    }
    
    private void updateDisplayName () {
        Person person = getLookup().lookup (Person.class);
        assert person != null;
        setDisplayName (NbBundle.getMessage(PersonNode.class, "CONCAT_NAME", 
                person.getFirstName(), person.getLastName()));
    }
    
    public void carChanged (Car car) {
        Car oldCar = this.car;
        if (car == oldCar) {
            return;
        }
        Person p = getLookup().lookup(Person.class);
        if (oldCar != null) {
            oldCar.removePassenger(p);
            content.remove (oldCar);
        }
        if (car != null) {
            car.addPassenger(p);
            content.add (car);
            this.car = car;
        }
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        //Get layer-registered ones
        Action[] layerActions = super.getActions(popup);
        //Add in some SystemActions
        Action[] result = new Action[layerActions.length + 3];
        System.arraycopy(layerActions, 0, result, 0, layerActions.length);
        result[layerActions.length + 1] = SystemAction.get(CutAction.class);
//        result[layerActions.length + 2] = SystemAction.get(PropertiesAction.class);
        return result;
    }
    
    private final class CarExchangerImpl implements CarExchanger {
        public void carChanged (Car car) {
            PersonNode.this.carChanged(car);
        }
    }
    
    private final class PersonRemoverImpl implements PersonRemover {
        public void remove() {
            Person p = getLookup().lookup (Person.class);
            car.removePassenger(p);
        }
    }
    
    private static final String[] PROPERTIES_TO_SHOW = new String[] {
        "firstName",
        "lastName",
        "age",
    };
    
    @Override
    protected Sheet createSheet() {
        PropertiesFactory factory = PropertiesFactory.create(Person.class, 
                new IP(), PROPERTIES_TO_SHOW);
        return factory.createSheet(getLookup().lookup(Person.class));
    }
    
    private static final class IP implements PropertiesFactory.InfoProvider {

        public String displayNameForProperty(String propName) {
            return NbBundle.getMessage (PersonNode.class, propName);
        }

        public String descriptionForProperty(String propName) {
            return NbBundle.getMessage (PersonNode.class, propName + "_desc");
        }

        public PropertyEditor propertyEditorForProperty(String property, Class valueType) {
            return null;
        }
        
    }
    
    private final class L implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String nm = evt.getPropertyName();
            if (Person.PROP_FIRST_NAME.equals(nm) || 
                    Person.PROP_LAST_NAME.equals(nm)) {
                updateDisplayName();
            }
        }
    }
}
