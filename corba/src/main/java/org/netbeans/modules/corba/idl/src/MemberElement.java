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

public class MemberElement extends IDLElement {

    IDLType type;

    static final long serialVersionUID =225631314107467399L;
    public MemberElement (int id) {
        super(id);
    }

    public MemberElement (IDLParser p, int id) {
        super(p, id);
    }

    public void setType (IDLType t) {
        type = t;
    }

    public IDLType getType () {
        return type;
    }
    /*
      public void jjtSetParent (Node n) {
      super.jjtSetParent (n);
      }
    */
    public void jjtClose () {
        //System.out.println ("MemberElement.jjtClose ()"); // NOI18N
        super.jjtClose ();
        // remove all children of type Identifier
        /*
        java.util.Vector tmp_members = getMembers ();
        for (int i=0; i<tmp_members.size (); i++) {
        if (tmp_members.elementAt (i) instanceof Identifier) {
        tmp_members.removeElementAt (i);
        System.out.println ("remove element at " + i + " from " + getType ());
    }
    }
        */
        /*
        public void jjtSetParent (Node n) {
        super.jjtSetParent (n);
        */
        /*
        if (getMember (getMembers ().size () - 1) instanceof Identifier)
        setName (((Identifier)getMember (getMembers ().size () - 1)).getName ());
        */
        if (getMember (0) instanceof DeclaratorElement) {
            for (int i = 0; i<getMembers ().size (); i++) {
                //System.out.println (((DeclaratorElement)getMember (i)).getName ()
                //		+ " set type " + getType ()); // NOI18N
                ((DeclaratorElement)getMember (i)).setType (getType ());
            }
        }
        if (getMember (0) instanceof TypeElement && !(getMember (0) instanceof DeclaratorElement)) {
            // first is struct, enum or union
            //Type type = ((TypeElement)getMember (0)).getType ();
            IDLType type = new IDLType (-1, ((TypeElement)getMember (0)).getName ());
            for (int i = 1; i<getMembers ().size (); i++)
                ((DeclaratorElement)getMember (i)).setType (type);
        }
        if (getMember (0) instanceof Identifier) {
            //String type = ((Identifier)getMember (0)).getName ();
            for (int i = 1; i<getMembers ().size (); i++)
                if (getMember (i) instanceof DeclaratorElement) {
                    // this is because of scoped names in Member
                    //((DeclaratorElement)getMember (i)).setType (type);
                    ((DeclaratorElement)getMember (i)).setType (getType ());
                }
        }


    }

}



