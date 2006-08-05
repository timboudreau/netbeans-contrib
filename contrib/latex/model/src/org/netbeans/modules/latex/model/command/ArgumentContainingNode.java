/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;
import org.netbeans.modules.latex.model.command.NamedAttributableWithArguments;

/**
 *
 * @author Jan Lahoda
 */
public interface ArgumentContainingNode extends Node {

    /** Return how many arguments are represented by this object.
     *  Note that this is not how many arguments is supposed the
     *  command to have.<BR>
     *  Exactly said, the arguments hold in this node are arguments
     *  0-(getArgumentCount()-1) of the command represented by this node.
     *
     *  @return the number of arguments represented by this object
     */
    public int     getArgumentCount();
    
    /** Returns index-th argument.
     *
     *  @param the index (0-getArgumentCount() - 1) of the argument to return.
     *  @return the index-throws argument
     */
    public ArgumentNode getArgument(int index);
    
    public NamedAttributableWithArguments getArgumentsSpecification();
    
}
