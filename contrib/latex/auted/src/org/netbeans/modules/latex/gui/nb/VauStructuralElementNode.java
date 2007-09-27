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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui.nb;

import java.beans.IntrospectionException;
import javax.swing.Action;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.GoToSourceAction;
import org.netbeans.modules.latex.model.structural.StructuralNode;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.cookies.OpenCookie;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class VauStructuralElementNode extends StructuralNode {
    
    public VauStructuralElementNode(VauStructuralElement element) throws IntrospectionException {
//        this(element, new InstanceContent());
//    }
//    
//    private VauStructuralElementNode(VauStructuralElement element, InstanceContent ic) throws IntrospectionException {
//        super(element, element.isValid() ? new AbstractLookup(ic) : Lookup.EMPTY);
        super(element);
        
//        ic.add(new OpenCookieImpl());
        
        setIconBase("org/netbeans/modules/latex/gui/nb/autedit_icon");
        setDisplayName(element.getCaption());
    }

    @Override
    public Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(OpenAction.class),
            null,
            SystemAction.get(GoToSourceAction.class),
            null,
            SystemAction.get(PropertiesAction.class),
        };
    }

    private class OpenCookieImpl implements OpenCookie {
        
        public void open() {
            VauElementTopComponent.openComponentForElement((VauStructuralElement) getBean());
        }
        
    }
    
    public SourcePosition getOpeningPosition() {
        return ((VauStructuralElement) getBean()).getStart();
    }

}
