/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.runtime;

import org.netbeans.modules.vcscore.commands.CommandTaskInfo;

/**
 * Interface, that if implemented by CommandTask, is asked for a RuntimeCommand
 * representation of this task.
 *
 * @author  Martin Entlicher
 */
public interface RuntimeCommandTask {
    
    public RuntimeCommand getRuntimeCommand(CommandTaskInfo info);
    
}
