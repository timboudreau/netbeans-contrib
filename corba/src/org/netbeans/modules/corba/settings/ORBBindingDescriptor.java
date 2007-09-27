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

import java.util.Properties;

/*
 * @author Karel Gardas
 * @version 0.01, Jan 8 2001
 */

public class ORBBindingDescriptor {

    private String _M_name;
    private String _M_template_tag;
    private String _M_local_tag;
    private String _M_import;
    private String _M_code;
    private WizardSettings _M_wizard_settings;
    private Properties _M_java_template_table;

    public ORBBindingDescriptor () {
    }

    public String getName () {
	return _M_name;
    }

    public void setName (String __value) {
	_M_name = __value;
    }

    public String getTemplateTag () {
	return _M_template_tag;
    }

    public void setTemplateTag (String __value) {
	_M_template_tag = __value;
    }

    public String getLocalTag () {
	return _M_template_tag;
    }

    public void setLocalTag (String __value) {
	_M_template_tag = __value;
    }

    public String getImport () {
	if (_M_import == null)
	    return "";
	return _M_import;
    }

    public void setImport (String __value) {
	_M_import = __value;
    }

    public String getCode () {
	if (_M_code == null)
	    return "";
	return _M_code;
    }

    public void setCode (String __value) {
	_M_code = __value;
    }

    public WizardSettings getWizardSettings () {
	return _M_wizard_settings;
    }

    public void setWizardSettings (WizardSettings __settings) {
	_M_wizard_settings = __settings;
    }

    public void setJavaTemplateCodeTable (Properties __value) {
	//System.out.println (this.getName () + "::setJavaTemplateCodeTable <-" + __value);
	_M_java_template_table = __value;
    }

    public Properties getJavaTemplateCodeTable () {
	if (_M_java_template_table == null)
	    _M_java_template_table = new Properties ();
	//System.out.println (this.getName () + "::getJavaTemplateCodeTable () -> " + _M_java_template_table);
	return _M_java_template_table;
    }

    public void addJavaTemplateCode (String __key, String __value) {
	//System.out.println ("add property for " + this.getName () + ": " + __key + ": " + __value);
	if (_M_java_template_table == null)
	    _M_java_template_table = new Properties ();
	_M_java_template_table.setProperty (__key, __value);
    }

    public String toString () {
	StringBuffer __buf = new StringBuffer ();
	__buf.append ("name: ");
	__buf.append (_M_name);
	__buf.append (", template-tag: ");
	__buf.append (_M_template_tag);
	__buf.append (", local-tag: ");
	__buf.append (_M_local_tag);
	__buf.append ("\nimport: ");
	__buf.append (_M_import);
	__buf.append ("\ncode: ");
	__buf.append (_M_code);
	__buf.append ("\nwizard settings: ");
	__buf.append (_M_wizard_settings);
	__buf.append ("\ntemplate codes: ");
	__buf.append (_M_java_template_table);
	return __buf.toString ();
    }
}

