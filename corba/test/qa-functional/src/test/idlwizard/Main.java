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

package test.idlwizard;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NewObjectNameStepOperator;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.modules.corba.idldialogs.AliasDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.AttributeDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ConstantDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.EnumDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.EnumEntryDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ExceptionDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ForwardDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.InterfaceDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.MemberDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ModuleDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.OperationDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.RemoveDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.StructureDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.UnionDefaultMemberDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.UnionDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.UnionMemberDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ValueBoxDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ValueDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ValuetypeDialog;
import org.netbeans.jellytools.modules.corba.idldialogs.ValuetypeFactoryDialog;
import org.netbeans.jellytools.modules.corba.idlwizard.DesignIDLStep;
import org.netbeans.jellytools.modules.corba.idlwizard.FinishIDLStep;
import org.netbeans.jellytools.modules.corba.idlwizard.IDLSourceStep;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import util.Filter;

public class Main extends JellyTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testIDLWizard"));
        return test;
    }
    
    ExplorerOperator exp;
    PrintStream out;
    EventTool ev;
    int step;
    
    public void setUp () {
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = true;
    }
    
    public String str () {
        return commands[step ++];
    }
    
    public boolean bool () {
        return Boolean.valueOf(commands[step ++]).booleanValue();
    }
    
    public static Hashtable hash;
    
    static {
        hash = new Hashtable ();
        hash.put ("Forward", "Forward Declaration");
        hash.put ("UnionMember", "Member");
        hash.put ("UnionDefaultMember", "Default Member");
        hash.put ("EnumEntry", "Enum Entry");
        hash.put ("ValuetypeFactory", "Valuetype Factory");
    }
    
    public void testIDLWizard () {
        NewWizardOperator.invoke(new Node (exp.repositoryTab().tree (), "|data|idlwizard"), "CORBA|Empty");
        NewObjectNameStepOperator nonso = new NewObjectNameStepOperator();
        nonso.setName("IDLWizard");
        nonso.next();
        IDLSourceStep iss = new IDLSourceStep ();
        iss.iDLWizard();
        iss.next ();
        DesignIDLStep dis = new DesignIDLStep ();
        step = 0;
        for (;;) {
            String command = str ();
            String node = str ();
            out.println ("COMMAND: " + command + "    NODE: " + node);
            new Node (dis.tree (), node).select ();
            ev.waitNoEvent(1000);
            if (command.indexOf('_') < 0) {
                String str = (String) hash.get (command);
                if (str == null)
                    str = command;
                dis.cboIDLTypes().selectItem(str);
                ev.waitNoEvent(1000);
                dis.btCreate().pushNoBlock ();
                ev.waitNoEvent(1000);
            }
            else if (command.endsWith("_DUMP")) {
                dis.btEdit().pushNoBlock ();
                ev.waitNoEvent(1000);
            }
            if ("Alias".equals (command)) {
                AliasDialog di = new AliasDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.setLength(str ());
                di.ok (); di.waitClosed();
            } else if ("Attribute".equals (command)) {
                AttributeDialog di = new AttributeDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.checkReadonly(bool ());
                di.ok (); di.waitClosed();
            } else if ("Constant".equals (command)) {
                ConstantDialog di = new ConstantDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.setValue(str ());
                di.ok (); di.waitClosed();
            } else if ("Enum".equals (command)) {
                EnumDialog di = new EnumDialog (false);
                di.setName(str ());
                di.setEnumValues(str ());
                di.ok (); di.waitClosed();
            } else if ("EnumEntry".equals (command)) {
                EnumEntryDialog di = new EnumEntryDialog (false);
                di.setName(str ());
                di.ok (); di.waitClosed();
            } else if ("Exception".equals (command)) {
                ExceptionDialog di = new ExceptionDialog (false);
                di.setName(str ());
                di.ok (); di.waitClosed();
            } else if ("Forward".equals (command)) {
                ForwardDialog di = new ForwardDialog (false);
                di.setName(str ());
                if ("Interface".equalsIgnoreCase(str ()))
                    di.rbInterface().push();
                else
                    di.rbValue().push();
                di.ok (); di.waitClosed();
            } else if ("Interface".equals (command)) {
                InterfaceDialog di = new InterfaceDialog (false);
                di.setName(str ());
                di.setBaseInterfaces(str ());
                di.checkAbstract(bool ());
                di.ok (); di.waitClosed();
            } else if ("Member".equals (command)) {
                MemberDialog di = new MemberDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.setLength(str ());
                di.ok (); di.waitClosed();
            } else if ("Module".equals (command)) {
                ModuleDialog di = new ModuleDialog (false);
                di.setName(str ());
                di.ok (); di.waitClosed();
            } else if ("Operation".equals (command)) {
                OperationDialog di = new OperationDialog (false);
                di.setName(str ());
                di.setReturnType(str ());
                di.setParameters(str ());
                di.setExceptions(str ());
                di.setContext(str ());
                di.checkOneway(bool ());
                di.ok (); di.waitClosed();
            } else if ("Structure".equals (command)) {
                StructureDialog di = new StructureDialog (false);
                di.setName(str ());
                di.ok (); di.waitClosed();
            } else if ("UnionDefaultMember".equals (command)) {
                UnionDefaultMemberDialog di = new UnionDefaultMemberDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.setLength(str ());
                di.ok (); di.waitClosed();
            } else if ("Union".equals (command)) {
                UnionDialog di = new UnionDialog (false);
                di.setName(str ());
                di.setDiscriminatorType(str ());
                di.ok (); di.waitClosed();
            } else if ("UnionMember".equals (command)) {
                UnionMemberDialog di = new UnionMemberDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.setLength(str ());
                di.setLabel(str ());
                di.ok (); di.waitClosed();
            } else if ("ValueBox".equals (command)) {
                ValueBoxDialog di = new ValueBoxDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.ok (); di.waitClosed();
            } else if ("Value".equals (command)) {
                ValueDialog di = new ValueDialog (false);
                di.setName(str ());
                di.setType(str ());
                di.setLength(str ());
                if ("Private".equalsIgnoreCase(str ()))
                    di.rbPrivate().push ();
                else
                    di.rbPublic().push ();
                di.ok (); di.waitClosed();
            } else if ("Valuetype".equals (command)) {
                ValuetypeDialog di = new ValuetypeDialog (false);
                di.setName(str ());
                di.setBase(str ());
                di.setSupports(str ());
                if (di.cbTruncatable ().isSelected ())
                    di.cbTruncatable ().setSelected(false);
                if (di.cbCustom ().isSelected ())
                    di.cbCustom ().setSelected(false);
                if (di.cbAbstract ().isSelected ())
                    di.cbAbstract ().setSelected(false);
                di.cbTruncatable().setSelected(bool ());
                di.cbCustom().setSelected(bool ());
                di.cbAbstract().setSelected(bool ());
                di.ok (); di.waitClosed();
            } else if ("ValuetypeFactory".equals (command)) {
                ValuetypeFactoryDialog di = new ValuetypeFactoryDialog (false);
                di.setName(str ());
                di.setParameters (str ());
                di.ok (); di.waitClosed();
            } else if ("Alias_DUMP".equals (command)) {
                AliasDialog di = new AliasDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                out.println (di.getLength());
                di.cancel (); di.waitClosed();
            } else if ("Attribute_DUMP".equals (command)) {
                AttributeDialog di = new AttributeDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                out.println (di.cbReadonly().isSelected());
                di.cancel (); di.waitClosed();
            } else if ("Constant_DUMP".equals (command)) {
                ConstantDialog di = new ConstantDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                out.println (di.getValue());
                di.cancel (); di.waitClosed();
            } else if ("Enum_DUMP".equals (command)) {
                EnumDialog di = new EnumDialog (true);
                out.println (di.getName());
                out.println (di.getEnumValues());
                di.cancel (); di.waitClosed();
            } else if ("EnumEntry_DUMP".equals (command)) {
                EnumEntryDialog di = new EnumEntryDialog (true);
                out.println (di.getName());
                di.cancel (); di.waitClosed();
            } else if ("Exception_DUMP".equals (command)) {
                ExceptionDialog di = new ExceptionDialog (true);
                out.println (di.getName());
                di.cancel (); di.waitClosed();
            } else if ("Forward_DUMP".equals (command)) {
                ForwardDialog di = new ForwardDialog (true);
                out.println (di.getName());
                out.println (di.rbInterface().isSelected());
                out.println (di.rbValue().isSelected());
                di.cancel (); di.waitClosed();
            } else if ("Interface_DUMP".equals (command)) {
                InterfaceDialog di = new InterfaceDialog (true);
                out.println (di.getName());
                out.println (di.getBaseInterfaces());
                out.println (di.cbAbstract().isSelected ());
                di.cancel (); di.waitClosed();
            } else if ("Member_DUMP".equals (command)) {
                MemberDialog di = new MemberDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                out.println (di.getLength());
                di.cancel (); di.waitClosed();
            } else if ("Module_DUMP".equals (command)) {
                ModuleDialog di = new ModuleDialog (true);
                out.println (di.getName());
                di.cancel (); di.waitClosed();
            } else if ("Operation_DUMP".equals (command)) {
                OperationDialog di = new OperationDialog (true);
                out.println (di.getName());
                out.println (di.getReturnType());
                out.println (di.getParameters());
                out.println (di.getExceptions());
                out.println (di.getContext());
                out.println (di.cbOneway().isSelected ());
                di.cancel (); di.waitClosed();
            } else if ("Structure_DUMP".equals (command)) {
                StructureDialog di = new StructureDialog (true);
                out.println (di.getName());
                di.cancel (); di.waitClosed();
            } else if ("UnionDefaultMember_DUMP".equals (command)) {
                UnionDefaultMemberDialog di = new UnionDefaultMemberDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                out.println (di.getLength());
                di.cancel (); di.waitClosed();
            } else if ("Union_DUMP".equals (command)) {
                UnionDialog di = new UnionDialog (true);
                out.println (di.getName());
                out.println (di.getDiscriminatorType());
                di.cancel (); di.waitClosed();
            } else if ("UnionMember_DUMP".equals (command)) {
                UnionMemberDialog di = new UnionMemberDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                out.println (di.getLength());
                out.println (di.getLabel());
                di.cancel (); di.waitClosed();
            } else if ("ValueBox_DUMP".equals (command)) {
                ValueBoxDialog di = new ValueBoxDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                di.cancel (); di.waitClosed();
            } else if ("Value_DUMP".equals (command)) {
                ValueDialog di = new ValueDialog (true);
                out.println (di.getName());
                out.println (di.getType());
                out.println (di.getLength());
                out.println (di.rbPrivate().isSelected ());
                out.println (di.rbPublic().isSelected ());
                di.cancel (); di.waitClosed();
            } else if ("Valuetype_DUMP".equals (command)) {
                ValuetypeDialog di = new ValuetypeDialog (true);
                out.println (di.getName());
                out.println (di.getBase());
                out.println (di.getSupports());
                out.println (di.cbCustom().isSelected());
                out.println (di.cbAbstract().isSelected());
                out.println (di.cbTruncatable().isSelected());
                di.cancel (); di.waitClosed();
            } else if ("ValuetypeFactory_DUMP".equals (command)) {
                ValuetypeFactoryDialog di = new ValuetypeFactoryDialog (true);
                out.println (di.getName());
                out.println (di.getParameters ());
                di.cancel (); di.waitClosed();
            } else if ("_DOWN".equals (command)) {
                dis.down();
            } else if ("_UP".equals (command)) {
                dis.up();
            } else if ("_REMOVE".equals (command)) {
                dis.btRemove().pushNoBlock();
                ev.waitNoEvent (1000);
                new RemoveDialog ().oK();
            } else if ("_FINISH".equals (command)) {
                break;
            } else
                assertTrue ("Unknown command: Step: " + step + " Command: " + command, false);
        }
        dis.next ();
        FinishIDLStep fis = new FinishIDLStep ();
        fis.finish ();
        
        out.println ();
        new OpenAction ().perform (new Node (exp.repositoryTab ().tree (), "|data|idlwizard|IDLWizard"));
        EditorWindowOperator ewo = new EditorWindowOperator ();
        EditorOperator eo = ewo.getEditor ("IDLWizard");
        ev.waitNoEvent (1000);
        String str = eo.getText ();
        out.println ("---------");
        StringTokenizer tok = new StringTokenizer (str, "\n");
        out.println (tok.nextToken());
        out.println (tok.nextToken());
        out.println (tok.nextToken());
        tok.nextToken();
        tok.nextToken();
        while (tok.hasMoreTokens())
            out.println (tok.nextToken ());
        compareReferenceFiles ();
    }

    String[] commands = new String[] {
        "Alias",                "",                     "MyString", "string", "",
        "Alias",                "",                     "MyStringArray10", "MyString", "10",
        "_DOWN",                "MyString",
        "Module",               "",                     "Module1",
        "Constant",             "Module1",              "Constant1", "long", "(1 << 8) + 1",
        "Forward",              "Module1",              "Forward1", "Interface",
        "Interface",            "Module1",              "Interface1", "Forward1", "false",
        "Attribute",            "Module1|Interface1",   "attr1", "long", "true",
        "Operation",            "Module1|Interface1",   "op1", "Forward1", "in Forward1 inf, inout Forward1 inoutf, out Forward1 outf", "", "", "true",
        "Interface",            "Module1",              "Forward1", "", "true",
        "Union",                "",                     "Union1", "MyString",
        "UnionMember",          "Union1",               "LongMember1", "long", "1", "\"CustomString1\"",
        "UnionDefaultMember",   "Union1",               "Any1", "any", "",
        "UnionMember",          "Union1",               "CharMember2", "char", "2", "\"CustomString2\"",
        "Enum",                 "",                     "Enum1", "before1, before2, forRemove, after",
        "EnumEntry",            "Enum1",                "inserted1",
        "EnumEntry",            "Enum1",                "inserted2",
        "EnumEntry",            "Enum1",                "inserted3",
        "_DOWN",                "Enum1|inserted2",
        "_REMOVE",              "Enum1|forRemove",
        "Exception",            "",                     "Exception1",
        "Member",               "Exception1",           "Long1", "long", "2",
        "Member",               "Exception1",           "Str1", "MyString", "",
        "Interface",            "",                     "Interface2", "::Module1::Interface1", "false",
        "Operation",            "Interface2",           "op2", "void", "inout char char1, inout string string1, inout short short1, inout long long1", "Exception1", "Context1, Context2", "false",
        "Structure",            "",                     "Structure1",
        "Member",               "Structure1",           "CharMember1", "char", "2",
        "Member",               "Structure1",           "ShortMember2", "short", "",
        "ValueBox",             "",                     "ValueBox1", "long",
        "Valuetype",            "",                     "Valuetype1", "", "Module1::Forward1", "false", "false", "true",
        "Valuetype",            "",                     "Valuetype2", "", "", "false", "false", "false",
        "Valuetype",            "",                     "Valuetype3", "", "", "false", "false", "false",
        "Valuetype",            "",                     "Valuetype4", "Valuetype3", "", "true", "false", "false",
        "Value",                "Valuetype2",           "val1", "char", "", "Private",
        "ValuetypeFactory",     "Valuetype2",           "init1", "in char input",

        "Alias_DUMP",           "MyString",
        "Alias_DUMP",           "MyStringArray10",
        "Module_DUMP",          "Module1",
        "Constant_DUMP",        "Module1|Constant1",
        "Interface_DUMP",       "Module1|Interface1",
        "Attribute_DUMP",       "Module1|Interface1|attr1",
        "Operation_DUMP",       "Module1|Interface1|op1",
        "Union_DUMP",           "Union1",
        "UnionMember_DUMP",     "Union1|LongMember1",
        "UnionMember_DUMP",     "Union1|CharMember2",
        "UnionDefaultMember_DUMP", "Union1|Any1",
        "Enum_DUMP",            "Enum1",
        "EnumEntry_DUMP",       "Enum1|before1",
        "EnumEntry_DUMP",       "Enum1|before2",
        "EnumEntry_DUMP",       "Enum1|after",
        "EnumEntry_DUMP",       "Enum1|inserted1",
        "EnumEntry_DUMP",       "Enum1|inserted2",
        "EnumEntry_DUMP",       "Enum1|inserted3",
        "Exception_DUMP",       "Exception1",
        "Member_DUMP",          "Exception1|Long1",
        "Member_DUMP",          "Exception1|Str1",
        "Interface_DUMP",       "Interface2",
        "Operation_DUMP",       "Interface2|op2",
        "Structure_DUMP",       "Structure1",
        "Member_DUMP",          "Structure1|CharMember1",
        "Member_DUMP",          "Structure1|ShortMember2",
        "ValueBox_DUMP",        "ValueBox1",
        "Valuetype_DUMP",       "Valuetype1",
        "Valuetype_DUMP",       "Valuetype2",
        "Valuetype_DUMP",       "Valuetype3",
        "Valuetype_DUMP",       "Valuetype4",
        "Value_DUMP",           "Valuetype2|val1",
        "ValuetypeFactory_DUMP","Valuetype2|init1",
        "_FINISH",              "",
    };

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
