/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author Jan Lahoda
 */
public class NamedAttributableWithArguments extends NamedAttributableWithSubElements {
    
    private List arguments;
    
    /*package private*/NamedAttributableWithArguments() {
        arguments = Collections.synchronizedList(new ArrayList());
    }
    
    public List getArguments() {
        return arguments;
    }
    
    public Command.Param getArgument(int index) {
        return (Command.Param) getArguments().get(index);
    }
    
    public int getArgumentCount() {
        return getArguments().size();
    }
    
}
