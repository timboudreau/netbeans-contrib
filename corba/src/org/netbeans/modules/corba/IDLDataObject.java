/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.corba;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import com.netbeans.ide.*;
import com.netbeans.ide.cookies.OpenCookie;
import com.netbeans.ide.filesystems.*;
import com.netbeans.ide.loaders.*;
import com.netbeans.ide.windows.*;
import com.netbeans.ide.actions.OpenAction;
import com.netbeans.ide.text.*;
import com.netbeans.ide.util.*;
import com.netbeans.ide.util.actions.*;
import com.netbeans.ide.nodes.Node;
import com.netbeans.ide.nodes.Children;
//import com.netbeans.developer.modules.text.EditorBase;

import com.netbeans.ide.loaders.*;
import com.netbeans.ide.nodes.CookieSet;


/** Object that provides main functionality for idl data loader.
* This class is final only for performance reasons,
* can be unfinaled if desired.
*
* @author Karel Gardas
*/

public class IDLDataObject extends MultiDataObject {

  public IDLDataObject (final FileObject obj, final MultiFileLoader loader)
                       throws DataObjectExistsException {
    super(obj, loader);
    // use editor support
    MultiDataObject.Entry entry = getPrimaryEntry ();
    CookieSet cookies = getCookieSet ();


    cookies.add(new EditorSupport (entry));
    cookies.add (new CompilerSupport.Compile (entry));
    FileUtil.setMIMEType ("idl", "text/plain");

  }

  /** Provides node that should represent this data object. When a node for representation
  * in a parent is requested by a call to getNode (parent) it is the exact copy of this node
  * with only parent changed. This implementation creates instance
  * <CODE>DataNode</CODE>.
  * <P>
  * This method is called only once.
  *
  * @return the node representation for this data object
  * @see DataNode
  */
  protected Node createNodeDelegate () {
    //return new DataNode (this, Children.LEAF);
    return new IDLNode(this);
  }

  /** Help context for this object.
  * @return help context
  */
  public HelpCtx getHelpCtx () {
    return HelpCtx.DEFAULT_HELP;
  }

}

/*
 * <<Log>>
 *  4    Gandalf   1.3         5/8/99   Karel Gardas    
 *  3    Gandalf   1.2         4/29/99  Ian Formanek    Fixed to compile
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */

