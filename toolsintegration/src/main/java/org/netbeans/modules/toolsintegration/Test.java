/*
 * Test.java
 *
 * Created on 3. leden 2006, 18:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.toolsintegration;

/**
 *
 * @author Owner
 */
public class Test {
    
    /**
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        System.out.println("working dir: " + System.getProperty ("user.dir"));
        int i, k = args.length;
        for (i = 0; i < k; i++)
            System.out.println("param " + args [i]);
    }
    
}
