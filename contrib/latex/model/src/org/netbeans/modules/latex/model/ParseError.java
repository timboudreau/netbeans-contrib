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
package org.netbeans.modules.latex.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.ErrorManager;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;

/** This is part of the LaTeX model SPI. All kinds of parsers should use this class
 *  and corresponding methods in {@link org.netbeans.modules.latex.model.Utilities}
 *  to represent user's mistakes in the source code.
 *
 *  <B>NO SOURCE PARSER SHOULD THROW AN EXCEPTION BECAUSE OF MALFORMED SOURCE CODE!</B>
 *
 *  @author Jan Lahoda
 */
public class ParseError {
    
    private String         message;
    private SourcePosition position;
    
    /** Creates a new instance of Error */
    public ParseError(String message, SourcePosition position) {
        assert message != null && position != null : "<null> value for arguments not allowed";
        this.message = message;
        this.position = position;
    }
    
    /** Getter for property message.
     * @return Value of property message.
     *
     */
    public String getMessage() {
        return message;
    }
    
    /** Getter for property position.
     * @return Value of property position.
     *
     */
    public SourcePosition getPosition() {
        return position;
    }

}
