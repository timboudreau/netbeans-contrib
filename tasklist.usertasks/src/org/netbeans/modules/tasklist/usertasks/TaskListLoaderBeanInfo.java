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

package org.netbeans.modules.tasklist.usertasks;

import java.awt.Image;
import java.beans.SimpleBeanInfo;

import org.openide.util.Utilities;

/** 
 * BeanInfo for TaskList object loader.
 *
 * @author Tim Lebedkov
 */
public final class TaskListLoaderBeanInfo extends SimpleBeanInfo {
    public Image getIcon(int type) {
        return Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/tasklistfile.gif"); // NOI18N
    }
}
