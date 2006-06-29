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

package org.netbeans.spi.adaptable;

/** Provides notifications about actions made on the Adaptable.
 * Implement when you want to be notified when a first call
 * is made or listener is attached to an Adaptable.
 *
 * @see Adaptors
 */
public interface Initializer {
    /** Notifies that an object's Adaptable is now being used. Allows
     * the implementators to do neccessary actions - e.g. attach listeners,
     * pre-initialize additional data structures, etc.
     *
     * @param representedObject the object backed by an adaptable
     */
  public void initialize (Object representedObject);
}
