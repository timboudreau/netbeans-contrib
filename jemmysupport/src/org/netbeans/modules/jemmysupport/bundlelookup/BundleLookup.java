/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * BundleLookup.java
 *
 * Created on January 11, 2002, 1:09 PM
 */

package org.netbeans.modules.jemmysupport.bundlelookup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.MissingResourceException;
import javax.swing.table.DefaultTableModel;
import org.openide.filesystems.Repository;
import org.openide.filesystems.*;

/** Class performing lookup through all .properties files in all mounted filesystems
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.2
 */
public class BundleLookup {
    
    static Method compile;
    static Method matcher;
    static Method matches;
    static final Integer CASE_SENSITIVE=new Integer(0);
    static Integer CASE_INSENSITIVE;
    static {
        try {
            Class pattern=Class.forName("java.util.regex.Pattern");
            compile=pattern.getDeclaredMethod("compile", new Class[]{String.class,Integer.TYPE});
            matcher=pattern.getDeclaredMethod("matcher", new Class[]{Class.forName("java.lang.CharSequence")});
            CASE_INSENSITIVE=(Integer)pattern.getDeclaredField("CASE_INSENSITIVE").get(null);
            matches=Class.forName("java.util.regex.Matcher").getDeclaredMethod("matches",null);
        } catch (Exception e) {}
    }
    
    static class Regex extends Object {
        Object regex;
        public Regex(String s, boolean cs, boolean sub) {
            try {
                if (sub)
                    s=".*"+s+".*";
                if (cs)
                    regex=compile.invoke(null, new Object[]{s, CASE_SENSITIVE});
                else
                    regex=compile.invoke(null, new Object[]{s, CASE_INSENSITIVE});
            } catch (Exception e) {}
        }
        public boolean equals(Object o) {
            try {
                Object m=matcher.invoke(regex, new Object[]{o});
                return ((Boolean)matches.invoke(m, null)).booleanValue();
            } catch (Exception e) {}
            return false;
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

    /** Method performing lookup through all .properties files in all mounted filesystems
     * @param table DefaultTableModel showing the results
     * @param text text to search for
     * @param caseSensitiveText case sensitive switch
     * @param substringText substring switch
     * @param bundle resource bundle filter
     * @param caseSensitiveBundle case sensitive filter switch
     * @param substringBundle substring filter switch
     */    
    public static void lookupText(DefaultTableModel table, String text, boolean caseSensitiveText, boolean substringText, boolean regexText, String bundle, boolean caseSensitiveBundle, boolean substringBundle, boolean regexBundle) {
        Enumeration filesystems=Repository.getDefault().getFileSystems();
        FileObject fo;
        ArrayList queue=new ArrayList();
        String name, key;
        Properties res;
        Enumeration keys;
        FileObject ch[];
        Object _text=resolve(text, caseSensitiveText, substringText, regexText);
        Object _bundle=resolve(bundle, caseSensitiveBundle, substringBundle, regexBundle);
        
        
        while (filesystems.hasMoreElements()) {
            fo=((FileSystem)filesystems.nextElement()).getRoot();
            if (fo.isValid()) {
                queue.add(fo);
            }
        }
        while (!queue.isEmpty()) {
            Thread.yield();
            fo=(FileObject)queue.remove(0);
            if ("properties".equalsIgnoreCase(fo.getExt())) {
                name=fo.getPackageName('.');
                try {
                    if (_bundle.equals(name)) {
                        res = new Properties();
                        res.load(fo.getInputStream());
                        keys = res.keys();
                        while (keys.hasMoreElements()) {
                            try {
                                key = (String)keys.nextElement();
                                if (_text.equals(res.getProperty(key))) {
                                    table.addRow(new String[]{name, key, res.getProperty(key).replace('\n','/')});
                                }
                            } catch (ClassCastException cce) {}
                        }
                    }
                } catch (FileNotFoundException fnfe) {
                } catch (IOException ioe) {}
            }
            ch=fo.getChildren();
            for (int i=0;i<ch.length;i++) {
                queue.add(ch[i]);
            }
        }
    }

}
