/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.xml.EntityCatalog;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public final class CommandPackage extends NamedAttributableWithSubElements {
    
    private static final boolean debug = Boolean.getBoolean("netbeans.latex.commandpackages.debug");
    private static final boolean timeDebug =    Boolean.getBoolean("netbeans.latex.commandpackages.time.debug")
                                             || Boolean.getBoolean("netbeans.latex.commandpackages.debug");

    private static final String DEFAULT_DOCUMENT_CLASS = "default-latex-docclass"; //TODO: find some better place and make it customizable
    
    public static final int PACKAGE = 1;
    public static final int DOCUMENT_CLASS = 2;
    
    private int type = 0;
    
    private Map/*<String, Option>*/ options;
    private Set/*<String>*/              includes;
    
    /*package private*/void setType(int type) {
        this.type = type;
    }
    
    /** Creates a new instance of Package */
    /*package private*/ CommandPackage() {
    }
    
    public int getType() {
        return type;
    }
    
    public synchronized Map getOptions() {
        if (options == null)
            return options = new HashMap();
        
        return options;
    }
    
    public synchronized Set getIncludes() {
        if (includes == null)
            return includes = new HashSet();

        return includes;
    }
    
    private static Map/*<String, CommandPackage>*/ name2documentClass = null;
    private static Map/*<String, CommandPackage>*/ name2package = null;
    
    private static synchronized Map/*<String, CommandPackage>*/ getName2DocumentClass() {
        if (name2documentClass == null) {
            load();
        }
        
        return name2documentClass;
    }
    
    private static synchronized Map/*<String, CommandPackage>*/ getName2Package() {
        if (name2package == null) {
            load();
        }
        
        return name2package;
    }
    
    public static CommandPackage getDefaultDocumentClass() {
        CommandPackage cp = getCommandPackageForName(DEFAULT_DOCUMENT_CLASS);
        
        if (cp.getType() != DOCUMENT_CLASS)
            return null;
        
        return cp;
    }

    public static CommandPackage getCommandPackageForName(String name) {
        CommandPackage result = (CommandPackage) getName2DocumentClass().get(name);
        
        if (result != null)
            return result;
        
        return (CommandPackage) getName2Package().get(name);
    }

    /**Returns the collection of names of known document classes.
     *
     * @returns Collection<String> of names of known document classes.
     */
    public static synchronized Collection getKnownDocumentClasses() {
        return Collections.unmodifiableCollection(getName2DocumentClass().keySet());
    }
    
    /**Returns the collection of names of known packages.
     *
     * @returns Collection<String> of names of known packages.
     */
    public static synchronized Collection getKnownPackages() {
        return Collections.unmodifiableCollection(getName2Package().keySet());
    }
    
    private static final String FILE_ROOT = "latex/commands2";
    
    private static synchronized void load() {
        long start = System.currentTimeMillis();
        
        name2documentClass = new HashMap();
        name2package = new HashMap();
        
        FileObject root = Repository.getDefault().getDefaultFileSystem().findResource(FILE_ROOT);
        FileObject[] children = root.getChildren();
        
        for (int cntr = 0; cntr < children.length; cntr++) {
            if (children[cntr].getAttribute("command-package") == null)
                continue;
            
            loadCommandPackage(children[cntr]);
            
            if (debug)
                System.err.println("found command package: " + children[cntr]);
        }
        
        long end = System.currentTimeMillis();
        
        if (timeDebug)
            System.err.println("Loading of command packages took: " + (end - start));
    }
    
    private static synchronized void loadCommandPackage(FileObject fo) {
        try {
            Command_descriptionHandlerImpl handler = new Command_descriptionHandlerImpl();
            
            new Command_descriptionParser(handler, EntityCatalog.getDefault()).parse(fo.getURL());
            
            CommandPackage pack = handler.getCommandPackage();
            
            switch (pack.getType()) {
                case CommandPackage.PACKAGE:
                    name2package.put(pack.getName(), pack);
                    break;
                case CommandPackage.DOCUMENT_CLASS:
                    name2documentClass.put(pack.getName(), pack);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command package type: " + pack.getType());
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        } catch(SAXException e) {
            e.printStackTrace(System.err);
        } catch(ParserConfigurationException e) {
            e.printStackTrace(System.err);
        }
    }
}
