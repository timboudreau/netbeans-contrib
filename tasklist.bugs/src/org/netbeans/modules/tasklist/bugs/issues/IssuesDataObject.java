/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
 
package org.netbeans.modules.tasklist.bugs.issues;

import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;


/**
 * Represents a tasklist object in the Repository.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public class IssuesDataObject extends MultiDataObject implements OpenCookie {

    private static final long serialVersionUID = 1;

    public IssuesDataObject(FileObject pf, IssuesLoader loader) throws DataObjectExistsException {
	super(pf, loader);
    	CookieSet cookies = getCookieSet();
	cookies.add(this); // OpenCookie
    }
  
    protected Node createNodeDelegate() {
	return new IssuesDataNode(this);
    }

    // Implements OpenCookie
    
    /** Invokes the open action. */
    public void open() {
	IssuesView view = new IssuesView(getPrimaryEntry().getFile());
        view.open();
    }   
}
