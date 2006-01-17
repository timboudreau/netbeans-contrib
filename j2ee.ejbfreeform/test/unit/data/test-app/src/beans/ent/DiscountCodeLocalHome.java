
package ent;

import java.math.BigDecimal;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;


/**
 * This is the local-home interface for DiscountCode enterprise bean.
 */
public interface DiscountCodeLocalHome extends EJBLocalHome {
    
    DiscountCodeLocal findByPrimaryKey(String key)  throws FinderException;

    public DiscountCodeLocal create(String discountCode, BigDecimal rate) throws CreateException;

    Collection findByDiscountCode(String discountCode) throws FinderException;

    Collection findByRate(BigDecimal rate) throws FinderException;
    
    
}
