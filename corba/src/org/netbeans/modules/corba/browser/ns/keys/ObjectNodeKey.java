/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
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
