/*
Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
You may not modify, use, reproduce, or distribute this software
except in compliance with the terms of the License at:
 http://developer.sun.com/berkeley_license.html
 */
package org.netbeans.modules.portalpack.commons.palette.jsp;

import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;

/**
 *
 * @author Satyaranjan
 */
public class PaletteEntry {
    
    public static final String JSP_PALETTE = "/JSPPalette";
    public static final String HTML_PALETTE = "/HTMLPalette";
    
    public static final String PP_JSP_PALETTE = "portalpack/palette/jsp";
    
    private static final String COMP_NAME_TOKEN = "__COMP_NAME__";
    private static final String paletteEntryXml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE editor_palette_item PUBLIC \"-//NetBeans//Editor Palette Item 1.0//EN\" \"http://www.netbeans.org/dtds/editor-palette-item-1_0.dtd\">\n" +
        "<editor_palette_item version=\"1.0\">\n" +
        "    <class name=\"" + COMP_NAME_TOKEN + "\"/>\n" +
        "    <icon16 urlvalue=\"org/netbeans/modules/sun/jmaki/palette/resources/jmaki16.jpg\"/>\n" +
        "    <icon32 urlvalue=\"org/netbeans/modules/sun/jmaki/palette/resources/jmaki32.jpg\"/>\n" +
        "    <description localizing-bundle=\"" + COMP_NAME_TOKEN + ".Bundle\"\n" +
        "               display-name-key=\"NAME_" + COMP_NAME_TOKEN + "\"\n" +
        "               tooltip-key=\"HINT_" + COMP_NAME_TOKEN + "\"/>\n" +
        "</editor_palette_item>\n";
    
    /** Register the new component in a palette area called from libraryName
     */
    public static boolean addEntry(final String palette, final String libraryName, final String componentName) {
        final Repository rep = (Repository) Lookup.getDefault().lookup(Repository.class);
        FileObject libsFolder = rep.getDefaultFileSystem().findResource(palette + "/" + libraryName); //NOI18N
        if (libsFolder == null) {
            FileObject l1 = rep.getDefaultFileSystem().findResource(palette);
            try {
                libsFolder = l1.createFolder(libraryName);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                return false;
            }
        }

        //  the palette entry descriptor file.
        final String componentXmlName = componentName + ".xml";
        FileObject entry = libsFolder.getFileObject(componentXmlName);
        if(entry == null) {
            try {
                Util.atomicWriteString(libsFolder, componentXmlName, 
                        paletteEntryXml.replaceAll(COMP_NAME_TOKEN, componentName), "UTF-8");
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                return false;
            }
        }
        
        return true;
    }
    
     private static FileObject getFolder(String listenerFolder) {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fo = fs.getRoot().getFileObject(listenerFolder);
        return fo;
    }

    
}
