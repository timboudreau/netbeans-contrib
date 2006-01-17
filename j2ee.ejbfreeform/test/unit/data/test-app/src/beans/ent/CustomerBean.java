package ent;

import java.util.Collection;
import javax.ejb.*;

/**
 * This is the bean class for the CustomerBean enterprise bean.
 * Created 12.1.2006 17:52:53
 * @author jungi
 */
public abstract class CustomerBean implements EntityBean, CustomerLocalBusiness {
    private EntityContext context;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click on the + sign on the left to edit the code.">
    // TODO Consider creating Transfer Object to encapsulate data
    // TODO Review finder methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context = null;
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
        
    }
    // </editor-fold>
    
    
    public abstract Integer getCustomerId();
    public abstract void setCustomerId(Integer customerId);
    
    public abstract String getZip();
    public abstract void setZip(String zip);
    
    public abstract String getName();
    public abstract void setName(String name);
    
    public abstract String getAddressline1();
    public abstract void setAddressline1(String addressline1);
    
    public abstract String getAddressline2();
    public abstract void setAddressline2(String addressline2);
    
    public abstract String getCity();
    public abstract void setCity(String city);
    
    public abstract String getState();
    public abstract void setState(String state);
    
    public abstract String getPhone();
    public abstract void setPhone(String phone);
    
    public abstract String getFax();
    public abstract void setFax(String fax);
    
    public abstract String getEmail();
    public abstract void setEmail(String email);
    
    public abstract Integer getCreditLimit();
    public abstract void setCreditLimit(Integer creditLimit);
    
    public abstract Collection getOrdersBean();
    public abstract void setOrdersBean(Collection ordersBean);
    
    public abstract DiscountCodeLocal getDiscountCode();
    public abstract void setDiscountCode(DiscountCodeLocal discountCode);
    
    
    public Integer ejbCreate(Integer customerId, String zip, String name, String addressline1, String addressline2, String city, String state, String phone, String fax, String email, Integer creditLimit, DiscountCodeLocal discountCode)  throws CreateException {
        if (customerId == null) {
            throw new CreateException("The field \"customerId\" must not be null");
        }
        if (zip == null) {
            throw new CreateException("The field \"zip\" must not be null");
        }
        if (discountCode == null) {
            throw new CreateException("The field \"discountCode\" must not be null");
        }
        
        // TODO add additional validation code, throw CreateException if data is not valid
        setCustomerId(customerId);
        setZip(zip);
        setName(name);
        setAddressline1(addressline1);
        setAddressline2(addressline2);
        setCity(city);
        setState(state);
        setPhone(phone);
        setFax(fax);
        setEmail(email);
        setCreditLimit(creditLimit);
        
        return null;
    }
    
    public void ejbPostCreate(Integer customerId, String zip, String name, String addressline1, String addressline2, String city, String state, String phone, String fax, String email, Integer creditLimit, DiscountCodeLocal discountCode) {
        // TODO populate relationships here if appropriate
        setDiscountCode(discountCode);
        
    }
}
