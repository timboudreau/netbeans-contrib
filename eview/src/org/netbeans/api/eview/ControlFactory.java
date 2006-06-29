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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
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
