/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.aspects;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adlookup.AdaptableLookup;
import org.netbeans.modules.adaptable.Suite;
import org.openide.util.Lookup;

/** Inherited tests from adaptable framework to guarantee the same behaviour.
 *
 * @author Jaroslav Tulach
 */
public class AdaptableTest extends Object {
    public static Test suite() {
        return Suite.create();
    }
  
}
