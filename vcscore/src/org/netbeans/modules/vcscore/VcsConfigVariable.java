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

package org.netbeans.modules.vcscore;

import java.util.*;
import java.io.*;
import java.text.*;

import org.openide.util.*;
import org.openide.filesystems.FileObject;

import org.netbeans.modules.vcscore.util.*;
//import org.netbeans.modules.vcscore.commands.VcsCommandOrder;

/**
 *
 * @author  Pavel Buzek, Martin Entlicher
 */

public class VcsConfigVariable extends Object implements Cloneable, Serializable {
    private static Debug E=new Debug("VcsConfigVariable", true); // NOI18N
    private static Debug D=E;

    /**
     * The variable name.
     */
    private String name;          // The variable name
    /**
     * The label of this variable in the Customizer.
     */
    private String label;         // The label of that variable in the Customizer
    /**
     * The value of the variable.
     */
    private String value;         // The value of this variable
    /**
     * Whether this variable is basic or not. Basic variables can be set in the Customizer.
     */
    private boolean basic;       // Whether this variable is basic or not (basic variables can be set in the Customizer)
    /**
     * Whether this variable is a local file. Browse buttom will be created for this variable.
     */
    private boolean localFile;   // Whether this variable is a local file. Browse buttom will be created for this variable.
    /**
     * Whether this variable is a local directory. Browse buttom will be created for this variable.
     */
    private boolean localDir;    // Whether this variable is a local directory. Browse buttom will be created for this variable.
    /**
     * Whether this variable is an executable. Browse button will be created for this variable.
     */
    private boolean executable;
    /**
     * The custom selector for this variable.
     * Select button will be created in the customizer and selector executed on its action.
     */
    private String customSelector;// The custom selector for this variable
    /**
     * The order of the variable in the Customizer.
     */
    private int order;           // The order of the variable in the Customizer

    static final long serialVersionUID =4230769028627379053L;

    /** Creates new VcsConfigVariable with zero order.
    * @param name the variable name
    * @param label the label of that variable in the Customizer
    * @param value the value of this variable
    * @param basic whether this variable is basic or not. Basic variables can be set in the Customizer.
    * @param localFile whether this variable is a local file. Browse buttom will be created for this variable.
    * @param localDir whether this variable is a local directory. Browse buttom will be created for this variable.
    * @param customSelector the custom selector for this variable. It can be a subclass of <code>VcsVariableSelector</code> or an executable.
    */
    public VcsConfigVariable(String name, String label, String value,
                             boolean basic, boolean localFile, boolean localDir,
                             String customSelector) {
        this(name, label, value, basic, localFile, localDir, customSelector, 0);
    }

    /** Creates new VcsConfigVariable
    * @param name the variable name
    * @param label the label of that variable in the Customizer
    * @param value the value of this variable
    * @param basic whether this variable is basic or not. Basic variables can be set in the Customizer.
    * @param localFile whether this variable is a local file. Browse buttom will be created for this variable.
    * @param localDir whether this variable is a local directory. Browse buttom will be created for this variable.
    * @param customSelector the custom selector for this variable. It can be a subclass of <code>VcsVariableSelector</code> or an executable.
    * @param order the order of this variable in the Customizer.
    */
    public VcsConfigVariable(String name, String label, String value,
                             boolean basic, boolean localFile, boolean localDir,
                             String customSelector, int order) {
        this.name = name;
        this.label = label;
        this.value = value;
        this.basic = basic;
        this.localFile = localFile;
        this.localDir = localDir;
        this.customSelector = customSelector;
        this.order = order;
        this.executable = false;
    }

    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    public String getLabel () { return label;  }
    public void setLabel (String label) { this.label = label;  }
    public String getValue () { return value;  }
    public void setValue (String value) { this.value = value;  }
    public boolean isBasic () { return basic;  }
    public void setBasic (boolean basic) { this.basic = basic;  }
    public boolean isLocalFile() { return localFile;  }
    public void setLocalFile (boolean localFile) { this.localFile = localFile;  }
    public boolean isLocalDir() { return localDir;  }
    public void setLocalDir (boolean localDir) { this.localDir = localDir;  }
    public boolean isExecutable() { return executable; }
    public void setExecutable(boolean executable) { this.executable = executable; }
    public String getCustomSelector () { return customSelector; }
    public void setCustomSelector (String customSelector) { this.customSelector = customSelector; }
    public void setOrder(int order) { this.order = order; }
    public int getOrder() { return this.order; }

    public String toString () {
        String strBasic = ""; // NOI18N
        if (isBasic ()) strBasic = "(basic)"; // NOI18N
        if (isLocalFile ()) strBasic += "(local file)"; // NOI18N
        if (isLocalDir ()) strBasic += "(local directory)"; // NOI18N
        if (isExecutable()) strBasic += "(executable)"; // NOI18N
        return name+"("+label+")"+strBasic+"="+value; // NOI18N
    }

    public Object clone () {
        VcsConfigVariable cloneVar = new VcsConfigVariable (name, label, value, basic, localFile, localDir, customSelector, order);
        cloneVar.setExecutable(isExecutable());
        return cloneVar;
    }


    /* Read list of VCS variables from properties. Variables are stored as
    * var.<NAME>.value and optionaly var.<NAME>.label, var.<NAME>.basic,
    * var.<NAME>.localFile or var.<NAME>.localDir.
    * If there is only value specified, label is empty string and basic, localFile
    * and localDir are false.
    *
    public static Vector readVariables(Properties props){
        Vector result=new Vector(20);
        String VAR_PREFIX = "var."; // NOI18N
        for(Iterator iter=props.keySet().iterator(); iter.hasNext();){
            String key=(String)iter.next();
            if(key.startsWith(VAR_PREFIX) && key.endsWith(".value")) { // NOI18N
                int startIndex = VAR_PREFIX.length ();
                int endIndex = key.length() - ".value".length (); // NOI18N

                String name = key.substring(startIndex, endIndex);
                String value = (String) props.get(key);

                String label = (String) props.get(VAR_PREFIX + name + ".label"); // NOI18N
                if (label == null) label = ""; // NOI18N

                String strBasic = (String) props.get(VAR_PREFIX + name + ".basic"); // NOI18N
                boolean basic = (strBasic != null) && (strBasic.equalsIgnoreCase ("true")); // NOI18N

                String strLocalFile = (String) props.get(VAR_PREFIX + name + ".localFile"); // NOI18N
                boolean localFile = (strLocalFile != null) && (strLocalFile.equalsIgnoreCase ("true")); // NOI18N

                String strLocalDir = (String) props.get(VAR_PREFIX + name + ".localDir"); // NOI18N
                boolean localDir = (strLocalDir != null) && (strLocalDir.equalsIgnoreCase ("true")); // NOI18N

                String strExec = (String) props.get(VAR_PREFIX + name + ".executable"); // NOI18N
                boolean exec = (strExec != null) && (strExec.equalsIgnoreCase ("true")); // NOI18N

                String customSelector=(String)props.get(VAR_PREFIX + name + ".selector"); // NOI18N
                if (customSelector == null) customSelector = "";

                String orderStr = (String) props.get(VAR_PREFIX + name + ".order"); // NOI18N
                int order = -1;
                if (orderStr != null) {
                    try {
                        order = Integer.parseInt(orderStr);
                    } catch (NumberFormatException e) {
                        // ignoring
                        order = -1;
                    }
                }
                VcsConfigVariable var = new VcsConfigVariable (name, label, value, basic, localFile, localDir, customSelector, order);
                var.setExecutable(exec);
                result.addElement(var);
            }
        }
        result = VcsConfigVariable.sortVariables(result);
        return result;
    }
     */

    /*
     * Write the configuration properties into the file.
     * @param file the file into which the properties will be stored.
     * @param label the label to use.
     * @param vars the variables to save.
     * @param advanced the advanced configuration properties (commands).
     * @param cust the advanced customizer used to write the advanced properties.
     *
    public static void writeConfiguration (FileObject file, String label, Vector vars,
                                           Object advanced, VcsAdvancedCustomizer cust) {
        Properties props=new Properties();
        props.setProperty ("label", label); // NOI18N
        props.setProperty ("debug", "true"); // NOI18N
        for(int i=0; i<vars.size (); i++) {
            VcsConfigVariable var = (VcsConfigVariable) vars.get (i);
            String name = var.getName();
            props.setProperty ("var." + name + ".value", var.getValue ()); // NOI18N
            if(!var.getLabel ().equals ("")) { // NOI18N
                props.setProperty ("var." + name + ".label", var.getLabel ()); // NOI18N
                props.setProperty ("var." + name + ".basic", "" + var.isBasic ()); // NOI18N
            } else if(var.isBasic ()) {
                props.setProperty ("var." + name + ".basic", "true"); // NOI18N
            }
            props.setProperty ("var." + name + ".localFile", "" + var.isLocalFile()); // NOI18N
            props.setProperty ("var." + name + ".localDir", "" + var.isLocalDir()); // NOI18N
            props.setProperty ("var." + name + ".executable", "" + var.isExecutable()); // NOI18N
            props.setProperty ("var." + name + ".selector", "" + var.getCustomSelector()); // NOI18N
            props.setProperty ("var." + name + ".order", "" + var.getOrder()); // NOI18N
        }
        cust.writeConfig (props, advanced);
        try{
            OutputStream out = file.getOutputStream(file.lock());
            props.store (out, g("MSG_User_defined_configuration")); // NOI18N
            out.close();
        }
        catch(IOException e){
            E.err(e,g("EXC_Problems_while_writting_user_defined_configuration",file.getName())); // NOI18N
        }
    }
     */

    /*
    private static boolean bundleListContains(FileObject[] list, String name) {
        for(int i = 0; i < list.length; i++) {
            String buName = list[i].getName();
            if (buName.equals(name)) return true;
        }
        return false;
    }

    /*
     * Find out whether the locale extension exists as a locale name.
     *
    private static boolean isLocale(String localeExt) {
        String lang;
        int index = localeExt.indexOf('_');
        if (index > 0) lang = localeExt.substring(0, index);
        else lang = localeExt;
        String[] languages = Locale.getISOLanguages();
        List list = Arrays.asList(languages);
        return list.contains(lang);
    }
     */
    
    /* Read list of available confugurations from the directory.
     * All files with extension ".properties" are considered to be configurations.
     * However only properties with current localization are read.
     * @return the available configurations.
     *
    public static Vector readConfigurations(FileObject file) {
        Vector res = new Vector(5);
        FileObject[] ch = file.getChildren();
        ArrayList list = new ArrayList(Arrays.asList(ch));
        for(int i = 0; i < list.size(); i++) {
            if (!((FileObject) list.get(i)).getExt().equalsIgnoreCase("properties")) {
                list.remove(i);
                i--;
            }
        }
        ch = (FileObject[]) list.toArray(new FileObject[0]);
        Locale locale = Locale.getDefault();
        // [PENDING] if uncommented, please use NbBundle.getLocalizingSuffixes() instead --jglick
        for(int i = 0; i < ch.length; i++) {
            String name = ch[i].getName();
            //System.out.println("name = "+name+", locale = "+locale.toString());
            int nameIndex = name.indexOf('_');
            String baseName = name;
            String localeExt = "";
            if (nameIndex > 0) {
                baseName = name.substring(0, nameIndex);
                localeExt = name.substring(nameIndex + 1);
            }
            if (localeExt.equals(locale.toString())) ; // OK
            else if (localeExt.equals(locale.getLanguage()+"_"+locale.getCountry())) {
                if (bundleListContains(ch, baseName+"_"+locale.toString())) continue; // current variant is somewhere
            } else if (localeExt.equals(locale.getLanguage())) {
                if (bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()+"_"+locale.getVariant())) continue;
            } else if (localeExt.length() == 0) {
                if (bundleListContains(ch, baseName+"_"+locale.getLanguage()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()+"_"+locale.getVariant())) continue;
            } else if (localeExt.length() > 0 && isLocale(localeExt)) continue;
            //System.out.println("adding: "+name+"."+ch[i].getExt());
            res.addElement(name+"."+ch[i].getExt());
        }
        return res;
    }
     */

    /* Open file and load properties from it.
     * @param configRoot the directory which contains properties.
     * @param name the name of properties to read.
     *
    public static Properties readPredefinedProperties(FileObject configRoot, String name){
        Properties props=new Properties();
        FileObject config = configRoot.getFileObject(name);
        if (config == null) {
            E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
            return props;
        }
        try{
            InputStream in = config.getInputStream();
            props.load(in);
            in.close();
        }
        catch(FileNotFoundException e) {
            E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
        }
        catch(IOException e){
            E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
        }
        return props;
    }
     */

    /*
    public static void main (String args[]) {
      if(args.length >0) {
        Properties props = readPredefinedProperties (args[0]);
        Enumeration en = readVariables (props).elements ();
        while(en.hasMoreElements ()) {
          VcsConfigVariable var = (VcsConfigVariable) en.nextElement ();
          System.out.println ("var(name="+var.getName ()+", label="+var.getLabel ()+", basic="+var.isBasic ()+", value="+var.getValue ()); // NOI18N
        }
      }
}
    */

    /**
     * Sort a vector of commands or variables by the orderArr property.
     * @param commands the commands or variables to sort
     * @return new sorted vector of commands or variables
     */
    public static Vector sortVariables(Vector variables) {
        //D.deb("sortCommands ()"); // NOI18N
        Vector sorted;
        //D.deb("commands = "+ commands); // NOI18N
        if (variables == null) return variables;
        Object[] vars = null;
        vars = (Object[]) variables.toArray();
        //D.deb("Doing sort ..."); // NOI18N
        java.util.Arrays.sort(vars, new Comparator() {
                                  public int compare(Object o1, Object o2) {
                                      if (o1 instanceof VcsConfigVariable)
                                          return ((VcsConfigVariable) o1).getOrder() - ((VcsConfigVariable) o2).getOrder();
                                      return 0; // the elements are not known to me
                                  }
                                  public boolean equals(Object o) {
                                      return false;
                                  }
                              });
        //D.deb("Sort finished."); // NOI18N
        sorted = new Vector();
        for(int i = 0; i < vars.length; i++) {
            sorted.addElement(vars[i]);
        }
        //D.deb("sorted vector = "+sorted); // NOI18N
        return sorted;
    }

    /*
    //-------------------------------------------
    static String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcscore.Bundle").getString (s);
    }
    static String g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    static String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    static String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }
     */
}

/*
 * Log
 *  12   Jaga      1.7.1.3     3/21/00  Martin Entlicher Added sorting of 
 *       variables.
 *  11   Jaga      1.7.1.2     3/15/00  Martin Entlicher Comment-out of methods 
 *       not used any more.
 *  10   Jaga      1.7.1.1     3/8/00   Martin Entlicher 
 *  9    Jaga      1.7.1.0     2/24/00  Martin Entlicher Custom Selector added.
 *  8    Gandalf   1.7         1/17/00  Martin Entlicher Internationalization
 *  7    Gandalf   1.6         1/6/00   Martin Entlicher 
 *  6    Gandalf   1.5         11/27/99 Patrik Knakal   
 *  5    Gandalf   1.4         11/24/99 Martin Entlicher Added localFile and 
 *       localDir properties.
 *  4    Gandalf   1.3         10/25/99 Pavel Buzek     copyright and log
 *  3    Gandalf   1.2         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  2    Gandalf   1.1         10/5/99  Pavel Buzek     VCS at least can be 
 *       mounted
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */
