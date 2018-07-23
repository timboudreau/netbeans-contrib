/*
 * HelloWorldActiv.java
 *
 * Created on 5. bøezen 2001, 9:15
 */

package data.work;

import java.rmi.*;
import java.io.*;
import java.rmi.activation.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.MalformedURLException;
import java.util.Properties;

/** Activatable server implementing remote interface.
 *
 * @author asotona
 * @version 1.0
 */

public class HelloWorldActiv extends Activatable implements HelloWorld {
    public static final java.util.ResourceBundle bundle=java.util.ResourceBundle.getBundle("data/RMITests");

    /** Constructs HelloWorldActiv object and exports it on default port.
     * Register the object with the activation system      
     */
    public HelloWorldActiv(ActivationID id, MarshalledObject data) throws RemoteException {
        super(id, 0);
    }

    /** Register HelloWorldActiv object to the activation system.
     * @param class - name of class of remote object
     */
    public static Remote registerToActivation(String classname) throws ActivationException, 
        UnknownGroupException, RemoteException, java.io.IOException {
        Properties currProps = System.getProperties();
        Properties props = new Properties();

        // prepare properties
        props.put("java.security.policy", currProps.getProperty("java.security.policy"));
        props.put("java.rmi.server.codebase", currProps.getProperty("java.rmi.server.codebase"));
        props.put("java.rmi.server.hostname", currProps.getProperty("java.rmi.server.hostname"));
        
        //assume that implementation class is in the same location
        ActivationGroupDesc agDesc = new ActivationGroupDesc(props, null);
        ActivationGroupID agID = ActivationGroup.getSystem().registerGroup(agDesc);
        
        String location = HelloWorldActiv.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
        MarshalledObject data = new MarshalledObject(classname);
        ActivationDesc desc = new ActivationDesc(agID, classname, location, data);
        return (Remote) Activatable.register(desc);
    }

    /** Register HelloWorldActiv object with the RMI registry.
     * @param name - name identifying the service in the RMI registry
     * @param obj - remote object
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
                new Thread() {
                    public void run() {
                        // prevent registry from exiting
                        while (true) {
                            try {
                                Thread.sleep(Long.MAX_VALUE);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }.start();
            } else throw ex;
        }
    }

    /** Main method.
     */
    public static void main(String[] args) {
        if (args.length != 1) 
            System.err.println("Parameter expected: output log file");
        else {
            try {
                //redirects Err and Out to given file
                PrintStream ps=new PrintStream(new FileOutputStream(args[1],true),true);
                System.setErr(ps);
                System.setOut(ps);
            } catch(Exception e) {};
            try {
                Remote obj = registerToActivation("work.HelloWorldActiv");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public String sayHello() throws RemoteException {
        return bundle.getString("Hello_World");
    }

    public void exit() throws RemoteException {
        System.out.println(bundle.getString("Finished."));
        System.exit(0);
    }
    
}
