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

package org.netbeans.modules.jndi;


import java.lang.reflect.InvocationTargetException;
import javax.naming.Context;
import org.openide.nodes.PropertySupport;


/** This class represents a property for ProviderNode
 */
public class ProviderProperty extends PropertySupport {

    /** data holder */
    private ProviderProperties repository;

    /** Creates new ProviderProperty
     *  @param String name of property (hash key of property in Properties)
     *  @param Class type
     *  @param String name to be displayed
     *  @param String short description
     *  @param Object repository (should be java.util.Properties)
     *  @param boollean writeable, can be changed
     */
    public ProviderProperty (String name, Class type, String displayName, String shortDescription, Object repository, boolean writable) {
        super (name, type, displayName,  shortDescription, true, writable);
        this.repository = (ProviderProperties) repository;
    }

    /** Returns value of the property
     *  @return Object value of property
     *  @exception IllegalAccessException, IllegalArgumentException, InvocationTargetException
     */
    public Object getValue () throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String key = getName();
        return this.repository.getProperty(key);
    }

    /** Sets value of the property
     *  @param Object value of property
     *  @exception IllegalAccessException, IllegalArgumentException, InvocationTargetException
     */
    public void setValue (Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String key = getName();
        this.repository.setProperty (key, value);
    }

}
