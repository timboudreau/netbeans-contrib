
package ent;

import java.math.BigDecimal;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;


/**
 * This is the local-home interface for Product enterprise bean.
 */
public interface ProductLocalHome extends EJBLocalHome {
    
    ProductLocal findByPrimaryKey(Integer key)  throws FinderException;

    public ProductLocal create(Integer productId, BigDecimal purchaseCost, Integer quantityOnHand, BigDecimal markup, String available, String description, ent.ManufactureLocal manufactureId, ent.ProductCodeLocal productCode) throws CreateException;

    Collection findByProductId(Integer productId) throws FinderException;

    Collection findByPurchaseCost(BigDecimal purchaseCost) throws FinderException;

    Collection findByQuantityOnHand(Integer quantityOnHand) throws FinderException;

    Collection findByMarkup(BigDecimal markup) throws FinderException;

    Collection findByAvailable(String available) throws FinderException;

    Collection findByDescription(String description) throws FinderException;
    
    
}
