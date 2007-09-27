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

import java.util.List;
import java.util.LinkedList;


/*
 * @author Karel Gardas
 * @version 0.01, Feb 19 2001
 */

public class WizardSettings {

    private boolean _M_is_supported = true;
    private List _M_requirements;

    public WizardSettings () {
	_M_requirements = new LinkedList ();
    }

    public WizardSettings (boolean __supported) {
	_M_requirements = new LinkedList ();
	_M_is_supported = __supported;
    }

    public WizardSettings (boolean __supported, LinkedList __requirements) {
	_M_is_supported = __supported;
	_M_requirements = __requirements;
    }

    public boolean isSupported () {
	return _M_is_supported;
    }

    public void setSupported (boolean __supported) {
	_M_is_supported = __supported;
    }

    public List getRequirements () {
	return _M_requirements;
    }

    public void setRequirements (LinkedList __requirements) {
	_M_requirements = __requirements;
    }

    public void addRequirement (WizardRequirement __requirement) {
	this.getRequirements ().add (__requirement);
    }

    public String toString () {
	StringBuffer __buf = new StringBuffer ();
	
	__buf.append ("supported: ");
	__buf.append (_M_is_supported);
	__buf.append ("\nrequirements: ");
	__buf.append (_M_requirements);

	return __buf.toString ();
    }

}

