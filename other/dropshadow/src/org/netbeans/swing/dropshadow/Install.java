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
 * Created on 22. December 2003, 15:34
 */
package org.netbeans.swing.dropshadow;
import java.beans.PropertyEditorManager;
import org.openide.modules.ModuleInstall;

/** A simple moduleinstall class that registers/unregisters the drop-shadow
 * PopupFactory
 *
 * @author  Tim Boudreau  */
public class Install extends ModuleInstall {
    /** Creates a new instance of Install */
    public Install() {
    }
    
    public void restored() {
        NbPopupFactory.install();
    }
    
    public void uninstalled() {
        NbPopupFactory.uninstall();
    }
    
    /** Main method for testing (semi-dangerous) */
    public static void main(String args[]) {
        new Install().restored();
    }    
}
