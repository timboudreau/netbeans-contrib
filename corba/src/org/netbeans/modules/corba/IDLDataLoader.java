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
import java.util.Vector;
import java.text.DateFormat;

import org.openide.loaders.MultiFileLoader;
//import org.openide.loaders.UniFileLoader;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.FileEntry;
import org.openide.loaders.ExtensionList;
import org.openide.filesystems.FileObject;
import org.openide.actions.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.util.MapFormat;


import com.netbeans.enterprise.modules.corba.settings.*;

/** Data loader which recognizes IDL files.
*
* @author Karel Gardas
*/

public class IDLDataLoader extends MultiFileLoader {
   //public class IDLDataLoader extends UniFileLoader {

   /** Creates new IDLDataLoader */

   //private static final boolean DEBUG = true;
   private static final boolean DEBUG = false;

   CORBASupportSettings css;
   public static final String IDL_EXTENSION = "idl";

   public ExtensionList extensions = null;

   protected int fi_counter = 0;

   public IDLDataLoader() {
       super(IDLDataObject.class);
       if (DEBUG)
         System.out.println ("IDLDataLoader...");
   }

   /** Does initialization. Initializes display name,
    * extension list and the actions. */
   protected void initialize () {
      setDisplayName(NbBundle.getBundle(IDLDataLoader.class).
		     getString("PROP_IDLLoader_Name"));
      ExtensionList ext = new ExtensionList ();
      ext.addExtension(IDL_EXTENSION);
      //    ext.addExtension("properties"); // now provided by properties DataObject
      setExtensions(ext);
      setActions(new SystemAction[] {
        SystemAction.get(OpenAction.class),
        SystemAction.get(FileSystemAction.class),
  	    null,
  	    SystemAction.get(CompileAction.class),
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
      if (css == null)
	 css = (CORBASupportSettings)
	    CORBASupportSettings.findObject (CORBASupportSettings.class, true);
      if (!css.hideGeneratedFiles ())
	 return null;

      // it can be java file generated from idl
      Vector idls = findIdls (fo);
      Vector idos = new Vector ();
      // first we look if this file is marked as generated from idl
      if (DEBUG) {
	 System.out.println ("exists attribute? ");
	 System.out.flush ();
      }
      String attr = (String)fo.getAttribute ("IDL_generated_file");
      if (attr != null) {
	 // so we can now find particular idl file object
	 if (DEBUG) {
	    System.out.print ("exists " + attr + "? ");
	    System.out.flush ();
	 }
	 FileObject idl = fo.getParent ().getFileObject (attr, "idl");
	 if (idl != null) {
	    if (DEBUG) {
	       System.out.println ("yes");
	       System.out.println ("catch " + fo.getName () + " generated from " 
				   + idl.getName ());
	    }
	    return idl;
	 }
	 else
	    return null;  // is marked but idl file don't exists
      }
      for (int i=0; i<idls.size (); i++) {
	 try {
	    idos.addElement (DataObject.find ((FileObject)idls.elementAt (i)));
	 } catch (Exception e) {
	    e.printStackTrace ();
	 }
      }
      for (int i=0; i<idos.size (); i++) {
	 if (((IDLDataObject)idos.elementAt (i)).canGenerate (fo)) {
	    if (DEBUG)
	       System.out.println (fo.getName () + " generated from " 
				   + ((IDLDataObject)idos.elementAt (i)).getPrimaryFile ()
				   .getName ());
	    try {
	       fo.setAttribute ("IDL_generated_file", ((IDLDataObject)idos.elementAt (i))
				.getPrimaryFile ().getName ());
	       //this.markFile (fo);
	    } catch (IOException e) {
	       e.printStackTrace ();
	    }
	    return ((IDLDataObject)idos.elementAt (i)).getPrimaryFile ();
	 }
      }
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

   protected Vector findIdls (FileObject fo) {
      fi_counter++;
      if (DEBUG)
	 System.out.println ("IDLDataLoader.findIdls ()..." + fi_counter);
      FileObject folder = fo.getParent ();
      if (folder == null)
	 System.out.println ("!!!!NULL FOLDER!!!! - for " + fo.getName ());
      FileObject[] files = folder.getChildren ();
      Vector idls = new Vector ();
      for (int i=0; i<files.length; i++)
	 if (files[i].isData ()) // file object represent data file
	    if ("idl".equals (files[i].getExt ())) {
	       // idl file
	       idls.addElement (files[i]);
	       if (DEBUG)
		  System.out.println ("idl file: " + files[i].getName ());
	    }
      return idls;
   }

   public void setExtensions (ExtensionList e) {
      extensions = e;
   }

   public ExtensionList getExtensions () {
      return extensions;
   }

   protected Map createStringsMap() {
      /*
      CORBASupportSettings css = (CORBASupportSettings) 
	 CORBASupportSettings.findObject (CORBASupportSettings.class, true);	
      */
      if (css == null)
	 css = (CORBASupportSettings)
	    CORBASupportSettings.findObject (CORBASupportSettings.class, true);

      return css.getReplaceableStringsProps();
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





