package test.indentation;

import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.modules.corba.nodes.IDLNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.JellyTestCase;

public class Main extends JellyTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testIndent"));
        return test;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    ExplorerOperator exp;
    
    public void testIndent () {
        exp = new ExplorerOperator ();
        NewWizardOperator.create ("CORBA|Empty", "|data|indentation", "01");
        new IDLNode (exp.repositoryTab().tree (), "|data|indentation|01").open ();
        EditorWindowOperator ewo = new EditorWindowOperator ();
        EditorOperator eo = ewo.getEditor ("01");
        eo.txtEditorPane().setText("");
        for (int a = 0; a < idl01.length; a ++) {
            for (int b = 0; b < idl01[a].length (); b ++)
                eo.typeKey (idl01[a].charAt (b));
            eo.typeKey('\n');
        }
        getRef ().println (eo.getText ());
        eo.close (false);
        compareReferenceFiles();
    }
    
    String[] idl01 = new String[] {
        "#ifndef ABC",
        "#define ABC",
        "module A {",
        "interface B {",
        "void op (in long il);",
        "};",
        "};",
        "union C switch (long) {",
        "case 1: long l;",
        "default: string s;",
        "};",
        "#endif",
    };
    
}
