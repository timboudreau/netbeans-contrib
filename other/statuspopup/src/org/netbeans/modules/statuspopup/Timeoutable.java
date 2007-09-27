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

/*
 * Clearable.java
 *
 * Created on September 23, 2000, 6:00 PM
 */

package org.netbeans.modules.statuspopup;

/** Classes implementing this interface can register themselves with
 * TimeoutRegistry, and have their doTimeout() methods called
 * at an interval they specify.
 * <P>
 * There is an explicit contract between Timeoutable and TimeoutRegistry
 * that, when initiating an action the results of which will be timed out
 * (such as showing a component that you want to be hidden on a timeout),
 * that at that time you will call TimeoutRegistry.confirmActive().
 * <P>
 * The reason for this is that, for performance and memory, the timer
 * thread expires if it is unused for a period of time.  ConfirmActive
 * restarts the thread if necessary.
 * <P>
 * To register a class with TimeoutRegistry, call the static 
 * TimeoutRegistry.registerObject (myTimeoutableInstance).  There is
 * an unregister operation as well - however, TimeoutRegistry stores
 * a weak reference to the object, so it is not imperative that you
 * unregister your object for it to be garbage collected.
 *<P>
 * This is not meant to be a general purpose timer architecture, but a 
 * lightweight, low overhead one for non-critical purposes.
 *
 * @author Tim Boudreau
 * @version 0.2
 */
interface Timeoutable {
/** Returns the current timeout value (in milliseconds) for this instance.
 * Following registration with TimeoutRegistry or a the last call to getTimeout,
 * doTimeout() will be called again after <code>timeout</code> milliseconds have
 * elapsed.
 * @return The timeout in milliseconds
 */    
    public long getTimeout();
/** Returns an arbitrary object.  If that object and the stored object from
 * the last call to <code>getTimerPollArg()</code> are equal, the timeout
 * continues; if they are non-equal, the countdown is reset
 * (to the new value returned by getTimeout()).  So the countdown is reset
 * whenever the argument returned changes.
 * <P>
 * If the return value is null
 * @return the object to be compared with the previous call's result
 */    
    public Object getTimerPollArg();
    
    
/** Called when the timeout has been reached, after the return value from
 * getTimerPollArg() has changed.
 */    
    public void doTimeout();
    
}

