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

import java.util.HashSet;
import java.util.Set;
import org.openide.loaders.DataObject;

/**
 *
 * @author David Strupl
 */
public class OpenedFiles {
    
    private static OpenedFiles instance = new OpenedFiles();
    
    private Set opened = new HashSet();
    
    /** Creates a new instance of FilesHistory */
    private OpenedFiles() {
    }
    
    public static OpenedFiles getDefault() {
        return instance;
    }
    
    public void addDataObject(DataObject dobj) {
        opened.add(dobj);
    }
    
    public void removeDataObject(DataObject dobj) {
        opened.remove(dobj);
    }
    
    public boolean keepOpened(DataObject dobj) {
        return opened.contains(dobj);
    }
}
