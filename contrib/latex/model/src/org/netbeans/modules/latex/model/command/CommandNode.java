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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

/**
 *
 * @author Jan Lahoda
 */
public interface CommandNode extends ArgumentContainingNode {
    
    /** Return the command represented by this node.
     *
     *  @return command represented by this node.
     */
    public Command getCommand();
    
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
