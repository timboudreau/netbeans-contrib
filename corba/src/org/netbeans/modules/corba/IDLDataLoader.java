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

import java.beans.PropertyVetoException;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import java.util.ResourceBundle;
import java.util.Map;
import java.util.Date;
import java.util.Vector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;

import java.text.DateFormat;

import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.FileEntry;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.ExtensionList;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileRenameEvent;

import org.openide.actions.*;

import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.util.MapFormat;
import org.openide.util.WeakSet;

import org.netbeans.modules.corba.settings.*;

/** Data loader which recognizes IDL files.
 *
 * @author Karel Gardas
 */

public class IDLDataLoader extends MultiFileLoader implements FileChangeListener {
    //public class IDLDataLoader extends UniFileLoader {
    
    static final long serialVersionUID =-1462379765695052830L;
    
    //private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;
    
    private transient CORBASupportSettings _M_css;
    public static final String IDL_EXTENSION = "idl"; // NOI18N
    
    private static final String JAVA_EXTENSION = "java"; // NOI18N
    private static final String CLASS_EXTENSION = "class"; // NOI18N
    
    public static final String PROP_EXTENSIONS = "extensions"; // NOI18N
    
    protected transient int fi_counter = 0;
    
    protected transient HashMap _M_folders;
    
    public boolean _M_hide_generated_files = true;
    
    //private double _M_msec_in_find_primary_file = 0;
    
    
    /** Creates new IDLDataLoader */
    public IDLDataLoader() {
        super("org.netbeans.modules.corba.IDLDataObject");
        if (DEBUG)
            System.out.println("IDLDataLoader..."); // NOI18N
        _M_folders = new HashMap();
    }
    
    protected SystemAction[] defaultActions () {
        return new SystemAction[] {
            SystemAction.get(OpenAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
            SystemAction.get(CompileAction.class),
            null,
            SystemAction.get(ParseAction.class),
            null,
            SystemAction.get(GenerateImplAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(SaveAsTemplateAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class),
        };
    }
    
    protected String defaultDisplayName () {
        return NbBundle.getBundle(IDLDataLoader.class).getString("PROP_IDLLoader_Name");
    }
    
    public ExtensionList getExtensions() {
        ExtensionList extensions = (ExtensionList)getProperty(PROP_EXTENSIONS);
        if (extensions == null) {
            extensions = new ExtensionList();
            extensions.addExtension(IDL_EXTENSION);
            putProperty(PROP_EXTENSIONS, extensions, false);
        }
        return extensions;
    }
    
    public void setExtensions(ExtensionList ext) {
        putProperty(PROP_EXTENSIONS, ext, true);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(1);
        out.writeObject(getProperty(PROP_EXTENSIONS));
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        if (in.available() > 0) {
            in.readInt();
            putProperty(PROP_EXTENSIONS, in.readObject(), false);
        }
    }
    
    /** Creates new IDLDataObject for this FileObject.
     * @param fo FileObject
     * @return new IDLDataObject
     */
    protected MultiDataObject createMultiObject (final FileObject fo) throws IOException {
        return new IDLDataObject(fo, this);
    }
    
    public boolean getHide () {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::getHide () -> " + _M_hide_generated_files); // NOI18N
        return _M_hide_generated_files;
    }
    
    public void setHide (boolean __value) {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::setHide (" + __value + ");"); // NOI18N
        //Thread.dumpStack ();
        boolean __old = _M_hide_generated_files;
        _M_hide_generated_files = __value;
	this.firePropertyChange ("_M_hide_generated_files", new Object (), new Object ()); // NOI18N
    }
    
    public boolean folderIsInCache (FileObject __folder) {
        if (DEBUG) {
	    System.out.println ("IDLDataLoader::folderIsInCache (" + __folder.toString () + ");"); // NOI18N
            //if (_M_folders.containsKey (__folder.toString ()))
	    if (_M_folders.containsKey (__folder))
		System.out.println ("YES"); // NOI18N
            else
		System.out.println ("NO"); // NOI18N
        }
        //return _M_folders.containsKey (__folder.toString ());
	return _M_folders.containsKey (__folder);
    }
    
    public String getAbsolutePath (FileObject __fo) {
	return __fo.getPackageName ('/') + __fo.getName ();
    }
    
    public void addFolderToCache (FileObject __folder) {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::addFolderToCache (" + __folder.toString () // NOI18N
            + ")"); // NOI18N
	WeakSet __tmp = new WeakSet ();
	__folder.addFileChangeListener (this);
        //_M_folders.put (__folder.toString (), __tmp);
	_M_folders.put (__folder, __tmp);
    }
    
    public void removeFolderFromCache (FileObject __folder) {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::removeFolderFromCache (" + __folder.toString () // NOI18N
            + ")"); // NOI18N
	__folder.removeFileChangeListener (this);
        //_M_folders.put (__folder.toString (), __tmp);
	_M_folders.remove (__folder);
    }
    
    public void addFosToCache (FileObject __parent, WeakSet __fos) {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::addFosToCache (" + __parent.toString () + ", " // NOI18N
				+  __fos.toString () + ")"); // NOI18N
	__parent.addFileChangeListener (this);
        //_M_folders.put (__parent.toString (), __fos);
	_M_folders.put (__parent, __fos);
    }
    
    public boolean isFileObjectInWeakSet (WeakSet __set, FileObject __fo) {
	Iterator __iterator = __set.iterator ();
	while (__iterator.hasNext ()) {
	    if (__fo.equals (__iterator.next ()))
                return true;
        }
        return false;
    }
    
    public void addFileObjectToCache (FileObject __fo) {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::addDataObjectToCache (" + __fo.toString () // NOI18N
            + ");"); // NOI18N
	FileObject __parent = __fo.getParent ();
        //WeakSet __idls_in_folder = (WeakSet)_M_folders.get (__parent.toString ());
	WeakSet __idls_in_folder = (WeakSet)_M_folders.get (__parent);
        if (__idls_in_folder != null) {
            //if (DEBUG)
	    if (!this.isFileObjectInWeakSet (__idls_in_folder, __fo)) {
		String __name = __fo.getName () + "." + __fo.getExt (); // NOI18N
                if (DEBUG) {
		    System.out.println ("adding file object: " + __name); // NOI18N
		    System.out.println ("weak set: " + __idls_in_folder); // NOI18N
                    //Thread.dumpStack ();
                }
		__idls_in_folder.add (__fo);
            }
        }
        else {
            if (DEBUG)
		System.out.println ("adding pair of folder and file object"); // NOI18N
	    WeakSet __tmp = new WeakSet ();
	    __tmp.add (__fo);
            //_M_folders.put (__parent.toString (), __tmp);
	    this.addFosToCache (__parent, __tmp);
            
        }
    }
    
    public void removeFileObjectFromCache (FileObject __fo) {
	if (this.folderIsInCache (__fo.getParent ())) {
	    WeakSet __set = (WeakSet)_M_folders.get (__fo.getParent ());
	    if (this.isFileObjectInWeakSet (__set, __fo)) {
                if (DEBUG) {
		    System.out.println ("weak set: " + __set + " : " + __set.size ()); // NOI18N
		    System.out.println ("sucessfully remove: " + __fo); // NOI18N
                }
		__set.remove (__fo);
                if (DEBUG) {
		    System.out.println ("weak set size: " + __set.size ()); // NOI18N
		    System.out.println ("weak set: " + __set + " : " + __set.size ()); // NOI18N
                }
            }
        }
    }
    
    public WeakSet getFileObjectsForFileObject (FileObject __fo) {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::getFileObjectsForFileObject (" // NOI18N
				+ __fo.toString () + ");"); // NOI18N
        //(Vector)_M_folders.get (fo.getParent ()));
        //return (WeakSet)_M_folders.get (__fo.getParent ().toString ());
	return (WeakSet)_M_folders.get (__fo.getParent ());
    }
    
    
    public LinkedList getDataObjectsFromFileObjects (WeakSet __fos) {
        if (DEBUG)
	    System.out.println ("IDLDataLoader::getDataObjectsFromFileObjects (" + __fos + ")"); // NOI18N
	LinkedList __idos = new LinkedList ();
        
        if (__fos == null)
            return __idos;
        
        DataObject __tmp_do = null;
        FileObject __fo = null;
	Iterator __iterator = __fos.iterator ();
	while (__iterator.hasNext ()) {
	    __fo = (FileObject)__iterator.next ();
            try {
		__tmp_do = DataObject.find (__fo);
            } catch (DataObjectNotFoundException __ex) {
                __tmp_do = null;
		if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		    __ex.printStackTrace ();
            }
            if (__tmp_do != null) {
		__idos.add (__tmp_do);
            }
        }
        /*
          for (int __i = 0; i < __fos.size (); __i++) {
          __fo = (FileObject)__fos.elementAt (i);
          try {
          __tmp_do = DataObject.find (__fo);
          } catch (DataObjectNotFoundException __ex) {
          __tmp_do = null;
          __ex.printStackTrace ();
          }
          if (__tmp_do != null) {
          __idos.add (__tmp_do);
          }
          }
         */
        return __idos;
    }
    /** For a given file finds a primary file.
     * @param fo the file to find primary file for
     *
     * @return the primary file for the file or null if the file is not
     *   recognized by this loader
     */
    protected FileObject findPrimaryFile(FileObject __fo) {
        //long __time_at_start = 0;
        //if (DEBUG) {
        //System.out.println ("IDLDataLoader::findPrimaryFile (" + __fo + ");"); // NOI18N
        //__time_at_start = System.currentTimeMillis ();
        //}
        
        if (__fo.isFolder())
            return null;
        
        if (__fo.getParent() == null) {
            if (DEBUG)
                System.out.println("!!!!NULL FOLDER!!!! - for " + __fo.toString()); // NOI18N
            return null;
        }
        
        String __ext = __fo.getExt();
        if (getExtensions().isRegistered(__fo)) {
            //	if (__ext.equals (IDL_EXTENSION)) {
            this.addFileObjectToCache(__fo);
            if (DEBUG) {
                // profiling hack
                //long __time_at_end = System.currentTimeMillis ();
                //_M_msec_in_find_primary_file += __time_at_end - __time_at_start;
                //System.out.println ("milisecond: " + _M_msec_in_find_primary_file); // NOI18N
                // end of profiling hack
            }
            return __fo;
        }
        
        if (!(__ext.equals(IDLDataLoader.JAVA_EXTENSION)
        || __ext.equals(IDLDataLoader.CLASS_EXTENSION))) {
            // __for isn't java
            return null;
        }
        
        if (_M_css == null)
            _M_css = (CORBASupportSettings)
            CORBASupportSettings.findObject(CORBASupportSettings.class, true);
        //System.out.println ("_M_css: " + _M_css); // NOI18N
        
        //ORBSettings __settings = _M_css.getActiveSetting ();
        //System.out.println ("__settings: " + __settings); // NOI18N
        //if (__settings != null) {
        //if (__settings.hideGeneratedFiles ()) {
        if (!this.folderIsInCache(__fo.getParent())) {
            if (DEBUG)
                System.out.println("find idls in folder"); // NOI18N
            WeakSet __idls_in_folder = this.findIdls(__fo);
            FileObject __parent = __fo.getParent();
            this.addFosToCache(__parent, __idls_in_folder);
        }
        
        LinkedList __idos = this.getDataObjectsFromFileObjects
        (this.getFileObjectsForFileObject(__fo));
        
        if (__idos == null) {
            if (DEBUG) {
                // profiling hack
                //long __time_at_end = System.currentTimeMillis ();
                //_M_msec_in_find_primary_file += __time_at_end - __time_at_start;
                //System.out.println ("milisecond: " + _M_msec_in_find_primary_file); // NOI18N
                // end of profiling hack
            }
            return null;
        }
        
        FileObject __retval = null;
        IDLDataObject __tmp_ido = null;
        try {
            // workaround for dynamic update of CORBA module
            Iterator __iterator = __idos.iterator();
            while (__iterator.hasNext()) {
                __tmp_ido = (IDLDataObject)__iterator.next();
                if (__tmp_ido.canGenerate(__fo)) {
                    String __orb_name = __tmp_ido.getOrbForCompilation();
                    ORBSettings __settings = (__orb_name != null) ? _M_css.getSettingByName(__orb_name) : _M_css.getActiveSetting();
                    if (__settings != null) {
                        if (__settings.hideGeneratedFiles()) {
                            __retval = __tmp_ido.getPrimaryFile();
                            if (DEBUG)
                                System.out.println(__fo.toString() + " generated from " // NOI18N
                                + __retval.toString());
                            if (DEBUG) {
                                // profiling hack
                                //long __time_at_end = System.currentTimeMillis ();
                                //_M_msec_in_find_primary_file += __time_at_end - __time_at_start;
                                //System.out.println ("milisecond: " // NOI18N
                                //		     + _M_msec_in_find_primary_file);
                                // end of profiling hack
                            }
                            return __retval;
                        }
                    }
                    return null;
                }
            }
                    /*
                      for (int __i=0; i<idos.size (); i++) {
                      tmp_ido = (IDLDataObject)idos.elementAt (i);
                      if (tmp_ido.canGenerate (fo)) {
                      retval = tmp_ido.getPrimaryFile ();
                      if (DEBUG)
                      System.out.println (fo.getName () + " generated from " + retval.getName ());
                      if (DEBUG) {
                      // profiling hack
                      long __time_at_end = System.currentTimeMillis ();
                      _M_msec_in_find_primary_file += __time_at_end - __time_at_start;
                      System.out.println ("milisecond: " + _M_msec_in_find_primary_file);
                      // end of profiling hack
                      }
                      return retval;
                      }
                      }
                     */
        } catch (ClassCastException ex) {
            //ex.printStackTrace ();
            if (DEBUG)
                System.out.println("exception: " + ex); // NOI18N
        }
        //}
        //}
        /*
          else {
          System.out.println ("settings == null for: " + __fo);
          }
         */
        if (DEBUG) {
            // profiling hack
            //long __time_at_end = System.currentTimeMillis ();
            //_M_msec_in_find_primary_file += __time_at_end - __time_at_start;
            //System.out.println ("milisecond: " + _M_msec_in_find_primary_file); // NOI18N
            // end of profiling hack
        }
        return null;
        
    }
    
    
    
    /** Creates the right primary entry for given primary file.
     *
     * @param primaryFile primary file recognized by this loader
     * @return primary entry for that file
     */
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile)  {
        return new IDLFileEntry(obj, primaryFile);
    }
    
    /** Creates right secondary entry for given file. The file is said to
     * belong to an object created by this loader.
     *
     * @param secondaryFile secondary file for which we want to create entry
     * @return the entry
     */
    protected MultiDataObject.Entry createSecondaryEntry (MultiDataObject obj,
    FileObject secondaryFile) {
        return new FileEntry.Numb(obj, secondaryFile);
    }
    
    protected WeakSet findIdls (FileObject __fo) {
        fi_counter++;
        if (DEBUG)
	    System.out.println ("IDLDataLoader::findIdls ()..." + fi_counter); // NOI18N
	FileObject __folder = __fo.getParent ();
        //if (__folder == null) {
        //if (DEBUG)
        //System.out.println ("!!!!NULL FOLDER!!!! - for " + fo.getName ()); // NOI18N
        /** Changed for the Jaga 1010 bug
         */
        //return new Vector ();
        //return new WeakSet ();
        /* End of change
         */
        //}
	FileObject[] __files = __folder.getChildren ();
	WeakSet __idls = new WeakSet ();
        for (int __i=0; __i < __files.length; __i++) {
	    if (__files[__i].isData ()) {
                // file object represent data file
                //System.out.println (files[i]);
		if (__files[__i].getExt ().equals ("idl")) { // NOI18N
                    // idl file
		    __idls.add (__files[__i]);
                    if (DEBUG)
			System.out.println ("idl file: " + __files[__i].toString ()); // NOI18N
                }
            }
        }
        return __idls;
    }
    
    /*
       public void setExtensions (ExtensionList e) {
       extensions = e;
       }
     
       public ExtensionList getExtensions () {
       return extensions;
       }
     */
    
    protected Map createStringsMap() {
        /*
          CORBASupportSettings css = (CORBASupportSettings)
          CORBASupportSettings.findObject (CORBASupportSettings.class, true);
         */
        if (_M_css == null)
            _M_css = (CORBASupportSettings)
            CORBASupportSettings.findObject(CORBASupportSettings.class, true);
        
        return _M_css.getActiveSetting().getReplaceableStringsProps();
    }
    
    
    /** This entry defines the format for replacing the text during
     * instantiation the data object.
     */
    public class IDLFileEntry extends FileEntry.Format {
        
        static final long serialVersionUID =-3139969782935474471L;
        
        /** Creates new IDLFileEntry */
        IDLFileEntry(MultiDataObject obj, FileObject file) {
            super (obj, file);
        }
        
        /** Method to provide suitable format for substitution of lines.
         *
         * @param target the target folder of the installation
         * @param n the name the file will have
         * @param e the extension the file will have
         * @return format to use for formating lines
         */
        protected java.text.Format createFormat(FileObject target, String n, String e) {
            Map map = createStringsMap();
            
            map.put("DATE", DateFormat.getDateInstance(DateFormat.LONG).format(new Date())); // NOI18N
            map.put("TIME", DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())); // NOI18N
            map.put("NAME", n); // NOI18N
            //map.put("PACKAGE", target.getPackageName('.')); // NOI18N
            
            MapFormat format = new MapFormat(map);
            format.setLeftBrace("__"); // NOI18N
            format.setRightBrace("__"); // NOI18N
            format.setExactMatch(false);
            return format;
        }
    }
    
    
    public void fileAttributeChanged (FileAttributeEvent __event) {
        if (DEBUG)
	    System.out.println ("fileAttributeChanged listened: " + __event); // NOI18N
    }
    
    public void fileChanged (FileEvent __event) {
        if (DEBUG)
	    System.out.println ("fileChanged listened: " + __event); // NOI18N
    }
    
    public void fileDataCreated (FileEvent __event) {
        if (DEBUG)
	    System.out.println ("fileDataCreated listened: " + __event // NOI18N
				+ ":" + __event.getFile ()); // NOI18N
	FileObject __fo = __event.getFile ();
	FileObject __parent = __fo.getParent ();
	if (this.folderIsInCache (__parent) && __fo.getExt ().equals (CORBASupport.IDL_EXT)) {
	    this.addFileObjectToCache (__fo);
        }
    }
    
    public void fileDeleted (FileEvent __event) {
        if (DEBUG)
	    System.out.println ("fileDeleted listened: " + __event + ":" + __event.getFile ()); // NOI18N
	FileObject __fo = __event.getFile ();
	if (__fo.isFolder ()) {
            // user deleted folder which is in cache
	    this.removeFolderFromCache (__fo);
        }
	if (__fo.isData ()) {
            // user deleted file which is in folder which is in cache
	    this.removeFileObjectFromCache (__fo);
        }
    }
    
    public void fileFolderCreated (FileEvent __event) {
        if (DEBUG)
	    System.out.println ("fileFolderCreated listened: " + __event); // NOI18N
    }
    
    public void fileRenamed (FileRenameEvent __event) {
        if (DEBUG)
	    System.out.println ("fileRenamed listened: " + __event); // NOI18N
    }
    
    
}

/*
 * <<Log>>
 */
