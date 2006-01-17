
package ent;

import java.util.Collection;


/**
 * This is the business interface for Manufacture enterprise bean.
 */
public interface ManufactureLocalBusiness {
    Integer getManufactureId();

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

    String getZip();

    void setZip(String zip);

    String getPhone();

    void setPhone(String phone);

    String getFax();

    void setFax(String fax);

    String getEmail();

    void setEmail(String email);

    String getRep();

    void setRep(String rep);

    Collection getProductBean();

    void setProductBean(Collection productBean);
    
}
