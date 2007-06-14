
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package com.sun.tthub.gde.ui.panels;

import com.sun.tthub.gde.util.FileUtilities;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;

/**
 * This class represents the filter that can be used to limit the files 
 * displayed in the file chooser.
 *
 * @author Hareesh Ravindran
 */
public class CustomFileFilter extends FileFilter {

    
    // Controls the display of files in the file chooser, based on the user
    // permissions. This will prevent the user from choosing files which do not
    // have appropriate permissions.
    public static final int PERMISSION_NONE = 0;    
    public static final int PERMISSION_READ = 1;
    public static final int PERMISSION_WRITE = 2;
    public static final int PERMISSION_READWRITE = 3;

    
    protected int filePermission = PERMISSION_NONE;
    protected boolean chooseOnlyDir = false;
    /**
     * Creates an emtyp file filter. This does not do anything. If this filter
     * is used as it is, in the FileChooser, it will display all the files.
     */
    public CustomFileFilter() {}
    
    /**
     * This constructor can be used for setting the description and the list
     * of extensions in a single step. The extensions should be specified as 
     * a string and each extension should be separated from the other using
     * a comma.
     */
    public CustomFileFilter(String description, String extensions) {
        this.description = description;
        String[] extArr = extensions.split(",");
        for(int i = 0; i < extArr.length; ++i) {
            String str = extArr[i];
            // Do not add if the string is empty.
            if(str == null || str.trim().equals(""))
                continue;
            this.extensionLst.add(extArr[i]);
        }
    }
    
    public CustomFileFilter(String description, boolean chooseOnlyDir) {
        this.description = description;
        this.chooseOnlyDir = chooseOnlyDir;
    }
    
    // Initialize a hash set to store 5 String objects.
    private Collection extensionLst = new HashSet(5);
    private String description;
    /**
     * This function adds the specified extension to the list maintained in the
     * value. Null value should not be added to the collection.
     */
    public void addExtension(String extension) {
        extensionLst.add(extension); // add the new extension to the list.
    }
        
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Following are the rules that are applied to determine whether to display
     * a file or not in the file chooser. 
     */
    public boolean accept(File file) {        
        
        // Check for permissions. Currently, this will check only for read 
        // permissions. This code has to be rewritten to consider all the
        // permissions listed above. Currently, the method returns false
        // if the user does not have read access to the file/directory.
        if(!file.canRead()) 
            return false;
        
        // If the file is a directory, return true.
        if(file.isDirectory()) {
            return true;
        }
        
        if(chooseOnlyDir) {
            return false;
        }
        
        // If the size of the extension list array is 0, then accept all files.      
        if(extensionLst.size() == 0) {
            return true;
        }                
        String extStr = FileUtilities.getExtension(file.getName()); // get the extension of the file
        // If one of the extenstions matches with the current file extension,
        // return true, else return false.
        for(Iterator it = extensionLst.iterator(); it.hasNext(); ) {
            String str = (String) it.next();
            // If a null value is there in the list, then select the files
            // with no extension. 
            if(str == null) {
                if(extStr == null)
                    return true;
                continue; // skip to the next element in the list.
            }
            if(str.equals(extStr)) {
                return true;
            }
        }
        return false;
    }
    
    public String getDescription() {
        return this.description;
    }    
    
}
