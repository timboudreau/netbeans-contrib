
package ent;

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;


/**
 * This is the local-home interface for Customer enterprise bean.
 */
public interface CustomerLocalHome extends EJBLocalHome {
    
    CustomerLocal findByPrimaryKey(Integer key)  throws FinderException;

    public CustomerLocal create(Integer customerId, String zip, String name, String addressline1, String addressline2, String city, String state, String phone, String fax, String email, Integer creditLimit, DiscountCodeLocal discountCode) throws CreateException;

    Collection findByCustomerId(Integer customerId) throws FinderException;

    Collection findByZip(String zip) throws FinderException;

    Collection findByName(String name) throws FinderException;

    Collection findByAddressline1(String addressline1) throws FinderException;

    Collection findByAddressline2(String addressline2) throws FinderException;

    Collection findByCity(String city) throws FinderException;

    Collection findByState(String state) throws FinderException;

    Collection findByPhone(String phone) throws FinderException;

    Collection findByFax(String fax) throws FinderException;

    Collection findByEmail(String email) throws FinderException;

    Collection findByCreditLimit(Integer creditLimit) throws FinderException;
    
    
}
