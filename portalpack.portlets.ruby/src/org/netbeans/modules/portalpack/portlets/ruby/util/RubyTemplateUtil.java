/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.portlets.ruby.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.ruby.RubyPortletConstants;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author PrakashR
 */
public class RubyTemplateUtil {
    
    private static String templateFolder = "ruby/templates";
    private static FileObject folder;
    protected static Logger logger = Logger.getLogger(RubyPortletConstants.RUBY_PORTLET_LOGGER);

    public static boolean createFileFromTemplate(String templateName,FileObject destObj,
                                                 String fileName, String extentionName){
        FileObject templateFile = getTemplateFile(templateName);
        if(templateFile == null)
        {
            logger.severe("Template File "+templateName + " not found !!!");
            return false;
        } 
        
        if(destObj == null)
        {
            logger.severe("Destination Object is null !!!");
            return false;
        }
        try {
            FileUtil.copyFile(templateFile, destObj, fileName, extentionName);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error creating file : " + fileName, ex);
            return false;
        }

        return true;
    }
    
    /**
     * It returns the template FileObject
     * 
     * @param name Template name. Template name is usually specified in the layer.xml 
     * @return Template FileObject
     **/
    public static FileObject getTemplateFile(String name) {
        FileObject fo = getFolder() != null ? getFolder().getFileObject(name) : null;
        return fo;
    }

    public static FileObject getFolder() {
        if (folder == null) {
            folder = Repository.getDefault().getDefaultFileSystem().findResource(templateFolder);
        }
        return folder;
    }

    public static void setFolder(FileObject folder) {
        RubyTemplateUtil.folder = folder;
    }
    
    public static FileObject mergeTemplateToFile(FileObject templateFileObj,FileObject folder,String fileName,Map values)
            throws DataObjectNotFoundException, IOException {
         DataObject dbObj = DataObject.find(templateFileObj);
         
         DataFolder folderObj = DataFolder.findFolder(folder);
         DataObject newdobj = dbObj.createFromTemplate(folderObj, fileName, values);
         if(newdobj == null)
             return null;
         return newdobj.getPrimaryFile();              
         
    }

}
