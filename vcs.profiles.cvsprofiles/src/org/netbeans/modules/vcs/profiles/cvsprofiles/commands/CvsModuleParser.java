/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.*;

import org.netbeans.modules.vcscore.util.Debug;

/**
 *
 * @author  Martin Entlicher
 * @version 
 */
public class CvsModuleParser extends Object {

    private Debug E=new Debug("CvsModuleParser",true); // NOI18N
    private Debug D=E;
    
    private static final String PATHSEP = "/";

    /**
     * The hash table of modules by name.
     * Keys are module names.
     * Values are Vectors of: - Boolean whether it is an alias,
     *                        - if is alias the set of alias values,
     *                        - if not alias working directory, repository directory and possibly set of files.
     */
    private Hashtable modules = new Hashtable();
    
    /**
     * Table of pairs "Work path", "Rep. path"
     */
    private Hashtable dirLocations = new Hashtable();
    /**
     * Table of pairs "Work path/file", "Rep. path/file"
     */
    private Hashtable fileLocations = new Hashtable();

    private String convertLastRepPath = "";
    private String convertLastWorkPath = "";
    private boolean convertFiles = false;

    /** Creates new CvsModuleParser */
    public CvsModuleParser() {
    }

    /**
     * Add the module definition.
     */
    public void addModule(String moduleDef) {
        D.deb("addModule("+moduleDef+")");
        int index = moduleDef.indexOf(' ');
        if (index < 0) return;
        int len = moduleDef.length();
        String moduleName = moduleDef.substring(0, index);
        if (modules.containsKey(moduleName)) return; // The module has duplicate definition => ignoring
        String moduleDir = moduleName;
        boolean alias = false;
        while(true) {
            while(index < len && Character.isWhitespace(moduleDef.charAt(index))) index++;
            if (index >= len) return;
            if (moduleDef.regionMatches(index, "-a", 0, "-a".length())) {
                alias = true;
                index += "-a".length();
            }
            if (moduleDef.regionMatches(index, "-d", 0, "-d".length())) {
                index += "-d".length();
                while(index < len && Character.isWhitespace(moduleDef.charAt(index))) index++;
                if (index >= len) return;
                int index2 = moduleDef.indexOf(' ', index);
                if (index2 < 0) return;
                moduleDir = moduleDef.substring(index, index2);
                index = index2;
            }
            if (moduleDef.charAt(index) == '-') { // an other option
                index += 2;
                while(index < len && Character.isWhitespace(moduleDef.charAt(index))) index++;
                if (index >= len) return;
                int index2 = moduleDef.indexOf(' ', index);
                if (index2 < 0) return;
                index = index2;
            } else break;
        }
        D.deb("Found module going into directory "+moduleDir+", alias = "+alias);
        D.deb("Module definition: "+moduleDef.substring(index));
        Vector module = new Vector();
        module.add(moduleDef.trim());
        module.add(new Boolean(alias));
        modules.put(moduleName, module);
        D.deb("Have module "+moduleName+", with content:"+module);
        if (alias) {
            while(true) {
                while(index < len && Character.isWhitespace(moduleDef.charAt(index))) index++;
                if (index >= len) break;
                int index2 = moduleDef.indexOf(' ', index);
                if (index2 < 0) index2 = len;
                String file = moduleDef.substring(index, index2);
                module.add(file);
                index = index2;
                //D.deb("adding "+file);
            }
        } else {
            while(index < len && Character.isWhitespace(moduleDef.charAt(index))) index++;
            int index2 = moduleDef.indexOf(' ', index);
            if (index2 < 0) index2 = len;
            int index3 = moduleDef.indexOf('&', index+1);
            if (index3 >= 0 && index3 < index2) index2 = index3;
            String repDir = moduleDef.substring(index, index2);
            /*
            if (repDir.length() > 0 && repDir.charAt(0) == '&') { // ampersand module
                repDir = repDir.substring(1);
            }
            */
            D.deb("moduleDir = "+moduleDir);
            D.deb("repDir = "+repDir);
            module.add(moduleDir);
            module.add(repDir);
            D.deb("Have module "+moduleName+", with content:"+module);
            index = index2;
            D.deb("index = "+index);
            boolean fileDefined = false;
            while(true) {
                while(index < len && Character.isWhitespace(moduleDef.charAt(index))) index++;
                if (index >= len) break;
                index2 = moduleDef.indexOf(' ', index);
                if (index2 < 0) index2 = len;
                index3 = moduleDef.indexOf('&', index+1);
                if (index3 > 0 && index3 < index2) {
                    index2 = index3;
                }
                D.deb("index2 = "+index2);
                String file = moduleDef.substring(index, index2);
                D.deb("file = "+file);
                if (file.length() > 0 && file.charAt(0) == '&') {
		    D.deb("put("+moduleName+PATHSEP+file.substring(1)+", "+file+")");
                    dirLocations.put(moduleName+PATHSEP+file.substring(1), file); // file = &module_name
                } else {
                    fileDefined = true;
                    D.deb("fileLocations.put("+moduleDir+", "+repDir+PATHSEP+file+")");
                    fileLocations.put(moduleDir+PATHSEP+file, repDir+PATHSEP+file);
                }
                module.add(file);
                index = index2;
                //D.deb("adding "+file);
            }
            if (!fileDefined) {
		if (repDir.length() > 0 && repDir.charAt(0) == '&') {
		    D.deb("put("+moduleDir+PATHSEP+repDir.substring(1)+", "+ repDir+")");
		    dirLocations.put(moduleDir+PATHSEP+repDir.substring(1), repDir);
		} else {
		    D.deb("put("+moduleDir+", "+repDir+")");
		    dirLocations.put(moduleDir, repDir);
		}
	    }
        }
        /*
        D.deb("Modules defined so far:");
        for(Enumeration enum2 = modules.keys(); enum2.hasMoreElements(); ) {
            String moduleName2 = (String) enum2.nextElement();
            D.deb("Module '"+moduleName2+"' : "+(Vector) modules.get(moduleName2));
        }
        */
    }
    
    /**
     * Set proper values when there are symbolic module links (ampersand modules).
     */
    public void resolveModuleLinks() {
        D.deb("resolveModuleLinks():");
        for(Enumeration enum = dirLocations.keys(); enum.hasMoreElements(); ) {
            String work = (String) enum.nextElement();
            String rep = (String) dirLocations.get(work);
            if (rep.length() > 0 && rep.charAt(0) == '&') { // resolve module link
                String moduleName = rep.substring(1);
                D.deb("resolving rep = "+rep+", work = "+work+", moduleName = "+moduleName);
                boolean match = false;
                for(Enumeration enum2 = modules.keys(); enum2.hasMoreElements(); ) {
                    String moduleName2 = (String) enum2.nextElement();
                    D.deb("    comparing to "+moduleName2);
                    if (moduleName2.equals(moduleName)) {
                        match = true;
                        Vector module = (Vector) modules.get(moduleName2);
                        Boolean alias = (Boolean) module.get(1);
                        D.deb("comparison successfull, alias = "+alias);
                        int n = module.size();
			dirLocations.remove(work);
                        if (alias.booleanValue()) {
                            for(int i = 2; i < n; i++) {
                                String moduleRep = (String) module.get(i);
                                D.deb("put("+work+PATHSEP+moduleRep+", "+moduleRep+")");
                                dirLocations.put(work+PATHSEP+moduleRep, moduleRep);
                            }
                        } else {
                            boolean fileDefined = false;
                            String moduleDir = (String) module.get(2);
                            String moduleRep = (String) module.get(3);
                            for(int i = 3; i < n; i++) {
                                String file = (String) module.get(i);
                                if (file.charAt(0) == '&') {
                                    D.deb("put("+work+/*PATHSEP+moduleDir+*/PATHSEP+file.substring(1)+", "+file+")");
                                    dirLocations.put(work+/*PATHSEP+moduleDir+*/PATHSEP+file.substring(1), file);
                                } else {
                                    fileDefined = true;
                                    D.deb("put("+work+/*PATHSEP+moduleDir+*/PATHSEP+file+", "+moduleRep+PATHSEP+file+")");
                                    fileLocations.put(work/*+PATHSEP+moduleDir*/+PATHSEP+file, moduleRep+PATHSEP+file);
                                }
                            }
                            if (!fileDefined) {
                                if (moduleRep.charAt(0) == '&') {
                                    D.deb("put("+work+/*PATHSEP+moduleDir+*/PATHSEP+moduleRep.substring(1)+", "+moduleRep+")");
                                    dirLocations.put(work+/*PATHSEP+moduleDir+*/PATHSEP+moduleRep.substring(1), moduleRep);
                                } else {
                                    D.deb("put("+work+/*PATHSEP+moduleDir+*/PATHSEP+moduleRep+", "+moduleRep+")");
                                    dirLocations.put(work+/*PATHSEP+moduleDir+*/PATHSEP+moduleRep, moduleRep);
                                }
                            }
                        }
                        //dirLocations.remove(work);
                        break;
                    }
                }
                if (!match) { // module is not defined => considering as a directory
                    D.deb("Module '"+moduleName+"' not defined => put("+work/*+PATHSEP+moduleName*/+", "+moduleName+")");
                    dirLocations.put(work/*+PATHSEP+moduleName*/, moduleName);
                    //dirLocations.remove(work);
                }
                enum = dirLocations.keys(); // dirLocations chaned => have to recreate Enumeration
            }
        }
        D.deb("resolveModuleLinks() done.");
    }

    public Vector getModuleNames() {
        Vector moduleNames = new Vector();
        Enumeration enum = modules.keys();
        while(enum.hasMoreElements()) {
            moduleNames.add(enum.nextElement());
        }
        return moduleNames;
    }

    public String getModuleDef(String name) {
        Vector module = (Vector) modules.get(name);
        if (module == null) return null;
        return (String) module.get(0);
    }

    /**
     * Convert the repository path to working paths based on module definitions.
     * @return the working paths or null when the conversion can not be done.
     */
    public String[] convertRepPathToWorking(String repPath, String fileName, boolean fileDependent[]) {
        D.deb("convertRepPathToWorking("+repPath+", "+fileName+", "+fileDependent[0]+")");
        Vector workings = new Vector();
        fileDependent[0] = false;
        for(Enumeration enum = dirLocations.keys(); enum.hasMoreElements(); ) {
            String work = (String) enum.nextElement();
            String rep = (String) dirLocations.get(work);
            if (repPath.regionMatches(0, rep, 0, rep.length())) {
                workings.add(work+repPath.substring(rep.length()));
            }
        }
        String filePath = repPath+PATHSEP+fileName;
        D.deb("filePath = "+filePath);
        for(Enumeration enum = fileLocations.keys(); enum.hasMoreElements(); ) {
            String work = (String) enum.nextElement();
            String rep = (String) fileLocations.get(work);
            if (rep.equals(filePath)) {
                int index = work.lastIndexOf(PATHSEP);
                if (index > 0) work = work.substring(0, index);
                workings.add(new String(work));
            }
            D.deb("'"+rep+"'.equals("+filePath+") = "+rep.equals(filePath));
            D.deb("  => "+((rep.equals(filePath)) ? "adding '"+work+"'" : "NOT adding '"+work+"'"));
            fileDependent[0] = true;
        }
        if (workings.isEmpty()) return null;
        else return (String[]) workings.toArray(new String[0]);
    }

}
