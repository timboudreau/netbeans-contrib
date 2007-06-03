/*
 * BooleanWrapperCancellable.java
 *
 * Created on February 21, 2007, 4:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.diskcleanuptool;

import org.openide.util.Cancellable;

/**
 * Class used to tie Cancellable into the process of deleting a directory disk and memory
 * efficiently.
 * @author Wade Chandler
 * @version 1.0
 */
public class BooleanWrapperCancellable implements Cancellable {
    
    DiskUtilities.BooleanWrapper wrapper = null;
    
    /** Creates a new instance of BooleanWrapperCancellable */
    public BooleanWrapperCancellable(DiskUtilities.BooleanWrapper wrapper) {
        this.wrapper = wrapper;
    }
    
    public boolean cancel() {
        wrapper.setValue(true);
        return wrapper.getValue();
    }
    
}
