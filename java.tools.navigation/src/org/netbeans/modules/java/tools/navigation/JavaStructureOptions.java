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
