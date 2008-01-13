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

import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import org.netbeans.api.objectloader.ObjectLoader;
import org.netbeans.api.objectloader.ObjectReceiver;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.examples.careditor.file.PersonNode.CarExchanger;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.examples.careditor.pojos.Person;
import org.netbeans.pojoeditors.api.PojoDataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.PasteType;

public class CarDataNode extends PojoDataNode<Car> {
    private static final String IMAGE_ICON_BASE = "org/netbeans/examples/careditor/file/car.gif";
    private final PersonChildFactory factory;
    private final PCL pcl = new PCL();
    @SuppressWarnings("Unchecked")
    public CarDataNode(CarDataObject obj) {
        this(obj, new PersonChildFactory(obj.getLookup().lookup(ObjectLoader.class)));
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    
    private CarDataNode (CarDataObject obj, PersonChildFactory factory) {
        super (obj, Children.create(factory, true), obj.getLookup(), "actioncontext");
        this.factory = factory;
    }

    @Override
    protected void onLoad(Car car) {
        System.err.println("CarDataNode.onLoad()");
        //Ensure that the user changing properties of the passengers marks
        //the dataobject as modified
        for (Person p : car.getPassengerList()) {
            p.addPropertyChangeListener(WeakListeners.propertyChange(pcl, p));
        }
    }

    @Override
    protected String[] getPropertyNames() {
        return new String[] {
            "make",
            "model",
            "year",
            "passengerList",
        };
    }

    @Override
    protected void hintChildrenChanged() {
        factory.refresh();
    }
    
    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        PasteType result = createPassengerPasteType(t, action);
        if (result == null) {
            result = super.getDropType(t, action, index);
        }
        return result;
    }
    
    private PasteType createPassengerPasteType (Transferable t, int action) {
        Node n = NodeTransfer.node(t, action);
        CarExchanger exchanger = n == null ? null : 
            n.getLookup().lookup(CarExchanger.class);
        PasteType result = null;
        if (exchanger != null) {
            result = new PassengerPasteType(exchanger, this);
        }
        return result;
    }

    @Override
    protected void createPasteTypes(Transferable t, List<PasteType> s) {
        PasteType type = createPassengerPasteType(t, 0);
        if (type != null) {
            s.add(type);
        }
    }    
    
    private static final class PassengerPasteType extends PasteType {
        private final CarExchanger exchanger;
        private final CarDataNode target;
        PassengerPasteType (CarExchanger exchanger, CarDataNode target) {
            this.exchanger = exchanger;
            this.target = target;
        }

        @Override
        public Transferable paste() throws IOException {
            @SuppressWarnings("Unchecked")
            ObjectLoader<Car> ldr = target.getLookup().lookup(ObjectLoader.class);
            Car car = ldr.getCachedInstance();
            if (car != null) {
                exchanger.carChanged(car);
            } else {
                ldr.get(new OL());
            }
            return ExTransferable.EMPTY;
        }
        
        private class OL implements ObjectReceiver<Car> {
            private ProgressHandle handle;
            public void setSynchronous(boolean val) {
                if (!val) {
                    startProgress();
                }
            }

            public void received(Car t) {
                exchanger.carChanged(t);
                stopProgress();
            }

            public void failed(Exception e) {
                Exceptions.printStackTrace(e);
                stopProgress();
            }
            
            private synchronized void startProgress() {
                handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(CarDataNode.class, "MSG_Loading", 
                        target.getDisplayName()));
                handle.start();
            }
            
            private synchronized void stopProgress() {
                if (handle != null) {
                    handle.finish();
                }
            }
        }
    }

    @Override
    protected PropertyEditor propertyEditorForProperty(String propName, Class valueType) {
        if (Car.PROP_PASSENGER_LIST.equals(propName)) {
            return new PassengerListPropertyEditor();
        }
        return null;
    }
    
    private class PassengerListPropertyEditor extends PropertyEditorSupport {
        PassengerListPropertyEditor() {
            
        }

        @Override
        public String getAsText() {
            StringBuilder sb = new StringBuilder();
            List <Person> l = (List<Person>) getValue();
            if (l != null) {
                for (Iterator<Person> i=l.iterator(); i.hasNext();) {
                    Person p = i.next();
                    String concatName = NbBundle.getMessage (CarDataNode.class,
                            "FirstNameLastName", p.getFirstName(), 
                            p.getLastName());
                    sb.append (concatName);
                    if (i.hasNext()) {
                        sb.append (", ");
                    }
                }
            }
            return sb.toString();
        }
        
    }
    
    private class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            System.err.println("Car node got property change from person");
            CarDataObject ob = getLookup().lookup (CarDataObject.class);
            ob.setModified(true);
        }
    }
}
