package org.netbeans.modules.pmd.sample;

import org.openide.util.NbBundle;
/** Show bad use of NbBundle.getMessage that refers to another class
 *
 * @author  Radim Kubacki
 */
public class ForeignBundleUse {
    
    public ForeignBundleUse() {
        // bad
        org.openide.util.NbBundle.getMessage (StaticBundleUse.class, "jkjkj", new Object());
        NbBundle.getMessage (StaticBundleUse.class, "jkjkj", new Object());
        org.openide.util.NbBundle.getBundle (StaticBundleUse.class).getString( "jkjkj");
        NbBundle.getBundle (StaticBundleUse.class).getString( "jkjkj");
        
        // correct
        org.openide.util.NbBundle.getMessage (ForeignBundleUse.class, "jkjkj", new Object());
        NbBundle.getMessage (ForeignBundleUse.class, "jkjkj", new Object());
        org.openide.util.NbBundle.getBundle (ForeignBundleUse.class).getString( "jkjkj");
        NbBundle.getBundle (ForeignBundleUse.class).getString( "jkjkj");
        
    }
    
    private static class Inner {
        private void foo () {
            // bad
            org.openide.util.NbBundle.getMessage (StaticBundleUse.class, "jkjkj", new Object());
            NbBundle.getMessage (StaticBundleUse.class, "jkjkj", new Object());
            org.openide.util.NbBundle.getBundle (StaticBundleUse.class).getString( "jkjkj");
            NbBundle.getBundle (StaticBundleUse.class).getString( "jkjkj");

            // correct
            NbBundle.getMessage (ForeignBundleUse.class, "jkjkj", new Object());
            NbBundle.getMessage (Inner.class, "jkjkj", new Object());
            NbBundle.getMessage (ForeignBundleUse.Inner.class, "jkjkj", new Object());
        }
    }
}
