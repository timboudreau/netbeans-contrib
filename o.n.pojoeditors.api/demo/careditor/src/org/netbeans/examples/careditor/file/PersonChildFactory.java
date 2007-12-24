/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.examples.careditor.file;

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
public class PersonChildFactory extends ChildFactory<Person> {
    private R receiver = new R();
    private ObjectLoader<Car> ldr;
    
    PersonChildFactory(ObjectLoader<Car> ldr){
        this.ldr = ldr;
    }
    
    public void refresh() {
        receiver.reset();
        super.refresh(true);
    }

    @Override
    protected Node createNodeForKey(Person key) {
        return new PersonNode(key);
    }

    protected boolean createKeys(List<Person> toPopulate) {
        Car c = null;
        boolean loadable = ldr.getState() != States.NOT_LOADABLE;
        int ct = 0;
        while (c == null && loadable && ct < 5) {
            System.err.println("Loop " + ct);
            synchronized (receiver) {
                //Try to get the car
                c = receiver.car;
                System.err.println("Got car? " + (c == null));
                //If we didn't get it and the receiver is not loading
                if (c == null && !receiver.loading) {
                    ldr.get(receiver);
                    //If it is cached, we can synchronously get it now
                    c = receiver.car;
                }
                System.err.println("Got car now? " + (c == null));
                //Didn't get it?
                if (c == null) {
                    //See if the synchronous flag was set to false
                    if (!receiver.sync) {
                        try {
                            //Wait for received() to be called, but time out
                            //eventually
                            System.err.println("Waiting...");
                            receiver.wait(5000);
                        } catch (InterruptedException ex) {
                            System.err.println("Interrupted");
                            break;
                        }
                    }
                    //Now, unless loading is taking a long time, get the car
                    c = receiver.car;
                    System.err.println("OK, got car now? " + (c == null));
                }
                //Check if loading failed so we can dump out of the loop
                loadable = ldr.getState() != States.NOT_LOADABLE;
            }
            ct++;
        }
        if (!loadable) {
            return true;
        }
        boolean result = c != null;
        if (result) {
            toPopulate.addAll(c.getPassengerList());
        }
        return result;
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
