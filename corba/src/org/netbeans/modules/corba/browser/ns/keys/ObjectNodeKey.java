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

package org.netbeans.modules.corba.browser.ns.keys;

/**
 *
 * @author  tzezula
 */
public class ObjectNodeKey {

        public static final int INTERFACE = 1;
        public static final int IOR = 2;

        private int type;
        private Object value;

        public ObjectNodeKey (int type, Object value) {
            this.type = type;
            this.value = value;
        }

        public int getType () {
            return this.type;
        }

        public Object getValue () {
            return this.value;
        }
        
        public int hashCode () {
            return this.type;
        }
        
        public boolean equals (Object other) {
            if (other == null)
                return false;
            if (other.hashCode() != this.hashCode())
                return false;
            return true;
        }

}
