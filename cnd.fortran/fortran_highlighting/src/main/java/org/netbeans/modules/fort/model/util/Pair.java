
package org.netbeans.modules.fort.model.util;

/**
 * utility class for Pair
 * @author Andrey Gubichev
 */
public class Pair<T1, T2> {
    private T1 p1; 
    private T2 p2; 
    /**
     * creates a instance of Pair
     */
    public Pair(T1 p1, T2 p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
    /**
     * @return first element
     */
    public T1 getFirst() {
        return p1;
    }
    /**
     * @return second element
     */    
    public T2 getSecond() {
        return p2;
    }    
     /**
     * @return pair
     */   
    public static <T1, T2> Pair<T1, T2> getPair(T1 p1, T2 p2) {
        return new Pair<T1,T2>(p1, p2);
    }
}
