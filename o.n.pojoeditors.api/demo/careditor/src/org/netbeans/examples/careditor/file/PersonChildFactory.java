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

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import org.netbeans.api.objectloader.ObjectLoader;
import org.netbeans.api.objectloader.ObjectReceiver;
import org.netbeans.api.objectloader.States;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.examples.careditor.pojos.Person;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 */
class PersonChildFactory extends ChildFactory<Person> {
    private R receiver = new R();
    private ObjectLoader<Car> ldr;
    
    PersonChildFactory(ObjectLoader<Car> ldr){
        this.ldr = ldr;
    }
    
    public void refresh() {
        receiver.reset();
        super.refresh(true);
    }

    private Reference<Car> carRef = null;
    @Override
    protected Node createNodeForKey(Person key) {
        Car car = carRef == null ? null : carRef.get();
        if (car != null) {
            return new PersonNode(key, car);
        } else {
            //We're populating children, but the parent node has been
            //garbage collected - it was hidden or similar while
            //we were creating the keys
            return null;
        }
    }

    protected boolean createKeys(List<Person> toPopulate) {
        boolean loadable = ldr.getState() != States.NOT_LOADABLE;
        if (!loadable) {
            return true;
        } else {
            try {
                Car c = ldr.getSynchronous();
                boolean result = c != null;
                if (result) {
                    carRef = new WeakReference<Car>(c);
                    toPopulate.addAll(c.getPassengerList());
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                return true;
            }
        }
        return true;
    }
    
    
    private static class R implements ObjectReceiver<Car> {
        Car car;
        private volatile boolean sync;
        private volatile boolean loading;
        public void setSynchronous(boolean val) {
            sync = val;
            loading = true;
        }

        public void received(Car t) {
            synchronized(this) {
                car = t;
                notifyAll();
            }
            loading = false;
        }

        public void failed(Exception e) {
            loading = false;
            synchronized (this) {
                car = null;
                notifyAll();
            }
            Exceptions.printStackTrace(e);
        }
        
        private synchronized void reset() {
            car = null;
            sync = false;
            loading = false;
        }
    }
}
