package org.netbeans.modules.pmd.sample;

/** Show bad use of NbBundle.getMessage that refers to another class
 *
 * @author  Radim Kubacki
 */
public class InnerClassWoConstructor {
    
    public InnerClassWoConstructor() {
        new BadOne ();
        new BadTwo ();
    }
    
    private static class BadOne {
        private void foo () {
        }

    }
    
    private class BadTwo {
        public void bar () {
        }
    }
}
