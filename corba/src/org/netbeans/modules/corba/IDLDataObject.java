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

package org.netbeans.modules.corba;

import java.awt.Image;
import java.awt.Toolkit;

import java.lang.reflect.InvocationTargetException;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.LinkedList;

import java.io.IOException;

import org.openide.TopManager;

import org.openide.cookies.OpenCookie;
import org.openide.cookies.CompilerCookie;
import org.openide.cookies.LineCookie;

import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileRenameEvent;

import org.openide.loaders.MultiFileLoader;

import org.openide.actions.OpenAction;

import org.openide.text.PositionRef;
import org.openide.text.Line;

import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;

import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;

import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObjectExistsException;

import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.compiler.ExternalCompiler;

import org.openide.execution.NbProcessDescriptor;

import org.openide.NotifyDescriptor;

import org.netbeans.modules.java.JavaCompilerType;
import org.netbeans.modules.java.JavaExternalCompilerType;

import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettings;

import org.netbeans.modules.corba.idl.src.IDLParser;
import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.Identifier;
import org.netbeans.modules.corba.idl.src.TypeElement;
import org.netbeans.modules.corba.idl.src.InterfaceElement;
import org.netbeans.modules.corba.idl.src.ModuleElement;
import org.netbeans.modules.corba.idl.src.ParseException;
import org.netbeans.modules.corba.idl.src.TokenMgrError;

import org.netbeans.modules.corba.idl.generator.ImplGenerator;

/** Object that provides main functionality for idl data loader.
* This class is final only for performance reasons,
* can be unfinaled if desired.
*
* @author Karel Gardas
*/

public class IDLDataObject extends MultiDataObject {

    static final long serialVersionUID =-7151972557886707595L;

    //public static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    private static final int STATUS_OK = 0;
    private static final int STATUS_ERROR = 1;

    private static final int STYLE_NOTHING = 0;
    private static final int STYLE_FIRST_LEVEL = 1;
    private static final int STYLE_FIRST_LEVEL_WITH_NESTED_TYPES = 2;
    private static final int STYLE_ALL = 3;

    private int status;
    private IDLElement _M_src;

    //private Vector idlConstructs;
    //private Vector idlInterfaces;
    private Hashtable _M_possible_names;

    private MultiFileLoader idl_loader;
    private IDLParser parser;

    private IDLNode idlNode;

    private ImplGenerator generator;

    private PositionRef position_of_element;

    private int _line;
    private int _column;

    private String _M_orb_for_compilation = null;
    private boolean _M_orb_for_compilation_cache = false;

    private boolean _M_generation = false;

    public IDLDataObject (final FileObject obj, final MultiFileLoader loader)
    throws DataObjectExistsException {
        super(obj, loader);

        if (DEBUG)
            System.out.println ("IDLDataObject::IDLDataObject (...)");
        idl_loader = loader;
        // use editor support
        MultiDataObject.Entry entry = getPrimaryEntry ();
        CookieSet cookies = getCookieSet ();

        //cookies.add (new EditorSupport (entry));
        cookies.add (new IDLEditorSupport (entry));
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
        FileObject __pfile = this.getPrimaryFile();
	//FileObject __pparent = __pfile.getParent ();
	__pfile.addFileChangeListener (new FileListener ());
	//if (__pparent != null)
	//__pparent.addFileChangeListener (new FolderListener ());
        /*
          startParsing ();
          getIdlConstructs ();
          getIdlInterfaces ();
          createPossibleNames ();
        */

        this.update ();
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
        if (DEBUG)
            System.out.println ("createNodeDelegate");
        try {
            idlNode = new IDLNode (this);
            idlNode.update ();
            if (status == STATUS_ERROR) {
                if (DEBUG)
                    System.out.println ("set error icon...");
                idlNode.setIconBase (IDLNode.IDL_ERROR_ICON);
            }
        } catch (Exception e) {
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

    public void openAtPosition (int line_pos, int offset) {
        if (DEBUG)
            System.out.println ("openAtPosition (" + line_pos + ", " + offset + ");");
        LineCookie line_cookie = (LineCookie)getCookie (LineCookie.class);
        if (line_cookie != null) {
            Line line = line_cookie.getLineSet().getOriginal (line_pos - 1);
            line.show (Line.SHOW_GOTO, offset - 1);
        }
    }

    public void openAtLinePosition () {
        openAtPosition (_line, 1);
    }

    public void setLinePosition (int line) {
        if (DEBUG)
            System.out.println ("setLinePosition: " + line);
        _line = line;
    }

    public int getLinePosition () {
        if (DEBUG)
            System.out.println ("getLinePosition: " + _line);
        return _line;
    }

    public void setColumnPosition (int column) {
        if (DEBUG)
            System.out.println ("setColumnPosition: " + column);
        _column = column;
    }

    public int getColumnPosition () {
        if (DEBUG)
            System.out.println ("getColumnPosition: " + _column);
        return _column;
    }

    public void setPositionRef (PositionRef ref) {
        if (DEBUG)
            System.out.println ("setPositionRef");
        position_of_element = ref;
    }


    public PositionRef getPositionRef () {
        if (DEBUG)
            System.out.println ("getPositionRef");
        return position_of_element;
    }


    public Compiler createCompiler (CompilerJob __job, Class __type) {
        if (DEBUG)
            System.out.println ("IDLDataObject.java:112:createCompiler");
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                   (CORBASupportSettings.class, true);
        if (css.getOrb () == null) {
            new NotSetuped ();
            return null;
        }

	ORBSettings __setting;
	if (this.getOrbForCompilation () != null) {
	    __setting = css.getSettingByName (this.getOrbForCompilation ());
	}
	else {
	    __setting = css.getActiveSetting ();
	}
	if (DEBUG)
            System.out.println ("IDLDataObject.java:112:createCompiler: orb for compilation:" 
				+ __setting.getName ());
	ExternalCompiler.ErrorExpression __eexpr = new ExternalCompiler.ErrorExpression
	    ("blabla", __setting.getErrorExpression (), 
	     __setting.file (), __setting.line (), 
	     __setting.column (), __setting.message ());

        FileObject __fo = this.getPrimaryFile ();
	NbProcessDescriptor __nb = __setting.getIdl ();
	ExternalCompiler __ec 
	    = new IDLExternalCompiler (this.getPrimaryFile (), __type, __nb, __eexpr);

	__job.add (__ec);

        Vector __gens = getGeneratedFileObjects ();
        //JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
        //JavaCompilerType jct = (JavaCompilerType)js.getCompiler ();
        JavaCompilerType __jct = (JavaCompilerType)TopManager.getDefault ().getServices
	    ().find(JavaExternalCompilerType.class);
        if (DEBUG)
            System.out.println ("generated files: " + __gens);
        FileSystem __fs = null;
        try {
            __fs = getPrimaryFile ().getFileSystem ();
        } catch (FileStateInvalidException __ex) {
            __ex.printStackTrace ();
        }

        String __package_name = "";
        for (int __j = 0; __j < __gens.size (); __j++) {
            if (DEBUG)
                System.out.println ("add compiler to job for "
                                    + ((FileObject)__gens.elementAt (__j)).getName ());

            __package_name = ((FileObject)__gens.elementAt (__j)).getPackageNameExt ('/', '.');

            if (DEBUG)
                System.out.println ("package name: " + __package_name);

            // future extension: jct.prepareIndirectCompiler
            //                    (type, fs, package_name, "text to status line");
            __job.add (__jct.prepareIndirectCompiler (__type, __fs, __package_name));
        }

	return __ec;
    }

    private LinkedList getIdlConstructs (int __style, IDLElement __element) {
        LinkedList __constructs = new LinkedList ();
        String __name;
        Vector __type_members;
        Vector __tmp_members;
        Vector __members;
        if (__element != null) {
            __members = __element.getMembers ();
            if (__style == STYLE_ALL) {
                for (int __i = 0; __i < __members.size (); __i++) {
                    if (__members.elementAt (__i) instanceof Identifier) {
                        // identifier
                        __constructs.add (((Identifier)__members.elementAt (__i)).getName ());
                    }
                    else {
                        // others
                        __constructs.addAll (this.getIdlConstructs 
					     (__style, (IDLElement)__members.elementAt (__i)));
                    }
                }
            }
            if (__style == STYLE_NOTHING) {
            }
            if (__style == STYLE_FIRST_LEVEL) {
                for (int __i = 0; __i < __members.size (); __i++) {
                    if (__members.elementAt (__i) instanceof TypeElement) {
                        __tmp_members = ((IDLElement)__members.elementAt (__i)).getMembers ();
                        for (int __j = 0; __j < __tmp_members.size (); __j++) {
                            if (((IDLElement)__members.elementAt (__i)).getMember (__j) instanceof Identifier)
                                // identifier
                                __name = ((IDLElement)__members.elementAt (__i)).getMember (__j).getName ();
                            else
                                // constructed type => struct, union, enum
                                __name = ((TypeElement)__members.elementAt (__i)).getMember (__j).getName ();
                            __constructs.add (__name);
                        }
                    }
                    else {
                        __name = ((IDLElement)__members.elementAt (__i)).getName ();
                        __constructs.add (__name);
                    }
                }
            }
            if (__style == STYLE_FIRST_LEVEL_WITH_NESTED_TYPES) {
                for (int __i = 0; __i < __members.size (); __i++) {
                    if (__members.elementAt (__i) instanceof TypeElement) {
                        __constructs.addAll 
			    (this.getIdlConstructs 
			     (STYLE_ALL, (TypeElement)__members.elementAt (__i)));
                    }
                    else {
                        __name = ((IDLElement)__members.elementAt (__i)).getName ();
                        __constructs.add (__name);
                    }
                }

            }
        }
        return __constructs;
    }

    private LinkedList getIdlConstructs (int __style) {
        if (DEBUG)
            System.out.println ("IDLDataObject::getIdlConstructs (" + __style + ");");
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

        return this.getIdlConstructs (__style, _M_src);
        //return idl_constructs;
    }

    private LinkedList getIdlInterfaces (int __style) {
        if (DEBUG)
            System.out.println ("IDLDataObject::getIdlInterfaces (" + __style + ");");
        // wrapper
        return this.getIdlInterfaces (_M_src, __style);
    }

    private LinkedList getIdlInterfaces (IDLElement __element, int __style) {
        if (DEBUG)
            System.out.println ("IDLDataObject.getIdlInterfaces (" 
				+ __element + ", " + __style + ");");
        LinkedList __idl_interfaces = new LinkedList ();
        String __name;
        Vector __type_members;
        Vector __tmp_members;
        if (__style == STYLE_NOTHING) {
            return __idl_interfaces;
        }
        if (__style == STYLE_FIRST_LEVEL) {
            if (__element != null) {
                //tmp_members = src.getMembers ();
                if (DEBUG)
                    System.out.println ("element: " + __element.getMembers ());
                for (int __i = 0; __i < __element.getMembers ().size (); __i++) {
                    if (__element.getMember (__i) instanceof InterfaceElement) {
                        __name = __element.getMember (__i).getName ();
                        __idl_interfaces.add (__name);
                    }
                }
                if (DEBUG) {
		    Iterator __iterator = __idl_interfaces.iterator ();
		    while (__iterator.hasNext ()) {
			System.out.println ("interface: " + (String)__iterator.next ());
		    }
		    /*
		      for (int __i = 0; __i < __idl_interfaces.size (); __i++)
		      System.out.println ("interface: " 
		      + (String)__idl_interfaces.elementAt (__i));
		    */
                }
            }
        }
        if (__style == STYLE_ALL) {
            if (__element != null) {
                //tmp_members = element.getMembers ();
                if (DEBUG)
                    System.out.println ("element: " + __element.getMembers ());
                for (int __i = 0; __i < __element.getMembers ().size (); __i++) {
                    if (__element.getMember (__i) instanceof InterfaceElement) {
                        __name = __element.getMember (__i).getName ();
                        __idl_interfaces.add (__name);
                    }
                    if (__element.getMember (__i) instanceof ModuleElement) {
                        LinkedList __nested = this.getIdlInterfaces 
			    ((IDLElement)__element.getMember (__i), STYLE_ALL);
                        if (__nested != null)
                            __idl_interfaces.addAll (__nested);
                    }

                }
                if (DEBUG) {
		    Iterator __iterator = __idl_interfaces.iterator ();
		    while (__iterator.hasNext ()) {
			System.out.println ("interface: " + (String)__iterator.next ());
		    }
		    /*
		      for (int __i = 0; __i < __idl_interfaces.size (); __i++)
		      System.out.println ("interface: " 
		      + (String)__idl_interfaces.elementAt (__i));
		    */
		}
            }
        }

        return __idl_interfaces;
    }

    public Hashtable createPossibleNames (LinkedList __idl_constructs, 
					  LinkedList __idl_interfaces) {
        Hashtable __possible_names = new Hashtable ();
        if (DEBUG)
            System.out.println ("IDLDataObject.createPossibleNames () ...");
        String __name;
        // for various idl constructs
	/*
	  for (int __i = 0; __i < __idl_constructs.size (); __i++) {
	  __name = (String)__idl_constructs.elementAt (i);
	*/
	Iterator __iterator = __idl_constructs.iterator ();
	while (__iterator.hasNext ()) {
	    __name = (String)__iterator.next (); 
            if (__name != null && (!__name.equals (""))) {
                __possible_names.put (__name + "Holder", "");
                __possible_names.put (__name + "Helper", "");
                __possible_names.put (__name, "");
            }
        }
        // for idl interfaces
	/*
	  for (int __i=0; i<__idl_interfaces.size (); __i++) {
	  __name = (String)__idl_interfaces.elementAt (__i);
	*/
	__iterator = __idl_interfaces.iterator ();
	while (__iterator.hasNext ()) {
	    __name = (String)__iterator.next ();
            if (__name != null && (!__name.equals (""))) {
                //
                // now I comment *tie* names which classes are necesary to instantiate in server
                // and it's better when user can see it in explorer
                //
                __possible_names.put ("_" + __name + "Stub", "");
                //possible_names.put ("POA_" + name + "_tie", "");
                //possible_names.put ("POA_" + name, "");
                __possible_names.put (__name + "POA", "");
                //possible_names.put (name + "POATie", "");
                __possible_names.put (__name + "Operations", "");
                //possible_names.put ("_" + name + "ImplBase_tie", "");

                // for JavaORB
                __possible_names.put ("StubFor" + __name, "");
                __possible_names.put ("_" + __name + "ImplBase", "");
                // for VisiBroker
                __possible_names.put ("_example_" + __name, "");
                //possible_names.put ("_tie_" + name, "");
                __possible_names.put ("_st_" + __name, "");
                // for OrbixWeb
                __possible_names.put ("_" + __name + "Skeleton", "");
                __possible_names.put ("_" + __name + "Stub", "");
                __possible_names.put ("_" + __name + "Operations", "");
                // for idltojava - with tie
                //possible_names.put ("_" + name + "Tie", "");
                // for hidding folders
                // possible_names.put (name + "Package", "");
            }

        }
        if (DEBUG)
            System.out.println ("possible names for " + this.getPrimaryFile ().getName () + " : "
                                + __possible_names) ;
        return __possible_names;
    }

    public boolean canGenerate (FileObject __fo) {
        String __name = __fo.getName ();
        if (DEBUG)
            System.out.print ("IDLDataObject.canGenerate (" + __name + ") ...");
        if (_M_possible_names.get (__name) != null) {
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

    public LinkedList getImplementationNames () {
        LinkedList __retval = new LinkedList ();
        String __impl_prefix = null;
        String __impl_postfix = null;
        CORBASupportSettings __css = (CORBASupportSettings)CORBASupportSettings.findObject
	    (CORBASupportSettings.class, true);
	if (!__css.getActiveSetting ().isTie ()) {
	    // inheritance based skeletons
	    __impl_prefix = __css.getActiveSetting ().getImplBasePrefix ();
	    __impl_postfix = __css.getActiveSetting ().getImplBasePostfix ();
	}
	else {
	    // tie based skeletons
	    __impl_prefix = __css.getActiveSetting ().getTiePrefix ();
	    __impl_postfix = __css.getActiveSetting ().getTiePostfix ();
	}
        LinkedList __int_names = this.getIdlInterfaces (STYLE_ALL);
	Iterator __iterator = __int_names.iterator ();
	while (__iterator.hasNext ()) {
	    __retval.add (__impl_prefix + (String)__iterator.next () + __impl_postfix);
	}
        /*
	  for (int i=0; i<int_names.size (); i++) {
	  retval.add (impl_prefix + (String)int_names.elementAt (i) + impl_postfix);
        }
	*/
        return __retval;
    }


    public int hasGeneratedImplementation () {
	//Thread.dumpStack ();
	//try {
	if (DEBUG)
	    System.out.println ("IDLDataObject::hasGeneratedImplementation ();");
	int __retval = 0;
	LinkedList __names = this.getImplementationNames ();
	if (DEBUG)
	    System.out.println ("names: " + __names + " of size: " + __names.size ());
	FileObject __ifo_folder = this.getPrimaryFile ().getParent ();
	/*
	  for (int i=0; i<names.size (); i++) {
	  if (ifo_folder.getFileObject ((String)names.elementAt (i), "java") != null) {
	*/
	Iterator __iterator = __names.iterator ();
	boolean __first = true;
	FileObject __file = null;
	while (__iterator.hasNext ()) {
	    if ((__file = __ifo_folder.getFileObject 
		 ((String)__iterator.next (), "java")) != null) {
		if (DEBUG)
		    System.out.println ("find file: " + __file);
		if (__retval == 0 && __first) {
		    __retval = 2;
		    __first = false;
		    continue;
		}
		if (__retval == 0) {
		    __retval = 1;
		    __first = false;
		    continue;
		}
	    }
	    else {
		if (__retval != 0)
		    __retval = 1;
		__first = false;
	    }
	}
	if (DEBUG)
	    System.out.println ("-> " + __retval);
	return __retval;
	//} catch (Exception __ex) {
	//    __ex.printStackTrace ();
	//}
	//return 0;
    }

    public void update () {
        if (DEBUG)
            System.out.println ("IDLDataObject.update ()...");
        // clearing MultiDataObject secondary entries

        Set __entries = this.secondaryEntries ();
        Iterator __iterator = __entries.iterator ();
        //entries.clear ();
        //for (int i=0; i<entries.size (); i++) {
	while (__iterator.hasNext ()) {
            Object o = __iterator.next ();
            if (DEBUG)
                System.out.println ("removing: " + o);
            this.removeSecondaryEntry ((MultiDataObject.Entry) o);
        }

        this.startParsing ();

        //getIdlConstructs ();
        //getIdlInterfaces ();
        /*
          possibleNames = createPossibleNames (getIdlConstructs (STYLE_NOTHING), 
          getIdlInterfaces (STYLE_NOTHING));
        */
        _M_possible_names = this.createPossibleNames 
	    (this.getIdlConstructs (STYLE_FIRST_LEVEL_WITH_NESTED_TYPES),
	     this.getIdlInterfaces (STYLE_FIRST_LEVEL));


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

        //if (src != null)
        //  src.xDump (" ");
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
            _M_src = (IDLElement)parser.Start ();
            //_M_src.xDump (" ");
            _M_src.setDataObject (this);
            if (idlNode != null)
                idlNode.setIconBase (IDLNode.IDL_ICON_BASE);
            status = STATUS_OK;
            if (DEBUG)
                _M_src.dump ("");
            if (DEBUG)
                System.out.println ("parse OK :-)");
        } catch (ParseException e) {
            if (DEBUG) {
                System.out.println ("parse exception");
                e.printStackTrace ();
            }
            if (idlNode != null) {
                idlNode.setIconBase (IDLNode.IDL_ERROR_ICON);
            } else {
                if (DEBUG)
                    System.out.println ("can't setup error icon!");
            }
            status = STATUS_ERROR;
            _M_src = null;
        } catch (TokenMgrError e) {
            if (idlNode != null) {
                idlNode.setIconBase (IDLNode.IDL_ERROR_ICON);
            } else {
                if (DEBUG)
                    System.out.println ("can't setup error icon!");
            }
            if (DEBUG) {
                System.out.println ("parser error!!!");
                e.printStackTrace ();
            }
            status = STATUS_ERROR;
            _M_src = null;
        } catch (java.io.FileNotFoundException e) {
            // e.printStackTrace ();
        } catch (Exception ex) {
            System.out.println ("IDLParser exception in " + this.getPrimaryFile ());
            ex.printStackTrace ();
        }
    }

    public IDLElement getSources () {
        return _M_src;
    }

    class FileListener extends FileChangeAdapter {
        public void fileChanged (FileEvent e) {
            if (DEBUG)
		System.out.println ("idl file was changed.");
            //IDLDataObject.this.handleFindDataObject (
            //IDLDataObject.this.startParsing ();
            IDLDataObject.this.update ();
	    if (IDLDataObject.this.idlNode != null)
		IDLDataObject.this.idlNode.update ();
            CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                       (CORBASupportSettings.class, true);
	    if (css.getActiveSetting ().getSynchro () == CORBASupport.SYNCHRO_ON_SAVE)
		IDLDataObject.this.generateImplementation ();
        }

        public void fileRenamed (FileRenameEvent e) {
            if (DEBUG)
		System.out.println ("IDLDocumentChildren::FileListener::FileRenamed (" + e + ")");
        }

	public void fileAttributeChanged (org.openide.filesystems.FileAttributeEvent __event) {
	    if (DEBUG)
		System.out.println ("fileAttributeChanged listened: " + __event);
	}

	public void fileDataCreated (FileEvent __event) {
	    if (DEBUG)
		System.out.println ("fileDataCreated listened: " + __event 
				    + ":" + __event.getFile ());
	}

	public void fileDeleted (FileEvent __event) {
	    if (DEBUG)
		System.out.println ("fileDeleted listened: " + __event 
				    + ":" + __event.getFile ());
	}

	public void fileFolderCreated (FileEvent __event) {
	    if (DEBUG)
		System.out.println ("fileFolderCreated listened: " + __event);
	}

    }


    public Hashtable getPossibleNames () {
        return _M_possible_names;
    }

    public Vector getGeneratedFileObjects () {
        Vector result = new Vector ();
        Hashtable h = this.getPossibleNames ();
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
	// this can be done in system RequestProcessor
	//generator.generate ();
	synchronized (this) {
	    if (!_M_generation) {
		_M_generation = true;
		RequestProcessor.postRequest (new Runnable () {
			public void run () {
			    generator.generate ();
			    _M_generation = false;
			}
		    });
	    }
	    else {
		//System.out.println ("can't generate while generating!");
		TopManager.getDefault ().notify (new NotifyDescriptor.Message ("can't generate while generating!"));
	    }
	}
    }

    public void setOrbForCompilation (String __value) throws IOException {
	_M_orb_for_compilation = __value;
	FileObject __idl_file = this.getPrimaryFile ();
	__idl_file.setAttribute ("orb_for_compilation", _M_orb_for_compilation);
    }

    public String getOrbForCompilation () {
	FileObject __idl_file = this.getPrimaryFile ();
	if (!_M_orb_for_compilation_cache) {
	    _M_orb_for_compilation = (String)__idl_file.getAttribute ("orb_for_compilation");
	    _M_orb_for_compilation_cache = true;
	}
	return _M_orb_for_compilation;
    }
   

}

/*
 * <<Log>>
 *  22   Gandalf   1.21        2/8/00   Karel Gardas    
 *  21   Gandalf   1.20        11/27/99 Patrik Knakal   
 *  20   Gandalf   1.19        11/9/99  Karel Gardas    - better exception 
 *       handling for CORBA 2.3 types
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

