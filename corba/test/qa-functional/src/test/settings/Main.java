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

package test.settings;

import java.io.*;
import java.io.PrintStream;
import java.util.ArrayList;

import org.openide.options.*;
import org.netbeans.modules.corba.settings.*;

import util.Helper;

public class Main extends org.netbeans.junit.NbTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testSettings"));
        return test;
    }
    
    public void testSettings() {
        PrintStream out = getRef();
        
        ArrayList filter = new ArrayList();
        filter.add("USER=");
        out.println("===================");
        CORBASupportSettings css = (CORBASupportSettings) SystemOption.findObject(CORBASupportSettings.class, true);
        java.util.Vector names = css.getNames();
        
        for (int a = 0; a < names.size(); a ++) {
            css.setOrb((String) names.elementAt(a));
            ORBSettings set = css.getActiveSetting();
            out.println("===================");
            out.println(set.displayName());
            out.println(set.getName());
            out.println(set.getName());
            out.println(set.getClientBindingName());
            out.println(set.getServerBindingName());
            out.println(set.getSkeletons());
            out.println(set.getParams());
            out.println(set.param());
            out.println(set.getClientBinding().toString());
            out.println(set.getServerBinding().toString());
            out.println(set.getIdl().getProcessName());
            out.println(set.getIdl().getArguments());
            out.println(set.getIdl().getInfo());
            out.println(set.idl());
            out.println(String.valueOf(set.isTie()));
            out.println(set.getTieParam());
            out.println(set.getPackageParam());
            out.println(set.package_param());
            out.println(set.getDirParam());
            out.println(set.dir_param());
            out.println(set.getPackageDelimiter());
            out.println(String.valueOf(set.delim()));
            out.println(set.getErrorExpression());
            out.println(set.expression());
            out.println(set.getFilePosition());
            out.println(String.valueOf(set.file()));
            out.println(set.getLinePosition());
            out.println(String.valueOf(set.line()));
            out.println(set.getColumnPosition());
            out.println(String.valueOf(set.column()));
            out.println(set.getMessagePosition());
            out.println(String.valueOf(set.message()));
            out.println(set.getImplBaseImplPrefix());
            out.println(set.getImplBaseImplPostfix());
            out.println(set.getExtClassPrefix());
            out.println(set.getExtClassPostfix());
            out.println(set.getTieImplPrefix());
            out.println(set.getTieImplPostfix());
            out.println(set.getImplIntPrefix());
            out.println(set.getImplIntPostfix());
            out.println(Helper.filter(filter, set.getReplaceableStringsTable().toString()));
            out.println(Helper.filter(filter, set.getReplaceableStringsProps().toString()));
            out.println(String.valueOf(set.hideGeneratedFiles()));
            out.println(set.getGeneration());
            out.println(set.getSynchro());
            out.println(set.getOrbName());
            java.util.List v;
            v = set.getServerBindings();
            for (int b = 0; b < v.size(); b ++)
                out.println(">" + v.get(b));
            v = set.getClientBindings();
            for (int b = 0; b < v.size(); b ++)
                out.println(">" + v.get(b));
            out.println(String.valueOf(set.isSupported()));
        }
        compareReferenceFiles();
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
}
