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
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.TopManager;

import org.openide.cookies.OpenCookie;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.SaveCookie;
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
import org.openide.util.RequestProcessor.Task;
import org.openide.util.Utilities;

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
import org.netbeans.modules.corba.idl.src.ValueBoxElement;
import org.netbeans.modules.corba.idl.src.ValueElement;
import org.netbeans.modules.corba.idl.src.ParseException;
import org.netbeans.modules.corba.idl.src.TokenMgrError;

import org.netbeans.modules.corba.idl.generator.ImplGenerator;

import org.netbeans.modules.corba.idl.cpp.Preprocessor;

import org.netbeans.modules.corba.utils.Pair;
import org.netbeans.modules.corba.utils.FileUtils;

/** Object that provides main functionality for idl data loader.
 *
 * @author Karel Gardas
 */

public class IDLDataObject extends MultiDataObject
    implements PropertyChangeListener {

    static final long serialVersionUID =-7151972557886707595L;
    
    //public static final boolean DEBUG = true;
    private static final boolean DEBUG = false;
    
    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_PARSING = 2;
    public static final int STATUS_NOT_PARSED = 3;
    
    public static final int STYLE_NOTHING = 0;
    public static final int STYLE_FIRST_LEVEL = 1;
    public static final int STYLE_FIRST_LEVEL_WITH_NESTED_TYPES = 2;
    public static final int STYLE_ALL = 3;
    
    public static final String EXT_JAVA = "java";   // No I18N
    
    private static RequestProcessor _S_request_processor
	= new RequestProcessor("CORBA - IDL Parser"); // NOI18N
    private static RequestProcessor _S_request_processor2
	= new RequestProcessor("CORBA - Implementation Generator"); // NOI18N
    
    private Task _M_generation_task;
    
    private int _M_status;
    private IDLElement _M_src;
    
    //private Vector idlConstructs;
    //private Vector idlInterfaces;
    private HashMap _M_possible_names;
    //private Object _M_possible_names_lock = new Object ();
    //private boolean _M_possible_names_lock;
    
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
    
    private transient boolean _M_registered_on_settings = false;
    
    private String _M_params = null;
    private boolean _M_params_cache = false;
    
    private String _M_cpp_params = null;
    private boolean _M_cpp_params_cache = false;
    
    //private FolderListener _M_folder_listener;
    //private FileObject _M_parent_folder;
    
    public IDLDataObject(final FileObject obj, final MultiFileLoader loader)
    throws DataObjectExistsException {
        super(obj, loader);
        
        if (DEBUG)
            System.out.println("IDLDataObject::IDLDataObject (...)"); // NOI18N
        idl_loader = loader;
        this.setStatus(IDLDataObject.STATUS_NOT_PARSED);
        //_M_status = STATUS_NOT_PARSED;
        // use editor support
        MultiDataObject.Entry entry = getPrimaryEntry();
        CookieSet cookies = getCookieSet();
        
        //cookies.add (new EditorSupport (entry));
        cookies.add(new IDLEditorSupport(entry));
        cookies.add(new IDLCompilerSupport.Compile(entry));
        // added for implementation generator
        cookies.add(new IDLNodeCookie() {
            public void GenerateImpl(IDLDataObject ido) {
                ido.generateImplementation();
            }
        });
        cookies.add(new ParseCookie() {
            public void parse(IDLDataObject __ido) {
                __ido.startParsing();
            }
        });
        
        FileUtil.setMIMEType("idl", "text/x-idl"); // NOI18N
        FileObject __pfile = this.getPrimaryFile();
        //_M_parent_folder = __pfile.getParent ();
        __pfile.addFileChangeListener(new FileListener());
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
        
        this.startParsing();
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
    protected Node createNodeDelegate() {
        //return new DataNode (this, Children.LEAF);
        if (DEBUG)
            System.out.println("createNodeDelegate"); // NOI18N
        try {
            _M_idl_node = new IDLNode(this);
            //if (_M_status == STATUS_OK) {
            // parser is quicker - so we have parsed idl file
            _M_idl_node.update();
            //}
            //if (_M_status == STATUS_ERROR) {
            if (this.getStatus() == STATUS_ERROR) {
                if (DEBUG)
                    System.out.println("set error icon..."); // NOI18N
                _M_idl_node.setIconBase(IDLNode.IDL_ERROR_ICON);
            }
        } catch (Exception e) {
            if (Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                e.printStackTrace();
        }
        return _M_idl_node;
    }
    
    /** Help context for this object.
     * @return help context
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public synchronized int getStatus() {
        //System.out.println (this + " IDLDataObject::getStatus () -> " + _M_status);
        return _M_status;
    }
    
    public synchronized void setStatus(int __value) {
        //System.out.println (this + " IDLDataObject::setStatus (" + __value + ");");
        _M_status = __value;
    }
    
    public void openAtPosition(int line_pos, int offset) {
        if (DEBUG)
            System.out.println("openAtPosition (" + line_pos + ", " + offset + ");"); // NOI18N
        LineCookie line_cookie = (LineCookie)getCookie(LineCookie.class);
        if (line_cookie != null) {
            Line line = line_cookie.getLineSet().getOriginal(line_pos - 1);
            line.show(Line.SHOW_GOTO, offset - 1);
        }
    }
    
    public void openAtLinePosition() {
        openAtPosition(_line, 1);
    }
    
    public void setLinePosition(int line) {
        if (DEBUG)
            System.out.println("setLinePosition: " + line); // NOI18N
        _line = line;
    }
    
    public int getLinePosition() {
        if (DEBUG)
            System.out.println("getLinePosition: " + _line); // NOI18N
        return _line;
    }
    
    public void setColumnPosition(int column) {
        if (DEBUG)
            System.out.println("setColumnPosition: " + column); // NOI18N
        _column = column;
    }
    
    public int getColumnPosition() {
        if (DEBUG)
            System.out.println("getColumnPosition: " + _column); // NOI18N
        return _column;
    }
    
    public void setPositionRef(PositionRef ref) {
        if (DEBUG)
            System.out.println("setPositionRef"); // NOI18N
        position_of_element = ref;
    }
    
    
    public PositionRef getPositionRef() {
        if (DEBUG)
            System.out.println("getPositionRef"); // NOI18N
        return position_of_element;
    }
    
    
    public Compiler createCompiler(CompilerJob __job, Class __type) {
        if (DEBUG)
            System.out.println(this + "IDLDataObject.java:112:createCompiler"); // NOI18N
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
        (CORBASupportSettings.class, true);
        if (css.getOrb() == null) {
            new NotSetuped();
            return null;
        }
        
        if (this.isModified()) {
            //System.out.println (this + " ido was modified.");
            //System.out.print ("Save...");
            SaveCookie __cookie = (SaveCookie)this.getCookie(SaveCookie.class);
            try {
                __cookie.save();
            } catch (IOException __ex) {
                if (Boolean.getBoolean("netbeans.debug.exceptions")) {
                    __ex.printStackTrace();
                }
            }
            //System.out.println ("done.");
        }
        
        ORBSettings __setting;
        if (this.getOrbForCompilation() != null) {
            __setting = css.getSettingByName(this.getOrbForCompilation());
        }
        else {
            __setting = css.getActiveSetting();
        }
        if (DEBUG)
            System.out.println("IDLDataObject.java:112:createCompiler: orb for compilation:" // NOI18N
            + __setting.getName());
        ExternalCompiler.ErrorExpression __eexpr = new ExternalCompiler.ErrorExpression
        ("blabla", __setting.getErrorExpression(), // NOI18N
        __setting.file(), __setting.line(),
        __setting.column(), __setting.message());
        
        FileObject __fo = this.getPrimaryFile();
        NbProcessDescriptor __nb = __setting.getIdl();
        //String __new_args = __cpp_params + " " + __nb.getArguments ();
        //NbProcessDescriptor __nb_with_cpp = new NbProcessDescriptor
        //(__nb.getProcessName (), __new_args, __nb.getInfo ());
        ExternalCompiler __ec  = new IDLExternalCompiler
        (__fo, __type, __nb, __eexpr);
        CompilerJob idlCompilerJob = new CompilerJob (Compiler.DEPTH_INFINITE);
        idlCompilerJob.add(__ec);
        ArrayList __gens = this.getGeneratedFileObjectNames();
        //JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
        //JavaCompilerType jct = (JavaCompilerType)js.getCompiler ();
        JavaCompilerType __jct = (JavaCompilerType)TopManager.getDefault().getServices
        ().find(JavaExternalCompilerType.class);
        FileSystem __fs = null;
        try {
            __fs = __fo.getFileSystem();
        } catch (FileStateInvalidException __ex) {
            if (Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                __ex.printStackTrace();
        }
        
        String __package_name = ""; // NOI18N
        for (Iterator it = __gens.iterator();it.hasNext();) {
            String javaFileName = (String) it.next ();
            // future extension: jct.prepareIndirectCompiler
            //                    (type, fs, package_name, "text to status line"); // NOI18N
            __job.add(__jct.prepareIndirectCompiler(__type, __fs, javaFileName));
        }
        __job.dependsOn (idlCompilerJob);
        return __ec;
    }
    
    private LinkedList getIdlConstructs(int __style, IDLElement __element) {
        LinkedList __constructs = new LinkedList();
        String __name;
        Vector __type_members;
        Vector __tmp_members;
        Vector __members;
        IDLElement __tmp_element;
        IDLElement __tmp_element2;
        if (__element != null) {
            __members = __element.getMembers();
            if (__style == STYLE_ALL) {
                for (int __i = 0; __i < __members.size(); __i++) {
                    __tmp_element = (IDLElement)__members.elementAt(__i);
                    if (__tmp_element instanceof Identifier) {
                        // identifier
                        __constructs.add(__tmp_element);
                    }
                    else {
                        // others
                        __constructs.addAll(this.getIdlConstructs
                        (__style, __tmp_element));
                    }
                }
            }
            if (__style == STYLE_NOTHING) {
            }
            if (__style == STYLE_FIRST_LEVEL) {
                for (int __i = 0; __i < __members.size(); __i++) {
                    __tmp_element = (IDLElement)__members.elementAt(__i);
                    if (__tmp_element instanceof TypeElement) {
                        __tmp_members = __tmp_element.getMembers();
                        for (int __j = 0; __j < __tmp_members.size(); __j++) {
                            __tmp_element2 = (IDLElement)__tmp_members.elementAt(__j);
                            if (__tmp_element2 instanceof Identifier) {
                                /*
                                // identifier
                                __name = ((IDLElement)__members.elementAt (__i)).getMember (__j).getName ();
                                else
                                // constructed type => struct, union, enum
                                __name = ((TypeElement)__members.elementAt (__i)).getMember (__j).getName ();
                                 */
                                __constructs.add(__tmp_element2);
                            }
                        }
                    }
                    else {
                        //__name = ((IDLElement)__members.elementAt (__i)).getName ();
                        __constructs.add(__tmp_element);
                    }
                }
            }
            if (__style == STYLE_FIRST_LEVEL_WITH_NESTED_TYPES) {
                for (int __i = 0; __i < __members.size(); __i++) {
                    __tmp_element = (IDLElement)__members.elementAt(__i);
                    if (__tmp_element instanceof TypeElement) {
                        __constructs.addAll
                        (this.getIdlConstructs
                        (STYLE_ALL, __tmp_element));
                    }
                    else if (__tmp_element instanceof ValueBoxElement) {
                        ValueBoxElement __box = (ValueBoxElement)__tmp_element;
                        //System.out.println ("found value box element");
                        __constructs.add(__box);
                        if (__box.getMembers().size() > 1) {
                            IDLElement __tmp = __box.getMember(1);
                            //__tmp.dump ("|");
                            if (ImplGenerator.is_constructed_type(__tmp)) {
                                //System.out.println ("which is constructed type");
                                __constructs.add(__tmp);
                            }
                        }
                        /*
                          else {
                          System.out.println ("no inner type found");
                          }
                         */
                    }
                    else {
                        //__name = ((IDLElement)__members.elementAt (__i)).getName ();
                        __constructs.add(__members.elementAt(__i));
                    }
                }
            }
        }
        return __constructs;
    }
    
    
    private LinkedList getIdlConstructs(int __style) {
        if (DEBUG)
            System.out.println("IDLDataObject::getIdlConstructs (" + __style + ");"); // NOI18N
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
        
        return this.getIdlConstructs(__style, _M_src);
        //return idl_constructs;
    }
    
    
    public LinkedList getIDLValues() {
        return this.getIDLValues(_M_src);
    }
    
    
    private LinkedList getIDLValues(IDLElement __element) {
        LinkedList __idl_values = new LinkedList();
        String __name;
        Vector __type_members;
        Vector __tmp_members;
        if (__element != null) {
            //tmp_members = src.getMembers ();
            if (DEBUG)
                System.out.println("element: " + __element.getMembers()); // NOI18N
            for (int __i = 0; __i < __element.getMembers().size(); __i++) {
                if (__element.getMember(__i) instanceof ValueElement) {
                    __idl_values.add(__element.getMember(__i));
                }
            }
        }
        return __idl_values;
    }
    
    
    public LinkedList getIdlInterfaces(int __style) {
        if (DEBUG)
            System.out.println("IDLDataObject::getIdlInterfaces (" + __style + ");"); // NOI18N
        // wrapper
        return this.getIdlInterfaces(_M_src, __style);
    }
    
    private LinkedList getIdlInterfaces(IDLElement __element, int __style) {
        if (DEBUG)
            System.out.println("IDLDataObject.getIdlInterfaces (" // NOI18N
            + __element + ", " + __style + ");"); // NOI18N
        LinkedList __idl_interfaces = new LinkedList();
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
                    System.out.println("element: " + __element.getMembers()); // NOI18N
                for (int __i = 0; __i < __element.getMembers().size(); __i++) {
                    if (__element.getMember(__i) instanceof InterfaceElement) {
                        //__name = __element.getMember (__i).getName ();
                        //__idl_interfaces.add (__name);
                        __idl_interfaces.add(__element.getMember(__i));
                    }
                }
                if (DEBUG) {
                    Iterator __iterator = __idl_interfaces.iterator();
                    while (__iterator.hasNext()) {
                        System.out.println("interface: " + __iterator.next()); // NOI18N
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
                    System.out.println("element: " + __element.getMembers()); // NOI18N
                for (int __i = 0; __i < __element.getMembers().size(); __i++) {
                    if (__element.getMember(__i) instanceof InterfaceElement) {
                        //__name = __element.getMember (__i).getName ();
                        //__idl_interfaces.add (__name);
                        __idl_interfaces.add(__element.getMember(__i));
                    }
                    if (__element.getMember(__i) instanceof ModuleElement) {
                        LinkedList __nested = this.getIdlInterfaces
                        ((IDLElement)__element.getMember(__i), STYLE_ALL);
                        if (__nested != null)
                            __idl_interfaces.addAll(__nested);
                    }
                    
                }
                if (DEBUG) {
                    Iterator __iterator = __idl_interfaces.iterator();
                    while (__iterator.hasNext()) {
                        System.out.println("interface: " + __iterator.next()); // NOI18N
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
    
    public HashMap createPossibleNames(LinkedList __idl_constructs,
    LinkedList __idl_interfaces,
    LinkedList __idl_values) {
        HashMap __possible_names = new HashMap();
        if (DEBUG)
            System.out.println("IDLDataObject.createPossibleNames () ..."); // NOI18N
        String __name;
        // for various idl constructs
        /*
          for (int __i = 0; __i < __idl_constructs.size (); __i++) {
          __name = (String)__idl_constructs.elementAt (i);
         */
        Iterator __iterator = __idl_constructs.iterator();
        while (__iterator.hasNext()) {
            IDLElement idlElement = (IDLElement) __iterator.next();
            __name = idlElement.getName();
            if (idlElement instanceof ModuleElement && __name != null && __name.length()>0) {// NOI18N
                __possible_names.put (__name,idlElement);   // NOI18N
            }
            else if (__name != null && __name.length()>0) {
                __possible_names.put(__name + "Holder", idlElement); // NOI18N
                __possible_names.put(__name + "Helper", idlElement); // NOI18N
                __possible_names.put(__name, idlElement); // NOI18N
            }
        }
        // for idl interfaces
        /*
          for (int __i=0; i<__idl_interfaces.size (); __i++) {
          __name = (String)__idl_interfaces.elementAt (__i);
         */
        __iterator = __idl_interfaces.iterator();
        while (__iterator.hasNext()) {
            IDLElement idlElement = (IDLElement) __iterator.next ();
            __name = idlElement.getName();
            if (__name != null && (!__name.equals(""))) { // NOI18N
                //
                // now I comment *tie* names which classes are necesary to instantiate in server
                // and it's better when user can see it in explorer
                //
                __possible_names.put("_" + __name + "Stub", idlElement); // NOI18N
                //possible_names.put ("POA_" + name + "_tie", ""); // NOI18N
                //possible_names.put ("POA_" + name, ""); // NOI18N
                __possible_names.put(__name + "POA", idlElement); // NOI18N
                //possible_names.put (name + "POATie", ""); // NOI18N
                __possible_names.put(__name + "Operations", idlElement); // NOI18N
                //possible_names.put ("_" + name + "ImplBase_tie", ""); // NOI18N
                
                // for JavaORB
                __possible_names.put("StubFor" + __name, idlElement); // NOI18N
                __possible_names.put("_" + __name + "ImplBase", idlElement); // NOI18N
                // for VisiBroker
                __possible_names.put("_example_" + __name, idlElement); // NOI18N
                //possible_names.put ("_tie_" + name, ""); // NOI18N
                __possible_names.put("_st_" + __name, idlElement); // NOI18N
                // for OrbixWeb
                __possible_names.put("_" + __name + "Skeleton", idlElement); // NOI18N
                __possible_names.put("_" + __name + "Stub", idlElement); // NOI18N
                __possible_names.put("_" + __name + "Operations", idlElement); // NOI18N
                // for idltojava - with tie
                //possible_names.put ("_" + name + "Tie", ""); // NOI18N
                // for hidding folders
                // possible_names.put (name + "Package", ""); // NOI18N
            }
            
        }
        __iterator = __idl_values.iterator();
        while (__iterator.hasNext()) {
            IDLElement idlElement = (IDLElement) __iterator.next();
            __name = idlElement.getName();
            if (__name != null && (!__name.equals(""))) { // NOI18N
                __possible_names.put(__name + "ValueFactory", idlElement); // NOI18N
            }
            
        }
        if (DEBUG)
            System.out.println("possible names for " + this.getPrimaryFile().getName() + " : " // NOI18N
            + __possible_names) ;
        return __possible_names;
    }
    
    public boolean canGenerate(FileObject __fo) {
        //boolean DEBUG = true;
        String __name = __fo.getName();
        String __ext = __fo.getExt();
        if (DEBUG)
            System.out.print("IDLDataObject::canGenerate (" + __name + "." + __ext + ") -> "); // NOI18N
        if (!(__ext.equals("java") ||__ext.equals("class"))) {// NOI18N
            if (DEBUG)
                System.out.println("false1");
            return false;
        }
        if (this.getOrbForCompilation() != null) {
            // user setuped ORB for compilation on this DO
            CORBASupportSettings __css
            = (CORBASupportSettings)CORBASupportSettings.findObject
            (CORBASupportSettings.class, true);
            ORBSettings __settings = __css.getSettingByName
            (this.getOrbForCompilation());
            if (!__settings.hideGeneratedFiles()) {
                if (DEBUG)
                    System.out.println("false2");
                return false;
            }
        }
        
        //try {
        //if (_M_possible_names.get (__name) != null) {
        if (this.getPossibleNames().get(__name) != null) {
            if (DEBUG)
                System.out.println("yes"); // NOI18N
            return true;
        }
        else {
            if (DEBUG)
                System.out.println("no"); // NOI18N
            return false;
        }
        /*
          } catch (java.lang.NullPointerException __ex) {
          //synchronized (_lock) {
          synchronized (this) {
          // thread must wait for parser
          try {
          if (_M_possible_names == null) {
          if (DEBUG)
          System.out.println (this + ": this.wait ();");
          this.wait ();
          }
          else {
          if (DEBUG)
          System.out.println ("DeadLock recovery successfull :-))");
          }
          } catch (java.lang.InterruptedException __ex2) {
          return false;
          }
          }
          if (DEBUG)
          System.out.println ("OK - run again");
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
         */
    }
    
    public LinkedList getImplementationNames() {
        LinkedList __retval = new LinkedList();
        String __impl_prefix = null;
        String __impl_postfix = null;
        String __value_impl_prefix = null;
        String __value_impl_postfix = null;
        CORBASupportSettings __css = (CORBASupportSettings)CORBASupportSettings.findObject
        (CORBASupportSettings.class, true);
        if (!__css.getActiveSetting().isTie()) {
            // inheritance based skeletons
            __impl_prefix = __css.getActiveSetting().getImplBaseImplPrefix();
            __impl_postfix = __css.getActiveSetting().getImplBaseImplPostfix();
        }
        else {
            // tie based skeletons
            __impl_prefix = __css.getActiveSetting().getTieImplPrefix();
            __impl_postfix = __css.getActiveSetting().getTieImplPostfix();
        }
        __value_impl_prefix = __css.getActiveSetting().getValueImplPrefix();
        __value_impl_postfix = __css.getActiveSetting().getValueImplPostfix();
        LinkedList __int_names = this.getIdlInterfaces(STYLE_ALL);
        Iterator __iterator = __int_names.iterator();
        while (__iterator.hasNext()) {
            InterfaceElement __interface = (InterfaceElement)__iterator.next();
	    if (!__interface.isAbstract ()) {
		String __package = ImplGenerator.modules2package(__interface);
		__retval.add (new Pair
		    (__package, __impl_prefix + __interface.getName() + __impl_postfix));
	    }
        }
        LinkedList __val_names = this.getIDLValues();
        __iterator = __val_names.iterator();
        while (__iterator.hasNext()) {
            ValueElement __value = (ValueElement)__iterator.next();
            String __package = ImplGenerator.modules2package(__value);
            __retval.add(new Pair
            (__package, __value_impl_prefix + __value.getName() +
            __value_impl_postfix));
        }
        
        return __retval;
    }
    
    
    public int hasGeneratedImplementation() {
        //boolean DEBUG=true;
        if (DEBUG)
            System.out.println("IDLDataObject::hasGeneratedImplementation ();"); // NOI18N
        int __retval = 0;
        LinkedList __names = this.getImplementationNames();
        if (DEBUG)
            System.out.println("names: " + __names + " of size: " + __names.size()); // NOI18N
        FileObject __ifo_folder = this.getPrimaryFile().getParent();
        String __default_package = __ifo_folder.getPackageName('.');
        try {
            FileSystem __fs = __ifo_folder.getFileSystem();
            Iterator __iterator = __names.iterator();
            boolean __first = true;
            FileObject __file = null;
            while (__iterator.hasNext()) {
                Pair __pair = (Pair)__iterator.next();
                String __package = (String)__pair.first;
                if (!__package.equals(""))
                    __package = __package.substring(0, __package.length() - 1);
                String __name = (String)__pair.second;
                //System.out.println ("pair: " + __pair);
                // use find on filesystem to find file object
                if ((__file = __fs.find(__default_package + "." + __package, // NOI18N
                __name, "java")) != null) { // NOI18N
                    if (DEBUG)
                        System.out.println("found file: " + __file); // NOI18N
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
        } catch (FileStateInvalidException __ex) {
        }
        if (DEBUG)
            System.out.println("-> " + __retval); // NOI18N
        return __retval;
    }
    
    
    public void update() {
        if (DEBUG)
            System.out.println("IDLDataObject.update ()..."); // NOI18N
        // clearing MultiDataObject secondary entries
        
        Set __entries = this.secondaryEntries();
        Iterator __iterator = __entries.iterator();
        //entries.clear ();
        //for (int i=0; i<entries.size (); i++) {
        while (__iterator.hasNext()) {
            Object o = __iterator.next();
            if (DEBUG)
                System.out.println("removing: " + o); // NOI18N
            this.removeSecondaryEntry((MultiDataObject.Entry) o);
        }
        this.setStatus(IDLDataObject.STATUS_NOT_PARSED);
        this.startParsing();
        
        //getIdlConstructs ();
        //getIdlInterfaces ();
        /*
          possibleNames = createPossibleNames (getIdlConstructs (STYLE_NOTHING),
          getIdlInterfaces (STYLE_NOTHING));
         */
        
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
    public void startParsing() {
        if (DEBUG)
            System.out.println("IDLDataObject::startParsing ();"); // NOI18N
        //_M_status = STATUS_PARSING;
        this.setStatus(IDLDataObject.STATUS_PARSING);
        this.firePropertyChange("_M_status", null, null); // NOI18N
        _S_request_processor.post(new Runnable() {
            public void run() {
                IDLDataObject.this.parse();
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
            System.out.println("IDLDataObject::startParsing (); --> END"); // NOI18N
    }
    
    public void parse() {
        //boolean DEBUG=true;
        if (DEBUG)
            System.out.println("IDLDataObject::parse () of " + this.getPrimaryFile()); // NOI18N
        //System.out.println (this + ": ->parse ();"); // NOI18N
        InputStream __stream = null;
        File __tmp = null;
        PrintStream __out = null;
        int __status;
        try {
            //__stream = this.getPrimaryFile ().getInputStream ();
            //String __file_name = this.getPrimaryFile ().toString ();
            /*
              String __filesystem = this.getPrimaryFile ().getFileSystem ().getDisplayName ();
              String __file_name = __filesystem + File.separator
              + this.getPrimaryFile ().toString ();
             */
            String __file_name = this.getRealFileName();
            if (DEBUG)
                System.out.println("primary file: " + __file_name);
            // added for CPP Support
            __tmp = File.createTempFile("cpp", ".ii");
            __tmp.deleteOnExit();
            if (DEBUG)
                System.out.println("created tmp file: " + __tmp);
            __out = new PrintStream(new FileOutputStream(__tmp));
            CORBASupportSettings __css
            = (CORBASupportSettings)CORBASupportSettings.findObject
            (CORBASupportSettings.class, true);
            ORBSettings __settings;
            if (this.getOrbForCompilation() != null) {
                __settings = __css.getSettingByName(this.getOrbForCompilation());
            }
            else {
                __settings = __css.getActiveSetting();
            }
            String __params = "";
            String __params_on_settings = __settings.getCPPParams();
            String __params_on_object = this.getCPPParams();
            if (__params_on_settings != null)
                __params += __params_on_settings;
            if (__params_on_object != null)
                __params += " " + __params_on_object;
/*            for (Enumeration en = TopManager.getDefault().getRepository().fileSystems(); en.hasMoreElements();) {
                FileSystem __file_system = (FileSystem)en.nextElement();
                if (!__file_system.isDefault())
                    __params += " -I\"" + __file_system.getDisplayName() + "\"";
            }
 */
            __params += " -W\"" + FileUtils.getRealPackageName(this.getPrimaryFile()) + "\"";
            __params += " \"" + __file_name + "\"";
            if (DEBUG)
                System.out.println("cpp params: " + __params);
            String[] __args = Utilities.parseParameters(__params);
            Preprocessor.main(__args, __out);
            __stream = new FileInputStream(__tmp);
            IDLParser __parser = new IDLParser(__stream);
            if (this.isTemplate()) {
                //System.out.println ("file: " + this.getPrimaryFile () + " is template.");
                __parser.setTemplate(true);
            }
            //if (DEBUG)
            if (DEBUG) {
                System.out.println("parsing of " + getPrimaryFile().getName()); // NOI18N
                System.out.println("from preprocesed file: " + __tmp);
            }
            _M_src = (IDLElement)__parser.Start();
            //_M_src.xDump (" "); // NOI18N
            _M_src.setDataObject(this);
            //_M_status = STATUS_OK;
            //this.setStatus (IDLDataObject.STATUS_OK);
            __status = STATUS_OK;
            if (DEBUG)
                _M_src.dump(""); // NOI18N
            if (DEBUG)
                System.out.println("parse OK :-)"); // NOI18N
        } catch (ParseException e) {
            if (DEBUG)
                System.out.println("parse exception"); // NOI18N
            //_M_status = STATUS_ERROR;
            //this.setStatus (IDLDataObject.STATUS_ERROR);
            //e.printStackTrace ();
            __status = STATUS_ERROR;
        } catch (TokenMgrError e) {
            if (DEBUG)
                System.out.println("parser error!!!"); // NOI18N
            //_M_status = STATUS_ERROR;
            //this.setStatus (IDLDataObject.STATUS_ERROR);
            __status = STATUS_ERROR;
        } catch (java.io.FileNotFoundException e) {
            //this.setStatus (IDLDataObject.STATUS_ERROR);
            __status = STATUS_ERROR;
            if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                e.printStackTrace();
                //Thread.dumpStack ();
            }
        } catch (Exception ex) {
            //this.setStatus (IDLDataObject.STATUS_ERROR);
            __status = STATUS_ERROR;
            //TopManager.getDefault ().notifyException (ex);
            if (DEBUG)
                System.out.println("IDLParser exception in " + this.getPrimaryFile()); // NOI18N
            if (Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                ex.printStackTrace();
        } finally {
            try {
                if (DEBUG)
                    System.out.println("parsing finally");
                if (__stream != null)
                    __stream.close();
                if (__out != null)
                    __out.close();
                if (__tmp != null)
                    __tmp.delete();
            } catch (IOException __ex) {
            }
        }
        //System.out.println ("after parsing...");
        //System.out.println ("status: " + this.getStatus ());
        //synchronized (_M_possible_names_lock) {
        synchronized (this) {
            this.setStatus(__status);
            //if (_M_status == STATUS_OK || _M_possible_names == null) {
            if (this.getStatus() == STATUS_OK || _M_possible_names == null) {
                //System.out.println ("// we can crate new map");
                // we can crate new map
                _M_possible_names = this.createPossibleNames
                (this.getIdlConstructs(STYLE_FIRST_LEVEL_WITH_NESTED_TYPES),
                this.getIdlInterfaces(STYLE_FIRST_LEVEL),
                this.getIDLValues());
            }
            if (DEBUG)
                System.out.println(this + ": this.notifyAll ();"); // NOI18N
            this.notifyAll();
        }
        //System.out.println ("this.notifyAll ();");
        //this.notifyAll ();
        if (DEBUG) {
            System.out.println(this + ": after notifyAll ();"); // NOI18N
            System.out.println(this + ": status: " + _M_status); // NOI18N
        }
        if (_M_idl_node != null) {
            //if (_M_status == STATUS_OK) {
            if (this.getStatus() == STATUS_OK) {
                if (DEBUG)
                    System.out.println("STATUS_OK"); // NOI18N
                _M_idl_node.setIconBase(IDLNode.IDL_ICON_BASE);
            }
            //if (_M_status == STATUS_ERROR) {
            if (this.getStatus() == STATUS_ERROR) {
                if (DEBUG)
                    System.out.println("STATUS_ERROR"); // NOI18N
                _M_idl_node.setIconBase(IDLNode.IDL_ERROR_ICON);
                _M_src = null;
            }
            
            _M_idl_node.update();
        }
        else {
            if (DEBUG)
                System.out.println("idl node is null"); // NOI18N
        }
        this.firePropertyChange("_M_status", null, null); // NOI18N
        //System.out.println (this + ": parse ();->");
    }
    
    public IDLElement getSources() {
        return _M_src;
    }
    
    public String getRealFileName() throws FileStateInvalidException {
	return FileUtils.getRealFileName (this.getPrimaryFile ());
    }
    
    class FileListener extends FileChangeAdapter {
        public void fileChanged(FileEvent e) {
            if (DEBUG)
                System.out.println("++++++++++++ idl file was changed. ++++++++"); // NOI18N
            //IDLDataObject.this.handleFindDataObject (
            //IDLDataObject.this.startParsing ();
            IDLDataObject.this.update();
            if (IDLDataObject.this._M_idl_node != null)
                IDLDataObject.this._M_idl_node.update();
            CORBASupportSettings __css
            = (CORBASupportSettings)CORBASupportSettings.findObject
            (CORBASupportSettings.class, true);
            IDLDataLoader __loader = (IDLDataLoader) IDLDataLoader.findObject
            (IDLDataLoader.class, true);
            __loader.setHide(__loader.getHide());
            
            ORBSettings __settings = null;
            if (IDLDataObject.this.getOrbForCompilation() != null) {
                // user setuped ORB for compilation on this DO
                __settings = __css.getSettingByName
                (IDLDataObject.this.getOrbForCompilation());
            }
            else {
                __settings = __css.getActiveSetting();
            }
            if (__settings.getSynchro().equals(ORBSettingsBundle.SYNCHRO_ON_SAVE)) {
                //System.out.println ("generating after save....");
                IDLDataObject.this.generateImplementation();
            }
        }
        
        public void fileRenamed(FileRenameEvent e) {
            if (DEBUG)
                System.out.println("IDLDocumentChildren::FileListener::FileRenamed (" + e + ")"); // NOI18N
        }
        
        public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent __event) {
            if (DEBUG)
                System.out.println("fileAttributeChanged listened: " + __event); // NOI18N
        }
        
        public void fileDataCreated(FileEvent __event) {
            if (DEBUG)
                System.out.println("fileDataCreated listened: " + __event // NOI18N
                + ":" + __event.getFile()); // NOI18N
        }
        
        public void fileDeleted(FileEvent __event) {
            if (DEBUG)
                System.out.println("fileDeleted listened: " + __event // NOI18N
                + ":" + __event.getFile()); // NOI18N
        }
        
        public void fileFolderCreated(FileEvent __event) {
            if (DEBUG)
                System.out.println("fileFolderCreated listened: " + __event); // NOI18N
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
    
    public synchronized HashMap getPossibleNames() {
        while (this.getStatus() != STATUS_OK && this.getStatus() != STATUS_ERROR) {
            try {
                //System.out.println (this + " IDLDataObject::getPossibleNames () -> wait ()");
                this.wait();
            } catch (InterruptedException __ex) {
            }
        }
        //System.out.println ("IDLDataObject::getPossibleNames () -> continue ...");
        if (this.getStatus() == STATUS_OK)
            return _M_possible_names;
        if (this.getStatus() == STATUS_ERROR)
            return _M_possible_names;
        //return new Hashtable ();
        // never execute
        return null;
    }
    
    public ArrayList getGeneratedFileObjectNames () {
        ArrayList result = new ArrayList ();
        HashMap possibleNames = this.getPossibleNames ();
        Iterator it = possibleNames.keySet().iterator ();
        Iterator vit = possibleNames.values().iterator();
        FileObject parent = this.getPrimaryFile().getParent();
        String prefix = parent.getPackageNameExt ('/','.');
        while (it.hasNext()) {
            IDLElement idlElement = (IDLElement) vit.next();
            String objectName = (String)it.next();
            if (idlElement instanceof ModuleElement)
                continue;
            String name = prefix + '/' + objectName  + '.' + EXT_JAVA;
            result.add (name);
        }
        return result;
    }
    
    public ArrayList getGeneratedFileObjects() {
        //System.out.println (this + " IDLDataObject::getGeneratedFileObjects ();");
        ArrayList __result = new ArrayList();
        HashMap __possible_names = this.getPossibleNames();
        Set __keys = __possible_names.keySet();
        FileObject __folder = this.getPrimaryFile().getParent();
        __folder.refresh();
        FileObject __gen_file;
        Iterator __iterator = __keys.iterator();
        while (__iterator.hasNext()) {
            String __name = (String)__iterator.next();
            __gen_file = __folder.getFileObject(__name, "java"); // NOI18N
            if (DEBUG)
                if (__gen_file != null)
                    System.out.println("add fo: " + __gen_file.getName()); // NOI18N
            if (__gen_file != null)
                __result.add(__gen_file);
        }
        return __result;
    }
    
    public void generateImplementation() {
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
        (CORBASupportSettings.class, true);
        if (css.getOrb() == null) {
            new NotSetuped();
            return;
        }
        try {
            if (this.getPrimaryFile().getFileSystem().isReadOnly()) {
                TopManager.getDefault().notify(new NotifyDescriptor.Message
                (CORBASupport.CANT_GENERATE_INTO_RO_FS));
                return;
            }
        } catch (FileStateInvalidException __ex) {
            TopManager.getDefault().getErrorManager().notify(__ex);
            return;
        }
        if (DEBUG)
            System.out.println("generating of idl implemenations..."); // NOI18N
        generator = new ImplGenerator(this);
        generator.setSources(this.getSources());
        // this can be done in system RequestProcessor
        //generator.generate ();
        synchronized (this) {
            if (!_M_generation) {
                _M_generation = true;
                //RequestProcessor.postRequest (new Runnable () {
                _M_generation_task = _S_request_processor2.post(new Runnable() {
                    public void run() {
                        generator.generate();
                        _M_generation = false;
                    }
                }, 0, Thread.MIN_PRIORITY);
            }
            else {
                //System.out.println ("can't generate while generating!"); // NOI18N
                TopManager.getDefault().notify
                (new NotifyDescriptor.Message(CORBASupport.CANT_GENERATE));
            }
        }
    }
    
    public void setOrbForCompilation (String __value) throws IOException {
        String __old = _M_orb_for_compilation;
        _M_orb_for_compilation = __value;
	FileObject __idl_file = this.getPrimaryFile ();
	__idl_file.setAttribute ("orb_for_compilation", _M_orb_for_compilation); // NOI18N
        // for hidding generated files
        CORBASupportSettings __css = (CORBASupportSettings)
            CORBASupportSettings.findObject(CORBASupportSettings.class, true);
        ORBSettings __settings;
        if (_M_orb_for_compilation != null) {
            if (_M_registered_on_settings) {
                _M_registered_on_settings = false;
                __css.removePropertyChangeListener(this);
            }
            __css.cacheThrow();
            __settings = __css.getSettingByName(this._M_orb_for_compilation);
        }
        else {
            if (!_M_registered_on_settings) {
                _M_registered_on_settings = true;
                __css.addPropertyChangeListener(this);
            }
            __settings = __css.getActiveSetting();
        }
        IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject
            (IDLDataLoader.class, true);
	__loader.setHide (__settings.hideGeneratedFiles ());       
	this.firePropertyChange ("_M_orb_for_compilation", __old, _M_orb_for_compilation);
    }
    
    public String getOrbForCompilation () {
        if (DEBUG)
	    System.out.print ("IDLDataObject::getOrbForCompilation () -> ");
	FileObject __idl_file = this.getPrimaryFile ();
        if (!_M_orb_for_compilation_cache) {
	    _M_orb_for_compilation = (String)__idl_file.getAttribute ("orb_for_compilation"); // NOI18N
            _M_orb_for_compilation_cache = true;
        }
        if (_M_orb_for_compilation == null) {
            if (DEBUG)
		System.out.println ("default from settings");
            if (!_M_registered_on_settings) {
                CORBASupportSettings __css =
                (CORBASupportSettings)CORBASupportSettings.findObject
                (CORBASupportSettings.class, true);
                _M_registered_on_settings = true;
		__css.addPropertyChangeListener (this);
            }
        } else {
            if (DEBUG)
		System.out.println (_M_orb_for_compilation);
        }
        return _M_orb_for_compilation;
    }
    
    public RequestProcessor getGeneratorProcessor () {
        return _S_request_processor2;
    }
    
    public RequestProcessor getParserProcessor () {
        return _S_request_processor;
    }
    
    private void fix_src_names (IDLElement __element, String __source, String __target) {
        if (__element == null)
            return;
	if (__source.equals (__element.getFileName ())) {
	    //System.out.println ("fixing file name in " + __element.getName ());
	    __element.setFileName (__target);
	}
	Vector __members = __element.getMembers ();
	for (int __i=0; __i<__members.size (); __i++) {
	    IDLElement __t_member = (IDLElement)__members.get (__i);
	    this.fix_src_names (__t_member, __source, __target);
	}
    }

    protected FileObject handleMove(DataFolder __dfolder) throws IOException {
        if (DEBUG)
	    System.out.println ("IDLDataObject::handleMove (" + __dfolder + ");"); // NOI18N
        String oldName = getName();
	String __orig_name = FileUtils.getRealFileName (this.getPrimaryFile ());
        FileObject __result = super.handleMove(__dfolder);
	String __current_name = FileUtils.getRealFileName (__result);
	this.fix_src_names (this._M_src, __orig_name, __current_name);
        //FileObject __pfile = this.getPrimaryFile ();
        //System.out.println ("new pfile: " + __pfile); // NOI18N
        //System.out.println ("__result: " + __result); // NOI18N
        //__pfile.addFileChangeListener (new FileListener ());
        __result.addFileChangeListener(new FileListener());
        String newName = __result.getName();
        if (!oldName.equals(newName))
            rename(newName);
        return __result;
    }
    
    protected FileObject handleRename(String __name) throws IOException {
        if (DEBUG)
            System.out.println("IDLDataObject::handleRename (" + __name + ");"); // NOI18N
        //FileObject __old_fo = getPrimaryFile();
        FileObject __result = super.handleRename(__name);
        return __result;
    }
    
    protected void handleDelete () throws IOException {
        if (DEBUG)
	    System.out.println ("IDLDataObject::handleDelete ();"); // NOI18N
        synchronized (this) {
	    while (this.getStatus () != STATUS_OK && this.getStatus () != STATUS_ERROR) {
                try {
                    //System.out.println (this + " IDLDataObject::handleDelete () -> wait ()");
		    this.wait ();
                } catch (InterruptedException __ex) {
                }
            }
        }
        //System.out.println ("continue...");
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
    
    public synchronized Task getGenerationTask () {
        return _M_generation_task;
    }
    
    
    public void setParams(String __value) throws IOException {
        String __old = _M_params;
        _M_params = __value;
        FileObject __idl_file = this.getPrimaryFile();
        __idl_file.setAttribute("params", _M_params); // NOI18N
        this.firePropertyChange("_M_params", __old, _M_params);
    }
    
    
    public String getParams() {
        FileObject __idl_file = this.getPrimaryFile();
        if (!_M_params_cache) {
            _M_params = (String)__idl_file.getAttribute("params"); // NOI18N
            _M_params_cache = true;
        }
        return _M_params;
    }
    
    
    public void setCPPParams(String __value) throws IOException {
        String __old = _M_cpp_params;
        _M_cpp_params = __value;
	FileObject __idl_file = this.getPrimaryFile ();
	__idl_file.setAttribute ("cpp_params", _M_cpp_params); // NOI18N
	this.firePropertyChange ("_M_cpp_params", __old, _M_cpp_params);
    }
    
    
    public String getCPPParams () {
	FileObject __idl_file = this.getPrimaryFile ();
        if (!_M_cpp_params_cache) {
	    _M_cpp_params = (String)__idl_file.getAttribute ("cpp_params"); // NOI18N
            _M_cpp_params_cache = true;
        }
        return _M_cpp_params;
    }
    
    
    public void propertyChange (PropertyChangeEvent __event) {
	if (__event == null || __event.getPropertyName () == null) {
            //Thread.dumpStack ();
            return;
        }
	if (__event.getPropertyName ().equals ("_M_orb_name")) {
            //System.out.println ("changed default orb!");
	    this.firePropertyChange ("_M_orb_for_compilation", _M_orb_for_compilation, 
            _M_orb_for_compilation);
        }
    }
    
}

