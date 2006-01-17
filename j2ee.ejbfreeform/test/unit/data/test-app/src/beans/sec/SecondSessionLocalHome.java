
package sec;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;


/**
 * This is the local-home interface for SecondSession enterprise bean.
 */
public interface SecondSessionLocalHome extends EJBLocalHome {
    
    SecondSessionLocal create()  throws CreateException;
    
    
}
