
package ent;


/**
 * This is the business interface for MicroMarkets enterprise bean.
 */
public interface MicroMarketsLocalBusiness {
    String getZipCode();

    Double getRadius();

    void setRadius(Double radius);

    Double getAreaLength();

    void setAreaLength(Double areaLength);

    Double getAreaWidth();

    void setAreaWidth(Double areaWidth);
    
}
