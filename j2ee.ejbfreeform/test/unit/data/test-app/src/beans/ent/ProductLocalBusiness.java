
package ent;

import java.math.BigDecimal;
import java.util.Collection;


/**
 * This is the business interface for Product enterprise bean.
 */
public interface ProductLocalBusiness {
    Integer getProductId();

    BigDecimal getPurchaseCost();

    void setPurchaseCost(BigDecimal purchaseCost);

    Integer getQuantityOnHand();

    void setQuantityOnHand(Integer quantityOnHand);

    BigDecimal getMarkup();

    void setMarkup(BigDecimal markup);

    String getAvailable();

    void setAvailable(String available);

    String getDescription();

    void setDescription(String description);

    ent.ManufactureLocal getManufactureId();

    void setManufactureId(ent.ManufactureLocal manufactureId);

    ent.ProductCodeLocal getProductCode();

    void setProductCode(ent.ProductCodeLocal productCode);

    Collection getOrdersBean();

    void setOrdersBean(Collection ordersBean);
    
}
