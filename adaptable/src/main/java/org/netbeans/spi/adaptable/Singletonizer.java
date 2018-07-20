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

package org.netbeans.spi.adaptable;

import java.util.TooManyListenersException;

/** Allows implementation of adaptors based on singletonizer pattern.
 * @see Adaptors
 */
public interface Singletonizer {
    /** Checks whether the class is right now supported - e.g. whether
     * its methods should be served.
     *
     * @param obj the represented object that the query applies to
     * @param c the class its method is being called
     * @return true if call is allowed, false if not
     */
    public boolean isEnabled (Object obj, Class c);

    /** Invokes a method of given class on provided represented object.
     * @param obj the represented object that is making the call
     * @param method method that was called on that object
     * @param args the arguments to the method
     */
    public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) throws Throwable;

    /** Allows the infrastructure to register a change listener on this
     * singletonizer. Whenever the set of supported classes changes,
     * the singletonizer is supposed to call the listener to notify all
     * Adaptables backed by this Singletonizer to update its state.
     * <p>
     * Usual implementation is:
     * <pre>
     * class MySingletonizer implements Singletonizer {
     *     private SingletonizerListener listener;
     *
     *     public synchronized void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException {
     *         if (this.listener != null) {
     *             throw new TooManyListenersException ();
     *         }
     *         this.listener = listener;
     *     }
     *     public synchronized void removeSingletonizerListener (SingletonizerListener listener) {
     *         if (this.listener == listener) {
     *             this.listener = null;
     *         }
     *     }
     * }
     * </pre>
     *
     * @param listener the listener to attache
     * @exception TooManyListenersException if there already is listener (no need to support more than one)
     */
    public void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException;
    
    /** Removes registered listener.
     * @see #addSingletonizerListener
     */
    public void removeSingletonizerListener (SingletonizerListener listener);
}
