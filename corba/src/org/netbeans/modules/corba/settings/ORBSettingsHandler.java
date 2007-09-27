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

package org.netbeans.modules.corba.settings;

import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;

/*
 * @author Karel Gardas
 * @version 0.01, Jan 11 2001
 */

public interface ORBSettingsHandler extends org.xml.sax.DocumentHandler {
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_policies_header(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;

/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_get_root_poa_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_servant_class(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_orb_settings(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_poa_settings(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_poa_activator_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_compiler(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_import(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_section_init_poas(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_tie_impl_prefix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_ext_class_prefix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_code(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_root_poa_init(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_create_servant_instance(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_idl_template_code(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_file_position(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_message_position(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_policies_footer(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_line_position(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_impl_int_postfix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_tie_impl_postfix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_package_param(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_tie_param(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_disable_action(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_activate_poa(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_error_expression(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_prepare_code_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_poa_policy(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_implbase_impl_prefix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_require_policy(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_section_init_servants(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_policies_separator(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_create_code_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_template_code(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_policies_header_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_ext_class_postfix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_policies_declaration(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_idl_compiler_settings(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_policies_declaration_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_policies_footer_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_dir_param(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_section_activate_poas(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_impl_generator_settings(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_column_position(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_prepare_code(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_client_binding(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_set_default_servant(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_impl_int_prefix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_package_delimiter(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_create_code(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_set_poa_activator(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_unrecognized_policy(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_servant_instance_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_create_poa(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_value(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_servant_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_java_template_code(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_activate_servant_with_system_id(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_conflicts_with_policy(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_set_servant_manager(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_activate_servant_with_user_id(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_servant_manager_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_root_poa_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_local_bundle(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_server_binding(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_poa_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_default_servant_pattern(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_implbase_impl_postfix(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;
    
/**
 * An event handling method.
 * @param data value or null
 * @param meta attributes
 */
    public void handle_get_poa_manager(final java.lang.String data,final org.xml.sax.AttributeList meta) throws org.xml.sax.SAXException;


    // written by hand
    public void handle_value_impl_prefix (String __data, AttributeList __meta) throws SAXException;
    public void handle_value_impl_postfix (String __data, AttributeList __meta) throws SAXException;
    public void handle_valuefactory_impl_prefix (String __data, AttributeList __meta) throws SAXException;
    public void handle_valuefactory_impl_postfix (String __data, AttributeList __meta) throws SAXException;

    public void handle_default_default_servant_var_name (String __data, AttributeList __meta) throws SAXException;
    public void handle_default_poa_activator_var_name (String __data, AttributeList __meta) throws SAXException;
    public void handle_default_poa_name (String __data, AttributeList __meta) throws SAXException;
    public void handle_default_poa_var_name (String __data, AttributeList __meta) throws SAXException;
    public void handle_default_servant_id (String __data, AttributeList __meta) throws SAXException;
    public void handle_default_servant_id_var_name (String __data, AttributeList __meta) throws SAXException;
    public void handle_default_servant_var_name (String __data, AttributeList __meta) throws SAXException;
    public void handle_default_servant_manager_var_name (String __data, AttributeList __meta) throws SAXException;

    public void handle_patch_code (String __data, AttributeList __meta) throws SAXException;
    
    public void handle_wizard_requires (String __data, AttributeList __meta) throws SAXException;
    public void handle_wizard_does_not_support (String __data, AttributeList __meta) throws SAXException;

    public void handle_binding_template_code (String __data, AttributeList __meta) throws SAXException;

    public void handle_tie_class_prefix (String __data, AttributeList __meta) throws SAXException;
    public void handle_tie_class_postfix (String __data, AttributeList __meta) throws SAXException;
    
    /*
      public void handle_ (String __data, AttributeList __meta) throws SAXException;
    */
}
