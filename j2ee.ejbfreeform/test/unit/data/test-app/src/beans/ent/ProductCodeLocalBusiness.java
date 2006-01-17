
package ent;

import java.util.Collection;


/**
 * This is the business interface for ProductCode enterprise bean.
 */
public interface ProductCodeLocalBusiness {
    String getProdCode();

    String getDiscountCode();

    void setDiscountCode(String discountCode);

    String getDescription();

    void setDescription(String description);

    Collection getProductBean();

    void setProductBean(Collection productBean);
    
}
