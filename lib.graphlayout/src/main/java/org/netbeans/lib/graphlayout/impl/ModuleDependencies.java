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

package org.netbeans.lib.graphlayout.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import org.netbeans.lib.graphlayout.*;

/** Displayes dependencies between NetBeans modules. Reads them from
 * web access to CVS.
 *
 * @author Jaroslav Tulach
 */
public final class ModuleDependencies extends JApplet implements ActionListener, Runnable {
    private static Map/*<String,byte[]>*/ cache = new HashMap ();
    private JButton load;
    
    public void start() {
        load = new JButton (getButtonMessge());
        load.addActionListener (this);
        getContentPane ().add (BorderLayout.CENTER, load);
    }
    
    private String getButtonMessge() {
        String b = getParameter("button.message"); // NOI18N
        if (b == null) {
            b = "Show Dependencies"; 
        }
        return b;
    }
    
    public void actionPerformed (ActionEvent ev) {
        load.setEnabled (false);
        load.setText ("Loading data...");
        load.setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        new Thread (this, "Loading data").start ();
    }
    
    public void run () {
        
        
        
        try {
            main (new String[] {
                getParameter("url"),
                getParameter("suffix"),
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog (this, ex);
        }
        load.setCursor (null);
        load.setEnabled (true);
        load.setText (getButtonMessge());
    }
    
    private static String baseURL = "";
    private static String suffix = "";
    
    private static InputStream read (String what) throws Exception {
        byte[] ret = (byte[])cache.get (what);
        if (ret != null) {
            return new ByteArrayInputStream (ret);
        }
        
        String url = baseURL + what + ".txt" + suffix;
        URL u = new URL (url);
        InputStream is = u.openStream ();
        ByteArrayOutputStream out;
        try {
            out = new ByteArrayOutputStream ();
            byte[] arr = new byte[4096];
            for (;;) {
                int len = is.read (arr);
                if (len == -1) break;
                out.write (arr, 0, len);
            }
        } finally {
            is.close();
        }
        ret = out.toByteArray();
        cache.put (what, ret);
        return new ByteArrayInputStream (ret);
    }

    public static void main (String[] args) throws Exception {
        if (args.length == 1) {
            baseURL = args[0];
            suffix = "";
        }
        if (args.length == 2) {
            baseURL = args[0];
            suffix = args[1];
        }
        
        Graph g = Graph.create ();
        Graph impl = Graph.create ();

        Pattern modules = Pattern.compile("MODULE ([^ /]+)(?:/[0-9]+)? \\(([a-zA-Z0-9]+)\\)");
        Pattern requires = Pattern.compile("  REQUIRES ([^ /]+).*");
        readVertexes (g, modules, null, read ("modules"), -1);
        readVertexes (impl, modules, null, read ("modules"), -1);
        readVertexes (g, modules, requires, read ("deps"), 1);
        readVertexes (g, modules, 
            Pattern.compile("  FRIEND ([^ /]+).*"), 
            Pattern.compile("  (PACKAGE|EXTERNAL).*"), 
            read ("friend-packages"), 
            10,
            true
        );
        readVertexes (g, modules, requires, read ("impl-deps"), 50);
        readVertexes (impl, modules, requires, read ("impl-deps"), 1);
        
        JFrame f = new JFrame ("NetBeans Modules Dependencies");
        JTabbedPane pane = new JTabbedPane ();
        JComponent gRend = g.createRenderer();
        pane.add ("All Matrix", g.createMatrix());
        pane.add ("All Deps", gRend);
        JComponent implRend = impl.createRenderer();
        pane.add ("Impl Matrix", impl.createMatrix());
        pane.add ("Impl Deps", implRend);
        f.getContentPane ().add (BorderLayout.CENTER, pane);
        try {
            f.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        } catch (SecurityException ex) {
            // ignore it if executed from applet
        }
        f.pack ();
        
        Dimension dim = (Dimension)f.getSize ().clone ();
        dim.width |= 2;
        dim.height |= 2;
        gRend.setPreferredSize(dim);
        f.setVisible(true);
    }
    
    private static void readVertexes (Graph g, Pattern nodeAndGroup, Pattern dep, InputStream is, int strength) throws IOException {
        readVertexes(g, nodeAndGroup, dep, null, is, strength, false);
    }
    private static void readVertexes (Graph g, Pattern nodeAndGroup, Pattern dep, Pattern ignore, InputStream is, int strength, boolean revert) throws IOException {
        BufferedReader r = new BufferedReader (new InputStreamReader (is));
        Vertex previousVertex = null;
        String previousVertexName = null;
        for (;;) {
            String l = r.readLine ();
            if (l == null) break;
            
            Matcher m = nodeAndGroup.matcher (l);
            Matcher dm = dep != null ? dep.matcher (l) : null;
            
            boolean matcherMatchers = m.matches ();
            boolean depMatchers = dm != null && dm.matches ();
            if (!matcherMatchers && !depMatchers) {
                if (ignore == null || !ignore.matcher(l).matches()) {
                    throw new IOException ("No match found: " + l);
                }
                continue;
            }
            
            if (matcherMatchers) {
                int cnt = m.groupCount ();
                if (cnt != 2) {
                    throw new IOException ("Wrong groups: " + l + " was: "+ cnt);
                }

                String name = m.group (1);
                String group = m.group (2);

                previousVertex = g.createVertex (name, group);
                previousVertexName = name;
            } else {
//                assert depMatchers : "Dependency match";
//                assert strength > 0 : "Strenght must be > 0 : " + strength;
                
                int cnt = dm.groupCount ();
                if (cnt != 1) {
                    throw new IOException ("Wrong groups: " + l + " was: "+ cnt);
                }

                String name = dm.group (1);

                if (name.startsWith ("org.openide.modules.os")) {
                    continue;
                }
                if (name.indexOf("ModuleFormat") >= 0) {
                    continue;
                }
                
                String v1 = previousVertexName;
                String v2 = name;
                if (revert) {
                    String x = v1;
                    v1 = v2;
                    v2 = x;
                }

                g.createEdge(v1, v2, strength, true);
            }
        }
    }
    
}
