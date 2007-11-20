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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package threadmanagement;

/**
 * <p>This class models a priority level for incoming request threads and is 
 * used by the <code>ThreadSynchronizer</code> class.</p>
 * @author mbohm
 */
public class ThreadPriority {
    
    /**
     * <p>A name to identify this priority level.</p>
     */
    private String name;
    
    /**
     * <p>If <code>true</code>, request threads of this priority level must execute in 
     * the order they are received; otherwise, they may execute in any order.</p>
     */
    private boolean serviceInOrderReceived;
    
    /**
     * <p>Construct a <code>ThreadPriority</code> instance.</p>
     * @param name A name to identify this priority level.
     * @param serviceInOrderReceived If <code>true</code>, request threads of this priority level must execute in 
     * the order they are received; otherwise, they may execute in any order.
     */
    public ThreadPriority(String name, boolean serviceInOrderReceived) {
        this.name = name;
        this.serviceInOrderReceived = serviceInOrderReceived;
    }
    
    /**
     * <p>Get a name to identify this priority level.</p>
     * @return A name to identify this priority level.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * <p>Get whether request threads of this priority level must execute in 
     * the order they are received as opposed to executing in any order.</p>
     * @return <code>true</code> if request threads of this priority level must execute in 
     * the order they are received, <code>false</code> otherwise.
     */
    public boolean isServiceInOrderReceived() {
        return this.serviceInOrderReceived;
    }
    
    /**
     * <p>Get a <code>String</code> instance that identifies the values of the 
     * <code>name</code> and <code>serviceInOrderReceived</code> properties.</p>
     * @return A <code>String</code> instance that identifies the values of the 
     * <code>name</code> and <code>serviceInOrderReceived</code> properties.
     */
    public String toString() {
        return "[ThreadPriority:name=" + this.name + ",serviceInOrderReceived=" + this.serviceInOrderReceived + "]";
    }
    
}
