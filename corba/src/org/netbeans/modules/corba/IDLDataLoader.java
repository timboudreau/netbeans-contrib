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

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;

import com.netbeans.ide.loaders.MultiFileLoader;
//import com.netbeans.ide.loaders.UniFileLoader;
import com.netbeans.ide.loaders.ExtensionList;
import com.netbeans.ide.loaders.DataObject;
import com.netbeans.ide.loaders.MultiDataObject;
import com.netbeans.ide.loaders.FileEntry;
import com.netbeans.ide.loaders.ExtensionList;
import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.ide.actions.*;
import com.netbeans.ide.util.actions.SystemAction;
import com.netbeans.ide.util.NbBundle;
import com.netbeans.ide.util.MapFormat;


import com.netbeans.enterprise.modules.corba.settings.*;

/** Data loader which recognizes IDL files.
*
* @author Karel Gardas
*/

public class IDLDataLoader extends MultiFileLoader {
   //public class IDLDataLoader extends UniFileLoader {

   /** Creates new IDLDataLoader */

   public static final String IDL_EXTENSION = "idl";

   public ExtensionList extensions = null;

   public IDLDataLoader() {
      super(IDLDataObject.class);
      initialize();
   }

   /** Does initialization. Initializes display name,
    * extension list and the actions. */
   private void initialize () {
      setDisplayName(NbBundle.getBundle(IDLDataLoader.class).
		     getString("PROP_IDLLoader_Name"));
      ExtensionList ext = new ExtensionList ();
      //  com.netbeans.ide.loaders.ExtensionList ext = new com.netbeans.ide.loaders.ExtensionList();
      ext.addExtension(IDL_EXTENSION);
      //    ext.addExtension("properties"); // now provided by properties DataObject
      setExtensions(ext);
      setActions(new SystemAction[] {
	 SystemAction.get(OpenAction.class),
	    null,
	    SystemAction.get (CompileAction.class),
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
	    SystemAction.get(PropertiesAction.class)
	    });
   }

   /** Creates new IDLDataObject for this FileObject.
    * @param fo FileObject
    * @return new IDLDataObject
    */
   protected MultiDataObject createMultiObject(final FileObject fo)
      throws IOException {
      return new IDLDataObject(fo, this);
   }


   /** For a given file finds a primary file.
    * @param fo the file to find primary file for
    *
    * @return the primary file for the file or null if the file is not
    *   recognized by this loader
    */
   protected FileObject findPrimaryFile (FileObject fo) {
      String ext = fo.getExt();
      if (ext.equals(IDL_EXTENSION))
	 return fo;
      else
	 //if (ext.equals(CLASS_EXTENSION))
	 //  return Util.findFile(fo, JAVA_EXTENSION);
	 return null;
   }

    /** Creates the right primary entry for given primary file.
     *
     * @param primaryFile primary file recognized by this loader
     * @return primary entry for that file
     */
    protected MultiDataObject.Entry createPrimaryEntry (MultiDataObject obj, FileObject primaryFile)
    {
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

   public void setExtensions (ExtensionList e) {
      extensions = e;
   }

   public ExtensionList getExtensions () {
      return extensions;
   }

    protected Map createStringsMap() {
	CORBASupportSettings cs = (CORBASupportSettings) 
	    CORBASupportSettings.findObject (CORBASupportSettings.class, true);	
	
	return cs.getReplaceableStringsProps();
    }


   /** This entry defines the format for replacing the text during
    * instantiation the data object.
    */
   public class IDLFileEntry extends FileEntry.Format {

      /** Creates new IDLFileEntry */
      IDLFileEntry(MultiDataObject obj, FileObject file) {
	 super(obj, file);
      }
    
      /** Method to provide suitable format for substitution of lines.
       *
       * @param target the target folder of the installation
       * @param n the name the file will have
       * @param e the extension the file will have
       * @return format to use for formating lines
       */
      protected java.text.Format createFormat (FileObject target, String n, String e) {
	 Map map = createStringsMap();

	 map.put("DATE", DateFormat.getDateInstance(DateFormat.LONG).format(new Date()));
	 map.put("TIME", DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
	 map.put("NAME", n);
	 //map.put("PACKAGE", target.getPackageName('.'));
      
	 MapFormat format = new MapFormat(map);
	 format.setLeftBrace("__");
	 format.setRightBrace("__");
	 format.setExactMatch(false);
	 return format;
      }
   }


}

/*
 * <<Log>>
 */
