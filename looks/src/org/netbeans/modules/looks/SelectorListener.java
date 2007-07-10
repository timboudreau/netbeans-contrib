/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.looks;

import java.util.EventListener;

/** Listener for changes in LookSelectors context.
 *
 * @see org.netbeans.spi.looks.LookSelector
 * @see org.netbeans.spi.looks.SelectorEvent
 * @author  Petr Hrebejk, Jaroslav Tulach
 */
public interface SelectorListener extends EventListener {

    /** Called when the content of the LookSelector changes. The event passed
     * to this method as parameter allows for deciding about which nodes
     * (represented objects) are affected by the change.
     * @param event Event describing the change.
     */
    public void contentsChanged( SelectorEvent event );
        
}
