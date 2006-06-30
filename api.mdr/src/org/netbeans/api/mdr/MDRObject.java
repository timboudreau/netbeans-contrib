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
package org.netbeans.api.mdr;

import org.netbeans.api.mdr.events.MDRChangeSource;
import javax.jmi.reflect.RefBaseObject;

/** Interface implemented by each repository object (besides the standard JMI interfaces).
 * Adds a method for navigating to the parent repository and methods for registering event
 * listeners.
 *
 * @author Martin Matula
 */
public interface MDRObject extends MDRChangeSource, RefBaseObject {
    /** Returns a reference to the home repository of this object.
     * @return home repository of this object.
     */
    public MDRepository repository();
}
