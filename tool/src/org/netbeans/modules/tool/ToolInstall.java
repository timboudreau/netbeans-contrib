/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.tool;

import org.openide.modules.ModuleInstall;

/**
 * This class is referenced from the module's manifest
 * via OpenIDE-Module-Install: section.
 * It performs the for now rather slow initialization of the
 * main toolbar during the startup sequence.
 *
 * @author David Strupl
 */
public class ToolInstall extends ModuleInstall {
    
    /** Creates a new instance of NetActInstall */
    public ToolInstall() {
    }
    
    /**
     * Overriden to save unsaved data.
     */
    public boolean closing () {
        return ExitDialog.showDialog();
    }
}
