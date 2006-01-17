
package ent;

import java.math.BigDecimal;
import java.util.Collection;


/**
 * This is the business interface for DiscountCode enterprise bean.
 */
public interface DiscountCodeLocalBusiness {
    String getDiscountCode();

    BigDecimal getRate();

    void setRate(BigDecimal rate);

    Collection getCustomerBean();

    void setCustomerBean(Collection customerBean);
    
}
