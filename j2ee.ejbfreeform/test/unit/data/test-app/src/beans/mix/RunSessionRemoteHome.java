
package mix;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;


/**
 * This is the home interface for RunSession enterprise bean.
 */
public interface RunSessionRemoteHome extends EJBHome {
    
    RunSessionRemote create()  throws CreateException, RemoteException;
    
    
}
