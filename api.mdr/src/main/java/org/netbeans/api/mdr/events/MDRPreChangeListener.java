/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
