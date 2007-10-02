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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
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

package org.netbeans.api.enode;

import org.openide.util.Lookup;

import org.netbeans.modules.enode.ExtensibleLookupImpl;

/**
 * Special lookup capable of reading its content from the system
 * file system. The lookup is bound to an instance of ExtensibleNode.
 * @author David Strupl
 */
public class ExtensibleLookup extends Lookup {

    /**
     * As the implementation is "hidden" from the API this
     * is a reference to an object with real implementation.
     */
    private ExtensibleLookupImpl impl;

    /**
     * Node to which this lookup is bound.
     */
    private ExtensibleNode myNode;
    
    /**
     * This public constructor needs an ExtensibleNode as paramater.
     * The path is to the objects found by this lookup is computed from
     * the result of calling method ExtensibleNode.getPath() on the
     * associated node.
     */
    public ExtensibleLookup(ExtensibleNode en) {
        myNode = en;
    }
    
    /**
     * The default constructor - you have to call setNode to pass ExtensibleNode
     * instance for the lookup to know from where to take the objects.
     */
    public ExtensibleLookup() {
    }
    
    /**
     * Associates the lookup object with the node.
     */
    public void setNode(ExtensibleNode en) {
        myNode = en;
        if (impl != null) {
            impl.setExtensibleNode(myNode);
        }
    }
    
    /**
     *
     */
    public Object lookup(Class clazz) {
        initializeImpl();
        return impl.lookup(clazz);
    }
    
    /**
     *
     */
    public Result lookup(Template template) {
        initializeImpl();
        return impl.lookup(template);
    }
    
    /**
     * Lazy initialization of the implementation reference.
     */
    private void initializeImpl() {
        if (impl == null) {
            impl = new ExtensibleLookupImpl();
            impl.setExtensibleNode(myNode);
        }
    }
    
}
