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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.objectloader.CacheStrategies;
import org.netbeans.examples.careditor.editor.AlternateCarEditorTopComponent;
import org.netbeans.examples.careditor.editor.CarEditorTopComponent;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.examples.careditor.pojos.Person;
import org.netbeans.pojoeditors.api.EditorFactory;
import org.netbeans.pojoeditors.api.PojoDataNode;
import org.netbeans.pojoeditors.api.PojoDataObject;
import org.netbeans.pojoeditors.api.PojoEditor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

public class CarDataObject extends PojoDataObject<Car> {
    private final PCL pcl = new PCL();
    
    public CarDataObject(FileObject pf, CarDataLoader loader) throws DataObjectExistsException, IOException {
        super (pf, loader, Car.class, CacheStrategies.WEAK, new CarEditorFactory());
    }

    @Override
    protected PojoDataNode createNode() {
        CarDataNode node = new CarDataNode(this);
        return node;
    }

    @Override
    protected DataObject handleCreateFromTemplate(DataFolder df, String name) throws IOException {
        FileObject folder = df.getPrimaryFile();
        FileObject nue = folder.createData(name + ".car"); //NOI18N
        FileLock lock = nue.lock();
        OutputStream out = nue.getOutputStream(lock);
        try {
            Car car = new Car();
            ObjectOutputStream oout = new ObjectOutputStream (out);
            oout.writeObject(car);
        } finally {
            out.close();
            lock.releaseLock();
        }
        return DataObject.find(nue);
    }
    
    @Override
    protected Car load (InputStream stream) throws IOException {
        ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(stream));
        try {
            Object result = in.readObject();
            Thread.sleep(800);
            return (Car) result;
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            throw new IOException (ex);
        } finally {
            in.close();
        }
    }
    
    @Override
    protected boolean propertyChange(Car src, String property, Object old, Object nue) {
        if (property.equals(Car.PROP_PASSENGER_LIST)) {
            super.hintNodeChildrenChanged();
        }
        return super.propertyChange(src, property, old, nue);
    }

    @Override
    protected void onLoad(Car car) {
        //Ensure that the user changing properties of the passengers marks
        //the dataobject as modified
        for (Person p : car.getPassengerList()) {
            p.addPropertyChangeListener(WeakListeners.propertyChange(pcl, p));
        }
    }
    
    private static final class CarEditorFactory extends EditorFactory {
        @Override
        public PojoEditor create(PojoDataObject obj, Kind kind) {
            switch (kind) {
                case OPEN :
                    return new CarEditorTopComponent ((CarDataObject) obj);
                case EDIT :
                    return new AlternateCarEditorTopComponent ((CarDataObject) obj);                    
                default :
                    throw new AssertionError();
            }
        }

        @Override
        public List supportedKinds() {
            return Arrays.asList(Kind.OPEN, Kind.EDIT);
        }

        @Override
        public Kind defaultKind() {
            return Kind.OPEN;
        }
    }
    
    private class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Car car = ldr.getCachedInstance();
            //Make sure it's not a person that used to belong to us but
            //was pasted into another car
            if (car.getPassengerList().contains(evt.getSource())) {
                setModified(true);
            }
        }
    }
}
