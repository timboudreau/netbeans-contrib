package com.sun.jbi.sapbc.sapwsdlgenerator.explorer.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters jar files.
 */
class FileExtensionFilter extends FileFilter {

    public FileExtensionFilter(String extension, String description) {
        if (description == null) {
            description = "";
        }
        if (extension == null || extension.trim().length() == 0) {
            throw new IllegalArgumentException("extension null or blank");
        }
        this.extension = extension;
        this.description = description.concat(" (*" + extension + ")");
    }
    
    public boolean accept(File f) {
        
        if (f.isDirectory()) {
            return true;
        }
        
        String name = f.getName();
        if (name.length() >= extension.length()) {
            String tail = name.substring(name.length() - extension.length());
            return extension.equalsIgnoreCase(tail);
        }
        
        return false;
    }

    public String getDescription() {
        return description;
    }
    
    private final String description;
    private final String extension;
}
