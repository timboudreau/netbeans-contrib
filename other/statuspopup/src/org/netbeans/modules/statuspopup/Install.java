/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Install.java
 *
 * Created on 1. December 2003, 15:34
 */
package org.netbeans.modules.statuspopup;
import java.beans.PropertyEditorManager;
import org.openide.modules.ModuleInstall;
/** A simple moduleinstall class that registers/unregister the color editor
 *
 * @author  Tim Boudreau
 */
public class Install extends ModuleInstall {
    /** Creates a new instance of Install */
    public Install() {
    }
    
    public void restored() {
        Object o = PopupStatusDisplayer.getInstance();
    }
    
    public void uninstalled() {
        if (PopupStatusDisplayer.instance != null) {
            PopupStatusDisplayer.instance.detachFromMainWindow();
        }
    }
}
