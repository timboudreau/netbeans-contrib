/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.providers;

import org.netbeans.modules.cnd.profiler.data.Function;

/**
 *
 * @author eu155513
 */
public class TestProvider implements FunctionsProvider {
    private static TestProvider instance = null;

    public static TestProvider getInstance() {
        if (instance == null) {
            instance = new TestProvider();
        }
        return instance;
    }
    
    public Function[] getFunctions() {
        Function[] res = new Function[4];
        Function f1 = new Function("main");
        Function f2 = new Function("foo1");
        Function f3 = new Function("foo2");
        Function f4 = new Function("foo3");
        
        // Attribs
       f1.setProperty("secs", 182.4);
       f2.setProperty("secs", 54.1);
       f3.setProperty("secs", 33.7);
       f4.setProperty("secs", 18.33);
       
       f1.setProperty("self", 100.3);
       f2.setProperty("self", 34.0);
       f3.setProperty("self", 0.1);
       f4.setProperty("self", 18.33);
        
        // Call information
        f1.addCallee(f2);
        f1.addCallee(f3);
        f2.addCallee(f4);
        
        res[0] = f1;
        res[1] = f2;
        res[2] = f3;
        res[3] = f4;
        return res;
    }
}
