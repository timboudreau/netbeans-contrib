package ent;

import java.math.BigDecimal;
import java.sql.Date;
import javax.ejb.*;

/**
 * This is the bean class for the OrdersBean enterprise bean.
 * Created 12.1.2006 17:52:53
 * @author jungi
 */
public abstract class OrdersBean implements EntityBean, OrdersLocalBusiness {
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
    
    
    public abstract Integer getOrderNum();
    public abstract void setOrderNum(Integer orderNum);
    
    public abstract Integer getQuantity();
    public abstract void setQuantity(Integer quantity);
    
    public abstract BigDecimal getShippingCost();
    public abstract void setShippingCost(BigDecimal shippingCost);
    
    public abstract Date getSalesDate();
    public abstract void setSalesDate(Date salesDate);
    
    public abstract Date getShippingDate();
    public abstract void setShippingDate(Date shippingDate);
    
    public abstract String getFreightCompany();
    public abstract void setFreightCompany(String freightCompany);
    
    public abstract ent.CustomerLocal getCustomerId();
    public abstract void setCustomerId(ent.CustomerLocal customerId);
    
    public abstract ProductLocal getProductId();
    public abstract void setProductId(ProductLocal productId);
    
    
    public Integer ejbCreate(Integer orderNum, Integer quantity, BigDecimal shippingCost, Date salesDate, Date shippingDate, String freightCompany, ent.CustomerLocal customerId, ProductLocal productId)  throws CreateException {
        if (orderNum == null) {
            throw new CreateException("The field \"orderNum\" must not be null");
        }
        if (customerId == null) {
            throw new CreateException("The field \"customerId\" must not be null");
        }
        if (productId == null) {
            throw new CreateException("The field \"productId\" must not be null");
        }
        
        // TODO add additional validation code, throw CreateException if data is not valid
        setOrderNum(orderNum);
        setQuantity(quantity);
        setShippingCost(shippingCost);
        setSalesDate(salesDate);
        setShippingDate(shippingDate);
        setFreightCompany(freightCompany);
        
        return null;
    }
    
    public void ejbPostCreate(Integer orderNum, Integer quantity, BigDecimal shippingCost, Date salesDate, Date shippingDate, String freightCompany, ent.CustomerLocal customerId, ProductLocal productId) {
        // TODO populate relationships here if appropriate
        setCustomerId(customerId);
        setProductId(productId);
        
    }
}
