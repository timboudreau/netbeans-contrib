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
