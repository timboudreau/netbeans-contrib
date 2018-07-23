/*
 * UcastImpl.java
 *
 * Created on October 3, 2001, 12:47 PM
 */

package data.work;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

/** Unicast remote object implementing remote interface.
 *
 * @author tb115823
 * @version 1.0
 */
public class UcastImpl extends java.rmi.server.UnicastRemoteObject implements HelloWorld {

    /** Constructs UcastImpl object and exports it on default port.
     */
    public UcastImpl() throws RemoteException {
        super();
    }

    /** Constructs UcastImpl object and exports it on specified port.
     * @param port The port for exporting
     */
    public UcastImpl(int port) throws RemoteException {
        super(port);
    }

    /** Register UcastImpl object with the RMI registry.
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
            UcastImpl obj = new UcastImpl ();
            registerToRegistry("UcastImpl", obj, true);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

public String sayHello() throws RemoteException {
    return ("Hello");
}

public void exit() throws RemoteException {
}

}
