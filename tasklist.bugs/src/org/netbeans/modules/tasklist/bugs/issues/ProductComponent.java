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

package org.netbeans.modules.tasklist.bugs.issues;

import java.util.ArrayList;
import java.util.List;

/**
 * A component
 */
public class ProductComponent {
    /** Name of this component */
    public String name = "component"; // todo i18n
    
    /** List of subcomponents. <String> */
    public List subcomponents = new ArrayList();
}
