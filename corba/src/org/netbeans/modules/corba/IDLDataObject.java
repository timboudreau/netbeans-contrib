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
import java.io.InputStream;

import org.openide.TopManager;

import org.openide.cookies.OpenCookie;
import org.openide.cookies.CloseCookie;
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
import org.openide.loaders.DataFolder;

import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.compiler.ExternalCompiler;

import org.openide.execution.NbProcessDescriptor;

import org.openide.NotifyDescriptor;

import org.netbeans.modules.java.JavaCompilerType;
import org.netbeans.modules.java.JavaExternalCompilerType;

import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;

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

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_PARSING = 2;
    public static final int STATUS_NOT_PARSED = 3;

    private static final int STYLE_NOTHING = 0;
    private static final int STYLE_FIRST_LEVEL = 1;
    private static final int STYLE_FIRST_LEVEL_WITH_NESTED_TYPES = 2;
    private static final int STYLE_ALL = 3;

    private static RequestProcessor _S_request_processor 
	= new RequestProcessor ("CORBA - IDL Parser"); // NOI18N
    private static RequestProcessor _S_request_processor2 
	= new RequestProcessor ("CORBA - Implementation Generator"); // NOI18N

    private int _M_status;
    private IDLElement _M_src;

    //private Vector idlConstructs;
    //private Vector idlInterfaces;
    private Hashtable _M_possible_names;

    private MultiFileLoader idl_loader;
    //private IDLParser _M_parser;

    private IDLNode _M_idl_node;

    private ImplGenerator generator;

    private PositionRef position_of_element;

    private int _line;
    private int _column;

    private String _M_orb_for_compilation = null;
    private boolean _M_orb_for_compilation_cache = false;

    private boolean _M_generation = false;

    //private FolderListener _M_folder_listener;
    //private FileObject _M_parent_folder;

    public IDLDataObject (final FileObject obj, final MultiFileLoader loader)
    throws DataObjectExistsException {
        super(obj, loader);

        if (DEBUG)
            System.out.println ("IDLDataObject::IDLDataObject (...)"); // NOI18N
        idl_loader = loader;
	_M_status = STATUS_NOT_PARSED;
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

        FileUtil.setMIMEType ("idl", "text/x-idl"); // NOI18N
        FileObject __pfile = this.getPrimaryFile();
	//_M_parent_folder = __pfile.getParent ();
	__pfile.addFileChangeListener (new FileListener ());
	//if (_M_parent_folder != null) {
	//    _M_folder_listener = new FolderListener ();
	//    _M_parent_folder.addFileChangeListener (_M_folder_listener);
	//}
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
	    System.out.println ("createNodeDelegate"); // NOI18N
        try {
            _M_idl_node = new IDLNode (this);
            //if (_M_status == STATUS_OK) {
	    // parser is quicker - so we have parsed idl file
	    _M_idl_node.update ();
	    //}
            if (_M_status == STATUS_ERROR) {
                if (DEBUG)
                    System.out.println ("set error icon..."); // NOI18N
                _M_idl_node.setIconBase (IDLNode.IDL_ERROR_ICON);
            }
        } catch (Exception e) {
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		e.printStackTrace ();
        }
        return _M_idl_node;
    }

    /** Help context for this object.
     * @return help context
     */
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    public int getStatus () {
	return _M_status;
    }

    public void openAtPosition (int line_pos, int offset) {
        if (DEBUG)
            System.out.println ("openAtPosition (" + line_pos + ", " + offset + ");"); // NOI18N
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
            System.out.println ("setLinePosition: " + line); // NOI18N
        _line = line;
    }

    public int getLinePosition () {
        if (DEBUG)
            System.out.println ("getLinePosition: " + _line); // NOI18N
        return _line;
    }

    public void setColumnPosition (int column) {
        if (DEBUG)
            System.out.println ("setColumnPosition: " + column); // NOI18N
        _column = column;
    }

    public int getColumnPosition () {
        if (DEBUG)
            System.out.println ("getColumnPosition: " + _column); // NOI18N
        return _column;
    }

    public void setPositionRef (PositionRef ref) {
        if (DEBUG)
            System.out.println ("setPositionRef"); // NOI18N
        position_of_element = ref;
    }


    public PositionRef getPositionRef () {
        if (DEBUG)
            System.out.println ("getPositionRef"); // NOI18N
        return position_of_element;
    }


    public Compiler createCompiler (CompilerJob __job, Class __type) {
        if (DEBUG)
            System.out.println ("IDLDataObject.java:112:createCompiler"); // NOI18N
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
            System.out.println ("IDLDataObject.java:112:createCompiler: orb for compilation:" // NOI18N
				+ __setting.getName ());
	ExternalCompiler.ErrorExpression __eexpr = new ExternalCompiler.ErrorExpression
	    ("blabla", __setting.getErrorExpression (), // NOI18N
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
            System.out.println ("generated files: " + __gens); // NOI18N
        FileSystem __fs = null;
        try {
            __fs = getPrimaryFile ().getFileSystem ();
        } catch (FileStateInvalidException __ex) {
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		__ex.printStackTrace ();
        }

        String __package_name = ""; // NOI18N
        for (int __j = 0; __j < __gens.size (); __j++) {
            if (DEBUG)
                System.out.println ("add compiler to job for " // NOI18N
                                    + ((FileObject)__gens.elementAt (__j)).getName ());

            __package_name = ((FileObject)__gens.elementAt (__j)).getPackageNameExt ('/', '.');

            if (DEBUG)
                System.out.println ("package name: " + __package_name); // NOI18N

            // future extension: jct.prepareIndirectCompiler
            //                    (type, fs, package_name, "text to status line"); // NOI18N
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
            System.out.println ("IDLDataObject::getIdlConstructs (" + __style + ");"); // NOI18N
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
            System.out.println ("IDLDataObject::getIdlInterfaces (" + __style + ");"); // NOI18N
        // wrapper
        return this.getIdlInterfaces (_M_src, __style);
    }

    private LinkedList getIdlInterfaces (IDLElement __element, int __style) {
        if (DEBUG)
            System.out.println ("IDLDataObject.getIdlInterfaces (" // NOI18N
				+ __element + ", " + __style + ");"); // NOI18N
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
                    System.out.println ("element: " + __element.getMembers ()); // NOI18N
                for (int __i = 0; __i < __element.getMembers ().size (); __i++) {
                    if (__element.getMember (__i) instanceof InterfaceElement) {
                        __name = __element.getMember (__i).getName ();
                        __idl_interfaces.add (__name);
                    }
                }
                if (DEBUG) {
		    Iterator __iterator = __idl_interfaces.iterator ();
		    while (__iterator.hasNext ()) {
			System.out.println ("interface: " + (String)__iterator.next ()); // NOI18N
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
                    System.out.println ("element: " + __element.getMembers ()); // NOI18N
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
			System.out.println ("interface: " + (String)__iterator.next ()); // NOI18N
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
            System.out.println ("IDLDataObject.createPossibleNames () ..."); // NOI18N
        String __name;
        // for various idl constructs
	/*
	  for (int __i = 0; __i < __idl_constructs.size (); __i++) {
	  __name = (String)__idl_constructs.elementAt (i);
	*/
	Iterator __iterator = __idl_constructs.iterator ();
	while (__iterator.hasNext ()) {
	    __name = (String)__iterator.next (); 
            if (__name != null && (!__name.equals (""))) { // NOI18N
                __possible_names.put (__name + "Holder", ""); // NOI18N
                __possible_names.put (__name + "Helper", ""); // NOI18N
                __possible_names.put (__name, ""); // NOI18N
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
            if (__name != null && (!__name.equals (""))) { // NOI18N
                //
                // now I comment *tie* names which classes are necesary to instantiate in server
                // and it's better when user can see it in explorer
                //
                __possible_names.put ("_" + __name + "Stub", ""); // NOI18N
                //possible_names.put ("POA_" + name + "_tie", ""); // NOI18N
                //possible_names.put ("POA_" + name, ""); // NOI18N
                __possible_names.put (__name + "POA", ""); // NOI18N
                //possible_names.put (name + "POATie", ""); // NOI18N
                __possible_names.put (__name + "Operations", ""); // NOI18N
                //possible_names.put ("_" + name + "ImplBase_tie", ""); // NOI18N

                // for JavaORB
                __possible_names.put ("StubFor" + __name, ""); // NOI18N
                __possible_names.put ("_" + __name + "ImplBase", ""); // NOI18N
                // for VisiBroker
                __possible_names.put ("_example_" + __name, ""); // NOI18N
                //possible_names.put ("_tie_" + name, ""); // NOI18N
                __possible_names.put ("_st_" + __name, ""); // NOI18N
                // for OrbixWeb
                __possible_names.put ("_" + __name + "Skeleton", ""); // NOI18N
                __possible_names.put ("_" + __name + "Stub", ""); // NOI18N
                __possible_names.put ("_" + __name + "Operations", ""); // NOI18N
                // for idltojava - with tie
                //possible_names.put ("_" + name + "Tie", ""); // NOI18N
                // for hidding folders
                // possible_names.put (name + "Package", ""); // NOI18N
            }

        }
        if (DEBUG)
            System.out.println ("possible names for " + this.getPrimaryFile ().getName () + " : " // NOI18N
                                + __possible_names) ;
        return __possible_names;
    }

    public boolean canGenerate (FileObject __fo) {
        String __name = __fo.getName ();
        if (DEBUG)
            System.out.print ("IDLDataObject.canGenerate (" + __name + ") ..."); // NOI18N
        if (_M_possible_names.get (__name) != null) {
            if (DEBUG)
                System.out.println ("yes"); // NOI18N
            return true;
        }
        else {
            if (DEBUG)
                System.out.println ("no"); // NOI18N
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
	    System.out.println ("IDLDataObject::hasGeneratedImplementation ();"); // NOI18N
	int __retval = 0;
	LinkedList __names = this.getImplementationNames ();
	if (DEBUG)
	    System.out.println ("names: " + __names + " of size: " + __names.size ()); // NOI18N
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
		 ((String)__iterator.next (), "java")) != null) { // NOI18N
		if (DEBUG)
		    System.out.println ("find file: " + __file); // NOI18N
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
	    System.out.println ("-> " + __retval); // NOI18N
	return __retval;
	//} catch (Exception __ex) {
	//    __ex.printStackTrace ();
	//}
	//return 0;
    }

    public void update () {
        if (DEBUG)
            System.out.println ("IDLDataObject.update ()..."); // NOI18N
        // clearing MultiDataObject secondary entries

        Set __entries = this.secondaryEntries ();
        Iterator __iterator = __entries.iterator ();
        //entries.clear ();
        //for (int i=0; i<entries.size (); i++) {
	while (__iterator.hasNext ()) {
            Object o = __iterator.next ();
            if (DEBUG)
                System.out.println ("removing: " + o); // NOI18N
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
	if (DEBUG)
	    System.out.println ("IDLDataObject::startParsing ();"); // NOI18N
	_M_status = STATUS_PARSING;
	this.firePropertyChange ("_M_status", null, null); // NOI18N
        _S_request_processor.post (new Runnable () {
		public void run () {
		    parse ();
		}
	    }, 0, Thread.MIN_PRIORITY);
        //if (src != null)
        //  src.xDump (" "); // NOI18N
        /*
          if (src != null)
          createKeys ();
          else
          setKeys (new Vector ());
        */
	if (DEBUG)
	    System.out.println ("IDLDataObject::startParsing (); --> END"); // NOI18N
    }    

    public void parse () {
	if (DEBUG)
	    System.out.println ("IDLDataObject::parse () of " + this.getPrimaryFile ()); // NOI18N
	InputStream __stream = null;
        try {
	    __stream = this.getPrimaryFile ().getInputStream ();
            IDLParser __parser = new IDLParser (getPrimaryFile ().getInputStream ());
            //if (DEBUG)
	    if (DEBUG)
		System.out.println ("parsing of " + getPrimaryFile ().getName ()); // NOI18N
            _M_src = (IDLElement)__parser.Start ();
            //_M_src.xDump (" "); // NOI18N
            _M_src.setDataObject (this);
            _M_status = STATUS_OK;	    
	    if (DEBUG)
                _M_src.dump (""); // NOI18N
            if (DEBUG)
		System.out.println ("parse OK :-)"); // NOI18N
        } catch (ParseException e) {
            if (DEBUG)
		System.out.println ("parse exception"); // NOI18N
            _M_status = STATUS_ERROR;
        } catch (TokenMgrError e) {
            if (DEBUG)
		System.out.println ("parser error!!!"); // NOI18N
            _M_status = STATUS_ERROR;
        } catch (java.io.FileNotFoundException e) {
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) { // NOI18N
		e.printStackTrace ();
	    }
        } catch (Exception ex) {
            //TopManager.getDefault ().notifyException (ex);
	    if (DEBUG)
		System.out.println ("IDLParser exception in " + this.getPrimaryFile ()); // NOI18N
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		ex.printStackTrace ();
        } finally {
	    try {
		if (__stream != null)
		    __stream.close ();
	    } catch (IOException __ex) {
	    }
	}
	if (DEBUG)
	    System.out.println ("status: " + _M_status); // NOI18N
	if (_M_idl_node != null) {
	    if (_M_status == STATUS_OK) {
		if (DEBUG)
		    System.out.println ("STATUS_OK"); // NOI18N
		_M_idl_node.setIconBase (IDLNode.IDL_ICON_BASE);
	    }
	    if (_M_status == STATUS_ERROR) {
		if (DEBUG)
		    System.out.println ("STATUS_ERROR"); // NOI18N
                _M_idl_node.setIconBase (IDLNode.IDL_ERROR_ICON);
		_M_src = null;
	    }
	    
	    _M_idl_node.update ();
	} 
	else {
	    if (DEBUG)
		System.out.println ("idl node is null"); // NOI18N
	}
	this.firePropertyChange ("_M_status", null, null); // NOI18N
    }

    public IDLElement getSources () {
        return _M_src;
    }

    class FileListener extends FileChangeAdapter {
        public void fileChanged (FileEvent e) {
            if (DEBUG)
		System.out.println ("idl file was changed."); // NOI18N
            //IDLDataObject.this.handleFindDataObject (
            //IDLDataObject.this.startParsing ();
            IDLDataObject.this.update ();
	    if (IDLDataObject.this._M_idl_node != null)
		IDLDataObject.this._M_idl_node.update ();
            CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                       (CORBASupportSettings.class, true);
	    if (css.getActiveSetting ().getSynchro () == ORBSettingsBundle.SYNCHRO_ON_SAVE)
		IDLDataObject.this.generateImplementation ();
        }

        public void fileRenamed (FileRenameEvent e) {
            if (DEBUG)
		System.out.println ("IDLDocumentChildren::FileListener::FileRenamed (" + e + ")"); // NOI18N
        }

	public void fileAttributeChanged (org.openide.filesystems.FileAttributeEvent __event) {
	    if (DEBUG)
		System.out.println ("fileAttributeChanged listened: " + __event); // NOI18N
	}

	public void fileDataCreated (FileEvent __event) {
	    if (DEBUG)
		System.out.println ("fileDataCreated listened: " + __event // NOI18N
				    + ":" + __event.getFile ()); // NOI18N
	}

	public void fileDeleted (FileEvent __event) {
	    if (DEBUG)
		System.out.println ("fileDeleted listened: " + __event // NOI18N
				    + ":" + __event.getFile ()); // NOI18N
	}

	public void fileFolderCreated (FileEvent __event) {
	    if (DEBUG)
		System.out.println ("fileFolderCreated listened: " + __event); // NOI18N
	}

    }
    /*
      class FolderListener extends FileChangeAdapter {
      public void fileDeleted (FileEvent __event) {
      //if (DEBUG)
      System.out.println ("fileDeleted listened: " + __event 
      + ":" + __event.getFile ());
      if (__event.getFile ().equals (IDLDataObject.this.getPrimaryFile ())) {
      // after `cut' or `delete'
      System.out.println ("// after `cut' or `delete'");
      }
      }
      }
    */

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
            gen_file = folder.getFileObject ((String)enum.nextElement (), "java"); // NOI18N
            if (DEBUG)
                if (gen_file != null)
                    System.out.println ("add fo: " + gen_file.getName ()); // NOI18N
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
	    System.out.println ("generating of idl implemenations..."); // NOI18N
        generator = new ImplGenerator (this);
        generator.setSources (getSources ());
	// this can be done in system RequestProcessor
	//generator.generate ();
	synchronized (this) {
	    if (!_M_generation) {
		_M_generation = true;
		//RequestProcessor.postRequest (new Runnable () {
		_S_request_processor2.post (new Runnable () {
			public void run () {
			    generator.generate ();
			    _M_generation = false;
			}
		    }, 0, Thread.MIN_PRIORITY);
	    }
	    else {
		//System.out.println ("can't generate while generating!"); // NOI18N
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Message (CORBASupport.CANT_GENERATE));
	    }
	}
    }

    public void setOrbForCompilation (String __value) throws IOException {
	_M_orb_for_compilation = __value;
	FileObject __idl_file = this.getPrimaryFile ();
	__idl_file.setAttribute ("orb_for_compilation", _M_orb_for_compilation); // NOI18N
    }

    public String getOrbForCompilation () {
	FileObject __idl_file = this.getPrimaryFile ();
	if (!_M_orb_for_compilation_cache) {
	    _M_orb_for_compilation = (String)__idl_file.getAttribute ("orb_for_compilation"); // NOI18N
	    _M_orb_for_compilation_cache = true;
	}
	return _M_orb_for_compilation;
    }
   
    public RequestProcessor getGeneratorProcessor () {
	return _S_request_processor2;
    }

    public RequestProcessor getParserProcessor () {
	return _S_request_processor;
    }

    protected FileObject handleMove (DataFolder __dfolder) throws IOException {
	if (DEBUG)
	    System.out.println ("IDLDataObject::handleMove (" + __dfolder + ");"); // NOI18N
	FileObject __result = super.handleMove (__dfolder);
	//FileObject __pfile = this.getPrimaryFile ();
	//System.out.println ("new pfile: " + __pfile); // NOI18N
	//System.out.println ("__result: " + __result); // NOI18N
	//__pfile.addFileChangeListener (new FileListener ());
	__result.addFileChangeListener (new FileListener ());
	return __result;
    }

    protected void handleDelete () throws IOException {
	if (DEBUG)
	    System.out.println ("IDLDataObject::handleDelete ();"); // NOI18N
	CloseCookie __cookie = (CloseCookie)this.getCookie (CloseCookie.class);
	if (__cookie.close ()) {
	    // user really want to close this data object
	    super.handleDelete ();
	}
    }

    public Set files () {
	IDLDataLoader __loader = (IDLDataLoader)this.getMultiFileLoader ();
	if (!__loader.getHide ()) {
	    // don't hide files => we must throw away all secondary entries
	    Iterator __iterator = this.secondaryEntries ().iterator ();
	    while (__iterator.hasNext ()) {
		Entry __en = (MultiDataObject.Entry)__iterator.next ();
		this.removeSecondaryEntry (__en);
	    }
	}
	Set __result = super.files ();
	return __result;
    }

}

