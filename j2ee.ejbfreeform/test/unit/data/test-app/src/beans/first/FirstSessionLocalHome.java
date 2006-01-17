
package first;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;


/**
 * This is the local-home interface for FirstSession enterprise bean.
 */
public interface FirstSessionLocalHome extends EJBLocalHome {
    
    FirstSessionLocal create()  throws CreateException;
    
    
}
