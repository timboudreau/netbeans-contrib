package ent;

import javax.ejb.*;

/**
 * This is the bean class for the MicroMarketsBean enterprise bean.
 * Created 12.1.2006 17:52:53
 * @author jungi
 */
public abstract class MicroMarketsBean implements EntityBean, MicroMarketsLocalBusiness {
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
    
    
    public abstract String getZipCode();
    public abstract void setZipCode(String zipCode);
    
    public abstract Double getRadius();
    public abstract void setRadius(Double radius);
    
    public abstract Double getAreaLength();
    public abstract void setAreaLength(Double areaLength);
    
    public abstract Double getAreaWidth();
    public abstract void setAreaWidth(Double areaWidth);
    
    
    public String ejbCreate(String zipCode, Double radius, Double areaLength, Double areaWidth)  throws CreateException {
        if (zipCode == null) {
            throw new CreateException("The field \"zipCode\" must not be null");
        }
        
        // TODO add additional validation code, throw CreateException if data is not valid
        setZipCode(zipCode);
        setRadius(radius);
        setAreaLength(areaLength);
        setAreaWidth(areaWidth);
        
        return null;
    }
    
    public void ejbPostCreate(String zipCode, Double radius, Double areaLength, Double areaWidth) {
        // TODO populate relationships here if appropriate
        
    }
}
