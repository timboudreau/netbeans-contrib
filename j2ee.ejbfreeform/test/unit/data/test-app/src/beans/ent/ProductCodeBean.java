package ent;

import java.util.Collection;
import javax.ejb.*;

/**
 * This is the bean class for the ProductCodeBean enterprise bean.
 * Created 12.1.2006 17:52:53
 * @author jungi
 */
public abstract class ProductCodeBean implements EntityBean, ProductCodeLocalBusiness {
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
    
    
    public abstract String getProdCode();
    public abstract void setProdCode(String prodCode);
    
    public abstract String getDiscountCode();
    public abstract void setDiscountCode(String discountCode);
    
    public abstract String getDescription();
    public abstract void setDescription(String description);
    
    public abstract Collection getProductBean();
    public abstract void setProductBean(Collection productBean);
    
    
    public String ejbCreate(String prodCode, String discountCode, String description)  throws CreateException {
        if (prodCode == null) {
            throw new CreateException("The field \"prodCode\" must not be null");
        }
        if (discountCode == null) {
            throw new CreateException("The field \"discountCode\" must not be null");
        }
        
        // TODO add additional validation code, throw CreateException if data is not valid
        setProdCode(prodCode);
        setDiscountCode(discountCode);
        setDescription(description);
        
        return null;
    }
    
    public void ejbPostCreate(String prodCode, String discountCode, String description) {
        // TODO populate relationships here if appropriate
        
    }
}
