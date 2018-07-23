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

package util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Filter {

    ArrayList filterAfter;
    ArrayList filterFrom;
    ArrayList replaceFrom;
    ArrayList replaceTo;
    ArrayList betweenBefore;
    ArrayList betweenAfter;

    public Filter () {
        filterAfter = new ArrayList ();
        filterFrom = new ArrayList ();
        replaceFrom = new ArrayList ();
        replaceTo = new ArrayList ();
        betweenBefore = new ArrayList ();
        betweenAfter = new ArrayList ();
    }
    
    public void addFilterAfter (String str) {
        filterAfter.add (str);
    }
    
    public void addFilterFrom (String str) {
        filterFrom.add (str);
    }
    
    public void addReplace (String from, String to) {
        replaceFrom.add (from);
        replaceTo.add (to);
    }
    
    public void addFilterBetween (String before, String after) {
        betweenBefore.add (before);
        betweenAfter.add (after);
    }
    
    public String filter (String str) {
        for (int a = 0; a < filterAfter.size (); a ++) {
            String f = (String) filterAfter.get (a);
            int i = str.indexOf(f);
            if (i >= 0)
                str = str.substring(0, i + f.length());
        }
        for (int a = 0; a < filterFrom.size (); a ++) {
            String f = (String) filterFrom.get (a);
            int i = str.indexOf(f);
            if (i >= 0)
                str = str.substring(0, i);
        }
        for (int a = 0; a < replaceFrom.size (); a ++)
            str = replaceAll(str, (String) replaceFrom.get (a), (String) replaceTo.get (a));
        for (int a = 0; a < betweenBefore.size (); a ++) {
            String bef = (String) betweenBefore.get (a);
            String aft = (String) betweenAfter.get (a);
            int i1 = str.indexOf (bef);
            if (i1 < 0)
                continue;
            i1 += bef.length();
            int i2 = str.indexOf (aft, i1);
            if (i2 < 0)
                continue;
            str = str.substring (0, i1) + str.substring (i2);
        }
        return str;
    }
    
    public static String replaceAll(String str, String from, String to) {
        if ("".equals (from)  ||  to.startsWith(from))
            return str;
        for (;;) {
            int index = str.indexOf(from);
            if (index < 0)
                break;
            str = str.substring(0, index) + to + str.substring(index + from.length());
        }
        return str;
    }
    
    public void filterStringLinesToStream (PrintStream out, String lines) {
        StringTokenizer st = new StringTokenizer (lines, "\n");
        while (st.hasMoreTokens())
            out.println (filter (st.nextToken()));
    }
    
}
