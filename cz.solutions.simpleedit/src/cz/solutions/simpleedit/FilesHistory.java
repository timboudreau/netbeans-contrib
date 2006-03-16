/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Simple Edit Module.
 * The Initial Developer of the Original Code is Internet Solutions s.r.o.
 * Portions created by Internet Solutions s.r.o. are
 * Copyright (C) Internet Solutions s.r.o..
 * All Rights Reserved.
 * 
 * Contributor(s): David Strupl.
 */

package cz.solutions.simpleedit;

import java.util.Enumeration;
import java.util.Stack;
import java.util.logging.Logger;
import org.openide.loaders.DataObject;

/**
 * Keeps history of opened files.
 * @author David Strupl
 */
public class FilesHistory {
    
    private static Logger logger = Logger.getLogger(FilesHistory.class.getName());
    private static FilesHistory instance = new FilesHistory();
    
    private Stack history = new Stack();
    private boolean ignoreNext = false;
    
    
    /** Creates a new instance of FilesHistory */
    private FilesHistory() {
    }
    
    public static FilesHistory getDefault() {
        return instance;
    }
    
    public void removeDataObject(DataObject dobj) {
        history.remove(dobj);
    }
    
    public void addDataObject(DataObject dobj) {
        if (ignoreNext) {
            ignoreNext = false;
            return;
        }
        if (history.contains(dobj)) {
            return;
        }
        logger.fine("adding " + dobj); // NOI18N
        history.push(dobj);
    }
    
    public boolean canNavigateBack() {
        logger.fine("canNavigateBack");  // NOI18N
        printDebugInfo();
        return ! history.isEmpty();
    }
    
    public DataObject navigateBack() {
        logger.fine("navigateBack");  // NOI18N
        printDebugInfo();
        // if we go back we don't want the returned to file
        // to be put into our stack
        ignoreNext = true;
        return (DataObject)history.pop();
    }
    
    private void printDebugInfo() {
        for (Enumeration en = history.elements(); en.hasMoreElements(); ) {
            logger.fine(en.nextElement().toString());
        }
    }
}
