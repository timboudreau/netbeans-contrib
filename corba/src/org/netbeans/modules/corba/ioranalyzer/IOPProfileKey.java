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

/*
 * IOPProfileKey.java
 *
 * Created on November 7, 2000, 2:39 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

/**
 *
 * @author  tzezula
 * @version
 */
public class IOPProfileKey extends ProfileKey {

    public IORProfile value;

    /** Creates new IOPProfileKey */
    public IOPProfileKey(int index, IORProfile profile) {
        super (index);
        this.value = profile;
    }


    public boolean equals (Object other) {
        if (!(other instanceof IOPProfileKey))
            return false;
        return value.equals (((IOPProfileKey)other).value);
    }
    
    public int hashCode () {
        return index;
    }

}
