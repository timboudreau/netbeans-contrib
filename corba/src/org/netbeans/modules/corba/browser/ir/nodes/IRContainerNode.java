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

/*
 * IRContainerNode.java
 *
 * Created on February 23, 2000, 11:35 AM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.openide.TopManager;
import org.openide.util.datatransfer.ExClipboard;
import java.awt.datatransfer.StringSelection;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;

/**
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public abstract class IRContainerNode extends IRAbstractNode implements Node.Cookie, Generatable {

    /** Creates new IRContainerNode */
    public IRContainerNode(Children children) {
        super(children);
        this.getCookieSet().add(this);
    }


    public void refresh () {
        ((Refreshable)this.getChildren()).createKeys ();
    }

    public void generateCode() {
        

        ExClipboard clipboard = TopManager.getDefault().getClipboard();
        StringSelection genCode = new StringSelection ( this.generateHierarchy ());
        clipboard.setContents(genCode,genCode);
    }

    public void generateCode (PrintWriter out) throws IOException {
        String hierarchy = this.generateHierarchy ();
        out.println (hierarchy);
    }
    
    public abstract org.omg.CORBA.Contained getOwner();

    public SystemAction[] createActions (){
        return new SystemAction[] {
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.GenerateCodeAction.class),
            null,
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.RefreshAction.class),
            null,
            SystemAction.get (org.openide.actions.PropertiesAction.class)
        };
    }

    private String generateHierarchy () {
        Node node = this.getParentNode();
        String code ="";

        // Generate the start of namespace
        ArrayList stack = new ArrayList();
        while ( node instanceof IRContainerNode){
            stack.add(node.getCookie (GenerateSupport.class));
            node = node.getParentNode();
        }
        int size = stack.size();
        org.omg.CORBA.StringHolder currentPrefix = new org.omg.CORBA.StringHolder ();
        for (int i = size -1 ; i>=0; i--)
            code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1), currentPrefix);

        // Generate element itself
        code = code + ((GenerateSupport)this.getCookie (GenerateSupport.class)).generateSelf(size, currentPrefix);
        //Generate tail of namespace
        for (int i = 0; i< stack.size(); i++)
            code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
        return code;
    }

}
