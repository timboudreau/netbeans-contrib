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

package test.irnodes;

import util.Helper;
import util.JHelper;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.modules.corba.dialogs.AddInterfaceRepositoryDialog;
import org.netbeans.jellytools.modules.corba.nodes.CORBAInterfaceRepositoryNode;
import org.netbeans.jellytools.modules.corba.nodes.InterfaceRepositoryNode;
/*import org.netbeans.jellytools.properties.Property;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;*/
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.modules.corba.browser.ir.IRRootNode;
import org.openide.nodes.Node;
import org.openide.actions.PropertiesAction;
import org.openide.util.actions.SystemAction;

public class Main extends JellyTestCase {
    
    public Main (String name) {
        super (name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testIR_Nodes"));
        return test;
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    ExplorerOperator exp = null;
    EventTool ev = null;
    PrintStream out = null;
    Timeouts time;
    
    public void setUp () {
        time = JemmyProperties.getCurrentTimeouts ();
        Timeouts t = new Timeouts ();
        try { t.loadDebugTimeouts (); } catch (IOException e) {}
        JemmyProperties.setCurrentTimeouts (t);
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = true;
//        dumpScreen = true;
//        css = (CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true);
//        assertNotNull ("Cannot find CORBASupportSettings class", css);
    }
    
    public void tearDown () {
        JemmyProperties.setCurrentTimeouts (time);
    }
	
    public static String printProperties (Node node) {
        String out = "";
        Node.PropertySet[] ps = node.getPropertySets ();
        for (int a = 0; a < ps.length; a ++) {
            out += " " + ps[a].getName () + " [";
            Node.Property[] p = ps[a].getProperties ();
            for (int b = 0; b < p.length; b ++) {
                try {
                out += " <" + p[b].getName () + " = " + p[b].getValue () + "> ";
                } catch (Exception e) {
                    out += " <" + p[b].getName () + " = Exception: " + e.getClass() + "> ";
                }
            }
            out += "] ";
        }
        return out;
    }

    public void printTree (Node _node, String prefix) {
        Node[] nodes = Helper.filterNodes (Helper.waitSubNodes(_node));
        for (int a = 0; a < nodes.length; a ++) {
            Node node = nodes[a];
            if (a + 1 < nodes.length) {
                out.println (prefix + "|-- " + node.getName () + " [" + printProperties (node) + "]");
                printTree (node, prefix + "|   ");
            } else {
                out.println (prefix + "`-- " + node.getName () + " [" + printProperties (node) + "]");
                printTree (node, prefix + "    ");
            }
        }
    }
    
    Node waitNodePath (String path) {
        getLog ().println ("Getting: " + path);
        Node n = IRRootNode.getDefault();
        StringTokenizer s = new StringTokenizer (path, "|");
        while (s.hasMoreTokens()) {
            String st = s.nextToken();
            getLog ().println ("Trying: " + st);
            n = Helper.waitSubNode(n, st);
            if (n == null) {
                getLog ().println ("ERROR");
                break;
            }
        }
        getLog ().println ("Return");
        return n;
    }
    
    String irname;
/*    
    void printProperties (String name) {
        int a;
        JHelper.closeAllProperties ();
        out.println ("-----------------------------");
        out.println ("Properties: Node: " + name);
/ *        a = 60;
        for (; a > 0; a --) {
            Node n1 = new InterfaceRepositoryNode (exp.runtimeTab ().tree (), irname + name);
            Helper.sleep (1000);
            Node n2 = new InterfaceRepositoryNode (exp.runtimeTab ().tree (), irname + name);
            String l1 = n1.getTreePath ().getLastPathComponent().toString ();
            String l2 = n2.getTreePath ().getLastPathComponent().toString ();
            getLog ().println ("COMPARE:");
            getLog ().println (l1);
            getLog ().println (l2);
            if (n1 == n2  &&  l1.equals (l2))
                break;
        }* /
        
        // !!! not working: new InterfaceRepositoryNode (exp.runtimeTab ().tree (), irname + name).properties ();
        Node n = waitNodePath (CORBAInterfaceRepositoryNode.NAME + irname + name);
        new InterfaceRepositoryNode (exp.runtimeTab ().tree (), irname + name);
        
        TopManager.getDefault ().getNodeOperation ().showProperties (n);
/ *        SystemAction[] sa = n.getActions ();
        SystemAction ac = null;
        if (sa != null) {
            for (int a = 0; a < sa.length; a ++) {
                if (sa[a] instanceof PropertiesAction) {
                    ac = sa[a];
                    break;
                }
            }
        }
        if (ac == null) {
            out.println ("Cannot PropertiesAction");
            return;
        }
        TopManager.getDefault ().getActionManager ().invokeAction (ac, new ActionEvent (node, ActionEvent.ACTION_PERFORMED, ""));* /
        
        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, name.substring (name.lastIndexOf('|') + 1));
        PropertySheetTabOperator psto = pso.getPropertySheetTabOperator("Properties");
        a = 60;
        for (; a > 0; a --) {
            if (psto.getCount () >= 2)
                break;
            Helper.sleep (1000);
        }
        if (a <= 0)
            out.println ("ERROR: Timeout expired. Properties are not fully loaded");
        Property prop = new Property (psto, "Id");
        for (int b = 0; b < psto.getCount (); b ++) {
            StringProperty str = new StringProperty (psto, new Property (psto, b).getName ());
            out.println (str.getName () + " : " + str.getStringValue());
        }
        pso.close ();
    }
*/    
    public void perform (String ior) {
        assertTrue ("Invalid IOR: " + ior, ior != null  &&  ior.startsWith ("IOR:"));
        
        irname = "IRNodesTest_" + new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));
        out.println ("Adding IR");
        new CORBAInterfaceRepositoryNode (exp.runtimeTab().tree ()).addInterfaceRepository();
        AddInterfaceRepositoryDialog addir = new AddInterfaceRepositoryDialog ();
        addir.setName (irname);
        addir.loadIOR(ior);
        addir.oK ();
        new InterfaceRepositoryNode (exp.runtimeTab().tree (), "|" + irname);
        out.println ("Done");
        
        printTree (waitNodePath(irname), "");
/*
        irname = "|" + irname;
        printProperties ("|A");
        printProperties ("|A|opA");
        printProperties ("|B");
        printProperties ("|B|opB");
        printProperties ("|D");
        printProperties ("|D|opD");
        printProperties ("|E");
        printProperties ("|E|aa");
        printProperties ("|E|bo");
        printProperties ("|E|init");
        printProperties ("|E|init2");
        printProperties ("|Exc");
        printProperties ("|Exc|message");
        printProperties ("|M");
        printProperties ("|M|B");
        printProperties ("|M|A");
        printProperties ("|M|A|op");
        printProperties ("|S");
        printProperties ("|S|S2");
        printProperties ("|S|S2|bb");
        printProperties ("|S|S2|cc");
        printProperties ("|S|LL");
        printProperties ("|S|sss");
        printProperties ("|U");
        printProperties ("|U|L1");
        printProperties ("|U|S1");
        printProperties ("|U|B1");
        printProperties ("|C");
        printProperties ("|CR");
        printProperties ("|VT");
        printProperties ("|VT|PA");
        printProperties ("|VT2");
        printProperties ("|VT2|PL");
        printProperties ("|VT2|ax");
        printProperties ("|VT3");
        printProperties ("|VT4");
        printProperties ("|VT4|ax");
        printProperties ("|VT5");
        printProperties ("|VT6");
        printProperties ("|STR");
        printProperties ("|CH");
        printProperties ("|LO");
        printProperties ("|FL");
        printProperties ("|VT410");
        printProperties ("|SEQSEQ");
        out.println ("---- END ----");
*/
        out.println ("Removing IR");
        new InterfaceRepositoryNode (exp.runtimeTab().tree (), "|" + irname).removeInterfaceRepository ();
        out.println ("Done");
    }

    public void testIR_Nodes () {
        perform (System.getProperty ("IR_IOR"));
        compareReferenceFiles();
    }
}
