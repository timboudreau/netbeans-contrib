package org.netbeans.modules.tasklist.timerwin;

import java.awt.Point;
import java.awt.Toolkit;
import java.util.prefs.Preferences;
import org.openide.modules.ModuleInstall;

/**
 * Reads settings.
 *
 * @author tl
 */
public class TimerWindowModuleInstall extends ModuleInstall {
    /** position of the window */
    public static Point WINDOW_POSITION = new Point();
    
    /** 
     * Creates a new instance of TimerWindowModuleInstall 
     */
    public TimerWindowModuleInstall() {
    }

    public void restored() {
        java.util.prefs.Preferences p = 
                Preferences.userNodeForPackage(TimerWindowModuleInstall.class);
        WINDOW_POSITION.x = p.getInt(
                "x", Toolkit.getDefaultToolkit().getScreenSize().width - 300);
        WINDOW_POSITION.y = p.getInt("y", 0);
    }
    
    /**
     * Saves position of the window.
     */
    public static void writeSettings() {
        java.util.prefs.Preferences p = 
                Preferences.userNodeForPackage(TimerWindowModuleInstall.class);
        p.putInt("x", WINDOW_POSITION.x);
        p.putInt("y", WINDOW_POSITION.y);
    }
}
