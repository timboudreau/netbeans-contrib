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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.visitors;

/**
 * Policies for renaming parameters in methods which override the method whose
 * parameters are being renamed.
 *
 * @author Tim Boudreau
 */
public enum ParameterRenamePolicy {
    /** Never rename parameters in implementing methods, leave them alone */
    DO_NOT_RENAME, 
    /** Rename parameters in implementing methods only if the current name of
      * the parameter matches the old name of the parameter being renamed */
    RENAME_IF_SAME, 
    /** Rename parameters in implementing methods unless there is a conflict
     * and that is not possible.
     */
    RENAME_UNLESS_CONFLICT
}
