/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.portlets.spring.handlers.form;

/**
 *
 * @author satyaranjan
 */
public class TypesHelper {
    
    public static final String TEXT_COMP = "text";
    public static final String PASSWORD_COMP = "password";
    public static final String CHECKBOX_COMP = "checkbox";
    public static final String RADIO_COMP = "radio";
    public static final String SELECT_COMP = "select";
    
    public static final DataType STRING_TYPE = new DataType("String","String");
    public static final DataType FILE_TYPE = new DataType("file","File");
    public static final DataType INT_TYPE = new DataType("int","Integer");
    public static final DataType DOUBLE_TYPE = new DataType("double","Double");
    public static final DataType BOOLEAN_TYPE = new DataType("boolean","Boolean");
    
    public static DataType[] getDefaultDataTypes() {
        
        DataType[] types = {STRING_TYPE,
                    FILE_TYPE,
                    INT_TYPE,
                    DOUBLE_TYPE,
                    BOOLEAN_TYPE};
        
        return types;
    }
    
    public static String[] getDefaultComponentTypes() {
        
        String[] componentTypes = {TEXT_COMP,PASSWORD_COMP,CHECKBOX_COMP,RADIO_COMP,SELECT_COMP};       
        return componentTypes;
    }
    
}
