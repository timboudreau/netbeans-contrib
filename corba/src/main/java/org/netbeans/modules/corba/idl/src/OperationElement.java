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

package org.netbeans.modules.corba.idl.src;

import java.util.Vector;

public class OperationElement extends IDLElement {

    private String op_attribute;
    //private Element op_type_spec;
    private IDLType op_type_spec;
    //private Element name;
    private Vector params;
    private Vector exceptions;
    private Vector contexts;

    static final long serialVersionUID =-533680242820260136L;
    public OperationElement(int id) {
        super(id);
        params = new Vector ();
        exceptions = new Vector ();
        contexts = new Vector ();
    }

    public OperationElement(IDLParser p, int id) {
        super(p, id);
        params = new Vector ();
        exceptions = new Vector ();
        contexts = new Vector ();
    }

    public void setAttribute (String attr) {
        op_attribute = attr;
    }

    public String getAttribute () {
        return op_attribute;
    }

    /*
    public void setReturnType (Element type) {
       op_type_spec = type;
}

    public Element getReturnType () {
       return op_type_spec;
}
    */

    public void setReturnType (IDLType type) {
        op_type_spec = type;
    }

    public IDLType getReturnType () {
        return op_type_spec;
    }

    public void setParameters (Vector ps) {
        params = ps;
    }

    public Vector getParameters () {
        return params;
    }

    public void setExceptions (Vector es) {
        exceptions = es;
    }

    public Vector getExceptions () {
        return exceptions;
    }


    public void setContexts (Vector cs) {
        contexts = cs;
    }

    public Vector getContexts () {
        return contexts;
    }

    public void jjtClose () {
        super.jjtClose ();
        for (int i=0; i<getMembers ().size (); i++) {
            if (getMember (i) instanceof ParameterElement)
                params.addElement ((ParameterElement)getMember (i));
        }
    }


}




