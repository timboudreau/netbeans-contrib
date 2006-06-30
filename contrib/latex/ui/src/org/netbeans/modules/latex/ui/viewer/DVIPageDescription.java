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
package org.netbeans.modules.latex.ui.viewer;

import java.util.List;
import org.netbeans.modules.latex.model.platform.FilePosition;

/**
 *
 * @author Jan Lahoda
 */
public class DVIPageDescription {

    private int pageNumber;
    private List<FilePosition> sourcePositions;

    /** Creates a new instance of DVIPageDescription */
    public DVIPageDescription(int pageNumber, List<FilePosition> sourcePositions) {
        this.pageNumber = pageNumber;
        this.sourcePositions = sourcePositions;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public List<FilePosition> getSourcePositions() {
        return sourcePositions;
    }

    public String toString() {
        return pageNumber + ":" + sourcePositions.toString();
    }
}
