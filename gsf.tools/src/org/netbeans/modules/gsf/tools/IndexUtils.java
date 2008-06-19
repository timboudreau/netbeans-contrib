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

package org.netbeans.modules.gsf.tools;

/**
 * This is extract from ruby just to remove dependency from gsf.tools on ruby
 *
 * @author Martin Adamek
 */
public class IndexUtils {

    public static final class IndexedElement {

        /** This method is documented */
        public static final int DOCUMENTED = 1 << 0;
        /** This method is protected */
        public static final int PROTECTED = 1 << 1;
        /** This method is private */
        public static final int PRIVATE = 1 << 2;
        /** This method is top level (implicit member of Object) */
        public static final int TOPLEVEL = 1 << 3;
        /** This element is "static" (e.g. it's a classvar for fields, class method for methods etc) */
        public static final int STATIC = 1 << 4;
        /** This element is deliberately not documented (rdoc :nodoc:) */
        public static final int NODOC = 1 << 5;

        /** Return flag corresponding to the given encoding chars */
        public static int stringToFlag(String s, int startIndex) {
            return stringToFlag(s.charAt(startIndex), s.charAt(startIndex+1));
        }

        /** Return flag corresponding to the given encoding chars */
        public static int stringToFlag(char first, char second) {
            int high = 0;
            int low = 0;
            if (first > '9') {
                high = first-'a'+10;
            } else {
                high = first-'0';
            }
            if (second > '9') {
                low = second-'a'+10;
            } else {
                low = second-'0';
            }
            return (high << 4) + low;
        }

        public static String decodeFlags(int flags) {
            StringBuilder sb = new StringBuilder();
            if ((flags & DOCUMENTED) != 0) {
                sb.append("|DOCUMENTED");
            }
            if ((flags & PRIVATE) != 0) {
                sb.append("|PRIVATE");
            }
            if ((flags & PROTECTED) != 0) {
                sb.append("|PROTECTED");
            }
            if ((flags & TOPLEVEL) != 0) {
                sb.append("|TOPLEVEL");
            }
            if ((flags & STATIC) != 0) {
                sb.append("|STATIC");
            }
            if ((flags & NODOC) != 0) {
                sb.append("|NODOC");
            }

            return sb.toString();
        }
    }

    public static final class IndexedClass {

        /** This class is a module rather than a proper class */
        public static final int MODULE = 1 << 6;

        public static String decodeFlags(int flags) {
            StringBuilder sb = new StringBuilder();
            sb.append(IndexedElement.decodeFlags(flags));

            if ((flags & MODULE) != 0) {
                sb.append("|MODULE");
            }
            if (sb.length() > 0) {
                sb.append("|");
            }

            return sb.toString();
        }

    }

    public static final class IndexedMethod {

        /** This method takes a (possibly optional, see BLOCK_OPTIONAL) block */
        public static final int BLOCK = 1 << 6;
        /** This method takes an optional block */
        public static final int BLOCK_OPTIONAL = 1 << 7;

        public static String decodeFlags(int flags) {
            StringBuilder sb = new StringBuilder();
            sb.append(IndexedElement.decodeFlags(flags));

            if ((flags & BLOCK) != 0) {
                sb.append("|BLOCK");
            }
            if ((flags & BLOCK_OPTIONAL) != 0) {
                sb.append("|BLOCK_OPTIONAL");
            }
            if (sb.length() > 0) {
                sb.append("|");
            }

            return sb.toString();
        }

    }

    public static final class IndexedField {

        public static String decodeFlags(int flags) {
            return IndexedElement.decodeFlags(flags);
        }

    }

}
