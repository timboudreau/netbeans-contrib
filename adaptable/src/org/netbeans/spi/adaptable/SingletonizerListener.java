/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.adaptable;

import java.util.EventListener;
import javax.swing.event.ChangeEvent;

/** Listener usually implemented by the infrastructure
 * and attached to a {@link Singletonizer} instances that allows them
 * to notify changes to the framework.
 *
 * @author Jaroslav Tulach
 */
public interface SingletonizerListener extends EventListener {
    public void stateChanged(SingletonizerEvent e);
}
