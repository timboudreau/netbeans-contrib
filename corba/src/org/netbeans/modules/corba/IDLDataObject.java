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
import java.util.*;


import org.openide.*;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.CompilerCookie;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.windows.*;
import org.openide.actions.OpenAction;
import org.openide.text.*;
import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
//import com.netbeans.developer.modules.text.EditorBase;

import org.openide.loaders.*;
import org.openide.nodes.CookieSet;

import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.compiler.ExternalCompiler;
import org.openide.execution.NbProcessDescriptor;


import com.netbeans.enterprise.modules.corba.settings.*;
import com.netbeans.enterprise.modules.corba.idl.src.*;
import com.netbeans.enterprise.modules.corba.idl.generator.*;

/** Object that provides main functionality for idl data loader.
* This class is final only for performance reasons,
* can be unfinaled if desired.
*
* @author Karel Gardas
*/

public class IDLDataObject extends MultiDataObject {

  //public static final boolean DEBUG = true;
  private static final boolean DEBUG = false;

  private static final int STATUS_OK = 0;
  private static final int STATUS_ERROR = 1;

  private static final int STYLE_NOTHING = 0;
  private static final int STYLE_FIRST_LEVEL = 1;
  private static final int STYLE_FIRST_LEVEL_WITH_NESTED_TYPES = 2;
  private static final int STYLE_ALL = 3;

  private int status;
  private IDLElement src;

  //private Vector idlConstructs;
  //private Vector idlInterfaces;
  private Hashtable possibleNames;

  private MultiFileLoader idl_loader;
  private IDLParser parser;

  private IDLNode idlNode;

  private ImplGenerator generator;

  public IDLDataObject (final FileObject obj, final MultiFileLoader loader)
    throws DataObjectExistsException {
    super(obj, loader);

    if (DEBUG)
      System.out.println ("IDLDataObject::IDLDataObject (...)");
    idl_loader = loader;
    // use editor support
    MultiDataObject.Entry entry = getPrimaryEntry ();
    CookieSet cookies = getCookieSet ();

    cookies.add (new EditorSupport (entry));
    cookies.add (new IDLCompilerSupport.Compile (entry));
    // added for implementation generator
    cookies.add (new IDLNodeCookie () {
      public void GenerateImpl (IDLDataObject ido) {
	ido.generateImplementation ();
	/*
	  CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	  (CORBASupportSettings.class, true);
	  if (css.getOrb () == null) {
	  new NotSetuped ();
	  return;
	  }
	  
	  if (DEBUG)
	  System.out.println ("generating of idl implemenations...");
	  generator = new ImplGenerator (ido);
	  generator.setSources (getSources ());
	  // genearte method can return JavaDataObject in near future to Open generated file
	  // in editor
	  generator.generate ();
	  
	  //CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	  //(CORBASupportSettings.class, true);
	  //css.loadImpl ();
	  //css.setJavaTemplateTable ();
	*/
      }
    });

    FileUtil.setMIMEType ("idl", "text/x-idl");
    getPrimaryFile().addFileChangeListener (new FileListener ());
    /*
      startParsing ();
      getIdlConstructs ();
      getIdlInterfaces ();
      createPossibleNames ();
    */
    update ();
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
    try {
      idlNode = new IDLNode (this);
      idlNode.update ();
    }	catch (Exception e) {
      e.printStackTrace ();
    }
    return idlNode;
  }

  /** Help context for this object.
   * @return help context
   */
  public HelpCtx getHelpCtx () {
    return HelpCtx.DEFAULT_HELP;
  }


  public Compiler createCompiler (CompilerJob job, Class type) {
    if (DEBUG)
      System.out.println ("IDLDataObject.java:112:createCompiler");
    CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
      (CORBASupportSettings.class, true);	   
    if (css.getOrb () == null) {
      new NotSetuped ();
      return null;
    }
    ExternalCompiler.ErrorExpression eexpr = new ExternalCompiler.ErrorExpression 
      ("blabla", css.getErrorExpression (), css.file (), 
       css.line (), css.column (), css.message ());

    FileObject fo = this.getPrimaryFile ();
    NbProcessDescriptor nb = css.getIdl ();
    ExternalCompiler ec = new IDLExternalCompiler (job, this.getPrimaryFile (), type, nb, eexpr);

    return ec;
  }

  private Vector getIdlConstructs (int style, IDLElement src) {
    Vector constructs = new Vector ();
    String name;
    Vector type_members;
    Vector tmp_members;
    Vector members;
    if (src != null) {
      members = src.getMembers ();
      if (style == STYLE_ALL) {
	for (int i = 0; i<members.size (); i++) {
	  if (members.elementAt (i) instanceof Identifier) {
	    // identifier
	    constructs.add (((Identifier)members.elementAt (i)).getName ());
	  }
	  else {
	    // others
	    constructs.addAll (getIdlConstructs (style, (IDLElement)members.elementAt (i)));
	  }
	}
      }
      if (style == STYLE_NOTHING) {
      }
      if (style == STYLE_FIRST_LEVEL) {
	for (int i=0; i<members.size (); i++) {
	  if (members.elementAt (i) instanceof TypeElement) {
	    tmp_members = ((IDLElement)members.elementAt (i)).getMembers ();
	    for (int j=0; j<tmp_members.size (); j++) {
	      if (((IDLElement)members.elementAt (i)).getMember (j) instanceof Identifier)
		// identifier
		name = ((IDLElement)members.elementAt (i)).getMember (j).getName ();
	      else
		// constructed type => struct, union, enum
		name = ((TypeElement)members.elementAt (i)).getMember (j).getName ();
	      constructs.addElement (name);
	    }
	  }
	  else {
	    name = ((IDLElement)members.elementAt (i)).getName ();
	    constructs.addElement (name);
	  }
	}
      }
      if (style == STYLE_FIRST_LEVEL_WITH_NESTED_TYPES) {
	for (int i=0; i<members.size (); i++) {
	  if (members.elementAt (i) instanceof TypeElement) {
	    constructs.addAll (getIdlConstructs (STYLE_ALL, 
						 (TypeElement)members.elementAt (i)));
	  }
	  else {
	    name = ((IDLElement)members.elementAt (i)).getName ();
	    constructs.addElement (name);
	  }
	}
	    
      }
    }
    return constructs;
  }

  private Vector getIdlConstructs (int style) {
    if (DEBUG)
      System.out.println ("IDLDataObject.getIdlConstructs ()...");
    /*
      Vector idl_constructs = new Vector ();
      String name;
      Vector type_members;
      Vector tmp_members;
      if (src != null) {
      //tmp_members = src.getMembers ();
      if (DEBUG)
      System.out.println ("src: " + src.getMembers ());
      for (int i=0; i<src.getMembers ().size (); i++) {
      if (src.getMember (i) instanceof TypeElement) {
      tmp_members = src.getMember (i).getMembers ();
      for (int j=0; j<tmp_members.size (); j++) {
      if (src.getMember (i).getMember (j) instanceof Identifier)
      // identifier
      name = src.getMember (i).getMember (j).getName ();
      else
      // constructed type => struct, union, enum
      name = ((TypeElement)src.getMember (i).getMember (j)).getName ();
      idl_constructs.addElement (name);
      }
      }
      else {
      name = src.getMember (i).getName ();
      idl_constructs.addElement (name);
      }
	    
      }
      if (DEBUG) {
      for (int i=0; i<idl_constructs.size (); i++)
      System.out.println ("construct: " + (String)idl_constructs.elementAt (i));
      }
      }
    */
      
    return getIdlConstructs (style, src);
    //return idl_constructs;
  }

  private Vector getIdlInterfaces (int style) {
    if (DEBUG)
      System.out.println ("IDLDataObject.getIdlInterfaces (" + style + ");");
    // wrapper
    return getIdlInterfaces (src, style);
  }

  private Vector getIdlInterfaces (IDLElement element, int style) {
    if (DEBUG)
      System.out.println ("IDLDataObject.getIdlInterfaces (" + element + ", " + style + ");");
    Vector idl_interfaces = new Vector ();
    String name;
    Vector type_members;
    Vector tmp_members;
    if (style == STYLE_NOTHING) {
      return idl_interfaces;
    }
    if (style == STYLE_FIRST_LEVEL) {
      if (element != null) {
	//tmp_members = src.getMembers ();
	if (DEBUG)
	  System.out.println ("element: " + element.getMembers ());
	for (int i=0; i<element.getMembers ().size (); i++) {
	  if (element.getMember (i) instanceof InterfaceElement) {
	    name = element.getMember (i).getName ();
	    idl_interfaces.addElement (name);
	  }
	}
	if (DEBUG) {
	  for (int i=0; i<idl_interfaces.size (); i++)
	    System.out.println ("interface: " + (String)idl_interfaces.elementAt (i));
	}
      }
    }
    if (style == STYLE_ALL) {
      if (element != null) {
	//tmp_members = element.getMembers ();
	if (DEBUG)
	  System.out.println ("element: " + element.getMembers ());
	for (int i=0; i<element.getMembers ().size (); i++) {
	  if (element.getMember (i) instanceof InterfaceElement) {
	    name = element.getMember (i).getName ();
	    idl_interfaces.addElement (name);
	  }
	  if (element.getMember (i) instanceof ModuleElement) {
	    Vector nested = getIdlInterfaces ((IDLElement)element.getMember (i), STYLE_ALL);
	    if (nested != null)
	      idl_interfaces.addAll (nested);
	  }

	}
	if (DEBUG) {
	  for (int i=0; i<idl_interfaces.size (); i++)
	    System.out.println ("interface: " + (String)idl_interfaces.elementAt (i));
	}
      }
    }
    
    return idl_interfaces;
  }

  public Hashtable createPossibleNames (Vector ic, Vector ii) {
    // ic = idl-constructs ii = idl-interfaces
    Hashtable possible_names = new Hashtable ();
    if (DEBUG)
      System.out.println ("IDLDataObject.createPossibleNames () ...");
    String name;
    // for various idl constructs
    for (int i=0; i<ic.size (); i++) {
      name = (String)ic.elementAt (i);
      if (name != null && (!name.equals (""))) {
	possible_names.put (name + "Holder", "");
	possible_names.put (name + "Helper", "");
	possible_names.put (name, "");
      }
    }
    // for idl interfaces
    for (int i=0; i<ii.size (); i++) {
      name = (String)ii.elementAt (i);
      if (name != null && (!name.equals (""))) {
	//
	// now I comment *tie* names which classes are necesary to instantiate in server
	// and it's better when user can see it in explorer
	//
	possible_names.put ("_" + name + "Stub", "");
	//possible_names.put ("POA_" + name + "_tie", "");
	//possible_names.put ("POA_" + name, "");
	possible_names.put (name + "POA", "");
	//possible_names.put (name + "POATie", "");
	possible_names.put (name + "Operations", "");
	//possible_names.put ("_" + name + "ImplBase_tie", "");
	    
	// for JavaORB
	possible_names.put ("StubFor" + name, "");
	possible_names.put ("_" + name + "ImplBase", "");
	// for VisiBroker
	possible_names.put ("_example_" + name, "");
	//possible_names.put ("_tie_" + name, "");
	possible_names.put ("_st_" + name, "");
	// for OrbixWeb
	possible_names.put ("_" + name + "Skeleton", "");
	possible_names.put ("_" + name + "Stub", "");
	possible_names.put ("_" + name + "Operations", "");
	// for idltojava - with tie
	//possible_names.put ("_" + name + "Tie", "");
	// for hidding folders
	// possible_names.put (name + "Package", "");
      }

    }
    if (DEBUG)
      System.out.println ("possible names for " + getPrimaryFile ().getName () + " : " 
			  + possible_names) ;
    return possible_names;
  }

  public boolean canGenerate (FileObject fo) {
    String name = fo.getName ();
    if (DEBUG)
      System.out.print ("IDLDataObject.canGenerate (" + name + ") ...");
    if (possibleNames.get (name) != null) {
      if (DEBUG)
	System.out.println ("yes");
      return true;
    }
    else {
      if (DEBUG)
	System.out.println ("no");
      return false;
    }
  }

  public Vector getImplementationNames () {
    Vector retval = new Vector ();
    String impl_prefix = null;
    String impl_postfix = null;
    CORBASupportSettings css = (CORBASupportSettings)CORBASupportSettings.findObject 
      (CORBASupportSettings.class, true);
    if (!css.isTie ()) {
      // inheritance based skeletons
      impl_prefix = css.getImplBasePrefix ();
      impl_postfix = css.getImplBasePostfix ();
    }
    else {
      // tie based skeletons
      impl_prefix = css.getTiePrefix ();
      impl_postfix = css.getTiePostfix ();
    }
    Vector int_names = getIdlInterfaces (STYLE_ALL);
    for (int i=0; i<int_names.size (); i++) {
      retval.add (impl_prefix + (String)int_names.elementAt (i) + impl_postfix);
    }
    return retval;
  }

  
  public int hasGeneratedImplementation () {
    if (DEBUG)
      System.out.println ("hasGeneratedImplementation ()");
    int retval = 0;
    Vector names = getImplementationNames ();
    if (DEBUG)
      System.out.println ("names: " + names + " of size: " + names.size ());
    FileObject ifo_folder = getPrimaryFile ().getParent ();
    for (int i=0; i<names.size (); i++) {
      if (ifo_folder.getFileObject ((String)names.elementAt (i), "java") != null) {
	//System.out.println ("find file: " + ifo_folder.getFileObject 
	//		    ((String)names.elementAt (i), "java"));
	if (retval == 0 && i == 0) {
	  retval = 2;
	  continue;
	}
	if (retval == 0) {
	  retval = 1;
	  continue;
	}
      }
      else {
	if (retval != 0)
	  retval = 1;
      }
    }
    //System.out.println ("-> " + retval);
    return retval;
  }

  public void update () {
    if (DEBUG)
      System.out.println ("IDLDataObject.update ()...");
    // clearing MultiDataObject secondary entries

    Set entries = secondaryEntries ();
    Iterator iter = entries.iterator ();
    //entries.clear ();
    for (int i=0; i<entries.size (); i++) {
      Object o = iter.next ();
      if (DEBUG)
	System.out.println ("removing: " + o);
      removeSecondaryEntry ((MultiDataObject.Entry) o);
    }

    startParsing ();

    //getIdlConstructs ();
    //getIdlInterfaces ();
    /*
      possibleNames = createPossibleNames (getIdlConstructs (STYLE_NOTHING), 
      getIdlInterfaces (STYLE_NOTHING));
    */
    possibleNames = createPossibleNames (getIdlConstructs (STYLE_FIRST_LEVEL_WITH_NESTED_TYPES), 
					 getIdlInterfaces (STYLE_FIRST_LEVEL));


    FileObject tmp_file = null;
    FileLock lock = null;
    /*
      try {
      tmp_file = getPrimaryFile ().getParent ().createData ("for_sucessfull_update", "java");
      //tmp_file.delete (tmp_file.lock ());
      //tmp_file = getPrimaryFile ().getParent ().createData ("for_sucessfull_update", "java");
      lock = tmp_file.lock ();
      tmp_file.delete (lock);
	    
      } catch (IOException e) {
      e.printStackTrace ();
      //} catch (FileAlreadyLockedException e) {
      //e.printStackTrace ();
      } finally {
      if (DEBUG)
      System.out.println ("release lock");
      if (lock != null)
      lock.releaseLock ();
      }
    */
    /*
      //getPrimaryFile ().getParent ().refresh ();
      try {
      getPrimaryFile ().getParent ().setAttribute ("update", ":-))");
      } catch (IOException e) {
      e.printStackTrace ();
      }
    */
  }
  public void startParsing () {
    parse ();

    //((Element)src).xDump (" ");
    /*
      if (src != null)
      createKeys ();
      else
      setKeys (new Vector ());
    */
  }      

  public void parse () {
    try {
      parser = new IDLParser (getPrimaryFile ().getInputStream ());
      if (DEBUG)
	System.out.println ("parsing of " + getPrimaryFile ().getName ());
      src = (IDLElement)parser.Start ();
      if (idlNode != null)
	idlNode.setIconBase (IDLNode.IDL_ICON_BASE);
      status = STATUS_OK;
      if (DEBUG)
	src.dump ("");
      if (DEBUG)
	System.out.println ("parse OK :-)");
    } catch (ParseException e) {
      if (DEBUG)
	System.out.println ("parse exception");
      if (idlNode != null)
	idlNode.setIconBase (IDLNode.IDL_ERROR_ICON);
      status = STATUS_ERROR;
      src = null;
    } catch (TokenMgrError e) {
      if (idlNode != null)
	idlNode.setIconBase (IDLNode.IDL_ERROR_ICON);
      if (DEBUG)
	System.out.println ("parser error!!!");
      src = null;
    } catch (java.io.FileNotFoundException e) {
      e.printStackTrace ();
    }
  }
   
  public IDLElement getSources () {
    return src;
  }
   
  class FileListener extends FileChangeAdapter {
    public void fileChanged (FileEvent e) {
      if (DEBUG)
	System.out.println ("idl file was changed.");
      //IDLDataObject.this.handleFindDataObject (
      //IDLDataObject.this.startParsing ();
      IDLDataObject.this.update ();
      IDLDataObject.this.idlNode.update ();
      CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	(CORBASupportSettings.class, true);
      if (css.getSynchro () == CORBASupport.SYNCHRO_ON_SAVE) 
	IDLDataObject.this.generateImplementation ();
    }
      
    public void fileRenamed (FileRenameEvent e) {
      if (DEBUG)
	System.out.println ("IDLDocumentChildren.FileListener.FileRenamed (" + e + ")");
    }
  }

  public Hashtable getPossibleNames () {
    return possibleNames;
  }

  public Vector getGeneratedFileObjects () {
    Vector result = new Vector ();
    Hashtable h = getPossibleNames ();
    Enumeration enum = h.keys ();
    FileObject folder = this.getPrimaryFile ().getParent ();
    FileObject gen_file;
    while (enum.hasMoreElements ()) {
      gen_file = folder.getFileObject ((String)enum.nextElement (), "java");
      if (DEBUG)
	if (gen_file != null)
	  System.out.println ("add fo: " + gen_file.getName ());
      if (gen_file != null)
	result.add (gen_file);
    }
    return result;
  }


  public void generateImplementation () {
    CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
      (CORBASupportSettings.class, true);
    if (css.getOrb () == null) {
      new NotSetuped ();
      return;
    }

    if (DEBUG)
      System.out.println ("generating of idl implemenations...");
    generator = new ImplGenerator (this);
    generator.setSources (getSources ());
    // genearte method can return JavaDataObject in near future to Open generated file
    // in editor
    generator.generate ();
  }

}

/*
 * <<Log>>
 *  19   Gandalf   1.18        11/4/99  Karel Gardas    - update from CVS
 *  18   Gandalf   1.17        11/4/99  Karel Gardas    update from CVS
 *  17   Gandalf   1.16        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  16   Gandalf   1.15        10/5/99  Karel Gardas    update from CVS
 *  15   Gandalf   1.14        10/1/99  Karel Gardas    updates from CVS
 *  14   Gandalf   1.13        8/7/99   Karel Gardas    changes in code which 
 *       hide generated files
 *  13   Gandalf   1.12        8/3/99   Karel Gardas    
 *  12   Gandalf   1.11        7/10/99  Karel Gardas    
 *  11   Gandalf   1.10        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  10   Gandalf   1.9         6/4/99   Karel Gardas    
 *  9    Gandalf   1.8         5/28/99  Karel Gardas    
 *  8    Gandalf   1.7         5/28/99  Karel Gardas    
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/22/99  Karel Gardas    
 *  5    Gandalf   1.4         5/15/99  Karel Gardas    
 *  4    Gandalf   1.3         5/8/99   Karel Gardas    
 *  3    Gandalf   1.2         4/29/99  Ian Formanek    Fixed to compile
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */

