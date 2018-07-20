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

import javax.swing.text.Position.Bias;

import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

import org.openide.text.PositionRef;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.IDLNode;
import org.netbeans.modules.corba.IDLEditorSupport;

import org.netbeans.modules.corba.idl.src.IDLElement;
/*
 * @author Karel Gardas
 */

public abstract class IDLAbstractNode extends AbstractNode {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    public IDLAbstractNode (Children children) {
        super (children);
        if (DEBUG)
            System.out.println ("IDLAbstractNode (...)"); // NOI18N
    }

    public void setCookieForDataObject (IDLDataObject ido) {
        CookieSet cookie = getCookieSet ();
        cookie.add (ido.getCookie (IDLEditorSupport.class));
    }

    public SystemAction getDefaultAction () {
        if (DEBUG)
            System.out.println ("getDefaultAction ()"); // NOI18N
        SystemAction result = super.getDefaultAction();
        if (DEBUG)
            System.out.println ("result: " + result); // NOI18N
        //getIDLElement ().getDataObject ().setPositionRef (getPositionRef ());
        getIDLElement ().getDataObject ().setLinePosition (getIDLElement ().getLine ());
        getIDLElement ().getDataObject ().setColumnPosition (getIDLElement ().getColumn ());
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    public String getDisplayName () {
	IDLElement __element = this.getIDLElement ();
        if (__element != null)
            return __element.getName ();
        else
            return ""; // NOI18N
    }

    abstract public IDLElement getIDLElement ();

    //public static int possition (long val) {
    //  return (int)(p & 0xFFFFFFFFL);
    //}

    public PositionRef getPositionRef () {
        int line = getIDLElement ().getLine ();
        if (DEBUG)
            System.out.println ("getPositionRef for line: " + line); // NOI18N
        IDLEditorSupport editor = (IDLEditorSupport)getIDLElement ().getDataObject ().getCookie
                                  (IDLEditorSupport.class);
        return editor.createPositionRef (line, Bias.Forward);
    }

}
/*
 * $Log
 * $
 */

