/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Satya
 */
public class CoreUtil {
    
    /**
     * 
     */
    public static final String CORE_LOGGER = "PORTAL_PLUGIN_CORE";
    
    /**
     * 
     * @param name 
     * @return 
     */
    public static boolean validateJavaTypeName(String name) {
        if (name == null) {
            return false;
        }
        String trimmed = name.trim();
        if (!name.equals(trimmed)) {
            return false;
        }
        int index = name.lastIndexOf('.');
        if (index == -1) {
            return isIdentifier(name);
            
        }
        return false;
              /*
                else {
                        // qualified name
                        String pkg = name.substring(0, index).trim();
                        if (!validatePackageName(pkg))return false;
                        // sequence of identifiers splitted by dots
                        String type = name.substring(index + 1).trim();
                        if (!scannedIdentifier(type))return false;
                        return true;
                }*/
    }
    
    /**
     * 
     * @param name 
     * @return 
     */
    public static boolean validatePackageName(String name)
    {
        if (name == null || name.trim().equals("")) {
            return true;
        }
        String trimmed = name.trim();
        if (!name.equals(trimmed)) {
            return false;
        }
        
        StringTokenizer st = new StringTokenizer(name,".");
        
        while(st.hasMoreElements()){
            String pkg = (String)st.nextElement();
            if(!isIdentifier(pkg))
                return false;
        }
        
        return true;
              
    }
    
    /**
     * 
     * @param name 
     * @return 
     */
    public static boolean isIdentifier(String name){
        if (name.length()==0)return false;
        if (!Character.isJavaIdentifierStart(name.charAt(0))){
            return false;
        }
        for (int a=1;a<name.length();a++){
            if
                    (!Character.isJavaIdentifierPart(name.charAt(a))){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param name 
     * @param allowSpaces 
     * @return 
     */
    public static boolean validateString(String name, boolean allowSpaces) {
        if(name == null || name.trim().length() == 0){
            return false;
        }
        String value = name.trim();
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if(!Character.isLetterOrDigit(c) && !((c == '_') || (allowSpaces && c == ' '))){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param name 
     * @return 
     */
    public static boolean validateXmlString(String name) {
        if(name == null || name.trim().length() == 0){
            return true;
        }
        String value = name.trim();
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if((c == '<') || (c == '>')) {
                return false;
            }
        }
        return true;
    }
    
     /**
      * 
      * @param file 
      * @return 
      * @throws org.xml.sax.SAXException 
      * @throws java.io.IOException 
      * @throws javax.xml.parsers.ParserConfigurationException 
      */
     public static Document createDocumentFromXml(File file) throws SAXException, IOException, ParserConfigurationException
    {
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        builder.setEntityResolver( new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException,IOException {
                return new InputSource(new StringBufferInputStream(""));
            }
        });
            
        Document document = builder.parse(file);
        return document;
    }
    
    /**
     * 
     * @param fileName 
     * @return 
     */
    public static boolean checkIfFileNeedsTobeOverwritten(String fileName) {
        String message =  fileName + "  " + NbBundle.getBundle(CoreUtil.class).getString("MSG_ALREADY_EXISTS");
        
        NotifyDescriptor nd = new NotifyDescriptor(message,NbBundle.getBundle(CoreUtil.class).getString("LBL_Overwrite_Warning"),NotifyDescriptor.YES_NO_CANCEL_OPTION,NotifyDescriptor.WARNING_MESSAGE,new Object[]{NotifyDescriptor.YES_OPTION,
        NotifyDescriptor.NO_OPTION},NotifyDescriptor.NO_OPTION);
        Object ob = DialogDisplayer.getDefault().notify(nd);
        if(ob == NotifyDescriptor.YES_OPTION) {
            //logger.finest("Yes to all option...");
            return true;
        } else if(ob == NotifyDescriptor.NO_OPTION) {
            //logger.finest("No Option.....");
            return false;
        } else{
            return false;
        }
    }
    
    /** Checks if the given file name can be created in the target folder.
     *
     * @param dir target directory
     * @param newObjectName name of created file
     * @param extension extension of created file
     * @return localized error message or null if all right
     */
    final public static String canUseFileName (FileObject folder, String objectName, String extension) {
        String newObjectName=objectName;
        if (extension != null && extension.length () > 0) {
            StringBuffer sb = new StringBuffer ();
            sb.append (objectName);
            sb.append ('.'); // NOI18N
            sb.append (extension);
            newObjectName = sb.toString ();
        }
        
        // check file name
        
        if (!checkFileName(objectName)) {
            return NbBundle.getMessage (CoreUtil.class, "MSG_invalid_filename", newObjectName); // NOI18N
        }
            
        // test whether the selected folder on selected filesystem is read-only or exists
        if (folder!=  null) {
            // target filesystem should be writable
            if (!folder.canWrite ()) {
                return NbBundle.getMessage (CoreUtil.class, "MSG_fs_is_readonly"); // NOI18N
            }

            if (folder.getFileObject(newObjectName) != null) {
                return NbBundle.getMessage (CoreUtil.class, "MSG_file_already_exist", newObjectName); // NOI18N
            }

            if (org.openide.util.Utilities.isWindows ()) {
                if (checkCaseInsensitiveName (folder, newObjectName)) {
                    return NbBundle.getMessage (CoreUtil.class, "MSG_file_already_exist", newObjectName); // NOI18N
                }
            }
        }

        // all ok
        return null;
    }
    
    private static boolean checkFileName(String str) {
        char c[] = str.toCharArray();
        for (int i=0;i<c.length;i++) {
            if (c[i]=='\\') return false;
            if (c[i]=='/') return false;
        }
        return true;
    }
    
    // This method is copied from web module
    /** Check existence of file on case insensitive filesystem.
     * Returns true if folder contains file with given name and extension.
     * @param folder folder for search
     * @param name name of file
     * @param extension extension of file
     * @return true if file with name and extension exists, false otherwise.
     */    
    private static boolean checkCaseInsensitiveName (FileObject folder, String name) {
        java.util.Enumeration children = folder.getChildren (false);
        FileObject fo;
        while (children.hasMoreElements ()) {
            fo = (FileObject) children.nextElement ();
            if (name.equalsIgnoreCase (fo.getName ())) {
                return true;
            }
        }
        return false;
    }
}
