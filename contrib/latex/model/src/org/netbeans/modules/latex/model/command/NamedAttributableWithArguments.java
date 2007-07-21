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
package org.netbeans.modules.latex.model.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jan Lahoda
 */
public class NamedAttributableWithArguments extends NamedAttributableWithSubElements {

    private List<Command.Param> arguments;

    /*package private*/NamedAttributableWithArguments() {
        arguments = Collections.synchronizedList(new ArrayList<Command.Param>());
    }
    
    public List<Command.Param> getArguments() {
        return arguments;
    }
    
    public Command.Param getArgument(int index) {
        return getArguments().get(index);
    }
    
    public int getArgumentCount() {
        return getArguments().size();
    }
    
}
