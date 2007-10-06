/*
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
 */

package org.netbeans.modules.tasklist.filter;

import org.openide.util.NbBundle;

/**
 * Base class for all conditions that have a list of options to choose
 * from the mode of operation, e.g. {Contains, Equals,..},
 * {Greater, Equal}, {True, NotTrue},etc.
 *
 * @author Tor Norbye
 * @author tl
 */
public abstract class OneOfFilterCondition extends FilterCondition {
    
    private String[] options = null;
    private int id; // the selected option from nameKeys
    
    /**
     * Creates a condition with the given set of options and selected option.
     *
     * @param opts the set of options to choose from
     * @param id one of the constants from this class
     */
    public OneOfFilterCondition(String [] opts, int id) {
        this.options = opts;
        this.id = id;
    }
    
    public OneOfFilterCondition(final OneOfFilterCondition rhs) {
        super(rhs);
        this.options = rhs.options;
        this.id = rhs.id;
    }
    
    protected OneOfFilterCondition(String [] opts) {
        this.options = opts;
        this.id = -1;
    }
    
    
    public boolean sameType(FilterCondition fc) {
        return super.sameType(fc) && this.id == ((OneOfFilterCondition)fc).id;
    }
    
    protected String getDisplayName() {
        return NbBundle.getMessage(this.getClass(), options[id]);
    }
    
    protected int getId() { return id;}
    
    
    protected abstract  static class Convertor extends FilterCondition.Convertor {
        private static final String ATTR_ID = "id";
        
        private String [] nameKeys;
        
        public Convertor(String root, String [] nameKeys) {
            super(root);
            this.nameKeys = nameKeys;
        }
        
        /**
         * Override if you want to use the default readElement and
         * writeElement
         */
        protected OneOfFilterCondition createCondition() {
            throw new UnsupportedOperationException();
        }
        
        protected void readCondition(org.w3c.dom.Element element, OneOfFilterCondition cond)
                throws java.io.IOException, java.lang.ClassNotFoundException {
            super.readCondition(element, cond);
            cond.id = findIndex(element.getAttribute(ATTR_ID), nameKeys);
        }
        
        protected Object readElement(org.w3c.dom.Element element)
                throws java.io.IOException, java.lang.ClassNotFoundException {
            OneOfFilterCondition cond = createCondition();
            this.readCondition(element, cond);
            return cond;
        }
        
        // write methods for supported condition types
        protected void writeCondition(org.w3c.dom.Document document, org.w3c.dom.Element element, OneOfFilterCondition cond)
                throws java.io.IOException, org.w3c.dom.DOMException {
            super.writeCondition(document, element, cond);
            element.setAttribute(ATTR_ID, nameKeys[cond.id]);
        }
        
        // write methods for supported condition types
        protected void writeElement(org.w3c.dom.Document document, org.w3c.dom.Element element, Object obj)
                throws java.io.IOException, org.w3c.dom.DOMException {
            this.writeCondition(document, element, (OneOfFilterCondition)obj);
        }
        
        /** A utility function for searching for a string in an array of
         * string.
         * @param str String to find
         * @param arr array to search
         * @return the index if str in arr or throws IOException when no found
         */
        protected static int findIndex(String str, String[] arr) throws java.io.IOException {
            for (int i = 0 ; i < arr.length; i++)
                if (arr[i].equals(str)) return i;
            
            throw new java.io.IOException("The value " + str + " not found in the array " + arr);
        }
    }
}
