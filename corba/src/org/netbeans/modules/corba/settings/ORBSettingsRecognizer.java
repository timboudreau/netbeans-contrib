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

package org.netbeans.modules.corba.settings;

/*
 * @author Karel Gardas
 * @version 0.01, Jan 11 2001
 */

public class ORBSettingsRecognizer implements org.xml.sax.DocumentHandler {
    private java.lang.StringBuffer buffer;
    
    private ORBSettingsParslet parslet;
    
    private ORBSettingsHandler handler;
    
    private java.util.Stack context;
    
    public ORBSettingsRecognizer(final ORBSettingsHandler handler,final ORBSettingsParslet parslet) {
        this.parslet = parslet;
        this.handler = handler;
        buffer = new StringBuffer(111);
        context = new java.util.Stack();
    }
    
    public void setDocumentLocator(org.xml.sax.Locator locator) {
        handler.setDocumentLocator(locator);
    }
    
    public void startDocument() throws org.xml.sax.SAXException {
        handler.startDocument();
    }
    
    public void endDocument() throws org.xml.sax.SAXException {
        handler.endDocument();
    }
    
    public void startElement(java.lang.String name,org.xml.sax.AttributeList attrs) throws org.xml.sax.SAXException {
        dispatch(true);
	//System.out.println ("this.dispatch (false);");
	//this.dispatch (false);
        context.push(new Object[] {name, new org.xml.sax.helpers.AttributeListImpl(attrs)});
        handler.startElement(name, attrs);
    }
    
    public void endElement(java.lang.String name) throws org.xml.sax.SAXException {
        dispatch(false);
        context.pop();
        handler.endElement(name);
    }
    
    public void characters(char[] chars, int start, int len) throws org.xml.sax.SAXException {
        buffer.append(chars, start, len);
        handler.characters(chars, start, len);
    }
    
    public void ignorableWhitespace(char[] chars,int start,int len) throws org.xml.sax.SAXException {
        handler.ignorableWhitespace(chars, start, len);
    }
    
    public void processingInstruction(java.lang.String target,java.lang.String data) throws org.xml.sax.SAXException {
        handler.processingInstruction(target, data);
    }
    
    private void dispatch(final boolean fireOnlyIfMixed) throws org.xml.sax.SAXException {
	try {
	if (fireOnlyIfMixed && buffer.length() == 0) {
	    //System.out.println ("skip...");
	    return; //skip it
        }
        Object[] ctx = (Object[]) context.peek();
        String here = (String) ctx[0];
        org.xml.sax.AttributeList attrs = (org.xml.sax.AttributeList) ctx[1];
        if ("policies-header".equals(here)) {
	    //System.err.println ("ORBSettingsRecognizer:: -> will handle policies-header");
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_policies_header(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("get-root-poa-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_get_root_poa_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("servant-class".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_servant_class(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("orb-settings".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_orb_settings(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("poa-settings".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_poa_settings(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("poa-activator-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_poa_activator_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("compiler".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_compiler(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("import".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_import(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("section-init-poas".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_section_init_poas(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("tie-impl-prefix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_tie_impl_prefix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("ext-class-prefix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_ext_class_prefix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("root-poa-init".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_root_poa_init(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("create-servant-instance".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_create_servant_instance(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("file-position".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_file_position(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("idl-template-code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_idl_template_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("message-position".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_message_position(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("policies-footer".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_policies_footer(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("line-position".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_line_position(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("impl-int-postfix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_impl_int_postfix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("tie-impl-postfix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_tie_impl_postfix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("package-param".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_package_param(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("tie-param".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_tie_param(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("disable-action".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_disable_action(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("error-expression".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_error_expression(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("prepare-code-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_prepare_code_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("activate-poa".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_activate_poa(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("poa-policy".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
	    //System.out.println ("ORBSettingsRecognizer::dispatch ()-> handle_poa_policy");
            handler.handle_poa_policy(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("implbase-impl-prefix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_implbase_impl_prefix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("require-policy".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_require_policy(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("section-init-servants".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_section_init_servants(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("create-code-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_create_code_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("policies-separator".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_policies_separator(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("template-code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_template_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("ext-class-postfix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_ext_class_postfix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("policies-header-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_policies_header_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("policies-declaration".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_policies_declaration(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("idl-compiler-settings".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_idl_compiler_settings(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("policies-footer-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_policies_footer_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("policies-declaration-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_policies_declaration_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("dir-param".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_dir_param(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("section-activate-poas".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_section_activate_poas(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("impl-generator-settings".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_impl_generator_settings(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("column-position".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_column_position(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("prepare-code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_prepare_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("client-binding".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_client_binding(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("set-default-servant".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_set_default_servant(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("impl-int-prefix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_impl_int_prefix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("package-delimiter".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_package_delimiter(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("create-code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_create_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("set-poa-activator".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_set_poa_activator(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("create-poa".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_create_poa(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("servant-instance-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_servant_instance_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("unrecognized-policy".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_unrecognized_policy(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("value".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_value(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("servant-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_servant_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("java-template-code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_java_template_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("conflicts-with-policy".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_conflicts_with_policy(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("activate-servant-with-system-id".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_activate_servant_with_system_id(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("set-servant-manager".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_set_servant_manager(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("activate-servant-with-user-id".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_activate_servant_with_user_id(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("servant-manager-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_servant_manager_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("server-binding".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_server_binding(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("local-bundle".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_local_bundle(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("root-poa-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_root_poa_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("implbase-impl-postfix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_implbase_impl_postfix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-servant-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_servant_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("poa-pattern".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_poa_pattern(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("get-poa-manager".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_get_poa_manager(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("value-impl-prefix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_value_impl_prefix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("value-impl-postfix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_value_impl_postfix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("valuefactory-impl-prefix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_valuefactory_impl_prefix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("valuefactory-impl-postfix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_valuefactory_impl_postfix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-default-servant-var-name".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_default_servant_var_name(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-poa-activator-var-name".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_poa_activator_var_name(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-poa-name".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_poa_name(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-poa-var-name".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_poa_var_name(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-servant-id".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_servant_id(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-servant-id-var-name".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_servant_id_var_name(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-servant-var-name".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_servant_var_name(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else if ("default-servant-manager-var-name".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_default_servant_manager_var_name(buffer.length() == 0 ? null : buffer.toString(), attrs);
	} else if ("patch-code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_patch_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
	} else if ("wizard-requires".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_wizard_requires(buffer.length() == 0 ? null : buffer.toString(), attrs);
	} else if ("wizard-does-not-support".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_wizard_does_not_support(buffer.length() == 0 ? null : buffer.toString(), attrs);
	} else if ("binding-template-code".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_binding_template_code(buffer.length() == 0 ? null : buffer.toString(), attrs);
	} else if ("tie-class-prefix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_tie_class_prefix(buffer.length() == 0 ? null : buffer.toString(), attrs);
	} else if ("tie-class-postfix".equals(here)) {
            if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected mixed content element or a parser reporting whitespaces via characters() event!");
            handler.handle_tie_class_postfix(buffer.length() == 0 ? null : buffer.toString(), attrs);
        } else {
            //do not care
        }
        buffer.delete(0, buffer.length());
	} catch (Exception __ex) {
	    __ex.printStackTrace ();
	}
    }
    
/**
 * The recognizer entry method taking an InputSource.
 * @param input InputSource to be parsed.
 * @throw java.io.IOException on I/O error.
 * @throw org.xml.sax.SAXException propagated exception thrown by a DocumentHandler.
 * @throw javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
 * @throw javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
 */
    public void parse(final org.xml.sax.InputSource input) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        parse(input, this);
    }
    
/**
 * The recognizer entry method taking a URL.
 * @param url URL source to be parsed.
 * @throw java.io.IOException on I/O error.
 * @throw org.xml.sax.SAXException propagated exception thrown by a DocumentHandler.
 * @throw javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
 * @throw javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
 */
    public void parse(final java.net.URL url) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        parse(new org.xml.sax.InputSource(url.toExternalForm()), this);
    }
    
/**
 * The recognizer entry method taking an Inputsource.
 * @param input InputSource to be parsed.
 * @throw java.io.IOException on I/O error.
 * @throw org.xml.sax.SAXException propagated exception thrown by a DocumentHandler.
 * @throw javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
 * @throw javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
 */
    public static void parse(final org.xml.sax.InputSource input,final ORBSettingsHandler handler,final ORBSettingsParslet parslet) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        parse(input, new ORBSettingsRecognizer(handler, parslet));
    }
    
/**
 * The recognizer entry method taking a URL.
 * @param url URL source to be parsed.
 * @throw java.io.IOException on I/O error.
 * @throw org.xml.sax.SAXException propagated exception thrown by a DocumentHandler.
 * @throw javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
 * @throw javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
 */
    public static void parse(final java.net.URL url,final ORBSettingsHandler handler,final ORBSettingsParslet parslet) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        parse(new org.xml.sax.InputSource(url.toExternalForm()), handler, parslet);
    }
    
    private static void parse(final org.xml.sax.InputSource input,final ORBSettingsRecognizer recognizer) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        javax.xml.parsers.SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance();
        factory.setValidating(true);  //the code was generated according DTD
        factory.setNamespaceAware(false);  //the code was generated according DTD
        org.xml.sax.Parser parser = factory.newSAXParser().getParser();
        parser.setDocumentHandler(recognizer);
        parser.parse(input);
    }
    
    
}

