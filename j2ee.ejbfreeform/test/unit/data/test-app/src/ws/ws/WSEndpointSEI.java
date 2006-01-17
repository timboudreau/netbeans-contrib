package ws;


/**
 * This is the service endpoint interface for the WSEndpointweb service.
 * Created 12.1.2006 17:54:51
 * @author jungi
 */

public interface WSEndpointSEI extends java.rmi.Remote {
    /**
     * Web service operation
     */
    public java.lang.String greet(String s) throws java.rmi.RemoteException;
    
}
