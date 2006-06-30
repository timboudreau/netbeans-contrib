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

