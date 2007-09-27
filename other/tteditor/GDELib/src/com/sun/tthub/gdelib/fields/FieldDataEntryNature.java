
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */


package com.sun.tthub.gdelib.fields;

/**
 * This interface defines the constants that represent the field data type
 * nature - i.e whether the field is of an entry type, single selection or
 * multi selection type. While the wizard determines the type of control to
 * be used to render the field, it uses the field data type nature as one 
 * of the inputs.
 *
 * @author Hareesh Ravindran
 */
public interface FieldDataEntryNature {
    
    /**
     * specifies that the field is of an entry type. i.e. there is no predefined
     * domain of values from which the user can select one or more. So, in all
     * cases, except that for a boolean, the wizard will render a text box for
     * capturing the user input for the value of the field. Boolean data type
     * is by itself a Single select data type with true and false as the selection
     * options. So, the wizard will render a checkbox or 2 radio buttons for 
     * this type.
     */
    public static final int TYPE_ENTRY = 0;
    
    /**
     * specifies that the field is of an enum type. i.e the user can select a 
     * value from a predefined set of values. So, the wizard will render either
     * a combo box or a single select list box or a radio button group 
     * in such cases, instead of a plain text box, so that it will prevent the 
     * user from entering incorrect values for the data type.
     */
    public static final int TYPE_SINGLE_SELECT = 1;
    
    /**
     * specifies that the field is of a mutilselect type. i.e. the user can select
     * one or more values from a pre defined set of values. So, the wizard will
     * render either a multi select list box or a check box group in such cases,
     * instead of a plain text box so that it will prevent the user from 
     * entering incorrect values for the data type.
     */
    public static final int TYPE_MULTI_SELECT = 2;
}
