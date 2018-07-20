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

package org.netbeans.modules.corba.browser.ns;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import java.io.*;
import java.net.*;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;

import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.settings.*;
/*
 * @author Karel Gardas
 */

public class ObjectNode extends NamingServiceNode implements Node.Cookie {

    static final String ICON_BASE
    = "org/netbeans/modules/corba/browser/ns/resources/interface";

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    private Binding binding;

    public ObjectNode () {
        super (Children.LEAF);
        //super (Children.LEAF);
        init ();
    }

    public ObjectNode (Binding b, String ref) {
        super (new ObjectNodeChildren());
        binding = b;
        setName (binding.binding_name[0].id);
        setKind (binding.binding_name[0].kind);
        setIOR (ref);
        init ();
    }

    public void init () {
        if (DEBUG)
            System.out.println ("ObjectNode () :-)");
        setDisplayName (getName ());
        setIconBase (ICON_BASE);

        systemActions = new SystemAction[] {
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.UnbindObject.class),
                            null,
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.CopyClientCode.class),
                            null,
                            SystemAction.get(org.openide.actions.PropertiesAction.class)
                        };
    }


    public Node.Cookie getCookie(Class c) {
        if (c.isInstance(this))
            return this;
        else
            return super.getCookie(c);
    }

    public void unbind () {
        NameComponent name_component = new NameComponent (getName (), getKind ()); // name, kind
        NameComponent[] context_name = new NameComponent[1];
        context_name[0] = name_component;
        try {
            ((ContextNode)getParentNode ()).getContext ().unbind (context_name);
            ((ContextChildren)((ContextNode)getParentNode ()).getChildren ()).addNotify ();
        } catch (Exception e) {
            TopManager.getDefault().notify (new NotifyDescriptor.Message (e.toString(), NotifyDescriptor.Message.ERROR_MESSAGE));
        }
    }
    
    public org.omg.CORBA.InterfaceDef createInterface () {
        try {
            if (this.getIOR() != null) {
                org.omg.CORBA.Object ref = this.getORB().string_to_object (getIOR());
                return org.omg.CORBA.InterfaceDefHelper.narrow (ref._get_interface_def ());
            }
            return null;
        }catch (Exception e) {
            return null;
        }
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ObjectNode.class.getName());
    }

}

/*
 * $Log
 * $
 */
