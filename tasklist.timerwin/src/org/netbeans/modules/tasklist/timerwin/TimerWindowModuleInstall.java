package org.netbeans.modules.tasklist.timerwin;

import org.openide.modules.ModuleInstall;

/**
 *
 * @author tl
 */
public class TimerWindowModuleInstall extends ModuleInstall {
    /** 
     * Creates a new instance of TimerWindowModuleInstall 
     */
    public TimerWindowModuleInstall() {
    }

    public boolean closing() {
        // write settings
    }

    public void restored() {
        // read settings
    }

    public void installed() {
        restored();
    }    
}
