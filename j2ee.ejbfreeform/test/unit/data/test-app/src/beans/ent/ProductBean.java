package ent;

import java.math.BigDecimal;
import java.util.Collection;
import javax.ejb.*;

/**
 * This is the bean class for the ProductBean enterprise bean.
 * Created 12.1.2006 17:52:53
 * @author jungi
 */
public abstract class ProductBean implements EntityBean, ProductLocalBusiness {
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
    
    
    public abstract Integer getProductId();
    public abstract void setProductId(Integer productId);
    
    public abstract BigDecimal getPurchaseCost();
    public abstract void setPurchaseCost(BigDecimal purchaseCost);
    
    public abstract Integer getQuantityOnHand();
    public abstract void setQuantityOnHand(Integer quantityOnHand);
    
    public abstract BigDecimal getMarkup();
    public abstract void setMarkup(BigDecimal markup);
    
    public abstract String getAvailable();
    public abstract void setAvailable(String available);
    
    public abstract String getDescription();
    public abstract void setDescription(String description);
    
    public abstract ent.ManufactureLocal getManufactureId();
    public abstract void setManufactureId(ent.ManufactureLocal manufactureId);
    
    public abstract ent.ProductCodeLocal getProductCode();
    public abstract void setProductCode(ent.ProductCodeLocal productCode);
    
    public abstract Collection getOrdersBean();
    public abstract void setOrdersBean(Collection ordersBean);
    
    
    public Integer ejbCreate(Integer productId, BigDecimal purchaseCost, Integer quantityOnHand, BigDecimal markup, String available, String description, ent.ManufactureLocal manufactureId, ent.ProductCodeLocal productCode)  throws CreateException {
        if (productId == null) {
            throw new CreateException("The field \"productId\" must not be null");
        }
        if (manufactureId == null) {
            throw new CreateException("The field \"manufactureId\" must not be null");
        }
        if (productCode == null) {
            throw new CreateException("The field \"productCode\" must not be null");
        }
        
        // TODO add additional validation code, throw CreateException if data is not valid
        setProductId(productId);
        setPurchaseCost(purchaseCost);
        setQuantityOnHand(quantityOnHand);
        setMarkup(markup);
        setAvailable(available);
        setDescription(description);
        
        return null;
    }
    
    public void ejbPostCreate(Integer productId, BigDecimal purchaseCost, Integer quantityOnHand, BigDecimal markup, String available, String description, ent.ManufactureLocal manufactureId, ent.ProductCodeLocal productCode) {
        // TODO populate relationships here if appropriate
        setManufactureId(manufactureId);
        setProductCode(productCode);
        
    }
}
