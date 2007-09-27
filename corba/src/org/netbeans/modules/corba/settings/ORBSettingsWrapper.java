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

public class ORBSettingsWrapper implements java.io.Serializable {

    public static final long serialVersionUID = -173181041826475252L;

    private static final boolean DEBUG = false;
    //private static final boolean DEBUG = true;

    private ORBSettings _M_settings;
    private String _M_value;

    public ORBSettingsWrapper () {
	if (DEBUG)
	    System.out.println ("ORBSettingsWrapper::ORBSettingsWrapper ()"); // NOI18N
    }

    public ORBSettingsWrapper (ORBSettings __settings) {
	if (DEBUG) 
	    System.out.println ("ORBSettingsWrapper::ORBSettingsWrapper (" + __settings + ")"); // NOI18N
	_M_settings = __settings;
	if (_M_settings.getServerBindings ().size () > 0)
	    _M_value = (String)_M_settings.getServerBindings ().get (0);
	else
	    _M_value = ""; // NOI18N
    }

    public ORBSettingsWrapper (ORBSettings __settings, String __value) {
	if (DEBUG) 
	    System.out.println ("ORBSettingsWrapper::ORBSettingsWrapper (" + __settings + ", " // NOI18N
				+ __value + ")"); // NOI18N
	_M_settings = __settings;
	_M_value = __value;
    }

    public ORBSettings getSettings () {
	return _M_settings;
    }

    public void setSettings (ORBSettings __settings) {
	_M_settings = __settings;
    }

    public String getValue () {
	return _M_value;
    }

    public void setValue (String __value) {
	if (DEBUG)
	    System.out.println ("ORBSettingsWrapper::setValue (" + __value); // NOI18N
	_M_value = __value;
    }

    public boolean equals (Object __value) {
	if (DEBUG)
	    System.out.print ("ORBSettingsWrapper::equals (...) -> "); // NOI18N
	boolean __return = false;
	try {
	    ORBSettingsWrapper __tmp = (ORBSettingsWrapper)__value;
	    if (this.getSettings ().equals (__tmp.getSettings ()))
		if (this.getValue ().equals (__tmp.getValue ()))
		    __return = true;
	} catch (Exception __e) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		__e.printStackTrace ();
	}
	if (DEBUG)
	    System.out.println (__return);
	return __return;
    }

    public String toString () {
	return _M_value;
    }
}




