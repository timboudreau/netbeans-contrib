/*
 * IndeterminateProgressHandleImpl.java
 *
 * Created on February 27, 2007, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.diskcleanuptool;

import org.netbeans.api.progress.ProgressHandle;

/**
 * Class used to help with showing progress.
 * @author Wade Chandler
 */
public class IndeterminateProgressHandleImpl
        implements DiskUtilities.IndeterminateProgressHandle {
    ProgressHandle ph = null;
    /** Creates a new instance of IndeterminateProgressHandleImpl */
    public IndeterminateProgressHandleImpl(ProgressHandle ph) {
        this.ph = ph;
    }
    
    public void updateStatus(String s) {
        ph.progress(s);
    }
    
    public void start() {
        ph.start();
        ph.switchToIndeterminate();
        ph.setInitialDelay(1);
    }
    
    public void finish() {
        ph.finish();
    }
    
}