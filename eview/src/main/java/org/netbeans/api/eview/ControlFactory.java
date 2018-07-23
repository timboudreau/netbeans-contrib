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
 * Software is Nokia. Portions Copyright 2005 Nokia.
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
package org.netbeans.api.eview;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;

/**
 * Instances implementing this interface should be returned from the
 * layer as widget
 * factories for forms created by ExtensibleViews. We need factory and
 * not simple instances because there might be more forms (instances)
 * created from the same configuration object (from the object returned
 * from the layer).
 *
 * @author David Strupl
 */
public interface ControlFactory {

    /**
     * Extracts the value from a given component. The component was created
 * via <code>createComponent</code> method. If the component was not
     * created by this factory an exception (IllegalArgumentException) 
     * can be thrown from this method.
     */
    public Object getValue(JComponent c);
    
    /**
     * If the GUI needs a text representation of the value from
     * given component this method is called. The value parameter
     * is one of the values displayed by the given component.
     */
    public String convertValueToString(JComponent c, Object value);

    /**
     * Sets a value to a given control component. The component was created
     * via <code>createComponent</code> method. If the component was not
     * created by this factory an exception (IllegalArgumentException) 
     * can be thrown from this method.
     */
    public void setValue(JComponent c, Object value);
    
    /**
     * Attaches a PropertyChangeListener for listening on changes in the
     * given component. The component was created
     * via <code>createComponent</code> method. If the component was not
     * created by this factory an exception (IllegalArgumentException) 
     * can be thrown from this method.
     */
    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l);
    
    /**
     * Removes a PropertyChangeListener from the
     * given component. The component was created
     * via <code>createComponent</code> method. If the component was not
     * created by this factory an exception (IllegalArgumentException) 
     * can be thrown from this method.
     */
    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l);
  
    /**
     * This method will be called by the infrastructure to create the widget.
     * The factory can keep track of the components created by it in order
     * to properly implement the other methods of this interface.
     */
    public JComponent createComponent();
    
}
