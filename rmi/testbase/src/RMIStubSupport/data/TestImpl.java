/*
 * TestImpl.java
 *
 * Created on August 20, 2001, 12:13 PM
 */

package RMIStubSupport.data;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

/** Unicast remote object implementing Test interface.
 *
 * @author mryzl
 * @version 1.0
 */
public class TestImpl extends UnicastRemoteObject implements Test {

    /** Constructs TestImpl object and exports it on default port.
     */
    public TestImpl() throws RemoteException {
        super();
    }

    /** Constructs TestImpl object and exports it on specified port.
     * @param port The port for exporting
     */
    public TestImpl(int port) throws RemoteException {
        super(port);
    }

    /** Register TestImpl object with the RMI registry.
     * @param name - name identifying the service in the RMI registry
     * @param create - create local registry if necessary
     * @throw RemoteException if cannot be exported or bound to RMI registry
     * @throw MalformedURLException if name cannot be used to construct a valid URL
     * @throw IllegalArgumentException if null passed as name
     */
    public static void registerToRegistry(String name, Remote obj, boolean create) throws RemoteException, MalformedURLException{

        if (name == null) throw new IllegalArgumentException("registration name can not be null");

        try {
            Naming.rebind(name, obj);
        } catch (RemoteException ex){
            if (create) {
                Registry r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                r.rebind(name, obj);
            } else throw ex;
        }
    }

    /** Main method.
     */
    public static void main(String[] args) {
        System.setSecurityManager(new RMISecurityManager());

        try {
            TestImpl obj = new TestImpl();
            registerToRegistry("TestImpl", obj, true);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static class A implements Remote {
    }
}
