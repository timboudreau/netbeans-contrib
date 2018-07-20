/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.providers;

import org.netbeans.modules.cnd.profiler.data.Call;
import org.netbeans.modules.cnd.profiler.data.Function;
import org.netbeans.modules.cnd.profiler.views.Column;

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
       f1.setProperty(Column.TIME_NAME, 182.4);
       f2.setProperty(Column.TIME_NAME, 54.1);
       f3.setProperty(Column.TIME_NAME, 33.7);
       f4.setProperty(Column.TIME_NAME, 18.33);
       
       f1.setProperty(Column.SELF_NAME, 100.3);
       f2.setProperty(Column.SELF_NAME, 34.0);
       f3.setProperty(Column.SELF_NAME, 0.1);
       f4.setProperty(Column.SELF_NAME, 18.33);
        
        // Call information
        f1.addCallee(new Call(f2));
        f1.addCallee(new Call(f3));
        f2.addCallee(new Call(f4));
        
        f2.addCaller(new Call(f1));
        f3.addCaller(new Call(f1));
        f4.addCaller(new Call(f2));
        
        res[0] = f1;
        res[1] = f2;
        res[2] = f3;
        res[3] = f4;
        return res;
    }
}
