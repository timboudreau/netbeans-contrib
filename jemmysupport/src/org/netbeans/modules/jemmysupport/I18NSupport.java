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

package org.netbeans.modules.jemmysupport;

/*
 * I18NSupport.java
 *
 * Created on February 11, 2003, 1:42 PM
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.*;
import org.netbeans.jemmy.JemmyException;
import org.openide.execution.NbClassLoader;
import org.openide.filesystems.Repository;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 */
public class I18NSupport {
    
    static boolean i18nActive=Boolean.getBoolean ("org.openide.util.NbBundle.DEBUG"); // NOI18N
    
    String bundles[]=new String[0];
    ClassLoader loader;
    
    /** Creates a new instance of I18NSupport */
    public I18NSupport() {
        if (i18nActive) getBundles();
    }

    public String translatePath(String text) {
        if (text.length()<6) return hardcoded(text);
        StringTokenizer st=new StringTokenizer(text, "|");
        StringBuffer sb=new StringBuffer(translate(st.nextToken()));
        while (st.hasMoreTokens()) {
            sb.append("+\"|\"+").append(translate(st.nextToken()));
        }
        return sb.toString();
    }
    
    public String translate(String text) {
        int i=text.lastIndexOf('(');
        int j=text.lastIndexOf(':');
        int k=text.lastIndexOf(')');
        LineNumberReader prop=null;
        if (i>=0 && j>i+1 && k>j+1) try {
            int bundle=Integer.parseInt(text.substring(i+1, j));
            int row=Integer.parseInt(text.substring(j+1, k));
            if (--bundle<bundles.length) {
                prop=new LineNumberReader(new InputStreamReader(loader.getResourceAsStream(bundles[bundle])));
                String key;
                while (--row>0) prop.readLine();
                key=prop.readLine();
                i=key.indexOf('=');
                if (i>0) {
                    String res=translate(text, bundles[bundle].endsWith(".properties")?bundles[bundle].substring(0, bundles[bundle].length()-11):bundles[bundle], key.substring(0, i));
                    if (res!=null) return res;
                }
            }
        } catch (NumberFormatException nfe) { 
        } catch (IOException ioe) {
        } catch (NullPointerException npe) {
        } finally {
            try {
                if (prop!=null) prop.close();
            } catch (IOException ioe2) {}
        }
        return hardcoded(text);
    }
        
    public static String escape(String text) {
        StringBuffer sb = new StringBuffer("\"");
        char c;
        for (int i=0;i<text.length();i++) {
            switch (c=text.charAt(i)) {
                case ('\b'):sb.append("\\b");break;
                case ('\t'):sb.append("\\t");break;
                case ('\n'):sb.append("\\n");break;
                case ('\f'):sb.append("\\f");break;
                case ('\r'):sb.append("\\r");break;
                case ('\"'):sb.append("\\\"");break;
                case ('\''):sb.append("\\'");break;
                case ('\\'):sb.append("\\\\");break;
                default:sb.append(c);
            }
        }
        sb.append('\"');
        return sb.toString();
    }

    public static String filterI18N(String text) {
        if (text==null || !i18nActive) return text;
        StringBuffer sb=new StringBuffer();
        boolean wasspace=false;
        for (int i=0; i<text.length(); i++) {
            char c=text.charAt(i);
            if (wasspace && c=='(') {
                int j=text.indexOf(':', i);
                int k=text.indexOf(')', i);
                if (j>i+1 && k>j+1) try {
                    Integer.parseInt(text.substring(i+1, j));
                    Integer.parseInt(text.substring(j+1, k));
                    i=k;
                    sb.deleteCharAt(sb.length()-1);
                } catch (NumberFormatException nfe) {
                     sb.append(c);
                } else sb.append(c);
            } else sb.append(c);
            wasspace=(c==' ');
        }
        return sb.toString();
    }
    
    private void getBundles() {
        try {
            loader=new NbClassLoader(Repository.getDefault().toArray());
            Field f=Class.forName("org.openide.util.NbBundle$DebugLoader").getDeclaredField("knownIDs");
            f.setAccessible(true);
            HashMap map=(HashMap)f.get(null);
            synchronized (map) {
                bundles=new String[map.size()];
                Iterator it=map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry=(Map.Entry)it.next();
                    bundles[((Integer)entry.getValue()).intValue()-1]=(String)entry.getKey();
                }
            }
        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchFieldException nsfe) {
        } catch (IllegalAccessException iae) {} 
    }

    private static String hardcoded(String text) {
        return filterI18N(escape(text));
    }

    private String translate(String text, String bundle, String key) {
        try {
            String value=NbBundle.getBundle(bundle, Locale.getDefault(), loader).getString(key);
            boolean amp=false;
            ArrayList parts=new ArrayList();
            StringBuffer part=new StringBuffer();
            char c,n;
            for (int i=0; i<value.length(); i++) {
                c=value.charAt(i);
                if (c=='&') amp=true;
                else if (c=='{' && value.length()>i+2 && value.charAt(i+2)=='}' && (n=value.charAt(i+1))>='0' && n<='9') {
                    parts.add(part.toString());
                    part=new StringBuffer();
                    parts.add(new Integer(n-'0'));
                    i+=2;
                } else part.append(c);
            }
            parts.add(part.toString());
            String s;
            if (text.startsWith(s=(String)parts.remove(0))) {
                StringBuffer res=new StringBuffer(amp?"Bundle.getStringTrimmed(\"":"Bundle.getString(\"");
                res.append(bundle).append("\", \"").append(key).append('\"');
                int i=s.length();
                if (parts.size()>0) {
                    res.append(", new Object[]{");
                    String args[]=new String[10];
                    int maxarg=-1;
                    while (parts.size()>0) {
                        int j=((Integer)parts.remove(0)).intValue();
                        int k=text.indexOf(s=(String)parts.remove(0), i);
                        if (k<0) return null;
                        args[j]=translate(text.substring(i, k));
                        if (j>maxarg) maxarg=j;
                        i=k+s.length();
                    }
                    for (int j=0; j<maxarg; j++) {
                        res.append(args[j]==null?"":args[j]).append(", ");
                    }
                    res.append(args[maxarg]==null?"":args[maxarg]).append('}');
                }
                res.append(')');
                if (i<text.length()) {
                    res.append('+').append(hardcoded(text.substring(i)));
                }
                return res.toString();
            }
        } catch (JemmyException je) {
        } catch (MissingResourceException mre) {} 
        return null;
    }
}
