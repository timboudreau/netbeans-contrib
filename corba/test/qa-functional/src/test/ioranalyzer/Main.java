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

package test.ioranalyzer;

import java.lang.reflect.InvocationTargetException;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.modules.corba.actions.MergeIORsAction;
import org.netbeans.jellytools.modules.corba.dialogs.MergeIORsDialog;
import org.netbeans.jellytools.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import util.Environment;

public class Main extends JellyTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testIOR_Merge"));
        test.addTest(new Main("testIOR_Parser"));
        return test;
    }
    
    public void setUp () {
        closeAllModal = true;
    }
    
    public void testIOR_Merge() {
        ExplorerOperator exp = new ExplorerOperator ();
        Node n1 = new Node (exp.repositoryTab ().tree (), "|data|ior|1051");
        Node n2 = new Node (exp.repositoryTab ().tree (), "|data|ior|1052");
        
        new MergeIORsAction ().perform (new Node[] { n1, n2 });
        MergeIORsDialog di = new MergeIORsDialog ();
        di.setIORFileName("output");
        new Node (di.tree (), "|data|ior").select ();
        di.oK ();
        print ("data/ior/output.ior");
        compareReferenceFiles ();
    }
    
    public void testIOR_Parser () {
        print ("data/ior/embl.ior");
        print ("data/ior/geneticcode.ior");
        print ("data/ior/meta.ior");
        print ("data/ior/random.ior");
        print ("data/ior/reference.ior");
        print ("data/ior/taxonomy.ior");
        compareReferenceFiles ();
    }
    
    public void print (String str) {
        getRef ().println ("----  " + str + " ----");
        FileObject fo = Environment.findFileObject(str);
        if (fo == null) {
            getRef ().println ("FileObject not found");
            return;
        }
        DataObject dao;
        try {
            dao = DataObject.find (fo);
        } catch (DataObjectNotFoundException e) {
            getRef ().println ("DataObject not found");
            return;
        }
        org.openide.nodes.Node no = dao.getNodeDelegate();
        print (no);
    }
    
    public void print (org.openide.nodes.Node no) {
        getRef ().println ("Node: " + no.getDisplayName());
        org.openide.nodes.Node.PropertySet[] ps = no.getPropertySets();
        if (ps != null)
            for (int a = 0; a < ps.length; a ++)
                print (ps[a]);
        org.openide.nodes.Node[] nos = no.getChildren().getNodes(true);
        if (nos == null)
            return;
        for (int a = 0; a < nos.length; a ++)
            print (nos[a]);
    }
    
    public void print (org.openide.nodes.Node.PropertySet ps) {
        getRef ().println ("PropertySet: " + ps.getDisplayName());
        org.openide.nodes.Node.Property[] p = ps.getProperties();
        if (p != null)
            for (int a = 0; a < p.length; a ++)
                print (p[a]);
    }
    
    public void print (org.openide.nodes.Node.Property p) {
        try {
            getRef ().println ("  " + p.getDisplayName() + " -> " + p.getValue());
        } catch (IllegalAccessException e) {
            getRef ().println ("  " + p.getDisplayName() + " -> IllegalAccessException");
        } catch (InvocationTargetException e) {
            getRef ().println ("  " + p.getDisplayName() + " -> InvocationTargetException");
        }
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
