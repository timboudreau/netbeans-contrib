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

package test.overall;

import java.io.File;
import org.openide.TopManager;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ns.ContextNode;
import org.netbeans.modules.corba.browser.ir.IRRootNode;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.browser.ns.CopyServerCode;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.PrintStream;
import org.netbeans.modules.corba.wizard.nodes.IdlFileNode;
import org.netbeans.modules.corba.wizard.nodes.MutableChildren;
import util.NameService;

public class Main extends org.netbeans.junit.NbTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testOverall"));
        return test;
    }
    
    PrintStream out, info;
    
    public void testOverall() {
        out = getRef ();
        info = getLog ();

        try {
            
            NameService ns = null;
            
            try {
                
        /* Test CORBA Support Settings */
                SystemOptionTest sot = new SystemOptionTest();
                sot.setIO(out);
                sot.testSystemOption("org.netbeans.modules.corba.settings.CORBASupportSettings");
                out.println();
                
        /* Get CORBA Naming Service and CORBA Interface Repository node */
                Node n, rn = TopManager.getDefault().getPlaces().nodes().environment();
                n = Helper.waitSubNode(rn, "CORBA Naming Service");
                assertNotNull ("CORBA Naming Service node does not exists!", n);
                //ContextNode nsNode = (ContextNode) Helper.getOriginalNode (n);
                ContextNode nsNode = ContextNode.getDefault();
                n = Helper.waitSubNode(rn, "CORBA Interface Repository");
                assertNotNull ("CORBA Interface Repository node does not exists!", n);
                //IRRootNode irNode = (IRRootNode) Helper.getOriginalNode (n);
                IRRootNode irNode = IRRootNode.getDefault();
                
        /* Print actions and properties of CORBA Naming Service and CORBA InterfaceRepository node */
                Helper.printActions(nsNode, out);
                printPropertiesWOIOR(nsNode);
                Helper.printActions(irNode, out);
                printPropertiesWOIOR(irNode);
                
        /* Start NS */
                ns = new NameService(info);
                String ior = ns.start(1065);
                assertNotNull ("Cannot start Naming Service!", ior);
                
        /* Bind new context and print it's node */
                ContextNode context;
                try {
                    nsNode.bind_new_context("NSName", "NSKind", "", ior);
                    context = (ContextNode) Helper.waitSubNode(nsNode, "NSName", 1000);
                    assertNotNull ("Cannot bind new context!", context);
                } catch (Exception e) {
                    info.println ("Exception while binding new context:");
                    e.printStackTrace(info);
                    throw new Exception ("Cannot bind new context!");
                }
                Helper.printActions(context, out);
                printPropertiesWOIOR(context);
                Helper.sleep(3000);
                
        /* Create new sub-context and print it's node */
                ContextNode subContext;
                try {
                    context.create_new_context("NSSubName", "NSSubKind");
                    subContext = (ContextNode) Helper.waitSubNode(context, "NSSubName", 1000);
                    if (subContext == null) {
                        ns.stop();
                        ns = null;
                        throw new Exception ("Cannot create new sub context!");
                    }
                } catch (Exception e) {
                    info.println ("Exception while creating new sub context:");
                    e.printStackTrace(info);
                    throw new Exception ("Cannot create new sub context!");
                }
                Helper.printActions(subContext, out);
                printPropertiesWOIOR(subContext);
                Helper.sleep(3000);
                
        /* Print Server binding code */
                Helper.performAction(subContext, CopyServerCode.class);
                Helper.sleep(3000);
                out.println("Server code on node: NSName(NSKind)/NSSubName(NSSubKind)");
                try {
                    Helper.printClipboardAsString(out);
                } catch (UnsupportedFlavorException e) {
                    info.println ("Error while getting text from clipboard:");
                    e.printStackTrace(info);
                    out.println("Clipboard error!");
                }
                Helper.sleep(3000);
                
        /* Unbind NS */
                context.unbind();
                Helper.sleep(3000);
                
        /* Stop NS */
                ns.stop();
                ns = null;
                
        /* Create wizard's idl file node */
                IdlFileNode rootNode = new IdlFileNode();
                Helper.printActions(rootNode, out);
                Helper.printProperties(rootNode, out);
                
        /* Create some subnodes as IDL */
                Node moduleNode = makeNodeByKey(rootNode, new NamedKey(NamedKey.MODULE, "TestModule"));
                makeNodeByKey(moduleNode, new ConstKey(NamedKey.CONSTANT, "TestConst", "unsigned long", "12"));
                makeNodeByKey(moduleNode, new AliasKey(NamedKey.ALIAS, "TestAlias", "String", "12,10"));
                Node structNode = makeNodeByKey(moduleNode, new NamedKey(NamedKey.STRUCT, "TestStruct"));
                makeNodeByKey(structNode, new AliasKey(NamedKey.STRUCT_MBR, "TestStructEntry", "octet", ""));
                
                Node exceptionNode = makeNodeByKey(moduleNode, new NamedKey(NamedKey.EXCEPTION, "TestException"));
                makeNodeByKey(exceptionNode, new AliasKey(NamedKey.STRUCT_MBR, "TestExceptionEntry", "boolean", ""));
                Node unionNode = makeNodeByKey(moduleNode, new AliasKey(NamedKey.UNION, "TestUnion", "long", null));
                makeNodeByKey(unionNode, new UnionMemberKey(NamedKey.UNION_MBR, "TestUnionEntry", "short", "", "1"));
                makeNodeByKey(moduleNode, new EnumKey(NamedKey.ENUM, "TestEnum", "B0"));
                
                Node interfaceNode = makeNodeByKey(moduleNode, new InterfaceKey(NamedKey.INTERFACE, "TestInterface", ""));
                makeNodeByKey(interfaceNode, new OperationKey(NamedKey.OPERATION, "TestOperation", "void", "in char c", "", "", true));
                makeNodeByKey(interfaceNode, new AttributeKey(NamedKey.ATTRIBUTE, "TestAttribute", "any", true));
                
        /* Print generated code */
                out.println("Testing Code Generation:");
                out.println(rootNode.generateSelf(1));
                
            } finally {
                if (ns != null) { ns.stop(); ns = null; }
            }
            
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable t) {
            info.println ("Throwable was catched while running test:");
            t.printStackTrace(info);
        }
        compareReferenceFiles();
    }
    
    public void printPropertiesWOIOR (Node node) {
        out.println ("Properties of node: " + node.getName ());
        Node.PropertySet[] nps = node.getPropertySets ();
        for (int a = 0; a < nps.length; a ++) {
            if (nps [a] == null)
                continue;
            out.println (" PropertySet: " + nps [a].getName ());
            Node.Property[] np = nps [a].getProperties ();
            for (int b = 0; b < np.length; b ++) {
                String s = np [b].getName () + ": ";
                try {
                    if (np [b].getValue () != null  &&  ! "IOR: ".equals (s))
                        s += np [b].getValue ().toString ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                out.println (s);
            }
        }
        out.println ();
    }

    public Node makeNodeByKey (Node node, NamedKey key) {
        ((MutableChildren) node.getChildren()).addKey (key);
        Node subNode = Helper.waitSubNode (node, key.getName ());
        Helper.printActions (subNode, out);
        Helper.printProperties (subNode, out);
        return subNode;
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
}
