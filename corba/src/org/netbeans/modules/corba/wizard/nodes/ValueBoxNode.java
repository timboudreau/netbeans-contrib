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

package org.netbeans.modules.corba.wizard.nodes;

import org.openide.*;
import org.openide.nodes.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;

public class ValueBoxNode extends AbstractMutableLeafNode {

    public static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/value";

    public ValueBoxNode (AliasKey key) {
	super (key);
	this.setIconBase (ICON_BASE);
    }

    public String getName () {
	return this.getDisplayName();
    }

    public String getDisplayName () {
	return this.key.getName();
    }
    
    public String generateSelf (int indent) {
	String fill = "";
	for (int i=0; i< indent; i++)
	    fill = fill + SPACE;
	fill = fill + "valuetype " + this.getName () +" "+((AliasKey)this.key).getType()+";\n";
	return fill;
    }
    
    public ExPanel getEditPanel () {
        ValueBoxPanel p = new ValueBoxPanel ();
        p.setName (this.getName());
        p.setType (((AliasKey)this.key).getType());
	return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ValueBoxPanel) {
            ValueBoxPanel vp = (ValueBoxPanel) p;
            String newName = vp.getName();
            String newType = vp.getType();
            if (!newName.equals (this.getName())) {
                this.setName (newName);
                this.key.setName (newName);
            }
            if (!newType.equals (((AliasKey)this.key).getType())) {
                ((AliasKey)this.key).setType (newType);
            }
        }
    }
}