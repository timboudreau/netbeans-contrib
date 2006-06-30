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
package org.netbeans.modules.mdr;

import java.lang.reflect.*;
import java.util.*;

import org.netbeans.api.mdr.*;
import org.netbeans.mdr.util.Logger;

/**
 *
 * @author  mmatula
 * @version
 */
public class MDRDescriptor extends Object {

    private static final HashMap repositories = new HashMap();

    private final String className;
    private final Map parameters;

    /** Creates new MDRDescriptor */
    public MDRDescriptor(String className, Map parameters) {
        this.className = className;
        this.parameters = parameters;
    }
    
    public synchronized MDRepository getMDRInstance() {
        MDRepository result = (MDRepository) repositories.get(this);
        
        if (result == null) {
            try {
                Class repClass = Class.forName(className);
                try {
                    Constructor c = repClass.getConstructor(new Class[] {Map.class});
                    result = (MDRepository) c.newInstance(new Object[] {parameters});
                } catch (NoSuchMethodException e) {
                    result = (MDRepository) repClass.newInstance();
                }
                repositories.put(this, result);
            } catch (Exception e) {
                // [PENDING] exception should be trown here
                Logger.getDefault().notify(Logger.INFORMATIONAL, e);
                return null;
            }
        }
        
        return result;
    }
    
    public boolean equals(Object o) {
        return (o instanceof MDRDescriptor) && parameters.equals(((MDRDescriptor) o).parameters) && className.equals(((MDRDescriptor) o).className);
    }
    
    public int hashCode() {
        return parameters.hashCode();
    }
}
