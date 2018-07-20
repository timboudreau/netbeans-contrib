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

package org.netbeans.modules.corba.poasupport.nodes;

import java.io.IOException;
import java.awt.Component;

import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/** A simple node with no children.
 *
 * @author Dusan Balek
 */
public class POAActivatorNode extends POAMemberElementNode {

    public static final String ICON_BASE =
    "/org/netbeans/modules/corba/poasupport/resources/POAActivatorNodeIcon"; // NOI18N

    public POAActivatorNode(POAActivatorElement _element) {
        super (_element);
        setIconBase (ICON_BASE);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // When you have help, change to:
        // return new HelpCtx (ServantNode.class);
    }
    
    public POAActivatorElement getPOAActivatorElement () {
        return (POAActivatorElement)element;
    }
    
    public void setName (String nue) {
        if (isWriteable()) {
            if (POAChecker.checkPOAMemberVarName(nue, getPOAActivatorElement(), (getPOAActivatorElement().getTypeName() != null) && (getPOAActivatorElement().getConstructor() != null), true))
            getPOAActivatorElement().setVarName(nue);
        }
    }

    public void destroy () throws IOException {
        ((POANode)getParentNode()).getPOAElement().removePOAActivator();
        super.destroy ();
    }

    // Create a property sheet:
    protected Sheet createSheet () {
        Sheet sheet = super.createSheet ();
        Sheet.Set props = sheet.get (Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet ();
            sheet.put (props);
        }
        return sheet;
    }
    
    public Component getCustomizer () {
        return new POAMemberCustomizer (getPOAActivatorElement());
    }
    
    // RECOMMENDED - handle cloning specially (so as not to invoke the overhead of FilterNode):
    /*
    public Node cloneNode () {
        // Try to pass in similar constructor params to what you originally got:
        return new ServantNode ();
    }
     */
    
    /*
    public Transferable clipboardCopy () {
        // Add to, do not replace, the default node copy flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCopy ());
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
    public Transferable clipboardCut () {
        // Add to, do not replace, the default node cut flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCut ());
        // This is not so useful because this node will not be destroyed afterwards
        // (it is up to the paste type to decide whether to remove the "original",
        // and it is not safe to assume that getData will only be called once):
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
     */
    
}
