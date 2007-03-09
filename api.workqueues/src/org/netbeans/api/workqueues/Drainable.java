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
package org.netbeans.api.workqueues;

import java.util.List;

/**
 * Represents a collection which can be drained of all elements assignable
 * to a particular type.
 * 
 * @author Tim Boudreau
 */
public interface Drainable <WorkType> {
    /**
     * Fetch all items owned by this object which are instances of the passed
     * type, removing them from the collection of items this 
     * Drainable holds.
     */
    public <T extends WorkType> List<T> drain(Class<T> clazz);
}