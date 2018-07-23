/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.jemmysupport;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import org.netbeans.jemmy.JemmyException;
import org.openide.util.NbBundle;

/**
 * Utilize bundle support implemented in IDE. When IDE is started with options
 * -nosplash -J-Dorg.openide.util.NbBundle.DEBUG=true all string from bundles
 * are signed with bundle number and line number in the bundle. If you type
 * those numbers (e.g. 13:34) in the search text field of the resource bundle 
 * lookup panel then you get immediatelly precise result.
 *
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @author Jiri.Skrivanek@sun.com
 */
public class I18NSupport {
    
    public static boolean i18nActive=Boolean.getBoolean ("org.openide.util.NbBundle.DEBUG"); // NOI18N
    
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
    
    public String getBundle(int index) {
        if (index>0 && index<=bundles.length) return bundles[index-1];
        return null;
    }
    
    public String getLine(String bundle, int row) {
        if (bundle==null) return null;
        LineNumberReader prop=null;
        try {
            prop=new LineNumberReader(new InputStreamReader(loader.getResourceAsStream(bundle)));
            String key;
            while (--row>0) prop.readLine();
            return  prop.readLine();
        } catch (IOException ioe) {
        } finally {
            if (prop!=null) try {
                prop.close();
            } catch (IOException ioe2) {}
        }
        return null;
    }        
    
    public String translate(String text) {
        int i=text.lastIndexOf('(');
        int j=text.lastIndexOf(':');
        int k=text.lastIndexOf(')');
        LineNumberReader prop=null;
        if (i>=0 && j>i+1 && k>j+1) try {
            int index=Integer.parseInt(text.substring(i+1, j));
            int row=Integer.parseInt(text.substring(j+1, k));
            String bundle=getBundle(index);
            if (bundle!=null) {
                String key=getLine(bundle, row);
                if (key!=null && (i=key.indexOf('='))>0) {
                    String res=translate(text, bundle.endsWith(".properties")?bundle.substring(0, bundle.length()-11):bundle, key.substring(0, i));
                    if (res!=null) return res;
                }
            }
        } catch (NumberFormatException nfe) { 
        } catch (NullPointerException npe) {}
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
            loader= Utils.getSystemClassLoader();
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
