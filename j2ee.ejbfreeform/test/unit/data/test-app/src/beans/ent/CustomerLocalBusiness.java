
package ent;

import java.util.Collection;


/**
 * This is the business interface for Customer enterprise bean.
 */
public interface CustomerLocalBusiness {
    Integer getCustomerId();

    String getZip();

    void setZip(String zip);

    String getName();

    void setName(String name);

    String getAddressline1();

    void setAddressline1(String addressline1);

    String getAddressline2();

    void setAddressline2(String addressline2);

    String getCity();

    void setCity(String city);

    String getState();

    void setState(String state);

    String getPhone();

    void setPhone(String phone);

    String getFax();

    void setFax(String fax);

    String getEmail();

    void setEmail(String email);

    Integer getCreditLimit();

    void setCreditLimit(Integer creditLimit);

    Collection getOrdersBean();

    void setOrdersBean(Collection ordersBean);

    DiscountCodeLocal getDiscountCode();

    void setDiscountCode(DiscountCodeLocal discountCode);
    
}
