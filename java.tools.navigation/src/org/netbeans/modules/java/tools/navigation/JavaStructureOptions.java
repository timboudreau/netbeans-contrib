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

package org.netbeans.modules.java.tools.navigation;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class JavaStructureOptions {
    /**
     * Holds value of property caseSensitive.
     */
    private static boolean caseSensitive = false;

    /**
     * Holds value of property showInherited.
     */
    private static boolean showInherited;

    /**
     * Holds value of property showInner.
     */
    private static boolean showInner = false;

    /**
     * Holds value of property showFQN.
     */
    private static boolean showFQN = false;

    /**
     * Holds value of property showConstructors.
     */
    private static boolean showConstructors = true;

    /**
     * Holds value of property showMethods.
     */
    private static boolean showMethods = true;

    /**
     * Holds value of property showFields.
     */
    private static boolean showFields = true;

    /**
     * Holds value of property showEnumConstants.
     */
    private static boolean showEnumConstants = true;

    /**
     * Holds value of property showProtected.
     */
    private static boolean showProtected = true;

    /**
     * Holds value of property showPackage.
     */
    private static boolean showPackage = true;

    /**
     * Holds value of property showPrivate.
     */
    private static boolean showPrivate = true;

    /**
     * Holds value of property showStatic.
     */
    private static boolean showStatic = true;

    /** Creates a new instance of JavaStructureOptions */
    public JavaStructureOptions() {
    }

    /**
     * Getter for property showInherited.
     * @return Value of property showInherited.
     */
    public static boolean isShowInherited() {
        return showInherited;
    }

    /**
     * Setter for property showInherited.
     * @param showInherited New value of property showInherited.
     */
    public static void setShowInherited(boolean showInherited) {
        JavaStructureOptions.showInherited = showInherited;
    }

    /**
     * Getter for property caseSensitive.
     * @return Value of property caseSensitive.
     */
    public static boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Setter for property caseSensitive.
     * @param caseSensitive New value of property caseSensitive.
     */
    public static void setCaseSensitive(boolean caseSensitive) {
        JavaStructureOptions.caseSensitive = caseSensitive;
    }

    /**
     * Getter for property showInner.
     * @return Value of property showInner.
     */
    public static boolean isShowFQN() {
        return JavaStructureOptions.showFQN;
    }

    /**
     * Setter for property showFQN.
     * @param showFQN New value of property showFQN.
     */
    public static void setShowFQN(boolean showFQN) {
        JavaStructureOptions.showFQN = showFQN;
    }

    /**
     * Getter for property showInner.
     * @return Value of property showInner.
     */
    public static boolean isShowInner() {
        return JavaStructureOptions.showInner;
    }

    /**
     * Setter for property showInner.
     * @param showInner New value of property showInner.
     */
    public static void setShowInner(boolean showInner) {
        JavaStructureOptions.showInner = showInner;
    }

    /**
     * Getter for property showConstructors.
     * @return Value of property showConstructors.
     */
    public static boolean isShowConstructors() {
        return JavaStructureOptions.showConstructors;
    }

    /**
     * Setter for property showConstructors.
     * @param showConstructors New value of property showConstructors.
     */
    public static void setShowConstructors(boolean showConstructors) {
        JavaStructureOptions.showConstructors = showConstructors;
    }

    /**
     * Getter for property showMethods.
     * @return Value of property showMethods.
     */
    public static boolean isShowMethods() {
        return JavaStructureOptions.showMethods;
    }

    /**
     * Setter for property showMethods.
     * @param showMethods New value of property showMethods.
     */
    public static void setShowMethods(boolean showMethods) {
        JavaStructureOptions.showMethods = showMethods;
    }

    /**
     * Getter for property showFields.
     * @return Value of property showFields.
     */
    public static boolean isShowFields() {
        return JavaStructureOptions.showFields;
    }

    /**
     * Setter for property showFields.
     * @param showFields New value of property showFields.
     */
    public static void setShowFields(boolean showFields) {
        JavaStructureOptions.showFields = showFields;
    }

    public static boolean isShowEnumConstants() {
        return showEnumConstants;
    }

    public static void setShowEnumConstants(boolean showEnumConstants) {
        JavaStructureOptions.showEnumConstants = showEnumConstants;
    }

    /**
     * Getter for property showPublicOnly.
     * @return Value of property showPublicOnly.
     */
    public static boolean isShowProtected() {
        return JavaStructureOptions.showProtected;
    }

    /**
     * Setter for property showPublicOnly.
     * @param showPublicOnly New value of property showPublicOnly.
     */
    public static void setShowProtected(boolean showProtected) {
        JavaStructureOptions.showProtected = showProtected;
    }

    /**
     * Getter for property showPackage.
     * @return Value of property showPackage.
     */
    public static boolean isShowPackage() {
        return JavaStructureOptions.showPackage;
    }

    /**
     * Setter for property showPackage.
     * @param showPackage New value of property showPackage.
     */
    public static void setShowPackage(boolean showPackage) {
        JavaStructureOptions.showPackage = showPackage;
    }

    /**
     * Getter for property showPrivate.
     * @return Value of property showPrivate.
     */
    public static boolean isShowPrivate() {
        return JavaStructureOptions.showPrivate;
    }

    /**
     * Setter for property showPrivate.
     * @param showPrivate New value of property showPrivate.
     */
    public static void setShowPrivate(boolean showPrivate) {
        JavaStructureOptions.showPrivate = showPrivate;
    }

    /**
     * Getter for property showStatic.
     * @return Value of property showStatic.
     */
    public static boolean isShowStatic() {
        return JavaStructureOptions.showStatic;
    }

    /**
     * Setter for property showStatic.
     * @param showStatic New value of property showStatic.
     */
    public static void setShowStatic(boolean showStatic) {
        JavaStructureOptions.showStatic = showStatic;
    }
}
