/*
 * ToTest.java
 *
 * Created on August 11, 2006, 8:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package testapplication;

/**
 *
 * @author Tim Boudreau
 */
public class ToTest {
    
    /** Creates a new instance of ToTest */
    public ToTest() {
    }
    
    public String a() {
        return "a";
    }
    
    public String b() {
        return "b";
    }
    
    public String c() {
        return "c";
    }
    
    public int bad() {
        throw new IllegalArgumentException ("I am bad");
    }
    
}
