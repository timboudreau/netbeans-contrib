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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.spi.editor.hints.ErrorDescription;


/**
 *
 * @author Jan Lahoda
 */
public final class VauParser extends DelegatedParser {

    private List<ErrorDescription> errors;
    
    public VauParser() {
    }

    public StructuralElement getElement(Node node) {
        if (errors == null) {
            errors = new LinkedList<ErrorDescription>();
        }
        
        if (node instanceof CommandNode) {
            return new VauStructuralElement((CommandNode) node, errors);
        }
        
        return null;
    }
    
    public String[] getSupportedAttributes() {
        return new String[] {"#vcdraw-command"};
    }

    @Override
    public Collection<ErrorDescription> getErrors() {
        return errors == null ? Collections.<ErrorDescription>emptyList() : errors;
    }

    @Override
    public void parsingFinished() {
        super.parsingFinished();
        errors = null;
    }
    
}
