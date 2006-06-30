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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.bibtex;

import java.util.Collection;
import java.util.EventObject;

/**
 *
 * @author Jan Lahoda
 */
public class BiBTeXModelChangedEvent extends EventObject {

    public static final int ENTRIES_ADDED = 1;
    public static final int ENTRIES_REMOVED = 2;

    private int type;
    private Collection entries;

    /** Creates a new instance of BiBTeXModelChangedEvent */
    public BiBTeXModelChangedEvent(BiBTeXModel source, int type, Collection entries) {
        super(source);
        this.type = type;
        this.entries = entries;
    }
    
    public int getType() {
        return type;
    }
    
    public Collection getEntries() {
        return entries;
    }
    
}
