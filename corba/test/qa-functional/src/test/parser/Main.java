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

package test.parser;

import java.io.PrintStream;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import util.Environment;
import util.Filter;

public class Main extends NbTestCase {
    
    public Main(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testParser"));
        return test;
    }

    CORBASupportSettings css = null;
    Filter filter = null;
    
    PrintStream out;
    PrintStream info;
    
    public void testParser() {
        out = getRef ();
        info = getLog ();
        css = (CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true);
        assertNotNull ("Cannot find CORBASupportSettings", css);
        filter = new Filter ();
        filter.addFilterAfter ("compilation: ");
        runTestCore("data/genimpl/");
        compareReferenceFiles ();
    }
    
    public void runTestCore(String _package) {
        FileObject fo = Environment.findFileObject(_package);
        assertNotNull("Cannot find " + _package + " folder in repository!", fo);
        findIDL(fo, _package + " : ");
    }
    
    public void findIDL(FileObject fo, String path) {
        fo.refresh();
        FileObject[] afo = fo.getChildren();
        for (int b = 0; b < afo.length - 1; b ++) {
            boolean changed = false;
            for (int a = 0; a < afo.length - 1; a ++) {
                if (afo[a].getName().compareTo(afo[a+1].getName()) > 0) {
                    FileObject chfo = afo[a];
                    afo[a] = afo[a+1];
                    afo[a+1] = chfo;
                    changed = true;
                }
            }
            if (!changed)
                break;
        }
        for (int a = 0; a < afo.length; a ++) {
            if (afo [a] == null)
                continue;
            if (afo [a].isFolder()) {
                findIDL(afo [a], path + "/" + afo [a].getName());
            } else if (afo [a].hasExt("idl")) {
                IDLDataObject idl = null;
                try {
                    idl = (IDLDataObject) DataObject.find(afo [a]);
                    testIDL(fo, idl, path + "/" + afo [a].getName());
                } catch (DataObjectNotFoundException e) {
                    info.println ("Data object not found: File name: " + path + " - " + afo[a].getName());
                    e.printStackTrace (info);
                }
            }
        }
    }
    
    public void testIDL(FileObject parent, IDLDataObject data, String path) {
        out.print(path + " ... ");
        info.println(path + " ... ");

        if (data.getStatus () == IDLDataObject.STATUS_NOT_PARSED)
            data.startParsing ();
        for (int a = 0; a < 10; a ++) {
            if (data.getStatus () == IDLDataObject.STATUS_ERROR  ||  data.getStatus () == IDLDataObject.STATUS_OK)
                break;
            try { Thread.currentThread ().sleep (1000); } catch (Exception e) {}
        }
        if (data.getStatus () != IDLDataObject.STATUS_OK) {
            out.println("ERROR: Parse error: Status=" + data.getStatus ());
            info.println("ERROR: Parse error: Status=" + data.getStatus ());
            return;
        }

        out.println ("OK");
        printIDLNode (data.getNodeDelegate());
        info.println ("OK");
    }
    
    public void printIDLNode (Node node) {
        printProperties (node);
        Node[] nodes = node.getChildren ().getNodes (true);
        for (int a = 0; a < nodes.length; a ++)
            printIDLNode (nodes [a]);
    }
    
    public void printProperties (Node node) {
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
                    if (np [b].getValue () != null)
                        s += np [b].getValue ().toString ();
                } catch (Exception e) {
                    info.println("Getting value of property: Node: " + node.getName () + " Property Set: " + nps[a].getName () + " Property: " + np[b].getName ());
                    e.printStackTrace (info);
                }
                out.println (filter.filter (s));
            }
        }
        out.println ();
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
