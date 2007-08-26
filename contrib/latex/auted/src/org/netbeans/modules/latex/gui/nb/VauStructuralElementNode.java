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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
        this(element, new InstanceContent());
    }
    
    private VauStructuralElementNode(VauStructuralElement element, InstanceContent ic) throws IntrospectionException {
        super(element, element.isValid() ? new AbstractLookup(ic) : Lookup.EMPTY);
        
        ic.add(new OpenCookieImpl());
        
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
