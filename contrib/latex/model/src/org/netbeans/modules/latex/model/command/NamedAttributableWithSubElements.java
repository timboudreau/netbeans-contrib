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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan Lahoda
 */
public class NamedAttributableWithSubElements extends NamedAttributable {
    
    private Map<String, Environment> environments;
    private Map<String, Command>     commands;
    
    /** Creates a new instance of NamedAttributableWithSubElements */
    /*package private*/ NamedAttributableWithSubElements() {
    }
    
    public synchronized Map<String, Environment> getEnvironments() {
        if (environments == null)
            return environments = new HashMap<String, Environment>();

        return environments;
    }
    
    public synchronized Map<String, Command> getCommands() {
        if (commands == null)
            return commands = new HashMap<String, Command>();

        return commands;
    }

}
