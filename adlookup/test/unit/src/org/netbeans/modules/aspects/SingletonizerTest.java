/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.aspects;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.aspects.*;
import org.openide.util.Lookup;

/** Tests Singletonizer behaviour.`
 *
 * @author Jaroslav Tulach
 */
public class SingletonizerTest extends org.netbeans.junit.NbTestCase {
    public SingletonizerTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new org.netbeans.junit.NbTestSuite(SingletonizerTest.class);
        
        return suite;
    }
    
    protected void setUp () throws Exception {
        super.setUp ();
    }

    public void testProvidesImplementationOfRunnable () {
        Class[] supported = { Runnable.class };
        
        class RunImpl implements Singletonizer.Impl {
            public boolean isEnabled = true;
            public int cnt;
            public Object representedObject;
            public java.lang.reflect.Method method;
            
            public boolean isEnabled (Class c) {
                return isEnabled;
            }
            
            public Object invoke (Object obj, java.lang.reflect.Method method) {
                this.cnt++;
                this.representedObject = obj;
                this.method = method;
                return null;
            }
        }
        
        RunImpl runImpl = new RunImpl ();
        AspectProvider provider = Singletonizer.create (supported, runImpl);
        Object representedObject = "sampleRO";
        Lookup lookup = Aspects.getLookup(representedObject, provider);
        
        assertNotNull ("Lookup created", lookup);
        
        Runnable r = (Runnable)lookup.lookup(Runnable.class);
        assertNotNull ("Runnable provided", r);
        r.run ();
        
        assertEquals ("One call to invoke method", 1, runImpl.cnt);
        assertEquals ("Called on RO", representedObject, runImpl.representedObject);
        assertNotNull ("Method provided", runImpl.method);
        assertEquals ("Method of the interface", Runnable.class, runImpl.method.getDeclaringClass ());
        assertEquals ("Method name is run", "run", runImpl.method.getName ());
        
    }
  
}
