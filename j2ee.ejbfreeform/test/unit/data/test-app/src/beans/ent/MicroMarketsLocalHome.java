
package ent;

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;


/**
 * This is the local-home interface for MicroMarkets enterprise bean.
 */
public interface MicroMarketsLocalHome extends EJBLocalHome {
    
    MicroMarketsLocal findByPrimaryKey(String key)  throws FinderException;

    public MicroMarketsLocal create(String zipCode, Double radius, Double areaLength, Double areaWidth) throws CreateException;

    Collection findByZipCode(String zipCode) throws FinderException;

    Collection findByRadius(Double radius) throws FinderException;

    Collection findByAreaLength(Double areaLength) throws FinderException;

    Collection findByAreaWidth(Double areaWidth) throws FinderException;
    
    
}
