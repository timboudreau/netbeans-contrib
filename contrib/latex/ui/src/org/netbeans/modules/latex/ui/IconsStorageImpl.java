/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.modules.latex.model.IconsStorage;

import org.openide.ErrorManager;

/**
 *
 * @author Jan Lahoda
 */
public class IconsStorageImpl extends IconsStorage {
    
    private Map/*<String, Map<String, Icon>>*/ cathegory2Name2Icon;
    
    /** Creates a new instance of IconsStorageImpl */
    public IconsStorageImpl() {
//        Thread.dumpStack();
    }

    public Icon getIcon(String command) {
        return (Icon) getAllIcons().get(command);
    }

    public boolean getIconsInstalled() {
        try {
            File iDir = getIconDirectory();
            
            return iDir.exists() && iDir.isDirectory();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return false;
        }
    }

    public Map getIconsForCathegory(String catName) {
        assureLoaded();
        
        return (Map) cathegory2Name2Icon.get(catName);
    }

    public Map getAllIcons() {
        assureLoaded();
        
        Map result = new HashMap();
        
        for (Iterator i = cathegory2Name2Icon.values().iterator(); i.hasNext(); ) {
            result.putAll((Map) i.next());
        }
        
        return result;
    }
    
    private File getIconDirectory() throws IOException {
        File iconDir = new File(new File(new File(System.getProperty("netbeans.user"), "var"), "latex"), "icons");
        
        return iconDir;
    }

    public Collection getCathegories() {
        assureLoaded();
        return cathegory2Name2Icon.keySet();
    }
    
    private void loadIcon(String iconDescription) {
        int colon = iconDescription.indexOf(':');
        String command = "";
        String attributes = "";
        
        if (colon == (-1)) {
            command = iconDescription;
        } else {
            command = iconDescription.substring(0, colon);
            attributes = iconDescription.substring(colon + 1);
        }
        
        String fileName = command.replaceAll("\\\\", "");

        try {
        File iconFile = new File(getIconDirectory(), fileName + "-16x16.png");
        
        if (iconFile.canRead() && iconFile.isFile()) {
            Icon icon = new ImageIcon(iconFile.getAbsolutePath());
            
            String cathegory = DEFAULT_CATHEGORY;
            Pattern p = Pattern.compile(".*symbols_([^,]*),?.*");
            Matcher m = p.matcher(attributes);
            
            if (m.find()) {
                cathegory = m.group(1);
            }

            Map name2Icon = (Map) cathegory2Name2Icon.get(cathegory);
            
            if (name2Icon == null) {
                name2Icon = new HashMap();
                
                cathegory2Name2Icon.put(cathegory, name2Icon);
            }
            
            name2Icon.put(command, icon);
        }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private synchronized void assureLoaded() {
        if (cathegory2Name2Icon != null)
            return ;
        
        cathegory2Name2Icon = new HashMap();
        
        String[] icons = readIconsFile();
        
        for (int cntr = 0; cntr < icons.length; cntr++) {
            loadIcon(icons[cntr]);
        }
    }
    
    public/*module private*/ static String[] readIconsFile() {
        try {
            InputStream ins = IconsStorageImpl.class.getResourceAsStream("/org/netbeans/modules/latex/ui/symbols/symbols.txt");
            BufferedReader bins = new BufferedReader(new InputStreamReader(ins));
            List icons = new ArrayList();
            String line;
            
            while ((line = bins.readLine()) != null) {
                icons.add(line);
            }
            
            return (String[] ) icons.toArray(new String[0]);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return new String[0];
        }
    }

    public String getCathegoryDisplayName(String catName) {
        return catName;
    }

}
