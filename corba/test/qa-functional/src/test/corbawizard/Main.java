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

package test.corbawizard;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.ListModel;
import javax.swing.tree.TreePath;
import junit.framework.Test;
import junit.textui.TestRunner;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jellytools.modules.corba.actions.CORBAWizardAction;
import org.netbeans.jellytools.modules.corba.corbawizard.FileBindingStep;
import org.netbeans.jellytools.modules.corba.corbawizard.FinishStep;
import org.netbeans.jellytools.modules.corba.corbawizard.NSBindingStep;
import org.netbeans.jellytools.modules.corba.corbawizard.ORBSettingsStep;
import org.netbeans.jellytools.modules.corba.corbawizard.ProprietaryBindingStep;
import org.netbeans.jellytools.modules.corba.corbawizard.RootInterfaceStep;
import org.netbeans.jellytools.modules.corba.corbawizard.RootInterfacesStep;
import org.netbeans.jellytools.modules.corba.corbawizard.STDBindingStep;
import org.netbeans.jellytools.modules.corba.corbawizard.SelectSourceIDLStep;
import org.netbeans.jellytools.modules.corba.corbawizard.TypeAplicationStep;
import org.netbeans.jellytools.modules.corba.nodes.IDLNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.junit.NbTestSuite;
import util.Environment;

public class Main extends JellyTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static Test suite() {
        NbTestSuite test = new NbTestSuite();
        test.addTest(new Main("testWizardDisabled_Start"));
        test.addTest(new Main("testWizardDisabled_ImplOnly"));
        test.addTest(new Main("testWizardDisabled_ServerOnly"));
        test.addTest(new Main("testWizardDisabled_ClientOnly"));
        test.addTest(new Main("testWizardDisabled_CallBackClientOnly"));
        test.addTest(new Main("testWizardDisabled_Binding"));
        test.addTest(new Main("testWizardDisabled_RevertChanges"));
        return test;
    }
    
    ExplorerOperator exp = null;
    EventTool ev = null;
    PrintStream out = null;
//    Filter filter = null;
    Timeouts time;

    public void setUp () {
        time = JemmyProperties.getCurrentTimeouts ();
        Timeouts t = new Timeouts ();
        try { t.loadDebugTimeouts (); } catch (IOException e) {}
//        JemmyProperties.setCurrentTimeouts (t);
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = false;
/*        filter = new Filter ();
        filter.addFilterAfter ("@author");
        filter.addFilterAfter ("Created on");
        filter.addFilterAfter ("by ");*/
    }
    
    public void tearDown () {
        JemmyProperties.setCurrentTimeouts (time);
    }
	
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    public void backToTypeApplicationStep () {
        getLog ().println ("Backing to TypeApplicationStep");
        for (;;) {
            WizardOperator wo = new WizardOperator ("CORBA Wizard");
            getLog ().println (wo.stepsGetSelectedValue ());
            if (TypeAplicationStep.STEP_NAME.equals (wo.stepsGetSelectedValue()))
                return;
            if (wo.stepsGetSelectedIndex() <= 1)
                return;
            wo.back ();
            try { Thread.sleep (1000); } catch (Exception e) {}
        }
    }
    
    public void backToORBSettingsStep () {
        getLog ().println ("Backing to ORBSettingsStep");
        for (int a = 0; a < 10; a ++) {
            WizardOperator wo = new WizardOperator ("CORBA Wizard");
            getLog ().println (wo.stepsGetSelectedValue ());
            if (ORBSettingsStep.STEP_NAME.equals (wo.stepsGetSelectedValue()))
                return;
            if (wo.stepsGetSelectedIndex() <= 1)
                return;
            wo.back ();
            try { Thread.sleep (1000); } catch (Exception e) {}
        }
    }
    
    public void failNotify(Throwable reason) {
        if ("testWizardDisabled_RevertChanges".equals (getName ())) {
            if (NbDialogOperator.findJDialog("CORBA Wizard", false, false) != null)
                new WizardOperator ("CORBA Wizard").cancel ();
        } else if (!"testWizardDisabled_Start".equals (getName()))
            backToTypeApplicationStep ();
    }
    
    public void printTreePath (TreePath tp) {
/*        printTreePath (tp, out);
    }

    public void printTreePath (TreePath tp, PrintStream out) {*/
        out.print ("Path: ");
        if (tp == null) {
            out.println ("<NULL>");
            return;
        }
        for (int a = 2; a < tp.getPathCount(); a ++)
            out.print ("|" + tp.getPathComponent(a).toString ());
        out.println ();
    }
    
    public void printButtons (WizardOperator wo) {
        out.println ("-- " + wo.stepsGetSelectedValue());
        out.println ("Back:   " + wo.btBack().isEnabled());
        out.println ("Next:   " + wo.btNext().isEnabled());
        out.println ("Finish: " + wo.btFinish().isEnabled());
        out.println ("Cancel: " + wo.btCancel().isEnabled());
        out.println ("--");
    }
    
    public void printTypeApplicationStep (TypeAplicationStep ta) {
        out.println("Impl:     " + ta.cbCreateImplementation().isSelected ());
        out.println("Tie:      " + ta.cbTieBased().isSelected ());
        out.println("Server:   " + ta.cbCreateServer().isSelected ());
        out.println("Client:   " + ta.cbCreateClient().isSelected ());
        out.println("CallBack: " + ta.cbCreateCallBackClient().isSelected ());
    }
    
    public void printORBSettingsStep (ORBSettingsStep ob) {
        out.println ("-- " + ob.stepsGetSelectedValue());
        out.println ("ORB: " + ob.cboChooseORBImplementation().isEnabled());
        out.println ("Bind: " + ob.cboChooseBindingMethod().isEnabled());
        out.println ("--");
    }
    
    public void printBindings (ORBSettingsStep ob, String orb) {
        out.println ("-- Bindings for: " + orb);
        ob.selectChooseORBImplementation(orb);
        JComboBoxOperator cbo = ob.cboChooseBindingMethod();
        for (int a = 0; a < cbo.getItemCount(); a ++)
            out.println ("" + (a + 1) + ". " + cbo.getItemAt (a).toString ());
    }

    public void printRootInterfaceStep (RootInterfaceStep ri) {
        out.println ("-- " + ri.stepsGetSelectedValue());
        JListOperator lo = ri.lstAvailableInterfaces();
        ListModel lm = lo.getModel();
        for (int a = 0; a < lm.getSize(); a ++)
            out.println (lm.getElementAt(a).toString ());
        out.println ("--");
    }
    
    public void printRootInterfacesStep (RootInterfacesStep ri) {
        out.println ("-- " + ri.stepsGetSelectedValue());
        JListOperator lo;
        ListModel lm;
        lo = ri.lstAvailableInterfaces();
        lm = lo.getModel();
        for (int a = 0; a < lm.getSize(); a ++)
            out.println (lm.getElementAt(a).toString ());
        out.println ("--");
        lo = ri.lstAvailableCallBackInterfaces();
        lm = lo.getModel();
        for (int a = 0; a < lm.getSize(); a ++)
            out.println (lm.getElementAt(a).toString ());
        out.println ("--");
    }
    
    public void testWizardDisabled_Start () {
        Environment.loadORBEnvironment("JDK14").setNSBinding ();
        Node n;
        n = new IDLNode (exp.repositoryTab().tree (), "|data|corbawizard|Wizard");
        new CORBAWizardAction ().perform (n);
        SelectSourceIDLStep ss = new SelectSourceIDLStep ();
        ss.verify ();
        ev.waitNoEvent(1000);
        try { Thread.sleep (5000); } catch (Exception e) {}
        //try { PNGEncoder.captureScreen (getWorkDirPath () + File.separator + "path1.png"); } catch (IOException e) {}
        out.println ("IDLFileName: " + ss.getIDLFileName());
        printTreePath (ss.tree().getSelectionPath());
        printButtons (ss);
        new Node (ss.tree (), "").select();
        ev.waitNoEvent(1000);
        try { Thread.sleep (5000); } catch (Exception e) {}
        //try { PNGEncoder.captureScreen (getWorkDirPath () + File.separator + "path2.png"); } catch (IOException e) {}
        out.println ("IDLFileName: " + ss.getIDLFileName());
        printTreePath (ss.tree().getSelectionPath());
        printButtons (ss);
        ss.txtIDLFileName().clearText ();
        ss.txtIDLFileName().typeText ("data.corbawizard.Wizard");
        ev.waitNoEvent(1000);
        try { Thread.sleep (5000); } catch (Exception e) {}
        //try { PNGEncoder.captureScreen (getWorkDirPath () + File.separator + "path3.png"); } catch (IOException e) {}
        out.println ("IDLFileName: " + ss.getIDLFileName());
        printTreePath (ss.tree().getSelectionPath());
        printButtons (ss);
        ss.next ();
        TypeAplicationStep ta = new TypeAplicationStep ();
        ta.verify ();
        printButtons (ta);
        compareReferenceFiles ();
    }
    
    public void testWizardDisabled_ImplOnly () {
        TypeAplicationStep ta = new TypeAplicationStep ();
        ta.verify ();
        ta.checkCreateImplementation(true);
        ta.checkTieBased(false);
        ta.checkCreateCallBackClient(false);
        ta.checkCreateClient(false);
        ta.checkCreateServer(false);
        printButtons (ta);
        ta.next ();
        ORBSettingsStep ob = new ORBSettingsStep ();
        ob.verify ();
        printORBSettingsStep (ob);
        printButtons (ob);
        ob.next ();
        FinishStep fs = new FinishStep ();
        printButtons (fs);
        fs.verify ();
        compareReferenceFiles ();
        backToTypeApplicationStep ();
    }
    
    public void testWizardDisabled_ServerOnly () {
        TypeAplicationStep ta = new TypeAplicationStep ();
        ta.verify ();
        ta.checkCreateImplementation(false);
        ta.checkTieBased(false);
        ta.checkCreateCallBackClient(false);
        ta.checkCreateClient(false);
        ta.checkCreateServer(true);
        printButtons (ta);
        ta.next ();
        ORBSettingsStep ob = new ORBSettingsStep ();
        ob.verify ();
        printORBSettingsStep (ob);
        ob.next ();
        RootInterfaceStep ri = new RootInterfaceStep ();
        ri.verify ();
        printRootInterfaceStep (ri);
        printButtons (ri);
        compareReferenceFiles ();
        backToTypeApplicationStep ();
    }
    
    public void testWizardDisabled_ClientOnly () {
        TypeAplicationStep ta = new TypeAplicationStep ();
        ta.verify ();
        ta.checkCreateImplementation(false);
        ta.checkTieBased(false);
        ta.checkCreateCallBackClient(false);
        ta.checkCreateClient(true);
        ta.checkCreateServer(false);
        printButtons (ta);
        ta.next ();
        ORBSettingsStep ob = new ORBSettingsStep ();
        ob.verify ();
        printORBSettingsStep (ob);
        ob.next ();
        RootInterfaceStep ri = new RootInterfaceStep ();
        ri.verify ();
        printRootInterfaceStep (ri);
        printButtons (ri);
        compareReferenceFiles ();
        backToTypeApplicationStep ();
    }
    
    public void testWizardDisabled_CallBackClientOnly () {
        TypeAplicationStep ta = new TypeAplicationStep ();
        ta.verify ();
        ta.checkCreateImplementation(false);
        ta.checkTieBased(false);
        ta.checkCreateCallBackClient(true);
        ta.checkCreateClient(false);
        ta.checkCreateServer(false);
        printButtons (ta);
        ta.next ();
        ORBSettingsStep ob = new ORBSettingsStep ();
        ob.verify ();
        printORBSettingsStep (ob);
        ob.next ();
        RootInterfacesStep ri = new RootInterfacesStep ();
        ri.verify ();
        printRootInterfacesStep (ri);
        printButtons (ri);
        compareReferenceFiles ();
        backToTypeApplicationStep ();
    }

    public void testWizardDisabled_Binding () {
        TypeAplicationStep ta = new TypeAplicationStep ();
        ta.verify ();
        ta.checkCreateImplementation(true);
        ta.checkTieBased(true);
        ta.checkCreateCallBackClient(true);
        ta.checkCreateClient(true);
        ta.checkCreateServer(true);
        printButtons (ta);
        ta.next ();
        
        ORBSettingsStep ob;
        RootInterfacesStep ri;
        ProprietaryBindingStep pb;
        FinishStep fi;
        
        ob = new ORBSettingsStep ();
        ob.verify ();
        ob.selectChooseBindingMethod(ORBSettingsStep.ITEM_NAMINGSERVICE);
        printORBSettingsStep (ob);
        ob.next ();
        ri = new RootInterfacesStep ();
        ri.verify ();
        ri.next ();
        NSBindingStep ns = new NSBindingStep ();
        ns.verify ();
        printButtons (ns);
        backToORBSettingsStep ();
        
        ob = new ORBSettingsStep ();
        ob.verify ();
        ob.selectChooseBindingMethod(ORBSettingsStep.ITEM_IORTOFILE);
        printORBSettingsStep (ob);
        ob.next ();
        ri = new RootInterfacesStep ();
        ri.verify ();
        ri.next ();
        FileBindingStep fb = new FileBindingStep ();
        fb.verify ();
        fb.txtIORFileName().clearText();
        printButtons (fb);
        fb.txtIORFileName().typeText ("ior.ior");
        printButtons (fb);
        fb.next ();
        fi = new FinishStep ();
        fi.verify ();
        printButtons (fi);
        backToORBSettingsStep ();
        
        ob = new ORBSettingsStep ();
        ob.verify ();
        ob.selectChooseBindingMethod(ORBSettingsStep.ITEM_IORTOSTANDARDOUTPUT);
        printORBSettingsStep (ob);
        ob.next ();
        ri = new RootInterfacesStep ();
        ri.verify ();
        ri.next ();
        STDBindingStep st = new STDBindingStep ();
        st.verify ();
        printButtons (st);
        st.next ();
        fi = new FinishStep ();
        fi.verify ();
        printButtons (fi);
        backToORBSettingsStep ();

        ob = new ORBSettingsStep ();
        ob.verify ();

        printBindings (ob, ORBSettingsStep.ITEM_J2EEORB);
        printBindings (ob, ORBSettingsStep.ITEM_JDK13ORB);
        printBindings (ob, ORBSettingsStep.ITEM_JDK14ORB);
        printBindings (ob, ORBSettingsStep.ITEM_ORBACUSFORJAVA4X);
        printBindings (ob, ORBSettingsStep.ITEM_ORBACUSFORJAVA4XFORWINDOWS);
        printBindings (ob, ORBSettingsStep.ITEM_ORBIX20001XFORJAVA);
        printBindings (ob, ORBSettingsStep.ITEM_ORBIXWEB32);
        printBindings (ob, ORBSettingsStep.ITEM_VISIBROKER34FORJAVA);
        printBindings (ob, ORBSettingsStep.ITEM_VISIBROKER4XFORJAVA);
        printBindings (ob, ORBSettingsStep.ITEM_EORB1XUNSUPPORTED);
        printBindings (ob, ORBSettingsStep.ITEM_JACORB13XUNSUPPORTED);
        printBindings (ob, ORBSettingsStep.ITEM_JAVAORB22XUNSUPPORTED);
        printBindings (ob, ORBSettingsStep.ITEM_JDK12ORBUNSUPPORTED);
        printBindings (ob, ORBSettingsStep.ITEM_OPENORB1XUNSUPPORTED);
        printBindings (ob, ORBSettingsStep.ITEM_ORBACUSFORJAVA3XUNSUPPORTED);
        printBindings (ob, ORBSettingsStep.ITEM_ORBACUSFORJAVA3XFORWINDOWSUNSUPPORTED);
        
        ob.selectChooseORBImplementation(Environment.winOS ? ORBSettingsStep.ITEM_ORBACUSFORJAVA3XFORWINDOWSUNSUPPORTED : ORBSettingsStep.ITEM_ORBACUSFORJAVA3XUNSUPPORTED);
        ob.selectChooseBindingMethod(ORBSettingsStep.ITEM_PROPRIETARYBINDER);
        ob.next ();
        ri = new RootInterfacesStep ();
        ri.verify ();
        ri.next ();
        pb = new ProprietaryBindingStep ();
        pb.verify ();
        out.println ("Server: " + pb.txtServerName().getText ());
        printButtons (pb);
        pb.txtServerName().clearText ();
        printButtons (pb);
        pb.txtServerName().typeText ("ServerName");
        out.println ("Server: " + pb.txtServerName().getText ());
        printButtons (pb);
        pb.next ();
        fi = new FinishStep ();
        fi.verify ();
        printButtons (fi);
        backToORBSettingsStep ();
        
        compareReferenceFiles ();
    }
    
    public void testWizardDisabled_RevertChanges () {
        TypeAplicationStep ta;
        ORBSettingsStep ob;
        RootInterfacesStep ri;
        FileBindingStep fb;
        FinishStep fi;
        
        backToTypeApplicationStep ();
        ta = new TypeAplicationStep ();
        ta.verify ();
        ta.checkCreateImplementation(true);
        ta.checkTieBased(true);
        ta.checkCreateCallBackClient(true);
        ta.checkCreateClient(true);
        ta.checkCreateServer(true);
        ta.next ();
        
        ob = new ORBSettingsStep ();
        ob.verify ();
        ob.selectChooseORBImplementation(ORBSettingsStep.ITEM_ORBIX20001XFORJAVA);
        ob.selectChooseBindingMethod(ORBSettingsStep.ITEM_IORTOFILE);
        printORBSettingsStep (ob);
        ob.next ();
        ri = new RootInterfacesStep ();
        ri.verify ();
        ri.lstAvailableInterfaces().selectItem("B::C");
        ri.lstAvailableCallBackInterfaces().selectItem("B::C");
        ri.next ();
        fb = new FileBindingStep ();
        fb.verify ();
        fb.txtIORFileName().clearText();
        fb.txtIORFileName().typeText ("ior.ior");
        fb.next ();
        fi = new FinishStep ();
        fi.verify ();
        fi.cancel ();
        
        Node n;
        n = new IDLNode (exp.repositoryTab().tree (), "|data|corbawizard|Wizard");
        new CORBAWizardAction ().perform (n);
        SelectSourceIDLStep ss = new SelectSourceIDLStep ();
        ss.verify ();
        ev.waitNoEvent(1000);
        try { Thread.sleep (5000); } catch (Exception e) {}
        //try { PNGEncoder.captureScreen (getWorkDirPath () + File.separator + "path1.png"); } catch (IOException e) {}
        out.println ("IDLFileName: " + ss.getIDLFileName());
        printTreePath (ss.tree().getSelectionPath());
        ss.next ();
        ta = new TypeAplicationStep ();
        ta.verify ();
        printTypeApplicationStep (ta);
        ta.checkCreateCallBackClient(true);
        ta.next ();
        ob = new ORBSettingsStep ();
        ob.verify ();
        printORBSettingsStep (ob);
        ob.selectChooseORBImplementation(ORBSettingsStep.ITEM_ORBIX20001XFORJAVA);
        ob.selectChooseBindingMethod(ORBSettingsStep.ITEM_IORTOFILE);
        ob.next ();
        ri = new RootInterfacesStep ();
        ri.verify ();
        out.println ("Client: " + ri.lstAvailableInterfaces().getSelectedValue());
        out.println ("CallBack: " + ri.lstAvailableCallBackInterfaces().getSelectedValue ());
        ri.next ();
        fb = new FileBindingStep ();
        fb.verify ();
        out.println ("File: " + fb.txtIORFileName().getText ());
        fb.cancel ();

        ev.waitNoEvent(1000);
        try { Thread.sleep (1000); } catch (Exception e) {}
        out.println ("ORB: " + Environment.getActiveORBName ());
        out.println ("Server: " + Environment.getServerBindingName ());
        out.println ("Client: " + Environment.getClientBindingName ());
        
        compareReferenceFiles ();
    }
    
}
