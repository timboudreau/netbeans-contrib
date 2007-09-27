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

package org.netbeans.modules.jndi;

import javax.naming.Binding;

/**
 * This class represents the key for Children.Keys used by this module
 * @author  tzezula
 * @version 1.0
 * @see JndiChildren
 */
public final class JndiKey extends Object {

    /* Failed this node while listing*/
    public boolean failed;
    /* The name class pair*/
    public Binding name;


    /** Constructor used for Keys representing remote objects
     *  @param NameClassPair name, name and class of remote object
     */
    public JndiKey (Binding name) {
        this.name = name;
        this.failed = false;
    }

    /** Constructor used for Keys representing remote objects
     *  @param NameClassPair name, name and class of remote object
     *  @param boolean failed, if the node is failed
     */
    public JndiKey (Binding name, boolean failed){
        this.name = name;
        this.failed = failed;
    }

    /** Comparator
     *  @param Object obj, object to compare with
     *  @return boolean, true if equals
     */
    public boolean equals(Object obj){
        if (! (obj instanceof JndiKey)){
            return false;
        }
        JndiKey key = (JndiKey) obj;
        if (!this.name.getName().equals(key.name.getName()))
            return false;
        else if (this.name.getClassName() == null) {
            return key.name.getClassName() == null;
        }
        else return this.name.getClassName().equals(key.name.getClassName());
    }

    /** Hash code of object
     *  @return int hash code of object
     */
    public int hashCode(){
        return this.name.getName().hashCode();
    }

    /** Returns the name of key
     *  return String name
     */
    public String toString () {
        return name.toString();
    }

}
