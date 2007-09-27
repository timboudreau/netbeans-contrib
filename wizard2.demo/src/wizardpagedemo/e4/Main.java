/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package wizardpagedemo.e4;
import java.awt.EventQueue;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

/*
 * @author Tim Boudreau
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Wizard wiz = 
                WizardPage.createWizard(new Class[] { 
                FirstPage.class, SecondPage.class }, 
                new WRP());
        
        System.out.println("Wizard Result: " + WizardDisplayer.showWizard(wiz));
    }
    
    public static Wizard createWizard() {
        Wizard wiz = 
                WizardPage.createWizard(new Class[] { 
                FirstPage.class, SecondPage.class }, 
                new WRP());
        return wiz;
    }
    
    private static class WRP implements WizardResultProducer {
        public Object finish(Map wizardData) throws WizardException {
            return new BackgroundResultCreator();
        }

        public boolean cancel(Map settings) {
            boolean dialogShouldClose = JOptionPane.showConfirmDialog (null,
                "Oh, come on, you want to finish the wizard, don't you?!!",
                "Don't Leave!", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION;
            return dialogShouldClose;        
        }
    }
    
    static boolean abortable = true;
    static class BackgroundResultCreator extends DeferredWizardResult {
        public BackgroundResultCreator() {
            super (abortable);
        }
        
        private volatile boolean aborted;
        public void abort() {
            aborted = true;
        }
        
        public void start(Map wizardData, DeferredWizardResult.ResultProgressHandle progress) {
            assert !EventQueue.isDispatchThread();
            for (int i = 0; i < 5; i++) {
                if (aborted) {
                    return;
                }
                progress.setProgress("Doing stuff " + (i  * 20) + '%', i, 5);
                try {
                    Thread.currentThread().sleep(1000);
                    if (aborted) {
                        return;
                    }
                } catch (InterruptedException ex) {
                    
                }
            }
            if (Boolean.TRUE.equals(wizardData.get("shouldFail"))) {
                progress.failed("The operation has failed.  This is the UI that " +
                        "is displayed when DeferredWizardResult.failed() is called.",
                        false);
            } else {
                progress.finished(createSummary (wizardData));
            }
        }
        
        private Object createSummary (Map wizardData) {
            //We will just return the wizard data here.  In real life we would
            //a compute a result here
            Summary summary;
            if (Boolean.TRUE.equals(wizardData.get("custom"))) {
                Object[][] data = new Object[wizardData.size()][2];
                int ix = 0;
                for (Iterator i=wizardData.keySet().iterator(); i.hasNext();) {
                    Object key = i.next();
                    Object val = wizardData.get(key);
                    data[ix][0] = key;
                    data[ix][1] = val;
                    ix++;
                }
                TableModel mdl = new DefaultTableModel (data, 
                        new String[] { "Key", "Value"});
                summary = Summary.create(new JScrollPane(new JTable(mdl)), wizardData);
            } else if (Boolean.TRUE.equals(wizardData.get("list"))) {
                String[] s = new String[wizardData.size()];
                int ix = 0;
                for (Iterator i=wizardData.keySet().iterator(); i.hasNext();) {
                    Object key = i.next();
                    s[ix] = key + " = " + wizardData.get(key);
                    ix++;
                }
                summary = Summary.create (s, wizardData);
            } else if (Boolean.TRUE.equals(wizardData.get("plainText"))) {
                summary = Summary.create ("This is some summary text", wizardData);
            } else {
                summary = null;
            }
            if (summary == null) {
                return wizardData;
            } else {
                return summary;
            }
        }
    }
}
