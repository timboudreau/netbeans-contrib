
package ent;

import java.math.BigDecimal;
import java.sql.Date;


/**
 * This is the business interface for Orders enterprise bean.
 */
public interface OrdersLocalBusiness {
    Integer getOrderNum();

    Integer getQuantity();

    void setQuantity(Integer quantity);

    BigDecimal getShippingCost();

    void setShippingCost(BigDecimal shippingCost);

    Date getSalesDate();

    void setSalesDate(Date salesDate);

    Date getShippingDate();

    void setShippingDate(Date shippingDate);

    String getFreightCompany();

    void setFreightCompany(String freightCompany);

    ent.CustomerLocal getCustomerId();

    void setCustomerId(ent.CustomerLocal customerId);

    ProductLocal getProductId();

    void setProductId(ProductLocal productId);
    
}
