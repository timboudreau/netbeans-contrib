/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
