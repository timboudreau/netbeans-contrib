
package ent;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;


/**
 * This is the local-home interface for Orders enterprise bean.
 */
public interface OrdersLocalHome extends EJBLocalHome {
    
    OrdersLocal findByPrimaryKey(Integer key)  throws FinderException;

    public OrdersLocal create(Integer orderNum, Integer quantity, BigDecimal shippingCost, Date salesDate, Date shippingDate, String freightCompany, ent.CustomerLocal customerId, ProductLocal productId) throws CreateException;

    Collection findByOrderNum(Integer orderNum) throws FinderException;

    Collection findByQuantity(Integer quantity) throws FinderException;

    Collection findByShippingCost(BigDecimal shippingCost) throws FinderException;

    Collection findByFreightCompany(String freightCompany) throws FinderException;
    
    
}
