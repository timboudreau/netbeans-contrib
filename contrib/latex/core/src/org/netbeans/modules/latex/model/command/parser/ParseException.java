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
package org.netbeans.modules.latex.model.command.parser;

import java.io.IOException;

/**
 *
 * @author Jan Lahoda
 */
public class ParseException extends /*Runtime*/Exception {
    
    private static String read(ParserInput pi) throws IOException {
        StringBuffer b = new StringBuffer();
        
        for (int cntr = 0; cntr < 10 && pi.hasNext(); cntr++) {
            b.append(pi.next());
        }
        
        return b.toString();
    }
    /** Creates a new instance of ParseException */
    public ParseException(ParserInput pi) throws IOException {
        super(pi == null ? "" : "Error at position: " + pi.getPosition().toString() + ", current context: " + read(pi));
    }
    
    public ParseException(Throwable t) {
        super(t);
    }

}
