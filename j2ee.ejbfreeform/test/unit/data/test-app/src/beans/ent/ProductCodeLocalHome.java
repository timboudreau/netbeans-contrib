
package ent;

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;


/**
 * This is the local-home interface for ProductCode enterprise bean.
 */
public interface ProductCodeLocalHome extends EJBLocalHome {
    
    ProductCodeLocal findByPrimaryKey(String key)  throws FinderException;

    public ProductCodeLocal create(String prodCode, String discountCode, String description) throws CreateException;

    Collection findByProdCode(String prodCode) throws FinderException;

    Collection findByDiscountCode(String discountCode) throws FinderException;

    Collection findByDescription(String description) throws FinderException;
    
    
}
