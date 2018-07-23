/*
 * Scanner.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.classfile.Code;
import org.netbeans.modules.classfile.Method;

/**
 * Code shared between scanners.
 *
 * @author  tball
 * @version
 */
abstract class Scanner {

    protected Method method;
    protected byte[] codeBytes;
    protected int offset;

    /** Don't instantiate. */
    protected Scanner(Method m) {
        this.method = m;
        Code code = m.getCode();
        if (code == null) // true for abstract methods
            codeBytes = new byte[0];
        else
            codeBytes = code.getByteCodes();
    }

    /**
     * Return the byte stored at a given index from the offset
     * within code bytes
     */  
    protected final int at(int index) {
    	return codeBytes[offset+index] & 0xFF;
    }

    /**
     * Return the short stored at a given index from the offset
     * within code bytes
     */  
    protected final int shortAt(int index) {
        int base = offset + index;
    	return ((codeBytes[base] & 0xFF) << 8) 
    	    | (codeBytes[base+1] & 0xFF);
    }

    /**
     * Given the table at the specified index, return the specified entry
     */  
    protected final long intAt(int tbl, int entry) {
        int base = tbl + (entry << 2);
    	return (codeBytes[base] << 24) 
    	    | ((codeBytes[base+1] & 0xFF) << 16)
    	    | ((codeBytes[base+2] & 0xFF) << 8)
    	    | ((codeBytes[base+3] & 0xFF));
    }
}
