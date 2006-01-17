
package ent;

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;


/**
 * This is the local-home interface for Manufacture enterprise bean.
 */
public interface ManufactureLocalHome extends EJBLocalHome {
    
    ManufactureLocal findByPrimaryKey(Integer key)  throws FinderException;

    public ManufactureLocal create(Integer manufactureId, String name, String addressline1, String addressline2, String city, String state, String zip, String phone, String fax, String email, String rep) throws CreateException;

    Collection findByManufactureId(Integer manufactureId) throws FinderException;

    Collection findByName(String name) throws FinderException;

    Collection findByAddressline1(String addressline1) throws FinderException;

    Collection findByAddressline2(String addressline2) throws FinderException;

    Collection findByCity(String city) throws FinderException;

    Collection findByState(String state) throws FinderException;

    Collection findByZip(String zip) throws FinderException;

    Collection findByPhone(String phone) throws FinderException;

    Collection findByFax(String fax) throws FinderException;

    Collection findByEmail(String email) throws FinderException;

    Collection findByRep(String rep) throws FinderException;
    
    
}
