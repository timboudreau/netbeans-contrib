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
public class TestProvider {
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
       f1.setAttrib("time", 182.4);
       f2.setAttrib("time", 54.1);
       f3.setAttrib("time", 33.7);
       f4.setAttrib("time", 18.33);
       
       f1.setAttrib("Self time", 100.3);
       f2.setAttrib("Self time", 34.0);
       f3.setAttrib("Self time", 0.1);
       f4.setAttrib("Self time", 18.33);
        
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
