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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.checkstyle;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public final class InsertStringSuggestionPerformer extends AbstractSuggestionPerformer {

    
    private String insert;
    
    /** Creates a new instance of InsertSpaceSuggestionPerformer */
    public InsertStringSuggestionPerformer(
            final Document doc,
            final int lineno,
            final int column) {

        super(doc, lineno, column);
    }

    public InsertStringSuggestionPerformer setString(final String insert){
        this.insert = insert;
        return this;
    }
    
    /** Now insert the method javadoc. **/
    protected void performImpl(final int docPosition) throws BadLocationException {

        if( insert == null ){
            throw new IllegalStateException("Must assign insert first.");
        }
        // TODO Get the setting for how many spaces to indent.
        doc.insertString(docPosition , insert, null);
    }

}
