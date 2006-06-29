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

package org.netbeans.modules.aspects;

import java.util.Collections;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adlookup.AdaptableLookup;
import org.netbeans.modules.adaptable.Suite;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/** Inherited tests from adaptable framework to guarantee the same behaviour.
 *
 * @author Jaroslav Tulach
 */
public class AdaptableTest extends Object {
    public static Test suite() {
        return Suite.create(Collections.singleton(Utilities.activeReferenceQueue()));
    }
  
}
