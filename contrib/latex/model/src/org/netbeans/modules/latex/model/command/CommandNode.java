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

import org.netbeans.modules.latex.model.command.Command.Param;
import org.netbeans.modules.latex.model.command.LaTeXSource;

/**
 *
 * @author Jan Lahoda
 */
public interface CommandNode extends Node {
    
    /** Return the command represented by this node.
     *
     *  @return command represented by this node.
     */
    public Command getCommand();
    
    /** Return how many arguments are represented by this object.
     *  Note that this is not how many arguments is supposed the
     *  command to have.<BR>
     *  Exactly said, the arguments hold in this node are arguments
     *  0-(getArgumentCount()-1) of the command represented by this node.
     *
     *  @return the number of arguments represented by this object
     */
    public int     getArgumentCount();
    
//    public Param   getArgumentDescription(int index);
    
    /** Returns index-th argument.
     *
     *  @param the index (0-getArgumentCount() - 1) of the argument to return.
     *  @return the index-throws argument
     */
    public ArgumentNode getArgument(int index);
    
    /** Check whether the command is overally valid. Particulary following items
     *  should be checked:
     *  <UL>
     *      <LI>Existence of this command.</LI>
     *      <LI>Valid number and type of arguments (incl. missing mandatory
     *          arguments).</LI>
     *      <LI>Validity of arguments.</LI>
     *  </UL>
     *
     *  @return true if the command is valid according to conditions above,
     *          false otherwise.
     */
    public boolean isValid();
    
//    public void setCommand(LaTeXSource.WriteLock lock, Command command);
}
