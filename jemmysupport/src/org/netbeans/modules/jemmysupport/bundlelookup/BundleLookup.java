/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.bundlelookup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import org.netbeans.modules.jemmysupport.I18NSupport;
import org.netbeans.modules.jemmysupport.Utils;

/** Class performing lookup through all Bundle.properties files in all packages
 * found in NetBeans System ClassLoader.
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @author Jiri.Skrivanek@sun.com
 */
public class BundleLookup {
    
    /** Descendant of NetBeans System ClassLoader which enables to obtain package names. */
    private static Utils.TestClassLoader testClassLoader;
    /** Signal to stop searching from user. */
    static boolean run;
    
    static class Regex extends Object {
        Pattern regex;
        public Regex(String s, boolean cs, boolean sub) {
            if (sub) {
                s=".*"+s+".*"; // NOI18N
            }
            if (cs) {
                regex = Pattern.compile(s);
            } else {
                regex = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
            }
        }
        public boolean equals(Object o) {
            return regex.matcher((CharSequence)o).matches();
        }
    }
    static class Substring extends Object {
        String s;
        public Substring(String s) {
            this.s=s;
        }
        public boolean equals(Object o) {
            return ((String)o).indexOf(s)>=0;
        }
    }
    static class IgnoreCaseSubstring extends Object {
        String s;
        public IgnoreCaseSubstring(String s) {
            this.s=s.toLowerCase();
        }
        public boolean equals(Object o) {
            return ((String)o).toLowerCase().indexOf(s)>=0;
        }
    }
    static class IgnoreCase extends Object {
        String s;
        public IgnoreCase(String s) {
            this.s=s;
        }
        public boolean equals(Object o) {
            return s.equalsIgnoreCase((String)o);
        }
    }
    
    
    static Object resolve(String s, boolean cs, boolean sub, boolean regex) {
        if (regex)
            return new Regex(s, cs, sub);
        if (cs) {
            if (sub)
                return new Substring(s);
            else
                return s;
        } else {
            if (sub) 
                return new IgnoreCaseSubstring(s);
            else
                return new IgnoreCase(s);
        }
    }
    
    private static boolean tryI18NSearch(DefaultTableModel table, String text) {
        if (!I18NSupport.i18nActive) return false;
        StringTokenizer st=new StringTokenizer(text, " ,:;()");
        if (st.hasMoreTokens()) try {
            int index=Integer.parseInt(st.nextToken());
            if (st.hasMoreTokens()) {
                int row=Integer.parseInt(st.nextToken());
                if (st.hasMoreTokens()) return false;
                I18NSupport sup=new I18NSupport();
                String bundle=sup.getBundle(index);
                if (bundle!=null) {
                    String line=sup.getLine(bundle, row);
                    int i;
                    if (line!=null && (i=line.indexOf('='))>0) {
                        if (bundle.endsWith(".properties")) bundle=bundle.substring(0, bundle.length()-11);
                        bundle=bundle.replace('/', '.');
                        table.addRow(new String[]{bundle, line.substring(0, i), line.substring(i+1)});
                        return true;
                    }
                }
            }
        } catch (NumberFormatException nfe) {}
        return false;
    }
    
    /** Method performing lookup through all Bundle.properties files in all packages
     * found in NetBeans System ClassLoader.
     * @param regexText boolean true if text is regular expression
     * @param regexBundle boolean true if bundle name is regular expression
     * @param table DefaultTableModel showing the results
     * @param text text to search for
     * @param caseSensitiveText case sensitive switch
     * @param substringText substring switch
     * @param bundle resource bundle filter
     * @param caseSensitiveBundle case sensitive filter switch
     * @param substringBundle substring filter switch */    
    public static void lookupText(DefaultTableModel table, String text, boolean caseSensitiveText, boolean substringText, boolean regexText, String bundle, boolean caseSensitiveBundle, boolean substringBundle, boolean regexBundle) {
        if (tryI18NSearch(table, text)) return;
        run=true;
        String name, key;
        Properties res;
        Enumeration keys;
        Object _text=resolve(text, caseSensitiveText, substringText, regexText);
        Object _bundle=resolve(bundle, caseSensitiveBundle, substringBundle, regexBundle);
        
        String[] packages = getPackages();
        int i=0;
        while (run && i<packages.length) {
            Thread.yield();
            try {
                if (_bundle.equals(packages[i])) {
                    res = new Properties();
                    // bundle fully qualified name (e.g. org.netbeans.core.Bundle)
                    name = packages[i]+".Bundle";
                    // get resource (e.g. org/netbeans/core/Bundle.properties)
                    InputStream is = getTestClassLoader().getResourceAsStream(name.replace('.', '/')+".properties");  // NOI18N
                    // find requested text within all properties
                    if(is != null) {
                        res.load(is);
                        keys = res.keys();
                        while(keys.hasMoreElements()) {
                            key = (String)keys.nextElement();
                            // if found add it to the result table
                            if (_text.equals(res.getProperty(key))) {
                                table.addRow(new String[]{name, key, res.getProperty(key).replace('\n','/')});
                            }
                        }
                    }
                }
            } catch (IOException ioe) {
                // ignore
            }
            i++;
        }
    }

    /** stops search (if running) */    
    public static void stop() {
        run=false;
    }

    /** Gets all package names in NetBeans System ClassLoader.
     * @return array of packages in dot notation (e.g. [org.netbeans.core, ...])
     */
    private static String[] getPackages() {
        Package[] ps = getTestClassLoader().getPackages();
        String[] packages = new String[ps.length];
        for(int i=0;i<ps.length;i++) {
            packages[i] = ps[i].getName();
        }
        return packages;
    }
    
    /** Gets descendant of NetBeans System ClassLoader which enables to obtain
     * package names currently loaded.
     * @return instace of TestClassLoader
     */
    private static Utils.TestClassLoader getTestClassLoader() {
        if(testClassLoader == null) {
            testClassLoader = new Utils.TestClassLoader(new URL[0], Utils.getSystemClassLoader());
        }
        return testClassLoader;
    }
}
