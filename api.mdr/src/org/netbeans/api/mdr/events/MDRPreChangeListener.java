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
package org.netbeans.api.mdr.events;

import java.util.EventListener;

/** Listener interface containing methods that allow clients to keep track of planned
 * changes in the metadata before they are performed. Both methods defined in this
 * interface should be called synchronously. It is forbidden to use these methods
 * to make changes in the repository - code in these methods should have only
 * read access to the MDR. Implementors of these methods should keep in mind that the
 * longer the code in their implementation of these methods performs, the longer the whole
 * repository is blocked by the source operation that fired these events.<p>
 * It is not guaranteed that operations of this listener interface will see the intermediate 
 * state of any complex operation (like addAll). This behavior is implementation specific 
 * so the pre-change listeners should not rely on it.
 * <p>Note: Adding a listener to any of the MDR event sources is not considered as a write
 * operation.
 *
 * @author Martin Matula
 */
public interface MDRPreChangeListener extends MDRChangeListener {
    /** This method gets called when a repository change is planned to occur.
     * Any operation that performs a change in MDR has to fire this notification
     * synchronously on each registered pre-change listener before the change is performed.<p>
     * Any run-time exception thrown by the implementation of this method should
     * not affect the events dispatching (i.e. it should be ignored by the event source).
     * @param e Object describing the planned change.
     */    
    public void plannedChange(MDRChangeEvent e) throws VetoChangeException;
    
    /** This method gets called if a planned change (which was already announced
     * by calling {@link #plannedChange} was cancelled (e.g. the operation that was
     * going to perform the change failed). This method is called synchronously by
     * the operation that tried to perform the change.<p>
     * Any run-time exception thrown by the implementation of this method should
     * not affect the events dispatching (i.e. it should be ignored by the event source).
     * @param e Object describing the cancelled change (has to be the same instance
     * as passed to the {@link #plannedChange} method).
     */
    public void changeCancelled(MDRChangeEvent e);
}
