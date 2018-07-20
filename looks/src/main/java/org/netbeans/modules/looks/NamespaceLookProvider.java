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

package org.netbeans.modules.looks;

import java.util.Enumeration;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;

/** Given name of context searches in the namespace for Looks/LookSelectors.
 * Subclasses may change the algorithm for finding the looks by overriding
 * the {@link #namesFor(java.lang.Object) namesFor( object )} method.
 * <P>
 * Default implementation is based on the inheritance hierarchy (including
 * interfaces) of the represented object's class.
 *
 * @author Petr Hrebejk, Jaroslav Tulach
 */
public interface NamespaceLookProvider {
            
    /** Defines the list of names that should be searched for a given 
     * represented object.
     * <P>
     * Subclasses are allowed to provide different implementation which can
     * base the naming based on for example public ID
     * for <code>org.w3c.dom.Document</code>.     
     * <P>
     * Default implementation of NamespaceLook uses the inheritance hierarchy 
     * of the represented object to locate the Looks/LookSelectors. I.e. this 
     * method returns Enumeration of names composed of the represented object's
     * class and it's superclasses and all interfaces (and superinterfaces) 
     * implemented by the represented object's class.
     * <P>
     * The {@link org.netbeans.spi.looks.Selectors#defaultTypes() Looks.defaultTypes()} and
     * {@link org.netbeans.spi.looks.Selectors#namespaceTypes(java.lang.String) Looks.namespaceTypes(String)}
     * use the this class.
     *
     * @param obj the represented object
     * @return enumeration of Strings
     * @see org.netbeans.spi.looks.Selectors#defaultTypes()
     * @see org.netbeans.spi.looks.Selectors#namespaceTypes(java.lang.String)
     * @since Made non abstract in version 0.2
     */
    public Enumeration getNamesForKey( Object obj );
    
    
    /** Returns key for given object. Make sure you return the same key for
     * given object during whole lifecycle of the LookSelector.
     * @param representedObject The represented object we want to find key for.
     * @return Key for given represented object. Returning <CODE>null</CODE>
     *         instead of a key will result in returning empty Enumeration 
     *         from the LookSelector.
     */
    public Object getKeyForObject( Object representedObject );
    
    
    public void addChangeListener( ChangeListener listener ) throws TooManyListenersException;
    
}
