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

package org.netbeans.modules.corba.idl.node;

import java.util.ResourceBundle;

import org.openide.util.NbBundle;

/*
 *
 * @author Karel Gardas
 * @version 0.01 August 12, 2000
 */

class IDLNodeBundle {

    public static ResourceBundle bundle = NbBundle.getBundle (IDLNodeBundle.class);

    public static String NAME = IDLNodeBundle.bundle.getString ("CTL_NAME");
    public static String TYPE = IDLNodeBundle.bundle.getString ("CTL_TYPE");
    public static String YES = IDLNodeBundle.bundle.getString ("CTL_YES");
    public static String NO = IDLNodeBundle.bundle.getString ("CTL_NO");
    public static String DIMENSION = IDLNodeBundle.bundle.getString ("CTL_DIMENSION");
    public static String READONLY = IDLNodeBundle.bundle.getString ("CTL_READONLY");
    public static String EXPRESSION = IDLNodeBundle.bundle.getString ("CTL_EXPRESSION");
    public static String ABSTRACT = IDLNodeBundle.bundle.getString ("CTL_ABSTRACT");
    public static String INHERITED = IDLNodeBundle.bundle.getString ("CTL_INHERITED");
    public static String RESULT = IDLNodeBundle.bundle.getString ("CTL_RESULT");
    public static String ATTRIBUTE = IDLNodeBundle.bundle.getString ("CTL_ATTRIBUTE");
    public static String PARAMETERS = IDLNodeBundle.bundle.getString ("CTL_PARAMETERS");
    public static String EXCEPTIONS = IDLNodeBundle.bundle.getString ("CTL_EXCEPTIONS");
    public static String CONTEXTS = IDLNodeBundle.bundle.getString ("CTL_CONTEXTS");
    public static String CASE = IDLNodeBundle.bundle.getString ("CTL_CASE");
    public static String SWITCH_TYPE = IDLNodeBundle.bundle.getString ("CTL_SWITCH_TYPE");
    public static String CUSTOM = IDLNodeBundle.bundle.getString ("CTL_CUSTOM");
    public static String SUPPORTED = IDLNodeBundle.bundle.getString ("CTL_SUPPORTED");
    public static String MODIFIER = IDLNodeBundle.bundle.getString ("CTL_MODIFIER");
    public static String PUBLIC = IDLNodeBundle.bundle.getString ("CTL_PUBLIC");
    public static String PRIVATE = IDLNodeBundle.bundle.getString ("CTL_PRIVATE");
    public static String UNKNOWN = IDLNodeBundle.bundle.getString ("CTL_UNKNOWN");
    public static String NAME_OF_ATTRIBUTE = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_ATTRIBUTE");
    public static String TYPE_OF_ATTRIBUTE = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_ATTRIBUTE");
    public static String READONLY_ATTRIBUTE = IDLNodeBundle.bundle.getString ("HINT_READONLY_ATTRIBUTE");
    public static String NAME_OF_CONSTANT = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_CONSTANT");
    public static String TYPE_OF_CONSTANT = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_CONSTANT");
    public static String CONSTANT_EXPRESSION = IDLNodeBundle.bundle.getString ("HINT_CONSTANT_EXPRESSION");
    public static String NAME_OF_DECLARATOR = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_DECLARATOR");
    public static String TYPE_OF_DECLARATOR = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_DECLARATOR");
    public static String DIMENSION_OF_DECLARATOR = IDLNodeBundle.bundle.getString ("HINT_DIMENSION_OF_DECLARATOR");
    public static String NAME_OF_EXCEPTION = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_EXCEPTION");
    public static String NAME_OF_INTERFACE = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_INTERFACE");
    public static String ABSTRACT_INTERFACE = IDLNodeBundle.bundle.getString ("HINT_ABSTRACT_INTERFACE");
    public static String INHERITED_FROM = IDLNodeBundle.bundle.getString ("HINT_INHERITED_FROM");
    public static String NAME_OF_MEMBER = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_MEMBER");
    public static String TYPE_OF_MEMBER = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_MEMBER");
    public static String NAME_OF_MODULE = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_MODULE");
    public static String NAME_OF_OPERATION = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_OPERATION");
    public static String TYPE_OF_RESULT = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_RESULT");
    public static String ATTRIBUTE_OF_OPERATION = IDLNodeBundle.bundle.getString ("HINT_ATTRIBUTE_OF_OPERATION");
    public static String PARAMETERS_OF_OPERATION = IDLNodeBundle.bundle.getString ("HINT_PARAMETERS_OF_OPERATION");
    public static String EXCEPTIONS_OF_OPERATION= IDLNodeBundle.bundle.getString ("HINT_EXCEPTIONS_OF_OPERATION");
    public static String CONTEXTS_OF_OPERATION = IDLNodeBundle.bundle.getString ("HINT_CONTEXTS_OF_OPERATION");
    public static String NAME_OF_TYPEDEF = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_TYPEDEF");
    public static String NAME_OF_UNION_MEMBER = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_UNION_MEMBER");
    public static String TYPE_OF_UNION_MEMBER = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_UNION_MEMBER");
    public static String CASE_OF_UNION_MEMBER = IDLNodeBundle.bundle.getString ("HINT_CASE_OF_UNION_MEMBER");
    public static String NAME_OF_UNION = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_UNION");
    public static String TYPE_OF_UNION = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_UNION");
    public static String TYPE_OF_SWITCH = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_SWITCH");
    public static String NAME_OF_VALUE = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_VALUE");
    public static String ABSTRACT_VALUE = IDLNodeBundle.bundle.getString ("HINT_ABSTRACT_VALUE");
    public static String CUSTOM_VALUE = IDLNodeBundle.bundle.getString ("HINT_CUSTOM_VALUE");
    public static String SUPPORTS_INTERFACES = IDLNodeBundle.bundle.getString ("HINT_SUPPORTS_INTERFACES");
    public static String NAME_OF_VALUEBOX= IDLNodeBundle.bundle.getString ("HINT_NAME_OF_VALUEBOX");
    public static String TYPE_OF_VALUEBOX = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_VALUEBOX");
    public static String NAME_OF_STATE = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_STATE");
    public static String TYPE_OF_STATE = IDLNodeBundle.bundle.getString ("HINT_TYPE_OF_STATE");
    public static String DIMENSION_OF_STATE = IDLNodeBundle.bundle.getString ("HINT_DIMENSION_OF_STATE");
    public static String MODIFIER_OF_STATE = IDLNodeBundle.bundle.getString ("HINT_MODIFIER_OF_STATE");
    public static String NAME_OF_FACTORY = IDLNodeBundle.bundle.getString ("HINT_NAME_OF_FACTORY");
    public static String PARAMETERS_OF_FACTORY = IDLNodeBundle.bundle.getString ("HINT_PARAMETERS_OF_FACTORY");
    //public static String = IDLNodeBundle.bundle.getString ("");
    

}
